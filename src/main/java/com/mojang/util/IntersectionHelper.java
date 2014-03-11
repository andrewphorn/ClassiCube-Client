package com.mojang.util;

/**
 * Helper class for determining intersections.
 *
 * @author tyteen4a03
 */

public class IntersectionHelper {
    /**
     * Returns whether the given YZ plane intersects the vector.
     * @param vec
     * @param y0
     * @param z0
     * @param y1
     * @param z1
     * @return
     */
    public static boolean xIntersects(Vec3D vec, float y0, float z0, float y1, float z1) {
        return vec != null && vec.y >= y0 && vec.y <= y1 && vec.z >= z0 && vec.z <= z1;
    }

    /**
     * Returns whether the given XZ plane intersects the vector.
     * @param vec
     * @param x0
     * @param z0
     * @param x1
     * @param z1
     * @return
     */
    public static boolean yIntersects(Vec3D vec, float x0, float z0, float x1, float z1) {
        return vec != null && vec.x >= x0 && vec.x <= x1 && vec.z >= z0 && vec.z <= z1;
    }

    /**
     * Returns whether the given XZ plane intersects the vector.
     * @param vec
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @return
     */
    public static boolean zIntersects(Vec3D vec, float x0, float y0, float x1, float y1) {
        return vec != null && vec.x >= x0 && vec.x <= x1 && vec.y >= y0 && vec.y <= y1;
    }
}
