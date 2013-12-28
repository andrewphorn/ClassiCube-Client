package com.mojang.minecraft.model;

import com.mojang.util.MathHelper;

public final class Vec3D {

	public float x;
	public float y;
	public float z;

	public Vec3D(float var1, float var2, float var3) {
		x = var1;
		y = var2;
		z = var3;
	}

	public final Vec3D add(float var1, float var2, float var3) {
		return new Vec3D(x + var1, y + var2, z + var3);
	}

	public final float distance(Vec3D var1) {
		float var2 = var1.x - x;
		float var3 = var1.y - y;
		float var4 = var1.z - z;
		return MathHelper.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
	}

	public final float distanceSquared(Vec3D var1) {
		float var2 = var1.x - x;
		float var3 = var1.y - y;
		float var4 = var1.z - z;
		return var2 * var2 + var3 * var3 + var4 * var4;
	}

	public final Vec3D getXIntersection(Vec3D var1, float var2) {
		float var3 = var1.x - x;
		float var4 = var1.y - y;
		float var5 = var1.z - z;
		return var3 * var3 < 1.0E-7F ? null
				: (var2 = (var2 - x) / var3) >= 0.0F && var2 <= 1.0F ? new Vec3D(x + var3 * var2, y
						+ var4 * var2, z + var5 * var2) : null;
	}

	public final Vec3D getYIntersection(Vec3D var1, float var2) {
		float var3 = var1.x - x;
		float var4 = var1.y - y;
		float var5 = var1.z - z;
		return var4 * var4 < 1.0E-7F ? null
				: (var2 = (var2 - y) / var4) >= 0.0F && var2 <= 1.0F ? new Vec3D(x + var3 * var2, y
						+ var4 * var2, z + var5 * var2) : null;
	}

	public final Vec3D getZIntersection(Vec3D var1, float var2) {
		float var3 = var1.x - x;
		float var4 = var1.y - y;
		float var5;
		return (var5 = var1.z - z) * var5 < 1.0E-7F ? null : (var2 = (var2 - z) / var5) >= 0.0F
				&& var2 <= 1.0F ? new Vec3D(x + var3 * var2, y + var4 * var2, z + var5 * var2)
				: null;
	}

	public final Vec3D normalize() {
		float var1 = MathHelper.sqrt(x * x + y * y + z * z);
		return new Vec3D(x / var1, y / var1, z / var1);
	}

	public final Vec3D subtract(Vec3D var1) {
		return new Vec3D(x - var1.x, y - var1.y, z - var1.z);
	}

	@Override
	public final String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
