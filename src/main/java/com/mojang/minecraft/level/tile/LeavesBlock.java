package com.mojang.minecraft.level.tile;

public final class LeavesBlock extends LeavesBaseBlock {

    protected LeavesBlock(int var1, int var2) {
	super(var1, var2);
    }

    public final int getDrop() {
	return Block.SAPLING.id;
    }

    public final int getDropCount() {
	return random.nextInt(10) == 0 ? 1 : 0;
    }
}
