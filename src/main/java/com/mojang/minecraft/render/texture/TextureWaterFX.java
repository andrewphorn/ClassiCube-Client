package com.mojang.minecraft.render.texture;

import com.mojang.minecraft.level.tile.Block;

public final class TextureWaterFX extends TextureFX {

    private float[] red = new float[256];
    private float[] blue = new float[256];
    private float[] green = new float[256];
    private float[] alpha = new float[256];

    public TextureWaterFX() {
        super(Block.WATER.textureId);
    }

    @Override
    public final void animate() {
        int var1;
        int var2;
        float var3;
        int var4;
        int var5;
        int var6;
        for (var1 = 0; var1 < 16; ++var1) {
            for (var2 = 0; var2 < 16; ++var2) {
                var3 = 0F;

                for (var4 = var1 - 1; var4 <= var1 + 1; ++var4) {
                    var5 = var4 & 15;
                    var6 = var2 & 15;
                    var3 += red[var5 + (var6 << 4)];
                }

                blue[var1 + (var2 << 4)] = var3 / 3.3F + green[var1 + (var2 << 4)] * 0.8F;
            }
        }

        for (var1 = 0; var1 < 16; ++var1) {
            for (var2 = 0; var2 < 16; ++var2) {
                green[var1 + (var2 << 4)] += alpha[var1 + (var2 << 4)] * 0.05F;
                if (green[var1 + (var2 << 4)] < 0F) {
                    green[var1 + (var2 << 4)] = 0F;
                }

                alpha[var1 + (var2 << 4)] -= 0.1F;
                if (Math.random() < 0.05D) {
                    alpha[var1 + (var2 << 4)] = 0.5F;
                }
            }
        }

        float[] var8 = blue;
        blue = red;
        red = var8;

        for (var2 = 0; var2 < 256; ++var2) {
            if ((var3 = red[var2]) > 1F) {
                var3 = 1F;
            }

            if (var3 < 0F) {
                var3 = 0F;
            }

            float var9 = var3 * var3;
            var5 = (int) (32F + var9 * 32F);
            var6 = (int) (50F + var9 * 64F);
            var1 = 255;
            int var10 = (int) (146F + var9 * 50F);
            textureData[var2 << 2] = (byte) var5;
            textureData[(var2 << 2) + 1] = (byte) var6;
            textureData[(var2 << 2) + 2] = (byte) var1;
            textureData[(var2 << 2) + 3] = (byte) var10;
        }

    }
}
