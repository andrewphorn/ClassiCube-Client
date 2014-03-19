package com.mojang.minecraft.net;

public class PacketType {
    public static final PacketType[] packets = new PacketType[256];

    public static final PacketType IDENTIFICATION = new PacketType(Byte.TYPE,
            String.class, String.class, Byte.TYPE);

    public static final PacketType LEVEL_INIT;
    public static final PacketType LEVEL_DATA;
    public static final PacketType LEVEL_FINALIZE;
    public static final PacketType PLAYER_SET_BLOCK;
    public static final PacketType BLOCK_CHANGE;
    public static final PacketType SPAWN_PLAYER;
    public static final PacketType POSITION_ROTATION;
    public static final PacketType POSITION_ROTATION_UPDATE;
    public static final PacketType POSITION_UPDATE;
    public static final PacketType ROTATION_UPDATE;
    public static final PacketType DESPAWN_PLAYER;
    public static final PacketType CHAT_MESSAGE;
    public static final PacketType DISCONNECT;
    public static final PacketType UPDATE_PLAYER_TYPE;
    // ------------------------------------------------
    public static final PacketType EXT_INFO; // 16
    public static final PacketType EXT_ENTRY; // 17
    public static final PacketType CLICK_DISTANCE; // 18
    public static final PacketType CUSTOM_BLOCK_SUPPORT_LEVEL; // 19
    public static final PacketType HOLDTHIS; // 20
    public static final PacketType SET_TEXT_HOTKEY; // 21
    public static final PacketType EXT_ADD_PLAYER_NAME; // 22
    public static final PacketType EXT_ADD_ENTITY; // 23
    public static final PacketType EXT_REMOVE_PLAYER_NAME; // 24
    public static final PacketType ENV_SET_COLOR; // 25
    public static final PacketType SELECTION_CUBOID; // 26
    public static final PacketType REMOVE_SELECTION_CUBOID; // 27
    public static final PacketType SET_BLOCK_PERMISSIONS; // 28
    public static final PacketType CHANGE_MODEL; // 29
    public static final PacketType ENV_SET_MAP_APPEARANCE; // 30
    public static final PacketType ENV_SET_WEATHER_TYPE; // 31
    public static final PacketType MESSAGE_TYPES; // [Placeholder]
    public int length;

