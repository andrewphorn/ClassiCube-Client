package com.mojang.minecraft.render;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.player.Player;

public final class LevelRenderer {

	public Level level;
	public TextureManager textureManager;
	public int listId;
	public IntBuffer buffer = BufferUtils.createIntBuffer(65536);
	public List<Chunk> chunks = new ArrayList<Chunk>();
	private Chunk[] loadQueue;
	public Chunk[] chunkCache;
	private int xChunks;
	private int yChunks;
	private int zChunks;
	private int baseListId;
	public Minecraft minecraft;
	private int[] chunkDataCache = new int['\uc350'];
	public int ticks = 0;
	private float lastLoadX = -9999.0F;
	private float lastLoadY = -9999.0F;
	private float lastLoadZ = -9999.0F;
	public float cracks;

	public List<BlockData> iceBlocks = new ArrayList<BlockData>();

	public LevelRenderer(Minecraft var1, TextureManager var2) {
		minecraft = var1;
		textureManager = var2;
		listId = GL11.glGenLists(2);
		baseListId = GL11.glGenLists(4096 << 6 << 1);
	}

	public final void queueChunks(int var1, int var2, int var3, int var4, int var5, int var6) {
		var1 /= 16;
		var2 /= 16;
		var3 /= 16;
		var4 /= 16;
		var5 /= 16;
		var6 /= 16;
		if (var1 < 0) {
			var1 = 0;
		}

		if (var2 < 0) {
			var2 = 0;
		}

		if (var3 < 0) {
			var3 = 0;
		}

		if (var4 > xChunks - 1) {
			var4 = xChunks - 1;
		}

		if (var5 > yChunks - 1) {
			var5 = yChunks - 1;
		}

		if (var6 > zChunks - 1) {
			var6 = zChunks - 1;
		}

		for (; var1 <= var4; ++var1) {
			for (int var7 = var2; var7 <= var5; ++var7) {
				for (int var8 = var3; var8 <= var6; ++var8) {
					Chunk var9;
					if (!(var9 = chunkCache[(var8 * yChunks + var7) * xChunks + var1]).loaded) {
						var9.loaded = true;
						chunks.add(chunkCache[(var8 * yChunks + var7) * xChunks + var1]);
					}
				}
			}
		}
	}

