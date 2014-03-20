package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBTBase {
    /**
     * The string value for the tag (cannot be empty).
     */
    public String data;

    public NBTTagString(String name) {
        super(name);
    }

    public NBTTagString(String name, String data) {
        super(name);
        if (data == null) {
            throw new IllegalArgumentException("Empty string not allowed");
        }

        this.data = data;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes.
     *
     * @param output The output stream to write to.
     */
    @Override
    void write(DataOutput output) throws IOException {
        output.writeUTF(this.data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes.
     *
     * @param input The input stream to read from.
     */
    @Override
    void load(DataInput input) throws IOException {
        this.data = input.readUTF();
    }

    /**
     * Gets the type byte for the tag.
     *
     * @return byte
     */
    @Override
    public byte getId() {
        return (byte) 8;
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
        return new NBTTagString(this.getName(), this.data);
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        } else {
            NBTTagString tempOther = (NBTTagString) other;
            return this.data == null && tempOther.data == null || this.data != null
                    && this.data.equals(tempOther.data);
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.data.hashCode();
    }
}
