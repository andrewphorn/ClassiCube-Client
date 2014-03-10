package com.mojang.minecraft.gui;

import java.awt.Color;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Setting;
import com.mojang.minecraft.gui.inputscreens.FogColorInputScreen;
import com.mojang.minecraft.gui.inputscreens.LightColorInputScreen;
import com.mojang.minecraft.gui.inputscreens.ShadowColorInputScreen;
import com.mojang.minecraft.gui.inputscreens.SkyColorInputScreen;
import com.mojang.minecraft.gui.inputscreens.WaterLevelInputScreen;

public final class AdvancedOptionsScreen extends GuiScreen {

    private final static Setting[] settingsOrder = new Setting[] { Setting.ENABLE_HACKS,
            Setting.SPEEDHACK_TYPE, Setting.ALLOW_SERVER_TEXTURES, Setting.SHOW_DEBUG };

    public static String decToHex(int dec) {
        int sizeOfIntInHalfBytes = 8;
        int numberOfBitsInAHalfByte = 4;
        int halfByte = 0x0F;
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
                'E', 'F' };
        StringBuilder hexBuilder = new StringBuilder(sizeOfIntInHalfBytes);
        hexBuilder.setLength(sizeOfIntInHalfBytes);
        for (int i = sizeOfIntInHalfBytes - 1; i >= 0; --i) {
            int j = dec & halfByte;
            hexBuilder.setCharAt(i, hexDigits[j]);
            dec >>= numberOfBitsInAHalfByte;
        }
        return hexBuilder.toString();
    }

    private final GuiScreen parent;
    private final String title = "Advanced Options";
    private final GameSettings settings;

    public AdvancedOptionsScreen(GuiScreen parent, GameSettings settings) {
        this.parent = parent;
        this.settings = settings;
    }

    @Override
    protected final void onButtonClick(Button clickedButton) {
        if (clickedButton.active) {
            if (clickedButton.id < 100) {
                Setting affectedSetting = settingsOrder[clickedButton.id];
                settings.toggleSetting(affectedSetting, 1);
                clickedButton.text = settings.getSetting(affectedSetting);

            }
            if (clickedButton.id == 100) {
                minecraft.setCurrentScreen(new CloudOptionsScreen(this, settings));
            }

            if (clickedButton.id == 200) {
                WaterLevelInputScreen screen = new WaterLevelInputScreen(parent, ""
                        + minecraft.level.waterLevel, height, "Enter new value for water level...");
                screen.numbersOnly = true;
                minecraft.setCurrentScreen(screen);
            }
            if (clickedButton.id == 300) {
                SkyColorInputScreen screen = new SkyColorInputScreen(parent, ""
                        + Integer.toHexString(minecraft.level.skyColor), height,
                        "Enter new value for sky color...");
                screen.allowedChars = "ABCDEFabcdef1234567890";
                screen.stringLimit = 6;
                minecraft.setCurrentScreen(screen);
            }
            if (clickedButton.id == 400) {
                FogColorInputScreen screen = new FogColorInputScreen(parent, ""
                        + Integer.toHexString(minecraft.level.fogColor), height,
                        "Enter new value for fog color...");
                screen.allowedChars = "ABCDEFabcdef1234567890";
                screen.stringLimit = 6;
                minecraft.setCurrentScreen(screen);
            }
            if (clickedButton.id == 500) {
                ColorCache c = minecraft.level.customLightColour;
                Color color = new Color(255, 255, 255);
                String colorString = "";
                if (c != null) {
                    colorString = String.format("%02x%02x%02x", (int) (c.R * 255),
                            (int) (c.G * 255), (int) (c.B * 255));
                } else {
                    colorString = String.format("%02x%02x%02x", color.getRed(), color.getGreen(),
                            color.getBlue());
                }
                LightColorInputScreen screen = new LightColorInputScreen(parent, "" + colorString,
                        height, "Enter new value for light color...");
                screen.allowedChars = "ABCDEFabcdef1234567890";
                screen.stringLimit = 6;
                minecraft.setCurrentScreen(screen);
            }
            if (clickedButton.id == 600) {
                ColorCache c = minecraft.level.customShadowColour;
                Color color = new Color(155, 155, 155);
                String colorString = "";
                if (c != null) {
                    colorString = String.format("%02x%02x%02x", (int) (c.R * 255),
                            (int) (c.G * 255), (int) (c.B * 255));
                } else {
                    colorString = String.format("%02x%02x%02x", color.getRed(), color.getGreen(),
                            color.getBlue());
                }
                ShadowColorInputScreen screen = new ShadowColorInputScreen(parent,
                        "" + colorString, height, "Enter new value for shadow color...");
                screen.allowedChars = "ABCDEFabcdef1234567890";
                screen.stringLimit = 6;
                minecraft.setCurrentScreen(screen);
            }

            if (clickedButton.id == 700) {
                minecraft.setCurrentScreen(new OptionsScreen(this, settings));
            }
        }
    }

    @Override
    public final void onOpen() {
        int heightSeparator = 0;
        for (int i = 0; i < settingsOrder.length; ++i) {
            // TODO: advanced settings
            buttons.add(new OptionButton(i, width / 2 - 155 + heightSeparator % 2 * 160, height / 6
                    + 24 * (heightSeparator >> 1), settings.getSetting(settingsOrder[i])));
            heightSeparator++;
        }
        buttons.add(new OptionButton(100, width / 2 - 155 + heightSeparator % 2 * 160, height / 6
                + 24 * (heightSeparator >> 1), "Clouds"));
        heightSeparator++;
        buttons.add(new OptionButton(200, width / 2 - 155 + heightSeparator % 2 * 160, height / 6
                + 24 * (heightSeparator >> 1), "Water Level"));
        heightSeparator++;
        buttons.add(new OptionButton(300, width / 2 - 155 + heightSeparator % 2 * 160, height / 6
                + 24 * (heightSeparator >> 1), "Sky Color"));
        heightSeparator++;
        buttons.add(new OptionButton(400, width / 2 - 155 + heightSeparator % 2 * 160, height / 6
                + 24 * (heightSeparator >> 1), "Fog Color"));
        heightSeparator++;
        buttons.add(new OptionButton(500, width / 2 - 155 + heightSeparator % 2 * 160, height / 6
                + 24 * (heightSeparator >> 1), "Sunlight Color"));
        heightSeparator++;
        buttons.add(new OptionButton(600, width / 2 - 155 + heightSeparator % 2 * 160, height / 6
                + 24 * (heightSeparator >> 1), "Shadow Color"));

        buttons.add(new Button(700, width / 2 - 100, height / 6 + 168, "Done"));

        // [Allow server textures] requires you to be on a server
        buttons.get(2).active = minecraft.session != null;
    }

    @Override
    public final void render(int var1, int var2) {
        drawFadingBox(0, 0, width, height, 1610941696, -1607454624);
        drawCenteredString(fontRenderer, title, width / 2, 20, 16777215);

        super.render(var1, var2);
    }
}
