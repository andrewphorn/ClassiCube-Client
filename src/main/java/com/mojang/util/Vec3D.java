package com.mojang.util;

public final class Vec3D {

    public float x;
    public float y;
    public float z;

    public Vec3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final Vec3D add(float otherX, float otherY, float otherZ) {
        return new Vec3D(x + otherX, y + otherY, z + otherZ);
    }

    public final float distance(Vec3D other) {
        float xDiff = other.x - x;
        float yDiff = other.y - y;
        float zDiff = other.z - z;
        return MathHelper.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
    }

    public final float distanceSquared(Vec3D other) {
        float xDiff = other.x - x;
        float yDiff = other.y - y;
        float zDiff = other.z - z;
        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
    }

    public final Vec3D getXIntersection(Vec3D other, float xAxis) {
        float xDiff = other.x - x;
        float yDiff = other.y - y;
        float zDiff = other.z - z;
        return xDiff * xDiff < 1.0E-7F ? null
                : (xAxis = (xAxis - x) / xDiff) >= 0F && xAxis <= 1F ? new Vec3D(x + xDiff * xAxis,
                        y + yDiff * xAxis, z + zDiff * xAxis) : null;
    }

    public final Vec3D getYIntersection(Vec3D other, float yAxis) {
        float xDiff = other.x - x;
        float yDiff = other.y - y;
        float zDiff = other.z - z;
        return yDiff * yDiff < 1.0E-7F ? null
                : (yAxis = (yAxis - y) / yDiff) >= 0F && yAxis <= 1F ? new Vec3D(x + xDiff * yAxis,
                        y + yDiff * yAxis, z + zDiff * yAxis) : null;
    }

    public final Vec3D getZIntersection(Vec3D other, float zAxis) {
        float xDiff = other.x - x;
        float yDiff = other.y - y;
        float zDiff = other.z - z;
        return zDiff * zDiff < 1.0E-7F ? null
                : (zAxis = (zAxis - z) / zDiff) >= 0F && zAxis <= 1F ? new Vec3D(x + xDiff * zAxis,
                        y + yDiff * zAxis, z + zDiff * zAxis) : null;
    }

    public final Vec3D normalize() {
        float dist = MathHelper.sqrt(x * x + y * y + z * z);
        return new Vec3D(x / dist, y / dist, z / dist);
    }

    public final Vec3D subtract(Vec3D other) {
        return new Vec3D(x - other.x, y - other.y, z - other.z);
    }

    @Override
    public final String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
