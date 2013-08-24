package com.mojang.minecraft.gui;

import com.mojang.minecraft.SessionData;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.level.tile.BlockID;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.render.TextureManager;
import org.lwjgl.opengl.GL11;

public final class BlockSelectScreen extends GuiScreen {

	int BlocksPerRow = 14;
	int Spacing = 18;
	public BlockSelectScreen() {
		this.grabsMouse = true;
	}

	private int getBlockOnScreen(int var1, int var2) {
		for (int var3 = 0; var3 < SessionData.allowedBlocks.size(); ++var3) {
			int var4 = this.width / 2 + var3 % BlocksPerRow * Spacing + -128
					- 3;
			int var5 = this.height / 2 + var3 / BlocksPerRow * Spacing + -45
					+ 3;
			if (var1 >= var4 && var1 <= var4 + Spacing
					&& var2 >= var5 - BlocksPerRow
					&& var2 <= var5 + BlocksPerRow) {
				return var3;
			}
		}

		return -1;
	}

	float lastAngle = 0;

	public final void render(int var1, int var2) {
		var1 = this.getBlockOnScreen(var1, var2);

		drawFadingBox(this.width / 2 - 140, 50, this.width / 2 + 140, 200,
				-1878719232, -1070583712);
		if (var1 >= 0) {
			var2 = this.width / 2 + var1 % BlocksPerRow * Spacing + -128;
			drawCenteredString(this.fontRenderer, GetBlockName(var1),
					this.width / 2, 185, 16777215);
		}

		drawCenteredString(this.fontRenderer, "Select block", this.width / 2,
				60, 16777215);
		TextureManager var7 = this.minecraft.textureManager;
		ShapeRenderer var8 = ShapeRenderer.instance;
		var2 = var7.load("/terrain.png");
		GL11.glBindTexture(3553, var2);
		for (var2 = 0; var2 < SessionData.allowedBlocks.size(); ++var2) {
			Block var4 = (Block) SessionData.allowedBlocks.get(var2);
			if (var4 != null) {
				GL11.glPushMatrix();
				int var5 = this.width / 2 + var2 % BlocksPerRow * Spacing
						+ -128;
				int var6 = this.height / 2 + var2 / BlocksPerRow * Spacing
						+ -45;
				GL11.glTranslatef((float) var5, (float) var6, 0.0F);
				GL11.glScalef(9.0F, 9.0F, 9.0F);
				GL11.glTranslatef(1.0F, 0.5F, 8.0F);
				GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				if (var1 == var2) {
					lastAngle += 0.7F;
					GL11.glRotatef(lastAngle, 0.0F, 1.0F, 0.0F);
					GL11.glScalef(1.6F, 1.6F, 1.6F);
				}

				GL11.glTranslatef(-1.5F, 0.5F, 0.5F);
				GL11.glScalef(-1.0F, -1.0F, -1.0F);
				var8.begin();
				var4.renderFullbright(var8);
				var8.end();
				GL11.glPopMatrix();
			}
		}
	}

	String GetBlockName(int id) {
		if (id <= 0 || id >= 255)
			return "";
		Block b = (Block) SessionData.allowedBlocks.get(id);
		if (b == null)
			return "";
		int ID = b.id;
		BlockID bid = BlockID.values()[ID + 1];
		String s;
		if (bid == null)
			s = "";
		else
			s = bid.name();
		return s;
	}

	protected final void onMouseClick(int var1, int var2, int var3) {
		if (var3 == 0) {
			this.minecraft.player.inventory.replaceSlot(this.getBlockOnScreen(
					var1, var2));
			this.minecraft.setCurrentScreen((GuiScreen) null);
		}

	}
}
