package com.mojang.minecraft.gui;

import com.mojang.minecraft.physics.AABB;

public class ChatScreenData {

    public float width;
    public float height;
    public float x;
    public float y;
    public String string;
    public AABB bounds;
    public FontRenderer renderer;

    public ChatScreenData(float width, float height, float x, float y, String message,
            FontRenderer f) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        string = message;
        renderer = f;
        bounds = new AABB(x, y, 0, f.getWidth(message), y + height, 0f);
    }
}
