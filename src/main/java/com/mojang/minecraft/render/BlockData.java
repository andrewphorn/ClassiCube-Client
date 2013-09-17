package com.mojang.minecraft.render;

import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.player.Player;

public final class BlockData {

	public int x;
	public int y;
	public int z;
	public Block block;

	public BlockData(int x, int y, int z, Block block) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.block = block;
	}
	
	public final float distanceSquared(Player var1) {
		float var2 = var1.x - (float) this.x;
		float var3 = var1.y - (float) this.y;
		float var4 = var1.z - (float) this.z;
		return var2 * var2 + var3 * var3 + var4 * var4;
	}
}