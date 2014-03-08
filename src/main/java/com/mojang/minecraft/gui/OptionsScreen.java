package com.mojang.minecraft.gui;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Setting;

public final class OptionsScreen extends GuiScreen {

    private final static Setting[] settingsOrder = new Setting[]{
        Setting.MUSIC, Setting.SOUND,
        Setting.INVERT_MOUSE, Setting.VIEW_BOBBING,
        Setting.RENDER_DISTANCE, Setting.LIMIT_FRAMERATE,
        Setting.SMOOTHING, Setting.ANISOTROPIC,
        Setting.FONT_SCALE, Setting.SHOW_NAMES
    };

    private final String title = "Options";
    private final GameSettings settings;

    public OptionsScreen(GuiScreen parent, GameSettings settings) {
        this.settings = settings;
    }

    @Override
    protected final void onButtonClick(Button clickedButton) {
        if (clickedButton.active) {
            if (clickedButton.id < 100) {
                // A settings button was clicked
                Setting affectedSetting = settingsOrder[clickedButton.id];
                settings.toggleSetting(affectedSetting, 1);
                clickedButton.text = settings.getSetting(affectedSetting);
                checkSettingsConsistency();

            } else if (clickedButton.id == 100) {
                // [Advanced Options] was clicked
                minecraft.setCurrentScreen(new AdvancedOptionsScreen(this, settings));

            } else if (clickedButton.id == 200) {
                // [Controls] was clicked
                minecraft.setCurrentScreen(new ControlsScreen(this, settings));

            } else if (clickedButton.id == 300) {
                // [Done] was clicked
                minecraft.setCurrentScreen(new PauseScreen());
            }
        }
    }

    @Override
    public final void onOpen() {
        for (int i = 0; i < settingsOrder.length; ++i) {
            buttons.add(new OptionButton(i,
                    width / 2 - 155 + (i % 2) * 160,
                    height / 6 + 24 * (i / 2),
                    settings.getSetting(settingsOrder[i])));
        }
        checkSettingsConsistency();

        buttons.add(new Button(100, width / 2 - 100, height / 6 + 90 + 32, "Advanced Options..."));

        buttons.add(new Button(200, width / 2 - 100, height / 6 + 120 + 26, "Controls..."));
        buttons.add(new Button(300, width / 2 - 100, height / 6 + 168, "Done"));
    }

    @Override
    public final void render(int mouseX, int mouseY) {
        drawFadingBox(0, 0, width, height, 1610941696, -1607454624);
        drawCenteredString(fontRenderer, title, width / 2, 20, 16777215);
        super.render(mouseX, mouseY);
    }

    private void checkSettingsConsistency() {
        // [Anisotropic] should only ne enabled if smoothing is on
        boolean smoothingOn = (minecraft.settings.smoothing > 0);
        buttons.get(indexOf(Setting.ANISOTROPIC, settingsOrder)).active = smoothingOn;
    }

    private static <T> int indexOf(T needle, T[] haystack) {
        for (int i = 0; i < haystack.length; i++) {
            if (haystack[i] != null && haystack[i].equals(needle)
                    || needle == null && haystack[i] == null) {
                return i;
            }
        }
        return -1;
    }
}
