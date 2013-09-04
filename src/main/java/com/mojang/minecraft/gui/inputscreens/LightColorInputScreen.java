package com.mojang.minecraft.gui.inputscreens;

import java.awt.Color;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.AdvancedOptionsScreen;
import com.mojang.minecraft.gui.Button;
import com.mojang.minecraft.gui.GuiScreen;

;

public class LightColorInputScreen extends InputValueScreen {

    public LightColorInputScreen(GuiScreen var1, String var2, int var3,
	    String Title) {
	super(var1, var2, var3, Title);
	// TODO Auto-generated constructor stub
    }

    protected final void onButtonClick(Button var1) {
	if (var1.active) {
	    if (var1.id == 0 && this.name.length() > 0) {
		Minecraft var10000 = this.minecraft;
		String var2 = this.name;
		Minecraft var4 = var10000;
		var4.level.customLightColour = hex2Rgb(var2);
		var4.levelRenderer.refresh();
		this.minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, this.minecraft.settings));
	    }

	    if (var1.id == 1) {
		this.minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, this.minecraft.settings));
	    }

	}
    }

    public static ColorCache hex2Rgb(String colorStr) {
	Color c = Color.decode("#" + colorStr);
	float red = c.getRed() / 255f;
	float blue = c.getBlue() / 255f;
	float green = c.getGreen() / 255f;
	return new ColorCache(red, blue, green);
    }
}
