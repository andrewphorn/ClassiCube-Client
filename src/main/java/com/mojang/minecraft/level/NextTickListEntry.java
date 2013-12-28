package com.mojang.minecraft.level;

public final class NextTickListEntry {

	public int x;
	public int y;
	public int z;
	public int block;
	public int ticks;

	public NextTickListEntry(int var1, int var2, int var3, int var4) {
		x = var1;
		y = var2;
		z = var3;
		block = var4;
	}
}
