package com.mojang.minecraft.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.minecraft.render.texture.Textures;
import com.mojang.util.LogUtil;

public final class FontRenderer {

    public int charHeight;
    public int charWidth;
    public int[] charOffsets = new int[256];
    public int[] charWidths = new int[256];
    private final int fontTextureId;
    private final GameSettings settings;

    public FontRenderer(GameSettings settings, TextureManager textures)
            throws IOException {
        this.settings = settings;
        BufferedImage fontTexture;

        if (textures.customFont != null) {
            fontTexture = textures.customFont;
        } else {
            fontTexture = ImageIO.read(TextureManager.class.getResourceAsStream(Textures.FONT));
        }
        int width = fontTexture.getWidth();
        int height = fontTexture.getHeight();
        charWidth = width;
        charHeight = height;

        calculateCharWidths(fontTexture, width, height);
        fontTextureId = textures.load(Textures.FONT);
    }

    private void calculateCharWidths(BufferedImage fontTexture, int width, int height) {
        int[] fontData = new int[width * height];
        fontTexture.getRGB(0, 0, width, height, fontData, 0, width);
        int maxCharWidth = width / 16;
        int maxCharHeight = height / 16;

        for (int character = 0; character < 128; ++character) {
            int col = character % 16;
            int row = character / 16;
            int offset = (col * maxCharWidth) + (row * maxCharHeight * width);

            if (character == 32) {
                // Space is always 50% width
                charWidths[32] = maxCharWidth / 2;
            } else {
                // Other chars' width is determined by examining pixels
                // First, find start of character (first non-empty row)
                int chStart = 0;
                for (int c = 0; c < maxCharWidth; c++) {
                    chStart = c;
                    if (!isColEmpty(fontData, offset + c, width, maxCharHeight)) {
                        break;
                    }
                }
                // Next, find end of character (last non-empty row)
                int chEnd = maxCharWidth - 1;
                for (int c = maxCharWidth - 1; c > chStart; c--) {
                    chEnd = c;
                    if (!isColEmpty(fontData, offset + c, width, maxCharHeight)) {
                        break;
                    }
                }

                charOffsets[character] = chStart;
                charWidths[character] = chEnd - chStart + 1;
            }
        }
    }

    private static boolean isColEmpty(int[] imgData, int offset, int imageWidth, int maxCharHeight) {
        for (int row = 0; row < maxCharHeight; row++) {
            int rowOffset = offset + row * imageWidth;
            if (((imgData[rowOffset] >> 24) & 0xFF) > 128) {
                // Non-transparent pixel found in column!
                return false;
            }
        }
        return true;
    }

    public static String stripColor(String message) {
        if (message == null) {
            return null;
        }
        int start = message.indexOf('&');
        if (start == -1) {
            return message;
        }
        int lastInsert = 0;
        StringBuilder output = new StringBuilder(message.length());
        while (start != -1) {
            output.append(message, lastInsert, start);
            lastInsert = Math.min(start + 2, message.length());
            start = message.indexOf('&', lastInsert);
        }
        output.append(message, lastInsert, message.length());
        return output.toString();
    }

    public float getScale() {
        return 7F / charHeight * settings.scale;
    }

    public int getWidth(String text) {
        if (text == null) {
            return 0;
        }
        int i = 0;
        for (int j = 0; j < text.length(); j++) {
            int k = text.charAt(j);
            if (k == 38) {
                j++;
            } else {
                i += charWidths[k] + 1;
            }
        }
        return (int) Math.floor(i * settings.scale);
    }

    public int getHeight() {
        return (int) Math.floor(charHeight * settings.scale);
    }

    private void render(String text, float x, float y, int color, boolean shadow) {
        if (text == null) {
            return;
        }
        if (shadow) {
            color = (color & 16579836) >> 2;
        }
        float f1 = settings.scale;
        float f2 = 1F / f1;
        x = x * f2;
        y = y * f2;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTextureId);

        ShapeRenderer.instance.begin();
        ShapeRenderer.instance.color(color);
        int xOffset = 0;
        for (int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            if (ch == '&' && text.length() > i + 1) {
                // Color code handling
                int code = "0123456789abcdef".indexOf(text.charAt(i + 1));
                if (code < 0) {
                    code = 15;
                }

                int intensity = (code & 8) << 3;
                int blue = (code & 1) * 191 + intensity;
                int green = ((code & 2) >> 1) * 191 + intensity;
                int red = ((code & 4) >> 2) * 191 + intensity;

                int c = red << 16 | green << 8 | blue;
                if (shadow) {
                    c = (c & 16579836) >> 2;
                }

                ShapeRenderer.instance.color(c);
                if (text.length() - 2 == i) {
                    break;
                }
                i += 2;
                ch = text.charAt(i);
            }
            int colOffset = ch % 16 << 3;
            int rowOffset = ch / 16 << 3;
            float charQuadSize = 7.99F;

            xOffset -= charOffsets[ch];

            ShapeRenderer.instance.vertexUV(x + xOffset, y + charQuadSize, 0F,
                    colOffset / 128F, (rowOffset + charQuadSize) / 128F);
            ShapeRenderer.instance.vertexUV(x + xOffset + charQuadSize, y + charQuadSize, 0F,
                    (colOffset + charQuadSize) / 128F, (rowOffset + charQuadSize) / 128F);
            ShapeRenderer.instance.vertexUV(x + xOffset + charQuadSize, y, 0F,
                    (colOffset + charQuadSize) / 128F, rowOffset / 128F);
            ShapeRenderer.instance.vertexUV(x + xOffset, y, 0F, colOffset / 128F, rowOffset / 128F);

            xOffset += charWidths[ch] + charOffsets[ch] + 1;
        }
        GL11.glPushMatrix();
        GL11.glScalef(f1, f1, 1F);
        ShapeRenderer.instance.end();
        GL11.glPopMatrix();
    }

    public final void render(String text, int x, int y, int color) {
        this.render(text, x + 1 * settings.scale, y + 1 * settings.scale, color, true);
        renderNoShadow(text, x, y, color);
    }

    public final void renderNoShadow(String text, int x, int y, int color) {
        this.render(text, x, y, color, false);
    }
}
