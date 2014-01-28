package com.mojang.minecraft.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.ChatLine;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.PlayerListNameData;
import com.mojang.minecraft.gamemode.SurvivalGameMode;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.player.Inventory;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.util.MathHelper;

public final class HUDScreen extends Screen {
    public List<ChatLine> chat = new ArrayList<ChatLine>();
    private Random random = new Random();
    private Minecraft mc;
    public int width;
    public int height;
    public String hoveredPlayer = null;
    public int ticks = 0;
    public static String Compass = "";
    public static String ServerName = "";
    public static String UserDetail = "";
    public static String BottomRight1 = "";
    public static String BottomRight2 = "";
    public static String BottomRight3 = "";
    public static String Announcement = "";
    public List<ChatScreenData> chatsOnScreen = new ArrayList<ChatScreenData>();

    int Page = 0;

    public HUDScreen(Minecraft var1, int var2, int var3) {
	mc = var1;
	width = var2 * 240 / var3;
	height = var3 * 240 / var3;
    }

    public final void addChat(String var1) {
	if (var1.contains("^detail.user=")) {
	    Compass = var1.replace("^detail.user=", "");
	    return;
	}

	chat.add(0, new ChatLine(var1));

	while (chat.size() > 50) {
	    chat.remove(chat.size() - 1);
	}

    }

    public int FindGroupChanges(int Page,
	    List<PlayerListNameData> playerListNames) {
	int groupChanges = 0;
	String lastGroupName = "";
	int rangeA = 28 * Page;
	int rangeB = rangeA + 28;
	rangeB = Math.min(rangeB, playerListNames.size());
	List<PlayerListNameData> namesToPrint = new ArrayList<PlayerListNameData>();
	for (int k = rangeA; k < rangeB; k++) {
	    namesToPrint.add(playerListNames.get(k));
	}
	for (int var11 = 0; var11 < namesToPrint.size(); ++var11) {
	    PlayerListNameData pi = namesToPrint.get(var11);
	    if (!lastGroupName.equals(pi.groupName)) {
		lastGroupName = pi.groupName;
		groupChanges++;
	    }
	}
	return groupChanges;
    }

