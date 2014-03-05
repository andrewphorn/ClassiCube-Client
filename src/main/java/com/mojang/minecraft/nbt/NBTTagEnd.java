package com.mojang.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd extends NBTBase
{
	public NBTTagEnd()
	{
		super((String)null);
	}

	/**
	 * Read the actual data contents of the tag, implemented in NBT extension classes
	 */
	@Override
	void load(DataInput par1DataInput) throws IOException {}

	/**
	 * Write the actual data contents of the tag, implemented in NBT extension classes
	 */
	@Override
	void write(DataOutput par1DataOutput) throws IOException {}

	/**
	 * Gets the type byte for the tag.
	 */
	@Override
	public byte getId()
	{
		return (byte)0;
	}

	@Override
	public String toString()
	{
		return "END";
	}

	/**
	 * Creates a clone of the tag.
	 */
	@Override
	public NBTBase copy()
	{
		return new NBTTagEnd();
	}
}
