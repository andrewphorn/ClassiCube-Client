package com.mojang.minecraft.level;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.Entity;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.MovingObjectPosition;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.model.Vec3D;
import com.mojang.minecraft.particle.ParticleManager;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.render.LevelRenderer;
import com.mojang.minecraft.sound.AudioInfo;
import com.mojang.minecraft.sound.EntitySoundPos;
import com.mojang.minecraft.sound.LevelSoundPos;
import com.mojang.util.MathHelper;

public class Level implements Serializable {

	public static final long serialVersionUID = 0L;
	public int width;
	public int height;
	public int depth;
	public byte[] blocks;
	public String name;
	public String creator;
	public long createTime;
	public int xSpawn;
	public int ySpawn;
	public int zSpawn;
	public float rotSpawn;
	private transient ArrayList<LevelRenderer> listeners = new ArrayList<LevelRenderer>();
	private transient int[] blockers;
	public transient Random random = new Random();
	private transient int randId;
	private transient ArrayList<NextTickListEntry> tickList;
	public BlockMap blockMap;
	private boolean networkMode;
	public transient Minecraft rendererContext$5cd64a7f;
	public boolean creativeMode;
	public int cloudLevel = -1;
	public int waterLevel;
	public int skyColor;
	public int fogColor;
	public int cloudColor;
	int unprocessed;
	private int tickCount;
	public Entity player;
	public transient ParticleManager particleEngine;
	public transient Object font;
	public boolean growTrees;
	public ColorCache customShadowColour;
	public ColorCache customLightColour;
	public short[] desiredSpawn;

	public Level() {
		randId = random.nextInt();
		tickList = new ArrayList<NextTickListEntry>();
		networkMode = false;
		unprocessed = 0;
		tickCount = 0;
		growTrees = false;
	}

	public void addEntity(Entity var1) {
		blockMap.insert(var1);
		var1.setLevel(this);
	}

	public void addListener(LevelRenderer var1) {
		listeners.add(var1);
	}

	public void addToTickNextTick(int var1, int var2, int var3, int var4) {
		if (!networkMode) {
			NextTickListEntry var5 = new NextTickListEntry(var1, var2, var3, var4);
			if (var4 > 0) {
				var3 = Block.blocks[var4].getTickDelay();
				var5.ticks = var3;
			}

			tickList.add(var5);
		}
	}

	public void calcLightDepths(int var1, int var2, int var3, int var4) {
		for (int var5 = var1; var5 < var1 + var3; ++var5) {
			for (int var6 = var2; var6 < var2 + var4; ++var6) {
				int var7 = blockers[var5 + var6 * width];

				int var8;
				for (var8 = depth - 1; var8 > 0 && !isLightBlocker(var5, var8, var6); --var8) {
					;
				}

				blockers[var5 + var6 * width] = var8;
				if (var7 != var8) {
					int var9 = var7 < var8 ? var7 : var8;
					var7 = var7 > var8 ? var7 : var8;

					for (var8 = 0; var8 < listeners.size(); ++var8) {
						listeners.get(var8).queueChunks(var5 - 1, var9 - 1, var6 - 1, var5 + 1,
								var7 + 1, var6 + 1);
					}
				}
			}
		}

	}

