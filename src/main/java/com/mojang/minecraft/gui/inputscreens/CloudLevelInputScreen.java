package com.mojang.minecraft.gui.inputscreens;

import com.mojang.minecraft.gui.Button;
import com.mojang.minecraft.gui.CloudOptionsScreen;
import com.mojang.minecraft.gui.GuiScreen;

public class CloudLevelInputScreen extends InputValueScreen {

    public CloudLevelInputScreen(GuiScreen var1, String var2, int var3, String Title) {
        super(var1, var2, var3, Title);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected final void onButtonClick(Button button) {
        if (button.active) {
            if (button.id == 0 && name.length() > 0) {
                String var2 = name;
                minecraft.level.cloudLevel = Integer.parseInt(var2);
                minecraft.setCurrentScreen(new CloudOptionsScreen(parent, minecraft.settings));
            }

            if (button.id == 1) {
                minecraft.setCurrentScreen(new CloudOptionsScreen(parent, minecraft.settings));
            }
            if (button.id == 800) {
                minecraft.level.cloudLevel = minecraft.level.height + 2;
                minecraft.setCurrentScreen(new CloudOptionsScreen(parent, minecraft.settings));
            }

        }
    }
}
