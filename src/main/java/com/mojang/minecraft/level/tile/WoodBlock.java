package com.mojang.minecraft.level.tile;

public final class WoodBlock extends Block {

	protected WoodBlock(int var1) {
		super(var1);
		textureId = 20;
	}

	@Override
	public final int getDrop() {
		return WOOD.id;
	}

	@Override
	public final int getDropCount() {
		return random.nextInt(3) + 3;
	}

	@Override
	public final int getTextureId(int texture) {
		return texture == 1 ? 21 : texture == 0 ? 21 : 20;
	}
}
