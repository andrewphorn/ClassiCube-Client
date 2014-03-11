package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;

public final class SpongeBlock extends Block {

    protected SpongeBlock(int id) {
        super(id);
    }

    @Override
    public final void onAdded(Level level, int x, int y, int z) {
        for (int i = x - 2; i <= x + 2; ++i) {
            for (int j = y - 2; j <= y + 2; ++j) {
                for (int k = z - 2; k <= z + 2; ++k) {
                    if (level.isWater(i, j, k)) {
                        level.setTileNoNeighborChange(i, j, k, 0);
                    }
                }
            }
        }

    }

    @Override
    public final void onRemoved(Level level, int x, int y, int z) {
        for (int i = x - 2; i <= x + 2; ++i) {
            for (int j = y - 2; j <= y + 2; ++j) {
                for (int k = z - 2; k <= z + 2; ++k) {
                    level.updateNeighborsAt(i, j, k, level.getTile(i, j, k));
                }
            }
        }

    }
}
