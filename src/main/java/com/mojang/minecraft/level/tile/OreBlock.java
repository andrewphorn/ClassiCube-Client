package com.mojang.minecraft.level.tile;

public final class OreBlock extends Block {

	public OreBlock(int var1) {
		super(var1);
	}

	@Override
	public final int getDrop() {
		return this == COAL_ORE ? SLAB.id : this == GOLD_ORE ? GOLD_BLOCK.id
				: this == IRON_ORE ? IRON_BLOCK.id : id;
	}

	@Override
	public final int getDropCount() {
		return random.nextInt(3) + 1;
	}
}
