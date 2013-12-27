package com.mojang.minecraft.gui;

import com.mojang.minecraft.ChatClickData;
import com.mojang.minecraft.ChatClickData.LinkData;
import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.PacketType;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URI;
import java.util.Vector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class ChatInputScreenExtension extends GuiScreen {
	public String inputLine = "";
	private int tickCount = 0;
	public int caretPos = 0;
	private int historyPos = 0;

	public static Vector<String> history = new Vector<String>();

	int j;

	private String getClipboard() {
		Transferable clipboard = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		try {
			if ((clipboard != null) && (clipboard.isDataFlavorSupported(DataFlavor.stringFlavor)))
				return (String) clipboard.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException ex) {
		} catch (IOException e) {
		}
		return null;
	}

	private void insertTextAtCaret(String paramString) {
		int i = 64 - this.minecraft.session.username.length() - 2;

		int j = paramString.length();
		this.inputLine = (this.inputLine.substring(0, this.caretPos) + paramString + this.inputLine
				.substring(this.caretPos));
		this.caretPos += j;
		if (this.inputLine.length() > i) {
			this.inputLine = this.inputLine.substring(0, i);
		}
		if (this.caretPos > this.inputLine.length())
			this.caretPos = this.inputLine.length();
	}

	@Override
	public final void onClose() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected final void onKeyPress(char paramChar, int paramInt) {
		if (paramInt == 1) {
			this.minecraft.setCurrentScreen((GuiScreen) null);
			return;
		}
		if (paramInt == Keyboard.KEY_F2) {
			this.minecraft.setCurrentScreen((GuiScreen) null);
			this.minecraft.takeAndSaveScreenshot(this.minecraft.width, this.minecraft.height);
			this.minecraft.setCurrentScreen(this);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_TAB))
			return;

		if (paramInt == 28) {
			String str1 = this.inputLine.trim();
			if (str1.length() > 0) {
				NetworkManager var10000 = this.minecraft.networkManager;
				NetworkManager var3 = var10000;
				if ((str1 = str1.trim()).length() > 0) {
					var3.netHandler.send(PacketType.CHAT_MESSAGE,
							new Object[] { Integer.valueOf(-1), str1 });
					history.add(str1);
				}

			}
			this.minecraft.setCurrentScreen((GuiScreen) null);
			return;
		}

		int i = this.inputLine.length();
		if ((paramInt == 14) && (i > 0) && (this.caretPos > 0)) {
			this.inputLine = (this.inputLine.substring(0, this.caretPos - 1) + this.inputLine
					.substring(this.caretPos));
			this.caretPos -= 1;
		}

		if ((paramInt == 203) && (this.caretPos > 0)) {
			this.caretPos -= 1;
		}

		if ((paramInt == 205) && (this.caretPos < i)) {
			this.caretPos += 1;
		}

		if (paramInt == 199) {
			this.caretPos = 0;
		}

		if (paramInt == 207) {
			this.caretPos = i;
		}

		if ((Keyboard.isKeyDown(219)) || (Keyboard.isKeyDown(220)) || (Keyboard.isKeyDown(29))
				|| (Keyboard.isKeyDown(157))) {
			if (paramInt == 47) {
				paramChar = '\000';
				String str2 = getClipboard();
				insertTextAtCaret(str2);
			} else if (paramInt == 46) {
				paramChar = '\000';
				setClipboard(this.inputLine);
			}
		}
		/*if (paramInt == Keyboard.KEY_TAB) {
			String namePart = this.inputLine;
			if (namePart == null || namePart.length() == 0)
				return;
			List<String> potentials = new ArrayList<String>();
			for (int index = 0; index < this.minecraft.networkManager.players.size(); index++) {
				if (this.minecraft.networkManager.players.get(index).name.toLowerCase().contains(namePart
						.toLowerCase())) {
					potentials.add(this.minecraft.networkManager.players.get(index).name);
				}
			}
			if (potentials.size() == 0)
				return;
			if (potentials.size() == 1) {
				this.inputLine = potentials.get(0);
			} else {
				try {
					this.minecraft.hud.addChat(joinToString((String[]) potentials.toArray()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}*/

		if (paramInt == 200) {
			j = history.size();
			if (this.historyPos < j) {
				this.historyPos += 1;
				this.inputLine = (history.get(j - this.historyPos));
				this.caretPos = this.inputLine.length();
			}
		}

		if (paramInt == 208) {
			j = history.size();
			if (this.historyPos > 0) {
				this.historyPos -= 1;

				if (this.historyPos > 0) {
					this.inputLine = (history.get(j - this.historyPos));
				} else {
					this.inputLine = "";
				}
				this.caretPos = this.inputLine.length();
			}
		}

		int j = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ,.:-_'*!\\\"#%/()=+?[]{}<>@|$;~`^"
				.indexOf(paramChar) >= 0 ? 1 : 0;

		if (j != 0)
			insertTextAtCaret(String.valueOf(paramChar));
	}

	public String joinToString(String[] Names) throws Exception {
		String buildable = "";
		if (Names == null)
			throw new Exception("Names cannot be null");
		if (Names.length == 0)
			return buildable;
		for (int i = 0; i < Names.length; i++) {
			buildable += Names[i];
			if (i != Names.length)
				buildable += ", ";
		}
		return buildable;
	}

	@Override
	protected final void onMouseClick(int x, int y, int clickType) {
		if ((clickType == 0) && (this.minecraft.hud.hoveredPlayer != null))
			insertTextAtCaret(this.minecraft.hud.hoveredPlayer + " ");
		if (clickType == 0) {
			for (int i = 0; i < this.minecraft.hud.chat.size(); i++) {
				for (ChatScreenData data : this.minecraft.hud.chatsOnScreen) {
					if (x > data.bounds.x0 && x < data.bounds.x1 && y > data.bounds.y0
							&& y < data.bounds.y1) {
						ChatClickData chatClickData = new ChatClickData(fontRenderer,
								this.minecraft.hud.chat.get(i), x, y);
						if (data.string == chatClickData.message) {
							for (LinkData ld : chatClickData.getClickedUrls()) {
								if (ld != null) {
									if (x > ld.x0 && x < ld.x1 && y > data.bounds.y0
											&& y < data.bounds.y1) {
										String s = FontRenderer.stripColor(ld.link);
										URI uri = chatClickData.getURI(s);
										if (uri != null) {
											this.openWebpage(uri);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public final void onOpen() {
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void render(int paramInt1, int paramInt2) {
		super.drawBox(2, this.height - 14, this.width - 2, this.height - 2, -2147483648);
		char[] temp = new char[128];
		for (int a = 0; a < this.inputLine.length(); a++) {
			temp[a] = this.inputLine.toCharArray()[a];
		}

		if (temp.length == 0)
			temp[temp.length] = (this.tickCount / 6 % 2 == 0 ? '_' : ' ');
		else
			temp[this.caretPos] = (this.tickCount / 6 % 2 == 0 ? '_' : temp[this.caretPos]);

		String string = "";
		for (int i = 0; i < temp.length; i++) {
			string += temp[i];
		}
		drawString(this.fontRenderer, "> " + string, 4, this.height - 12, 14737632);

		int x = Mouse.getEventX() * this.width / this.minecraft.width;
		int y = this.height - Mouse.getEventY() * this.height / this.minecraft.height - 1;
		for (int i = 0; i < this.minecraft.hud.chat.size(); i++) {
			for (ChatScreenData data : this.minecraft.hud.chatsOnScreen) {
				if (x > data.bounds.x0 && x < data.bounds.x1 && y > data.bounds.y0
						&& y < data.bounds.y1) {
					ChatClickData chatClickData = new ChatClickData(fontRenderer,
							this.minecraft.hud.chat.get(i), x, y);
					if (data.string == chatClickData.message) {
						for (LinkData ld : chatClickData.getClickedUrls()) {
							if (ld != null) {
								if (x > ld.x0 && x < ld.x1 && y > data.bounds.y0
										&& y < data.bounds.y1) {
									super.drawBox(ld.x0, data.y - 1, ld.x1 + 3, data.y + 9,
											-2147483648);
								}
							}
						}
					}
				}
			}
		}
	}

	private void setClipboard(String paramString) {
		StringSelection localStringSelection = new StringSelection(paramString);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(localStringSelection, null);
	}

	@Override
	public final void tick() {
		++this.tickCount;
	}
}