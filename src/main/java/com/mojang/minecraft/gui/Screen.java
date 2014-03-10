package com.mojang.minecraft.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.render.ShapeRenderer;

/**
 * Base class for any kind of screen.
 */
public class Screen {

    /**
     * Draws a box to the screen
     * 
     * @param x1
     *            X coordinate of the first point of the box.
     * @param y1
     *            Y coordinate of the first point of the box.
     * @param x2
     *            X coordinate of the second point of the box.
     * @param y2
     *            Y coordinate of the second point of the box.
     * @param colorRGB
     *            The color of the box. See {@Color}
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
     * @param x1
     *            X coordinate of the first point of the box.
     * @param y1
     *            Y coordinate of the first point of the box.
     * @param x2
     *            X coordinate of the second point of the box.
     * @param y2
     *            Y coordinate of the second point of the box.
     * @param colorRGB
     *            The color of the box. See {@Color}
     */
    protected static void drawBox(int x1, int y1, int x2, int y2, int colorRGB) {
        drawBox((float) x1, (float) y1, (float) x2, (float) y2, colorRGB);
    }

    /**
     * Draws a string that is centered.
     * 
     * @param renderer
     *            {@FontRenderer} used to render the used font.
     * @param text
     *            Text to draw and center
     * @param x
     *            X-Coordinate of position to draw.
     * @param y
     *            Y-Coordinate of position to draw.
     * @param colorRGB
     *            The color of the box. See {@Color}
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
     * @param renderer
     *            {@FontRenderer} used to render the used font.
     * @param text
     *            Text to draw
     * @param x
     *            X-Coordinate of position to draw.
     * @param y
     *            Y-Coordinate of position to draw.
     * @param colorRGB
     *            The color of the box. See {@Color}
     */
    public static void drawString(FontRenderer renderer, String text, int x, int y, int colorRGB) {
        renderer.render(text, x, y, colorRGB);
    }

    protected static void drawFadingBox(int var0, int var1, int var2, int var3, int var4, int var5) {
        GL11.glAlphaFunc(516, 0F);
        float var6 = (var4 >>> 24) / 255F;
        float var7 = (var4 >> 16 & 255) / 255F;
        float var8 = (var4 >> 8 & 255) / 255F;
        float var12 = (var4 & 255) / 255F;
        float var9 = (var5 >>> 24) / 255F;
        float var10 = (var5 >> 16 & 255) / 255F;
        float var11 = (var5 >> 8 & 255) / 255F;
        float var13 = (var5 & 255) / 255F;
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(7);
        GL11.glColor4f(var7, var8, var12, var6);
        GL11.glVertex2f(var2, var1);
        GL11.glVertex2f(var0, var1);
        GL11.glColor4f(var10, var11, var13, var9);
        GL11.glVertex2f(var0, var3);
        GL11.glVertex2f(var2, var3);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glAlphaFunc(516, 0.5F);
    }

    protected float imgZ = 0;

    public final void drawImage(int screenX, int screenY, int u, int v, int width, int height) {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        ShapeRenderer renderer = ShapeRenderer.instance;
        renderer.begin();
        renderer.vertexUV(screenX, screenY + height, imgZ, u * var7, (v + height) * var8);
        renderer.vertexUV(screenX + width, screenY + height, imgZ, (u + width) * var7, (v + height)
                * var8);
        renderer.vertexUV(screenX + width, screenY, imgZ, (u + width) * var7, v * var8);
        renderer.vertexUV(screenX, screenY, imgZ, u * var7, v * var8);
        renderer.end();
    }
}
