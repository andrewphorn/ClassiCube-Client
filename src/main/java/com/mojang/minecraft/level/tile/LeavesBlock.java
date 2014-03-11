package com.mojang.minecraft.level.tile;

public final class LeavesBlock extends LeavesBaseBlock {

    protected LeavesBlock(int id) {
        super(id);
    }

    @Override
    public final int getDrop() {
        return Block.SAPLING.id;
    }

    @Override
    public final int getDropCount() {
        return random.nextInt(10) == 0 ? 1 : 0;
    }
}
