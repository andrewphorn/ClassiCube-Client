package com.mojang.minecraft.gui.inputscreens;

import com.mojang.minecraft.gui.AdvancedOptionsScreen;
import com.mojang.minecraft.gui.Button;
import com.mojang.minecraft.gui.GuiScreen;

;

public class WaterLevelInputScreen extends InputValueScreen {

	public WaterLevelInputScreen(GuiScreen var1, String var2, int var3, String Title) {
		super(var1, var2, var3, Title);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected final void onButtonClick(Button var1) {
		if (var1.active) {
			if (var1.id == 0 && name.length() > 0) {
				minecraft.level.waterLevel = Integer.parseInt(name);
				minecraft.levelRenderer.refreshEnvironment();
				minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, minecraft.settings));
			}

			if (var1.id == 1) {
				minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, minecraft.settings));
			}
			if (var1.id == 800) {
				minecraft.level.waterLevel = minecraft.level.height / 2;
				minecraft.levelRenderer.refreshEnvironment();
				minecraft.setCurrentScreen(new AdvancedOptionsScreen(parent, minecraft.settings));
			}

		}
	}
}
