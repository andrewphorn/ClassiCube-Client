package com.mojang.minecraft.gui;

import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelLoader;
import com.mojang.util.LogUtil;
import java.io.File;

public final class ErrorScreen extends GuiScreen {

    private final String title;
    private final String text;
    private int timeOpen = 359;

    public ErrorScreen(String title, String subtitle) {
        this.title = title;
        text = subtitle;
        isOpaque = true;
    }

    @Override
    protected final void onButtonClick(Button button) {
        if (button.id == 0) {
            minecraft.setCurrentScreen(null);
            if (Minecraft.isSinglePlayer) {
                minecraft.restartSinglePlayer();
            } else {
                minecraft.reconnect();
            }
        }
    }

    @Override
    protected final void onKeyPress(char var1, int var2) {
    }

    @Override
    public final void onOpen() {
        buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 96,
                !Minecraft.isSinglePlayer ? "Try to reconnect..." + timeOpen / 60 : "Restart ClassiCube"));
        if (minecraft.isFullScreen) {
            minecraft.toggleFullscreen();
        }
    }

    @Override
    public final void render(int mouseX, int mouseY) {
        drawFadingBox(0, 0, width, height, -12574688, -11530224);
        drawCenteredString(fontRenderer, title, width / 2, 90, 16777215);
        drawCenteredString(fontRenderer, text, width / 2, 110, 16777215);
        super.render(mouseX, mouseY);
        String buttonLabel;
        if (Minecraft.isSinglePlayer) {
            buttonLabel = "Restart ClassiCube";
        } else {
            buttonLabel = (timeOpen / 60 > 0 ? "Try to reconnect..." + timeOpen / 60 : "Try to reconnect");
        }
        buttons.set(0, new Button(0, this.width / 2 - 100, this.height / 4 + 96, buttonLabel));
        if (timeOpen / 60 > 0 && !Minecraft.isSinglePlayer) {
            --timeOpen;
            buttons.get(0).active = false;
        }
    }
}
