package com.mojang.minecraft.level.tile;

public final class BookshelfBlock extends Block {

	public BookshelfBlock(int var1) {
		super(var1);
	}

	@Override
	public final int getDropCount() {
		return 0;
	}

	@Override
	public final int getTextureId(int texture) {
		return texture <= 1 ? 4 : textureId;
	}
}
