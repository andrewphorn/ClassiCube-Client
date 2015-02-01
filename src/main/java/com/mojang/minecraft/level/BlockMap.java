package com.mojang.minecraft.level;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.physics.AABB;
import com.mojang.minecraft.render.Frustum;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.util.Vector3f;

public class BlockMap implements Serializable {

    public List<Entity>[] entityGrid;
    public List<Entity> all = new ArrayList<>();
    private int width;
    private int depth;
    private int height;
    private final BlockMapSlot slot = new BlockMapSlot(this);
    private final BlockMapSlot slot2 = new BlockMapSlot(this);
    private final List<Entity> tmp = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public BlockMap(int x, int y, int z) {
        width = x / 16;
        depth = y / 16;
        height = z / 16;
        if (width == 0) {
            width = 1;
        }

        if (depth == 0) {
            depth = 1;
        }

        if (height == 0) {
            height = 1;
        }

        entityGrid = new ArrayList[width * depth * height];

        for (x = 0; x < width; ++x) {
            for (y = 0; y < depth; ++y) {
                for (z = 0; z < height; ++z) {
                    entityGrid[(z * depth + y) * width + x] = new ArrayList<>();
                }
            }
        }

    }

    // $FF: synthetic method
    static int getDepth(BlockMap blockMap) {
        return blockMap.depth;
    }

    // $FF: synthetic method
    static int getHeight(BlockMap blockMap) {
        return blockMap.height;
    }

    // $FF: synthetic method
    static int getWidth(BlockMap blockMap) {
        return blockMap.width;
    }

