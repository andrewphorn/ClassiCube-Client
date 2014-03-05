package com.mojang.minecraft.render;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.player.Player;
import com.mojang.util.MathHelper;

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
        level = var1;
        this.x = x;
        this.y = y;
        this.z = z;
        chunkSize = 16;
        MathHelper.sqrt(chunkSize * chunkSize + chunkSize * chunkSize + chunkSize * chunkSize);
        baseListId = listID;
        setAllDirty();
    }

    public final int appendLists(int[] var1, int var2, int var3) {
        if (!visible) {
            return var2;
        } else {
            if (!dirty[var3]) {
                var1[var2++] = baseListId + var3;
            }

            return var2;
        }
    }

    public final void clip(Frustrum frustrum) {
        visible = frustrum.isBoxInFrustum(x, y, z, x + chunkSize, y + chunkSize, z + chunkSize);
    }

    public final void dispose() {
        setAllDirty();
        level = null;
    }

    public final float distanceSquared(Player player) {
        float dx = player.x - x;
        float dy = player.y - y;
        float dz = player.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    private void setAllDirty() {
        for (int i = 0; i < 2; ++i) {
            dirty[i] = true;
        }
    }

    public final void update() {
        chunkUpdates++;
        int sx = x;
        int sy = y;
        int sz = z;
        int ex = x + chunkSize;
        int ey = y + chunkSize;
        int ez = z + chunkSize;

        int renderPassType;
        for (renderPassType = 0; renderPassType < 2; ++renderPassType) {
            dirty[renderPassType] = true;
        }

        for (renderPassType = 0; renderPassType < 2; ++renderPassType) {
            boolean wasSkipped = false; // perhaps its called this
            boolean wasRendered = false;
            GL11.glNewList(baseListId + renderPassType, GL11.GL_COMPILE);

            shapeRenderer.begin();
            for (int posX = sx; posX < ex; ++posX) {
                for (int posY = sy; posY < ey; ++posY) {
                    for (int posZ = sz; posZ < ez; ++posZ) {
                        int var13;
                        if ((var13 = level.getTile(posX, posY, posZ)) > 0) {
                            Block var14;
                            if ((var14 = Block.blocks[var13]).getRenderPass() != renderPassType) {
                                wasSkipped = true;
                            } else {
                                wasRendered |= var14.render(level, posX, posY, posZ, shapeRenderer);
                            }
                        }
                    }
                }
            }
            shapeRenderer.end();

            GL11.glEndList();
            if (wasRendered) {
                dirty[renderPassType] = false;
            }

            if (!wasSkipped) {
                break;
            }
        }
    }
}