package com.mojang.minecraft.gui;

import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mojang.minecraft.Minecraft;

final class TextureDialog extends Thread {

    // $FF: synthetic field
    private TextureSelectionScreen screen;
    private Minecraft mc;
    TextureDialog(TextureSelectionScreen var1, Minecraft minecraft) {
	super();
	this.screen = var1;
	this.mc = minecraft;
    }

    public final void run() {
	JFileChooser var1;
	try {
	    TextureSelectionScreen var10000 = this.screen;
	    var1 = new JFileChooser();
	    var10000.chooser = var1;
	    FileNameExtensionFilter var3 = new FileNameExtensionFilter(
		    ".Zip Texture Packs", new String[] { "zip" });
	    this.screen.chooser.setFileFilter(var3);
	    this.screen.chooser.setMultiSelectionEnabled(false);
	    int var7;
	    if (this.screen.saving) {
		var7 = this.screen.chooser
			.showSaveDialog(this.screen.minecraft.canvas);
	    } else {
		var7 = this.screen.chooser
			.showOpenDialog(this.screen.minecraft.canvas);
	    }

	    if (var7 == 0) {
		(this.screen).selectedFile = this.screen.chooser
			.getSelectedFile();
		openTexture(this.screen.chooser
			.getSelectedFile().getName());
		
	    }
	} finally {
	    this.screen.frozen = false;
	    var1 = null;
	    this.screen.chooser = var1;
	}

    }
    protected void openTexture(String file) {
	try {
	    this.mc.textureManager.loadTexturePack(file);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	this.mc.setCurrentScreen((GuiScreen) null);
	this.mc.grabMouse();
    }
}
