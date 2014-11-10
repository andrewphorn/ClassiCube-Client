package com.mojang.minecraft.mob;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.item.Item;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.render.texture.Textures;

public class Pig extends QuadrupedMob {

    public Pig(Level level, float posX, float posY, float posZ) {
        super(level, "pig", posX, posY, posZ);
        heightOffset = 1.72F;
        textureName = Textures.MOB_PIG;
    }

    @Override
    public void die(Entity killedBy) {
        if (killedBy != null) {
            killedBy.awardKillScore(this, 10);
        }

        int var2 = (int) (Math.random() + Math.random() + 1D);

        for (int var3 = 0; var3 < var2; ++var3) {
            level.addEntity(new Item(level, x, y, z, Block.BROWN_MUSHROOM.id));
        }

        super.die(killedBy);
    }
}
