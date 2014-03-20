package com.mojang.minecraft.level.tile;

import com.mojang.util.ColorCache;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.physics.AABB;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.util.MathHelper;

public final class FireBlock extends Block {

    protected FireBlock(int id) {
        super(id);
        float var3 = 0.4F;
        setBounds(0.5F - var3, 0F, 0.5F - var3, var3 + 0.5F, var3 * 2F, var3 + 0.5F);
    }

    @Override
    protected final ColorCache getBrightness(Level level, int x, int y, int z) {
        return new ColorCache(255F / 255F, 255F / 255F, 255F / 255F);
    }

    @Override
    public AABB getCollisionBox(int x, int y, int z) {
        return null;
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
    public final boolean render(Level level, int x, int y, int z, ShapeRenderer shapeRenderer) {
        ColorCache var6 = getBrightness(level, x, y, z);
        shapeRenderer.color(var6.R, var6.G, var6.B);
        this.render(shapeRenderer, x, y, z);
        return true;
    }

    private void render(ShapeRenderer var1, float var2, float var3, float var4) {
        int var15;
        int var5 = (var15 = getTextureId(15)) % 16 << 4;
        int var6 = var15 / 16 << 4;
        float var16 = var5 / 256F;
        float var17 = (var5 + 15.99F) / 256F;
        float var7 = var6 / 256F;
        float var18 = (var6 + 15.99F) / 256F;

        for (int var8 = 0; var8 < 2; ++var8) {
            float var9 = (float) (MathHelper.sin(var8 * (float) Math.PI / 2F + 0.7853982F) * 0.5D);
            float var10 = (float) (MathHelper.cos(var8 * (float) Math.PI / 2F + 0.7853982F) * 0.5D);
            float var11 = var2 + 0.5F - var9;
            var9 += var2 + 0.5F;
            float var13 = var3 + 1F;
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
    public final void renderFullBrightness(ShapeRenderer shapeRenderer) {
        shapeRenderer.color(1F, 1F, 1F);
        this.render(shapeRenderer, -2, 0F, 0F);
    }

    @Override
    public final void renderPreview(ShapeRenderer var1) {
        var1.normal(0F, 1F, 0F);
        var1.begin();
        this.render(var1, 0F, 0.4F, -0.3F);
        var1.end();
    }

}
