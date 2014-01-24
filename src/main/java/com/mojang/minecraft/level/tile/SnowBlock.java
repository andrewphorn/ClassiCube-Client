package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.phys.AABB;

public final class SnowBlock extends Block {

	int id;

	public SnowBlock(int var1) {
		super(var1);
		id = var1;
		setBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.20F, 1.0F);
	}

	@Override
	public final boolean canRenderSide(Level level, int x, int y, int z, int side) {
		if (this != SNOW) {
			super.canRenderSide(level, x, y, z, side);
		}

		return side == 1 ? true : !super.canRenderSide(level, x, y, z, side) ? false
				: side == 0 ? true : level.getTile(x, y, z) != id;
	}

	@Override
	public AABB getCollisionBox(int x, int y, int z) {
		return null;
	}

	@Override
	public final int getDrop() {
		return SNOW.id;
	}

	@Override
	public final int getTextureId(int texture) {
		return textureId;
	}

	@Override
	public final boolean isCube() {
		return false;
	}

	@Override
	public final boolean isOpaque() {
		return true;
	}

	@Override
	public final boolean isSolid() {
		return false;
	}
}
