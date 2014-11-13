package com.mojang.minecraft.level;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import com.mojang.util.LogUtil;
import com.mojang.nbt.CompressedStreamTools;
import com.mojang.nbt.NBTTagCompound;
import com.mojang.minecraft.player.Player;

public class LevelLoader {

    // Used for received map streams from servers
    public static byte[] decompress(InputStream input) throws IOException {
        try (DataInputStream stream = new DataInputStream(new GZIPInputStream(input))) {
            byte[] blockArray = new byte[stream.readInt()];
            stream.readFully(blockArray);
            return blockArray;
        }
    }

    public Level load(File fullFilePath, Player player) throws FileNotFoundException, IOException {
        LogUtil.logInfo("Loading level " + fullFilePath.getAbsolutePath());
        NBTTagCompound tc = CompressedStreamTools.readCompressed(new FileInputStream(fullFilePath));

        Level newLevel = new Level();
        byte FormatVersion = tc.getByte("FormatVersion");

        String Name = tc.getString("Name");
        byte[] UUID = tc.getByteArray("UUID");
        short X = tc.getShort("X");
        short Y = tc.getShort("Y");
        short Z = tc.getShort("Z");

        byte[] blocks = tc.getByteArray("BlockArray");

        newLevel.width = X;
        newLevel.length = Z;
        newLevel.height = Y;
        newLevel.blocks = blocks;

        NBTTagCompound spawn = tc.getCompoundTag("Spawn");

        short x = spawn.getShort("X");
        short y = spawn.getShort("Y");
        short z = spawn.getShort("Z");
        short r = spawn.getByte("H");
        short l = spawn.getByte("P");
        newLevel.desiredSpawn = new short[]{x, y, z, r, l};

        boolean debug = false;
        if (debug) {
            LogUtil.logInfo("FormatVersion=" + FormatVersion);
            LogUtil.logInfo("Name=" + Name);
            LogUtil.logInfo("UUID=byte[" + UUID.length + "]");
            LogUtil.logInfo("X=" + X);
            LogUtil.logInfo("Y=" + Y);
            LogUtil.logInfo("Z=" + Z);
            LogUtil.logInfo("blocks=byte[" + blocks.length + "]");
        }
        return newLevel;
    }
}
