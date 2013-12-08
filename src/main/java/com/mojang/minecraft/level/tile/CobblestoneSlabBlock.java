package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;

public final class CobblestoneSlabBlock extends Block {

	private boolean doubleSlab;

	public CobblestoneSlabBlock(int var1, boolean var2) {
		super(var1);
		this.doubleSlab = var2;
		if (!var2) {
			this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}

	}

	public final boolean canRenderSide(Level level, int x, int y, int z, int side) {
		if (this != slabCobblestone) {
			super.canRenderSide(level, x, y, z, side);
		}

		return side == 1 ? true : (!super.canRenderSide(level, x, y, z, side) ? false
				: (side == 0 ? true : level.getTile(x, y, z) != this.id));
	}

	public final int getDrop() {
		return slabCobblestone.id;
	}

	protected final int getTextureId(int texture) {
		return 16;
	}

	public final boolean isCube() {
		return this.doubleSlab;
	}

	public final boolean isSolid() {
		return this.doubleSlab;
	}

	public final void onAdded(Level level, int x, int y, int z) {
		if (this != slabCobblestone) {
			super.onAdded(level, x, y, z);
		}

		if (level.getTile(x, y - 1, z) == slabCobblestone.id) {
			level.setTile(x, y, z, 0);
			level.setTile(x, y - 1, z, slabCobblestone.id);
		}

	}

	public final void onNeighborChange(Level var1, int var2, int var3, int var4, int var5) {
		if (this == slabCobblestone) {
			;
		}
	}
}
