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

	public NBTTagCompound(String name)
	{
		super(name);
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
	public void setTag(String name, NBTBase tag)
	{
		this.tagMap.put(name, tag.setName(name));
	}

	/**
	 * Stores a new NBTTagByte with the given byte value into the map with the given string key.
	 */
	public void setByte(String name, byte theByte)
	{
		this.tagMap.put(name, new NBTTagByte(name, theByte));
	}

	/**
	 * Stores a new NBTTagShort with the given short value into the map with the given string key.
	 */
	public void setShort(String name, short theShort)
	{
		this.tagMap.put(name, new NBTTagShort(name, theShort));
	}

	/**
	 * Stores a new NBTTagInt with the given integer value into the map with the given string key.
	 */
	public void setInteger(String name, int theInt)
	{
		this.tagMap.put(name, new NBTTagInt(name, theInt));
	}

	/**
	 * Stores a new NBTTagLong with the given long value into the map with the given string key.
	 */
	public void setLong(String name, long theLong)
	{
		this.tagMap.put(name, new NBTTagLong(name, theLong));
	}

	/**
	 * Stores a new NBTTagFloat with the given float value into the map with the given string key.
	 */
	public void setFloat(String name, float theStr)
	{
		this.tagMap.put(name, new NBTTagFloat(name, theStr));
	}

	/**
	 * Stores a new NBTTagDouble with the given double value into the map with the given string key.
	 */
	public void setDouble(String name, double theDouble)
	{
		this.tagMap.put(name, new NBTTagDouble(name, theDouble));
	}

	/**
	 * Stores a new NBTTagString with the given string value into the map with the given string key.
	 */
	public void setString(String name, String theDouble)
	{
		this.tagMap.put(name, new NBTTagString(name, theDouble));
	}

	/**
	 * Stores a new NBTTagByteArray with the given array as data into the map with the given string key.
	 */
	public void setByteArray(String name, byte[] theByteArray)
	{
		this.tagMap.put(name, new NBTTagByteArray(name, theByteArray));
	}

	/**
	 * Stores a new NBTTagIntArray with the given array as data into the map with the given string key.
	 */
	public void setIntArray(String name, int[] theIntArray)
	{
		this.tagMap.put(name, new NBTTagIntArray(name, theIntArray));
	}

	/**
	 * Stores the given NBTTagCompound into the map with the given string key.
	 */
	public void setCompoundTag(String name, NBTTagCompound theCompound)
	{
		this.tagMap.put(name, theCompound.setName(name));
	}

	/**
	 * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for false, using the given string key.
	 */
	public void setBoolean(String name, boolean theBool)
	{
		this.setByte(name, (byte)(theBool ? 1 : 0));
	}

	/**
	 * gets a generic tag with the specified name
	 */
	public NBTBase getTag(String name)
	{
		return this.tagMap.get(name);
	}

	/**
	 * Returns whether the given string has been previously stored as a key in the map.
	 */
	public boolean hasKey(String name)
	{
		return this.tagMap.containsKey(name);
	}

	/**
	 * Retrieves a byte value using the specified key, or 0 if no such key was stored.
	 */
	public byte getByte(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? 0 : ((NBTTagByte)this.tagMap.get(name)).data;
		}
		catch (ClassCastException e)
		{
			throw e;
		}
	}

	/**
	 * Retrieves a short value using the specified key, or 0 if no such key was stored.
	 */
	public short getShort(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? 0 : ((NBTTagShort)this.tagMap.get(name)).data;
		}
		catch (ClassCastException e)
		{
			throw e;
		}
	}

	/**
	 * Retrieves an integer value using the specified key, or 0 if no such key was stored.
	 */
	public int getInteger(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? 0 : ((NBTTagInt)this.tagMap.get(name)).data;
		}
		catch (ClassCastException e)
		{
			throw e;
		}
	}

	/**
	 * Retrieves a long value using the specified key, or 0 if no such key was stored.
	 */
	public long getLong(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? 0L : ((NBTTagLong)this.tagMap.get(name)).data;
		}
		catch (ClassCastException e)
		{
			throw e;
		}
	}

	/**
	 * Retrieves a float value using the specified key, or 0 if no such key was stored.
	 */
	public float getFloat(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? 0.0F : ((NBTTagFloat)this.tagMap.get(name)).data;
		}
		catch (ClassCastException e)
		{
			throw e;
		}
	}

	/**
	 * Retrieves a double value using the specified key, or 0 if no such key was stored.
	 */
	public double getDouble(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? 0.0D : ((NBTTagDouble)this.tagMap.get(name)).data;
		}
		catch (ClassCastException e)
		{
			throw e;
		}
	}

	/**
	 * Retrieves a string value using the specified key, or an empty string if no such key was stored.
	 */
	public String getString(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? "" : ((NBTTagString)this.tagMap.get(name)).data;
		}
		catch (ClassCastException e)
		{
			throw e;
		}
	}

	/**
	 * Retrieves a byte array using the specified key, or a zero-length array if no such key was stored.
	 */
	public byte[] getByteArray(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? new byte[0] : ((NBTTagByteArray)this.tagMap.get(name)).byteArray;
		}
		catch (ClassCastException e)
		{
			throw e;
		}
	}

	/**
	 * Retrieves an int array using the specified key, or a zero-length array if no such key was stored.
	 */
	public int[] getIntArray(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? new int[0] : ((NBTTagIntArray)this.tagMap.get(name)).intArray;
		}
		catch (ClassCastException e)
		{
			throw e;
		}
	}

	/**
	 * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty NBTTagCompound if no such key was
	 * stored.
	 */
	public NBTTagCompound getCompoundTag(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? new NBTTagCompound(name) : (NBTTagCompound)this.tagMap.get(name);
		}
		catch (ClassCastException e)
		{
			throw e;
		}
	}

	/**
	 * Retrieves a NBTTagList subtag matching the specified key, or a new empty NBTTagList if no such key was stored.
	 */
	public NBTTagList getTagList(String name)
	{
		try
		{
			return !this.tagMap.containsKey(name) ? new NBTTagList(name) : (NBTTagList)this.tagMap.get(name);
		}
		catch (ClassCastException e)
		{
			throw e;
		}
	}

	/**
	 * Retrieves a boolean value using the specified key, or false if no such key was stored. This uses the getByte
	 * method.
	 */
	public boolean getBoolean(String name)
	{
		return this.getByte(name) != 0;
	}

	/**
	 * Remove the specified tag.
	 */
	public void removeTag(String name)
	{
		this.tagMap.remove(name);
	}

	@Override
	public String toString()
	{
		String s = this.getName() + ":[";
		String s1;

		for (Iterator<String> iter = this.tagMap.keySet().iterator(); iter.hasNext(); s = s + s1 + ":" + this.tagMap.get(s1) + ",")
		{
			s1 = iter.next();
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
		NBTTagCompound finalCompound = new NBTTagCompound(this.getName());
		Iterator<String> iter = this.tagMap.keySet().iterator();

		while (iter.hasNext())
		{
			String s = iter.next();
			finalCompound.setTag(s, this.tagMap.get(s).copy());
		}

		return finalCompound;
	}

	@Override
	public boolean equals(Object other)
	{
		if (super.equals(other))
		{
			NBTTagCompound tempOther = (NBTTagCompound)other;
			return this.tagMap.entrySet().equals(tempOther.tagMap.entrySet());
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
	static Map<String, NBTBase> getTagMap(NBTTagCompound compound)
	{
		return compound.tagMap;
	}
}
