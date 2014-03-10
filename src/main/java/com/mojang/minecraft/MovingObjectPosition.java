package com.mojang.minecraft;

import com.mojang.util.Vec3D;

public class MovingObjectPosition {
	public boolean hasEntity;

	public int x;
	public int y;
	public int z;
	public int face;
	public Vec3D vec;

	public Entity entity;

	public MovingObjectPosition(Entity entity) {
		hasEntity = true;
		this.entity = entity;
	}

	public MovingObjectPosition(int x, int y, int z, int side, Vec3D blockPos) {
		hasEntity = false;

		this.x = x;
		this.y = y;
		this.z = z;

		face = side;

		vec = new Vec3D(blockPos.x, blockPos.y, blockPos.z);
	}
}
