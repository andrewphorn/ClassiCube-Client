/*
 * $ProjectName$
 * $ProjectRevision$
 * -----------------------------------------------------------
 * $Id: CachedUrlStream.java,v 1.1 2003/04/10 19:48:22 jarnbjo Exp $
 * -----------------------------------------------------------
 *
 * $Author: jarnbjo $
 *
 * Description:
 *
 * Copyright 2002-2003 Tor-Einar Jarnbjo
 * -----------------------------------------------------------
 *
 * Change History
 * -----------------------------------------------------------
 * $Log: CachedUrlStream.java,v $
 * Revision 1.1  2003/04/10 19:48:22  jarnbjo
 * no message
 *
 *
 */
package de.jarnbjo.ogg;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Implementation of the <code>PhysicalOggStream</code> interface for reading and caching an Ogg
 * stream from a URL. This class reads the data as fast as possible from the URL, caches it locally
 * either in memory or on disk, and supports seeking within the available data.
 */
public class CachedUrlStream implements PhysicalOggStream {

    public class LoaderThread implements Runnable {

        private InputStream source;
        private RandomAccessFile drain;
        private byte[] memoryCache;

        private boolean bosDone = false;

        private int pageNumber;

        public LoaderThread(InputStream source, RandomAccessFile drain, byte[] memoryCache) {
            this.source = source;
            this.drain = drain;
            this.memoryCache = memoryCache;
        }

        public boolean isBosDone() {
            return bosDone;
        }

        public void run() {
            try {
                boolean eos = false;
                while (!eos) {
                    OggPage op = OggPage.create(source);
                    synchronized (drainLock) {
                        int listSize = pageOffsets.size();

                        long pos = listSize > 0 ? pageOffsets.get(listSize - 1) + pageLengths.get(listSize - 1) : 0;

                        byte[] arr1 = op.getHeader();
                        byte[] arr2 = op.getSegmentTable();
                        byte[] arr3 = op.getData();

                        if (drain != null) {
                            drain.seek(pos);
                            drain.write(arr1);
                            drain.write(arr2);
                            drain.write(arr3);
                        } else {
                            System.arraycopy(arr1, 0, memoryCache, (int) pos, arr1.length);
                            System.arraycopy(arr2, 0, memoryCache, (int) pos + arr1.length,
                                    arr2.length);
                            System.arraycopy(arr3, 0, memoryCache, (int) pos + arr1.length
                                    + arr2.length, arr3.length);
                        }

                        pageOffsets.add(pos);
                        pageLengths.add((long) arr1.length + arr2.length + arr3.length);
                    }

                    if (!op.isBos()) {
                        bosDone = true;
                        // System.out.println("bosDone=true;");
                    }
                    if (op.isEos()) {
                        eos = true;
                    }

                    LogicalOggStreamImpl los = (LogicalOggStreamImpl) getLogicalStream(op
                            .getStreamSerialNumber());
                    if (los == null) {
                        los = new LogicalOggStreamImpl(CachedUrlStream.this);
                        logicalStreams.put(op.getStreamSerialNumber(), los);
                        los.checkFormat(op);
                    }

                    los.addPageNumberMapping(pageNumber);
                    los.addGranulePosition(op.getAbsoluteGranulePosition());

                    pageNumber++;
                    cacheLength = op.getAbsoluteGranulePosition();
                    // System.out.println("read page: "+pageNumber);
                }
            } catch (EndOfOggStreamException e) {
                // ok
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean closed = false;
    private URLConnection source;
    private InputStream sourceStream;
    private Object drainLock = new Object();
    private RandomAccessFile drain;
    private byte[] memoryCache;
    private ArrayList<Long> pageOffsets = new ArrayList<>();
    private ArrayList<Long> pageLengths = new ArrayList<>();

    private long cacheLength;

    private HashMap<Integer, LogicalOggStreamImpl> logicalStreams = new HashMap<>();

    private LoaderThread loaderThread;

    /**
     * Creates an instance of this class, using a memory cache.
     */
    public CachedUrlStream(URL source) throws IOException {
        this(source, null);
    }

    /**
     * Creates an instance of this class, using the specified file as cache. The file is not
     * automatically deleted when this class is disposed.
     */
    public CachedUrlStream(URL source, RandomAccessFile drain) throws OggFormatException,
            IOException {

        this.source = source.openConnection();

        if (drain == null) {
            int contentLength = this.source.getContentLength();
            if (contentLength == -1) {
                throw new IOException(
                        "The URLConncetion's content length must be set when operating with a in-memory cache.");
            }
            memoryCache = new byte[contentLength];
        }

        this.drain = drain;
        this.sourceStream = this.source.getInputStream();

        loaderThread = new LoaderThread(sourceStream, drain, memoryCache);
        new Thread(loaderThread, "Ogg-CachedUrlStream").start();

        while (!loaderThread.isBosDone() || pageOffsets.size() < 20) {
            // System.out.print("pageOffsets.size(): "+pageOffsets.size()+"\r");
            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
            }
        }
        // System.out.println();
        // System.out.println("caching "+pageOffsets.size()+"/20 pages\r");
    }

    public void close() throws IOException {
        closed = true;
        sourceStream.close();
    }

    public long getCacheLength() {
        return cacheLength;
    }

    private LogicalOggStream getLogicalStream(int serialNumber) {
        return logicalStreams.get(new Integer(serialNumber));
    }

    /*
     * private OggPage getNextPage() throws EndOfOggStreamException,
     * IOException, OggFormatException { return getNextPage(false); }
     *
     * private OggPage getNextPage(boolean skipData) throws
     * EndOfOggStreamException, IOException, OggFormatException { return
     * OggPage.create(sourceStream, skipData); }
     */
    public Collection<LogicalOggStreamImpl> getLogicalStreams() {
        return logicalStreams.values();
    }

    public OggPage getOggPage(int index) throws IOException {
        synchronized (drainLock) {
            Long offset = pageOffsets.get(index);
            Long length = pageLengths.get(index);
            if (offset != null) {
                if (drain != null) {
                    drain.seek(offset);
                    return OggPage.create(drain);
                } else {
                    byte[] tmpArray = new byte[length.intValue()];
                    System.arraycopy(memoryCache, offset.intValue(), tmpArray, 0, length.intValue());
                    return OggPage.create(tmpArray);
                }
            } else {
                return null;
            }
        }
    }

    public boolean isOpen() {
        return !closed;
    }

    public boolean isSeekable() {
        return true;
    }

    public void setTime(long granulePosition) throws IOException {
        for (LogicalOggStream los : logicalStreams.values()) {
            los.setTime(granulePosition);
        }
    }
}
