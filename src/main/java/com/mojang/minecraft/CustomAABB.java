package com.mojang.minecraft;

/**
 * A custom implementation of AABB designed to stop the graphical glitches in
 * SelectionBoxes
 *
 * @author Jon
 *
 */
public class CustomAABB {

    public float maxX;
    public float maxY;
    public float maxZ;
    public float minX;
    public float minY;
    public float minZ;

    public CustomAABB(float maxX, float maxY, float maxZ, float minX, float minY, float minZ) {
        this.maxX = maxX - 0.02F;
        this.maxY = maxY - 0.02F;
        this.maxZ = maxZ - 0.02F;
        this.minX = minX - 0.02F;
        this.minY = minY - 0.02F;
        this.minZ = minZ - 0.02F;
    }

}
