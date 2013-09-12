package com.mojang.minecraft.level.tile;

public final class BookshelfBlock extends Block {

	public BookshelfBlock(int var1, int var2) {
		super(var1, var2);
	}

	public final int getDropCount() {
		return 0;
	}

	protected final int getTextureId(int texture) {
		return texture <= 1 ? 4 : this.textureId;
	}
}
