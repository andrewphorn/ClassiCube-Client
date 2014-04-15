package com.mojang.minecraft.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.render.ShapeRenderer;

/**
 * Base class for any kind of screen.
 */
public class Screen {

    protected float imgZ = 0;

    /**
     * Draws a box to the screen
     *
     * @param x1       X coordinate of the first point of the box.
     * @param y1       Y coordinate of the first point of the box.
     * @param x2       X coordinate of the second point of the box.
     * @param y2       Y coordinate of the second point of the box.
     * @param colorRGB The color of the box. See {@Color}
     */
    protected static void drawBox(float x1, float y1, float x2, float y2, int colorRGB) {

        float alpha = (colorRGB >>> 24) / 255F;
        float red = (colorRGB >> 16 & 255) / 255F;
        float green = (colorRGB >> 8 & 255) / 255F;
        float blue = (colorRGB & 255) / 255F;
        ShapeRenderer renderer = ShapeRenderer.instance;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(red, green, blue, alpha);

        renderer.begin();
        renderer.vertex(x1, y2, 0F);
        renderer.vertex(x2, y2, 0F);
        renderer.vertex(x2, y1, 0F);
        renderer.vertex(x1, y1, 0F);
        renderer.end();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Draws a box to the screen
     *
     * @param x1       X coordinate of the first point of the box.
     * @param y1       Y coordinate of the first point of the box.
     * @param x2       X coordinate of the second point of the box.
     * @param y2       Y coordinate of the second point of the box.
     * @param colorRGB The color of the box. See {@Color}
     */
    protected static void drawBox(int x1, int y1, int x2, int y2, int colorRGB) {
        drawBox((float) x1, (float) y1, (float) x2, (float) y2, colorRGB);
    }

    /**
     * Draws a string that is centered.
     *
     * @param renderer {@FontRenderer} used to render the used font.
     * @param text     Text to draw and center
     * @param x        X-Coordinate of position to draw.
     * @param y        Y-Coordinate of position to draw.
     * @param colorRGB The color of the box. See {@Color}
     */
    public static void drawCenteredString(FontRenderer renderer, String text, int x, int y,
                                          int colorRGB) {
        // Measure the length of the text with the current font and then divide
        // it by two
        drawString(renderer, text, x - renderer.getWidth(text) / 2, y, colorRGB);
    }

    /**
     * Draws a given string
     *
     * @param renderer {@FontRenderer} used to render the used font.
     * @param text     Text to draw
     * @param x        X-Coordinate of position to draw.
     * @param y        Y-Coordinate of position to draw.
     * @param colorRGB The color of the box. See {@Color}
     */
    public static void drawString(FontRenderer renderer, String text, int x, int y, int colorRGB) {
        renderer.render(text, x, y, colorRGB);
    }

    protected static void drawFadingBox(int x1, int y1, int x2, int y2, int bgColor, int frontColor) {
        GL11.glAlphaFunc(GL11.GL_GREATER, 0F);
        float bgAlpha = (bgColor >>> 24) / 255F;
        float bgRed = (bgColor >> 16 & 255) / 255F;
        float bgGreen = (bgColor >> 8 & 255) / 255F;
        float bgBlue = (bgColor & 255) / 255F;
        float frontAlpha = (frontColor >>> 24) / 255F;
        float frontRed = (frontColor >> 16 & 255) / 255F;
        float frontGreen = (frontColor >> 8 & 255) / 255F;
        float frontBlue = (frontColor & 255) / 255F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor4f(bgRed, bgGreen, bgBlue, bgAlpha);
        GL11.glVertex2f(x2, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glColor4f(frontRed, frontGreen, frontBlue, frontAlpha);
        GL11.glVertex2f(x1, y2);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
    }

    public final void drawImage(int screenX, int screenY, int u, int v, int width, int height) {
        float offset = 1F / 256F;
        ShapeRenderer renderer = ShapeRenderer.instance;
        renderer.begin();
        renderer.vertexUV(screenX, screenY + height, imgZ, u * offset, (v + height) * offset);
        renderer.vertexUV(screenX + width, screenY + height, imgZ, (u + width) * offset,
                (v + height) * offset);
        renderer.vertexUV(screenX + width, screenY, imgZ, (u + width) * offset, v * offset);
        renderer.vertexUV(screenX, screenY, imgZ, u * offset, v * offset);
        renderer.end();
    }
}
