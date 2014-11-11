package com.mojang.minecraft.level;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.mojang.util.ColorCache;
import com.mojang.minecraft.Entity;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.MovingObjectPosition;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.particle.ParticleManager;
import com.mojang.minecraft.physics.AABB;
import com.mojang.minecraft.render.LevelRenderer;
import com.mojang.minecraft.sound.AudioInfo;
import com.mojang.minecraft.sound.EntitySoundPos;
import com.mojang.minecraft.sound.LevelSoundPos;
import com.mojang.util.MathHelper;
import com.mojang.util.Vec3D;
import java.util.ArrayDeque;

public class Level implements Serializable {

    public static final int DEFAULT_CLOUD_COLOR = 16777215,
            DEFAULT_FOG_COLOR = 16777215,
            DEFAULT_SKY_COLOR = 10079487;

    public int width;
    public int length;
    public int height;
    public byte[] blocks;
    public String name;
    public String creator;
    public long createTime;
    public int xSpawn;
    public int ySpawn;
    public int zSpawn;
    public float rotSpawn;
    public Random random = new Random();
    public BlockMap blockMap;
    public Minecraft minecraft;
    public boolean creativeMode;
    public int cloudLevel = -1;
    public int waterLevel;
    public int skyColor;
    public int fogColor;
    public int cloudColor;
    public Entity player;
    public ParticleManager particleEngine;
    public Object font;
    public boolean growTrees;

    public ColorCache customShadowColor;
    public ColorCache customLightColor;
    static final ColorCache defaultShadowColor = new ColorCache(0.6F, 0.6F, 0.6F);
    static final ColorCache defaultLightColor = new ColorCache(1, 1, 1);

    public short[] desiredSpawn;
    int unprocessed;
    private final ArrayList<LevelRenderer> listeners = new ArrayList<>();
    private int[] blockers;
    private int randId;
    private final ArrayDeque<NextTickListEntry> tickList = new ArrayDeque<>();
    private boolean networkMode;
    private int tickCount;

    public Level() {
        randId = random.nextInt();
        networkMode = false;
        unprocessed = 0;
        tickCount = 0;
        growTrees = false;
    }

    public void addEntity(Entity entity) {
        blockMap.insert(entity);
        entity.setLevel(this);
    }

    public void addListener(LevelRenderer levelRenderer) {
        listeners.add(levelRenderer);
    }

    public void addToTickNextTick(int x, int y, int z, int tile) {
        if (!networkMode) {
            NextTickListEntry entry = new NextTickListEntry(x, y, z, tile);
            if (tile > 0) {
                z = Block.blocks[tile].getTickDelay();
                entry.ticks = z;
            }

            tickList.add(entry);
        }
    }

