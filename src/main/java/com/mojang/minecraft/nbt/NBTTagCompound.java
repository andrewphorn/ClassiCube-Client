package com.mojang.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NBTTagCompound extends NBTBase {
    /**
     * The key-value pairs for the tag. Each key is a UTF string, each value is a tag.
     */
    private Map<String, NBTBase> tagMap = new HashMap<>();

    public NBTTagCompound() {
        super("");
    }

    public NBTTagCompound(String name) {
        super(name);
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes.
     * @param output The data output
     */
    @Override
    void write(DataOutput output) throws IOException {
        for (NBTBase nbtbase : this.tagMap.values()) {
            NBTBase.writeNamedTag(nbtbase, output);
        }
        // Write the end
        output.writeByte(0);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes.
     * @param input The data input.
     */
    @Override
    void load(DataInput input) throws IOException {
        this.tagMap.clear();
        NBTBase nbtbase;

        while ((nbtbase = NBTBase.readNamedTag(input)).getId() != 0) {
            this.tagMap.put(nbtbase.getName(), nbtbase);
        }
    }

    /**
     * Returns all the values in the tagMap HashMap.
     * @return Collection
     */
    public Collection<NBTBase> getTags() {
        return this.tagMap.values();
    }

    /**
     * Gets the type byte for the tag.
     */
    @Override
    public byte getId() {
        return (byte) 10;
    }

    /**
     * Stores the given tag into the map with the given string key. This is mostly used to store
     * tag lists.
     * @param name The tag name.
     * @param tag The tag to be set.
     */
    public void setTag(String name, NBTBase tag) {
        this.tagMap.put(name, tag.setName(name));
    }

    /**
     * Stores a new NBTTagByte with the given byte value into the map with the given string key.
     * @param name The tag name.
     * @param theByte The byte value to be set.
     */
    public void setByte(String name, byte theByte) {
        this.tagMap.put(name, new NBTTagByte(name, theByte));
    }

    /**
     * Stores a new NBTTagShort with the given short value into the map with the given string key.
     * @param name The tag name.
     * @param theShort The short value to be set.
     */
    public void setShort(String name, short theShort) {
        this.tagMap.put(name, new NBTTagShort(name, theShort));
    }

    /**
     * Stores a new NBTTagInt with the given integer value into the map with the given string key.
     * @param name The tag name.
     * @param theInt The int value to be set.
     */
    public void setInteger(String name, int theInt) {
        this.tagMap.put(name, new NBTTagInt(name, theInt));
    }

    /**
     * Stores a new NBTTagLong with the given long value into the map with the given string key.
     * @param name The tag name.
     * @param theLong The long value to be set.
     */
    public void setLong(String name, long theLong) {
        this.tagMap.put(name, new NBTTagLong(name, theLong));
    }

    /**
     * Stores a new NBTTagFloat with the given float value into the map with the given string key.
     * @param name The tag name.
     * @param theFloat The float value to be set.
     */
    public void setFloat(String name, float theFloat) {
        this.tagMap.put(name, new NBTTagFloat(name, theFloat));
    }

    /**
     * Stores a new NBTTagDouble with the given double value into the map with the given string key.
     * @param name The tag name.
     * @param theDouble The double value to be set.
     */
    public void setDouble(String name, double theDouble) {
        this.tagMap.put(name, new NBTTagDouble(name, theDouble));
    }

    /**
     * Stores a new NBTTagString with the given string value into the map with the given string key.
     * @param name The tag name.
     * @param theString The string value to be set.
     */
    public void setString(String name, String theString) {
        this.tagMap.put(name, new NBTTagString(name, theString));
    }

    /**
     * Stores a new NBTTagByteArray with the given array as data into the map with the given
     * string key.
     * @param name The tag name.
     * @param theByteArray The ByteArray value to be set.
     */
    public void setByteArray(String name, byte[] theByteArray) {
        this.tagMap.put(name, new NBTTagByteArray(name, theByteArray));
    }

    /**
     * Stores a new NBTTagIntArray with the given array as data into the map with the given
     * string key.
     * @param name The tag name.
     * @param theIntArray The IntArray value to be set.
     */
    public void setIntArray(String name, int[] theIntArray) {
        this.tagMap.put(name, new NBTTagIntArray(name, theIntArray));
    }

    /**
     * Stores the given NBTTagCompound into the map with the given string key.
     * @param name The tag name.
     * @param theCompound The compound value to be set.
     */
    public void setCompoundTag(String name, NBTTagCompound theCompound) {
        this.tagMap.put(name, theCompound.setName(name));
    }

    /**
     * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for false,
     * using the given string key.
     * @param name The tag name.
     * @param theBool The boolean value to be set.
     */
    public void setBoolean(String name, boolean theBool) {
        this.setByte(name, (byte) (theBool ? 1 : 0));
    }

    /**
     * Gets a generic tag with the specified name.
     * @param name The tag name.
     * @return NBTBase The NBT Tag.
     */
    public NBTBase getTag(String name) {
        return this.tagMap.get(name);
    }

    /**
     * Returns whether the given string has been previously stored as a key in the map.
     * @param name The tag name.
     * @return boolean
     */
    public boolean hasKey(String name) {
        return this.tagMap.containsKey(name);
    }

    /**
     * Retrieves a byte value using the specified key, or 0 if no such key was stored.
     * @param name The tag name.
     * @return byte The byte stored.
     */
    public byte getByte(String name) {
        return !this.tagMap.containsKey(name) ? 0 : ((NBTTagByte) this.tagMap.get(name)).data;
    }

    /**
     * Retrieves a short value using the specified key, or 0 if no such key was stored.
     * @param name The tag name.
     * @return short The short stored.
     */
    public short getShort(String name) {
        return !this.tagMap.containsKey(name) ? 0 : ((NBTTagShort) this.tagMap.get(name)).data;
    }

    /**
     * Retrieves an integer value using the specified key, or 0 if no such key was stored.
     * @param name The tag name.
     * @return int The integer stored.
     */
    public int getInteger(String name) {
        return !this.tagMap.containsKey(name) ? 0 : ((NBTTagInt) this.tagMap.get(name)).data;
    }

    /**
     * Retrieves a long value using the specified key, or 0 if no such key was stored.
     * @param name The tag name.
     * @return long The long value.
     */
    public long getLong(String name) {
        return !this.tagMap.containsKey(name) ? 0L : ((NBTTagLong) this.tagMap.get(name)).data;
    }

    /**
     * Retrieves a float value using the specified key, or 0 if no such key was stored.
     * @param name The tag name.
     * @return float The float stored.
     */
    public float getFloat(String name) {
        return !this.tagMap.containsKey(name) ? 0F : ((NBTTagFloat) this.tagMap.get(name)).data;
    }

    /**
     * Retrieves a double value using the specified key, or 0 if no such key was stored.
     * @param name The tag name.
     * @return double The double stored.
     */
    public double getDouble(String name) {
        return !this.tagMap.containsKey(name) ? 0D : ((NBTTagDouble) this.tagMap.get(name)).data;
    }

    /**
     * Retrieves a string value using the specified key, or an empty string if no such key
     * was stored.
     * @param name The tag name.
     * @return String The string stored.
     */
    public String getString(String name) {
        return !this.tagMap.containsKey(name) ? "" : ((NBTTagString) this.tagMap.get(name)).data;
    }

    /**
     * Retrieves a byte array using the specified key, or a zero-length array if no such key
     * was stored.
     * @param name The tag name.
     * @return byte The byte array stored.
     */
    public byte[] getByteArray(String name) {
        return !this.tagMap.containsKey(name) ? new byte[0] : ((NBTTagByteArray) this.tagMap.get(name)).byteArray;
    }

    /**
     * Retrieves an int array using the specified key, or a zero-length array if no such key
     * was stored.
     * @param name The tag name.
     * @return int[] The int array stored.
     */
    public int[] getIntArray(String name) {
        return !this.tagMap.containsKey(name) ? new int[0] : ((NBTTagIntArray) this.tagMap.get(name)).intArray;
    }

    /**
     * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty NBTTagCompound
     * if no such key was stored.
     * @param name The tag name.
     * @return NBTTagCompound The compound stored.
     */
    public NBTTagCompound getCompoundTag(String name) {
        return !this.tagMap.containsKey(name) ? new NBTTagCompound(name) : (NBTTagCompound) this.tagMap.get(name);
    }

    /**
     * Retrieves a NBTTagList subtag matching the specified key, or a new empty NBTTagList if
     * no such key was stored.
     * @param name The tag name.
     * @return NBTTagList The tag list stored.
     */
    public NBTTagList getTagList(String name) {
        return !this.tagMap.containsKey(name) ? new NBTTagList(name) : (NBTTagList) this.tagMap.get(name);
    }

    /**
     * Retrieves a boolean value using the specified key, or false if no such key was stored.
     * This uses the getByte method.
     * @param name The tag name.
     * @return boolean The boolean stored.
     */
    public boolean getBoolean(String name) {
        return this.getByte(name) != 0;
    }

    /**
     * Remove the specified tag.
     * @param name The tag name to be removed.
     */
    public void removeTag(String name) {
        this.tagMap.remove(name);
    }

    /**
     * Return the tag map for this compound.
     * @param compound The compound.
     * @return Map The tag map.
     */
    static Map<String, NBTBase> getTagMap(NBTTagCompound compound) {
        return compound.tagMap;
    }

    @Override
    public String toString() {
        String s = this.getName() + ":[";
        String s1;

        for (Iterator<String> iter = this.tagMap.keySet().iterator(); iter.hasNext();
             s = s + s1 + ":" + this.tagMap.get(s1) + ",") {
            s1 = iter.next();
        }

        return s + "]";
    }

    /**
     * Return whether this compound has no tags.
     * @return boolean
     */
    public boolean hasNoTags() {
        return this.tagMap.isEmpty();
    }

    /**
     * Creates a clone of the tag.
     * @return NBTTagCompound The clone.
     */
    @Override
    public NBTBase copy() {
        NBTTagCompound finalCompound = new NBTTagCompound(this.getName());

        for (String s : this.tagMap.keySet()) {
            finalCompound.setTag(s, this.tagMap.get(s).copy());
        }

        return finalCompound;
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            NBTTagCompound tempOther = (NBTTagCompound) other;
            return this.tagMap.entrySet().equals(tempOther.tagMap.entrySet());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.tagMap.hashCode();
    }
}
