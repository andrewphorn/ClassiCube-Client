package com.oyasunadev.mcraft.client.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: Oliver Yasuna Date: 9/30/12 Time: 7:57 PM
 */

public class Constants {
	/**
	 * Just a reference to the MCraft version if needed.
	 */
	public static final String MCRAFT_VERSION = "1.0";
	/**
	 * Just a reference to the Minecraft version if needed.
	 */
	public static final String MINECRAFT_VERSION = "0.30";

	/**
	 * ClassiCube Version.
	 */
	public static final String CLASSICUBE_VERSION = "0.12";

	/**
	 * The Minecraft Classic protocol version. Default is 0x07.
	 */
	public static final byte PROTOCOL_VERSION = 0x07;
	/**
	 * The client type sent to the server to identify what client is being used.
	 * Default is 0x00.
	 */
	public static final byte CLIENT_TYPE = 0x42;

	public static final String CLIENT_NAME = "ClassiCube Client";

	// TODO Add system information
	public static final String USER_AGENT = "ClassiCube " + CLASSICUBE_VERSION + "(Minecraft "
			+ MINECRAFT_VERSION + "; Protocol " + PROTOCOL_VERSION + ")";

	public static final byte CUSTOM_BLOCK_SUPPORT_LEVEL = (byte) 1;

	public static List<ExtData> SERVER_SUPPORTED_EXTENSIONS = new ArrayList<>();
}
