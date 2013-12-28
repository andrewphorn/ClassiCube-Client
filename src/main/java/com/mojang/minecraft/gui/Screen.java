package com.mojang.minecraft.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.render.ShapeRenderer;

public class Screen {

	protected static void drawBox(float var0, float var1, float var2, float var3, int var4) {

		float var5 = (var4 >>> 24) / 255.0F;
		float var6 = (var4 >> 16 & 255) / 255.0F;
		float var7 = (var4 >> 8 & 255) / 255.0F;
		float var9 = (var4 & 255) / 255.0F;
		ShapeRenderer var8 = ShapeRenderer.instance;
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glColor4f(var6, var7, var9, var5);
		var8.begin();
		var8.vertex(var0, var3, 0.0F);
		var8.vertex(var2, var3, 0.0F);
		var8.vertex(var2, var1, 0.0F);
		var8.vertex(var0, var1, 0.0F);
		var8.end();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
	}

	protected static void drawBox(int var0, int var1, int var2, int var3, int var4) {

		float var5 = (var4 >>> 24) / 255.0F;
		float var6 = (var4 >> 16 & 255) / 255.0F;
		float var7 = (var4 >> 8 & 255) / 255.0F;
		float var9 = (var4 & 255) / 255.0F;
		ShapeRenderer var8 = ShapeRenderer.instance;
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glColor4f(var6, var7, var9, var5);
		var8.begin();
		var8.vertex(var0, var3, 0.0F);
		var8.vertex(var2, var3, 0.0F);
		var8.vertex(var2, var1, 0.0F);
		var8.vertex(var0, var1, 0.0F);
		var8.end();
		GL11.glEnable(3553);
		GL11.glDisable(3042);
	}

	public static void drawCenteredString(FontRenderer var0, String var1, int var2, int var3,
			int var4) {
		var0.render(var1, var2 - var0.getWidth(var1) / 2, var3, var4);
	}

	protected static void drawFadingBox(int var0, int var1, int var2, int var3, int var4, int var5) {
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

	public static void drawString(FontRenderer var0, String var1, int var2, int var3, int var4) {
		var0.render(var1, var2, var3, var4);
	}

	protected float imgZ = 0.0F;

	public final void drawImage(int var1, int var2, int var3, int var4, int var5, int var6) {
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		ShapeRenderer var9 = ShapeRenderer.instance;
		ShapeRenderer.instance.begin();
		var9.vertexUV(var1, var2 + var6, imgZ, var3 * var7, (var4 + var6) * var8);
		var9.vertexUV(var1 + var5, var2 + var6, imgZ, (var3 + var5) * var7, (var4 + var6) * var8);
		var9.vertexUV(var1 + var5, var2, imgZ, (var3 + var5) * var7, var4 * var8);
		var9.vertexUV(var1, var2, imgZ, var3 * var7, var4 * var8);
		var9.end();
	}
}
