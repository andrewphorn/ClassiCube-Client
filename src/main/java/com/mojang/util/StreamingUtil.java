package com.mojang.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * Utility class to deal with streams more efficiently.
 */

public class StreamingUtil {
    private static final int BUFFER_SIZE = 64 * 1024; // 64 KB

    /**
     * Reads given stream to the end, and writes its contents to a file
     *
     * @param inStream The input buffer stream.
     * @param file     The file to write to.
     * @throws IOException
     */
    public static void copyStreamToFile(InputStream inStream, File file) throws IOException {
        try (ReadableByteChannel in = Channels.newChannel(inStream)) {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            try (FileOutputStream outStream = new FileOutputStream(file)) {
                FileChannel out = outStream.getChannel();
                long offset = 0;
                long count;
                while ((count = out.transferFrom(in, offset, BUFFER_SIZE)) > 0) {
                    offset += count;
                }
            }
        }
    }
}
