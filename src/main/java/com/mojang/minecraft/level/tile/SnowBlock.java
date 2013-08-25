package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.phys.AABB;

public final class SnowBlock extends Block {

	int Texture;
	int id;
	public SnowBlock(int var1, int var2) {
		super(var1, var2);
		id = var1;
		Texture = var2;
		this.setBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
	}

	protected final int getTextureId(int texture) {
		return Texture;
	}

	public final boolean isSolid() {
	      return false;
	   }
	
	public final boolean isOpaque() {
	      return true;
	   }

	public final int getDrop() {
		return SNOW.id;
	}

	public final boolean isCube() {
		return false;
	}

	public final boolean canRenderSide(Level level, int x, int y, int z,
			int side) {
		if (this != SNOW) {
			super.canRenderSide(level, x, y, z, side);
		}

		return side == 1 ? true
				: (!super.canRenderSide(level, x, y, z, side) ? false
						: (side == 0 ? true : level.getTile(x, y, z) != this.id));
	}

	@Override
	public AABB getCollisionBox(int x, int y, int z) {
		return null;
	}
}
