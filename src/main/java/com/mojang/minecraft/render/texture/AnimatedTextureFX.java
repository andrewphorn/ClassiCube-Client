package com.mojang.minecraft.render.texture;

import com.mojang.minecraft.LogUtil;
import com.mojang.minecraft.render.TextureManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimatedTextureFX extends TextureFX {

    protected int index = 0;
    protected List<BufferedImage> atlas = new ArrayList<>();

    protected BufferedImage file;

    public AnimatedTextureFX(int targetTextureID, BufferedImage image, int scale) {
        super(targetTextureID);
        file = image;
        int frames = file.getHeight() / file.getWidth();
        int frameSize = file.getWidth();
        unStitch(frames, frameSize);
    }

    public AnimatedTextureFX(int targetTextureID, String fileToLoad, int scale) {
        super(targetTextureID);
        try {
            file = ImageIO.read(TextureManager.class.getResourceAsStream(fileToLoad));
        } catch (IOException ex) {
            LogUtil.logError("Error loading texture from " + fileToLoad, ex);
        }
        scaling = file.getWidth() / 16;
        int frames = file.getHeight() / file.getWidth();
        int frameSize = file.getWidth();
        unStitch(frames, frameSize);
    }

    @Override
    public void animate() {
        if (atlas.size() == 0) {
            return;
        }
        BufferedImage image = atlas.get(index);
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];

        image.getRGB(0, 0, width, height, pixels, 0, width);
        textureData = new byte[width * height * 4];
        scaling = file.getWidth() / 16;
        for (int pixel = 0; pixel < pixels.length; pixel++) {
            int alpha = pixels[pixel] >>> 24;
            int red = pixels[pixel] >> 16 & 0xFF;
            int green = pixels[pixel] >> 8 & 0xFF;
            int blue = pixels[pixel] & 0xFF;

            int i = pixel << 2;
            textureData[i] = (byte) red;
            textureData[i + 1] = (byte) green;
            textureData[i + 2] = (byte) blue;
            textureData[i + 3] = (byte) alpha;
        }
        index++;
        if (index >= atlas.size()) {
            index = 0;
        }
    }

    public void unStitch(int frames, int frameSize) {
        for (int i = 0; i < frames; i++) {
            BufferedImage image = new BufferedImage(frameSize, frameSize, 6);
            for (int j = 0; j < frameSize; j++) {
                for (int k = 0; k < frameSize; k++) {
                    image.setRGB(j, k, file.getRGB(j, k + i * frameSize));
                }
            }
            atlas.add(image);
        }
    }
}