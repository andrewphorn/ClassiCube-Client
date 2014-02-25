package com.mojang.minecraft.gui;

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

import com.mojang.minecraft.ChatClickData;
import com.mojang.minecraft.ChatClickData.LinkData;
import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.PacketType;

public class ChatInputScreenExtension extends GuiScreen {
    public String inputLine = "";
    private int tickCount = 0;
    public int caretPos = 0;
    private int historyPos = 0;

    public static Vector<String> history = new Vector<String>();

    int j;

    private String getClipboard() {
	Transferable clipboard = Toolkit.getDefaultToolkit()
		.getSystemClipboard().getContents(null);
	try {
	    if (clipboard != null
		    && clipboard.isDataFlavorSupported(DataFlavor.stringFlavor)) {
		return (String) clipboard
			.getTransferData(DataFlavor.stringFlavor);
	    }
	} catch (UnsupportedFlavorException ex) {
	} catch (IOException e) {
	}
	return null;
    }

    private void insertTextAtCaret(String paramString) {
	int i = 64 - minecraft.session.username.length() - 2;

	int j = paramString.length();
	inputLine = inputLine.substring(0, caretPos) + paramString
		+ inputLine.substring(caretPos);
	caretPos += j;
	if (inputLine.length() > i) {
	    inputLine = inputLine.substring(0, i);
	}
	if (caretPos > inputLine.length()) {
	    caretPos = inputLine.length();
	}
    }

    public String joinToString(String[] Names) throws Exception {
	String buildable = "";
	if (Names == null) {
	    throw new Exception("Names cannot be null");
	}
	if (Names.length == 0) {
	    return buildable;
	}
	for (int i = 0; i < Names.length; i++) {
	    buildable += Names[i];
	    if (i != Names.length) {
		buildable += ", ";
	    }
	}
	return buildable;
    }

    @Override
    public final void onClose() {
	Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected final void onKeyPress(char paramChar, int paramInt) {
	if (paramInt == Keyboard.KEY_ESCAPE) {
	    minecraft.setCurrentScreen((GuiScreen) null);
	    return;
	}
	if (paramInt == Keyboard.KEY_F2) {
	    minecraft.setCurrentScreen((GuiScreen) null);
	    minecraft.takeAndSaveScreenshot(minecraft.width, minecraft.height);
	    minecraft.setCurrentScreen(this);
	}

	if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
	    return;
	}

	if (paramInt == Keyboard.KEY_RETURN) { // 28
	    String str1 = inputLine.trim();
	    if (str1.length() > 0) {
		NetworkManager var10000 = minecraft.networkManager;
		NetworkManager var3 = var10000;
		if ((str1 = str1.trim()).length() > 0) {
		    var3.netHandler.send(PacketType.CHAT_MESSAGE, new Object[] {
			    Integer.valueOf(-1), str1 });
		    history.add(str1);
		}

	    }
	    minecraft.setCurrentScreen((GuiScreen) null);
	    return;
	}

	int i = inputLine.length();
	if (paramInt == Keyboard.KEY_BACK && i > 0 && caretPos > 0) {
	    inputLine = inputLine.substring(0, caretPos - 1)
		    + inputLine.substring(caretPos);
	    caretPos -= 1;
	}

	if (paramInt == Keyboard.KEY_LEFT && caretPos > 0) {
	    caretPos -= 1;
	}

	if (paramInt == Keyboard.KEY_RIGHT && caretPos < i) {
	    caretPos += 1;
	}

	if (paramInt == Keyboard.KEY_HOME) {
	    caretPos = 0;
	}

	if (paramInt == Keyboard.KEY_END) {
	    caretPos = i;
	}

	if (Keyboard.isKeyDown(Keyboard.KEY_LMETA)
		|| Keyboard.isKeyDown(Keyboard.KEY_RMETA)
		|| Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
		|| Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
	    if (paramInt == Keyboard.KEY_V) {
		paramChar = '\000';
		String str2 = getClipboard();
		insertTextAtCaret(str2);
	    } else if (paramInt == Keyboard.KEY_C) {
		paramChar = '\000';
		setClipboard(inputLine);
	    }
	}
	/*
	 * if (paramInt == Keyboard.KEY_TAB) { String namePart = this.inputLine;
	 * if (namePart == null || namePart.length() == 0) return; List<String>
	 * potentials = new ArrayList<String>(); for (int index = 0; index <
	 * this.minecraft.networkManager.players.size(); index++) { if
	 * (this.minecraft
	 * .networkManager.players.get(index).name.toLowerCase().contains
	 * (namePart .toLowerCase())) {
	 * potentials.add(this.minecraft.networkManager
	 * .players.get(index).name); } } if (potentials.size() == 0) return; if
	 * (potentials.size() == 1) { this.inputLine = potentials.get(0); } else
	 * { try { this.minecraft.hud.addChat(joinToString((String[])
	 * potentials.toArray())); } catch (Exception e) { e.printStackTrace();
	 * } } }
	 */

	if (paramInt == Keyboard.KEY_UP) {
	    j = history.size();
	    if (historyPos < j) {
		historyPos += 1;
		inputLine = history.get(j - historyPos);
		caretPos = inputLine.length();
	    }
	}

	if (paramInt == Keyboard.KEY_DOWN) {
	    j = history.size();
	    if (historyPos > 0) {
		historyPos -= 1;

		if (historyPos > 0) {
		    inputLine = history.get(j - historyPos);
		} else {
		    inputLine = "";
		}
		caretPos = inputLine.length();
	    }
	}

	int j = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ,.:-_'*!\\\"#%/()=+?[]{}<>@|$;~`^"
		.indexOf(paramChar) >= 0 ? 1 : 0;

	if (j != 0) {
	    insertTextAtCaret(String.valueOf(paramChar));
	}
    }

