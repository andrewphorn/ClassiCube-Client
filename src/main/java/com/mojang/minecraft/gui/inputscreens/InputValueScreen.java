package com.mojang.minecraft.gui.inputscreens;

import org.lwjgl.input.Keyboard;

import com.mojang.util.LogUtil;
import com.mojang.minecraft.gui.Button;
import com.mojang.minecraft.gui.GuiScreen;
import com.mojang.minecraft.level.LevelSerializer;

public class InputValueScreen extends GuiScreen {

    public GuiScreen parent;
    public String title = "Enter level name:";
    public int id;
    public String name;
    public int counter = 0;
    public boolean numbersOnly = false;
    public String allowedChars = null;
    public int stringLimit = 64;

    public InputValueScreen(GuiScreen guiScreen, String value, int id, String title) {
        parent = guiScreen;
        this.id = id;
        name = value;
        this.title = title;
        if (name.equals("-")) {
            name = "";
        }

    }

    @Override
    protected void onButtonClick(Button button) {
        if (button.active) {
            if (button.id == 0 && name.trim().length() > 1) {
                try {
                    new LevelSerializer(minecraft.level).saveMap("test");
                } catch (Exception ex) {
                    LogUtil.logError("Error saving map to test", ex);
                }
                minecraft.setCurrentScreen(null);
                minecraft.grabMouse();
            }

            if (button.id == 1) {
                minecraft.setCurrentScreen(parent);
            }

        }
    }

    @Override
    public final void onClose() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public final void onKeyPress(char var1, int var2) {
        if (var2 == 14 && name.length() > 0) {
            name = name.substring(0, name.length() - 1);
        }

        String canUse = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ,.:-_\'*!\"#%/()=+?[]{}<>";
        if (numbersOnly) {
            canUse = "0123456789";
        }
        if (allowedChars != null) {
            canUse = allowedChars;
        }
        if (canUse.indexOf(var1) >= 0 && name.length() < stringLimit) {
            name = name + var1;
        }

        buttons.get(0).active = name.trim().length() > 0;
    }

    @Override
    public final void onOpen() {
        buttons.clear();
        Keyboard.enableRepeatEvents(true);
        buttons.add(new Button(0, width / 2 - 100, height / 4 + 120, "Save"));
        buttons.add(new Button(1, width / 2 - 100, height / 4 + 144, "Cancel"));
        buttons.get(0).active = name.trim().length() > 1;
        int w = minecraft.fontRenderer.getWidth("Screenshots...");
        buttons.add(new Button(800, width - w - 15, height - 36, w, "Default"));
    }

    @Override
    public final void render(int var1, int var2) {
        drawFadingBox(0, 0, width, height, 1610941696, -1607454624);
        drawCenteredString(fontRenderer, title, width / 2, 40, 16777215);
        int var3 = width / 2 - 100;
        int var4 = height / 2 - 10;
        drawBox(var3 - 1, var4 - 1, var3 + 200 + 1, var4 + 20 + 1, -6250336);
        drawBox(var3, var4, var3 + 200, var4 + 20, -16777216);
        drawString(fontRenderer, name + (counter / 6 % 2 == 0 ? "_" : ""), var3 + 4, var4 + 6,
                14737632);
        super.render(var1, var2);
    }

    @Override
    public final void tick() {
        ++counter;
    }
}
