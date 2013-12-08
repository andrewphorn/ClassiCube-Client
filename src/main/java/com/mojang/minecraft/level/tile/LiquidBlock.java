package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.ColorCache;

import java.util.Random;

public class LiquidBlock extends Block {

	protected LiquidType type;
	protected int stillId;
	protected int movingId;

	protected LiquidBlock(int var1, LiquidType var2) {
		super(var1);
		this.type = var2;
		this.textureId = 14;
		if (var2 == LiquidType.lava) {
			this.textureId = 30;
		}

		Block.liquid[var1] = true;
		this.movingId = var1;
		this.stillId = var1 + 1;
		float var4 = 0.01F;
		float var3 = 0.1F;
		this.setBounds(var4 + 0.0F, 0.0F - var3 + var4, var4 + 0.0F, var4 + 1.0F, 1.0F - var3
				+ var4, var4 + 1.0F);
		this.setPhysics(true);
	}

	private boolean canFlow(Level var1, int var2, int var3, int var4) {
		if (this.type == LiquidType.water) {
			for (int var7 = var2 - 2; var7 <= var2 + 2; ++var7) {
				for (int var5 = var3 - 2; var5 <= var3 + 2; ++var5) {
					for (int var6 = var4 - 2; var6 <= var4 + 2; ++var6) {
						if (var1.getTile(var7, var5, var6) == Block.SPONGE.id) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	public final boolean canRenderSide(Level level, int x, int y, int z, int side) {
		int var6;
		return x >= 0 && y >= 0 && z >= 0 && x < level.width && z < level.height ? ((var6 = level
				.getTile(x, y, z)) != this.movingId && var6 != this.stillId ? (side == 1
				&& (level.getTile(x - 1, y, z) == 0 || level.getTile(x + 1, y, z) == 0
						|| level.getTile(x, y, z - 1) == 0 || level.getTile(x, y, z + 1) == 0) ? true
				: super.canRenderSide(level, x, y, z, side))
				: false)
				: false;
	}

	public final void dropItems(Level var1, int var2, int var3, int var4, float var5) {
	}

	private boolean flow(Level var1, int var2, int var3, int var4) {
		if (var1.getTile(var2, var3, var4) == 0) {
			if (!this.canFlow(var1, var2, var3, var4)) {
				return false;
			}

			if (var1.setTile(var2, var3, var4, this.movingId)) {
				var1.addToTickNextTick(var2, var3, var4, this.movingId);
			}
		}

		return false;
	}

	protected final ColorCache getBrightness(Level level, int x, int y, int z) {
		if (this.type == LiquidType.lava) {
			final ColorCache c = new ColorCache(0, 0, 0);
			c.R = 100F;
			c.G = 100F;
			c.B = 100F;
			return c;
		}

		else
			return level.getBrightnessColor(x, y, z);
	}

	@Override
	public AABB getCollisionBox(int x, int y, int z) {
		return null;
	}

	public final int getDropCount() {
		return 0;
	}

	public final LiquidType getLiquidType() {
		return this.type;
	}

	public final int getRenderPass() {
		return this.type == LiquidType.water ? 1 : 0;
	}

	public final int getTickDelay() {
		return this.type == LiquidType.lava ? 5 : 0;
	}

	public final boolean isCube() {
		return false;
	}

	public final boolean isOpaque() {
		return true;
	}

	public final boolean isSolid() {
		return false;
	}

	public final void onBreak(Level var1, int var2, int var3, int var4) {
	}

	public void onNeighborChange(Level var1, int var2, int var3, int var4, int var5) {
		if (var5 != 0) {
			LiquidType var6 = Block.blocks[var5].getLiquidType();
			if (this.type == LiquidType.water && var6 == LiquidType.lava
					|| var6 == LiquidType.water && this.type == LiquidType.lava) {
				var1.setTile(var2, var3, var4, Block.OBSIDIAN.id);
				return;
			}
		}

		var1.addToTickNextTick(var2, var3, var4, var5);
	}

	public final void onPlace(Level level, int x, int y, int z) {
		level.addToTickNextTick(x, y, z, this.movingId);
	}

	public final void renderInside(ShapeRenderer shapeRenderer, int x, int y, int z, int side) {
		super.renderInside(shapeRenderer, x, y, z, side);
		super.renderSide(shapeRenderer, x, y, z, side);
	}

	public void update(Level level, int x, int y, int z, Random rand) {
		boolean var8 = false;

		boolean var6;
		do {
			--y;
			if (level.getTile(x, y, z) != 0 || !this.canFlow(level, x, y, z)) {
				break;
			}

			if (var6 = level.setTile(x, y, z, this.movingId)) {
				var8 = true;
			}
		} while (var6 && this.type != LiquidType.lava);

		++y;
		if (this.type == LiquidType.water || !var8) {
			var8 = var8 | this.flow(level, x - 1, y, z) | this.flow(level, x + 1, y, z)
					| this.flow(level, x, y, z - 1) | this.flow(level, x, y, z + 1);
		}

		if (!var8) {
			level.setTileNoUpdate(x, y, z, this.stillId);
		} else {
			level.addToTickNextTick(x, y, z, this.movingId);
		}

	}
}
