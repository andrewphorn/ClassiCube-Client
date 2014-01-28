package com.mojang.minecraft.gui;

import org.lwjgl.input.Keyboard;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.PacketType;

public final class ChatInputScreen extends GuiScreen {

	public String message = "";
	private int counter = 0;

	@Override
	public final void onClose() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected final void onKeyPress(char var1, int var2) {
		if (var2 == Keyboard.KEY_UP) {
			GameSettings.typinglogpos--;
			if (GameSettings.typinglogpos < 0) {
				GameSettings.typinglogpos = 0;
			}
			if (GameSettings.typinglogpos >= 0
					&& GameSettings.typinglogpos < GameSettings.typinglog.size()) {
				message = GameSettings.typinglog.get(GameSettings.typinglogpos);
			}
		}
		if (var2 == Keyboard.KEY_DOWN) {
			GameSettings.typinglogpos++;
			if (GameSettings.typinglogpos > GameSettings.typinglog.size()) {
				GameSettings.typinglogpos = GameSettings.typinglog.size();
			}
			if (GameSettings.typinglogpos >= 0
					&& GameSettings.typinglogpos < GameSettings.typinglog.size()) {
				message = GameSettings.typinglog.get(GameSettings.typinglogpos);
			}
			if (GameSettings.typinglogpos == GameSettings.typinglog.size()) {
				message = "";
			}
		}
		if (var2 == Keyboard.KEY_ESCAPE) {
			minecraft.setCurrentScreen((GuiScreen) null);
		} else if (var2 == Keyboard.KEY_RETURN) {
			NetworkManager var10000 = minecraft.networkManager;
			String var4 = message.trim();
			NetworkManager var3 = var10000;
			if ((var4 = var4.trim()).length() > 0) {
				var3.netHandler.send(PacketType.CHAT_MESSAGE, new Object[] { Integer.valueOf(-1),
						var4 });
				GameSettings.typinglog.add(var4);
				GameSettings.typinglogpos = GameSettings.typinglog.size();
			}

			minecraft.setCurrentScreen((GuiScreen) null);
		} else {
			if (var2 == Keyboard.KEY_BACK && message.length() > 0) {
				message = message.substring(0, message.length() - 1);
			}

			if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ,.:-_\'*!\\\"#%/()=+?[]{}<>@|$;^`~"
					.indexOf(var1) >= 0
					&& message.length() < 64 - (minecraft.session.username.length() + 2)) {
				message = message + var1;
			}

		}
	}

	@Override
	protected final void onMouseClick(int var1, int var2, int var3) {
		if (var3 == 0 && minecraft.hud.hoveredPlayer != null) {
			if (message.length() > 0 && !message.endsWith(" ")) {
				message = message + " ";
			}

			message = message + minecraft.hud.hoveredPlayer;
			var1 = 64 - (minecraft.session.username.length() + 2);
			if (message.length() > var1) {
				message = message.substring(0, var1);
			}
		}

	}

	@Override
	public final void onOpen() {
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public final void render(int var1, int var2) {
	    	int x1 = 2;
		/* Add the beginning position of the box
		 * + the length of '> _'
		 * + the length of the trimmed message
		 * + the x position of the '>  _' string.
		 */
		int x2 = x1 + fontRenderer.getWidth("> _" + message) + 4;

		int y1 = height - 14;
		int y2 = y1 + 12;
		super.drawBox(x1, y1, x2, y2, ChatInputScreenExtension.ChatRGB);
		//drawBox(2, height - 14, width - 2, height - 2, ChatInputScreenExtension.ChatRGB);
		
		drawString(fontRenderer, "> " + message + (counter / 6 % 2 == 0 ? "_" : ""), 4,
				height - 12, 14737632);
	}

	@Override
	public final void tick() {
		++counter;
	}
}
