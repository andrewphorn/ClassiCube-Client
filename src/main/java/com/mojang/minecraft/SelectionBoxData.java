package com.mojang.minecraft;

import com.mojang.minecraft.player.Player;

public class SelectionBoxData {
	public byte ID;
	public String Name;
	public ColorCache Color;
	public CustomAABB Bounds;

	public SelectionBoxData(byte ID, String Name, ColorCache Color, CustomAABB Bounds) {
		this.ID = ID;
		this.Name = Name;
		this.Bounds = Bounds;
		this.Color = Color;
	}
	
	public final float distanceSquared0(Player var1) {
		float var2 = var1.x - this.Bounds.x0;
		float var3 = var1.y - this.Bounds.y0;
		float var4 = var1.z - this.Bounds.z0;
		return var2 * var2 + var3 * var3 + var4 * var4;
	}
	public final float distanceSquared1(Player var1) {
		float var2 = var1.x - this.Bounds.x1;
		float var3 = var1.y - this.Bounds.y1;
		float var4 = var1.z - this.Bounds.z1;
		return var2 * var2 + var3 * var3 + var4 * var4;
	}
}
