package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;

public final class CobblestoneSlabBlock extends Block {

    private boolean doubleSlab;

    public CobblestoneSlabBlock(int id, boolean isDoubleSlab) {
        super(id);
        doubleSlab = isDoubleSlab;
        if (!isDoubleSlab) {
            setBounds(0F, 0F, 0F, 1F, 0.5F, 1F);
        }

    }

    @Override
    public final boolean canRenderSide(Level level, int x, int y, int z, int side) {
        if (this != COBBLESTONE_SLAB) {
            super.canRenderSide(level, x, y, z, side);
        }

        return side == 1
                || (super.canRenderSide(level, x, y, z, side)
                && (side == 0 || level.getTile(x, y, z) != id));
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

    // TODO
    @Override
    public final void onNeighborChange(Level level, int x, int y, int z, int side) {
        if (this == COBBLESTONE_SLAB) {
        }
    }
}
