/*
 * $ProjectName$
 * $ProjectRevision$
 * -----------------------------------------------------------
 * $Id: BasicStream.java,v 1.1 2003/08/08 19:48:22 jarnbjo Exp $
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
 * $Log: BasicStream.java,v $
 *
 */

package de.jarnbjo.ogg;

import java.io.*;
import java.util.*;

/**
 * Implementation of the <code>PhysicalOggStream</code> interface for reading an
 * Ogg stream from a URL. This class performs no internal caching, and will not
 * read data from the network before requested to do so. It is intended to be
 * used in non-realtime applications like file download managers or similar.
 */

public class BasicStream implements PhysicalOggStream {

    private boolean closed = false;
    private InputStream sourceStream;
    private int position = 0;

    private HashMap<Integer, LogicalOggStreamImpl> logicalStreams = new HashMap<Integer, LogicalOggStreamImpl>();
    private OggPage firstPage;

    int pageNumber = 2;

    public BasicStream(InputStream sourceStream) throws OggFormatException, IOException {
        firstPage = OggPage.create(sourceStream);
        position += firstPage.getTotalLength();
        LogicalOggStreamImpl los = new LogicalOggStreamImpl(this);
        logicalStreams.put(new Integer(firstPage.getStreamSerialNumber()), los);
        los.checkFormat(firstPage);
    }

    public void close() throws IOException {
        closed = true;
        sourceStream.close();
    }

    public int getContentLength() {
        return -1;
    }

    public Collection<LogicalOggStreamImpl> getLogicalStreams() {
        return logicalStreams.values();
    }

    public OggPage getOggPage(int index) throws IOException {
        if (firstPage != null) {
            OggPage tmp = firstPage;
            firstPage = null;
            return tmp;
        } else {
            OggPage page = OggPage.create(sourceStream);
            position += page.getTotalLength();
            return page;
        }
    }

    public int getPosition() {
        return position;
    }

    public boolean isOpen() {
        return !closed;
    }

    /**
     * @return always <code>false</code>
     */

    public boolean isSeekable() {
        return false;
    }

    public void setTime(long granulePosition) throws IOException {
        throw new UnsupportedOperationException("Method not supported by this class");
    }

}