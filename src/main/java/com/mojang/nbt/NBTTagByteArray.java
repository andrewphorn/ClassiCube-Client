package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase {
    /**
     * The byte array stored in the tag.
     */
    public byte[] byteArray;

    public NBTTagByteArray(String name) {
        super(name);
    }

    public NBTTagByteArray(String name, byte[] byteArrayInput) {
        super(name);
        this.byteArray = byteArrayInput;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes.
     *
     * @param output The output stream to write to.
     */
    @Override
    void write(DataOutput output) throws IOException {
        output.writeInt(this.byteArray.length);
        output.write(this.byteArray);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes.
     *
     * @param input The input stream to read from.
     */
    @Override
    void load(DataInput input) throws IOException {
        int i = input.readInt();
        this.byteArray = new byte[i];
        input.readFully(this.byteArray);
    }

    /**
     * Gets the type byte for the tag.
     *
     * @return byte
     */
    @Override
    public byte getId() {
        return (byte) 7;
    }

    @Override
    public String toString() {
        return "[" + this.byteArray.length + " bytes]";
    }

    /**
     * Creates a clone of the tag.
     */
    @Override
    public NBTBase copy() {
        byte[] abyte = new byte[this.byteArray.length];
        System.arraycopy(this.byteArray, 0, abyte, 0, this.byteArray.length);
        return new NBTTagByteArray(this.getName(), abyte);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other) && Arrays.equals(this.byteArray,
                ((NBTTagByteArray) other).byteArray);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.byteArray);
    }
}
