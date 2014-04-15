package com.mojang.minecraft.level.tile;

import java.util.Random;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;

public final class StillLiquidBlock extends LiquidBlock {

    protected StillLiquidBlock(int id, LiquidType liquidType) {
        super(id, liquidType);
        movingId = id - 1;
        stillId = id;
        setPhysics(false);
    }

    @Override
    public final void onNeighborChange(Level level, int x, int y, int z, int side) {
        if (side != 0) {
            LiquidType var7 = Block.blocks[side].getLiquidType();
            if (type == LiquidType.water && var7 == LiquidType.lava || var7 == LiquidType.water
                    && type == LiquidType.lava) {
                level.setTile(x, y, z, Block.STONE.id);
                return;
            }
        }

        if ((level.getTile(x - 1, y, z) == 0 || level.getTile(x + 1, y, z) == 0
                || level.getTile(x, y, z - 1) == 0 || level.getTile(x, y, z + 1) == 0
                || level.getTile(x, y - 1, z) == 0)) {
            level.setTileNoUpdate(x, y, z, movingId);
            level.addToTickNextTick(x, y, z, movingId);
        }

    }

    @Override
    public final void update(Level level, int x, int y, int z, Random rand) {
    }
}
