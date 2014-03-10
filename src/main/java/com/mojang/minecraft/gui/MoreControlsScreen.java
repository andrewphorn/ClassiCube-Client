package com.mojang.minecraft.gui;

import com.mojang.minecraft.GameSettings;

public final class MoreControlsScreen extends GuiScreen {

    private String title = "Controls cont...";
    private GameSettings settings;
    private int selected = -1;

    public MoreControlsScreen(GuiScreen var1, GameSettings var2) {
        settings = var2;
    }

    @Override
    protected final void onButtonClick(Button var1) {
        for (int var2 = 0; var2 < settings.bindingsmore.length; ++var2) {
            buttons.get(var2).text = settings.getBindingMore(var2);
        }

        if (var1.id == 200) {
            minecraft.setCurrentScreen(new ControlsScreen(this, minecraft.settings));
        } else {
            selected = var1.id;
            var1.text = "> " + settings.getBindingMore(var1.id) + " <";
        }
    }

    @Override
    protected final void onKeyPress(char var1, int var2) {
        if (selected >= 0) {
            settings.setBindingMore(selected, var2);
            buttons.get(selected).text = settings.getBindingMore(selected);
            selected = -1;
        } else {
            super.onKeyPress(var1, var2);
        }
    }

    @Override
    public final void onOpen() {
        for (int var1 = 0; var1 < settings.bindingsmore.length; ++var1) {
            buttons.add(new OptionButton(var1, width / 2 - 155 + var1 % 2 * 160, height / 6 + 24
                    * (var1 >> 1), settings.getBindingMore(var1)));
        }

        buttons.add(new Button(200, width / 2 - 100, height / 6 + 168, "Done"));
    }

    @Override
    public final void render(int var1, int var2) {
        drawFadingBox(0, 0, width, height, 1610941696, -1607454624);
        drawCenteredString(fontRenderer, title, width / 2, 20, 16777215);
        super.render(var1, var2);
    }
}
