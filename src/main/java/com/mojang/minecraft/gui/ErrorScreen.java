package com.mojang.minecraft.gui;

import com.mojang.minecraft.Minecraft;
import com.mojang.util.Timer;

public final class ErrorScreen extends GuiScreen {

    private final String title;
    private final String text;
    private final double endTime;
    private static final int RECONNECT_DELAY = 3; // Wait 3 seconds before allowing reconnect

    public ErrorScreen(String title, String subtitle) {
        this.title = title;
        text = subtitle;
        isOpaque = true;
        endTime = System.nanoTime() / Timer.NANOSEC_PER_SEC + RECONNECT_DELAY;
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
                !Minecraft.isSinglePlayer ? "Try to reconnect..." + secondsUntilReconnect() : "Restart ClassiCube"));
        if (minecraft.isFullScreen) {
            minecraft.toggleFullscreen();
        }
    }

    int secondsUntilReconnect() {
        double now = System.nanoTime() / Timer.NANOSEC_PER_SEC;
        return (int) Math.max(0, Math.ceil(endTime - now));
    }

    @Override
    public final void render(int mouseX, int mouseY) {
        drawFadingBox(0, 0, width, height, -12574688, -11530224);
        drawCenteredString(fontRenderer, title, width / 2, 90, 16777215);
        drawCenteredString(fontRenderer, text, width / 2, 110, 16777215);
        super.render(mouseX, mouseY);
        String buttonLabel;
        int secToReconnect = secondsUntilReconnect();
        if (Minecraft.isSinglePlayer) {
            buttonLabel = "Restart ClassiCube";
        } else {
            buttonLabel = "Try to reconnect";
            if (secToReconnect > 0) {
                buttonLabel = buttonLabel + "..." + secondsUntilReconnect();
            }
        }
        buttons.set(0, new Button(0, this.width / 2 - 100, this.height / 4 + 96, buttonLabel));
        if (!Minecraft.isSinglePlayer && secToReconnect > 0) {
            buttons.get(0).active = false;
        }
    }
}