	public MovingObjectPosition clip(Vec3D var1, Vec3D var2) {
		if (!Float.isNaN(var1.x) && !Float.isNaN(var1.y) && !Float.isNaN(var1.z)) {
			if (!Float.isNaN(var2.x) && !Float.isNaN(var2.y) && !Float.isNaN(var2.z)) {
				int var3 = (int) Math.floor(var2.x);
				int var4 = (int) Math.floor(var2.y);
				int var5 = (int) Math.floor(var2.z);
				int var6 = (int) Math.floor(var1.x);
				int var7 = (int) Math.floor(var1.y);
				int var8 = (int) Math.floor(var1.z);
				int var9 = 20;

				while (var9-- >= 0) {
					if (Float.isNaN(var1.x) || Float.isNaN(var1.y) || Float.isNaN(var1.z)) {
						return null;
					}

					if (var6 == var3 && var7 == var4 && var8 == var5) {
						return null;
					}

					float var10 = 999.0F;
					float var11 = 999.0F;
					float var12 = 999.0F;
					if (var3 > var6) {
						var10 = var6 + 1.0F;
					}

					if (var3 < var6) {
						var10 = var6;
					}

					if (var4 > var7) {
						var11 = var7 + 1.0F;
					}

					if (var4 < var7) {
						var11 = var7;
					}

					if (var5 > var8) {
						var12 = var8 + 1.0F;
					}

					if (var5 < var8) {
						var12 = var8;
					}

					float var13 = 999.0F;
					float var14 = 999.0F;
					float var15 = 999.0F;
					float var16 = var2.x - var1.x;
					float var17 = var2.y - var1.y;
					float var18 = var2.z - var1.z;
					if (var10 != 999.0F) {
						var13 = (var10 - var1.x) / var16;
					}

					if (var11 != 999.0F) {
						var14 = (var11 - var1.y) / var17;
					}

					if (var12 != 999.0F) {
						var15 = (var12 - var1.z) / var18;
					}

					byte var24;
					if (var13 < var14 && var13 < var15) {
						if (var3 > var6) {
							var24 = 4;
						} else {
							var24 = 5;
						}

						var1.x = var10;
						var1.y += var17 * var13;
						var1.z += var18 * var13;
					} else if (var14 < var15) {
						if (var4 > var7) {
							var24 = 0;
						} else {
							var24 = 1;
						}

						var1.x += var16 * var14;
						var1.y = var11;
						var1.z += var18 * var14;
					} else {
						if (var5 > var8) {
							var24 = 2;
						} else {
							var24 = 3;
						}

						var1.x += var16 * var15;
						var1.y += var17 * var15;
						var1.z = var12;
					}

					Vec3D var20;
					var6 = (int) ((var20 = new Vec3D(var1.x, var1.y, var1.z)).x = (float) Math
							.floor(var1.x));
					if (var24 == 5) {
						--var6;
						++var20.x;
					}

					var7 = (int) (var20.y = (float) Math.floor(var1.y));
					if (var24 == 1) {
						--var7;
						++var20.y;
					}

					var8 = (int) (var20.z = (float) Math.floor(var1.z));
					if (var24 == 3) {
						--var8;
						++var20.z;
					}

					int var22 = getTile(var6, var7, var8);
					Block var21 = Block.blocks[var22];
					if (var22 > 0 && var21.getLiquidType() == LiquidType.notLiquid) {
						MovingObjectPosition var23;
						if (var21.isCube()) {
							if ((var23 = var21.clip(var6, var7, var8, var1, var2)) != null) {
								return var23;
							}
						} else if ((var23 = var21.clip(var6, var7, var8, var1, var2)) != null) {
							return var23;
						}
					}
				}

				return null;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public boolean containsAnyLiquid(AABB var1) {
		int var2 = (int) var1.x0;
		int var3 = (int) var1.x1 + 1;
		int var4 = (int) var1.y0;
		int var5 = (int) var1.y1 + 1;
		int var6 = (int) var1.z0;
		int var7 = (int) var1.z1 + 1;
		if (var1.x0 < 0.0F) {
			--var2;
		}

		if (var1.y0 < 0.0F) {
			--var4;
		}

		if (var1.z0 < 0.0F) {
			--var6;
		}

		if (var2 < 0) {
			var2 = 0;
		}

		if (var4 < 0) {
			var4 = 0;
		}

		if (var6 < 0) {
			var6 = 0;
		}

		if (var3 > width) {
			var3 = width;
		}

		if (var5 > depth) {
			var5 = depth;
		}

		if (var7 > height) {
			var7 = height;
		}

		for (int var10 = var2; var10 < var3; ++var10) {
			for (var2 = var4; var2 < var5; ++var2) {
				for (int var8 = var6; var8 < var7; ++var8) {
					Block var9;
					if ((var9 = Block.blocks[getTile(var10, var2, var8)]) != null
							&& var9.getLiquidType() != LiquidType.notLiquid) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean containsBlock(AABB var1, Block var2) {
		int var3 = (int) var1.x0;
		int var4 = (int) var1.x1 + 1;
		int var5 = (int) var1.y0;
		int var6 = (int) var1.y1 + 1;
		int var7 = (int) var1.z0;
		int var8 = (int) var1.z1 + 1;
		if (var1.x0 < 0.0F) {
			--var3;
		}

		if (var1.y0 < 0.0F) {
			--var5;
		}

		if (var1.z0 < 0.0F) {
			--var7;
		}

		if (var3 < 0) {
			var3 = 0;
		}

		if (var5 < 0) {
			var5 = 0;
		}

		if (var7 < 0) {
			var7 = 0;
		}

		if (var4 > width) {
			var4 = width;
		}

		if (var6 > depth) {
			var6 = depth;
		}

		if (var8 > height) {
			var8 = height;
		}

		for (int var11 = var3; var11 < var4; ++var11) {
			for (var3 = var5; var3 < var6; ++var3) {
				for (int var9 = var7; var9 < var8; ++var9) {
					Block var10;
					if ((var10 = Block.blocks[getTile(var11, var3, var9)]) != null && var10 == var2) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean containsLiquid(AABB var1, LiquidType var2) {
		int var3 = (int) var1.x0;
		int var4 = (int) var1.x1 + 1;
		int var5 = (int) var1.y0;
		int var6 = (int) var1.y1 + 1;
		int var7 = (int) var1.z0;
		int var8 = (int) var1.z1 + 1;
		if (var1.x0 < 0.0F) {
			--var3;
		}

		if (var1.y0 < 0.0F) {
			--var5;
		}

		if (var1.z0 < 0.0F) {
			--var7;
		}

		if (var3 < 0) {
			var3 = 0;
		}

		if (var5 < 0) {
			var5 = 0;
		}

		if (var7 < 0) {
			var7 = 0;
		}

		if (var4 > width) {
			var4 = width;
		}

		if (var6 > depth) {
			var6 = depth;
		}

		if (var8 > height) {
			var8 = height;
		}

		for (int var11 = var3; var11 < var4; ++var11) {
			for (var3 = var5; var3 < var6; ++var3) {
				for (int var9 = var7; var9 < var8; ++var9) {
					Block var10;
					if ((var10 = Block.blocks[getTile(var11, var3, var9)]) != null
							&& var10.getLiquidType() == var2) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public byte[] copyBlocks() {
		return Arrays.copyOf(blocks, blocks.length);
	}

	public int countInstanceOf(Class<?> var1) {
		int var2 = 0;

		for (int var3 = 0; var3 < blockMap.all.size(); ++var3) {
			Entity var4 = blockMap.all.get(var3);
			if (var1.isAssignableFrom(var4.getClass())) {
				++var2;
			}
		}

		return var2;
	}

	public void explode(Entity var1, float var2, float var3, float var4, float var5) {
		int var6 = (int) (var2 - var5 - 1.0F);
		int var7 = (int) (var2 + var5 + 1.0F);
		int var8 = (int) (var3 - var5 - 1.0F);
		int var9 = (int) (var3 + var5 + 1.0F);
		int var10 = (int) (var4 - var5 - 1.0F);
		int var11 = (int) (var4 + var5 + 1.0F);

		int var13;
		float var15;
		float var16;
		for (int var12 = var6; var12 < var7; ++var12) {
			for (var13 = var9 - 1; var13 >= var8; --var13) {
				for (int var14 = var10; var14 < var11; ++var14) {
					var15 = var12 + 0.5F - var2;
					var16 = var13 + 0.5F - var3;
					float var17 = var14 + 0.5F - var4;
					int var19;
					if (var12 >= 0 && var13 >= 0 && var14 >= 0 && var12 < width && var13 < depth
							&& var14 < height
							&& var15 * var15 + var16 * var16 + var17 * var17 < var5 * var5
							&& (var19 = getTile(var12, var13, var14)) > 0
							&& Block.blocks[var19].canExplode()) {
						Block.blocks[var19].dropItems(this, var12, var13, var14, 0.3F);
						setTile(var12, var13, var14, 0);
						Block.blocks[var19].explode(this, var12, var13, var14);
					}
				}
			}
		}

		List<?> var18 = blockMap.getEntities(var1, var6, var8, var10, var7, var9, var11);

		for (var13 = 0; var13 < var18.size(); ++var13) {
			Entity var20;
			if ((var15 = (var20 = (Entity) var18.get(var13)).distanceTo(var2, var3, var4) / var5) <= 1.0F) {
				var16 = 1.0F - var15;
				var20.hurt(var1, (int) (var16 * 15.0F + 1.0F));
			}
		}

	}

	@Override
	public void finalize() {
	}

	public List<Entity> findEntities(Entity var1, AABB var2) {
		return blockMap.getEntities(var1, var2);
	}

	public void findSpawn() {
		if (this.desiredSpawn != null) {
			xSpawn = this.desiredSpawn[0];
			ySpawn = this.desiredSpawn[1];
			zSpawn = this.desiredSpawn[2];
			this.desiredSpawn = null;
			return;
		}
		Random var1 = new Random();
		int var2 = 0;

		int var3;
		int var4;
		int var5;
		do {
			++var2;
			var3 = var1.nextInt(width / 2) + width / 4;
			var4 = var1.nextInt(height / 2) + height / 4;
			var5 = getHighestTile(var3, var4) + 1;
			if (var2 == 10000) {
				xSpawn = var3;
				ySpawn = -100;
				zSpawn = var4;
				return;
			}
		} while (var5 <= getWaterLevel());

		xSpawn = var3;
		ySpawn = var5;
		zSpawn = var4;
	}

	public Entity findSubclassOf(Class<?> var1) {
		for (int var2 = 0; var2 < blockMap.all.size(); ++var2) {
			Entity var3 = blockMap.all.get(var2);
			if (var1.isAssignableFrom(var3.getClass())) {
				return var3;
			}
		}

		return null;
	}

	public float getBrightness(int var1, int var2, int var3) {
		return isLit(var1, var2, var3) ? 1.0F : 0.6F;
	}

	public ColorCache getBrightnessColor(int var1, int var2, int var3) {
		float a = 0.6F, b = 0.6F, c = 0.6F;
		float d = 1.0F, e = 1.0F, f = 1.0F;
		if (customShadowColour != null) {
			a = customShadowColour.R;
			b = customShadowColour.G;
			c = customShadowColour.B;
		}
		if (customLightColour != null) {
			d = customLightColour.R;
			e = customLightColour.G;
			f = customLightColour.B;
		}
		return isLit(var1, var2, var3) ? new ColorCache(d, e, f) : new ColorCache(a, b, c);
	}

	public float getCaveness(Entity var1) {
		float var2 = MathHelper.cos(-var1.yRot * 0.017453292F + 3.1415927F);
		float var3 = MathHelper.sin(-var1.yRot * 0.017453292F + 3.1415927F);
		float var4 = MathHelper.cos(-var1.xRot * 0.017453292F);
		float var5 = MathHelper.sin(-var1.xRot * 0.017453292F);
		float var6 = var1.x;
		float var7 = var1.y;
		float var21 = var1.z;
		float var8 = 1.6F;
		float var9 = 0.0F;
		float var10 = 0.0F;

		for (int var11 = 0; var11 <= 200; ++var11) {
			float var12 = ((float) var11 / (float) 200 - 0.5F) * 2.0F;
			int var13 = 0;

			while (var13 <= 200) {
				float var14 = ((float) var13 / (float) 200 - 0.5F) * var8;
				float var16 = var4 * var14 + var5;
				var14 = var4 - var5 * var14;
				float var17 = var2 * var12 + var3 * var14;
				var14 = var2 * var14 - var3 * var12;
				int var15 = 0;

				// here
				if (var15 < 10) {
					float var18 = var6 + var17 * var15 * 0.8F;
					float var19 = var7 + var16 * var15 * 0.8F;
					float var20 = var21 + var14 * var15 * 0.8F;
					if (!this.isSolid(var18, var19, var20)) {
						++var9;
						if (isLit((int) var18, (int) var19, (int) var20)) {
							++var10;
						}

						++var15;
					}
				}

				++var13;
			}
		}

		if (var9 == 0.0F) {
			return 0.0F;
		} else {
			float var22;
			if ((var22 = var10 / var9 / 0.1F) > 1.0F) {
				var22 = 1.0F;
			}

			var22 = 1.0F - var22;
			return 1.0F - var22 * var22 * var22;
		}
	}

	public float getCaveness(float var1, float var2, float var3, float var4) {
		int var5 = (int) var1;
		int var14 = (int) var2;
		int var6 = (int) var3;
		float var7 = 0.0F;
		float var8 = 0.0F;

		for (int var9 = var5 - 6; var9 <= var5 + 6; ++var9) {
			for (int var10 = var6 - 6; var10 <= var6 + 6; ++var10) {
				if (isInBounds(var9, var14, var10) && !isSolidTile(var9, var14, var10)) {
					float var11 = var9 + 0.5F - var1;

					float var12;
					float var13;
					for (var13 = (float) (Math.atan2(var12 = var10 + 0.5F - var3, var11) - var4
							* 3.1415927F / 180.0F + 1.5707963705062866D); var13 < -3.1415927F; var13 += 6.2831855F) {
						;
					}

					while (var13 >= 3.1415927F) {
						var13 -= 6.2831855F;
					}

					if (var13 < 0.0F) {
						var13 = -var13;
					}

					var11 = MathHelper.sqrt(var11 * var11 + 4.0F + var12 * var12);
					var11 = 1.0F / var11;
					if (var13 > 1.0F) {
						var11 = 0.0F;
					}

					if (var11 < 0.0F) {
						var11 = 0.0F;
					}

					var8 += var11;
					if (isLit(var9, var14, var10)) {
						var7 += var11;
					}
				}
			}
		}

		if (var8 == 0.0F) {
			return 0.0F;
		} else {
			return var7 / var8;
		}
	}

	public ArrayList<AABB> getCubes(AABB var1) {
		ArrayList<AABB> var2 = new ArrayList<AABB>();
		int var3 = (int) var1.x0;
		int var4 = (int) var1.x1 + 1;
		int var5 = (int) var1.y0;
		int var6 = (int) var1.y1 + 1;
		int var7 = (int) var1.z0;
		int var8 = (int) var1.z1 + 1;
		if (var1.x0 < 0.0F) {
			--var3;
		}

		if (var1.y0 < 0.0F) {
			--var5;
		}

		if (var1.z0 < 0.0F) {
			--var7;
		}
		for (; var3 < var4; ++var3) {
			for (int var9 = var5; var9 < var6; ++var9) {
				for (int var10 = var7; var10 < var8; ++var10) {
					AABB var11;
					if (var3 >= 0 && var9 >= 0 && var10 >= 0 && var3 < width && var9 < depth
							&& var10 < height) {
						Block var12;
						if ((var12 = Block.blocks[getTile(var3, var9, var10)]) != null
								&& (var11 = var12.getCollisionBox(var3, var9, var10)) != null
								&& var1.intersectsInner(var11)) {
							var2.add(var11);
						}
					} else if ((var3 < 0 || var9 < 0 || var10 < 0 || var3 >= width || var10 >= height)
							&& (var11 = Block.BEDROCK.getCollisionBox(var3, var9, var10)) != null
							&& var1.intersectsInner(var11)) {
						var2.add(var11);
					}
				}
			}
		}

		return var2;
	}

	public float getGroundLevel() {
		return getWaterLevel() - 2.0F;
	}

	public int getHighestTile(int var1, int var2) {
		int var3;
		for (var3 = depth; (getTile(var1, var3 - 1, var2) == 0 || Block.blocks[getTile(var1,
				var3 - 1, var2)].getLiquidType() != LiquidType.notLiquid) && var3 > 0; --var3) {
			;
		}

		return var3;
	}

	public LiquidType getLiquid(int var1, int var2, int var3) {
		int var4;
		return (var4 = getTile(var1, var2, var3)) == 0 ? LiquidType.notLiquid : Block.blocks[var4]
				.getLiquidType();
	}

	public Entity getPlayer() {
		return player;
	}

	public int getTile(int var1, int var2, int var3) {
		return var1 >= 0 && var2 >= 0 && var3 >= 0 && var1 < width && var2 < depth && var3 < height ? blocks[(var2
				* height + var3)
				* width + var1] & 255
				: 0;
	}

	public float getWaterLevel() {
		return waterLevel;
	}

	public void initTransient() {
		if (blocks == null) {
			throw new RuntimeException("The level is corrupt!");
		} else {
			listeners = new ArrayList<LevelRenderer>();
			blockers = new int[width * height];
			Arrays.fill(blockers, depth);
			calcLightDepths(0, 0, width, height);
			random = new Random();
			randId = random.nextInt();
			tickList = new ArrayList<NextTickListEntry>();
			if (waterLevel == 0) {
				waterLevel = depth / 2;
			}

			if (skyColor == 0) {
				skyColor = 10079487;
			}

			if (fogColor == 0) {
				fogColor = 16777215;
			}

			if (cloudColor == 0) {
				cloudColor = 16777215;
			}

			if (xSpawn == 0 && ySpawn == 0 && zSpawn == 0) {
				findSpawn();
			}

			if (blockMap == null) {
				blockMap = new BlockMap(width, depth, height);
			}

		}
	}

	public boolean isFree(AABB var1) {
		return blockMap.getEntities((Entity) null, var1).size() == 0;
	}

	public boolean isInBounds(int var1, int var2, int var3) {
		return var1 >= 0 && var2 >= 0 && var3 >= 0 && var1 < width && var2 < depth && var3 < height;
	}

	public boolean isLightBlocker(int var1, int var2, int var3) {
		Block var4;
		return (var4 = Block.blocks[getTile(var1, var2, var3)]) == null ? false : var4.isOpaque();
	}

	public boolean isLit(int var1, int var2, int var3) {
		return var1 >= 0 && var2 >= 0 && var3 >= 0 && var1 < width && var2 < depth && var3 < height ? var2 >= blockers[var1
				+ var3 * width]
				: true;
	}

	private boolean isSolid(float var1, float var2, float var3) {
		int var4;
		return (var4 = getTile((int) var1, (int) var2, (int) var3)) > 0
				&& Block.blocks[var4].isSolid();
	}

	public boolean isSolid(float var1, float var2, float var3, float var4) {
		return this.isSolid(var1 - var4, var2 - var4, var3 - var4) ? true : this.isSolid(var1
				- var4, var2 - var4, var3 + var4) ? true : this.isSolid(var1 - var4, var2 + var4,
				var3 - var4) ? true : this.isSolid(var1 - var4, var2 + var4, var3 + var4) ? true
				: this.isSolid(var1 + var4, var2 - var4, var3 - var4) ? true : this.isSolid(var1
						+ var4, var2 - var4, var3 + var4) ? true : this.isSolid(var1 + var4, var2
						+ var4, var3 - var4) ? true : this.isSolid(var1 + var4, var2 + var4, var3
						+ var4);
	}

	public boolean isSolidTile(int var1, int var2, int var3) {
		Block var4;
		return (var4 = Block.blocks[getTile(var1, var2, var3)]) == null ? false : var4.isSolid();
	}

	public boolean isWater(int var1, int var2, int var3) {
		int var4;
		return (var4 = getTile(var1, var2, var3)) > 0
				&& Block.blocks[var4].getLiquidType() == LiquidType.water;
	}

	public boolean maybeGrowTree(int var1, int var2, int var3) {
		int var4 = random.nextInt(3) + 4;
		boolean var5 = true;

		int var6;
		int var8;
		int var9;
		for (var6 = var2; var6 <= var2 + 1 + var4; ++var6) {
			byte var7 = 1;
			if (var6 == var2) {
				var7 = 0;
			}

			if (var6 >= var2 + 1 + var4 - 2) {
				var7 = 2;
			}

			for (var8 = var1 - var7; var8 <= var1 + var7 && var5; ++var8) {
				for (var9 = var3 - var7; var9 <= var3 + var7 && var5; ++var9) {
					if (var8 >= 0 && var6 >= 0 && var9 >= 0 && var8 < width && var6 < depth
							&& var9 < height) {
						if ((blocks[(var6 * height + var9) * width + var8] & 255) != 0) {
							var5 = false;
						}
					} else {
						var5 = false;
					}
				}
			}
		}

		if (!var5) {
			return false;
		} else if ((blocks[((var2 - 1) * height + var3) * width + var1] & 255) == Block.GRASS.id
				&& var2 < depth - var4 - 1) {
			setTile(var1, var2 - 1, var3, Block.DIRT.id);

			int var13;
			for (var13 = var2 - 3 + var4; var13 <= var2 + var4; ++var13) {
				var8 = var13 - (var2 + var4);
				var9 = 1 - var8 / 2;

				for (int var10 = var1 - var9; var10 <= var1 + var9; ++var10) {
					int var12 = var10 - var1;

					for (var6 = var3 - var9; var6 <= var3 + var9; ++var6) {
						int var11 = var6 - var3;
						if (Math.abs(var12) != var9 || Math.abs(var11) != var9
								|| random.nextInt(2) != 0 && var8 != 0) {
							setTile(var10, var13, var6, Block.LEAVES.id);
						}
					}
				}
			}

			for (var13 = 0; var13 < var4; ++var13) {
				setTile(var1, var2 + var13, var3, Block.LOG.id);
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean netSetTile(int var1, int var2, int var3, int var4) {
		if (netSetTileNoNeighborChange(var1, var2, var3, var4)) {
			updateNeighborsAt(var1, var2, var3, var4);
			return true;
		} else {
			return false;
		}
	}

	public boolean netSetTileNoNeighborChange(int var1, int var2, int var3, int var4) {
		if (var1 >= 0 && var2 >= 0 && var3 >= 0 && var1 < width && var2 < depth && var3 < height) {
			if (var4 == blocks[(var2 * height + var3) * width + var1]) {
				return false;
			} else {
				if (var4 == 0
						&& (var1 == 0 || var3 == 0 || var1 == width - 1 || var3 == height - 1)
						&& var2 >= getGroundLevel() && var2 < getWaterLevel() && !networkMode) {
					var4 = Block.WATER.id;
				}

				byte var5 = blocks[(var2 * height + var3) * width + var1];
				blocks[(var2 * height + var3) * width + var1] = (byte) var4;
				if (var5 != 0) {
					Block.blocks[var5].onRemoved(this, var1, var2, var3);
				}

				if (var4 != 0) {
					Block.blocks[var4].onAdded(this, var1, var2, var3);
				}

				calcLightDepths(var1, var3, 1, 1);

				for (var4 = 0; var4 < listeners.size(); ++var4) {
					listeners.get(var4).queueChunks(var1 - 1, var2 - 1, var3 - 1, var1 + 1,
							var2 + 1, var3 + 1);
				}

				return true;
			}
		} else {
			return false;
		}
	}

	public void playSound(String var1, Entity var2, float var3, float var4, boolean footStep) {
		if (rendererContext$5cd64a7f != null) {
			Minecraft var5;
			if ((var5 = rendererContext$5cd64a7f).soundPlayer == null || !var5.settings.sound) {
				return;
			}

			AudioInfo var6;
			if (var2.distanceToSqr(var5.player) < 1024.0F
					&& (var6 = var5.sound.getAudioInfo(var1, var3, var4)) != null) {
				var5.soundPlayer.play(var6, new EntitySoundPos(var2, var5.player));
			}
		}

	}

	public void playSound(String var1, float var2, float var3, float var4, float var5, float var6) {
		if (rendererContext$5cd64a7f != null) {
			Minecraft var7;
			if ((var7 = rendererContext$5cd64a7f).soundPlayer == null || !var7.settings.sound) {
				return;
			}

			AudioInfo var8;
			if ((var8 = var7.sound.getAudioInfo(var1, var5, var6)) != null) {
				var7.soundPlayer.play(var8, new LevelSoundPos(var2, var3, var4, var7.player));
			}
		}

	}

	public void removeAllNonCreativeModeEntities() {
		blockMap.removeAllNonCreativeModeEntities();
	}

	public void removeEntity(Entity var1) {
		blockMap.remove(var1);
	}

	public void removeListener(LevelRenderer var1) {
		listeners.remove(var1);
	}

	public void setData(int var1, int var2, int var3, byte[] var4) {
		width = var1;
		height = var3;
		depth = var2;
		blocks = var4;
		blockers = new int[var1 * var3];
		Arrays.fill(blockers, depth);
		calcLightDepths(0, 0, var1, var3);

		for (var1 = 0; var1 < listeners.size(); ++var1) {
			listeners.get(var1).refresh();
		}

		tickList.clear();
		findSpawn();
		initTransient();
		System.gc();
	}

	public void setNetworkMode(boolean var1) {
		networkMode = var1;
	}

	public void setSpawnPos(int var1, int var2, int var3, float var4) {
		xSpawn = var1;
		ySpawn = var2;
		zSpawn = var3;
		rotSpawn = var4;
	}

	public boolean setTile(int var1, int var2, int var3, int var4) {
		if (networkMode) {
			return false;
		} else if (setTileNoNeighborChange(var1, var2, var3, var4)) {
			updateNeighborsAt(var1, var2, var3, var4);
			return true;
		} else {
			return false;
		}
	}

	public boolean setTileNoNeighborChange(int var1, int var2, int var3, int var4) {
		return networkMode ? false : netSetTileNoNeighborChange(var1, var2, var3, var4);
	}

	public boolean setTileNoUpdate(int var1, int var2, int var3, int var4) {
		if (var1 >= 0 && var2 >= 0 && var3 >= 0 && var1 < width && var2 < depth && var3 < height) {
			if (var4 == blocks[(var2 * height + var3) * width + var1]) {
				return false;
			} else {
				blocks[(var2 * height + var3) * width + var1] = (byte) var4;
				return true;
			}
		} else {
			return false;
		}
	}

	public void swap(int var1, int var2, int var3, int var4, int var5, int var6) {
		if (!networkMode) {
			int var7 = getTile(var1, var2, var3);
			int var8 = getTile(var4, var5, var6);
			setTileNoNeighborChange(var1, var2, var3, var8);
			setTileNoNeighborChange(var4, var5, var6, var7);
			updateNeighborsAt(var1, var2, var3, var8);
			updateNeighborsAt(var4, var5, var6, var7);
		}
	}

	public void tick() {
		++tickCount;
		int var1 = 1;

		int var2;
		for (var2 = 1; 1 << var1 < width; ++var1) {
			;
		}

		while (1 << var2 < height) {
			++var2;
		}

		int var3 = height - 1;
		int var4 = width - 1;
		int var5 = depth - 1;
		int var6;
		int var7;
		if (tickCount % 5 == 0) {
			var6 = tickList.size();

			for (var7 = 0; var7 < var6; ++var7) {
				NextTickListEntry var8;
				if ((var8 = tickList.remove(0)).ticks > 0) {
					--var8.ticks;
					tickList.add(var8);
				} else {
					byte var9;
					if (isInBounds(var8.x, var8.y, var8.z)
							&& (var9 = blocks[(var8.y * height + var8.z) * width + var8.x]) == var8.block
							&& var9 > 0) {
						Block.blocks[var9].update(this, var8.x, var8.y, var8.z, random);
					}
				}
			}
		}

		unprocessed += width * height * depth;
		var6 = unprocessed / 200;
		unprocessed -= var6 * 200;

		for (var7 = 0; var7 < var6; ++var7) {
			randId = randId * 3 + 1013904223;
			int var12;
			int var13 = (var12 = randId >> 2) & var4;
			int var10 = var12 >> var1 & var3;
			var12 = var12 >> var1 + var2 & var5;
			byte var11 = blocks[(var12 * height + var10) * width + var13];
			if (Block.physics[var11]) {
				Block.blocks[var11].update(this, var13, var12, var10, random);
			}
		}

	}

	public void tickEntities() {
		blockMap.tickAll();
	}

	public void updateNeighborsAt(int var1, int var2, int var3, int var4) {
		updateTile(var1 - 1, var2, var3, var4);
		updateTile(var1 + 1, var2, var3, var4);
		updateTile(var1, var2 - 1, var3, var4);
		updateTile(var1, var2 + 1, var3, var4);
		updateTile(var1, var2, var3 - 1, var4);
		updateTile(var1, var2, var3 + 1, var4);
	}

	private void updateTile(int var1, int var2, int var3, int var4) {
		if (var1 >= 0 && var2 >= 0 && var3 >= 0 && var1 < width && var2 < depth && var3 < height) {
			Block var5;
			if ((var5 = Block.blocks[blocks[(var2 * height + var3) * width + var1]]) != null) {
				var5.onNeighborChange(this, var1, var2, var3, var4);
			}

		}
	}
}
