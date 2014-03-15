package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;

import java.util.Random;

public final class MushroomBlock extends FlowerBlock {

    protected MushroomBlock(int id) {
        super(id);
        float offset = 0.2F;
        setBounds(0.5F - offset, 0F, 0.5F - offset, offset + 0.5F, offset * 2F, offset + 0.5F);
    }

    @Override
    public final void update(Level level, int x, int y, int z, Random rand) {
        int var6 = level.getTile(x, y - 1, z);
        if (level.isLit(x, y, z) || var6 != STONE.id && var6 != GRAVEL.id && var6 != COBLESTONE.id) {
            level.setTile(x, y, z, 0);
        }

    }
}