    @Override
    protected final void onMouseClick(int x, int y, int clickType) {
	if (clickType == 0 && minecraft.hud.hoveredPlayer != null) {
	    insertTextAtCaret(minecraft.hud.hoveredPlayer + " ");
	}
	if (clickType == 0) {
	    for (int i = 0; i < minecraft.hud.chat.size(); i++) {
		for (ChatScreenData data : minecraft.hud.chatsOnScreen) {
		    if (x > data.bounds.x0 && x < data.bounds.x1
			    && y > data.bounds.y0 && y < data.bounds.y1) {
			ChatClickData chatClickData = new ChatClickData(
				fontRenderer, minecraft.hud.chat.get(i), x, y);
			if (data.string == chatClickData.message) {
			    for (LinkData ld : chatClickData.getClickedUrls()) {
				if (ld != null) {
				    if (x > ld.x0 && x < ld.x1
					    && y > data.bounds.y0
					    && y < data.bounds.y1) {
					String s = FontRenderer
						.stripColor(ld.link);
					URI uri = chatClickData.getURI(s);
					if (uri != null) {
					    openWebpage(uri);
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

    @Override
    public final void onOpen() {
	Keyboard.enableRepeatEvents(true);
    }

    public void openWebpage(URI uri) {
	Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop()
		: null;
	if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	    try {
		desktop.browse(uri);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * The background color of the chat.
     */
    public static int ChatRGB = new java.awt.Color(0, 0, 0, 130).getRGB();

    @Override
	public void render(int paramInt1, int paramInt2) {
		// super.drawBox(2, height - 14, width - 2, height - 2, -2147483648);
		char[] temp = new char[128];
		for (int a = 0; a < inputLine.length(); a++) {
			temp[a] = inputLine.toCharArray()[a];
		}

		if (temp.length == 0) {
			temp[temp.length] = tickCount / 6 % 2 == 0 ? '_' : ' ';
		} else {
			temp[caretPos] = tickCount / 6 % 2 == 0 ? '_' : temp[caretPos];
		}

		String string = "";
		String messageNoCaret = "";
		for (int i = 0; i < temp.length; i++) {
			if(i != caretPos){
				messageNoCaret += temp[i];
			}
			string += temp[i];
		}
		int x1 = 2;
		/* Add the beginning position of the box
		 * + the length of '> _'
		 * + the length of the trimmed message
		 * + the x position of the '>  _' string.
		 */
		int x2 = x1 + fontRenderer.getWidth("> _" + messageNoCaret.trim()) + 4;

		int y1 = height - 14;
		int y2 = y1 + 12;
		super.drawBox(x1, y1, x2, y2, ChatRGB);

		drawString(fontRenderer, "> " + string, 4, height - 12, 14737632);
		float scale = 0.6f;
		int x = Mouse.getEventX() * width / minecraft.width;
		int y = height - Mouse.getEventY() * height / minecraft.height - 1;
		for (int i = 0; i < minecraft.hud.chat.size(); i++) {
			for (ChatScreenData data : minecraft.hud.chatsOnScreen) {
				if (x > data.bounds.x0 && x < data.bounds.x1
						&& y > data.bounds.y0 && y < data.bounds.y1) {
					ChatClickData chatClickData = new ChatClickData(
							fontRenderer, minecraft.hud.chat.get(i), x, y);
					if (data.string == chatClickData.message) {
						for (LinkData ld : chatClickData.getClickedUrls()) {
							if (ld != null) {
								if (x > ld.x0 && x < ld.x1
										&& y > data.bounds.y0
										&& y < data.bounds.y1) {
									super.drawBox(ld.x0, data.y - 1, ld.x1 + 3
											* scale, data.y + 9 * scale,
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
	Toolkit.getDefaultToolkit().getSystemClipboard()
		.setContents(localStringSelection, null);
    }

    @Override
    public final void tick() {
	++tickCount;
    }
}
