package com.mojang.minecraft.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.inputscreens.InputValueScreen;
import com.mojang.minecraft.level.LevelSerializer;

public final class SaveLevelScreen extends LoadLevelScreen {

	public SaveLevelScreen(GuiScreen var1) {
		super(var1);
		title = "Save level";
		saving = true;
	}

	@Override
	public final void onOpen() {
		super.onOpen();
		buttons.get(5).text = "Save file...";
	}

	@Override
	protected final void openLevel(File var1) {
		if (!var1.getName().endsWith(".dat")) {
			var1 = new File(var1.getParentFile(), var1.getName() + ".dat");
		}

		File var2 = var1;
		Minecraft var3 = minecraft;
		try {
			new LevelSerializer(var3.level).saveMap(var2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// this.minecraft.levelIo.save(var3.level, var2);
		minecraft.setCurrentScreen(parent);
	}

	@Override
	protected final void openLevel(int var1) {
		minecraft.setCurrentScreen(new InputValueScreen(this, buttons.get(var1).text, var1,
				"Enter level name..."));
	}

	@Override
	public final void render(int var1, int var2) {
		super.render(var1, var2);
	}

	@Override
	protected final void setLevels(String[] var1) {
		for (int var2 = 0; var2 < 5; ++var2) {
			buttons.get(var2).text = var1[var2];
			buttons.get(var2).visible = true;
			buttons.get(var2).active = true;
		}

	}
}
