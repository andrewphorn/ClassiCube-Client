package com.mojang.minecraft.level.tile;

import java.util.Random;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.render.ShapeRenderer;

public class LiquidBlock extends Block {

    protected LiquidType type;
    protected int stillId;
    protected int movingId;

    protected LiquidBlock(int var1, LiquidType var2) {
        super(var1);
        type = var2;
        textureId = 14;
        if (var2 == LiquidType.lava) {
            textureId = 30;
        }

        Block.liquid[var1] = true;
        movingId = var1;
        stillId = var1 + 1;
        float var4 = 0.01F;
        float var3 = 0.1F;
        setBounds(var4 + 0.0F, 0.0F - var3 + var4, var4 + 0.0F, var4 + 1.0F, 1.0F - var3 + var4,
                var4 + 1.0F);
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
        int var6;
        return x >= 0 && y >= 0 && z >= 0 && x < level.width && z < level.length ? (var6 = level
                .getTile(x, y, z)) != movingId && var6 != stillId ? side == 1
                && (level.getTile(x - 1, y, z) == 0 || level.getTile(x + 1, y, z) == 0
                        || level.getTile(x, y, z - 1) == 0 || level.getTile(x, y, z + 1) == 0) ? true
                : super.canRenderSide(level, x, y, z, side)
                : false
                : false;
    }

    @Override
    public final void dropItems(Level var1, int var2, int var3, int var4, float var5) {
    }

    private boolean flow(Level var1, int var2, int var3, int var4) {
        if (var1.getTile(var2, var3, var4) == 0) {
            if (!canFlow(var1, var2, var3, var4)) {
                return false;
            }

            if (var1.setTile(var2, var3, var4, movingId)) {
                var1.addToTickNextTick(var2, var3, var4, movingId);
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
    public final void onBreak(Level var1, int var2, int var3, int var4) {
    }

    @Override
    public void onNeighborChange(Level var1, int var2, int var3, int var4, int var5) {
        if (var5 != 0) {
            LiquidType var6 = Block.blocks[var5].getLiquidType();
            if (type == LiquidType.water && var6 == LiquidType.lava || var6 == LiquidType.water
                    && type == LiquidType.lava) {
                var1.setTile(var2, var3, var4, Block.OBSIDIAN.id);
                return;
            }
        }

        var1.addToTickNextTick(var2, var3, var4, var5);
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
