package com.mojang.minecraft.gui.inputscreens;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.Button;
import com.mojang.minecraft.gui.GuiScreen;
import com.mojang.minecraft.level.LevelSerializer;

import org.lwjgl.input.Keyboard;

public class InputValueScreen extends GuiScreen {

	public GuiScreen parent;
	public String title = "Enter level name:";
	public int id;
	public String name;
	public int counter = 0;
	public boolean numbersOnly = false;
	public String allowedChars = null;
	public int stringLimit = 64;

	public InputValueScreen(GuiScreen var1, String var2, int var3, String Title) {
		this.parent = var1;
		this.id = var3;
		this.name = var2;
		this.title = Title;
		if (this.name.equals("-")) {
			this.name = "";
		}

	}

	@Override
	protected void onButtonClick(Button var1) {
		if (var1.active) {
			if (var1.id == 0 && this.name.trim().length() > 1) {
				Minecraft var10000 = this.minecraft;
				int var10001 = this.id;
				String var2 = this.name.trim();
				int var3 = var10001;
				Minecraft var4 = var10000;
				try {
					new LevelSerializer(var4.level).saveMap("test");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.minecraft.setCurrentScreen((GuiScreen) null);
				this.minecraft.grabMouse();
			}

			if (var1.id == 1) {
				this.minecraft.setCurrentScreen(this.parent);
			}

		}
	}

	@Override
	public final void onClose() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public final void onKeyPress(char var1, int var2) {
		if (var2 == 14 && this.name.length() > 0) {
			this.name = this.name.substring(0, this.name.length() - 1);
		}

		String canUse = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ,.:-_\'*!\"#%/()=+?[]{}<>";
		if (this.numbersOnly)
			canUse = "0123456789";
		if (this.allowedChars != null)
			canUse = allowedChars;
		if (canUse.indexOf(var1) >= 0 && this.name.length() < stringLimit) {
			this.name = this.name + var1;
		}

		this.buttons.get(0).active = this.name.trim().length() > 0;
	}

	@Override
	public final void onOpen() {
		this.buttons.clear();
		Keyboard.enableRepeatEvents(true);
		this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 120, "Save"));
		this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 144, "Cancel"));
		this.buttons.get(0).active = this.name.trim().length() > 1;
		int w = this.minecraft.fontRenderer.getWidth("Screenshots...");
		this.buttons.add(new Button(800, this.width - w - 15, this.height - 36, w, "Default"));
	}

	@Override
	public final void render(int var1, int var2) {
		drawFadingBox(0, 0, this.width, this.height, 1610941696, -1607454624);
		drawCenteredString(this.fontRenderer, this.title, this.width / 2, 40, 16777215);
		int var3 = this.width / 2 - 100;
		int var4 = this.height / 2 - 10;
		drawBox(var3 - 1, var4 - 1, var3 + 200 + 1, var4 + 20 + 1, -6250336);
		drawBox(var3, var4, var3 + 200, var4 + 20, -16777216);
		drawString(this.fontRenderer, this.name + (this.counter / 6 % 2 == 0 ? "_" : ""), var3 + 4,
				var4 + 6, 14737632);
		super.render(var1, var2);
	}

	@Override
	public final void tick() {
		++this.counter;
	}
}
