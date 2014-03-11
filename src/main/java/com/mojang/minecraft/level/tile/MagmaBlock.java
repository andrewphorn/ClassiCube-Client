package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.level.Level;

public final class MagmaBlock extends Block {

    protected MagmaBlock(int id) {
        super(id);
    }

    @Override
    protected final ColorCache getBrightness(Level level, int x, int y, int z) {
        return new ColorCache(255F / 255F, 255F / 255F, 255F / 255F);
    }
}
