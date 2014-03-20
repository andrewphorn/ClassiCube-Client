package com.mojang.minecraft.gui;

public class Button extends Screen {

    public int x;
    public int y;
    public String text;
    public int id;
    public boolean active;
    public boolean visible;
    int width;
    int height;

    public Button(int buttonID, int buttonX, int buttonY, int buttonWidth, String buttonText) {
        width = 200;
        height = 20;
        active = true;
        visible = true;
        id = buttonID;
        x = buttonX;
        y = buttonY;
        width = buttonWidth;
        height = 20;
        text = buttonText;
    }

    public Button(int id, int x, int y, String text) {
        this(id, x, y, 200, text);
    }
}