    private static int nextOpcode;
    public byte opcode;
    @SuppressWarnings("rawtypes")
    public Class[] params;
    public String extName = "";
    public int Version = 1;
    static {
        new PacketType();

        LEVEL_INIT = new PacketType();
        LEVEL_DATA = new PacketType(Short.TYPE, byte[].class, Byte.TYPE);
        LEVEL_FINALIZE = new PacketType(Short.TYPE, Short.TYPE, Short.TYPE);
        PLAYER_SET_BLOCK = new PacketType(Short.TYPE, Short.TYPE, Short.TYPE,
                Byte.TYPE, Byte.TYPE);
        BLOCK_CHANGE = new PacketType(Short.TYPE, Short.TYPE, Short.TYPE, Byte.TYPE);
        SPAWN_PLAYER = new PacketType(Byte.TYPE, String.class, Short.TYPE,
                Short.TYPE, Short.TYPE, Byte.TYPE, Byte.TYPE);
        POSITION_ROTATION = new PacketType(Byte.TYPE, Short.TYPE, Short.TYPE,
                Short.TYPE, Byte.TYPE, Byte.TYPE);
        POSITION_ROTATION_UPDATE = new PacketType(Byte.TYPE, Byte.TYPE, Byte.TYPE,
                Byte.TYPE, Byte.TYPE, Byte.TYPE);
        POSITION_UPDATE = new PacketType(Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE);
        ROTATION_UPDATE = new PacketType(Byte.TYPE, Byte.TYPE, Byte.TYPE);
        DESPAWN_PLAYER = new PacketType(Byte.TYPE);
        CHAT_MESSAGE = new PacketType(Byte.TYPE, String.class);
        DISCONNECT = new PacketType(String.class);
        UPDATE_PLAYER_TYPE = new PacketType(Byte.TYPE);

        // --------------------------------------------------------------
        EXT_INFO = new PacketType(String.class, Short.TYPE);
        EXT_ENTRY = new PacketType(String.class, Integer.TYPE);
        CLICK_DISTANCE = new PacketType(Short.TYPE);
        CUSTOM_BLOCK_SUPPORT_LEVEL = new PacketType(Byte.TYPE);
        HOLDTHIS = new PacketType(Byte.TYPE, Byte.TYPE);
        SET_TEXT_HOTKEY = new PacketType(String.class, String.class, Integer.TYPE,
                Byte.TYPE);
        EXT_ADD_PLAYER_NAME = new PacketType(Short.TYPE, String.class, String.class,
                String.class, Byte.TYPE);
        EXT_ADD_ENTITY = new PacketType(Byte.TYPE, String.class, String.class);
        EXT_REMOVE_PLAYER_NAME = new PacketType(Short.TYPE);
        ENV_SET_COLOR = new PacketType(
                Byte.TYPE, Short.TYPE, Short.TYPE, Short.TYPE);
        SELECTION_CUBOID = new PacketType(Byte.TYPE, String.class, Short.TYPE,
                Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE,
                Short.TYPE, Short.TYPE);
        REMOVE_SELECTION_CUBOID = new PacketType(Byte.TYPE);
        SET_BLOCK_PERMISSIONS = new PacketType(Byte.TYPE, Byte.TYPE, Byte.TYPE);
        CHANGE_MODEL = new PacketType(Byte.TYPE, String.class);
        ENV_SET_MAP_APPEARANCE = new PacketType(String.class, Byte.TYPE, Byte.TYPE,
                Short.TYPE);
        ENV_SET_WEATHER_TYPE = new PacketType(Byte.TYPE);
        MESSAGE_TYPES = new PacketType(Byte.TYPE);

        // set names
        EXT_INFO.extName = "ExtInfo";
        EXT_ENTRY.extName = "ExtEntry";
        CLICK_DISTANCE.extName = "ClickDistance";
        CUSTOM_BLOCK_SUPPORT_LEVEL.extName = "CustomBlocks";
        HOLDTHIS.extName = "HeldBlock";
        SET_TEXT_HOTKEY.extName = "TextHotKey";
        EXT_ADD_PLAYER_NAME.extName = "ExtPlayerList"; // 3 packets in here
        ENV_SET_COLOR.extName = "EnvColors";
        SELECTION_CUBOID.extName = "SelectionCuboid";
        SET_BLOCK_PERMISSIONS.extName = "BlockPermissions";
        CHANGE_MODEL.extName = "ChangeModel";
        ENV_SET_MAP_APPEARANCE.extName = "EnvMapAppearance";
        ENV_SET_WEATHER_TYPE.extName = "EnvWeatherType";
        MESSAGE_TYPES.extName = "MessageTypes";
        nextOpcode = 0;
    }

    @SuppressWarnings("rawtypes")
    private PacketType(Class... classes) {
        opcode = (byte) nextOpcode++;
        packets[opcode] = this;
        params = new Class[classes.length];

        int length = 0;

        for (int classNumber = 0; classNumber < classes.length; classNumber++) {
            Class class_ = classes[classNumber];

            params[classNumber] = class_;

            if (class_ == Long.TYPE) {
                length += 8;
            } else if (class_ == Integer.TYPE) {
                length += 4;
            } else if (class_ == Short.TYPE) {
                length += 2;
            } else if (class_ == Byte.TYPE) {
                ++length;
            } else if (class_ == Float.TYPE) {
                length += 4;
            } else if (class_ == Double.TYPE) {
                length += 8;
            } else if (class_ == byte[].class) {
                length += 1024;
            } else if (class_ == String.class) {
                length += 64;
            }
        }

        this.length = length;
    }
}
