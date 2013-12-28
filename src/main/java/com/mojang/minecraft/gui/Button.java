package com.mojang.minecraft.gui;

public class Button extends Screen {

	int width;
	int height;
	public int x;
	public int y;
	public String text;
	public int id;
	public boolean active;
	public boolean visible;

	public Button(int var1, int var2, int var3, int var4, String var6) {
		width = 200;
		height = 20;
		active = true;
		visible = true;
		id = var1;
		x = var2;
		y = var3;
		width = var4;
		height = 20;
		text = var6;
	}

	public Button(int var1, int var2, int var3, String var4) {
		this(var1, var2, var3, 200, var4);
	}
}
