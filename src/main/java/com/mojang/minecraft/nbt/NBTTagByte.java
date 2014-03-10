package com.mojang.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTBase {
	/** The byte value for the tag. */
	public byte data;

	public NBTTagByte(String name) {
		super(name);
	}

	public NBTTagByte(String name, byte data) {
		super(name);
		this.data = data;
	}

	/**
	 * Write the actual data contents of the tag, implemented in NBT extension
	 * classes
	 */
	@Override
	void write(DataOutput par1DataOutput) throws IOException {
		par1DataOutput.writeByte(this.data);
	}

	/**
	 * Read the actual data contents of the tag, implemented in NBT extension
	 * classes
	 */
	@Override
	void load(DataInput par1DataInput) throws IOException {
		this.data = par1DataInput.readByte();
	}

	/**
	 * Gets the type byte for the tag.
	 */
	@Override
	public byte getId() {
		return (byte) 1;
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
		return new NBTTagByte(this.getName(), this.data);
	}

	@Override
	public boolean equals(Object other) {
		if (super.equals(other)) {
			NBTTagByte tempOther = (NBTTagByte) other;
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
