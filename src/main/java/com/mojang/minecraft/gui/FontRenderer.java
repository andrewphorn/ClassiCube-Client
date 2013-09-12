package com.mojang.minecraft.gui;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.render.TextureManager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

public final class FontRenderer {
	public static String stripColor(String var0) {
		char[] var3 = var0.toCharArray();
		String var1 = "";

		for (int var2 = 0; var2 < var3.length; ++var2) {
			if (var3[var2] == 38) {
				++var2;
			} else {
				var1 = var1 + var3[var2];
			}
		}

		return var1;
	}

	private int fontId = 0;
	private GameSettings settings;

	private int[] font = new int[256];

	public FontRenderer(GameSettings settings, String fontImage, TextureManager textures)
			throws IOException {
		this.settings = settings;
		BufferedImage font;

		try {
			font = ImageIO.read(TextureManager.class.getResourceAsStream(fontImage));
		} catch (IOException e) {
			throw new IOException("Missing resource");
		}
		int width = font.getWidth();
		int height = font.getHeight();
		int[] fontData = new int[256 * 256];
		font.getRGB(0, 0, width, height, fontData, 0, width);

		for (int character = 0; character < 256; ++character) {
			int var6 = (int) (character % 16);
			int var7 = (int) (character / 16);
			int chWidth = 0;

			for (boolean var9 = false; chWidth < 8 && !var9; chWidth++) {
				int var10 = (var6 << 3) + chWidth;
				var9 = true;

				for (int var11 = 0; var11 < 8 && var9; ++var11) {
					int var12 = ((var7 << 3) + var11) * width;
					if ((fontData[var10 + var12] & 255) > 128) {
						var9 = false;
					}
				}
			}

			if (character == 32) {
				chWidth = (int) (4);
			}
			this.font[character] = chWidth;
		}
		this.fontId = textures.load(fontImage);
	}

	public int getWidth(String paramString) {
		if (paramString == null) {
			return 0;
		}
		char[] arrayOfChar = paramString.toCharArray();
		int i = 0;
		for (int j = 0; j < arrayOfChar.length; j++) {
			int k = arrayOfChar[j];
			if (k == 38) {
				j++;
			} else {
				i += this.font[k];
			}
		}
		return (int) Math.floor(i);
	}

	public final void render(String text, int x, int y, int color) {
		this.render(text, x + 1, y + 1, color, true);
		this.renderNoShadow(text, x, y, color);
	}

	private void render(String text, int x, int y, int color, boolean shadow) {
		if (text != null) {
			char[] chars = text.toCharArray();
			if (shadow) {
				color = (color & 16579836) >> 2;
			}

			GL11.glBindTexture(3553, this.fontId);

			ShapeRenderer.instance.begin();
			ShapeRenderer.instance.color(color);
			int var7 = 0;
			for (int count = 0; count < chars.length; ++count) {
				if (chars[count] == '&' && chars.length > count + 1) {
					int code = "0123456789abcdef".indexOf(chars[count + 1]);
					if (code < 0) {
						code = 15;
					}

					int var9 = (code & 8) << 3;
					int var10 = (code & 1) * 191 + var9;
					int var11 = ((code & 2) >> 1) * 191 + var9;
					int blue = ((code & 4) >> 2) * 191 + var9;
					if (this.settings.anaglyph) {
						var9 = (code * 30 + var11 * 59 + var10 * 11) / 100;
						var11 = (code * 30 + var11 * 70) / 100;
						var10 = (code * 30 + var10 * 70) / 100;
						blue = var9;
					}

					int c = blue << 16 | var11 << 8 | var10;
					if (shadow) {
						c = (c & 16579836) >> 2;
					}

					ShapeRenderer.instance.color(c);
					if (chars.length - 2 == count) {
						break;
					}
					count += 2;
				}
				color = chars[count] % 16 << 3;
				int var9 = chars[count] / 16 << 3;
				float var13 = 7.99F;

				ShapeRenderer.instance.vertexUV((x + var7), y + var13, 0.0F, color / 128.0F,
						(var9 + var13) / 128.0F);
				ShapeRenderer.instance.vertexUV((x + var7) + var13, y + var13, 0.0F,
						(color + var13) / 128.0F, (var9 + var13) / 128.0F);
				ShapeRenderer.instance.vertexUV((x + var7) + var13, y, 0.0F,
						(color + var13) / 128.0F, var9 / 128.0F);
				ShapeRenderer.instance.vertexUV((x + var7), y, 0.0F, color / 128.0F, var9 / 128.0F);

				if (chars[count] < this.font.length) {
					var7 += this.font[chars[count]];
				}
			}

			ShapeRenderer.instance.end();
		}
	}

	public final void renderNoShadow(String text, int x, int y, int color) {
		this.render(text, x, y, color, false);
	}
}