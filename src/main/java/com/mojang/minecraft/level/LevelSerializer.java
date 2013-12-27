package com.mojang.minecraft.level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.nbt.CompressedStreamTools;
import com.mojang.minecraft.nbt.NBTTagCompound;

public class LevelSerializer {
	String EXT = ".cw";
	Level level;

	public LevelSerializer(Level level) {
		this.level = level;
	}

	public void saveMap(String Name) throws FileNotFoundException, IOException {
		save(new File(Minecraft.getMinecraftDirectory(), Name));
	}

	public void saveMap(File file) throws FileNotFoundException, IOException {
		save(file);
	}

	void save(File fullFilePath) throws FileNotFoundException, IOException {
		System.out.println("Saving level");
		NBTTagCompound tc = new NBTTagCompound("ClassicWorld");

		tc.setByte("FormatVersion", (byte) 1);
		tc.setString("Name", "SinglePlayerMap");
		tc.setByteArray("UUID", asByteArray(UUID.randomUUID()));
		tc.setShort("X", (short) level.width);
		tc.setShort("Y", (short) level.height);
		tc.setShort("Z", (short) level.depth);
		tc.setByteArray("BlockArray", level.blocks);
		boolean debug = false;
		if (debug) {
			System.out.println(level.blocks.length);
		}

		CompressedStreamTools.writeCompressed(tc,
				new FileOutputStream(new File(fullFilePath + EXT)));
	}

	private static byte[] asByteArray(UUID uuid) {
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		byte[] buffer = new byte[16];

		for (int i = 0; i < 8; i++) {
			buffer[i] = (byte) (msb >>> 8 * (7 - i));
		}
		for (int i = 8; i < 16; i++) {
			buffer[i] = (byte) (lsb >>> 8 * (7 - i));
		}

		return buffer;

	}

	private static UUID toUUID(byte[] byteArray) {
		long msb = 0;
		long lsb = 0;
		for (int i = 0; i < 8; i++)
			msb = (msb << 8) | (byteArray[i] & 0xff);
		for (int i = 8; i < 16; i++)
			lsb = (lsb << 8) | (byteArray[i] & 0xff);
		UUID result = new UUID(msb, lsb);

		return result;
	}
}
