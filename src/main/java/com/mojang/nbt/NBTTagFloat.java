package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagFloat extends NBTBase {
    /**
     * The float value for the tag.
     */
    public float data;

    public NBTTagFloat(String name) {
        super(name);
    }

    public NBTTagFloat(String name, float data) {
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
        output.writeFloat(this.data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes.
     *
     * @param input The input stream to read from.
     */
    @Override
    void load(DataInput input) throws IOException {
        this.data = input.readFloat();
    }

    /**
     * Gets the type byte for the tag.
     *
     * @return byte
     */
    @Override
    public byte getId() {
        return (byte) 5;
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
        return new NBTTagFloat(this.getName(), this.data);
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            NBTTagFloat tempOther = (NBTTagFloat) other;
            return this.data == tempOther.data;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Float.floatToIntBits(this.data);
    }
}
