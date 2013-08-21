package com.mojang.minecraft.gui;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.render.TextureManager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

public final class FontRenderer {
	private int fontId = 0;
	private int charHeight;
	public static float RenderScale = 1F;
	private GameSettings settings;
	private int[] font = new int[256];

	public FontRenderer(GameSettings settings, String fontImage,
			TextureManager textures, float Scale) throws IOException {
		this.settings = settings;
		RenderScale = Scale;
		BufferedImage font;

		try {
			if (textures.Applet) {
				font = ImageIO.read(TextureManager.class
						.getResourceAsStream(fontImage));
			} else {
				font = ImageIO.read(TextureManager.class
						.getResourceAsStream("/resources" + fontImage));
			}
		} catch (IOException e) {
			throw new IOException("Missing resource");
		}
		int width = font.getWidth();
		int height = font.getHeight();
		this.charHeight = (height / 16);
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
				chWidth = (int) (4 * (1 / getScale()));
			}
			this.font[character] = chWidth;
		}

		this.fontId = textures.load(fontImage);
	}

	public float getScale() {
		return 7.0F / this.charHeight * RenderScale;
	}

	public final void render(String text, int x, int y, int color) {
		this.render(text, x + 1, y + 1, color, true);
		this.renderNoShadow(text, x, y, color);
	}

	public final void renderNoShadow(String text, int x, int y, int color) {
		this.render(text, x, y, color, false);
	}

	private void render(String text, int x, int y, int color, boolean shadow) {
		if (text != null) {
			char[] chars = text.toCharArray();
			if (shadow) {
				color = (color & 16579836) >> 2;
			}

			float f1 = getScale();
			f1 = 1.0F / f1;
			x = (int) (x * f1);
			y = (int) (y * f1);

			GL11.glPushMatrix();
			/*
			 * if(shadow){ if(RenderScale < 1F){ float f3 = 1.0F * RenderScale;
			 * GL11.glTranslatef(-f3, -f3, 0.0F); } if(RenderScale > 1F){ float
			 * f3 = 1.0F * RenderScale; GL11.glTranslatef(+f3, +f3, 0.0F); } }
			 */
			GL11.glBindTexture(3553, this.fontId);
			GL11.glScalef(getScale(), getScale(), 1.0F);

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
					count += 2;
				}

				color = chars[count] % 16 << 3;
				int var9 = chars[count] / 16 << 3;
				float var13 = 7.99F;

				ShapeRenderer.instance.vertexUV((x + var7), y + var13, 0.0F,
						color / 128.0F, (var9 + var13) / 128.0F);
				ShapeRenderer.instance
						.vertexUV((x + var7) + var13, y + var13, 0.0F,
								(color + var13) / 128.0F,
								(var9 + var13) / 128.0F);
				ShapeRenderer.instance.vertexUV((x + var7) + var13, y, 0.0F,
						(color + var13) / 128.0F, var9 / 128.0F);
				ShapeRenderer.instance.vertexUV((x + var7), y, 0.0F,
						color / 128.0F, var9 / 128.0F);
				if (chars[count] < this.font.length) {
					var7 += this.font[chars[count]];
				}
			}

			ShapeRenderer.instance.end();
			GL11.glPopMatrix();
		}
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
		return (int) Math.floor(i * getScale());
	}

	public static String StripColors(String message) {
		int start = message.indexOf('&');
		if (start == -1) {
			return message;
		}
		int lastInsert = 0;
		StringBuilder output = new StringBuilder(message.length());
		while (start != -1) {
			output.append(message, lastInsert, start - lastInsert);
			lastInsert = Math.min(start + 2, message.length());
			start = message.indexOf('&', lastInsert);
		}
		output.append(message, lastInsert, message.length() - lastInsert);
		return output.toString();
	}
}