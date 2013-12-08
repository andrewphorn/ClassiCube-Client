package com.mojang.minecraft.level.tile;

public final class OreBlock extends Block {

	public OreBlock(int var1) {
		super(var1);
	}

	public final int getDrop() {
		return this == coalOre ? stoneSlab.id : (this == goldOre ? goldBlock.id
				: (this == ironOre ? ironBlock.id : this.id));
	}

	public final int getDropCount() {
		return random.nextInt(3) + 1;
	}
}
