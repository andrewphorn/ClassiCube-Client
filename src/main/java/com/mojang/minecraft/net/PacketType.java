package com.mojang.minecraft.net;

public class PacketType {

    // Packet opcodes are assigned in order in which they are defined

    public static final PacketType[] packets = new PacketType[256];
    private static int nextOpcode;

    // ----  STANDARD PACKETS ----------------------------------------------------------------------
    public static final PacketType IDENTIFICATION = // 0
            new PacketType(Byte.TYPE, String.class, String.class, Byte.TYPE);

    public static final PacketType PING // 1 -- unused
            = new PacketType();

    public static final PacketType LEVEL_INIT // 2
            = new PacketType();

    public static final PacketType LEVEL_DATA // 3
            = new PacketType(Short.TYPE, byte[].class, Byte.TYPE);

    public static final PacketType LEVEL_FINALIZE // 4
            = new PacketType(Short.TYPE, Short.TYPE, Short.TYPE);

    public static final PacketType PLAYER_SET_BLOCK // 5
            = new PacketType(Short.TYPE, Short.TYPE, Short.TYPE, Byte.TYPE, Byte.TYPE);

    public static final PacketType BLOCK_CHANGE // 6
            = new PacketType(Short.TYPE, Short.TYPE, Short.TYPE, Byte.TYPE);

    public static final PacketType SPAWN_PLAYER // 7
            = new PacketType(Byte.TYPE, String.class, Short.TYPE, Short.TYPE,
                    Short.TYPE, Byte.TYPE, Byte.TYPE);

    public static final PacketType POSITION_ROTATION // 8
            = new PacketType(Byte.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Byte.TYPE, Byte.TYPE);

    public static final PacketType POSITION_ROTATION_UPDATE // 9
            = new PacketType(Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE);

    public static final PacketType POSITION_UPDATE // 10
            = new PacketType(Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE);

    public static final PacketType ROTATION_UPDATE // 11
            = new PacketType(Byte.TYPE, Byte.TYPE, Byte.TYPE);

    public static final PacketType DESPAWN_PLAYER // 12
            = new PacketType(Byte.TYPE);

    public static final PacketType CHAT_MESSAGE // 13
            = new PacketType(Byte.TYPE, String.class);

    public static final PacketType DISCONNECT // 14
            = new PacketType(String.class);

    public static final PacketType UPDATE_PLAYER_TYPE // 15
            = new PacketType(Byte.TYPE);

    // ----  CPE PACKETS ---------------------------------------------------------------------------
    public static final PacketType EXT_INFO // 16
            = new PacketType(String.class, Short.TYPE);

    public static final PacketType EXT_ENTRY // 17
            = new PacketType(String.class, Integer.TYPE);

    public static final PacketType CLICK_DISTANCE // 18
            = new PacketType(Short.TYPE);

    public static final PacketType CUSTOM_BLOCK_SUPPORT_LEVEL // 19
            = new PacketType(Byte.TYPE);

    public static final PacketType HOLD_THIS // 20
            = new PacketType(Byte.TYPE, Byte.TYPE);

    public static final PacketType SET_TEXT_HOTKEY // 21
            = new PacketType(String.class, String.class, Integer.TYPE, Byte.TYPE);

    public static final PacketType EXT_ADD_PLAYER_NAME // 22
            = new PacketType(Short.TYPE, String.class, String.class, String.class, Byte.TYPE);

    public static final PacketType EXT_ADD_ENTITY // 23 -- unused
            = new PacketType(Byte.TYPE, String.class, String.class);

    public static final PacketType EXT_REMOVE_PLAYER_NAME // 24
            = new PacketType(Short.TYPE);

    public static final PacketType ENV_SET_COLOR // 25
            = new PacketType(Byte.TYPE, Short.TYPE, Short.TYPE, Short.TYPE);

    public static final PacketType SELECTION_CUBOID // 26
            = new PacketType(Byte.TYPE, String.class, Short.TYPE, Short.TYPE, Short.TYPE,
                    Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE,
                    Short.TYPE);

    public static final PacketType REMOVE_SELECTION_CUBOID // 27
            = new PacketType(Byte.TYPE);

    public static final PacketType SET_BLOCK_PERMISSIONS // 28
            = new PacketType(Byte.TYPE, Byte.TYPE, Byte.TYPE);

    public static final PacketType CHANGE_MODEL // 29
            = new PacketType(Byte.TYPE, String.class);

    public static final PacketType ENV_SET_MAP_APPEARANCE // 30
            = new PacketType(String.class, Byte.TYPE, Byte.TYPE, Short.TYPE);

    public static final PacketType ENV_SET_WEATHER_TYPE // 31
            = new PacketType(Byte.TYPE);

    public static final PacketType HACK_CONTROL // 32
            = new PacketType(Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE, Short.TYPE);

    public static final PacketType EXT_ADD_ENTITY2 // 33
            = new PacketType(Byte.TYPE, String.class, String.class, Short.TYPE,
                    Short.TYPE, Short.TYPE, Byte.TYPE, Byte.TYPE);

    public int length;
    public byte opcode;
    @SuppressWarnings("rawtypes")
    public Class[] params;

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
