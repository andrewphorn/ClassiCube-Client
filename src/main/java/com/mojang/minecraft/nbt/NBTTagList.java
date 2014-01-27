package com.mojang.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NBTTagList extends NBTBase
{
    /** The array list containing the tags encapsulated in this list. */
    private List<NBTBase> tagList = new ArrayList<NBTBase>();

    /**
     * The type byte for the tags in the list - they must all be of the same type.
     */
    private byte tagType;

    public NBTTagList()
    {
        super("");
    }

    public NBTTagList(String data)
    {
        super(data);
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    @Override
    void write(DataOutput par1DataOutput) throws IOException
    {
        if (!this.tagList.isEmpty())
        {
            this.tagType = this.tagList.get(0).getId();
        }
        else
        {
            this.tagType = 1;
        }

        par1DataOutput.writeByte(this.tagType);
        par1DataOutput.writeInt(this.tagList.size());

        for (int i = 0; i < this.tagList.size(); ++i)
        {
            this.tagList.get(i).write(par1DataOutput);
        }
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    @Override
    void load(DataInput par1DataInput) throws IOException
    {
        this.tagType = par1DataInput.readByte();
        int i = par1DataInput.readInt();
        this.tagList = new ArrayList<NBTBase>();

        for (int j = 0; j < i; ++j)
        {
            NBTBase nbtbase = NBTBase.newTag(this.tagType, (String)null);
            nbtbase.load(par1DataInput);
            this.tagList.add(nbtbase);
        }
    }

    /**
     * Gets the type byte for the tag.
     */
    @Override
    public byte getId()
    {
        return (byte)9;
    }

    @Override
    public String toString()
    {
        return "" + this.tagList.size() + " entries of type " + NBTBase.getname(this.tagType);
    }

    /**
     * Adds the provided tag to the end of the list. There is no check to verify this tag is of the same type as any
     * previous tag.
     */
    public void appendTag(NBTBase tag)
    {
        this.tagType = tag.getId();
        this.tagList.add(tag);
   }
    /**
     * Removes a tag at the given index.
     */
    public NBTBase removeTag(int index)
    {
        return this.tagList.remove(index);
    }

    /**
     * Retrieves the tag at the specified index from the list.
     */
    public NBTBase tagAt(int index)
    {
        return this.tagList.get(index);
    }

    /**
     * Returns the number of tags in the list.
     */
    public int tagCount()
    {
        return this.tagList.size();
    }

    /**
     * Creates a clone of the tag.
     */
    @Override
    public NBTBase copy()
    {
        NBTTagList finalTagList = new NBTTagList(this.getName());
        nbttaglist.tagType = this.tagType;
        Iterator<NBTBase> iter = this.tagList.iterator();

        while (iter.hasNext())
        {
            NBTBase nextTag = iter.next();
            NBTBase nextTagByValue = nextTag.copy();
            finalTagList.tagList.add(nextTagByValue);
        }

        return finalTagList;
    }

    @Override
    public boolean equals(Object other)
    {
        if (super.equals(other))
        {
            NBTTagList tempOther = (NBTTagList)other;

            if (this.tagType == tempOther.tagType)
            {
                return this.tagList.equals(tempOther.tagList);
            }
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode() ^ this.tagList.hashCode();
    }
}
