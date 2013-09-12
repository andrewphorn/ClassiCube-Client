package com.mojang.minecraft;

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
}
