package com.mojang.minecraft.particle;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.util.MathHelper;

public class Particle extends Entity {

    private static final long serialVersionUID = 1L;
    protected int tex;
    protected float uo;
    protected float vo;
    protected int age = 0;
    protected int lifetime = 0;
    protected float size;
    protected float gravity;
    protected float rCol;
    protected float gCol;
    protected float bCol;

    public Particle(Level level, float var2, float var3, float var4, float var5, float var6,
            float var7) {
        super(level);
        setSize(0.2F, 0.2F);
        heightOffset = bbHeight / 2F;
        this.setPos(var2, var3, var4);
        rCol = gCol = bCol = 1F;
        xd = var5 + (float) (Math.random() * 2D - 1D) * 0.4F;
        yd = var6 + (float) (Math.random() * 2D - 1D) * 0.4F;
        zd = var7 + (float) (Math.random() * 2D - 1D) * 0.4F;
        float var8 = (float) (Math.random() + Math.random() + 1D) * 0.15F;
        var2 = MathHelper.sqrt(xd * xd + yd * yd + zd * zd);
        xd = xd / var2 * var8 * 0.4F;
        yd = yd / var2 * var8 * 0.4F + 0.1F;
        zd = zd / var2 * var8 * 0.4F;
        uo = (float) Math.random() * 3F;
        vo = (float) Math.random() * 3F;
        size = (float) (Math.random() * 0.5D + 0.5D);
        lifetime = (int) (4D / (Math.random() * 0.9D + 0.1D));
        age = 0;
        makeStepSound = false;
    }

    public int getParticleTexture() {
        return 0;
    }

    public void render(ShapeRenderer shapeRenderer, float var2, float var3, float var4, float var5,
            float var6, float var7) {
        float var8;
        float var9 = (var8 = tex % 16 / 16F) + 0.0624375F;
        float var10;
        float var11 = (var10 = tex / 16 / 16F) + 0.0624375F;
        float var12 = 0.1F * size;
        float var13 = xo + (x - xo) * var2;
        float var14 = yo + (y - yo) * var2;
        float var15 = zo + (z - zo) * var2;
        ColorCache var21 = getBrightnessColor();
        shapeRenderer.color(rCol * var21.R, gCol * var21.G, bCol * var21.B);
        shapeRenderer.vertexUV(var13 - var3 * var12 - var6 * var12, var14 - var4 * var12,
                var15 - var5 * var12 - var7 * var12, var8, var11);
        shapeRenderer.vertexUV(var13 - var3 * var12 + var6 * var12, var14 + var4 * var12,
                var15 - var5 * var12 + var7 * var12, var8, var10);
        shapeRenderer.vertexUV(var13 + var3 * var12 + var6 * var12, var14 + var4 * var12,
                var15 + var5 * var12 + var7 * var12, var9, var10);
        shapeRenderer.vertexUV(var13 + var3 * var12 - var6 * var12, var14 - var4 * var12,
                var15 + var5 * var12 - var7 * var12, var9, var11);
    }

    public Particle scale(float scale) {
        setSize(0.2F * scale, 0.2F * scale);
        size *= scale;
        return this;
    }

    public Particle setPower(float power) {
        xd *= power;
        yd = (yd - 0.1F) * power + 0.1F;
        zd *= power;
        return this;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime) {
            remove();
        }

        yd = (float) (yd - 0.04D * gravity);
        move(xd, yd, zd);
        xd *= 0.98F;
        yd *= 0.98F;
        zd *= 0.98F;
        if (onGround) {
            xd *= 0.7F;
            zd *= 0.7F;
        }

    }
}
