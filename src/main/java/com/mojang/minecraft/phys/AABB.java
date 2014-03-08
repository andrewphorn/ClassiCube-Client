package com.mojang.minecraft.phys;

import java.io.Serializable;

import com.mojang.minecraft.MovingObjectPosition;
import com.mojang.util.Vec3D;

public class AABB implements Serializable {

    public static final long serialVersionUID = 0L;
    private float epsilon = 0.0F;
    public float x0;
    public float y0;
    public float z0;
    public float x1;
    public float y1;
    public float z1;

    public AABB(float var1, float var2, float var3, float var4, float var5, float var6) {
        x0 = var1;
        y0 = var2;
        z0 = var3;
        x1 = var4;
        y1 = var5;
        z1 = var6;
    }

    public MovingObjectPosition clip(Vec3D var1, Vec3D var2) {
        Vec3D var3 = var1.getXIntersection(var2, x0);
        Vec3D var4 = var1.getXIntersection(var2, x1);
        Vec3D var5 = var1.getYIntersection(var2, y0);
        Vec3D var6 = var1.getYIntersection(var2, y1);
        Vec3D var7 = var1.getZIntersection(var2, z0);
        var2 = var1.getZIntersection(var2, z1);
        if (!xIntersects(var3)) {
            var3 = null;
        }

        if (!xIntersects(var4)) {
            var4 = null;
        }

        if (!yIntersects(var5)) {
            var5 = null;
        }

        if (!yIntersects(var6)) {
            var6 = null;
        }

        if (!zIntersects(var7)) {
            var7 = null;
        }

        if (!zIntersects(var2)) {
            var2 = null;
        }

        Vec3D var8 = null;
        if (var3 != null) {
            var8 = var3;
        }

        if (var4 != null
                && (var8 == null || var1.distanceSquared(var4) < var1.distanceSquared(var8))) {
            var8 = var4;
        }

        if (var5 != null
                && (var8 == null || var1.distanceSquared(var5) < var1.distanceSquared(var8))) {
            var8 = var5;
        }

        if (var6 != null
                && (var8 == null || var1.distanceSquared(var6) < var1.distanceSquared(var8))) {
            var8 = var6;
        }

        if (var7 != null
                && (var8 == null || var1.distanceSquared(var7) < var1.distanceSquared(var8))) {
            var8 = var7;
        }

        if (var2 != null
                && (var8 == null || var1.distanceSquared(var2) < var1.distanceSquared(var8))) {
            var8 = var2;
        }

        if (var8 == null) {
            return null;
        } else {
            byte var9 = -1;
            if (var8 == var3) {
                var9 = 4;
            }

            if (var8 == var4) {
                var9 = 5;
            }

            if (var8 == var5) {
                var9 = 0;
            }

            if (var8 == var6) {
                var9 = 1;
            }

            if (var8 == var7) {
                var9 = 2;
            }

            if (var8 == var2) {
                var9 = 3;
            }

            return new MovingObjectPosition(0, 0, 0, var9, var8);
        }
    }

    public float clipXCollide(AABB var1, float var2) {
        if (var1.y1 > y0 && var1.y0 < y1) {
            if (var1.z1 > z0 && var1.z0 < z1) {
                float var3;
                if (var2 > 0.0F && var1.x1 <= x0 && (var3 = x0 - var1.x1 - epsilon) < var2) {
                    var2 = var3;
                }

                if (var2 < 0.0F && var1.x0 >= x1 && (var3 = x1 - var1.x0 + epsilon) > var2) {
                    var2 = var3;
                }

                return var2;
            } else {
                return var2;
            }
        } else {
            return var2;
        }
    }

    public float clipYCollide(AABB var1, float var2) {
        if (var1.x1 > x0 && var1.x0 < x1) {
            if (var1.z1 > z0 && var1.z0 < z1) {
                float var3;
                if (var2 > 0.0F && var1.y1 <= y0 && (var3 = y0 - var1.y1 - epsilon) < var2) {
                    var2 = var3;
                }

                if (var2 < 0.0F && var1.y0 >= y1 && (var3 = y1 - var1.y0 + epsilon) > var2) {
                    var2 = var3;
                }

                return var2;
            } else {
                return var2;
            }
        } else {
            return var2;
        }
    }

