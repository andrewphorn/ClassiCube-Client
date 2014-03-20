package com.mojang.nbt;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedStreamTools {

    /**
     * Load the gzipped compound from the InputStream.
     */
    public static NBTTagCompound readCompressed(InputStream stream) throws IOException {
        NBTTagCompound compound;
        try (DataInputStream inStream = new DataInputStream(new BufferedInputStream(
                new GZIPInputStream(stream)))) {
            compound = read(inStream);
        }
        return compound;
    }

    /**
     * Write the compound, gzipped, to the OutputStream.
     */
    public static void writeCompressed(NBTTagCompound tag, OutputStream stream) throws IOException {
        try (DataOutputStream outStream = new DataOutputStream(new GZIPOutputStream(stream))) {
            write(tag, outStream);
        }
    }

    public static NBTTagCompound decompress(byte[] buffer) throws IOException {
        NBTTagCompound compound;
        try (DataInputStream inputStream = new DataInputStream(new BufferedInputStream(
                new GZIPInputStream(new ByteArrayInputStream(buffer))))) {
            compound = read(inputStream);
        }
        return compound;
    }

    public static byte[] compress(NBTTagCompound tag) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DataOutputStream outStream = new DataOutputStream(new GZIPOutputStream(
                byteArrayOutputStream))) {
            write(tag, outStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static void safeWrite(NBTTagCompound compound, File file) throws IOException {
        File file2 = new File(file.getAbsolutePath() + "_tmp");

        if (file2.exists()) {
            file2.delete();
        }

        write(compound, file2);

        if (file.exists()) {
            file.delete();
        }

        if (file.exists()) {
            throw new IOException("Failed to delete " + file);
        } else {
            file2.renameTo(file);
        }
    }

    /**
     * Reads from a CompressedStream.
     */
    public static NBTTagCompound read(DataInput input) throws IOException {
        NBTBase content = NBTBase.readNamedTag(input);

        if (content instanceof NBTTagCompound) {
            return (NBTTagCompound) content;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void write(NBTTagCompound compound, DataOutput output) throws IOException {
        NBTBase.writeNamedTag(compound, output);
    }

    public static void write(NBTTagCompound tag, File file) throws IOException {
        try (DataOutputStream outStream = new DataOutputStream(new FileOutputStream(file))) {
            write(tag, outStream);
        }
    }

    public static NBTTagCompound read(File file) throws IOException {
        if (!file.exists()) {
            return null;
        } else {
            NBTTagCompound compound;
            try (DataInputStream inStream = new DataInputStream(new FileInputStream(file))) {
                compound = read(inStream);
            }
            return compound;
        }
    }
}
