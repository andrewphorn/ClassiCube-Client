package com.mojang.minecraft;

public class CustomAABB {

    public float x0;
    public float y0;
    public float z0;
    public float x1;
    public float y1;
    public float z1;

    public CustomAABB(float x0, float y0, float z0, float x1, float y1, float z1) {
        x0 -= 0.02F;
        y0 -= 0.02F;
        z0 -= 0.02F;
        x1 += 0.02F;
        y1 += 0.02F;
        z1 += 0.02F;

        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
    }

}
