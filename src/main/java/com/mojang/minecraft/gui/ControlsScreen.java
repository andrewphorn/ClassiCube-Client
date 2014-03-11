package com.mojang.minecraft.gui;

import com.mojang.minecraft.GameSettings;

public final class ControlsScreen extends GuiScreen {

    private String title = "Controls";
    private GameSettings settings;
    private int selected = -1;

    public ControlsScreen(GameSettings gameSettings) {
        settings = gameSettings;
    }

    @Override
    protected final void onButtonClick(Button button) {
        for (int i = 0; i < settings.bindings.length; ++i) {
            buttons.get(i).text = settings.getBinding(i);
        }

        if (button.id == 100) {
            minecraft.setCurrentScreen(new MoreControlsScreen(minecraft.settings));
        } else if (button.id == 200) {
            minecraft.setCurrentScreen(new OptionsScreen(minecraft.settings));
        } else {
            selected = button.id;
            button.text = "> " + settings.getBinding(button.id) + " <";
        }
    }

    @Override
    protected final void onKeyPress(char var1, int var2) {
        if (selected >= 0) {
            settings.setBinding(selected, var2);
            buttons.get(selected).text = settings.getBinding(selected);
            selected = -1;
        } else {
            super.onKeyPress(var1, var2);
        }
    }

    @Override
    public final void onOpen() {
        for (int i = 0; i < settings.bindings.length; ++i) {
            buttons.add(new OptionButton(i, width / 2 - 155 + i % 2 * 160, height / 6 + 24
                    * (i >> 1), settings.getBinding(i)));
        }
        buttons.add(new OptionButton(100, width / 2 - 77 + 10 % 2 * 160, height / 6 + 24
                * (10 >> 1), "More..."));

        buttons.add(new Button(200, width / 2 - 100, height / 6 + 168, "Done"));
    }

    @Override
    public final void render(int var1, int var2) {
        drawFadingBox(0, 0, width, height, 1610941696, -1607454624);
        drawCenteredString(fontRenderer, title, width / 2, 20, 16777215);
        super.render(var1, var2);
    }
}
