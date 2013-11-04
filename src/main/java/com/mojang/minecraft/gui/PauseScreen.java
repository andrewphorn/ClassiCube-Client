package com.mojang.minecraft.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.management.MBeanServerConnection;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.ProgressBarDisplay;
import com.sun.management.OperatingSystemMXBean;

public final class PauseScreen extends GuiScreen {

	protected final void onButtonClick(Button var1) {
		if (var1.id == 0) {
			this.minecraft.setCurrentScreen(new OptionsScreen(this, this.minecraft.settings));
		}

		if (var1.id == 1) {
			this.minecraft.setCurrentScreen(new GenerateLevelScreen(this));
		}
		if (var1.id == 2) {
			this.minecraft.setCurrentScreen(new SaveLevelScreen(this));
		}

		if (var1.id == 3) {
			this.minecraft.setCurrentScreen(new TextureSelectionScreen(this));
		}

		if (var1.id == 4) {
			this.minecraft.setCurrentScreen((GuiScreen) null);
			this.minecraft.grabMouse();
		}
		if (var1.id == 5) {
			File file = new File(Minecraft.getMinecraftDirectory(), "/Screenshots/");
			file.mkdirs();
			try {
				Desktop.getDesktop().open(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (var1.id == 6) {
			File file = new File(Minecraft.getMinecraftDirectory(), "/logs/");
			file.mkdirs();
			try {
				Desktop.getDesktop().open(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public final void onOpen() {
		this.buttons.clear();
		this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4, "Options..."));
		this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 24,
				"Generate new level..."));
		this.buttons.add(new Button(2, this.width / 2 - 100, this.height / 4 + 48, "Save level.."));
		this.buttons.add(new Button(3, this.width / 2 - 100, this.height / 4 + 72,
				"Change texture pack.."));
		this.buttons
				.add(new Button(4, this.width / 2 - 100, this.height / 4 + 120, "Back to game"));
		if (this.minecraft.session == null) {
			((Button) this.buttons.get(2)).active = true;
			((Button) this.buttons.get(3)).active = true;
		}
		int w = this.fontRenderer.getWidth("Screenshots...");
		this.buttons
				.add(new Button(5, this.width - this.fontRenderer.getWidth("Screenshots...") - 15,
						this.height - 36, this.fontRenderer.getWidth("Screenshots..."),
						"Screenshots"));
		this.buttons.add(new Button(6, this.width - w - 15, this.height - 58, w, "Chat Logs"));

		if (this.minecraft.networkManager != null) {
			((Button) this.buttons.get(1)).active = false;
			((Button) this.buttons.get(2)).active = true;
			((Button) this.buttons.get(3)).active = true;
		}

	}
	
	int greenColor = 8454016;
	int orangeColor = 16750160;
	int redColor = 16737380;
	
	String VersionString = "0.11";

	public final void render(int var1, int var2) {
		
		String titlePrint = ProgressBarDisplay.title;
		String t = titlePrint.toLowerCase();
		if(t.contains("loading level") || t.contains("generating level.."))
			titlePrint = "SinglePlayer";
		
		drawFadingBox(0, 0, this.width, this.height, 1610941696, -1607454624);
		drawCenteredString(this.fontRenderer, "Game menu", this.width / 2, 40, 16777215);
		drawString(this.fontRenderer, titlePrint,
				this.width - this.fontRenderer.getWidth(titlePrint) - 15, 2, 16777215);
		drawString(this.fontRenderer, "ClassiCube " + this.VersionString,
				this.width - this.fontRenderer.getWidth("ClassiCube "+ this.VersionString) - 15, 13, 14474460);
		
		double cpuUsage = this.minecraft.monitoringThread.getAvarageUsagePerCPU();
		double roundedCpuUsage = Math.round(cpuUsage * 100.0) / 100.0;
		
		int colorToUse = greenColor;
		if(cpuUsage >= 21)
			colorToUse = orangeColor;
		else if(cpuUsage >= 30)
			colorToUse = redColor;
		else if(cpuUsage <= 20)
			colorToUse = greenColor;
		
		String s = "Average CPU: " + roundedCpuUsage + "%";
		drawString(this.fontRenderer, s, this.width - this.fontRenderer.getWidth(s) - 15, 24,
				colorToUse);
		
		long dMem = this.minecraft.monitoringThread.totalMemory
				- this.minecraft.monitoringThread.freeMemory;
		float percent = dMem * 100L / this.minecraft.monitoringThread.maxMemory;
		if(percent >=75)
			colorToUse = redColor;
		else if(percent >=50)
			colorToUse = orangeColor;
		else 
			colorToUse = greenColor;
		
		String Usage = "Used memory: " + percent
				+ "% (" + dMem / 1024L / 1024L + "MB)";
		drawString(this.fontRenderer, Usage, this.width - this.fontRenderer.getWidth(Usage) - 15,
				35, colorToUse);
		String max = "Allocated memory: " + this.minecraft.monitoringThread.maxMemory / 1024L
				/ 1024L + "MB";
		drawString(this.fontRenderer, max, this.width - this.fontRenderer.getWidth(max) - 15, 46,
				15132260);
		super.render(var1, var2);
	}

}
