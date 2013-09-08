package com.mojang.minecraft.gui.inputscreens;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.AdvancedOptionsScreen;
import com.mojang.minecraft.gui.Button;
import com.mojang.minecraft.gui.GuiScreen;

;

public class WaterLevelInputScreen extends InputValueScreen {

    public WaterLevelInputScreen(GuiScreen var1, String var2, int var3,
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
		var4.level.waterLevel = Integer.parseInt(var2);
		var4.levelRenderer.refresh();
		this.minecraft.setCurrentScreen(new AdvancedOptionsScreen(
			parent, this.minecraft.settings));
	    }

	    if (var1.id == 1) {
		this.minecraft.setCurrentScreen(new AdvancedOptionsScreen(
			parent, this.minecraft.settings));
	    }

	}
    }
}
