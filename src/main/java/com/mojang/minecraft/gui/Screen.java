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
	protected static void drawBox(float x1, float y1, float x2, float y2,
			int colorRGB) {

		float alpha = (colorRGB >>> 24) / 255.0F;
		float red = (colorRGB >> 16 & 255) / 255.0F;
		float green = (colorRGB >> 8 & 255) / 255.0F;
		float blue = (colorRGB & 255) / 255.0F;
		ShapeRenderer renderer = ShapeRenderer.instance;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(red, green, blue, alpha);

		renderer.begin();
		renderer.vertex(x1, y2, 0.0F);
		renderer.vertex(x2, y2, 0.0F);
		renderer.vertex(x2, y1, 0.0F);
		renderer.vertex(x1, y1, 0.0F);
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
		drawBox((float)x1, (float)y1, (float)x2, (float)y2, colorRGB);
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
	public static void drawCenteredString(FontRenderer renderer, String text,
			int x, int y, int colorRGB) {
		// Measure the length of the text with the current font and then divide it by two
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
	public static void drawString(FontRenderer renderer, String text,
			int x, int y, int colorRGB) {
		renderer.render(text, x, y, colorRGB);
	}
	protected static void drawFadingBox(int var0, int var1, int var2, int var3,
			int var4, int var5) {
		GL11.glAlphaFunc(516, 0.0F);
		float var6 = (var4 >>> 24) / 255.0F;
		float var7 = (var4 >> 16 & 255) / 255.0F;
		float var8 = (var4 >> 8 & 255) / 255.0F;
		float var12 = (var4 & 255) / 255.0F;
		float var9 = (var5 >>> 24) / 255.0F;
		float var10 = (var5 >> 16 & 255) / 255.0F;
		float var11 = (var5 >> 8 & 255) / 255.0F;
		float var13 = (var5 & 255) / 255.0F;
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

	protected float imgZ = 0.0F;

	public final void drawImage(int var1, int var2, int var3, int var4,
			int var5, int var6) {
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		ShapeRenderer var9 = ShapeRenderer.instance;
		ShapeRenderer.instance.begin();
		var9.vertexUV(var1, var2 + var6, imgZ, var3 * var7, (var4 + var6)
				* var8);
		var9.vertexUV(var1 + var5, var2 + var6, imgZ, (var3 + var5) * var7,
				(var4 + var6) * var8);
		var9.vertexUV(var1 + var5, var2, imgZ, (var3 + var5) * var7, var4
				* var8);
		var9.vertexUV(var1, var2, imgZ, var3 * var7, var4 * var8);
		var9.end();
	}
}
