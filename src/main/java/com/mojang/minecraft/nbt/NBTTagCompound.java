package com.mojang.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NBTTagCompound extends NBTBase
{
    /**
     * The key-value pairs for the tag. Each key is a UTF string, each value is a tag.
     */
    private Map<String, NBTBase> tagMap = new HashMap<String, NBTBase>();

    public NBTTagCompound()
    {
        super("");
    }

    public NBTTagCompound(String par1Str)
    {
        super(par1Str);
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    @Override
	void write(DataOutput par1DataOutput) throws IOException
    {
        Iterator<NBTBase> iterator = this.tagMap.values().iterator();

        while (iterator.hasNext())
        {
            NBTBase nbtbase = iterator.next();
            NBTBase.writeNamedTag(nbtbase, par1DataOutput);
        }

        par1DataOutput.writeByte(0);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    @Override
	void load(DataInput par1DataInput) throws IOException
    {
        this.tagMap.clear();
        NBTBase nbtbase;

        while ((nbtbase = NBTBase.readNamedTag(par1DataInput)).getId() != 0)
        {
            this.tagMap.put(nbtbase.getName(), nbtbase);
        }
    }

    /**
     * Returns all the values in the tagMap HashMap.
     */
    public Collection<NBTBase> getTags()
    {
        return this.tagMap.values();
    }

    /**
     * Gets the type byte for the tag.
     */
    @Override
	public byte getId()
    {
        return (byte)10;
    }

    /**
     * Stores the given tag into the map with the given string key. This is mostly used to store tag lists.
     */
    public void setTag(String par1Str, NBTBase par2NBTBase)
    {
        this.tagMap.put(par1Str, par2NBTBase.setName(par1Str));
    }

    /**
     * Stores a new NBTTagByte with the given byte value into the map with the given string key.
     */
    public void setByte(String par1Str, byte par2)
    {
        this.tagMap.put(par1Str, new NBTTagByte(par1Str, par2));
    }

    /**
     * Stores a new NBTTagShort with the given short value into the map with the given string key.
     */
    public void setShort(String par1Str, short par2)
    {
        this.tagMap.put(par1Str, new NBTTagShort(par1Str, par2));
    }

    /**
     * Stores a new NBTTagInt with the given integer value into the map with the given string key.
     */
    public void setInteger(String par1Str, int par2)
    {
        this.tagMap.put(par1Str, new NBTTagInt(par1Str, par2));
    }

    /**
     * Stores a new NBTTagLong with the given long value into the map with the given string key.
     */
    public void setLong(String par1Str, long par2)
    {
        this.tagMap.put(par1Str, new NBTTagLong(par1Str, par2));
    }

    /**
     * Stores a new NBTTagFloat with the given float value into the map with the given string key.
     */
    public void setFloat(String par1Str, float par2)
    {
        this.tagMap.put(par1Str, new NBTTagFloat(par1Str, par2));
    }

    /**
     * Stores a new NBTTagDouble with the given double value into the map with the given string key.
     */
    public void setDouble(String par1Str, double par2)
    {
        this.tagMap.put(par1Str, new NBTTagDouble(par1Str, par2));
    }

    /**
     * Stores a new NBTTagString with the given string value into the map with the given string key.
     */
    public void setString(String par1Str, String par2Str)
    {
        this.tagMap.put(par1Str, new NBTTagString(par1Str, par2Str));
    }

    /**
     * Stores a new NBTTagByteArray with the given array as data into the map with the given string key.
     */
    public void setByteArray(String par1Str, byte[] par2ArrayOfByte)
    {
        this.tagMap.put(par1Str, new NBTTagByteArray(par1Str, par2ArrayOfByte));
    }

    /**
     * Stores a new NBTTagIntArray with the given array as data into the map with the given string key.
     */
    public void setIntArray(String par1Str, int[] par2ArrayOfInteger)
    {
        this.tagMap.put(par1Str, new NBTTagIntArray(par1Str, par2ArrayOfInteger));
    }

    /**
     * Stores the given NBTTagCompound into the map with the given string key.
     */
    public void setCompoundTag(String par1Str, NBTTagCompound par2NBTTagCompound)
    {
        this.tagMap.put(par1Str, par2NBTTagCompound.setName(par1Str));
    }

    /**
     * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for false, using the given string key.
     */
    public void setBoolean(String par1Str, boolean par2)
    {
        this.setByte(par1Str, (byte)(par2 ? 1 : 0));
    }

    /**
     * gets a generic tag with the specified name
     */
    public NBTBase getTag(String par1Str)
    {
        return this.tagMap.get(par1Str);
    }

    /**
     * Returns whether the given string has been previously stored as a key in the map.
     */
    public boolean hasKey(String par1Str)
    {
        return this.tagMap.containsKey(par1Str);
    }

    /**
     * Retrieves a byte value using the specified key, or 0 if no such key was stored.
     */
    public byte getByte(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0 : ((NBTTagByte)this.tagMap.get(par1Str)).data;
        }
        catch (ClassCastException classcastexception)
        {
            throw  classcastexception;
        }
    }

    /**
     * Retrieves a short value using the specified key, or 0 if no such key was stored.
     */
    public short getShort(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0 : ((NBTTagShort)this.tagMap.get(par1Str)).data;
        }
        catch (ClassCastException classcastexception)
        {
            throw classcastexception;
        }
    }

    /**
     * Retrieves an integer value using the specified key, or 0 if no such key was stored.
     */
    public int getInteger(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0 : ((NBTTagInt)this.tagMap.get(par1Str)).data;
        }
        catch (ClassCastException classcastexception)
        {
            throw classcastexception;
        }
    }

    /**
     * Retrieves a long value using the specified key, or 0 if no such key was stored.
     */
    public long getLong(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0L : ((NBTTagLong)this.tagMap.get(par1Str)).data;
        }
        catch (ClassCastException classcastexception)
        {
            throw classcastexception;
        }
    }

    /**
     * Retrieves a float value using the specified key, or 0 if no such key was stored.
     */
    public float getFloat(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0.0F : ((NBTTagFloat)this.tagMap.get(par1Str)).data;
        }
        catch (ClassCastException classcastexception)
        {
            throw classcastexception;
        }
    }

    /**
     * Retrieves a double value using the specified key, or 0 if no such key was stored.
     */
    public double getDouble(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? 0.0D : ((NBTTagDouble)this.tagMap.get(par1Str)).data;
        }
        catch (ClassCastException classcastexception)
        {
            throw classcastexception;
        }
    }

    /**
     * Retrieves a string value using the specified key, or an empty string if no such key was stored.
     */
    public String getString(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? "" : ((NBTTagString)this.tagMap.get(par1Str)).data;
        }
        catch (ClassCastException classcastexception)
        {
            throw classcastexception;
        }
    }

    /**
     * Retrieves a byte array using the specified key, or a zero-length array if no such key was stored.
     */
    public byte[] getByteArray(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? new byte[0] : ((NBTTagByteArray)this.tagMap.get(par1Str)).byteArray;
        }
        catch (ClassCastException classcastexception)
        {
            throw classcastexception;
        }
    }

    /**
     * Retrieves an int array using the specified key, or a zero-length array if no such key was stored.
     */
    public int[] getIntArray(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? new int[0] : ((NBTTagIntArray)this.tagMap.get(par1Str)).intArray;
        }
        catch (ClassCastException classcastexception)
        {
            throw classcastexception;
        }
    }

    /**
     * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty NBTTagCompound if no such key was
     * stored.
     */
    public NBTTagCompound getCompoundTag(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? new NBTTagCompound(par1Str) : (NBTTagCompound)this.tagMap.get(par1Str);
        }
        catch (ClassCastException classcastexception)
        {
            throw  classcastexception;
        }
    }

    /**
     * Retrieves a NBTTagList subtag matching the specified key, or a new empty NBTTagList if no such key was stored.
     */
    public NBTTagList getTagList(String par1Str)
    {
        try
        {
            return !this.tagMap.containsKey(par1Str) ? new NBTTagList(par1Str) : (NBTTagList)this.tagMap.get(par1Str);
        }
        catch (ClassCastException classcastexception)
        {
            throw  classcastexception;
        }
    }

    /**
     * Retrieves a boolean value using the specified key, or false if no such key was stored. This uses the getByte
     * method.
     */
    public boolean getBoolean(String par1Str)
    {
        return this.getByte(par1Str) != 0;
    }

    /**
     * Remove the specified tag.
     */
    public void removeTag(String par1Str)
    {
        this.tagMap.remove(par1Str);
    }

    @Override
	public String toString()
    {
        String s = this.getName() + ":[";
        String s1;

        for (Iterator<String> iterator = this.tagMap.keySet().iterator(); iterator.hasNext(); s = s + s1 + ":" + this.tagMap.get(s1) + ",")
        {
            s1 = iterator.next();
        }

        return s + "]";
    }

    /**
     * Return whether this compound has no tags.
     */
    public boolean hasNoTags()
    {
        return this.tagMap.isEmpty();
    }

    /**
     * Creates a clone of the tag.
     */
    @Override
	public NBTBase copy()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound(this.getName());
        Iterator<String> iterator = this.tagMap.keySet().iterator();

        while (iterator.hasNext())
        {
            String s = iterator.next();
            nbttagcompound.setTag(s, this.tagMap.get(s).copy());
        }

        return nbttagcompound;
    }

    @Override
	public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagCompound nbttagcompound = (NBTTagCompound)par1Obj;
            return this.tagMap.entrySet().equals(nbttagcompound.tagMap.entrySet());
        }
        else
        {
            return false;
        }
    }

    @Override
	public int hashCode()
    {
        return super.hashCode() ^ this.tagMap.hashCode();
    }

    /**
     * Return the tag map for this compound.
     */
    static Map<String, NBTBase> getTagMap(NBTTagCompound par0NBTTagCompound)
    {
        return par0NBTTagCompound.tagMap;
    }
}