    public void calcLightDepths(int var1, int var2, int var3, int var4) {
        for (int x = var1; x < var1 + var3; ++x) {
            for (int z = var2; z < var2 + var4; ++z) {
                int var7 = blockers[x + z * width];

                int y = height - 1;
                while (y > 0 && !isLightBlocker(x, y, z)) {
                    --y;
                }

                blockers[x + z * width] = y;
                if (var7 != y) {
                    int var9 = var7 < y ? var7 : y;
                    var7 = var7 > y ? var7 : y;

                    for (y = 0; y < listeners.size(); ++y) {
                        listeners.get(y).queueChunks(x - 1, var9 - 1, z - 1, x + 1,
                                var7 + 1, z + 1);
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
                int x = (int) Math.floor(var1.x);
                int y = (int) Math.floor(var1.y);
                int z = (int) Math.floor(var1.z);
                int var9 = 1024;

                while (var9-- >= 0) {
                    if (Float.isNaN(var1.x) || Float.isNaN(var1.y) || Float.isNaN(var1.z)) {
                        return null;
                    }

                    if (x == var3 && y == var4 && z == var5) {
                        return null;
                    }

                    float var10 = 999F;
                    float var11 = 999F;
                    float var12 = 999F;
                    if (var3 > x) {
                        var10 = x + 1F;
                    }

                    if (var3 < x) {
                        var10 = x;
                    }

                    if (var4 > y) {
                        var11 = y + 1F;
                    }

                    if (var4 < y) {
                        var11 = y;
                    }

                    if (var5 > z) {
                        var12 = z + 1F;
                    }

                    if (var5 < z) {
                        var12 = z;
                    }

                    float var13 = 999F;
                    float var14 = 999F;
                    float var15 = 999F;
                    float var16 = var2.x - var1.x;
                    float var17 = var2.y - var1.y;
                    float var18 = var2.z - var1.z;
                    if (var10 != 999F) {
                        var13 = (var10 - var1.x) / var16;
                    }

                    if (var11 != 999F) {
                        var14 = (var11 - var1.y) / var17;
                    }

                    if (var12 != 999F) {
                        var15 = (var12 - var1.z) / var18;
                    }

                    byte var24;
                    if (var13 < var14 && var13 < var15) {
                        if (var3 > x) {
                            var24 = 4;
                        } else {
                            var24 = 5;
                        }

                        var1.x = var10;
                        var1.y += var17 * var13;
                        var1.z += var18 * var13;
                    } else if (var14 < var15) {
                        if (var4 > y) {
                            var24 = 0;
                        } else {
                            var24 = 1;
                        }

                        var1.x += var16 * var14;
                        var1.y = var11;
                        var1.z += var18 * var14;
                    } else {
                        if (var5 > z) {
                            var24 = 2;
                        } else {
                            var24 = 3;
                        }

                        var1.x += var16 * var15;
                        var1.y += var17 * var15;
                        var1.z = var12;
                    }

                    x = (int) Math.floor(var1.x);
                    if (var24 == 5) {
                        --x;
                    }

                    y = (int) Math.floor(var1.y);
                    if (var24 == 1) {
                        --y;
                    }

                    z = (int) Math.floor(var1.z);
                    if (var24 == 3) {
                        --z;
                    }

                    int var22 = getTile(x, y, z);
                    Block var21 = Block.blocks[var22];
                    if (var22 > 0 && var21.getLiquidType() == LiquidType.notLiquid) {
                        MovingObjectPosition var23;
                        if (var21.isCube()) {
                            if ((var23 = var21.clip(x, y, z, var1, var2)) != null) {
                                return var23;
                            }
                        } else if ((var23 = var21.clip(x, y, z, var1, var2)) != null) {
                            return var23;
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean containsAnyLiquid(AABB cuboid) {
        int xStart = (int) cuboid.maxX;
        int xEnd = (int) cuboid.minX + 1;
        int yStart = (int) cuboid.maxY;
        int yEnd = (int) cuboid.minY + 1;
        int zStart = (int) cuboid.maxZ;
        int zEnd = (int) cuboid.minZ + 1;
        if (cuboid.maxX < 0F) {
            --xStart;
        }

        if (cuboid.maxY < 0F) {
            --yStart;
        }

        if (cuboid.maxZ < 0F) {
            --zStart;
        }

        if (xStart < 0) {
            xStart = 0;
        }

        if (yStart < 0) {
            yStart = 0;
        }

        if (zStart < 0) {
            zStart = 0;
        }

        if (xEnd > width) {
            xEnd = width;
        }

        if (yEnd > height) {
            yEnd = height;
        }

        if (zEnd > length) {
            zEnd = length;
        }

        for (int x = xStart; x < xEnd; ++x) {
            for (int y = yStart; y < yEnd; ++y) {
                for (int z = zStart; z < zEnd; ++z) {
                    Block block = Block.blocks[getTile(x, y, z)];
                    if (block != null && block.getLiquidType() != LiquidType.notLiquid) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean containsBlock(AABB area, Block blockType) {
        int xMin = (int) area.maxX;
        int xMax = (int) area.minX + 1;
        int yMin = (int) area.maxY;
        int yMax = (int) area.minY + 1;
        int zMin = (int) area.maxZ;
        int zMax = (int) area.minZ + 1;
        if (area.maxX < 0F) {
            --xMin;
        }

        if (area.maxY < 0F) {
            --yMin;
        }

        if (area.maxZ < 0F) {
            --zMin;
        }

        if (xMin < 0) {
            xMin = 0;
        }

        if (yMin < 0) {
            yMin = 0;
        }

        if (zMin < 0) {
            zMin = 0;
        }

        if (xMax > width) {
            xMax = width;
        }

        if (yMax > height) {
            yMax = height;
        }

        if (zMax > length) {
            zMax = length;
        }

        for (int x = xMin; x < xMax; ++x) {
            for (int y = yMin; y < yMax; ++y) {
                for (int z = zMin; z < zMax; ++z) {
                    Block block = Block.blocks[getTile(x, y, z)];
                    if (block != null && block == blockType) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean containsLiquid(AABB var1, LiquidType var2) {
        int var3 = (int) var1.maxX;
        int var4 = (int) var1.minX + 1;
        int var5 = (int) var1.maxY;
        int var6 = (int) var1.minY + 1;
        int var7 = (int) var1.maxZ;
        int var8 = (int) var1.minZ + 1;
        if (var1.maxX < 0F) {
            --var3;
        }

        if (var1.maxY < 0F) {
            --var5;
        }

        if (var1.maxZ < 0F) {
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

        if (var6 > height) {
            var6 = height;
        }

        if (var8 > length) {
            var8 = length;
        }

        for (int x = var3; x < var4; ++x) {
            for (int y = var5; y < var6; ++y) {
                for (int z = var7; z < var8; ++z) {
                    Block var10;
                    if ((var10 = Block.blocks[getTile(x, y, z)]) != null
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
        int count = 0;

        for (int var3 = 0; var3 < blockMap.all.size(); ++var3) {
            Entity var4 = blockMap.all.get(var3);
            if (var1.isAssignableFrom(var4.getClass())) {
                ++count;
            }
        }

        return count;
    }

    public void explode(Entity var1, float var2, float var3, float var4, float var5) {
        int var6 = (int) (var2 - var5 - 1F);
        int var7 = (int) (var2 + var5 + 1F);
        int var8 = (int) (var3 - var5 - 1F);
        int var9 = (int) (var3 + var5 + 1F);
        int var10 = (int) (var4 - var5 - 1F);
        int var11 = (int) (var4 + var5 + 1F);

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
                    if (var12 >= 0 && var13 >= 0 && var14 >= 0 && var12 < width && var13 < height
                            && var14 < length
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
            if ((var15 = (var20 = (Entity) var18.get(var13)).distanceTo(var2, var3, var4) / var5) <= 1F) {
                var16 = 1F - var15;
                var20.hurt(var1, (int) (var16 * 15F + 1F));
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
            var4 = var1.nextInt(length / 2) + length / 4;
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

    public float getBrightness(int x, int y, int z) {
        return isLit(x, y, z) ? 1F : 0.6F;
    }

    public ColorCache getBrightnessColor(int x, int y, int z) {
        if (isLit(x, y, z)) {
            if (customLightColor != null) {
                return customLightColor;
            }
            return defaultLightColor;
        } else {
            if (customShadowColor != null) {
                return customShadowColor;
            }
            return defaultShadowColor;
        }
    }

    public float getCaveness(Entity var1) {
        float var2 = MathHelper.cos(-var1.yRot * (float) (Math.PI / 180D) + (float) Math.PI);
        float var3 = MathHelper.sin(-var1.yRot * (float) (Math.PI / 180D) + (float) Math.PI);
        float var4 = MathHelper.cos(-var1.xRot * (float) (Math.PI / 180D));
        float var5 = MathHelper.sin(-var1.xRot * (float) (Math.PI / 180D));
        float var6 = var1.x;
        float var7 = var1.y;
        float var21 = var1.z;
        float var8 = 1.6F;
        float var9 = 0F;
        float var10 = 0F;

        for (int var11 = 0; var11 <= 200; ++var11) {
            float var12 = ((float) var11 / (float) 200 - 0.5F) * 2F;
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

        if (var9 == 0F) {
            return 0F;
        } else {
            float var22;
            if ((var22 = var10 / var9 / 0.1F) > 1F) {
                var22 = 1F;
            }

            var22 = 1F - var22;
            return 1F - var22 * var22 * var22;
        }
    }

    public float getCaveness(float var1, float var2, float var3, float var4) {
        int var5 = (int) var1;
        int var14 = (int) var2;
        int var6 = (int) var3;
        float var7 = 0F;
        float var8 = 0F;

        for (int var9 = var5 - 6; var9 <= var5 + 6; ++var9) {
            for (int var10 = var6 - 6; var10 <= var6 + 6; ++var10) {
                if (isInBounds(var9, var14, var10) && !isSolidTile(var9, var14, var10)) {
                    float var11 = var9 + 0.5F - var1;

                    float var12 = var10 + 0.5F - var3;
                    float var13 = (float) (Math.atan2(var12, var11) - var4 * (float) Math.PI / 180F + (float) (Math.PI / 2F)); // 1.5707963705062866D, I suspect it meant pi / 2
                    while (var13 < -(float) Math.PI) {
                        var13 += (float) Math.PI * 2D;
                    }

                    while (var13 >= (float) Math.PI) {
                        var13 -= (float) Math.PI * 2D;
                    }

                    if (var13 < 0F) {
                        var13 = -var13;
                    }

                    var11 = MathHelper.sqrt(var11 * var11 + 4F + var12 * var12);
                    var11 = 1F / var11;
                    if (var13 > 1F) {
                        var11 = 0F;
                    }

                    if (var11 < 0F) {
                        var11 = 0F;
                    }

                    var8 += var11;
                    if (isLit(var9, var14, var10)) {
                        var7 += var11;
                    }
                }
            }
        }

        if (var8 == 0F) {
            return 0F;
        } else {
            return var7 / var8;
        }
    }

    public ArrayList<AABB> getCubes(AABB var1) {
        ArrayList<AABB> var2 = new ArrayList<>();
        int var3 = (int) var1.maxX;
        int var4 = (int) var1.minX + 1;
        int var5 = (int) var1.maxY;
        int var6 = (int) var1.minY + 1;
        int var7 = (int) var1.maxZ;
        int var8 = (int) var1.minZ + 1;
        if (var1.maxX < 0F) {
            --var3;
        }

        if (var1.maxY < 0F) {
            --var5;
        }

        if (var1.maxZ < 0F) {
            --var7;
        }
        for (; var3 < var4; ++var3) {
            for (int var9 = var5; var9 < var6; ++var9) {
                for (int var10 = var7; var10 < var8; ++var10) {
                    AABB var11;
                    if (var3 >= 0 && var9 >= 0 && var10 >= 0 && var3 < width && var9 < height
                            && var10 < length) {
                        Block var12 = Block.blocks[getTile(var3, var9, var10)];
                        if (var12 != null
                                && (var11 = var12.getCollisionBox(var3, var9, var10)) != null
                                && var1.intersectsInner(var11)) {
                            var2.add(var11);
                        }
                    } else if ((var3 < 0 || var9 < 0 || var10 < 0 || var3 >= width || var10 >= length)
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
        return getWaterLevel() - 2F;
    }

    public int getHighestTile(int x, int z) {
        int y = height;
        while ((getTile(x, y - 1, z) == 0 || Block.blocks[getTile(x, y - 1, z)].getLiquidType() != LiquidType.notLiquid) && y > 0) {
            --y;
        }
        return y;
    }

    public LiquidType getLiquid(int x, int y, int z) {
        int blockId = getTile(x, y, z);
        return blockId == 0
                ? LiquidType.notLiquid
                : Block.blocks[blockId].getLiquidType();
    }

    public Entity getPlayer() {
        return player;
    }

    public int getTile(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < width && y < height && z < length
                ? blocks[(y * length + z) * width + x] & 255
                : 0;
    }

    public float getWaterLevel() {
        return waterLevel;
    }

    public void initTransient() {
        if (blocks == null) {
            throw new RuntimeException("The level is corrupt!");
        } else {
            listeners.clear();
            blockers = new int[width * length];
            Arrays.fill(blockers, height);
            calcLightDepths(0, 0, width, length);
            random = new Random();
            randId = random.nextInt();
            tickList.clear();
            if (waterLevel == 0) {
                waterLevel = height / 2;
            }

            if (skyColor == 0) {
                skyColor = DEFAULT_SKY_COLOR;
            }

            if (fogColor == 0) {
                fogColor = DEFAULT_FOG_COLOR;
            }

            if (cloudColor == 0) {
                cloudColor = DEFAULT_CLOUD_COLOR;
            }

            if (xSpawn == 0 && ySpawn == 0 && zSpawn == 0) {
                findSpawn();
            }

            if (blockMap == null) {
                blockMap = new BlockMap(width, height, length);
            }

        }
    }

    public boolean isFree(AABB var1) {
        return blockMap.getEntities(null, var1).isEmpty();
    }

    public boolean isInBounds(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0
                && x < width && y < height && z < length;
    }

    public boolean isLightBlocker(int x, int y, int z) {
        Block block = Block.blocks[getTile(x, y, z)];
        return block != null && block.isOpaque();
    }

    public boolean isLit(int x, int y, int z) {
        return !(x >= 0 && y >= 0 && z >= 0 && x < width && y < height
                && z < length) || y >= blockers[x + z * width];
    }

    private boolean isSolid(float x, float y, float z) {
        int tile = getTile((int) x, (int) y, (int) z);
        return tile > 0 && Block.blocks[tile].isSolid();
    }

    public boolean isSolid(float x, float y, float z, float side) {
        // Checks the neighbouring blocks to see if they are solid
        return this.isSolid(x - side, y - side, z - side)
                || (this.isSolid(x - side, y - side, z + side)
                || this.isSolid(x - side, y + side, z - side)
                || this.isSolid(x - side, y + side, z + side)
                || this.isSolid(x + side, y - side, z - side)
                || (this.isSolid(x + side, y - side, z + side)
                || this.isSolid(x + side, y + side, z - side)
                || this.isSolid(x + side, y + side, z + side)));
    }

    public boolean isSolidTile(int x, int y, int z) {
        int tile = getTile(x, y, z);
        return tile > 0
                && Block.blocks[tile].isSolid();
    }

    public boolean isWater(int x, int y, int z) {
        int tile = getTile(x, y, z);
        return tile > 0
                && Block.blocks[tile].getLiquidType() == LiquidType.water;
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
                    if (var8 >= 0 && var6 >= 0 && var9 >= 0 && var8 < width && var6 < height
                            && var9 < length) {
                        if ((blocks[(var6 * length + var9) * width + var8] & 255) != 0) {
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
        } else if ((blocks[((var2 - 1) * length + var3) * width + var1] & 255) == Block.GRASS.id
                && var2 < height - var4 - 1) {
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

    public boolean netSetTileNoNeighborChange(int x, int y, int z, int tile) {
        if (x >= 0 && y >= 0 && z >= 0 && x < width && y < height && z < length) {
            if (tile == blocks[(y * length + z) * width + x]) {
                return false;
            } else {
                if (tile == 0
                        && (x == 0 || z == 0 || x == width - 1 || z == length - 1)
                        && y >= getGroundLevel() && y < getWaterLevel() && !networkMode) {
                    tile = Block.WATER.id;
                }

                byte var5 = blocks[(y * length + z) * width + x];
                blocks[(y * length + z) * width + x] = (byte) tile;
                if (var5 != 0) {
                    Block.blocks[var5].onRemoved(this, x, y, z);
                }

                if (tile != 0) {
                    Block.blocks[tile].onAdded(this, x, y, z);
                }

                calcLightDepths(x, z, 1, 1);

                for (tile = 0; tile < listeners.size(); ++tile) {
                    listeners.get(tile).queueChunks(x - 1, y - 1, z - 1, x + 1,
                            y + 1, z + 1);
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public void playSound(String var1, Entity var2, float var3, float var4, boolean footStep) {
        if (minecraft != null) {
            if (minecraft.soundPlayer == null || !minecraft.settings.sound) {
                return;
            }

            AudioInfo var6;
            if (var2.distanceToSqr(minecraft.player) < 1024F
                    && (var6 = minecraft.sound.getAudioInfo(var1, var3, var4)) != null) {
                minecraft.soundPlayer.play(var6, new EntitySoundPos(var2, minecraft.player));
            }
        }
    }

    public void playSound(String var1, float x, float y, float z, float var5, float var6) {
        if (minecraft != null) {
            if (minecraft.soundPlayer == null || !minecraft.settings.sound) {
                return;
            }

            AudioInfo audioInfo = minecraft.sound.getAudioInfo(var1, var5, var6);
            if (audioInfo != null) {
                minecraft.soundPlayer.play(audioInfo, new LevelSoundPos(x, y, z, minecraft.player));
            }
        }
    }

    public void removeAllNonCreativeModeEntities() {
        blockMap.removeAllNonCreativeModeEntities();
    }

    public void removeEntity(Entity entity) {
        blockMap.remove(entity);
    }

    public void removeListener(LevelRenderer levelRenderer) {
        listeners.remove(levelRenderer);
    }

    public void setData(int width, int height, int length, byte[] blockArray) {
        this.width = width;
        this.length = length;
        this.height = height;
        blocks = blockArray;
        blockers = new int[width * length];
        Arrays.fill(blockers, this.height);
        calcLightDepths(0, 0, width, length);

        for (width = 0; width < listeners.size(); ++width) {
            listeners.get(width).refresh();
        }

        tickList.clear();
        findSpawn();
        initTransient();
        System.gc();
    }

    public void setNetworkMode(boolean networkMode) {
        this.networkMode = networkMode;
    }

    public void setSpawnPos(int x, int y, int z, float rot) {
        xSpawn = x;
        ySpawn = y;
        zSpawn = z;
        rotSpawn = rot;
    }

    public boolean setTile(int x, int y, int z, int block) {
        if (networkMode) {
            return false;
        } else if (setTileNoNeighborChange(x, y, z, block)) {
            updateNeighborsAt(x, y, z, block);
            return true;
        } else {
            return false;
        }
    }

    public boolean setTileNoNeighborChange(int x, int y, int z, int side) {
        return !networkMode && netSetTileNoNeighborChange(x, y, z, side);
    }

    public boolean setTileNoUpdate(int x, int y, int z, int side) {
        if (x >= 0 && y >= 0 && z >= 0 && x < width && y < height && z < length) {
            if (side == blocks[(y * length + z) * width + x]) {
                return false;
            } else {
                blocks[(y * length + z) * width + x] = (byte) side;
                return true;
            }
        } else {
            return false;
        }
    }

    public void swap(int x0, int y0, int z0, int x1, int y1, int z1) {
        if (!networkMode) {
            int tile0 = getTile(x0, y0, z0);
            int tile1 = getTile(x1, y1, z1);
            setTileNoNeighborChange(x0, y0, z0, tile1);
            setTileNoNeighborChange(x1, y1, z1, tile0);
            updateNeighborsAt(x0, y0, z0, tile1);
            updateNeighborsAt(x1, y1, z1, tile0);
        }
    }

    public void tick() {
        ++tickCount;
        int var1 = 1;
        int var2 = 1;

        while (1 << var1 < width) {
            ++var1;
        }

        while (1 << var2 < length) {
            ++var2;
        }

        if (tickCount % 5 == 0) {
            processTickList();
        }

        unprocessed += width * length * height;
        int var6 = unprocessed / 200;
        unprocessed -= var6 * 200;

        int var3 = length - 1;
        int var4 = width - 1;
        int var5 = height - 1;

        for (int i = 0; i < var6; ++i) {
            randId = randId * 3 + 1013904223;
            int var12 = randId >> 2;
            int x = var12 & var4;
            int z = var12 >> var1 & var3;
            int y = var12 >> var1 + var2 & var5;
            byte tile = blocks[(y * length + z) * width + x];
            if (Block.physics[tile]) {
                Block.blocks[tile].update(this, x, y, z, random);
            }
        }
    }

    private void processTickList() {
        // Do this every 5th tick
        int tickListSize = tickList.size();

        for (int i = 0; i < tickListSize; ++i) {
            NextTickListEntry nextEntity = tickList.removeFirst();
            if (nextEntity.ticks > 0) {
                --nextEntity.ticks;
                tickList.add(nextEntity);
            } else {
                byte block = blocks[(nextEntity.y * length + nextEntity.z) * width + nextEntity.x];
                if (isInBounds(nextEntity.x, nextEntity.y, nextEntity.z) && block == nextEntity.block && block > 0) {
                    Block.blocks[block].update(this, nextEntity.x, nextEntity.y, nextEntity.z, random);
                }
            }
        }
    }

    public void tickEntities() {
        blockMap.tickAll();
    }

    public void updateNeighborsAt(int x, int y, int z, int side) {
        updateTile(x - 1, y, z, side);
        updateTile(x + 1, y, z, side);
        updateTile(x, y - 1, z, side);
        updateTile(x, y + 1, z, side);
        updateTile(x, y, z - 1, side);
        updateTile(x, y, z + 1, side);
    }

    private void updateTile(int x, int y, int z, int side) {
        if (x >= 0 && y >= 0 && z >= 0 && x < width && y < height && z < length) {
            Block var5;
            if ((var5 = Block.blocks[blocks[(y * length + z) * width + x]]) != null) {
                var5.onNeighborChange(this, x, y, z, side);
            }
        }
    }
}
