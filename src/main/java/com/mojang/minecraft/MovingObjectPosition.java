package com.mojang.minecraft;

import com.mojang.util.Vector3f;

public class MovingObjectPosition {
    public boolean hasEntity;

    public int x;
    public int y;
    public int z;
    public int face;
    public Vector3f vec;

    public Entity entity;

    public MovingObjectPosition(Entity entity) {
        hasEntity = true;
        this.entity = entity;
    }

    public MovingObjectPosition(int x, int y, int z, int side, Vector3f blockPos) {
        hasEntity = false;

        this.x = x;
        this.y = y;
        this.z = z;

        face = side;

        vec = new Vector3f(blockPos.x, blockPos.y, blockPos.z);
    }
}
