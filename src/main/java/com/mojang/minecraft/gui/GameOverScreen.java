package com.mojang.minecraft.gui;

import org.lwjgl.opengl.GL11;

public final class GameOverScreen extends GuiScreen {

    @Override
    protected final void onButtonClick(Button button) {
        if (button.id == 0) {
            minecraft.setCurrentScreen(new OptionsScreen(minecraft.settings));
        }

        if (button.id == 1) {
            minecraft.setCurrentScreen(new GenerateLevelScreen(this));
        }

        if (minecraft.session != null && button.id == 2) {
            minecraft.setCurrentScreen(new LoadLevelScreen(this));
        }

    }

    @Override
    public final void onOpen() {
        buttons.clear();
        buttons.add(new Button(1, width / 2 - 100, height / 4 + 72, "Generate new level..."));
        buttons.add(new Button(2, width / 2 - 100, height / 4 + 96, "Load level.."));
        if (minecraft.session == null) {
            buttons.get(1).active = false;
        }

    }

    @Override
    public final void render(int var1, int var2) {
        drawFadingBox(0, 0, width, height, 1615855616, -1602211792);
        GL11.glPushMatrix();
        GL11.glScalef(2F, 2F, 2F);
        drawCenteredString(fontRenderer, "Game over!", width / 2 / 2, 30, 16777215);
        GL11.glPopMatrix();
        drawCenteredString(fontRenderer, "Score: &e" + minecraft.player.getScore(), width / 2, 100,
                16777215);
        super.render(var1, var2);
    }
}
