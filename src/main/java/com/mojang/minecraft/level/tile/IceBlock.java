package com.mojang.minecraft.level.tile;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.render.ShapeRenderer;

public final class IceBlock extends Block {

	int ID;
	boolean showNeighborSides = false;

	protected IceBlock(int var1, int var2) {
		super(var1, var2);
		ID = var1;
		Block.liquid[var1] = true;
	}

	public final boolean canRenderSide(Level level, int x, int y, int z, int side) {
		int var6 = level.getTile(x, y, z);
		return !this.showNeighborSides && var6 == this.id ? false : super.canRenderSide(level, x,
				y, z, side);
	}

	public final int getRenderPass() {
		return 1;
	}

	public final boolean isOpaque() {
		return true;
	}

	public final boolean isSolid() {
		return false;
	}

	@Override
	public void renderPreview(ShapeRenderer shapeRenderer) {
		shapeRenderer.begin();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(770, 771);
		for (int face = 0; face < 6; ++face) {
			/*if (face == 0) {
				shapeRenderer.useNormal(0.0F, 1.0F, 0.0F);
			}

			if (face == 1) { 
				shapeRenderer.useNormal(0.0F, -1.0F, 0.0F);
			}

			if (face == 2) {
				shapeRenderer.useNormal(0.0F, 0.0F, 1.0F);
			}

			if (face == 3) {
				shapeRenderer.useNormal(0.0F, 0.0F, -1.0F);
			}

			if (face == 4) {
				shapeRenderer.useNormal(1.0F, 0.0F, 0.0F);
			}

			if (face == 5) {
				shapeRenderer.useNormal(-1.0F, 0.0F, 0.0F);
			}*/

			this.renderInside(shapeRenderer, 0, 0, 0, face);
		}
		GL11.glDisable(GL11.GL_BLEND);
		shapeRenderer.end();
	}
	
	@Override
	public void renderInside(ShapeRenderer shapeRenderer, int x, int y, int z, int side) {
		int textureID1 = getTextureId(side);

		renderSide(shapeRenderer, x, y, z, side, textureID1);
	}
}