	public final void refresh() {
		int var1;
		if (chunkCache != null) {
			for (var1 = 0; var1 < chunkCache.length; ++var1) {
				chunkCache[var1].dispose();
			}
		}

		xChunks = level.width / 16;
		yChunks = level.depth / 16;
		zChunks = level.height / 16;
		chunkCache = new Chunk[xChunks * yChunks * zChunks];
		loadQueue = new Chunk[xChunks * yChunks * zChunks];
		var1 = 0;

		int var2;
		int var4;
		for (var2 = 0; var2 < xChunks; ++var2) {
			for (int var3 = 0; var3 < yChunks; ++var3) {
				for (var4 = 0; var4 < zChunks; ++var4) {
					chunkCache[(var4 * yChunks + var3) * xChunks + var2] = new Chunk(level,
							var2 << 4, var3 << 4, var4 << 4, baseListId + var1);
					loadQueue[(var4 * yChunks + var3) * xChunks + var2] = chunkCache[(var4
							* yChunks + var3)
							* xChunks + var2];
					var1 += 2;
				}
			}
		}

		for (var2 = 0; var2 < chunks.size(); ++var2) {
			chunks.get(var2).loaded = false;
		}

		chunks.clear();
		GL11.glNewList(listId, 4864);
		LevelRenderer var9 = this;
		float waterLevel = 0.5F;
		if (level.customLightColour != null) {
			GL11.glColor4f(level.customLightColour.R, level.customLightColour.G,
					level.customLightColour.B, 1.0F);
		} else {
			GL11.glColor4f(0.5F, waterLevel, waterLevel, 1.0F);
		}
		ShapeRenderer var11 = ShapeRenderer.instance;
		float groundLevel = level.getGroundLevel();
		int var5 = 128;
		if (128 > level.width) {
			var5 = level.width;
		}

		if (var5 > level.height) {
			var5 = level.height;
		}

		int var6 = 2048 / var5;
		var11.begin();

		int var7;
		for (var7 = -var5 * var6; var7 < var9.level.width + var5 * var6; var7 += var5) {
			for (int var8 = -var5 * var6; var8 < var9.level.height + var5 * var6; var8 += var5) {
				waterLevel = groundLevel;
				if (var7 >= 0 && var8 >= 0 && var7 < var9.level.width && var8 < var9.level.height) {
					waterLevel = 0.0F;
				}
				var11.vertexUV(var7, waterLevel, var8 + var5, 0.0F, var5);
				var11.vertexUV(var7 + var5, waterLevel, var8 + var5, var5, var5);
				var11.vertexUV(var7 + var5, waterLevel, var8, var5, 0.0F);
				var11.vertexUV(var7, waterLevel, var8, 0.0F, 0.0F);
			}
		}

		var11.end();
		if (level.customLightColour != null) {
			GL11.glColor4f(level.customLightColour.R, level.customLightColour.G,
					level.customLightColour.B, 1.0F);
		}
		var11.begin();

		for (var7 = 0; var7 < var9.level.width; var7 += var5) {
			var11.vertexUV(var7, 0.0F, 0.0F, 0.0F, 0.0F);
			var11.vertexUV(var7 + var5, 0.0F, 0.0F, var5, 0.0F);
			var11.vertexUV(var7 + var5, groundLevel, 0.0F, var5, groundLevel);
			var11.vertexUV(var7, groundLevel, 0.0F, 0.0F, groundLevel);
			var11.vertexUV(var7, groundLevel, var9.level.height, 0.0F, groundLevel);
			var11.vertexUV(var7 + var5, groundLevel, var9.level.height, var5, groundLevel);
			var11.vertexUV(var7 + var5, 0.0F, var9.level.height, var5, 0.0F);
			var11.vertexUV(var7, 0.0F, var9.level.height, 0.0F, 0.0F);
		}

		if (level.customLightColour != null) {
			GL11.glColor4f(level.customLightColour.R, level.customLightColour.G,
					level.customLightColour.B, 1.0F);
		}

		for (var7 = 0; var7 < var9.level.height; var7 += var5) {
			var11.vertexUV(0.0F, groundLevel, var7, 0.0F, 0.0F);
			var11.vertexUV(0.0F, groundLevel, var7 + var5, var5, 0.0F);
			var11.vertexUV(0.0F, 0.0F, var7 + var5, var5, groundLevel);
			var11.vertexUV(0.0F, 0.0F, var7, 0.0F, groundLevel);
			var11.vertexUV(var9.level.width, 0.0F, var7, 0.0F, groundLevel);
			var11.vertexUV(var9.level.width, 0.0F, var7 + var5, var5, groundLevel);
			var11.vertexUV(var9.level.width, groundLevel, var7 + var5, var5, 0.0F);
			var11.vertexUV(var9.level.width, groundLevel, var7, 0.0F, 0.0F);
		}

		var11.end();
		GL11.glEndList();

		GL11.glNewList(listId + 1, 4864);
		var9 = this;
		if (level.customLightColour != null) {
			GL11.glColor4f(level.customLightColour.R, level.customLightColour.G,
					level.customLightColour.B, 1.0F);
		}
		waterLevel = level.getWaterLevel();

		GL11.glBlendFunc(770, 771);
		var11 = ShapeRenderer.instance;
		var4 = 128;
		if (128 > level.width) {
			var4 = level.width;
		}

		if (var4 > level.height) {
			var4 = level.height;
		}

		var5 = 2048 / var4;
		var11.begin();

		for (var6 = -var4 * var5; var6 < var9.level.width + var4 * var5; var6 += var4) {
			for (var7 = -var4 * var5; var7 < var9.level.height + var4 * var5; var7 += var4) {
				float var13 = waterLevel - 0.1F;
				if (var6 < 0 || var7 < 0 || var6 >= var9.level.width || var7 >= var9.level.height) {
					var11.vertexUV(var6, var13, var7 + var4, 0.0F, var4);
					var11.vertexUV(var6 + var4, var13, var7 + var4, var4, var4);
					var11.vertexUV(var6 + var4, var13, var7, var4, 0.0F);
					var11.vertexUV(var6, var13, var7, 0.0F, 0.0F);
					var11.vertexUV(var6, var13, var7, 0.0F, 0.0F);
					var11.vertexUV(var6 + var4, var13, var7, var4, 0.0F);
					var11.vertexUV(var6 + var4, var13, var7 + var4, var4, var4);
					var11.vertexUV(var6, var13, var7 + var4, 0.0F, var4);
				}
			}
		}

		var11.end();
		GL11.glDisable(3042);
		GL11.glEndList();
		queueChunks(0, 0, 0, level.width, level.depth, level.height);
	}

	public final int sortChunks(Player var1, int var2) {
		float var3 = var1.x - lastLoadX;
		float var4 = var1.y - lastLoadY;
		float var5 = var1.z - lastLoadZ;
		if (var3 * var3 + var4 * var4 + var5 * var5 > 64.0F) {
			lastLoadX = var1.x;
			lastLoadY = var1.y;
			lastLoadZ = var1.z;
			Arrays.sort(loadQueue, new ChunkDirtyDistanceComparator(var1));
		}

		int var6 = 0;

		for (int var7 = 0; var7 < loadQueue.length; ++var7) {
			var6 = loadQueue[var7].appendLists(chunkDataCache, var6, var2);
		}

		buffer.clear();
		buffer.put(chunkDataCache, 0, var6);
		buffer.flip();
		if (buffer.remaining() > 0) {
			GL11.glBindTexture(3553, textureManager.load("/terrain.png"));
			GL11.glCallLists(buffer);
		}

		return buffer.remaining();
	}
}
