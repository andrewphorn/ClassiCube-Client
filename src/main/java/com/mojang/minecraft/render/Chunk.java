package com.mojang.minecraft.render;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.player.Player;
import com.mojang.util.MathHelper;
import org.lwjgl.opengl.GL11;

public final class Chunk {

	private Level level;
	private int baseListId = -1;
	private static ShapeRenderer shapeRenderer = ShapeRenderer.instance;
	public static int chunkUpdates = 0;
	private int x;
	private int y;
	private int z;
	private int chunkSize;
	public boolean visible = false;
	private boolean[] dirty = new boolean[2];
	public boolean loaded;

	public Chunk(Level var1, int x, int y, int z, int listID) {
		this.level = var1;
		this.x = x;
		this.y = y;
		this.z = z;
		this.chunkSize = 16;
		MathHelper.sqrt(this.chunkSize * this.chunkSize + this.chunkSize * this.chunkSize + this.chunkSize
				* this.chunkSize);
		this.baseListId = listID;
		this.setAllDirty();
	}

	public final int appendLists(int[] var1, int var2, int var3) {
		if (!this.visible) {
			return var2;
		} else {
			if (!this.dirty[var3]) {
				var1[var2++] = this.baseListId + var3;
			}

			return var2;
		}
	}

	public final void clip(Frustrum frustrum) {
		this.visible = frustrum.isBoxInFrustum(this.x, this.y, this.z,
				this.x + this.chunkSize, this.y + this.chunkSize,
				this.z + this.chunkSize);
	}

	public final void dispose() {
		this.setAllDirty();
		this.level = null;
	}

	public final float distanceSquared(Player player) {
		float dx = player.x - this.x;
		float dy = player.y - this.y;
		float dz = player.z - this.z;
		return dx * dx + dy * dy + dz * dz;
	}

	private void setAllDirty() {
		for (int i = 0; i < 2; ++i) {
			this.dirty[i] = true;
		}
	}

	public final void update() {
		chunkUpdates++;
		int sx = this.x;
		int sy = this.y;
		int sz = this.z;
		int ex = this.x + this.chunkSize;
		int ey = this.y + this.chunkSize;
		int ez = this.z + this.chunkSize;

		int renderPassType;
		for (renderPassType = 0; renderPassType < 2; ++renderPassType) {
			this.dirty[renderPassType] = true;
		}

		for (renderPassType = 0; renderPassType < 2; ++renderPassType) {
			boolean wasSkipped = false; //perhaps its called this
			boolean wasRendered = false;
			GL11.glNewList(this.baseListId + renderPassType, GL11.GL_COMPILE);
			
			shapeRenderer.begin();
			for (int posX = sx; posX < ex; ++posX) {
				for (int posY = sy; posY < ey; ++posY) {
					for (int posZ = sz; posZ < ez; ++posZ) {
						int var13;
						if ((var13 = this.level.getTile(posX, posY, posZ)) > 0) {
							Block var14;
							if ((var14 = Block.blocks[var13]).getRenderPass() != renderPassType) {
								wasSkipped = true;
							} else {
								wasRendered |= var14.render(this.level, posX, posY, posZ, shapeRenderer);
							}
						}
					}
				}
			}
			shapeRenderer.end();
			
			GL11.glEndList();
			if (wasRendered) {
				this.dirty[renderPassType] = false;
			}

			if (!wasSkipped) {
				break;
			}
		}
	}
}