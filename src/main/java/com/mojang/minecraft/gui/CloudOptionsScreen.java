package com.mojang.minecraft.gui;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.gui.inputscreens.CloudColorInputScreen;
import com.mojang.minecraft.gui.inputscreens.CloudLevelInputScreen;

public final class CloudOptionsScreen extends GuiScreen {

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

	private GuiScreen parent;
	private String title = "Cloud Options";
	private GameSettings settings;

	public CloudOptionsScreen(GuiScreen var1, GameSettings var2) {
		parent = var1;
		settings = var2;
	}

	@Override
	protected final void onButtonClick(Button var1) {
		if (var1.active) {
			if (var1.id == 1) {
				CloudLevelInputScreen screen = new CloudLevelInputScreen(parent, ""
						+ minecraft.level.cloudLevel, height, "Enter new value for cloud level...");
				screen.numbersOnly = true;
				minecraft.setCurrentScreen(screen);
			}
			if (var1.id == 2) {
				CloudColorInputScreen screen = new CloudColorInputScreen(parent, ""
						+ Integer.toHexString(minecraft.level.cloudColor), height,
						"Enter new value for cloud color...");
				screen.allowedChars = "ABCDEFabcdef1234567890";
				screen.stringLimit = 6;
				minecraft.setCurrentScreen(screen);
			}

			if (var1.id == 3) {
				settings.showClouds = !settings.showClouds;
				buttons.set(2, new OptionButton(3, width / 2 - 77, height / 6 + 72, "Clouds: "
						+ (settings.showClouds ? "On" : "Off")));
			}

			if (var1.id == 4) {
				minecraft.setCurrentScreen(new AdvancedOptionsScreen(this, settings));
			}
		}
	}

	@Override
	public final void onOpen() {
		buttons.add(new OptionButton(1, width / 2 - 77, height / 6 + 24, "Cloud Level"));
		buttons.add(new OptionButton(2, width / 2 - 77, height / 6 + 48, "Cloud Color"));
		buttons.add(new OptionButton(3, width / 2 - 77, height / 6 + 72, "Clouds: "
				+ (settings.showClouds ? "On" : "Off")));
		buttons.add(new Button(4, width / 2 - 100, height / 6 + 168, "Done"));
	}

	@Override
	public final void render(int var1, int var2) {
		drawFadingBox(0, 0, width, height, 1610941696, -1607454624);
		drawCenteredString(fontRenderer, title, width / 2, 20, 16777215);

		super.render(var1, var2);
	}
}
