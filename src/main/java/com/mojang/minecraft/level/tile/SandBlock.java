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
        int newY = y;

        while (true) {
            int below = newY - 1;
            int tile = level.getTile(x, below, z);
            LiquidType liquidType = blocks[tile].getLiquidType();
            if (!(tile == 0 || (liquidType == LiquidType.water || liquidType == LiquidType.lava)) || newY <= 0) {
                if (newY != y) {
                    if ((tile = level.getTile(x, newY, z)) > 0
                            && blocks[tile].getLiquidType() != LiquidType.notLiquid) {
                        level.setTileNoUpdate(x, newY, z, 0);
                    }

                    level.swap(x, y, z, x, newY, z);
                }

                return;
            }

            --newY;
        }
    }

    @Override
    public final void onNeighborChange(Level level, int x, int y, int z, int side) {
        fall(level, x, y, z);
    }

    @Override
    public final void onPlace(Level level, int x, int y, int z) {
        fall(level, x, y, z);
    }
}
