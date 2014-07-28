package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.Setting;
import com.mojang.minecraft.level.Level;

public class LeavesBaseBlock extends Block {

    private boolean showNeighborSides = true;

    protected LeavesBaseBlock(int id) {
        super(id);
    }

    /*@Override
    public final boolean canRenderSide(Level level, int x, int y, int z, int side) {
        int var6 = level.getTile(x, y, z);
        return !(!showNeighborSides && var6 == id) && super.canRenderSide(level, x, y, z, side);
    }*/

    @Override
    public final boolean isOpaque() {
        return Minecraft.fastLeaves;
    }

    @Override
    public final boolean isSolid() {
        return Minecraft.fastLeaves;
    }
}
