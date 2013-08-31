package com.mojang.minecraft.render;

import com.mojang.minecraft.level.tile.Block;

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
}