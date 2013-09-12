package com.mojang.minecraft.gui;

import com.mojang.minecraft.GameSettings;

public class ScaledResolution {
	private int scaledWidth;
	private int scaledHeight;
	private double scaledWidthD;
	private double scaledHeightD;
	private int scaleFactor;

	public ScaledResolution(GameSettings par1GameSettings, int par2, int par3) {
		this.scaledWidth = par2;
		this.scaledHeight = par3;
		this.scaleFactor = 1;
		int var4 = 1000; // scale

		if (var4 == 0) {
			var4 = 1000;
		}

		while (this.scaleFactor < var4 && this.scaledWidth / (this.scaleFactor + 1) >= 320
				&& this.scaledHeight / (this.scaleFactor + 1) >= 240) {
			++this.scaleFactor;
		}

		this.scaledWidthD = (double) this.scaledWidth / (double) this.scaleFactor;
		this.scaledHeightD = (double) this.scaledHeight / (double) this.scaleFactor;
		this.scaledWidth = ceiling_double_int(this.scaledWidthD);
		this.scaledHeight = ceiling_double_int(this.scaledHeightD);
	}

	public static int ceiling_double_int(double par0) {
		int var2 = (int) par0;
		return par0 > (double) var2 ? var2 + 1 : var2;
	}

	public int getScaledWidth() {
		return this.scaledWidth;
	}

	public int getScaledHeight() {
		return this.scaledHeight;
	}

	public double getScaledWidth_double() {
		return this.scaledWidthD;
	}

	public double getScaledHeight_double() {
		return this.scaledHeightD;
	}

	public int getScaleFactor() {
		return this.scaleFactor;
	}
}