package com.mojang.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTBase
{
    /** The short value for the tag. */
    public short data;

    public NBTTagShort(String name)
    {
        super(name);
    }

    public NBTTagShort(String name, short par2)
    {
        super(name);
        this.data = par2;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    @Override
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeShort(this.data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    @Override
    void load(DataInput par1DataInput) throws IOException
    {
        this.data = par1DataInput.readShort();
    }

    /**
     * Gets the type byte for the tag.
     */
    @Override
    public byte getId()
    {
        return (byte)2;
    }

    @Override
    public String toString()
    {
        return "" + this.data;
    }

    /**
     * Creates a clone of the tag.
     */
    @Override
    public NBTBase copy()
    {
        return new NBTTagShort(this.getName(), this.data);
    }

    @Override
    public boolean equals(Object other)
    {
        if (super.equals(other))
        {
            NBTTagShort tempOther = (NBTTagShort)other;
            return this.data == tempOther.data;
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return super.hashCode() ^ this.data;
    }
}
