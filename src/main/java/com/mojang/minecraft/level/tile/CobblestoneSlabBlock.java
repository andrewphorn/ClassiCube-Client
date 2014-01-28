package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;

public final class CobblestoneSlabBlock extends Block {

	private boolean doubleSlab;

	public CobblestoneSlabBlock(int var1, boolean var2) {
		super(var1);
		doubleSlab = var2;
		if (!var2) {
			setBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}

	}

	@Override
	public final boolean canRenderSide(Level level, int x, int y, int z, int side) {
		if (this != COBBLESTONE_SLAB) {
			super.canRenderSide(level, x, y, z, side);
		}

		return side == 1 ? true : !super.canRenderSide(level, x, y, z, side) ? false
				: side == 0 ? true : level.getTile(x, y, z) != id;
	}

	@Override
	public final int getDrop() {
		return COBBLESTONE_SLAB.id;
	}

	@Override
	public final int getTextureId(int texture) {
		return 16;
	}

	@Override
	public final boolean isCube() {
		return doubleSlab;
	}

	@Override
	public final boolean isSolid() {
		return doubleSlab;
	}

	@Override
	public final void onAdded(Level level, int x, int y, int z) {
		if (this != COBBLESTONE_SLAB) {
			super.onAdded(level, x, y, z);
		}

		if (level.getTile(x, y - 1, z) == COBBLESTONE_SLAB.id) {
			level.setTile(x, y, z, 0);
			level.setTile(x, y - 1, z, COBBLESTONE_SLAB.id);
		}

	}

	@Override
	public final void onNeighborChange(Level var1, int var2, int var3, int var4, int var5) {
		if (this == COBBLESTONE_SLAB) {
			;
		}
	}
}
