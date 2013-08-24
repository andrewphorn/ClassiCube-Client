package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;

import java.util.Random;

public final class SaplingBlock extends FlowerBlock {

	protected SaplingBlock(int var1, int var2) {
		super(var1, var2);
		float var3 = 0.4F;
		this.setBounds(0.5F - var3, 0.0F, 0.5F - var3, var3 + 0.5F,
				var3 * 2.0F, var3 + 0.5F);
	}

	public final void update(Level level, int x, int y, int z, Random rand) {
		if (this.id != WEB.id) {
			int var6 = level.getTile(x, y - 1, z);
			if (level.isLit(x, y, z) && (var6 == DIRT.id || var6 == GRASS.id)) {
				if (rand.nextInt(5) == 0) {
					level.setTileNoUpdate(x, y, z, 0);
					if (!level.maybeGrowTree(x, y, z)) {
						level.setTileNoUpdate(x, y, z, this.id);
					}
				}

			} else {
				level.setTile(x, y, z, 0);
			}
		}
	}
}
