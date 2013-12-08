package com.mojang.minecraft.level.tile;

public final class SandStoneBlock extends Block {

	public SandStoneBlock(int var1) {
		super(var1);
	}

	protected final int getTextureId(int texture) {
		return texture == 1 ? this.textureId - 16 : (texture == 0 ? this.textureId + 16
				: this.textureId);
	}
}
