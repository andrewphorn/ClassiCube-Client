package com.mojang.minecraft.level.tile;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.MovingObjectPosition;
import com.mojang.util.Vec3D;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.render.ShapeRenderer;

public class BlockModelRenderer {
    public int textureId;

    public float x1;

    public float y1;
    public float z1;
    public float x2;
    public float y2;
    public float z2;

    public BlockModelRenderer(int id) {
        textureId = id;
        setBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean canRenderSide(int x, int y, int z, int side) {
        return true;
    }

    public final MovingObjectPosition clip(int var1, int var2, int var3, Vec3D var4, Vec3D var5) {
        var4 = var4.add(-var1, -var2, -var3);
        var5 = var5.add(-var1, -var2, -var3);
        Vec3D var6 = var4.getXIntersection(var5, x1);
        Vec3D var7 = var4.getXIntersection(var5, x2);
        Vec3D var8 = var4.getYIntersection(var5, y1);
        Vec3D var9 = var4.getYIntersection(var5, y2);
        Vec3D var10 = var4.getZIntersection(var5, z1);
        var5 = var4.getZIntersection(var5, z2);
        if (!xIntersects(var6)) {
            var6 = null;
        }

        if (!xIntersects(var7)) {
            var7 = null;
        }

        if (!yIntersects(var8)) {
            var8 = null;
        }

        if (!yIntersects(var9)) {
            var9 = null;
        }

        if (!zIntersects(var10)) {
            var10 = null;
        }

        if (!zIntersects(var5)) {
            var5 = null;
        }

        Vec3D var11 = null;
        if (var6 != null) {
            var11 = var6;
        }

        if (var7 != null && (var11 == null || var4.distance(var7) < var4.distance(var11))) {
            var11 = var7;
        }

        if (var8 != null && (var11 == null || var4.distance(var8) < var4.distance(var11))) {
            var11 = var8;
        }

        if (var9 != null && (var11 == null || var4.distance(var9) < var4.distance(var11))) {
            var11 = var9;
        }

        if (var10 != null && (var11 == null || var4.distance(var10) < var4.distance(var11))) {
            var11 = var10;
        }

        if (var5 != null && (var11 == null || var4.distance(var5) < var4.distance(var11))) {
            var11 = var5;
        }

        if (var11 == null) {
            return null;
        } else {
            byte var12 = -1;
            if (var11 == var6) {
                var12 = 4;
            }

            if (var11 == var7) {
                var12 = 5;
            }

            if (var11 == var8) {
                var12 = 0;
            }

            if (var11 == var9) {
                var12 = 1;
            }

            if (var11 == var10) {
                var12 = 2;
            }

            if (var11 == var5) {
                var12 = 3;
            }

            return new MovingObjectPosition(var1, var2, var3, var12, var11.add(var1, var2, var3));
        }
    }

    protected ColorCache getBrightness(int x, int y, int z) {
        return new ColorCache(1.0f, 1.0f, 1.0f);
    }

    public AABB getCollisionBox(int x, int y, int z) {
        AABB aabb = new AABB(x + x1, y + y1, z + z1, x + x2, y + y2, z + z2);
        ;

        return aabb;
    }

    public AABB getSelectionBox(int x, int y, int z) {
        AABB aabb = new AABB(x + x1, y + y1, z + z1, x + x2, y + y2, z + z2);
        ;

        return aabb;
    }

    protected int getTextureId(int texture) {
        return textureId;
    }

    public void renderFullbright(ShapeRenderer shapeRenderer) {
        float red = 0.5F;
        float green = 0.8F;
        float blue = 0.6F;

        shapeRenderer.color(red, red, red);
        renderInside(shapeRenderer, -2, 0, 0, 0);

        shapeRenderer.color(1.0F, 1.0F, 1.0F);
        renderInside(shapeRenderer, -2, 0, 0, 1);

        shapeRenderer.color(green, green, green);
        renderInside(shapeRenderer, -2, 0, 0, 2);

        shapeRenderer.color(green, green, green);
        renderInside(shapeRenderer, -2, 0, 0, 3);

        shapeRenderer.color(blue, blue, blue);
        renderInside(shapeRenderer, -2, 0, 0, 4);

        shapeRenderer.color(blue, blue, blue);
        renderInside(shapeRenderer, -2, 0, 0, 5);
    }

    public void renderInside(ShapeRenderer shapeRenderer, int x, int y, int z, int side) {
        int textureID1 = getTextureId(side);

        renderSide(shapeRenderer, x, y, z, side, textureID1);
    }

    public void renderPreview(ShapeRenderer var1) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(770, 768);
        var1.begin();

        for (int var2 = 0; var2 < 6; ++var2) {
            if (var2 == 0) {
                var1.normal(0.0F, 1.0F, 0.0F);
            }

            if (var2 == 1) {
                var1.normal(0.0F, -1.0F, 0.0F);
            }

            if (var2 == 2) {
                var1.normal(0.0F, 0.0F, 1.0F);
            }

            if (var2 == 3) {
                var1.normal(0.0F, 0.0F, -1.0F);
            }

            if (var2 == 4) {
                var1.normal(1.0F, 0.0F, 0.0F);
            }

            if (var2 == 5) {
                var1.normal(-1.0F, 0.0F, 0.0F);
            }

            renderInside(var1, 0, 0, 0, var2);
        }
        GL11.glDisable(GL11.GL_BLEND);
        var1.end();
    }

