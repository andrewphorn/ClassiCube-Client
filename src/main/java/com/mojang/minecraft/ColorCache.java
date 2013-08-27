package com.mojang.minecraft;

public class ColorCache {

	public float R;
	public float G;
	public float B;
	
	public ColorCache(float r, float g, float b)
	{
		R = FixColor(r);
		G = FixColor(g);
		B = FixColor(b);
		
	}
	float FixColor(float color){
		if(color > 1) return 1F;
		if(color < 0.01F) return 0.01F;
		return color;
	}
}
