package com.mojang.minecraft.gui.inputscreens;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.AdvancedOptionsScreen;
import com.mojang.minecraft.gui.Button;
import com.mojang.minecraft.gui.GuiScreen;

;

public class SkyColorInputScreen extends InputValueScreen {

    public SkyColorInputScreen(GuiScreen var1, String var2, int var3, String Title) {
        super(var1, var2, var3, Title);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected final void onButtonClick(Button var1) {
        if (var1.active) {
            if (var1.id == 0 && name.length() > 0) {
                Minecraft var10000 = minecraft;
                String var2 = name;
                Minecraft var4 = var10000;
                var4.level.skyColor = Integer.parseInt(var2, 16);
                minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, minecraft.settings));
            }

            if (var1.id == 1) {
                minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, minecraft.settings));
            }
            if (var1.id == 800) {
                minecraft.level.skyColor = Integer.parseInt("99ccff", 16);
                minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, minecraft.settings));
            }

        }
    }
}
