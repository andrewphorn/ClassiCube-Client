package com.mojang.util;

public final class Vector3f {

    public float x;
    public float y;
    public float z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final Vector3f add(float otherX, float otherY, float otherZ) {
        return new Vector3f(x + otherX, y + otherY, z + otherZ);
    }

    public final float distance(Vector3f other) {
        float xDiff = other.x - x;
        float yDiff = other.y - y;
        float zDiff = other.z - z;
        return MathHelper.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
    }

    public final float distanceSquared(Vector3f other) {
        float xDiff = other.x - x;
        float yDiff = other.y - y;
        float zDiff = other.z - z;
        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
    }

    public final Vector3f getXIntersection(Vector3f other, float xAxis) {
        float xDiff = other.x - x;
        float yDiff = other.y - y;
        float zDiff = other.z - z;
        return xDiff * xDiff < 1.0E-7F ? null
                : (xAxis = (xAxis - x) / xDiff) >= 0F && xAxis <= 1F ? new Vector3f(x + xDiff * xAxis,
                y + yDiff * xAxis, z + zDiff * xAxis) : null;
    }

    public final Vector3f getYIntersection(Vector3f other, float yAxis) {
        float xDiff = other.x - x;
        float yDiff = other.y - y;
        float zDiff = other.z - z;
        return yDiff * yDiff < 1.0E-7F ? null
                : (yAxis = (yAxis - y) / yDiff) >= 0F && yAxis <= 1F ? new Vector3f(x + xDiff * yAxis,
                y + yDiff * yAxis, z + zDiff * yAxis) : null;
    }

    public final Vector3f getZIntersection(Vector3f other, float zAxis) {
        float xDiff = other.x - x;
        float yDiff = other.y - y;
        float zDiff = other.z - z;
        return zDiff * zDiff < 1.0E-7F ? null
                : (zAxis = (zAxis - z) / zDiff) >= 0F && zAxis <= 1F ? new Vector3f(x + xDiff * zAxis,
                y + yDiff * zAxis, z + zDiff * zAxis) : null;
    }

    public final Vector3f normalize() {
        float dist = MathHelper.sqrt(x * x + y * y + z * z);
        return new Vector3f(x / dist, y / dist, z / dist);
    }

    public final Vector3f subtract(Vector3f other) {
        return new Vector3f(x - other.x, y - other.y, z - other.z);
    }

    @Override
    public final String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
