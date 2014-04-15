package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NBTTagList extends NBTBase {
    /**
     * The array list containing the tags encapsulated in this list.
     */
    private List<NBTBase> tagList = new ArrayList<>();

    /**
     * The type byte for the tags in the list - they must all be of the same
     * type.
     */
    private byte tagType;

    public NBTTagList() {
        super("");
    }

    public NBTTagList(String data) {
        super(data);
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes.
     *
     * @param output The output stream to write to.
     */
    @Override
    void write(DataOutput output) throws IOException {
        if (!this.tagList.isEmpty()) {
            this.tagType = this.tagList.get(0).getId();
        } else {
            this.tagType = 1;
        }

        output.writeByte(this.tagType);
        output.writeInt(this.tagList.size());

        for (NBTBase aTagList : this.tagList) {
            aTagList.write(output);
        }
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes.
     *
     * @param input The input stream to read from.
     */
    @Override
    void load(DataInput input) throws IOException {
        this.tagType = input.readByte();
        int i = input.readInt();
        this.tagList = new ArrayList<>();

        for (int j = 0; j < i; ++j) {
            NBTBase nbtbase = NBTBase.newTag(this.tagType, null);
            nbtbase.load(input);
            this.tagList.add(nbtbase);
        }
    }

    /**
     * Gets the type byte for the tag.
     *
     * @return byte
     */
    @Override
    public byte getId() {
        return (byte) 9;
    }

    @Override
    public String toString() {
        return "" + this.tagList.size() + " entries of type " + NBTBase.getTagName(this.tagType);
    }

    /**
     * Adds the provided tag to the end of the list. There is no check to verify
     * this tag is of the same type as any previous tag.
     */
    public void appendTag(NBTBase tag) {
        this.tagType = tag.getId();
        this.tagList.add(tag);
    }

    /**
     * Removes a tag at the given index.
     */
    public NBTBase removeTag(int index) {
        return this.tagList.remove(index);
    }

    /**
     * Retrieves the tag at the specified index from the list.
     */
    public NBTBase tagAt(int index) {
        return this.tagList.get(index);
    }

    /**
     * Returns the number of tags in the list.
     */
    public int tagCount() {
        return this.tagList.size();
    }

    /**
     * Creates a clone of the tag.
     */
    @Override
    public NBTBase copy() {
        NBTTagList finalTagList = new NBTTagList(this.getName());
        finalTagList.tagType = this.tagType;

        for (NBTBase nextTag : this.tagList) {
            NBTBase nextTagByValue = nextTag.copy();
            finalTagList.tagList.add(nextTagByValue);
        }

        return finalTagList;
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            NBTTagList tempOther = (NBTTagList) other;

            if (this.tagType == tempOther.tagType) {
                return this.tagList.equals(tempOther.tagList);
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.tagList.hashCode();
    }
}
