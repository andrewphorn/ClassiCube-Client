package com.mojang.minecraft.nbt;

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
     * Load the gzipped compound from the inputstream.
     */
    public static NBTTagCompound readCompressed(InputStream stream) throws IOException {
        NBTTagCompound nbttagcompound;
        try (DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(stream)))) {
            nbttagcompound = read(datainputstream);
        }
        return nbttagcompound;
    }

    /**
     * Write the compound, gzipped, to the outputstream.
     */
    public static void writeCompressed(NBTTagCompound tag, OutputStream stream)
            throws IOException {
        try (DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(stream))) {
            write(tag, dataoutputstream);
        }
    }

    public static NBTTagCompound decompress(byte[] buffer) throws IOException {
        NBTTagCompound nbttagcompound;
        try (DataInputStream dataStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(buffer))))) {
            nbttagcompound = read(dataStream);
        }
        return nbttagcompound;
    }

    public static byte[] compress(NBTTagCompound tag) throws IOException {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        try (DataOutputStream dataStream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream))) {
            write(tag, dataStream);
        }
        return bytearrayoutputstream.toByteArray();
    }

    public static void safeWrite(NBTTagCompound par0NBTTagCompound, File par1File) throws IOException {
        File file2 = new File(par1File.getAbsolutePath() + "_tmp");

        if (file2.exists()) {
            file2.delete();
        }

        write(par0NBTTagCompound, file2);

        if (par1File.exists()) {
            par1File.delete();
        }

        if (par1File.exists()) {
            throw new IOException("Failed to delete " + par1File);
        } else {
            file2.renameTo(par1File);
        }
    }

    /**
     * Reads from a CompressedStream.
     */
    public static NBTTagCompound read(DataInput par0DataInput) throws IOException {
        NBTBase nbtbase = NBTBase.readNamedTag(par0DataInput);

        if (nbtbase instanceof NBTTagCompound) {
            return (NBTTagCompound) nbtbase;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void write(NBTTagCompound par0NBTTagCompound, DataOutput par1DataOutput) throws IOException {
        NBTBase.writeNamedTag(par0NBTTagCompound, par1DataOutput);
    }

    public static void write(NBTTagCompound tag, File file) throws IOException {
        try (DataOutputStream dataStream = new DataOutputStream(new FileOutputStream(file))) {
            write(tag, dataStream);
        }
    }

    public static NBTTagCompound read(File par0File) throws IOException {
        if (!par0File.exists()) {
            return null;
        } else {
            NBTTagCompound nbttagcompound;
            try (DataInputStream datainputstream = new DataInputStream(new FileInputStream(par0File))) {
                nbttagcompound = read(datainputstream);
            }
            return nbttagcompound;
        }
    }
}
