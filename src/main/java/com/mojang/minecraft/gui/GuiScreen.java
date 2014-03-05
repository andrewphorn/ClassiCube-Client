package com.mojang.minecraft.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.Minecraft;

public class GuiScreen extends Screen {

    protected Minecraft minecraft;
    public int width;
    public int height;
    protected List<Button> buttons = new ArrayList<Button>();
    public boolean grabsMouse = false;
    protected FontRenderer fontRenderer;

    public final void doInput() {
        while (Mouse.next()) {
            mouseEvent();
        }

        while (Keyboard.next()) {
            keyboardEvent();
        }

    }

    public final void keyboardEvent() {
        if (Keyboard.getEventKeyState()) {
            onKeyPress(Keyboard.getEventCharacter(), Keyboard.getEventKey());
        }

    }

    public final void mouseEvent() {
        if (Mouse.getEventButtonState()) {
            int var1 = Mouse.getEventX() * width / minecraft.width;
            int var2 = height - Mouse.getEventY() * height / minecraft.height - 1;
            onMouseClick(var1, var2, Mouse.getEventButton());
        }

    }

    protected void onButtonClick(Button var1) {
    }

    public void onClose() {
    }

    protected void onKeyPress(char var1, int var2) {
        if (var2 == 1) {
            minecraft.setCurrentScreen((GuiScreen) null);
            minecraft.grabMouse();
        }
        if (Keyboard.getEventKey() == Keyboard.KEY_F2) {
            minecraft.takeAndSaveScreenshot(minecraft.width, minecraft.height);
        }
    }

    protected void onMouseClick(int var1, int var2, int var3) {
        if (var3 == 0) {
            for (var3 = 0; var3 < buttons.size(); ++var3) {
                Button var4;
                Button var7;
                if ((var7 = var4 = buttons.get(var3)).active && var1 >= var7.x && var2 >= var7.y
                        && var1 < var7.x + var7.width && var2 < var7.y + var7.height) {
                    onButtonClick(var4);
                }
            }
        }

    }

    public void onOpen() {
    }

    public final void open(Minecraft var1, int var2, int var3) {
        minecraft = var1;
        fontRenderer = var1.fontRenderer;
        width = var2;
        height = var3;
        onOpen();
    }

    public void render(int var1, int var2) {
        for (int var3 = 0; var3 < buttons.size(); ++var3) {
            Button var10000 = buttons.get(var3);
            Minecraft var7 = minecraft;
            Button var4 = var10000;
            if (var10000.visible) {
                FontRenderer var8 = var7.fontRenderer;
                GL11.glBindTexture(3553, var7.textureManager.load("/gui/gui.png"));
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                byte var9 = 1;
                boolean var6 = var1 >= var4.x && var2 >= var4.y && var1 < var4.x + var4.width
                        && var2 < var4.y + var4.height;
                if (!var4.active) {
                    var9 = 0;
                } else if (var6) {
                    var9 = 2;
                }

                var4.drawImage(var4.x, var4.y, 0, 46 + var9 * 20, var4.width / 2, var4.height);
                var4.drawImage(var4.x + var4.width / 2, var4.y, 200 - var4.width / 2,
                        46 + var9 * 20, var4.width / 2, var4.height);
                if (!var4.active) {
                    drawCenteredString(var8, var4.text, var4.x + var4.width / 2, var4.y
                            + (var4.height - 8) / 2, -6250336);
                } else if (var6) {
                    drawCenteredString(var8, var4.text, var4.x + var4.width / 2, var4.y
                            + (var4.height - 8) / 2, 16777120);
                } else {
                    drawCenteredString(var8, var4.text, var4.x + var4.width / 2, var4.y
                            + (var4.height - 8) / 2, 14737632);
                }
            }
        }

    }

    public void tick() {
    }
}
