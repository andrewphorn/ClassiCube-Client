package com.mojang.minecraft.level.tile;

public final class SandStoneBlock extends Block {

    public SandStoneBlock(int id) {
        super(id);
    }

    @Override
    public final int getTextureId(int texture) {
        return texture == 1 ? textureId - 16 : texture == 0 ? textureId + 16 : textureId;
    }
}
