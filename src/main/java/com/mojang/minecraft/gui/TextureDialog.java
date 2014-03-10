package com.mojang.minecraft.gui;

import com.mojang.minecraft.LogUtil;
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
		screen = var1;
		mc = minecraft;
	}

	protected void openTexture(String file) {
		try {
			mc.textureManager.loadTexturePack(file);
		} catch (IOException ex) {
			LogUtil.logError("Error loading texture pack from " + file, ex);
		}
		mc.setCurrentScreen((GuiScreen) null);
		mc.grabMouse();
	}

	@Override
	public final void run() {
		JFileChooser var1;
		try {
			TextureSelectionScreen var10000 = screen;
			var1 = new JFileChooser();
			var10000.chooser = var1;
			FileNameExtensionFilter var3 = new FileNameExtensionFilter(".Zip Texture Packs",
					new String[] { "zip" });
			screen.chooser.setFileFilter(var3);
			screen.chooser.setMultiSelectionEnabled(false);
			int var7;
			if (screen.saving) {
				var7 = screen.chooser.showSaveDialog(screen.minecraft.canvas);
			} else {
				var7 = screen.chooser.showOpenDialog(screen.minecraft.canvas);
			}

			if (var7 == 0) {
				screen.selectedFile = screen.chooser.getSelectedFile();
				openTexture(screen.chooser.getSelectedFile().getName());

			}
		} finally {
			screen.frozen = false;
			var1 = null;
			screen.chooser = var1;
		}

	}
}
