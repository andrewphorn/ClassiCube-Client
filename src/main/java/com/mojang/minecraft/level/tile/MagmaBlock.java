package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.ColorCache;

public final class MagmaBlock extends Block {

	protected MagmaBlock(int var1) {
		super(var1);
	}

	@Override
	protected final ColorCache getBrightness(Level level, int x, int y, int z) {
		return new ColorCache(255.0F / 255.0F, 255.0F / 255.0F, 255.0F / 255.0F);
	}
}
