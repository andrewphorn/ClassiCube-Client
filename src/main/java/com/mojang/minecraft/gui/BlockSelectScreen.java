package com.mojang.minecraft.gui;

import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.SessionData;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.level.tile.BlockID;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.render.TextureManager;

public final class BlockSelectScreen extends GuiScreen {

    boolean lessThan49 = SessionData.allowedBlocks.size() <= 50;
    int BlocksPerRow = 13;
    int Spacing = 20;

    private final Timer timer = new Timer();

    private final int miliseconds = 30;
    public TimerTask timertask;

    float lastRotation = 0;

    public BlockSelectScreen() {
        grabsMouse = true;
        start();
        if (lessThan49) {
            BlocksPerRow = 11;
            Spacing = 24;
        }
    }

    String GetBlockName(int id) {
        String s;
        if (id < 0 || id > 255) {
            return "";
        }
        try {
            Block b = SessionData.allowedBlocks.get(id);
            if (b == null) {
                return "";
            }
            int ID = b.id;
            BlockID bid = BlockID.values()[ID + 1];
            if (bid == null) {
                s = "";
            } else {
                s = bid.name();
            }
        } catch (Exception e) {
            return "";
        }
        return s;
    }

    private int getBlockOnScreen(int var1, int var2) {
        for (int var3 = 0; var3 < SessionData.allowedBlocks.size(); ++var3) {
            int var4 = width / 2 + var3 % BlocksPerRow * Spacing + -128 - 3;
            int var5 = height / 2 + var3 / BlocksPerRow * Spacing + -60 + 3;
            if (var1 >= var4 && var1 <= var4 + 22 && var2 >= var5 - BlocksPerRow
                    && var2 <= var5 + BlocksPerRow) {
                return var3;
            }
        }

        return -1;
    }

    @Override
    protected final void onMouseClick(int var1, int var2, int var3) {
        if (var3 == 0) {
            minecraft.player.inventory.replaceSlot(getBlockOnScreen(var1, var2));
            minecraft.setCurrentScreen((GuiScreen) null);
        }

    }

    @Override
    public final void render(int var1, int var2) {
        var1 = getBlockOnScreen(var1, var2);
        if (lessThan49) {
            drawFadingBox(width / 2 - 140, 30, width / 2 + 140, 195, -1878719232, -1070583712);
        } else {
            drawFadingBox(width / 2 - 140, 30, width / 2 + 140, 180, -1878719232, -1070583712);
        }
        if (var1 >= 0) {
            var2 = width / 2 + var1 % BlocksPerRow * Spacing + -128;
            if (lessThan49) {
                drawCenteredString(fontRenderer, GetBlockName(var1), width / 2, 180, 16777215);
            } else {
                drawCenteredString(fontRenderer, GetBlockName(var1), width / 2, 165, 16777215);
            }
        }

        drawCenteredString(fontRenderer, "Select block", width / 2, 40, 16777215);
        TextureManager var7 = minecraft.textureManager;
        ShapeRenderer var8 = ShapeRenderer.instance;
        var2 = var7.load("/terrain.png");
        GL11.glBindTexture(3553, var2);

        for (var2 = 0; var2 < SessionData.allowedBlocks.size(); ++var2) {
            Block var4 = SessionData.allowedBlocks.get(var2);
            if (var4 != null) {
                GL11.glPushMatrix();
                int var5 = width / 2 + var2 % BlocksPerRow * Spacing + -128;
                int var6 = height / 2 + var2 / BlocksPerRow * Spacing + -60;
                GL11.glTranslatef(var5, var6, 0F);
                GL11.glScalef(9F, 9F, 9F);
                GL11.glTranslatef(1F, 0.5F, 8F);
                GL11.glRotatef(-30F, 1F, 0F, 0F);
                GL11.glRotatef(45F, 0F, 1F, 0F);
                if (var1 == var2) {
                    GL11.glScalef(1.6F, 1.6F, 1.6F);
                    GL11.glRotatef(lastRotation, 0F, 1F, 0F);
                }

                GL11.glTranslatef(-1.5F, 0.5F, 0.5F);
                GL11.glScalef(-1F, -1F, -1F);
                var8.begin();
                var4.renderFullBrightness(var8);
                var8.end();
                GL11.glPopMatrix();
            }
        }

    }

    void rotate() {
        lastRotation += 2.7F;
    }

    public void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                rotate();
                // timer.cancel();
            }
        }, miliseconds, miliseconds);
    }
}