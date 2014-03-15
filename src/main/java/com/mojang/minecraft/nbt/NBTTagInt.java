package com.mojang.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTBase {
    /** The integer value for the tag. */
    public int data;

    public NBTTagInt(String name) {
        super(name);
    }

    public NBTTagInt(String name, int data) {
        super(name);
        this.data = data;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    @Override
    void write(DataOutput output) throws IOException {
        output.writeInt(this.data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    @Override
    void load(DataInput output) throws IOException {
        this.data = output.readInt();
    }

    /**
     * Gets the type byte for the tag.
     */
    @Override
    public byte getId() {
        return (byte) 3;
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
        return new NBTTagInt(this.getName(), this.data);
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            NBTTagInt tempOther = (NBTTagInt) other;
            return this.data == tempOther.data;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.data;
    }
}
