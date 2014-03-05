package com.mojang.minecraft.level.tile;

import java.util.Random;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.util.MathHelper;

public class FlowerBlock extends Block {

    protected FlowerBlock(int var1) {
        super(var1);
        setPhysics(true);
        float var3 = 0.2F;
        setBounds(0.5F - var3, 0.0F, 0.5F - var3, var3 + 0.5F, var3 * 3.0F, var3 + 0.5F);
    }

    @Override
    public AABB getCollisionBox(int x, int y, int z) {
        return null;
    }

    @Override
    public final boolean isCube() {
        return false;
    }

    @Override
    public final boolean isOpaque() {
        return false;
    }

    @Override
    public final boolean isSolid() {
        return false;
    }

    @Override
    public final boolean render(Level var1, int var2, int var3, int var4, ShapeRenderer var5) {
        ColorCache var6 = var1.getBrightnessColor(var2, var3, var4);
        var5.color(var6.R, var6.G, var6.B);
        this.render(var5, var2, var3, var4);
        return true;
    }

    private void render(ShapeRenderer var1, float var2, float var3, float var4) {
        int var15;
        int var5 = (var15 = getTextureId(15)) % 16 << 4;
        int var6 = var15 / 16 << 4;
        float var16 = var5 / 256.0F;
        float var17 = (var5 + 15.99F) / 256.0F;
        float var7 = var6 / 256.0F;
        float var18 = (var6 + 15.99F) / 256.0F;

        for (int var8 = 0; var8 < 2; ++var8) {
            float var9 = (float) (MathHelper.sin(var8 * 3.1415927F / 2.0F + 0.7853982F) * 0.5D);
            float var10 = (float) (MathHelper.cos(var8 * 3.1415927F / 2.0F + 0.7853982F) * 0.5D);
            float var11 = var2 + 0.5F - var9;
            var9 += var2 + 0.5F;
            float var13 = var3 + 1.0F;
            float var14 = var4 + 0.5F - var10;
            var10 += var4 + 0.5F;
            var1.vertexUV(var11, var13, var14, var17, var7);
            var1.vertexUV(var9, var13, var10, var16, var7);
            var1.vertexUV(var9, var3, var10, var16, var18);
            var1.vertexUV(var11, var3, var14, var17, var18);
            var1.vertexUV(var9, var13, var10, var17, var7);
            var1.vertexUV(var11, var13, var14, var16, var7);
            var1.vertexUV(var11, var3, var14, var16, var18);
            var1.vertexUV(var9, var3, var10, var17, var18);
        }

    }

    @Override
    public final void renderFullbright(ShapeRenderer shapeRenderer) {
        shapeRenderer.color(1.0F, 1.0F, 1.0F);
        this.render(shapeRenderer, -2, 0.0F, 0.0F);
    }

    @Override
    public final void renderPreview(ShapeRenderer var1) {
        var1.normal(0.0F, 1.0F, 0.0F);
        var1.begin();
        this.render(var1, 0.0F, 0.4F, -0.3F);
        var1.end();
    }

    @Override
    public void update(Level level, int x, int y, int z, Random rand) {
        if (!level.growTrees) {
            int var6 = level.getTile(x, y - 1, z);
            if (id != 53 && !level.isLit(x, y, z) || var6 != DIRT.id && var6 != GRASS.id) {
                level.setTile(x, y, z, 0);
            }
        }
    }
}
