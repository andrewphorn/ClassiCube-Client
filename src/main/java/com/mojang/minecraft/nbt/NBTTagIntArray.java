package com.mojang.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase {
    /** The array of saved integers */
    public int[] intArray;

    public NBTTagIntArray(String name) {
        super(name);
    }

    public NBTTagIntArray(String name, int[] intArrayInput) {
        super(name);
        this.intArray = intArrayInput;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    @Override
    void write(DataOutput par1DataOutput) throws IOException {
        par1DataOutput.writeInt(this.intArray.length);

        for (int i = 0; i < this.intArray.length; ++i) {
            par1DataOutput.writeInt(this.intArray[i]);
        }
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    @Override
    void load(DataInput par1DataInput) throws IOException {
        int i = par1DataInput.readInt();
        this.intArray = new int[i];

        for (int j = 0; j < i; ++j) {
            this.intArray[j] = par1DataInput.readInt();
        }
    }

    /**
     * Gets the type byte for the tag.
     */
    @Override
    public byte getId() {
        return (byte) 11;
    }

    @Override
    public String toString() {
        return "[" + this.intArray.length + " bytes]";
    }

    /**
     * Creates a clone of the tag.
     */
    @Override
    public NBTBase copy() {
        int[] aint = new int[this.intArray.length];
        System.arraycopy(this.intArray, 0, aint, 0, this.intArray.length);
        return new NBTTagIntArray(this.getName(), aint);
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        } else {
            NBTTagIntArray tempOther = (NBTTagIntArray) other;
            return this.intArray == null && tempOther.intArray == null || this.intArray != null
                    && Arrays.equals(this.intArray, tempOther.intArray);
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.intArray);
    }
}