    // TODO.
    public void renderSide(ShapeRenderer var1, int var2, int var3, int var4, int var5) {

        int var6;
        float var7;
        float var8 = (var7 = (var6 = getTextureId(var5)) % 16 / 16.0F) + 0.0624375F;
        float var16;
        float var9 = (var16 = var6 / 16 / 16.0F) + 0.0624375F;
        float var10 = var2 + x1;
        float var14 = var2 + x2;
        float var11 = var3 + y1;
        float var15 = var3 + y2;
        float var12 = var4 + z1;
        float var13 = var4 + z2;
        if (var5 == 0) {
            var1.vertexUV(var14, var11, var13, var8, var9);
            var1.vertexUV(var14, var11, var12, var8, var16);
            var1.vertexUV(var10, var11, var12, var7, var16);
            var1.vertexUV(var10, var11, var13, var7, var9);
        }

        if (var5 == 1) {
            var1.vertexUV(var10, var15, var13, var7, var9);
            var1.vertexUV(var10, var15, var12, var7, var16);
            var1.vertexUV(var14, var15, var12, var8, var16);
            var1.vertexUV(var14, var15, var13, var8, var9);
        }

        if (var5 == 2) {
            var1.vertexUV(var10, var11, var12, var8, var9);
            var1.vertexUV(var14, var11, var12, var7, var9);
            var1.vertexUV(var14, var15, var12, var7, var16);
            var1.vertexUV(var10, var15, var12, var8, var16);
        }

        if (var5 == 3) {
            var1.vertexUV(var14, var15, var13, var8, var16);
            var1.vertexUV(var14, var11, var13, var8, var9);
            var1.vertexUV(var10, var11, var13, var7, var9);
            var1.vertexUV(var10, var15, var13, var7, var16);
        }

        if (var5 == 4) {
            var1.vertexUV(var10, var11, var13, var8, var9);
            var1.vertexUV(var10, var11, var12, var7, var9);
            var1.vertexUV(var10, var15, var12, var7, var16);
            var1.vertexUV(var10, var15, var13, var8, var16);
        }

        if (var5 == 5) {
            var1.vertexUV(var14, var15, var13, var7, var16);
            var1.vertexUV(var14, var15, var12, var8, var16);
            var1.vertexUV(var14, var11, var12, var8, var9);
            var1.vertexUV(var14, var11, var13, var7, var9);
        }

    }

    // TODO.
    public void renderSide(ShapeRenderer shapeRenderer, int x, int y, int z, int side, int textureID) {
        int var7 = textureID % 16 << 4;
        int var8 = textureID / 16 << 4;
        float var9 = var7 / 256.0F;
        float var17 = (var7 + 15.99F) / 256.0F;
        float var10 = var8 / 256.0F;
        float var11 = (var8 + 15.99F) / 256.0F;
        if (side >= 2 && textureID < 240) {
            if (y1 >= 0.0F && y2 <= 1.0F) {
                var10 = (var8 + y1 * 15.99F) / 256.0F;
                var11 = (var8 + y2 * 15.99F) / 256.0F;
            } else {
                var10 = var8 / 256.0F;
                var11 = (var8 + 15.99F) / 256.0F;
            }
        }

        float var16 = x + x1;
        float var14 = x + x2;
        float var18 = y + y1;
        float var15 = y + y2;
        float var12 = z + z1;
        float var13 = z + z2;
        if (side == 0) {
            shapeRenderer.vertexUV(var16, var18, var13, var9, var11);
            shapeRenderer.vertexUV(var16, var18, var12, var9, var10);
            shapeRenderer.vertexUV(var14, var18, var12, var17, var10);
            shapeRenderer.vertexUV(var14, var18, var13, var17, var11);
        } else if (side == 1) {
            shapeRenderer.vertexUV(var14, var15, var13, var17, var11);
            shapeRenderer.vertexUV(var14, var15, var12, var17, var10);
            shapeRenderer.vertexUV(var16, var15, var12, var9, var10);
            shapeRenderer.vertexUV(var16, var15, var13, var9, var11);
        } else if (side == 2) {
            shapeRenderer.vertexUV(var16, var15, var12, var17, var10);
            shapeRenderer.vertexUV(var14, var15, var12, var9, var10);
            shapeRenderer.vertexUV(var14, var18, var12, var9, var11);
            shapeRenderer.vertexUV(var16, var18, var12, var17, var11);
        } else if (side == 3) {
            shapeRenderer.vertexUV(var16, var15, var13, var9, var10);
            shapeRenderer.vertexUV(var16, var18, var13, var9, var11);
            shapeRenderer.vertexUV(var14, var18, var13, var17, var11);
            shapeRenderer.vertexUV(var14, var15, var13, var17, var10);
        } else if (side == 4) {
            shapeRenderer.vertexUV(var16, var15, var13, var17, var10);
            shapeRenderer.vertexUV(var16, var15, var12, var9, var10);
            shapeRenderer.vertexUV(var16, var18, var12, var9, var11);
            shapeRenderer.vertexUV(var16, var18, var13, var17, var11);
        } else if (side == 5) {
            shapeRenderer.vertexUV(var14, var18, var13, var9, var11);
            shapeRenderer.vertexUV(var14, var18, var12, var17, var11);
            shapeRenderer.vertexUV(var14, var15, var12, var17, var10);
            shapeRenderer.vertexUV(var14, var15, var13, var9, var10);
        }
    }

    protected void setBounds(float x1, float y1, float z1, float x2, float y2, float z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    private boolean xIntersects(Vec3D var1) {
        return var1 == null ? false : var1.y >= y1 && var1.y <= y2 && var1.z >= z1 && var1.z <= z2;
    }

    private boolean yIntersects(Vec3D var1) {
        return var1 == null ? false : var1.x >= x1 && var1.x <= x2 && var1.z >= z1 && var1.z <= z2;
    }

    private boolean zIntersects(Vec3D var1) {
        return var1 == null ? false : var1.x >= x1 && var1.x <= x2 && var1.y >= y1 && var1.y <= y2;
    }

}