package com.mojang.minecraft;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.render.ShapeRenderer;

public final class ProgressBarDisplay {

    public static String text = "";
    public static String title = "";
    public static String terrainId = "";
    public static String sideId = "";
    public static String edgeId = "";
    private final Minecraft minecraft;
    private final long start = System.currentTimeMillis();

    public ProgressBarDisplay(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public final void setProgress(int progress) {
        if (!minecraft.isRunning) {
            throw new StopGameException();
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - start < 0L || currentTime - start >= 20L) {
                // TODO: fix flicker on rendering
                int var4 = minecraft.width * 240 / minecraft.height;
                int var5 = minecraft.height * 240 / minecraft.height;
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                ShapeRenderer renderer = ShapeRenderer.instance;
                int textureId = minecraft.textureManager.load("/dirt.png");
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
                float uvScale = 32f;
                renderer.begin();
                renderer.color(0x404040);
                renderer.vertexUV(0f, var5, 0f, 0f, var5 / uvScale);
                renderer.vertexUV(var4, var5, 0f, var4 / uvScale, var5 / uvScale);
                renderer.vertexUV(var4, 0f, 0f, var4 / uvScale, 0f);
                renderer.vertexUV(0f, 0f, 0f, 0f, 0f);
                renderer.end();

                if (progress >= 0) {
                    int barX = var4 / 2 - 50;
                    int barY = var5 / 2 + 16;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    renderer.begin();
                    renderer.color(0x808080);
                    renderer.vertex(barX, barY, 0f);
                    renderer.vertex(barX, barY + 2, 0f);
                    renderer.vertex(barX + 100, barY + 2, 0f);
                    renderer.vertex(barX + 100, barY, 0f);

                    renderer.color(0x80FF80);
                    renderer.vertex(barX, barY, 0f);
                    renderer.vertex(barX, barY + 2, 0f);
                    renderer.vertex(barX + progress, barY + 2, 0f);
                    renderer.vertex(barX + progress, barY, 0f);
                    renderer.end();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                minecraft.fontRenderer.render(title,
                        (var4 - minecraft.fontRenderer.getWidth(title)) / 2, var5 / 2 - 4 - 16,
                        16777215);
                minecraft.fontRenderer.render(text,
                        (var4 - minecraft.fontRenderer.getWidth(text)) / 2, var5 / 2 - 4 + 8,
                        16777215);
                Display.update();

                try {
                    Thread.yield();
                } catch (Exception e) {
                }
            }
        }
    }

    public final void setText(String message) {
        if (!minecraft.isRunning) {
            throw new StopGameException();
        } else {
            text = message;
            if (minecraft.session == null) {
                // Enable all hax in singleplayer
                HackState.setAllEnabled();
                return;
            }
        }
        setProgress(-1);
    }

    public final void setTitle(String title) {
        if (!minecraft.isRunning) {
            throw new StopGameException();
        } else {
            ProgressBarDisplay.title = title;
            int x = minecraft.width * 240 / minecraft.height;
            int y = minecraft.height * 240 / minecraft.height;
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0D, x, y, 0D, 100D, 300D);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0F, 0F, -200F);
        }
    }
}
