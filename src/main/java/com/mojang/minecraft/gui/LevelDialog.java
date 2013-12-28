package com.mojang.minecraft.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mojang.minecraft.level.LevelSerializer;

final class LevelDialog extends Thread {

	// $FF: synthetic field
	private LoadLevelScreen screen;

	LevelDialog(LoadLevelScreen var1) {
		super();
		screen = var1;
	}

	@Override
	public final void run() {
		JFileChooser var1;
		try {
			LoadLevelScreen var10000 = screen;
			var1 = new JFileChooser();
			var10000.chooser = var1;
			FileNameExtensionFilter var3 = new FileNameExtensionFilter("ClassicWorld format (.cw)",
					new String[] { "cw" });
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
				screen.selectedFile = new File(screen.selectedFile + "");
				try {
					new LevelSerializer(screen.minecraft.level).saveMap(screen.selectedFile);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} finally {
			screen.frozen = false;
			var1 = null;
			screen.chooser = var1;
		}

	}
}
