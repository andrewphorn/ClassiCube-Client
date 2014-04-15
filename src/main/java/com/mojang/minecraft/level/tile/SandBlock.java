package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;

public final class SandBlock extends Block {

    public SandBlock(int id) {
        super(id);
    }

    private void fall(Level level, int x, int y, int z) {
        if (!Minecraft.isSinglePlayer) {
            return;
        }
        int yOffset = y;

        while (true) {
            int yCheckPos = yOffset - 1;
            int nextTile;
            LiquidType liquidType;
            if (!((nextTile = level.getTile(x, yCheckPos, z)) == 0 ? true : (liquidType = blocks[nextTile]
                    .getLiquidType()) == LiquidType.water ? true : liquidType == LiquidType.lava)
                    || yOffset <= 0) {
                if (yOffset != y) {
                    if ((nextTile = level.getTile(x, yOffset, z)) > 0
                            && blocks[nextTile].getLiquidType() != LiquidType.notLiquid) {
                        level.setTileNoUpdate(x, yOffset, z, 0);
                    }

                    level.swap(x, y, z, x, yOffset, z);
                }

                return;
            }

            --yOffset;
        }
    }

    @Override
    public final void onNeighborChange(Level level, int x, int y, int z, int unused) {
        fall(level, x, y, z);
    }

    @Override
    public final void onPlace(Level level, int x, int y, int z) {
        fall(level, x, y, z);
    }
}