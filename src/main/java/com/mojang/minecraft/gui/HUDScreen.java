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
import com.mojang.minecraft.render.texture.Textures;
import com.mojang.util.MathHelper;

public final class HUDScreen extends Screen {
    public static String Compass = "";
    public static String ServerName = "";
    public static String UserDetail = "";
    public static String BottomRight1 = "";
    public static String BottomRight2 = "";
    public static String BottomRight3 = "";
    public static String Announcement = "";
    public static long AnnouncementTimer = 0;
    public static List<ChatLine> chat = new ArrayList<>();
    public int width;
    public int height;
    public String hoveredPlayer = null;
    public int ticks = 0;
    public List<ChatScreenData> chatsOnScreen = new ArrayList<>();
    int page = 0;
    private Random random = new Random();
    private Minecraft minecraft;
    public static int chatLocation = 0;
    
    public HUDScreen(Minecraft minecraft, int width, int height) {
        this.minecraft = minecraft;
        this.width = width * 240 / height;
        this.height = height * 240 / height;
    }

    public final void addChat(String text) {
        if (text.contains("^detail.user=")) {
            Compass = text.replace("^detail.user=", "");
            return;
        }

        chat.add(0, new ChatLine(text));

        while (chat.size() > 1000) {
            chat.remove(chat.size() - 1);
        }

    }

    public int findGroupChanges(int Page, List<PlayerListNameData> playerListNames) {
        int groupChanges = 0;
        String lastGroupName = "";
        int rangeA = 28 * Page;
        int rangeB = rangeA + 28;
        rangeB = Math.min(rangeB, playerListNames.size());
        List<PlayerListNameData> namesToPrint = new ArrayList<>();
        for (int k = rangeA; k < rangeB; k++) {
            namesToPrint.add(playerListNames.get(k));
        }
        for (PlayerListNameData pi : namesToPrint) {
            if (!lastGroupName.equals(pi.groupName)) {
                lastGroupName = pi.groupName;
                groupChanges++;
            }
        }
        return groupChanges;
    }

