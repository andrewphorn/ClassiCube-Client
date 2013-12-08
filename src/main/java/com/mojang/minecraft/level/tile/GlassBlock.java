package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;

public class GlassBlock extends Block {

	private boolean showNeighborSides = false;

	protected GlassBlock(int var1) {
		super(var1);
	}

	public final boolean canRenderSide(Level level, int x, int y, int z, int side) {
		int var6 = level.getTile(x, y, z);
		return !this.showNeighborSides && var6 == this.id ? false : super.canRenderSide(level, x,
				y, z, side);
	}

	public final boolean isOpaque() {
		return false;
	}

	public final boolean isSolid() {
		return false;
	}
}
