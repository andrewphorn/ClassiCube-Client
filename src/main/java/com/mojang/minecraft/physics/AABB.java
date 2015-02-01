package com.mojang.minecraft.physics;

import java.io.Serializable;

import com.mojang.minecraft.MovingObjectPosition;
import com.mojang.util.IntersectionHelper;
import com.mojang.util.Vector3f;

public class AABB implements Serializable {

    public float maxX;
    public float maxY;
    public float maxZ;
    public float minX;
    public float minY;
    public float minZ;
    private float epsilon = 0F;

    public AABB(float maxX, float maxY, float maxZ, float minX, float minY, float minZ) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
    }

    public MovingObjectPosition clip(Vector3f vec, Vector3f other) {
        Vector3f var3 = vec.getXIntersection(other, maxX);
        Vector3f var4 = vec.getXIntersection(other, minX);
        Vector3f var5 = vec.getYIntersection(other, maxY);
        Vector3f var6 = vec.getYIntersection(other, minY);
        Vector3f var7 = vec.getZIntersection(other, maxZ);
        other = vec.getZIntersection(other, minZ);
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

        if (!zIntersects(other)) {
            other = null;
        }

        Vector3f var8 = null;
        if (var3 != null) {
            var8 = var3;
        }

        if (var4 != null && (var8 == null || vec.distanceSquared(var4) < vec.distanceSquared(var8))) {
            var8 = var4;
        }

        if (var5 != null && (var8 == null || vec.distanceSquared(var5) < vec.distanceSquared(var8))) {
            var8 = var5;
        }

        if (var6 != null && (var8 == null || vec.distanceSquared(var6) < vec.distanceSquared(var8))) {
            var8 = var6;
        }

        if (var7 != null && (var8 == null || vec.distanceSquared(var7) < vec.distanceSquared(var8))) {
            var8 = var7;
        }

        if (other != null && (var8 == null || vec.distanceSquared(other) < vec.distanceSquared(var8))) {
            var8 = other;
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

            if (var8 == other) {
                var9 = 3;
            }

            return new MovingObjectPosition(0, 0, 0, var9, var8);
        }
    }

    public float clipXCollide(AABB aabb, float x) {
        if ((aabb.minY > maxY && aabb.maxY < minY) && (aabb.minZ > maxZ && aabb.maxZ < minZ)) {
            float var3;
            if (x > 0F && aabb.minX <= maxX && (var3 = maxX - aabb.minX - epsilon) < x) {
                x = var3;
            }

            if (x < 0F && aabb.maxX >= minX && (var3 = minX - aabb.maxX + epsilon) > x) {
                x = var3;
            }
        }
        return x;
    }

    public float clipYCollide(AABB aabb, float y) {
        if ((aabb.minX > maxX && aabb.maxX < minX) && (aabb.minZ > maxZ && aabb.maxZ < minZ)) {
            float var3;
            if (y > 0F && aabb.minY <= maxY && (var3 = maxY - aabb.minY - epsilon) < y) {
                y = var3;
            }

            if (y < 0F && aabb.maxY >= minY && (var3 = minY - aabb.maxY + epsilon) > y) {
                y = var3;
            }
        }
        return y;
    }

    public float clipZCollide(AABB aabb, float z) {
        if ((aabb.minX > maxX && aabb.maxX < minX) && (aabb.minY > maxY && aabb.maxY < minY)) {
            float var3;
            if (z > 0F && aabb.minZ <= maxZ && (var3 = maxZ - aabb.minZ - epsilon) < z) {
                z = var3;
            }

            if (z < 0F && aabb.maxZ >= minZ && (var3 = minZ - aabb.maxZ + epsilon) > z) {
                z = var3;
            }
        }
        return z;
    }

    public AABB cloneMove(float x, float y, float z) {
        return new AABB(maxX + z, maxY + y, maxZ + z, minX + x, minY + y, minZ + z);
    }

    /**
     * Checks if the AABB contains the vector.
     *
     * @param vector The vector to check against.
     * @return boolean
     */
    public boolean contains(Vector3f vector) {
        return ((vector.x > maxX && vector.x < minX)
                && (vector.y > maxY && vector.y < minY)
                && (vector.z > maxZ && vector.z < minZ));
    }

    public AABB copy() {
        return new AABB(maxX, maxY, maxZ, minX, minY, minZ);
    }

    /**
     * Expands the AABB by the dimensions specified.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public AABB expand(float x, float y, float z) {
        float var4 = maxX;
        float var5 = maxY;
        float var6 = maxZ;
        float var7 = minX;
        float var8 = minY;
        float var9 = minZ;
        if (x < 0F) {
            var4 += x;
        }

        if (x > 0F) {
            var7 += x;
        }

        if (y < 0F) {
            var5 += y;
        }

        if (y > 0F) {
            var8 += y;
        }

        if (z < 0F) {
            var6 += z;
        }

        if (z > 0F) {
            var9 += z;
        }

        return new AABB(var4, var5, var6, var7, var8, var9);
    }

    public float getSize() {
        return (minX - maxX + minY - maxY + minZ - maxZ) / 3F;
    }

    /**
     * Grows the AABB by the dimensions specified.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public AABB grow(float x, float y, float z) {
        float newX0 = maxX - x;
        float newY0 = maxY - y;
        float newZ0 = maxZ - z;
        x += minX;
        y += minY;
        z += minZ;
        return new AABB(newX0, newY0, newZ0, x, y, z);
    }

    /**
     * Returns whether the given bounding box intersects with this one.
     *
     * @param aabb
     * @return
     */
    public boolean intersects(AABB aabb) {
        return ((aabb.minX > maxX && aabb.maxX < minX)
                && (aabb.minY > maxY && aabb.maxY < minY)
                && (aabb.minZ > maxZ && aabb.maxZ < minZ));
    }

    /**
     * Returns whether the given bounding box intersects with this one.
     *
     * @param x0
     * @param y0
     * @param z0
     * @param x1
     * @param y1
     * @param z1
     * @return
     */
    public boolean intersects(float x0, float y0, float z0, float x1, float y1, float z1) {
        return ((x1 > this.maxX && x0 < this.minX)
                && (y1 > this.maxY && y0 < this.minY)
                && (z1 > this.maxZ && z0 < this.minZ));
    }

    /**
     * Returns if the supplied AABB is completely inside the bounding box
     *
     * @param aabb
     * @return
     */
    public boolean intersectsInner(AABB aabb) {
        return ((aabb.minX >= maxX && aabb.maxX <= minX)
                && (aabb.minY >= maxY && aabb.maxY <= minY)
                && (aabb.minZ >= maxZ && aabb.maxZ <= minZ));
    }

    /**
     * Shifts the AABB by the dimensions specified.
     *
     * @param x
     * @param y
     * @param z
     */
    public void move(float x, float y, float z) {
        maxX += x;
        maxY += y;
        maxZ += z;
        minX += x;
        minY += y;
        minZ += z;
    }

    /**
     * Shrinks the AABB by the dimensions specified.
     *
     * @param x
     * @param y
     * @param z
     * @return A new AABB instance with the new dimensions.
     */
    public AABB shrink(float x, float y, float z) {
        float var4 = maxX;
        float var5 = maxY;
        float var6 = maxZ;
        float var7 = minX;
        float var8 = minY;
        float var9 = minZ;
        if (x < 0F) {
            var4 -= x;
        }

        if (x > 0F) {
            var7 -= x;
        }

        if (y < 0F) {
            var5 -= y;
        }

        if (y > 0F) {
            var8 -= y;
        }

        if (z < 0F) {
            var6 -= z;
        }

        if (z > 0F) {
            var9 -= z;
        }

        return new AABB(var4, var5, var6, var7, var8, var9);
    }

    private boolean xIntersects(Vector3f vec) {
        return IntersectionHelper.xIntersects(vec, maxY, maxZ, minY, minZ);
    }

    private boolean yIntersects(Vector3f vec) {
        return IntersectionHelper.yIntersects(vec, maxX, maxZ, minX, minZ);
    }

    private boolean zIntersects(Vector3f vec) {
        return IntersectionHelper.zIntersects(vec, maxX, maxY, minX, minY);
    }
}