    public final void render(float var1, boolean var2, int var3, int var4) {
        FontRenderer fontRenderer = minecraft.fontRenderer;
        minecraft.renderer.enableGuiMode();
        if (!minecraft.canRenderGUI) return;
        TextureManager var6 = minecraft.textureManager;
        GL11.glBindTexture(3553, minecraft.textureManager.load(Textures.GUI));
        ShapeRenderer var7 = ShapeRenderer.instance;
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(3042);
        Inventory var8 = minecraft.player.inventory;
        imgZ = -90F;
        drawImage(width / 2 - 91, height - 22, 0, 0, 182, 22);
        drawImage(width / 2 - 91 - 1 + var8.selected * 20, height - 22 - 1, 0, 22, 24, 22);
        GL11.glBindTexture(3553, minecraft.textureManager.load(Textures.ICONS));
        drawImage(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
        boolean var9 = (minecraft.player.invulnerableTime / 3 & 1) == 1;
        if (minecraft.player.invulnerableTime < 10) {
            var9 = false;
        }

        int health = minecraft.player.health;
        int lastHealth = minecraft.player.lastHealth;
        random.setSeed(ticks * 312871);
        int var12;
        int i;
        int var15;
        int var26;
        if (minecraft.gamemode.isSurvival()) {
            for (var12 = 0; var12 < 10; ++var12) {
                byte var13 = 0;
                if (var9) {
                    var13 = 1;
                }

                i = width / 2 - 91 + (var12 << 3);
                var15 = height - 32;
                if (health <= 4) {
                    var15 += random.nextInt(2);
                }

                drawImage(i, var15, 16 + var13 * 9, 0, 9, 9);
                if (var9) {
                    if ((var12 << 1) + 1 < lastHealth) {
                        drawImage(i, var15, 70, 0, 9, 9);
                    }

                    if ((var12 << 1) + 1 == lastHealth) {
                        drawImage(i, var15, 79, 0, 9, 9);
                    }
                }

                if ((var12 << 1) + 1 < health) {
                    drawImage(i, var15, 52, 0, 9, 9);
                }

                if ((var12 << 1) + 1 == health) {
                    drawImage(i, var15, 61, 0, 9, 9);
                }
            }

            if (minecraft.player.isUnderWater()) {
                var12 = (int) Math.ceil((minecraft.player.airSupply - 2) * 10D / 300D);
                var26 = (int) Math.ceil(minecraft.player.airSupply * 10D / 300D) - var12;

                for (i = 0; i < var12 + var26; ++i) {
                    if (i < var12) {
                        drawImage(width / 2 - 91 + (i << 3), height - 32 - 9, 16, 18, 9, 9);
                    } else {
                        drawImage(width / 2 - 91 + (i << 3), height - 32 - 9, 25, 18, 9, 9);
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
                GL11.glTranslatef(var26, i, -50F);
                if (var8.popTime[var12] > 0) {
                    float var18;
                    float var21 = -MathHelper.sin((var18 = (var8.popTime[var12] - var1) / 5F)
                            * var18 * (float) Math.PI) * 8F;
                    float var19 = MathHelper.sin(var18 * var18 * (float) Math.PI) + 1F;
                    float var16 = MathHelper.sin(var18 * (float) Math.PI) + 1F;
                    GL11.glTranslatef(10F, var21 + 10F, 0F);
                    GL11.glScalef(var19, var16, 1F);
                    GL11.glTranslatef(-10F, -10F, 0F);
                }

                GL11.glScalef(10F, 10F, 10F);
                GL11.glTranslatef(1F, 0.5F, 0F);
                GL11.glRotatef(-30F, 1F, 0F, 0F);
                GL11.glRotatef(45F, 0F, 1F, 0F);
                GL11.glTranslatef(-1.5F, 0.5F, 0.5F);
                GL11.glScalef(-1F, -1F, -1F);
                int var20 = var6.load(Textures.TERRAIN);
                GL11.glBindTexture(3553, var20);
                var7.begin();
                Block.blocks[var15].renderFullBrightness(var7);
                var7.end();
                GL11.glPopMatrix();
                if (var8.count[var12] > 1) {
                    var23 = "" + var8.count[var12];
                    fontRenderer.render(var23, var26 + 19 - fontRenderer.getWidth(var23), i + 6,
                            16777215);
                }
            }
        }
        // if (Minecraft.isSinglePlayer)
        // var5.render("Development Build", 2, 32, 16777215);
        if (minecraft.settings.showDebug) {
            GL11.glPushMatrix();
            GL11.glScalef(0.7F, 0.7F, 1F);
            fontRenderer.render("ClassiCube", 2, 2, 16777215);
            fontRenderer.render(minecraft.debug, 2, 12, 16777215);
            fontRenderer.render("Position: (" + (int) minecraft.player.x + ", " + (int) minecraft.player.y + ", "
                    + (int) minecraft.player.z + ")", 2, 22, 16777215);
            GL11.glPopMatrix();
        }
        fontRenderer.render(Compass, width - (fontRenderer.getWidth(Compass) + 2), 12, 16777215);

        fontRenderer.render(ServerName, width - (fontRenderer.getWidth(ServerName) + 2), 2,
                16777215);

        fontRenderer.render(UserDetail, width - (fontRenderer.getWidth(UserDetail) + 2), 24,
                16777215);

        fontRenderer.render(BottomRight1, width - (fontRenderer.getWidth(BottomRight1) + 2),
                height - 35, 16777215);
        fontRenderer.render(BottomRight2, width - (fontRenderer.getWidth(BottomRight2) + 2),
                height - 45, 16777215);
        fontRenderer.render(BottomRight3, width - (fontRenderer.getWidth(BottomRight3) + 2),
                height - 55, 16777215);

        GL11.glPushMatrix();
        GL11.glScalef(1.5F, 1.5F, 1F);
        fontRenderer.render(Announcement, (width / 3) - (fontRenderer.getWidth(Announcement) / 2),
                35, 16777215);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glScalef(0.7F, 0.7F, 1F);
        if (minecraft.player.flyingMode && !minecraft.player.noPhysics) {
            fontRenderer.render("Fly: ON.", 2, 32, 16777215);
        } else if (!minecraft.player.flyingMode && minecraft.player.noPhysics) {
            fontRenderer.render("NoClip: ON.", 2, 32, 16777215);
        } else if (minecraft.player.flyingMode && minecraft.player.noPhysics) {
            fontRenderer.render("Fly: ON. NoClip: ON", 2, 32, 16777215);
        }
        GL11.glPopMatrix();
        if (minecraft.gamemode instanceof SurvivalGameMode) {
            String var24 = "Score: &e" + minecraft.player.getScore();
            fontRenderer.render(var24, width - fontRenderer.getWidth(var24) - 2, 2, 16777215);
            fontRenderer
                    .render("Arrows: " + minecraft.player.arrows, width / 2 + 8, height - 33, 16777215);
        }

        byte chatLinesInScreen = 10; // chats per screen
        boolean isLargeChatScreen = false;
        if (minecraft.currentScreen instanceof ChatInputScreen) {
            chatLinesInScreen = 20;
            isLargeChatScreen = true;
        }
        chatLinesInScreen = (byte) (chatLinesInScreen
                + (chatLinesInScreen - chatLinesInScreen * minecraft.settings.scale) - 1);

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
            drawBox(chatX, chatY, chatWidth, chatHeight, ChatInputScreen.ChatRGB);
        }
        chatsOnScreen.clear();
        for (i = chatLocation; i < chat.size() && i < chatLinesInScreen + chatLocation; ++i) {
            if (chat.get(i).time < 200 || isLargeChatScreen) {
                String message = chat.get(i).message;
                fontRenderer.render(message, 4, height - 8 - (i - chatLocation) * 9 - 27, 16777215);
                // add click data for urls
                chatsOnScreen.add(new ChatScreenData(1, 8, 4, height - 8 - (i - chatLocation) * 9 - 27, message,
                        fontRenderer));
            }
        }

        i = width / 2;
        var15 = height / 2;
        hoveredPlayer = null;
        if (Keyboard.isCreated()) {
            if (Keyboard.isKeyDown(15) && minecraft.networkManager != null
                    && minecraft.networkManager.isConnected()) {
                for (int l = 2; l < 11; l++) {
                    if (Keyboard.isKeyDown(l)) {
                        page = l - 2;
                    }
                }
                List<String> playersOnWorld = minecraft.networkManager.getPlayerNames();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glColor4f(0F, 0F, 0F, 0.7F);
                GL11.glVertex2f(i + 132, var15 - 72 - 12);
                GL11.glVertex2f(i - 132, var15 - 72 - 12);
                GL11.glColor4f(0.2F, 0.2F, 0.2F, 0.8F);
                GL11.glVertex2f(i - 132, var15 + 72);
                GL11.glVertex2f(i + 132, var15 + 72);
                GL11.glEnd();
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                boolean drawDefault = false;
                List<PlayerListNameData> playerListNames = minecraft.playerListNameData;
                if (playerListNames.isEmpty()) {
                    drawDefault = true;
                }
                int maxStringsPerColumn = 14;
                int maxStringsPerScreen = 28;

                var23 = !drawDefault ? "Players online: (page " + (page + 1) + ")"
                        : "Players online:";
                fontRenderer.render(var23, i - fontRenderer.getWidth(var23) / 2, var15 - 64 - 12,
                        25855);
                if (drawDefault) {
                    for (lastHealth = 0; lastHealth < playersOnWorld.size(); ++lastHealth) {
                        int var28 = i + lastHealth % 2 * 120 - 120;
                        int var17 = var15 - 64 + (lastHealth / 2 << 3);
                        if (var2 && var3 >= var28 && var4 >= var17 && var3 < var28 + 120
                                && var4 < var17 + 8) {
                            hoveredPlayer = playersOnWorld.get(lastHealth);
                            fontRenderer.renderNoShadow(playersOnWorld.get(lastHealth), var28 + 2,
                                    var17, 16777215);
                        } else {
                            fontRenderer.renderNoShadow(playersOnWorld.get(lastHealth), var28, var17,
                                    15658734);
                        }
                    }
                } else { // draw the new screen
                    String lastGroupName = "";
                    int x = i + 8;
                    int y = var15 - 73;
                    int groupChanges = 0;
                    boolean hasStartedNewColumn = false;

                    List<PlayerListNameData> namesToPrint = new ArrayList<>();

                    for (int m = 0; m < page; m++) {
                        groupChanges += findGroupChanges(m, playerListNames);
                    }
                    int rangeA = maxStringsPerScreen * page - groupChanges;
                    int rangeB = rangeA + maxStringsPerScreen
                            - findGroupChanges(page, playerListNames);
                    rangeB = Math.min(rangeB, playerListNames.size());
                    for (int k = rangeA; k < rangeB; k++) {
                        namesToPrint.add(playerListNames.get(k));
                    }
                    int groupsOnThisPage = 0;
                    for (lastHealth = 0; lastHealth < namesToPrint.size(); ++lastHealth) {
                        if (lastHealth < maxStringsPerColumn - groupsOnThisPage) {
                            x = i - 128 + 8;
                        } else {
                            if (lastHealth >= maxStringsPerColumn - groupsOnThisPage
                                    && !hasStartedNewColumn) {
                                y = var15 - 73;
                                hasStartedNewColumn = true;
                            }
                            x = i + 8;
                        }

                        y += 9;
                        PlayerListNameData pi = namesToPrint.get(lastHealth);
                        if (!lastGroupName.equals(pi.groupName)) {
                            lastGroupName = pi.groupName;
                            fontRenderer.render(lastGroupName, x + 2, y, 51455);
                            groupsOnThisPage++;
                            y += 9;
                        }
                        String playerName = FontRenderer.stripColor(pi.playerName);
                        String listName = pi.listName;
                        if (var2 && var3 >= x && var4 >= y && var3 < x + 120 && var4 < y + 8) {
                            // if your mouse is hovered over this name
                            hoveredPlayer = playerName;
                            fontRenderer.renderNoShadow(listName, x + 8, y, 16777215);
                        } else { // else render a normal name
                            fontRenderer.renderNoShadow(listName, x + 6, y, 15658734);
                        }
                    }
                }
            }
        }
    }
}
