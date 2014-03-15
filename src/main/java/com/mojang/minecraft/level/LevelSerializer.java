package com.mojang.minecraft.level;

import com.mojang.minecraft.LogUtil;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.nbt.CompressedStreamTools;
import com.mojang.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class LevelSerializer {

    Level level;
    String EXT = ".cw";

    public LevelSerializer(Level level) {
        this.level = level;
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

    void save(File fullFilePath) throws Exception {
        LogUtil.logInfo("Saving level " + fullFilePath.getAbsolutePath());
        if (level == null) {
            throw new Exception("level");
        }

        NBTTagCompound master = new NBTTagCompound("ClassicWorld");
        master.setByte("FormatVersion", (byte) 1);
        master.setString("Name", "SinglePlayerMap");
        master.setByteArray("UUID", asByteArray(UUID.randomUUID()));
        master.setShort("X", (short) level.width);
        master.setShort("Y", (short) level.height);
        master.setShort("Z", (short) level.length);
        master.setByteArray("BlockArray", level.blocks);

        NBTTagCompound createdBy = new NBTTagCompound("CreatedBy");
        createdBy.setString("Service", "ClassiCube");
        createdBy.setString("Username", "ClassiCube User");

        NBTTagCompound spawn = new NBTTagCompound("Spawn");
        spawn.setShort("X", (short) level.player.x);
        spawn.setShort("Y", (short) level.player.y);
        spawn.setShort("Z", (short) level.player.z);
        spawn.setByte("H", (byte) level.player.xRot);
        spawn.setByte("P", (byte) level.player.yRot);

        master.setCompoundTag("CreatedBy", createdBy);
        master.setCompoundTag("Spawn", spawn);

        String fileName = fullFilePath
                + (fullFilePath.getAbsolutePath().endsWith(EXT) ? "" : EXT);
        try (FileOutputStream fs = new FileOutputStream(new File(fileName))) {
            CompressedStreamTools.writeCompressed(master, fs);
        }
    }

    public void saveMap(File file) throws Exception {
        save(file);
    }

    public void saveMap(String Name) throws Exception {
        save(new File(Minecraft.getMinecraftDirectory(), Name));
    }
}
