package com.mojang.minecraft.net;

public class PacketType {
	public static final PacketType[] packets = new PacketType[256];

	public static final PacketType IDENTIFICATION = new PacketType(new Class[] { Byte.TYPE,
			String.class, String.class, Byte.TYPE });

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
	public int length;

	private static int nextOpcode;
	public byte opcode;
	@SuppressWarnings("rawtypes")
	public Class[] params;
	public String extName = "";
	public int Version = 1;
	static {
		new PacketType(new Class[0]);

		LEVEL_INIT = new PacketType(new Class[0]);
		LEVEL_DATA = new PacketType(new Class[] { Short.TYPE, byte[].class, Byte.TYPE });
		LEVEL_FINALIZE = new PacketType(new Class[] { Short.TYPE, Short.TYPE, Short.TYPE });
		PLAYER_SET_BLOCK = new PacketType(new Class[] { Short.TYPE, Short.TYPE, Short.TYPE,
				Byte.TYPE, Byte.TYPE });
		BLOCK_CHANGE = new PacketType(new Class[] { Short.TYPE, Short.TYPE, Short.TYPE, Byte.TYPE });
		SPAWN_PLAYER = new PacketType(new Class[] { Byte.TYPE, String.class, Short.TYPE,
				Short.TYPE, Short.TYPE, Byte.TYPE, Byte.TYPE });
		POSITION_ROTATION = new PacketType(new Class[] { Byte.TYPE, Short.TYPE, Short.TYPE,
				Short.TYPE, Byte.TYPE, Byte.TYPE });
		POSITION_ROTATION_UPDATE = new PacketType(new Class[] { Byte.TYPE, Byte.TYPE, Byte.TYPE,
				Byte.TYPE, Byte.TYPE, Byte.TYPE });
		POSITION_UPDATE = new PacketType(new Class[] { Byte.TYPE, Byte.TYPE, Byte.TYPE, Byte.TYPE });
		ROTATION_UPDATE = new PacketType(new Class[] { Byte.TYPE, Byte.TYPE, Byte.TYPE });
		DESPAWN_PLAYER = new PacketType(new Class[] { Byte.TYPE });
		CHAT_MESSAGE = new PacketType(new Class[] { Byte.TYPE, String.class });
		DISCONNECT = new PacketType(new Class[] { String.class });
		UPDATE_PLAYER_TYPE = new PacketType(new Class[] { Byte.TYPE });

		// --------------------------------------------------------------
		EXT_INFO = new PacketType(new Class[] { String.class, Short.TYPE });
		EXT_ENTRY = new PacketType(new Class[] { String.class, Integer.TYPE });
		CLICK_DISTANCE = new PacketType(new Class[] { Short.TYPE });
		CUSTOM_BLOCK_SUPPORT_LEVEL = new PacketType(new Class[] { Byte.TYPE });
		HOLDTHIS = new PacketType(new Class[] { Byte.TYPE, Byte.TYPE });
		SET_TEXT_HOTKEY = new PacketType(new Class[] { String.class, String.class, Integer.TYPE,
				Byte.TYPE });
		EXT_ADD_PLAYER_NAME = new PacketType(new Class[] { Short.TYPE, String.class, String.class,
				String.class, Byte.TYPE });
		EXT_ADD_ENTITY = new PacketType(new Class[] { Byte.TYPE, String.class, String.class });
		EXT_REMOVE_PLAYER_NAME = new PacketType(new Class[] { Short.TYPE });
		ENV_SET_COLOR = new PacketType(
				new Class[] { Byte.TYPE, Short.TYPE, Short.TYPE, Short.TYPE });
		SELECTION_CUBOID = new PacketType(new Class[] { Byte.TYPE, String.class, Short.TYPE,
				Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE, Short.TYPE,
				Short.TYPE, Short.TYPE });
		REMOVE_SELECTION_CUBOID = new PacketType(new Class[] { Byte.TYPE });
		SET_BLOCK_PERMISSIONS = new PacketType(new Class[] { Byte.TYPE, Byte.TYPE, Integer.TYPE });
		CHANGE_MODEL = new PacketType(new Class[] { Byte.TYPE, String.class });
		ENV_SET_MAP_APPEARANCE = new PacketType(new Class[] { String.class, Byte.TYPE, Byte.TYPE,
				Short.TYPE });

		// set names
		EXT_INFO.extName = "ExtInfo";
		EXT_ENTRY.extName = "ExtEntry";
		CLICK_DISTANCE.extName = "SetClickDistance";
		CUSTOM_BLOCK_SUPPORT_LEVEL.extName = "CustomBlocks";
		HOLDTHIS.extName = "HoldThis";
		SET_TEXT_HOTKEY.extName = "SetTextHotKey";
		EXT_ADD_PLAYER_NAME.extName = "ExtAddPlayerName";
		EXT_ADD_ENTITY.extName = "ExtAddEntity";
		EXT_REMOVE_PLAYER_NAME.extName = "ExtRemovePlayerName";
		ENV_SET_COLOR.extName = "EnvSetColor";
		SELECTION_CUBOID.extName = "SelectionCuboid";
		REMOVE_SELECTION_CUBOID.extName = "RemoveSelectionCuboid";
		SET_BLOCK_PERMISSIONS.extName = "SetBlockPermissions";
		CHANGE_MODEL.extName = "ChangeModel";
		ENV_SET_MAP_APPEARANCE.extName = "EnvSetMapAppearance";

		nextOpcode = 0;
	}

	@SuppressWarnings("rawtypes")
	private PacketType(Class... classes) {
		opcode = (byte) (nextOpcode++);
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
