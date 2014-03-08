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
    protected List<Button> buttons = new ArrayList<>();
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
            int mouseX = Mouse.getEventX() * width / minecraft.width;
            int mouseY = height - Mouse.getEventY() * height / minecraft.height - 1;
            onMouseClick(mouseX, mouseY, Mouse.getEventButton());
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

    protected void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) { // Left-click
            for (Button button : buttons) {
                if (button.active && mouseX >= button.x && mouseY >= button.y
                        && mouseX < button.x + button.width && mouseY < button.y + button.height) {
                    onButtonClick(button);
                }
            }
        }
    }

    public void onOpen() {
    }

    public final void open(Minecraft minecraft, int width, int height) {
        this.minecraft = minecraft;
        fontRenderer = minecraft.fontRenderer;
        this.width = width;
        this.height = height;
        onOpen();
    }

    public void render(int mouseX, int mouseY) {
        for (Button button : buttons) {
            if (!button.visible) {
                continue;
            }

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.textureManager.load("/gui/gui.png"));
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            byte spriteOffset = 1;
            boolean isHovered = (mouseX >= button.x) && (mouseY >= button.y)
                    && (mouseX < button.x + button.width)
                    && (mouseY < button.y + button.height);
            if (!button.active) {
                spriteOffset = 0;
            } else if (isHovered) {
                spriteOffset = 2;
            }

            button.drawImage(button.x, button.y,
                    0, 46 + spriteOffset * 20, button.width / 2, button.height);
            button.drawImage(button.x + button.width / 2, button.y,
                    200 - button.width / 2, 46 + spriteOffset * 20, button.width / 2, button.height);

            int textColorRGBA;
            if (!button.active) {
                textColorRGBA = -6250336; // A0A0A0FF
            } else if (isHovered) {
                textColorRGBA = 16777120; // A0FFFF00
            } else {
                textColorRGBA = 14737632; // E0E0E000
            }

            drawCenteredString(minecraft.fontRenderer, button.text,
                    button.x + button.width / 2, button.y + (button.height - 8) / 2,
                    textColorRGBA);
        }
    }

    public void tick() {
    }
}
