package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTBase {
    /**
     * The long value for the tag.
     */
    public long data;

    public NBTTagLong(String name) {
        super(name);
    }

    public NBTTagLong(String name, long data) {
        super(name);
        this.data = data;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes.
     *
     * @param output The output stream to write to.
     */
    @Override
    void write(DataOutput output) throws IOException {
        output.writeLong(this.data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes.
     *
     * @param input The input stream to read from.
     */
    @Override
    void load(DataInput input) throws IOException {
        this.data = input.readLong();
    }

    /**
     * Gets the type byte for the tag.
     *
     * @return byte
     */
    @Override
    public byte getId() {
        return (byte) 4;
    }

    @Override
    public String toString() {
        return "" + this.data;
    }

    /**
     * Creates a clone of the tag.
     */
    @Override
    public NBTBase copy() {
        return new NBTTagLong(this.getName(), this.data);
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            NBTTagLong tempOther = (NBTTagLong) other;
            return this.data == tempOther.data;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ (int) (this.data ^ this.data >>> 32);
    }
}
