package com.mojang.minecraft.gui;

import com.oyasunadev.mcraft.client.core.MinecraftStandalone;

public final class ErrorScreen extends GuiScreen {

	private String title;
	private String text;

	public ErrorScreen(String var1, String var2) {
		title = var1;
		text = var2;
	}

	@Override
	protected final void onButtonClick(Button var1) {
		if (var1.id == 0) {
			minecraft.isRunning = false;
			minecraft.shutdown();
			// System.exit(0);
			MinecraftStandalone.main(MinecraftStandalone.storedArgs);
		}
	}

	@Override
	protected final void onKeyPress(char var1, int var2) {
	}

	@Override
	public final void onOpen() {
		buttons.clear();
		// this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4
		// + 96, this.minecraft
		// .isOnline() ? "Try to reconnect..." : "Restart ClassiCube"));
	}

	@Override
	public final void render(int var1, int var2) {
		drawFadingBox(0, 0, width, height, -12574688, -11530224);
		drawCenteredString(fontRenderer, title, width / 2, 90, 16777215);
		drawCenteredString(fontRenderer, text, width / 2, 110, 16777215);
		super.render(var1, var2);
	}
}
