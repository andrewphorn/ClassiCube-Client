package com.mojang.minecraft.level.tile;

import java.util.Random;

import com.mojang.util.ColorCache;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.physics.AABB;
import com.mojang.minecraft.render.ShapeRenderer;

public class LiquidBlock extends Block {

    protected LiquidType type;
    protected int stillId;
    protected int movingId;

    protected LiquidBlock(int id, LiquidType liquidType) {
        super(id);
        type = liquidType;
        textureId = 14;
        if (liquidType == LiquidType.lava) {
            textureId = 30;
        }

        Block.liquid[id] = true;
        movingId = id;
        stillId = id + 1;
        setBounds(0F, -0.1F, 0F, 1F, 0.9F, 1F);
        setPhysics(true);
    }

    private boolean canFlow(Level var1, int var2, int var3, int var4) {
        if (type == LiquidType.water) {
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

    @Override
    public final boolean canRenderSide(Level level, int x, int y, int z, int side) {
        int var6 = level.getTile(x, y, z);

        return
                // Is there space to spread?
                x >= 0 && y >= 0 && z >= 0 && x < level.width && z < level.length
                        // Can we move?
                        && (var6 != movingId && var6 != stillId &&
                        // Is there air around us?
                        (side == 1 && (level.getTile(x - 1, y, z) == 0 || level.getTile(x + 1, y, z) == 0
                                || level.getTile(x, y, z - 1) == 0 || level.getTile(x, y, z + 1) == 0)
                                || super.canRenderSide(level, x, y, z, side)));
    }

    @Override
    public final void dropItems(Level level, int x, int y, int z, float dropProbability) {
    }

    private boolean flow(Level level, int x, int y, int z) {
        if (level.getTile(x, y, z) == 0) {
            if (!canFlow(level, x, y, z)) {
                return false;
            }

            if (level.setTile(x, y, z, movingId)) {
                level.addToTickNextTick(x, y, z, movingId);
                // TODO Actually return true here?
            }
        }

        return false;
    }

    @Override
    protected final ColorCache getBrightness(Level level, int x, int y, int z) {
        if (type == LiquidType.lava) {
            final ColorCache c = new ColorCache(0, 0, 0);
            c.R = 100F;
            c.G = 100F;
            c.B = 100F;
            return c;
        } else {
            return level.getBrightnessColor(x, y, z);
        }
    }

    @Override
    public AABB getCollisionBox(int x, int y, int z) {
        return null;
    }

    @Override
    public final int getDropCount() {
        return 0;
    }

    @Override
    public final LiquidType getLiquidType() {
        return type;
    }

    @Override
    public final int getRenderPass() {
        return type == LiquidType.water ? 1 : 0;
    }

    @Override
    public final int getTickDelay() {
        return type == LiquidType.lava ? 5 : 0;
    }

    @Override
    public final boolean isCube() {
        return false;
    }

    @Override
    public final boolean isOpaque() {
        return true;
    }

    @Override
    public final boolean isSolid() {
        return false;
    }

    @Override
    public final void onBreak(Level level, int x, int y, int z) {
    }

    @Override
    public void onNeighborChange(Level level, int x, int y, int z, int side) {
        if (side != 0) {
            LiquidType var6 = Block.blocks[side].getLiquidType();
            if (type == LiquidType.water && var6 == LiquidType.lava || var6 == LiquidType.water
                    && type == LiquidType.lava) {
                level.setTile(x, y, z, Block.OBSIDIAN.id);
                return;
            }
        }

        level.addToTickNextTick(x, y, z, side);
    }

    @Override
    public final void onPlace(Level level, int x, int y, int z) {
        level.addToTickNextTick(x, y, z, movingId);
    }

    @Override
    public final void renderInside(ShapeRenderer shapeRenderer, int x, int y, int z, int side) {
        super.renderInside(shapeRenderer, x, y, z, side);
        super.renderSide(shapeRenderer, x, y, z, side);
    }

    @Override
    public void update(Level level, int x, int y, int z, Random rand) {
        boolean var8 = false;

        boolean var6;
        do {
            --y;
            if (level.getTile(x, y, z) != 0 || !canFlow(level, x, y, z)) {
                break;
            }

            if (var6 = level.setTile(x, y, z, movingId)) {
                var8 = true;
            }
        } while (var6 && type != LiquidType.lava);

        ++y;
        if (type == LiquidType.water || !var8) {
            var8 = var8 | flow(level, x - 1, y, z) | flow(level, x + 1, y, z)
                    | flow(level, x, y, z - 1) | flow(level, x, y, z + 1);
        }

        if (!var8) {
            level.setTileNoUpdate(x, y, z, stillId);
        } else {
            level.addToTickNextTick(x, y, z, movingId);
        }

    }
}
