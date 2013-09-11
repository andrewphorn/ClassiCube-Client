package com.mojang.minecraft.gui;

import com.mojang.minecraft.phys.AABB;

public class ChatScreenData {

    public int width;
    public int height;
    public int x;
    public int y;
    public String string;
    public AABB bounds;
    public FontRenderer renderer;

    public ChatScreenData(int width, int height, int x, int y, String message,
	    FontRenderer f) {
	this.width = width;
	this.height = height;
	this.x = x;
	this.y = y;
	this.string = message;
	this.renderer = f;
	this.bounds = new AABB(x, y, 0, f.getWidth(message), y + height, 0f);
    }
}