    public void clear() {
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < depth; ++y) {
                for (int z = 0; z < height; ++z) {
                    entityGrid[(z * depth + y) * width + x].clear();
                }
            }
        }

    }

    public List<Entity> getEntities(Entity entitiy, AABB aabb) {
        tmp.clear();
        return this.getEntities(entitiy, aabb.maxX, aabb.maxY, aabb.maxZ, aabb.minX, aabb.minY, aabb.minZ, tmp);
    }

    public List<Entity> getEntities(Entity var1, AABB var2, List<Entity> var3) {
        return this.getEntities(var1, var2.maxX, var2.maxY, var2.maxZ, var2.minX, var2.minY, var2.minZ, var3);
    }

    public List<Entity> getEntities(Entity entity, float x1, float y1, float z1, float x2,
                                    float y2, float z2) {
        tmp.clear();
        return this.getEntities(entity, x1, y1, z1, x2, y2, z2, tmp);
    }

    public List<Entity> getEntities(Entity entity, float x1, float y1, float z1, float x2,
                                    float y2, float z2, List<Entity> entityListToChange) {
        BlockMapSlot thisSlot = slot.init(x1, y1, z1);
        BlockMapSlot otherSlot = slot2.init(x2, y2, z2);

        for (int i = BlockMapSlot.getXSlot(thisSlot) - 1; i <= BlockMapSlot.getXSlot(otherSlot) + 1; ++i) {
            for (int j = BlockMapSlot.getYSlot(thisSlot) - 1; j <= BlockMapSlot.getYSlot(otherSlot) + 1; ++j) {
                for (int k = BlockMapSlot.getZSlot(thisSlot) - 1; k <= BlockMapSlot.getZSlot(otherSlot) + 1; ++k) {
                    if (i >= 0 && j >= 0 && k >= 0 && i < width && j < depth && k < height) {
                        for (Entity theEntity : entityGrid[(k * depth + j) * width + i]) {
                            if (theEntity != entity && theEntity.intersects(x1, y1, z1, x2, y2, z2)) {
                                entityListToChange.add(theEntity);
                            }
                        }
                    }
                }
            }
        }

        return entityListToChange;
    }

    public void insert(Entity entity) {
        all.add(entity);
        slot.init(entity.x, entity.y, entity.z).add(entity);
        entity.xOld = entity.x;
        entity.yOld = entity.y;
        entity.zOld = entity.z;
        entity.blockMap = this;
    }

    public void moved(Entity entity) {
        BlockMapSlot var2 = slot.init(entity.xOld, entity.yOld, entity.zOld);
        BlockMapSlot var3 = slot2.init(entity.x, entity.y, entity.z);
        if (!var2.equals(var3)) {
            var2.remove(entity);
            var3.add(entity);
            entity.xOld = entity.x;
            entity.yOld = entity.y;
            entity.zOld = entity.z;
        }
    }

    public void remove(Entity entity) {
        slot.init(entity.xOld, entity.yOld, entity.zOld).remove(entity);
        all.remove(entity);
    }

    public void removeAllNonCreativeModeEntities() {
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < depth; ++y) {
                for (int z = 0; z < height; ++z) {
                    List<?> entitySlotInGrid = entityGrid[(z * depth + y) * width + x];

                    for (int i = 0; i < entitySlotInGrid.size(); ++i) {
                        if (!((Entity) entitySlotInGrid.get(i)).isCreativeModeAllowed()) {
                            entitySlotInGrid.remove(i--);
                        }
                    }
                }
            }
        }

    }

    public void render(Vector3f playerVector, Frustum frustum, TextureManager textureManager, float delta) {
        for (int x = 0; x < width; ++x) {
            float var6 = (x << 4) - 2;
            float var7 = (x + 1 << 4) + 2;

            for (int y = 0; y < depth; ++y) {
                float var9 = (y << 4) - 2;
                float var10 = (y + 1 << 4) + 2;

                for (int z = 0; z < height; ++z) {
                    List<?> entitySlotInGrid = entityGrid[(z * depth + y) * width + x];
                    if (!entitySlotInGrid.isEmpty()) {
                        float var13 = (z << 4) - 2;
                        float var14 = (z + 1 << 4) + 2;
                        if (frustum.isBoxInFrustum(var6, var9, var13, var7, var10, var14)) {
                            float var16 = var14;
                            float var17 = var10;
                            float var15 = var7;
                            var14 = var13;
                            var13 = var9;
                            float var18 = var6;
                            Frustum var19 = frustum;
                            int var20 = 0;

                            boolean var10000;
                            while (true) {
                                if (var20 >= 6) {
                                    var10000 = true;
                                    break;
                                }

                                if (var19.frustum[var20][0] * var18 + var19.frustum[var20][1]
                                        * var13 + var19.frustum[var20][2] * var14
                                        + var19.frustum[var20][3] <= 0F) {
                                    var10000 = false;
                                    break;
                                }

                                if (var19.frustum[var20][0] * var15 + var19.frustum[var20][1]
                                        * var13 + var19.frustum[var20][2] * var14
                                        + var19.frustum[var20][3] <= 0F) {
                                    var10000 = false;
                                    break;
                                }

                                if (var19.frustum[var20][0] * var18 + var19.frustum[var20][1]
                                        * var17 + var19.frustum[var20][2] * var14
                                        + var19.frustum[var20][3] <= 0F) {
                                    var10000 = false;
                                    break;
                                }

                                if (var19.frustum[var20][0] * var15 + var19.frustum[var20][1]
                                        * var17 + var19.frustum[var20][2] * var14
                                        + var19.frustum[var20][3] <= 0F) {
                                    var10000 = false;
                                    break;
                                }

                                if (var19.frustum[var20][0] * var18 + var19.frustum[var20][1]
                                        * var13 + var19.frustum[var20][2] * var16
                                        + var19.frustum[var20][3] <= 0F) {
                                    var10000 = false;
                                    break;
                                }

                                if (var19.frustum[var20][0] * var15 + var19.frustum[var20][1]
                                        * var13 + var19.frustum[var20][2] * var16
                                        + var19.frustum[var20][3] <= 0F) {
                                    var10000 = false;
                                    break;
                                }

                                if (var19.frustum[var20][0] * var18 + var19.frustum[var20][1]
                                        * var17 + var19.frustum[var20][2] * var16
                                        + var19.frustum[var20][3] <= 0F) {
                                    var10000 = false;
                                    break;
                                }

                                if (var19.frustum[var20][0] * var15 + var19.frustum[var20][1]
                                        * var17 + var19.frustum[var20][2] * var16
                                        + var19.frustum[var20][3] <= 0F) {
                                    var10000 = false;
                                    break;
                                }

                                ++var20;
                            }

                            boolean var21 = var10000;

                            for (Object anEntitySlotInGrid : entitySlotInGrid) {
                                Entity var22 = (Entity) anEntitySlotInGrid;
                                if (var22.shouldRender(playerVector)) {
                                    if (!var21) {
                                        AABB var24 = var22.boundingBox;
                                        if (!frustum.isBoxInFrustum(var24.maxX, var24.maxY, var24.maxZ,
                                                var24.minX, var24.minY, var24.minZ)) {
                                            continue;
                                        }
                                    }

                                    var22.render(textureManager, delta);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public void tickAll() {
        for (int var1 = 0; var1 < all.size(); ++var1) {
            Entity var2;
            (var2 = all.get(var1)).tick();
            if (var2.removed) {
                all.remove(var1--);
                slot.init(var2.xOld, var2.yOld, var2.zOld).remove(var2);
            } else {
                int var3 = (int) (var2.xOld / 16F);
                int var4 = (int) (var2.yOld / 16F);
                int var5 = (int) (var2.zOld / 16F);
                int var6 = (int) (var2.x / 16F);
                int var7 = (int) (var2.y / 16F);
                int var8 = (int) (var2.z / 16F);
                if (var3 != var6 || var4 != var7 || var5 != var8) {
                    moved(var2);
                }
            }
        }

    }
}