    public float clipZCollide(AABB var1, float var2) {
        if (var1.x1 > x0 && var1.x0 < x1) {
            if (var1.y1 > y0 && var1.y0 < y1) {
                float var3;
                if (var2 > 0.0F && var1.z1 <= z0 && (var3 = z0 - var1.z1 - epsilon) < var2) {
                    var2 = var3;
                }

                if (var2 < 0.0F && var1.z0 >= z1 && (var3 = z1 - var1.z0 + epsilon) > var2) {
                    var2 = var3;
                }

                return var2;
            } else {
                return var2;
            }
        } else {
            return var2;
        }
    }

    public AABB cloneMove(float var1, float var2, float var3) {
        return new AABB(x0 + var3, y0 + var2, z0 + var3, x1 + var1, y1 + var2, z1 + var3);
    }

    public boolean contains(Vec3D var1) {
        return var1.x > x0 && var1.x < x1 ? var1.y > y0 && var1.y < y1 ? var1.z > z0 && var1.z < z1
                : false : false;
    }

    public AABB copy() {
        return new AABB(x0, y0, z0, x1, y1, z1);
    }

    public AABB expand(float var1, float var2, float var3) {
        float var4 = x0;
        float var5 = y0;
        float var6 = z0;
        float var7 = x1;
        float var8 = y1;
        float var9 = z1;
        if (var1 < 0.0F) {
            var4 += var1;
        }

        if (var1 > 0.0F) {
            var7 += var1;
        }

        if (var2 < 0.0F) {
            var5 += var2;
        }

        if (var2 > 0.0F) {
            var8 += var2;
        }

        if (var3 < 0.0F) {
            var6 += var3;
        }

        if (var3 > 0.0F) {
            var9 += var3;
        }

        return new AABB(var4, var5, var6, var7, var8, var9);
    }

    public float getSize() {
        float var1 = x1 - x0;
        float var2 = y1 - y0;
        float var3 = z1 - z0;
        return (var1 + var2 + var3) / 3.0F;
    }

    public AABB grow(float var1, float var2, float var3) {
        float var4 = x0 - var1;
        float var5 = y0 - var2;
        float var6 = z0 - var3;
        var1 += x1;
        var2 += y1;
        float var7 = z1 + var3;
        return new AABB(var4, var5, var6, var1, var2, var7);
    }

    public boolean intersects(AABB var1) {
        return var1.x1 > x0 && var1.x0 < x1 ? var1.y1 > y0 && var1.y0 < y1 ? var1.z1 > z0
                && var1.z0 < z1 : false : false;
    }

    public boolean intersects(float var1, float var2, float var3, float var4, float var5, float var6) {
        return var4 > x0 && var1 < x1 ? var5 > y0 && var2 < y1 ? var6 > z0 && var3 < z1 : false
                : false;
    }

    public boolean intersectsInner(AABB var1) {
        return var1.x1 >= x0 && var1.x0 <= x1 ? var1.y1 >= y0 && var1.y0 <= y1 ? var1.z1 >= z0
                && var1.z0 <= z1 : false : false;
    }

    public void move(float var1, float var2, float var3) {
        x0 += var1;
        y0 += var2;
        z0 += var3;
        x1 += var1;
        y1 += var2;
        z1 += var3;
    }

    public AABB shrink(float var1, float var2, float var3) {
        float var4 = x0;
        float var5 = y0;
        float var6 = z0;
        float var7 = x1;
        float var8 = y1;
        float var9 = z1;
        if (var1 < 0.0F) {
            var4 -= var1;
        }

        if (var1 > 0.0F) {
            var7 -= var1;
        }

        if (var2 < 0.0F) {
            var5 -= var2;
        }

        if (var2 > 0.0F) {
            var8 -= var2;
        }

        if (var3 < 0.0F) {
            var6 -= var3;
        }

        if (var3 > 0.0F) {
            var9 -= var3;
        }

        return new AABB(var4, var5, var6, var7, var8, var9);
    }

    private boolean xIntersects(Vec3D var1) {
        return var1 == null ? false : var1.y >= y0 && var1.y <= y1 && var1.z >= z0 && var1.z <= z1;
    }

    private boolean yIntersects(Vec3D var1) {
        return var1 == null ? false : var1.x >= x0 && var1.x <= x1 && var1.z >= z0 && var1.z <= z1;
    }

    private boolean zIntersects(Vec3D var1) {
        return var1 == null ? false : var1.x >= x0 && var1.x <= x1 && var1.y >= y0 && var1.y <= y1;
    }
}
