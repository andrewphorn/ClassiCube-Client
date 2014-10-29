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
            if (!Minecraft.isSinglePlayer) {
                minecraft.networkManager = new NetworkManager(minecraft);
                minecraft.networkManager.beginConnect(minecraft.server, minecraft.port,
                        minecraft.session.username, minecraft.session.mppass);
            } else {
                try {
                    if (!minecraft.isLevelLoaded) {
                        // Try to load a previously-saved level
                        Level level = new LevelLoader().load(new File(Minecraft.mcDir, "levelc.cw"), minecraft.player);
                        if (level != null) {
                            minecraft.progressBar.setText("Loading saved map...");
                            minecraft.setLevel(level);
                            Minecraft.isSinglePlayer = true;
                        }
                    }
                } catch (Exception ex) {
                    LogUtil.logError("Failed to load a saved singleplayer level.", ex);
                }
                if (minecraft.level == null) {
                    // If loading failed, generate a new level.
                    minecraft.generateLevel(1);
                }
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
    public final void render(int var1, int var2) {
        drawFadingBox(0, 0, width, height, -12574688, -11530224);
        drawCenteredString(fontRenderer, title, width / 2, 90, 16777215);
        drawCenteredString(fontRenderer, text, width / 2, 110, 16777215);
        super.render(var1, var2);
        buttons.set(0, new Button(0, this.width / 2 - 100, this.height / 4 + 96,
                !Minecraft.isSinglePlayer ? (timeOpen / 60 > 0 ? "Try to reconnect..." + timeOpen / 60 : "Try to reconnect") : "Restart ClassiCube"));
        if (timeOpen / 60 > 0 && !Minecraft.isSinglePlayer) {
            --timeOpen;
            buttons.get(0).active = false;
        }
    }
}
