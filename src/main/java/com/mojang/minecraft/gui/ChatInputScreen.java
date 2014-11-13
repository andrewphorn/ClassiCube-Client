package com.mojang.minecraft.gui;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.mojang.minecraft.ChatClickData;
import com.mojang.minecraft.ChatClickData.LinkData;
import com.mojang.minecraft.ChatLine;
import com.mojang.util.LogUtil;
import com.mojang.minecraft.net.PacketType;

public class ChatInputScreen extends GuiScreen {

    public static List<String> history = new ArrayList<>();
    /**
     * The background color of the chat.
     */
    public static int ChatRGB = new java.awt.Color(0, 0, 0, 130).getRGB();
    public String inputLine = "";
    public int caretPos = 0;
    int j; // TODO What is this?
    private int tickCount = 0;
    private int historyPos = 0;

    private String getClipboard() {
        Transferable clipboard = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            if (clipboard != null && clipboard.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) clipboard.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException ex) {
        }
        return null;
    }

    private void setClipboard(String paramString) {
        StringSelection localStringSelection = new StringSelection(paramString);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(localStringSelection, null);
    }

    private void insertTextAtCaret(String paramString) {
        int i;
        if (minecraft.session != null) {
            i = 64 - minecraft.session.username.length() - 2;
        } else {
            i = 64;
        }

        int j = paramString.length();
        inputLine = inputLine.substring(0, caretPos) + paramString + inputLine.substring(caretPos);
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
            minecraft.setCurrentScreen(null);
            return;
        }
        if (paramInt == Keyboard.KEY_F2) {
            minecraft.setCurrentScreen(null);
            minecraft.takeAndSaveScreenshot(minecraft.width, minecraft.height);
            minecraft.setCurrentScreen(this);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
            return;
        }

        if (paramInt == Keyboard.KEY_RETURN) { // 28
            String message = inputLine.trim();
            if (message.toLowerCase().startsWith("/client")) {
                if (message.equalsIgnoreCase("/client debug")) {
                    minecraft.settings.showDebug = !minecraft.settings.showDebug;
                    minecraft.hud.addChat("&eDebug: &a" + (!minecraft.settings.showDebug ? "On" : "Off") + " -> "
                            + (minecraft.settings.showDebug ? "On" : "Off"));
                } else if (message.equalsIgnoreCase("/client gui")) {
                    minecraft.canRenderGUI = !minecraft.canRenderGUI;
                    minecraft.hud.addChat("&eGUI: &a" + (!minecraft.canRenderGUI ? "On" : "Off") + " -> "
                            + (minecraft.canRenderGUI ? "On" : "Off"));
                } else if (message.equalsIgnoreCase("/client hacks")) {
                    minecraft.settings.hacksEnabled = !minecraft.settings.hacksEnabled;
                    minecraft.hud.addChat("&eHacks: &a" + (!minecraft.settings.hacksEnabled ? "Enabled" : "Disabled")
                            + " -> " + (minecraft.settings.hacksEnabled ? "Enabled" : "Disabled"));
                } else if (message.equalsIgnoreCase("/client speedhack")) {
                    if (minecraft.settings.hackType == 1) {
                        minecraft.settings.hackType = 0;
                    } else {
                        minecraft.settings.hackType = 1;
                    }
                    minecraft.hud.addChat("&eSpeedHack: &a"
                            + (!(minecraft.settings.hackType == 0) ? "Normal" : "Advanced") + " -> "
                            + ((minecraft.settings.hackType == 0) ? "Normal" : "Advanced"));
                } else if (message.equalsIgnoreCase("/client help")) {
                    minecraft.hud.addChat("&a/Client GUI &e- Toggles the GUI");
                    minecraft.hud.addChat("&a/Client Debug &e- Toggles the showing of the debug information");
                    minecraft.hud.addChat("&a/Client Hacks &e- Toggles being able to use hacks");
                    minecraft.hud.addChat("&a/Client SpeedHack &e- Switches between normal and advanced speedhack");
                    minecraft.hud.addChat("&a/Client Status &e- Lists the settings and their current state");
                    minecraft.hud.addChat("&a/Client Help &e- Displays this current page");
                    minecraft.hud.addChat("&eTell us what you want as a command!");
                } else if (message.equalsIgnoreCase("/client status")) {
                    minecraft.hud.addChat("&eCurrent client command settings:");
                    minecraft.hud.addChat("  &eGUI: &a" + (minecraft.canRenderGUI ? "On" : "Off"));
                    minecraft.hud.addChat("  &eDebug: &a" + (minecraft.settings.showDebug ? "On" : "Off"));
                    minecraft.hud.addChat("  &eHacks: &a" + (minecraft.settings.hacksEnabled ? "Enabled" : "Disabled"));
                    minecraft.hud.addChat("  &eSpeedHack: &a"
                            + ((minecraft.settings.hackType == 0) ? "Normal" : "Advanced"));
                } else {
                    minecraft.hud.addChat("&eTo see a list of client commands type in &a/Client Help");
                }
            } else if (minecraft.session == null) {
                minecraft.hud.addChat("&f" + message);
            } else if (message.length() > 0) {
                if ((message = message.trim()).length() > 0) {
                    minecraft.networkManager.send(PacketType.CHAT_MESSAGE, -1, message);
                }
            }
            history.add(message);
            minecraft.setCurrentScreen(null);
            return;
        }

        int i = inputLine.length();
        if (paramInt == Keyboard.KEY_BACK && i > 0 && caretPos > 0) {
            inputLine = inputLine.substring(0, caretPos - 1) + inputLine.substring(caretPos);
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

        if (Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA)
                || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
            if (paramInt == Keyboard.KEY_V) {
                paramChar = '\000';
                String clipboardText = getClipboard();
                if (clipboardText != null) {
                    insertTextAtCaret(clipboardText);
                }
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
            for (ChatScreenData chatLine : minecraft.hud.chatsOnScreen) {
                if (x >= chatLine.bounds.maxX && x <= chatLine.bounds.minX && y >= chatLine.bounds.maxY && y <= chatLine.bounds.minY) {
                    ChatClickData chatClickData = new ChatClickData(fontRenderer, chatLine.string);
                    for (LinkData link : chatClickData.getClickedUrls()) {
                        if (x >= 4 + link.x0 && x <= 4 + link.x1) {
                            String s = FontRenderer.stripColor(link.link);
                            URI uri = chatClickData.getURI(s);
                            if (uri != null) {
                                openWebPage(uri);
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

    public void openWebPage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception ex) {
                LogUtil.logError("Error opening a chat link: " + uri, ex);
            }
        }
    }

    @Override
    public void render(int paramInt1, int paramInt2) {
        String lineWithCaret, line;

        lineWithCaret = "> " + inputLine.substring(0, caretPos) + "_";
        if (inputLine.length() > caretPos) {
            lineWithCaret += inputLine.substring(caretPos + 1);
        }

        boolean makeCaret = (tickCount / 6 % 2 == 0);
        if (makeCaret) {
            line = lineWithCaret;
        } else {
            line = "> " + inputLine;
        }

        int x1 = 2;
        /*
         * Add the beginning position of the box + the length of '> _' + the
         * length of the trimmed message + the x position of the '> _' string.
         */
        int x2 = x1 + fontRenderer.getWidth(lineWithCaret) + 4;

        int y1 = height - 14;
        int y2 = y1 + 12;
        GuiScreen.drawBox(x1, y1, x2, y2, ChatRGB);
        drawString(fontRenderer, line, 4, height - 12, 14737632);

        int chatSpacing = (int) Math.ceil(9 * minecraft.settings.scale);
        int x = Mouse.getEventX() * width / minecraft.width;
        int y = height - Mouse.getEventY() * height / minecraft.height - 1;
        for (ChatScreenData chatLine : minecraft.hud.chatsOnScreen) {
            if (x > chatLine.bounds.maxX && x < chatLine.bounds.minX && y > chatLine.bounds.maxY && y < chatLine.bounds.minY) {
                ChatClickData chatClickData = new ChatClickData(fontRenderer, chatLine.string);
                for (LinkData link : chatClickData.getClickedUrls()) {
                    if (x > 3 + link.x0 && x < 3 + link.x1) {
                        GuiScreen.drawBox(3 + link.x0, chatLine.y, 5 + link.x1, chatLine.y + chatSpacing, -2147483648);
                    }
                }
            }
        }
    }

    @Override
    public final void tick() {
        ++tickCount;
    }
}
