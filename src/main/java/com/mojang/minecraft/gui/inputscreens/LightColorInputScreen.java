package com.mojang.minecraft.gui.inputscreens;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.gui.AdvancedOptionsScreen;
import com.mojang.minecraft.gui.Button;
import com.mojang.minecraft.gui.GuiScreen;

public class LightColorInputScreen extends InputValueScreen {

    public LightColorInputScreen(GuiScreen var1, String var2, int var3, String Title) {
        super(var1, var2, var3, Title);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected final void onButtonClick(Button button) {
        if (button.active) {
            if (button.id == 0 && name.length() > 0) {
                minecraft.level.customLightColour = ColorCache.parseHex(name);
                minecraft.levelRenderer.refresh();
                minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, minecraft.settings));
            }

            if (button.id == 1) {
                minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, minecraft.settings));
            }
            if (button.id == 800) {
                minecraft.level.customLightColour = new ColorCache(0.99f, 0.99f, 0.99f);
                minecraft.levelRenderer.refresh();
                minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, minecraft.settings));
            }

        }
    }
}
