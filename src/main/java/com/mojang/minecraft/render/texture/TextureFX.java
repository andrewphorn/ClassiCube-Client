package com.mojang.minecraft.render.texture;

public class TextureFX {
    public byte[] textureData = new byte[1024];

    public int textureId;
    public boolean anaglyph = false;
    public int scaling = 1;

    public TextureFX(int textureID) {
        textureId = textureID;
    }

    public void animate() {
    }
}
