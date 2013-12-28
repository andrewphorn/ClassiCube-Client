package com.mojang.minecraft.level;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.ProgressBarDisplay;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.mob.Creeper;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.mob.Pig;
import com.mojang.minecraft.mob.Sheep;
import com.mojang.minecraft.mob.Skeleton;
import com.mojang.minecraft.mob.Spider;
import com.mojang.minecraft.mob.Zombie;

public final class MobSpawner {

	public Level level;

	public MobSpawner(Level var1) {
		level = var1;
	}

	public final int spawn(int var1, Entity var2, ProgressBarDisplay var3) {
		int var4 = 0;

		for (int var5 = 0; var5 < var1; ++var5) {
			if (var3 != null) {
				var3.setProgress(var5 * 100 / (var1 - 1));
			}

			int var6 = level.random.nextInt(6);
			int var7 = level.random.nextInt(level.width);
			int var8 = (int) (Math.min(level.random.nextFloat(), level.random.nextFloat()) * level.depth);
			int var9 = level.random.nextInt(level.height);
			if (!level.isSolidTile(var7, var8, var9)
					&& level.getLiquid(var7, var8, var9) == LiquidType.notLiquid
					&& (!level.isLit(var7, var8, var9) || level.random.nextInt(5) == 0)) {
				for (int var10 = 0; var10 < 3; ++var10) {
					int var11 = var7;
					int var12 = var8;
					int var13 = var9;

					for (int var14 = 0; var14 < 3; ++var14) {
						var11 += level.random.nextInt(6) - level.random.nextInt(6);
						var12 += level.random.nextInt(1) - level.random.nextInt(1);
						var13 += level.random.nextInt(6) - level.random.nextInt(6);
						if (var11 >= 0 && var13 >= 1 && var12 >= 0 && var12 < level.depth - 2
								&& var11 < level.width && var13 < level.height
								&& level.isSolidTile(var11, var12 - 1, var13)
								&& !level.isSolidTile(var11, var12, var13)
								&& !level.isSolidTile(var11, var12 + 1, var13)) {
							float var15 = var11 + 0.5F;
							float var16 = var12 + 1.0F;
							float var17 = var13 + 0.5F;
							float var19;
							float var18;
							float var20;
							if (var2 != null) {
								var18 = var15 - var2.x;
								var19 = var16 - var2.y;
								var20 = var17 - var2.z;
								if (var18 * var18 + var19 * var19 + var20 * var20 < 256.0F) {
									continue;
								}
							} else {
								var18 = var15 - level.xSpawn;
								var19 = var16 - level.ySpawn;
								var20 = var17 - level.zSpawn;
								if (var18 * var18 + var19 * var19 + var20 * var20 < 256.0F) {
									continue;
								}
							}

							Object var21 = null;
							if (var6 == 0) {
								var21 = new Zombie(level, var15, var16, var17);
							}

							if (var6 == 1) {
								var21 = new Skeleton(level, var15, var16, var17);
							}

							if (var6 == 2) {
								var21 = new Pig(level, var15, var16, var17);
							}

							if (var6 == 3) {
								var21 = new Creeper(level, var15, var16, var17);
							}

							if (var6 == 4) {
								var21 = new Spider(level, var15, var16, var17);
							}

							if (var6 == 5) {
								var21 = new Sheep(level, var15, var16, var17);
							}

							if (level.isFree(((Mob) var21).bb)) {
								++var4;
								level.addEntity((Entity) var21);
							}
						}
					}
				}
			}
		}

		return var4;
	}
}
