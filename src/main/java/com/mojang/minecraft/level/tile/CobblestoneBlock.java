package com.mojang.minecraft.level.tile;

public final class CobblestoneBlock extends Block {

	public CobblestoneBlock(int var1) {
		super(var1);
	}

	public final int getDrop() {
		return cobblestone.id;
	}
}
