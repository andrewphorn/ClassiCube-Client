package com.mojang.minecraft;

public class CustomAABB {

    public float x0;
    public float y0;
    public float z0;
    public float x1;
    public float y1;
    public float z1;

    public CustomAABB(float var1, float var2, float var3, float var4,
	    float var5, float var6) {
	var1 += 0.05F;
	var2 += 0.05F;
	var3 += 0.05F;
	var4 -= 0.05F;
	var5 -= 0.05F;
	var6 -= 0.05F;

	this.x0 = var1;
	this.y0 = var2;
	this.z0 = var3;
	this.x1 = var4;
	this.y1 = var5;
	this.z1 = var6;
    }

}
