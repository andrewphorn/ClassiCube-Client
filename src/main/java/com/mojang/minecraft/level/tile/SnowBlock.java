package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.physics.AABB;

public final class SnowBlock extends Block {

    int id;

    public SnowBlock(int id) {
        super(id);
        setBounds(0F, 0F, 0F, 1F, 0.20F, 1F);
    }

    @Override
    public final boolean canRenderSide(Level level, int x, int y, int z, int side) {
        if (this != SNOW) {
            super.canRenderSide(level, x, y, z, side);
        }

        return side == 1 || (super.canRenderSide(level, x, y, z, side)
                && (side == 0 || level.getTile(x, y, z) != id));
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
