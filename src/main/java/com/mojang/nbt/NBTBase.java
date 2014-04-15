package com.mojang.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class NBTBase {
    public static final String[] NBTTypes = new String[]{"END", "BYTE", "SHORT", "INT", "LONG",
            "FLOAT", "DOUBLE", "BYTE[]", "STRING", "LIST", "COMPOUND", "INT[]"};

    /**
     * The UTF string key used to lookup values.
     */
    private String name;

    protected NBTBase(String name) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }
    }

    /**
     * Reads and returns a tag from the given DataInput, or the End tag if no
     * tag could be read.
     */
    public static NBTBase readNamedTag(DataInput input) throws IOException {
        byte b0 = input.readByte();

        if (b0 == 0) {
            return new NBTTagEnd();
        } else {
            String s = input.readUTF();
            NBTBase nbtbase = newTag(b0, s);

            nbtbase.load(input);
            return nbtbase;
        }
    }

    /**
     * Writes the specified tag to the given DataOutput, writing the type byte,
     * the UTF string key and then calling the tag to write its data.
     *
     * @param tag    The NBT Tag to write.
     * @param output The data output.
     */
    public static void writeNamedTag(NBTBase tag, DataOutput output) throws IOException {
        output.writeByte(tag.getId());
        if (tag.getId() != 0) {
            output.writeUTF(tag.getName());
            tag.write(output);
        }
    }

    /**
     * Creates and returns a new tag of the specified type, or null if invalid.
     */
    public static NBTBase newTag(byte typeID, String name) {
        switch (typeID) {
            case 0:
                return new NBTTagEnd();
            case 1:
                return new NBTTagByte(name);
            case 2:
                return new NBTTagShort(name);
            case 3:
                return new NBTTagInt(name);
            case 4:
                return new NBTTagLong(name);
            case 5:
                return new NBTTagFloat(name);
            case 6:
                return new NBTTagDouble(name);
            case 7:
                return new NBTTagByteArray(name);
            case 8:
                return new NBTTagString(name);
            case 9:
                return new NBTTagList(name);
            case 10:
                return new NBTTagCompound(name);
            case 11:
                return new NBTTagIntArray(name);
            default:
                return null;
        }
    }

    /**
     * Returns the string name of a tag with the specified type, or 'UNKNOWN' if
     * invalid.
     */
    public static String getTagName(byte typeID) {
        switch (typeID) {
            case 0:
                return "TAG_End";
            case 1:
                return "TAG_Byte";
            case 2:
                return "TAG_Short";
            case 3:
                return "TAG_Int";
            case 4:
                return "TAG_Long";
            case 5:
                return "TAG_Float";
            case 6:
                return "TAG_Double";
            case 7:
                return "TAG_Byte_Array";
            case 8:
                return "TAG_String";
            case 9:
                return "TAG_List";
            case 10:
                return "TAG_Compound";
            case 11:
                return "TAG_Int_Array";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes.
     *
     * @param output The output stream to write to.
     */
    abstract void write(DataOutput output) throws IOException;

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes.
     *
     * @param input The input stream to read from.
     */
    abstract void load(DataInput input) throws IOException;

    /**
     * Gets the type byte for the tag.
     *
     * @return byte
     */
    public abstract byte getId();

    /**
     * Gets the name corresponding to the tag, or an empty string if none set.
     *
     * @return String The tag's name.
     */
    public String getName() {
        return this.name == null ? "" : this.name;
    }

    /**
     * Sets the name for this tag and returns this for convenience.
     *
     * @param name The tag name.
     */
    public NBTBase setName(String name) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }

        return this;
    }

    /**
     * Creates a clone of the tag.
     */
    public abstract NBTBase copy();

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof NBTBase)) {
            return false;
        } else {
            NBTBase tempOther = (NBTBase) other;
            return this.getId() == tempOther.getId() && ((this.name != null || tempOther.name == null)
                    && (this.name == null || tempOther.name != null) && (this.name == null
                    || this.name.equals(tempOther.name)));
        }
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() ^ this.getId();
    }
}
