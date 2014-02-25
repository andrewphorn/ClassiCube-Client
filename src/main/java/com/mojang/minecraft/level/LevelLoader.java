package com.mojang.minecraft.level;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import com.mojang.minecraft.nbt.CompressedStreamTools;
import com.mojang.minecraft.nbt.NBTTagCompound;
import com.mojang.minecraft.player.Player;

public class LevelLoader {

	// Used for received map streams from servers
	public static byte[] decompress(InputStream var0) {
		try {
			DataInputStream var3;
			byte[] var1 = new byte[(var3 = new DataInputStream(new GZIPInputStream(var0)))
					.readInt()];
			var3.readFully(var1);
			var3.close();
			return var1;
		} catch (Exception var2) {
			throw new RuntimeException(var2);
		}
	}

	Level level;

	public LevelLoader() {
	}

	public Level load(File fullFilePath, Player player) throws FileNotFoundException, IOException {
		System.out.println("Loading " + fullFilePath.getAbsolutePath());
		NBTTagCompound tc = CompressedStreamTools.readCompressed(new FileInputStream(fullFilePath));

		Level level = new Level();
		byte FormatVersion;
		String Name;
		byte[] UUID;
		byte[] blocks = null;
		short X = 0;
		short Y = 0;
		short Z = 0;
		FormatVersion = tc.getByte("FormatVersion");

		Name = tc.getString("Name");
		UUID = tc.getByteArray("UUID");
		X = tc.getShort("X");
		Y = tc.getShort("Y");
		Z = tc.getShort("Z");

		blocks = tc.getByteArray("BlockArray");

		level.width = X;
		level.depth = Y;
		level.height = Z;
		level.blocks = blocks;

		NBTTagCompound spawn = tc.getCompoundTag("Spawn");

		short x = spawn.getShort("X");
		short y = spawn.getShort("Y");
		short z = spawn.getShort("Z");
		short r = spawn.getByte("H");
		short l = spawn.getByte("P");
		level.desiredSpawn = new short[] { x, y, z, r, l };

		boolean debug = false;
		if (debug) {
			System.out.println("FormatVersion=" + FormatVersion);
			System.out.println("Name=" + Name);
			System.out.println("UUID=byte[" + UUID.length + "]");
			System.out.println("X=" + X);
			System.out.println("Y=" + Y);
			System.out.println("Z=" + Z);
			System.out.println("blocks=byte[" + blocks.length + "]");
		}
		return level;
	}
}
