package com.mojang.minecraft.level;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import com.mojang.minecraft.LogUtil;
import com.mojang.minecraft.nbt.CompressedStreamTools;
import com.mojang.minecraft.nbt.NBTTagCompound;
import com.mojang.minecraft.player.Player;

public class LevelLoader {

    // Used for recieved map streams from servers
    public static byte[] decompress(InputStream var0) {
        try {
            DataInputStream var3;
            byte[] var1 = new byte[(var3 = new DataInputStream(new GZIPInputStream(var0)))
                    .readInt()];
            var3.readFully(var1);
            var3.close();
            return var1;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    Level level;

    public LevelLoader() {
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
        newLevel.desiredSpawn = new short[] { x, y, z, r, l };

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
