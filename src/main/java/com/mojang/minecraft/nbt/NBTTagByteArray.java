package com.mojang.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase
{
    /** The byte array stored in the tag. */
    public byte[] byteArray;

    public NBTTagByteArray(String name)
    {
        super(name);
    }

    public NBTTagByteArray(String name, byte[] byteArrayInput)
    {
        super(name);
        this.byteArray = byteArrayInput;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    @Override
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.byteArray.length);
        par1DataOutput.write(this.byteArray);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    @Override
    void load(DataInput par1DataInput) throws IOException
    {
        int i = par1DataInput.readInt();
        this.byteArray = new byte[i];
        par1DataInput.readFully(this.byteArray);
    }

    /**
     * Gets the type byte for the tag.
     */
    @Override
    public byte getId()
    {
        return (byte)7;
    }

    @Override
    public String toString()
    {
        return "[" + this.byteArray.length + " bytes]";
    }

    /**
     * Creates a clone of the tag.
     */
    @Override
    public NBTBase copy()
    {
        byte[] abyte = new byte[this.byteArray.length];
        System.arraycopy(this.byteArray, 0, abyte, 0, this.byteArray.length);
        return new NBTTagByteArray(this.getName(), abyte);
    }

    @Override
    public boolean equals(Object other)
    {
        return super.equals(other) ? Arrays.equals(this.byteArray, ((NBTTagByteArray)other).byteArray) : false;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode() ^ Arrays.hashCode(this.byteArray);
    }
}