    public final void render(float var1, boolean var2, int var3, int var4) {
	FontRenderer fontRenderer = mc.fontRenderer;
	mc.renderer.enableGuiMode();
	TextureManager var6 = mc.textureManager;
	GL11.glBindTexture(3553, mc.textureManager.load("/gui/gui.png"));
	ShapeRenderer var7 = ShapeRenderer.instance;
	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	GL11.glEnable(3042);
	Inventory var8 = mc.player.inventory;
	imgZ = -90.0F;
	drawImage(width / 2 - 91, height - 22, 0, 0, 182, 22);
	drawImage(width / 2 - 91 - 1 + var8.selected * 20, height - 22 - 1, 0,
		22, 24, 22);
	GL11.glBindTexture(3553, mc.textureManager.load("/gui/icons.png"));
	drawImage(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
	boolean var9 = (mc.player.invulnerableTime / 3 & 1) == 1;
	if (mc.player.invulnerableTime < 10) {
	    var9 = false;
	}

	int var10 = mc.player.health;
	int var11 = mc.player.lastHealth;
	random.setSeed(ticks * 312871);
	int var12;
	int i;
	int var15;
	int var26;
	if (mc.gamemode.isSurvival()) {
	    for (var12 = 0; var12 < 10; ++var12) {
		byte var13 = 0;
		if (var9) {
		    var13 = 1;
		}

		i = width / 2 - 91 + (var12 << 3);
		var15 = height - 32;
		if (var10 <= 4) {
		    var15 += random.nextInt(2);
		}

		drawImage(i, var15, 16 + var13 * 9, 0, 9, 9);
		if (var9) {
		    if ((var12 << 1) + 1 < var11) {
			drawImage(i, var15, 70, 0, 9, 9);
		    }

		    if ((var12 << 1) + 1 == var11) {
			drawImage(i, var15, 79, 0, 9, 9);
		    }
		}

		if ((var12 << 1) + 1 < var10) {
		    drawImage(i, var15, 52, 0, 9, 9);
		}

		if ((var12 << 1) + 1 == var10) {
		    drawImage(i, var15, 61, 0, 9, 9);
		}
	    }

	    if (mc.player.isUnderWater()) {
		var12 = (int) Math
			.ceil((mc.player.airSupply - 2) * 10.0D / 300.0D);
		var26 = (int) Math.ceil(mc.player.airSupply * 10.0D / 300.0D)
			- var12;

		for (i = 0; i < var12 + var26; ++i) {
		    if (i < var12) {
			drawImage(width / 2 - 91 + (i << 3), height - 32 - 9,
				16, 18, 9, 9);
		    } else {
			drawImage(width / 2 - 91 + (i << 3), height - 32 - 9,
				25, 18, 9, 9);
		    }
		}
	    }
	}

	GL11.glDisable(3042);

	String var23;
	for (var12 = 0; var12 < var8.slots.length; ++var12) {
	    var26 = width / 2 - 90 + var12 * 20;
	    i = height - 16;
	    if ((var15 = var8.slots[var12]) > 0) {
		GL11.glPushMatrix();
		GL11.glTranslatef(var26, i, -50.0F);
		if (var8.popTime[var12] > 0) {
		    float var18;
		    float var21 = -MathHelper
			    .sin((var18 = (var8.popTime[var12] - var1) / 5.0F)
				    * var18 * 3.1415927F) * 8.0F;
		    float var19 = MathHelper.sin(var18 * var18 * 3.1415927F) + 1.0F;
		    float var16 = MathHelper.sin(var18 * 3.1415927F) + 1.0F;
		    GL11.glTranslatef(10.0F, var21 + 10.0F, 0.0F);
		    GL11.glScalef(var19, var16, 1.0F);
		    GL11.glTranslatef(-10.0F, -10.0F, 0.0F);
		}

		GL11.glScalef(10.0F, 10.0F, 10.0F);
		GL11.glTranslatef(1.0F, 0.5F, 0.0F);
		GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-1.5F, 0.5F, 0.5F);
		GL11.glScalef(-1.0F, -1.0F, -1.0F);
		int var20 = var6.load("/terrain.png");
		GL11.glBindTexture(3553, var20);
		var7.begin();
		Block.blocks[var15].renderFullbright(var7);
		var7.end();
		GL11.glPopMatrix();
		if (var8.count[var12] > 1) {
		    var23 = "" + var8.count[var12];
		    fontRenderer.render(var23,
			    var26 + 19 - fontRenderer.getWidth(var23), i + 6,
			    16777215);
		}
	    }
	}
	// if (Minecraft.isSinglePlayer)
	// var5.render("Development Build", 2, 32, 16777215);
	if (mc.settings.showDebug) {
	    GL11.glPushMatrix();
	    GL11.glScalef(0.7F, 0.7F, 1.0F);
	    fontRenderer.render("ClassiCube", 2, 2, 16777215); // lol fuck that.
	    fontRenderer.render(mc.debug, 2, 12, 16777215);
	    fontRenderer.render("Position: (" + (int) mc.player.x + ", "
		    + (int) mc.player.y + ", " + (int) mc.player.z + ")", 2,
		    22, 16777215);
	    GL11.glPopMatrix();

	    fontRenderer.render(Compass,
		    width - (fontRenderer.getWidth(Compass) + 2), 12, 16777215);

	    fontRenderer.render(ServerName,
		    width - (fontRenderer.getWidth(ServerName) + 2), 2,
		    16777215);

	    fontRenderer.render(UserDetail,
		    width - (fontRenderer.getWidth(UserDetail) + 2), 24,
		    16777215);

	    fontRenderer.render(BottomRight1,
		    width - (fontRenderer.getWidth(BottomRight1) + 2),
		    height - 35, 16777215);
	    fontRenderer.render(BottomRight2,
		    width - (fontRenderer.getWidth(BottomRight2) + 2),
		    height - 45, 16777215);
	    fontRenderer.render(BottomRight3,
		    width - (fontRenderer.getWidth(BottomRight3) + 2),
		    height - 55, 16777215);

	    GL11.glPushMatrix();
	    GL11.glScalef(1.5F, 1.5F, 1.0F);
	    fontRenderer.render(Announcement,
		    (width / 3) - (fontRenderer.getWidth(Announcement) / 2),
		    35, 16777215);
	    GL11.glPopMatrix();
	}
	GL11.glPushMatrix();
	GL11.glScalef(0.7F, 0.7F, 1.0F);
	if (mc.player.flyingMode && !mc.player.noPhysics) {
	    fontRenderer.render("Fly: ON.", 2, 32, 16777215);
	} else if (!mc.player.flyingMode && mc.player.noPhysics) {
	    fontRenderer.render("NoClip: ON.", 2, 32, 16777215);
	} else if (mc.player.flyingMode && mc.player.noPhysics) {
	    fontRenderer.render("Fly: ON. NoClip: ON", 2, 32, 16777215);
	}
	GL11.glPopMatrix();
	if (mc.gamemode instanceof SurvivalGameMode) {
	    String var24 = "Score: &e" + mc.player.getScore();
	    fontRenderer.render(var24,
		    width - fontRenderer.getWidth(var24) - 2, 2, 16777215);
	    fontRenderer.render("Arrows: " + mc.player.arrows, width / 2 + 8,
		    height - 33, 16777215);
	}

	byte chatLinesInScreen = 10; // chats per screen
	boolean isLargeChatScreen = false;
	if (mc.currentScreen instanceof ChatInputScreenExtension) {
	    chatLinesInScreen = 20;
	    isLargeChatScreen = true;
	}
	chatLinesInScreen = (byte) (chatLinesInScreen
		+ (chatLinesInScreen - chatLinesInScreen * mc.settings.scale) - 1);

	if (isLargeChatScreen) {
	    int chatX = 2;
	    int chatY = height - chatsOnScreen.size() * 9 - 30;
	    // The longest line's length

	    String longestMessageNoColor = "";
	    String longestMessage = "";
	    for (ChatScreenData line : chatsOnScreen) {
		String lineNoColor = FontRenderer.stripColor(line.string);
		if (lineNoColor.length() > longestMessageNoColor.length()) {
		    longestMessage = line.string;
		    longestMessageNoColor = lineNoColor;
		}
	    }
	    int messageWidth = fontRenderer.getWidth(longestMessage);
	    int chatWidth = chatX + messageWidth + 6;
	    // Get the chat lines, multiply by their height to get the chat
	    // height.
	    int chatHeight = chatY + chatsOnScreen.size() * 9 + 6;
	    drawBox(chatX, chatY, chatWidth, chatHeight,
		    ChatInputScreenExtension.ChatRGB);
	}
	chatsOnScreen.clear();
	for (i = 0; i < chat.size() && i < chatLinesInScreen; ++i) {
	    if (chat.get(i).time < 200 || isLargeChatScreen) {
		String message = chat.get(i).message;
		fontRenderer.render(message, 4, height - 8 - i * 9 - 27,
			16777215);
		// add click data for urls
		chatsOnScreen.add(new ChatScreenData(1, 8, 4, height - 8 - i
			* 9 - 27, message, fontRenderer));
	    }
	}

	i = width / 2;
	var15 = height / 2;
	hoveredPlayer = null;
	if (Keyboard.isCreated()) {
	    if (Keyboard.isKeyDown(15) && mc.networkManager != null
		    && mc.networkManager.isConnected()) {
		for (int l = 2; l < 11; l++) {
		    if (Keyboard.isKeyDown(l)) {
			Page = l - 2;
		    }
		}
		List<String> playersOnWorld = mc.networkManager.getPlayers();
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glBegin(7);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.7F);
		GL11.glVertex2f(i + 132, var15 - 72 - 12);
		GL11.glVertex2f(i - 132, var15 - 72 - 12);
		GL11.glColor4f(0.2F, 0.2F, 0.2F, 0.8F);
		GL11.glVertex2f(i - 132, var15 + 72);
		GL11.glVertex2f(i + 132, var15 + 72);
		GL11.glEnd();
		GL11.glDisable(3042);
		GL11.glEnable(3553);
		boolean drawDefault = false;
		List<PlayerListNameData> playerListNames = mc.playerListNameData;
		if (playerListNames.isEmpty()) {
		    drawDefault = true;
		}
		int maxStringsPerColumn = 14;
		int maxStringsPerScreen = 28;

		var23 = !drawDefault ? "Players online: (Page " + (Page + 1)
			+ ")" : "Players online:";
		fontRenderer.render(var23,
			i - fontRenderer.getWidth(var23) / 2, var15 - 64 - 12,
			25855);
		if (drawDefault) {
		    for (var11 = 0; var11 < playersOnWorld.size(); ++var11) {
			int var28 = i + var11 % 2 * 120 - 120;
			int var17 = var15 - 64 + (var11 / 2 << 3);
			if (var2 && var3 >= var28 && var4 >= var17
				&& var3 < var28 + 120 && var4 < var17 + 8) {
			    hoveredPlayer = playersOnWorld.get(var11);
			    fontRenderer.renderNoShadow(
				    playersOnWorld.get(var11), var28 + 2,
				    var17, 16777215);
			} else {
			    fontRenderer.renderNoShadow(
				    playersOnWorld.get(var11), var28, var17,
				    15658734);
			}
		    }
		} else { // draw the new screen
		    String lastGroupName = "";
		    int x = i + 8;
		    int y = var15 - 73;
		    int groupChanges = 0;
		    boolean hasStartedNewColumn = false;

		    List<PlayerListNameData> namesToPrint = new ArrayList<PlayerListNameData>();

		    for (int m = 0; m < Page; m++) {
			groupChanges += FindGroupChanges(m, playerListNames);
		    }
		    int rangeA = maxStringsPerScreen * Page - groupChanges;
		    int rangeB = rangeA + maxStringsPerScreen
			    - FindGroupChanges(Page, playerListNames);
		    rangeB = Math.min(rangeB, playerListNames.size());
		    for (int k = rangeA; k < rangeB; k++) {
			namesToPrint.add(playerListNames.get(k));
		    }
		    int groupsOnThisPage = 0;
		    for (var11 = 0; var11 < namesToPrint.size(); ++var11) {
			if (var11 < maxStringsPerColumn - groupsOnThisPage) {
			    x = i - 128 + 8;
			} else {
			    if (var11 >= maxStringsPerColumn - groupsOnThisPage
				    && !hasStartedNewColumn) {
				y = var15 - 73;
				hasStartedNewColumn = true;
			    }
			    x = i + 8;
			}

			y += 9;
			PlayerListNameData pi = namesToPrint.get(var11);
			if (!lastGroupName.equals(pi.groupName)) {
			    lastGroupName = pi.groupName;
			    fontRenderer.render(lastGroupName, x + 2, y, 51455);
			    groupsOnThisPage++;
			    y += 9;
			}
			String playerName = FontRenderer
				.stripColor(pi.playerName);
			String listName = pi.listName;
			if (var2 && var3 >= x && var4 >= y && var3 < x + 120
				&& var4 < y + 8) {
			    // if your mouse is hovered over this name
			    hoveredPlayer = playerName;
			    fontRenderer.renderNoShadow(listName, x + 8, y,
				    16777215);
			} else { // else render a normal name
			    fontRenderer.renderNoShadow(listName, x + 6, y,
				    15658734);
			}
		    }
		}
	    }
	}
    }
}
