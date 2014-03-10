package com.mojang.minecraft.mob;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.item.Item;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;

public class Pig extends QuadrupedMob {

	public static final long serialVersionUID = 0L;

	public Pig(Level var1, float var2, float var3, float var4) {
		super(var1, var2, var3, var4);
		heightOffset = 1.72F;
		modelName = "pig";
		textureName = "/mob/pig.png";
	}

	@Override
	public void die(Entity var1) {
		if (var1 != null) {
			var1.awardKillScore(this, 10);
		}

		int var2 = (int) (Math.random() + Math.random() + 1D);

		for (int var3 = 0; var3 < var2; ++var3) {
			level.addEntity(new Item(level, x, y, z, Block.BROWN_MUSHROOM.id));
		}

		super.die(var1);
	}
}
