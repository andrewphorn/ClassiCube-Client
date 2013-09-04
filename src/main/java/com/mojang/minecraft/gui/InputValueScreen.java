package com.mojang.minecraft.gui;

import com.mojang.minecraft.Minecraft;
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

    public final void onOpen() {
	this.buttons.clear();
	Keyboard.enableRepeatEvents(true);
	this.buttons.add(new Button(0, this.width / 2 - 100,
		this.height / 4 + 120, "Save"));
	this.buttons.add(new Button(1, this.width / 2 - 100,
		this.height / 4 + 144, "Cancel"));
	((Button) this.buttons.get(0)).active = this.name.trim().length() > 1;
    }

    public final void onClose() {
	Keyboard.enableRepeatEvents(false);
    }

    public final void tick() {
	++this.counter;
    }

    protected void onButtonClick(Button var1) {
	if (var1.active) {
	    if (var1.id == 0 && this.name.trim().length() > 1) {
		Minecraft var10000 = this.minecraft;
		int var10001 = this.id;
		String var2 = this.name.trim();
		int var3 = var10001;
		Minecraft var4 = var10000;
		var10000.levelIo.saveOnline(var4.level, var4.host,
			var4.session.username, var4.session.sessionId, var2,
			var3);
		this.minecraft.setCurrentScreen((GuiScreen) null);
		this.minecraft.grabMouse();
	    }

	    if (var1.id == 1) {
		this.minecraft.setCurrentScreen(this.parent);
	    }

	}
    }

    public final void onKeyPress(char var1, int var2) {
	if (var2 == 14 && this.name.length() > 0) {
	    this.name = this.name.substring(0, this.name.length() - 1);
	}

	String canUse = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ,.:-_\'*!\"#%/()=+?[]{}<>";
	if(this.numbersOnly)
	    canUse = "0123456789";
	if(this.allowedChars!=null)
	    canUse = allowedChars;
	if (canUse.indexOf(var1) >= 0 && this.name.length() < stringLimit) {
	    this.name = this.name + var1;
	}

	((Button) this.buttons.get(0)).active = this.name.trim().length() > 0;
    }

    public final void render(int var1, int var2) {
	drawFadingBox(0, 0, this.width, this.height, 1610941696, -1607454624);
	drawCenteredString(this.fontRenderer, this.title, this.width / 2, 40,
		16777215);
	int var3 = this.width / 2 - 100;
	int var4 = this.height / 2 - 10;
	drawBox(var3 - 1, var4 - 1, var3 + 200 + 1, var4 + 20 + 1, -6250336);
	drawBox(var3, var4, var3 + 200, var4 + 20, -16777216);
	drawString(this.fontRenderer, this.name
		+ (this.counter / 6 % 2 == 0 ? "_" : ""), var3 + 4, var4 + 6,
		14737632);
	super.render(var1, var2);
    }
}
