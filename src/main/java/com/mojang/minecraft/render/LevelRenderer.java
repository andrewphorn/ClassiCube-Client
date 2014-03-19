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
    public List<Chunk> chunks = new ArrayList<>();
    private Chunk[] loadQueue;
    public Chunk[] chunkCache;
    private int xChunks;
    private int yChunks;
    private int zChunks;
    private int baseListId;
    private int listsCount = -1;
    public Minecraft minecraft;
    private int[] chunkDataCache = new int['\uc350'];
    public int ticks = 0;
    private float lastLoadX = -9999F;
    private float lastLoadY = -9999F;
    private float lastLoadZ = -9999F;
    public float cracks;

    public LevelRenderer(Minecraft minecraft, TextureManager textureManager) {
        this.minecraft = minecraft;
        this.textureManager = textureManager;
        listId = GL11.glGenLists(2);
    }

    public final void queueChunks(int x1, int y1, int z1, int x2, int y2, int z2) {
        x1 /= 16;
        y1 /= 16;
        z1 /= 16;
        x2 /= 16;
        y2 /= 16;
        z2 /= 16;

        if (x1 < 0) {
            x1 = 0;
        }
        if (y1 < 0) {
            y1 = 0;
        }
        if (z1 < 0) {
            z1 = 0;
        }

        if (x2 > xChunks - 1) {
            x2 = xChunks - 1;
        }
        if (y2 > yChunks - 1) {
            y2 = yChunks - 1;
        }
        if (z2 > zChunks - 1) {
            z2 = zChunks - 1;
        }

        for (int x = x1; x <= x2; ++x) {
            for (int y = y1; y <= y2; ++y) {
                for (int z = z1; z <= z2; ++z) {
                    Chunk chunk = chunkCache[(z * yChunks + y) * xChunks + x];
                    if (!chunk.loaded) {
                        chunk.loaded = true;
                        chunks.add(chunk);
                    }
                }
            }
        }
    }

    static int nextMultipleOf16(int value) {
        int remainder = value % 16;
        if (remainder != 0) {
            return value + (16 - remainder);
        }
        return value;
    }

    public final void refresh() {
        if (chunkCache != null) {
            for (Chunk aChunkCache : chunkCache) {
                aChunkCache.dispose();
            }
        }
        if (listsCount > -1) {
            GL11.glDeleteLists(baseListId, listsCount);
        }
        // So that worlds that are not multiples of 16 do not have invisible
        // chunks.
        int paddedWidth = nextMultipleOf16(level.width);
        int paddedHeight = nextMultipleOf16(level.height);
        int paddedLength = nextMultipleOf16(level.length);

        xChunks = paddedWidth / 16;
        yChunks = paddedHeight / 16;
        zChunks = paddedLength / 16;
        chunkCache = new Chunk[xChunks * yChunks * zChunks];
        loadQueue = new Chunk[xChunks * yChunks * zChunks];

        int offset = 0;
        listsCount = xChunks * yChunks * zChunks * 2;
        baseListId = GL11.glGenLists(listsCount);

        for (int x = 0; x < xChunks; ++x) {
            for (int y = 0; y < yChunks; ++y) {
                for (int z = 0; z < zChunks; ++z) {
                    chunkCache[(z * yChunks + y) * xChunks + x] = new Chunk(level, x << 4, y << 4,
                            z << 4, baseListId + offset);
                    loadQueue[(z * yChunks + y) * xChunks + x] = chunkCache[(z * yChunks + y)
                            * xChunks + x];
                    offset += 2;
                }
            }
        }

        for (Chunk chunk : chunks) {
            chunk.loaded = false;
        }

        chunks.clear();
        refreshEnvironment();
        queueChunks(0, 0, 0, paddedWidth, paddedHeight, paddedLength);
    }

    public final void refreshEnvironment() {
        GL11.glNewList(listId, 4864);
        if (level.customLightColour != null) {
            GL11.glColor4f(level.customLightColour.R, level.customLightColour.G,
                    level.customLightColour.B, 1F);
        } else {
            GL11.glColor4f(0.5F, 0.5F, 0.5F, 1F);
        }

        int size = 128;
        if (size > level.width) {
            size = level.width;
        }

        if (size > level.length) {
            size = level.length;
        }
        int extent = 2048 / size;

        ShapeRenderer renderer = ShapeRenderer.instance;
        float groundLevel = level.getGroundLevel();

        renderer.begin();
        // Bedrock horizontal axis. (beneath and outside map)
        for (int x = -size * extent; x < level.width + size * extent; x += size) {
            for (int z = -size * extent; z < level.length + size * extent; z += size) {
                float y = groundLevel;
                if (x >= 0 && z >= 0 && x < level.width && z < level.length) {
                    y = 0F;
                }
                renderer.vertexUV(x, y, z + size, 0F, size);
                renderer.vertexUV(x + size, y, z + size, size, size);
                renderer.vertexUV(x + size, y, z, size, 0F);
                renderer.vertexUV(x, y, z, 0F, 0F);
            }
        }

        // Bedrock vertical X axis.
        for (int x = 0; x < level.width; x += size) {
            renderer.vertexUV(x, 0F, 0F, 0F, 0F);
            renderer.vertexUV(x + size, 0F, 0F, size, 0F);
            renderer.vertexUV(x + size, groundLevel, 0F, size, groundLevel);
            renderer.vertexUV(x, groundLevel, 0F, 0F, groundLevel);
            renderer.vertexUV(x, groundLevel, level.length, 0F, groundLevel);
            renderer.vertexUV(x + size, groundLevel, level.length, size, groundLevel);
            renderer.vertexUV(x + size, 0F, level.length, size, 0F);
            renderer.vertexUV(x, 0F, level.length, 0F, 0F);
        }

        // Bedrock vertical Z axis.
        for (int z = 0; z < level.length; z += size) {
            renderer.vertexUV(0F, groundLevel, z, 0F, 0F);
            renderer.vertexUV(0F, groundLevel, z + size, size, 0F);
            renderer.vertexUV(0F, 0F, z + size, size, groundLevel);
            renderer.vertexUV(0F, 0F, z, 0F, groundLevel);
            renderer.vertexUV(level.width, 0F, z, 0F, groundLevel);
            renderer.vertexUV(level.width, 0F, z + size, size, groundLevel);
            renderer.vertexUV(level.width, groundLevel, z + size, size, 0F);
            renderer.vertexUV(level.width, groundLevel, z, 0F, 0F);
        }
        renderer.end();
        GL11.glEndList();

        GL11.glNewList(listId + 1, 4864);
        if (level.customLightColour != null) {
            GL11.glColor4f(level.customLightColour.R, level.customLightColour.G,
                    level.customLightColour.B, 1F);
        }
        float waterLevel = level.getWaterLevel();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        renderer.begin();

        // Water horizontal axis. (outside map)
        for (int x = -size * extent; x < level.width + size * extent; x += size) {
            for (int z = -size * extent; z < level.length + size * extent; z += size) {
                float y = waterLevel - 0.1F;
                if (x < 0 || z < 0 || x >= level.width || z >= level.length) {
                    renderer.vertexUV(x, y, z + size, 0F, size);
                    renderer.vertexUV(x + size, y, z + size, size, size);
                    renderer.vertexUV(x + size, y, z, size, 0F);
                    renderer.vertexUV(x, y, z, 0F, 0F);

                    // Seems to be rendered twice? Not sure why, possibly used
                    // for animated textures?
                    renderer.vertexUV(x, y, z, 0F, 0F);
                    renderer.vertexUV(x + size, y, z, size, 0F);
                    renderer.vertexUV(x + size, y, z + size, size, size);
                    renderer.vertexUV(x, y, z + size, 0F, size);
                }
            }
        }
        renderer.end();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEndList();
    }

    public final int sortChunks(Player player, int renderPass) {
        float distX = player.x - lastLoadX;
        float distY = player.y - lastLoadY;
        float distZ = player.z - lastLoadZ;
        if (distX * distX + distY * distY + distZ * distZ > 64f) {
            lastLoadX = player.x;
            lastLoadY = player.y;
            lastLoadZ = player.z;
            Arrays.sort(loadQueue, new ChunkDirtyDistanceComparator(player));
        }

        int count = 0;
        for (Chunk aLoadQueue : loadQueue) {
            count = aLoadQueue.appendLists(chunkDataCache, count, renderPass);
        }

        buffer.clear();
        buffer.put(chunkDataCache, 0, count);
        buffer.flip();
        if (buffer.remaining() > 0) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.load("/terrain.png"));
            GL11.glCallLists(buffer);
        }

        return buffer.remaining();
    }
}
