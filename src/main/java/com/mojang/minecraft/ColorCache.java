package com.mojang.minecraft;

import java.io.Serializable;

public class ColorCache implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	public float R;
	public float G;
	public float B;
	public float A;

	public ColorCache(float r, float g, float b) {
		R = FixColor(r);
		G = FixColor(g);
		B = FixColor(b);
		A = 1F;
	}

	public ColorCache(float r, float g, float b, float a) {
		R = FixColor(r);
		G = FixColor(g);
		B = FixColor(b);
		A = a;
	}

	float FixColor(float color) {
		if (color > 1.0F) {
			return 1F;
		}
		if (color < 0.00F) {
			return 0.00F;
		}
		return color;
	}
}
