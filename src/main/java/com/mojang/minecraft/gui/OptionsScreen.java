package com.mojang.minecraft.gui;

import com.mojang.minecraft.GameSettings;

public final class OptionsScreen extends GuiScreen {

    private String title = "Options";
    private GameSettings settings;

    public OptionsScreen(GuiScreen var1, GameSettings var2) {
        settings = var2;
    }

    @Override
    protected final void onButtonClick(Button var1) {
        if (var1.active) {
            if (var1.id < 100) {
                settings.toggleSetting(var1.id, 1);
                var1.text = settings.getSetting(var1.id);
            }
            buttons.get(9).active = minecraft.settings.smoothing > 0;

            if (var1.id == 100) {
                minecraft.setCurrentScreen(new AdvancedOptionsScreen(this, settings));
            }

            if (var1.id == 200) {
                minecraft.setCurrentScreen(new ControlsScreen(this, settings));
            }

            if (var1.id == 300) {
                minecraft.setCurrentScreen(new PauseScreen());
            }

        }
    }

    @Override
    public final void onOpen() {
        for (int var1 = 0; var1 < 10; ++var1) {
            buttons.add(new OptionButton(var1, width / 2 - 155 + var1 % 2 * 160, height / 6 + 24
                    * (var1 >> 1), settings.getSetting(var1)));
        }

        buttons.add(new Button(100, width / 2 - 100, height / 6 + 90 + 32, "Advanced Options..."));

        buttons.add(new Button(200, width / 2 - 100, height / 6 + 120 + 26, "Controls..."));
        buttons.add(new Button(300, width / 2 - 100, height / 6 + 168, "Done"));

        buttons.get(9).active = minecraft.settings.smoothing > 0;
    }

    @Override
    public final void render(int var1, int var2) {
        drawFadingBox(0, 0, width, height, 1610941696, -1607454624);
        drawCenteredString(fontRenderer, title, width / 2, 20, 16777215);
        super.render(var1, var2);
    }
}
