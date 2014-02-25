package com.mojang.minecraft.level.tile;

public final class SandStoneBlock extends Block {

	public SandStoneBlock(int var1) {
		super(var1);
	}

	@Override
	public final int getTextureId(int texture) {
		return texture == 1 ? textureId - 16 : texture == 0 ? textureId + 16 : textureId;
	}
}
