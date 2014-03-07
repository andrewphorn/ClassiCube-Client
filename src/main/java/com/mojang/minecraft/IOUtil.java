package com.mojang.minecraft;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

// Utility class to simplify working with streams efficiently
public class IOUtil {
    private static final int BUFFER_SIZE = 64 * 1024; // 64 KB

    // Reads given stream to the end, and writes its contents to a file
    public static void copyStreamToFile(InputStream inStream, File file)
            throws IOException {
        ReadableByteChannel in = Channels.newChannel(inStream);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            try {
                FileChannel out = outStream.getChannel();
                long offset = 0;
                long count;
                while ((count = out.transferFrom(in, offset, BUFFER_SIZE)) > 0) {
                    offset += count;
                }
            } finally {
                outStream.close();
            }
        } finally {
            in.close();
        }
    }
}
