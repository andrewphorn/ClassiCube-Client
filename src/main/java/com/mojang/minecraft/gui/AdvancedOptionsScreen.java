package com.mojang.minecraft.gui;

import java.awt.Color;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.gui.inputscreens.CloudColorInputScreen;
import com.mojang.minecraft.gui.inputscreens.FogColorInputScreen;
import com.mojang.minecraft.gui.inputscreens.LightColorInputScreen;
import com.mojang.minecraft.gui.inputscreens.ShadowColorInputScreen;
import com.mojang.minecraft.gui.inputscreens.SkyColorInputScreen;
import com.mojang.minecraft.gui.inputscreens.WaterLevelInputScreen;

public final class AdvancedOptionsScreen extends GuiScreen {

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
	private String title = "Advanced Options";
	private GameSettings settings;

	boolean drawWarning = false;

	boolean lastVBOValue;

	String optionWarningMessage = "Changing to/from VBOs requires a client restart";

	public AdvancedOptionsScreen(GuiScreen var1, GameSettings var2) {
		this.parent = var1;
		this.settings = var2;
		lastVBOValue = this.settings.VBOs;
	}

	protected final void onButtonClick(Button var1) {
		if (var1.active) {
			if (var1.id < 100) {
				this.settings.toggleSetting(var1.id, 1);
				var1.text = this.settings.getSetting(var1.id);
				if (var1.text.contains("VBO")) {
					if (this.settings.VBOs != lastVBOValue) {
						this.drawWarning = true;
					} else {
						this.drawWarning = false;
					}
				}
				if(var1.text.contains("SpeedHack")){
					this.minecraft.player.input.fly = false;
					this.minecraft.player.input.noclip = false;
					
					this.minecraft.player.noPhysics = false;
					this.minecraft.player.flyingMode = false;
					this.minecraft.player.hovered = false;
				}
			}

			if (var1.id == 100) {
				WaterLevelInputScreen screen = new WaterLevelInputScreen(parent, ""
						+ this.minecraft.level.waterLevel, height,
						"Enter new value for water level...");
				screen.numbersOnly = true;
				this.minecraft.setCurrentScreen(screen);
			}
			if (var1.id == 200) {
				SkyColorInputScreen screen = new SkyColorInputScreen(parent, ""
						+ Integer.toHexString(this.minecraft.level.skyColor), height,
						"Enter new value for sky color...");
				screen.allowedChars = "ABCDEFabcdef1234567890";
				screen.stringLimit = 6;
				this.minecraft.setCurrentScreen(screen);
			}
			if (var1.id == 300) {
				CloudColorInputScreen screen = new CloudColorInputScreen(parent, ""
						+ Integer.toHexString(this.minecraft.level.cloudColor), height,
						"Enter new value for cloud color...");
				screen.allowedChars = "ABCDEFabcdef1234567890";
				screen.stringLimit = 6;
				this.minecraft.setCurrentScreen(screen);
			}
			if (var1.id == 400) {
				FogColorInputScreen screen = new FogColorInputScreen(parent, ""
						+ Integer.toHexString(this.minecraft.level.fogColor), height,
						"Enter new value for fog color...");
				screen.allowedChars = "ABCDEFabcdef1234567890";
				screen.stringLimit = 6;
				this.minecraft.setCurrentScreen(screen);
			}
			if (var1.id == 500) {
				ColorCache c = this.minecraft.level.customLightColour;
				Color color = new Color(255, 255, 255);
				String colorString = "";
				if (c != null)
					colorString = String.format("%02x%02x%02x", (int) (c.R * 255),
							(int) (c.G * 255), (int) (c.B * 255));
				else
					colorString = String.format("%02x%02x%02x", color.getRed(), color.getGreen(),
							color.getBlue());
				LightColorInputScreen screen = new LightColorInputScreen(parent, "" + colorString,
						height, "Enter new value for light color...");
				screen.allowedChars = "ABCDEFabcdef1234567890";
				screen.stringLimit = 6;
				this.minecraft.setCurrentScreen(screen);
			}
			if (var1.id == 600) {
				ColorCache c = this.minecraft.level.customShadowColour;
				Color color = new Color(155, 155, 155);
				String colorString = "";
				if (c != null)
					colorString = String.format("%02x%02x%02x", (int) (c.R * 255),
							(int) (c.G * 255), (int) (c.B * 255));
				else
					colorString = String.format("%02x%02x%02x", color.getRed(), color.getGreen(),
							color.getBlue());
				ShadowColorInputScreen screen = new ShadowColorInputScreen(parent,
						"" + colorString, height, "Enter new value for shadow color...");
				screen.allowedChars = "ABCDEFabcdef1234567890";
				screen.stringLimit = 6;
				this.minecraft.setCurrentScreen(screen);
			}

			if (var1.id == 700) {
				this.minecraft.setCurrentScreen(new OptionsScreen(this, settings));
			}
		}
	}

	public final void onOpen() {
		int heightSeperator = 0;
		for (int var1 = 10; var1 < this.settings.settingCount; ++var1) {
			this.buttons.add(new OptionButton(var1, this.width / 2 - 155 + heightSeperator % 2
					* 160, this.height / 6 + 24 * (heightSeperator >> 1), this.settings
					.getSetting(var1)));
			heightSeperator++;
		}
		this.buttons.add(new OptionButton(100, this.width / 2 - 155 + heightSeperator % 2 * 160,
				this.height / 6 + 24 * (heightSeperator >> 1), "Water Level"));
		heightSeperator++;
		this.buttons.add(new OptionButton(200, this.width / 2 - 155 + heightSeperator % 2 * 160,
				this.height / 6 + 24 * (heightSeperator >> 1), "Sky Color"));
		heightSeperator++;
		this.buttons.add(new OptionButton(300, this.width / 2 - 155 + heightSeperator % 2 * 160,
				this.height / 6 + 24 * (heightSeperator >> 1), "Cloud Color"));
		heightSeperator++;
		this.buttons.add(new OptionButton(400, this.width / 2 - 155 + heightSeperator % 2 * 160,
				this.height / 6 + 24 * (heightSeperator >> 1), "Fog Color"));
		heightSeperator++;
		this.buttons.add(new OptionButton(500, this.width / 2 - 155 + heightSeperator % 2 * 160,
				this.height / 6 + 24 * (heightSeperator >> 1), "Sunlight Color"));
		heightSeperator++;
		this.buttons.add(new OptionButton(600, this.width / 2 - 155 + heightSeperator % 2 * 160,
				this.height / 6 + 24 * (heightSeperator >> 1), "Shadow Color"));

		this.buttons.add(new Button(700, this.width / 2 - 100, this.height / 6 + 168, "Done"));

		buttons.get(0).active = this.minecraft.session != null;
		buttons.get(4).active = this.minecraft.player.userType >= 100;
	}

	public final void render(int var1, int var2) {
		drawFadingBox(0, 0, this.width, this.height, 1610941696, -1607454624);
		drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
		if (drawWarning) {
			drawCenteredString(this.minecraft.fontRenderer, this.optionWarningMessage,
					this.minecraft.width / 2 / 2, this.height / 6 + 152, 16711680);
		}
		super.render(var1, var2);
	}
}
