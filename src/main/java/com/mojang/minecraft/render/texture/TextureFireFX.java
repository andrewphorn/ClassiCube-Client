package com.mojang.minecraft.render.texture;

import com.mojang.minecraft.level.tile.Block;

public class TextureFireFX extends TextureFX {
	protected float a[];
	protected float b[];

	public TextureFireFX() {
		super(Block.FIRE.textureId);
		a = new float[320];
		b = new float[320];
	}

	@Override
	public void animate() {
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 20; j++) {
				int k = 18;
				float f = a[i + (j + 1) % 20 * 16] * k;
				for (int i1 = i - 1; i1 <= i + 1; i1++) {
					for (int j1 = j; j1 <= j + 1; j1++) {
						int l1 = i1;
						int j2 = j1;
						if (l1 >= 0 && j2 >= 0 && l1 < 16 && j2 < 20) {
							f += a[l1 + j2 * 16];
						}
						k++;
					}

				}

				b[i + j * 16] = f / (k * 1.06F);
				if (j >= 19) {
					b[i + j * 16] = (float) (Math.random() * Math.random() * Math.random() * 4D
							+ Math.random() * 0.10000000149011612D + 0.20000000298023224D);
				}
			}

		}

		float af[] = b;
		b = a;
		a = af;
		for (int l = 0; l < 256; l++) {
			float f1 = a[l] * 1.8F;
			if (f1 > 1.0F) {
				f1 = 1.0F;
			}
			if (f1 < 0.0F) {
				f1 = 0.0F;
			}
			float f2 = f1;
			int k1 = (int) (f2 * 155F + 100F);
			int i2 = (int) (f2 * f2 * 255F);
			int k2 = (int) (f2 * f2 * f2 * f2 * f2 * f2 * f2 * f2 * f2 * f2 * 255F);
			char c = '\377';
			if (f2 < 0.5F) {
				c = '\0';
			}
			f2 = (f2 - 0.5F) * 2.0F;
			if (anaglyph) {
				int l2 = (k1 * 30 + i2 * 59 + k2 * 11) / 100;
				int i3 = (k1 * 30 + i2 * 70) / 100;
				int j3 = (k1 * 30 + k2 * 70) / 100;
				k1 = l2;
				i2 = i3;
				k2 = j3;
			}
			textureData[l * 4 + 0] = (byte) k1;
			textureData[l * 4 + 1] = (byte) i2;
			textureData[l * 4 + 2] = (byte) k2;
			textureData[l * 4 + 3] = (byte) c;
		}
	}
}