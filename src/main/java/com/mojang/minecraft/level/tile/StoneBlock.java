package com.mojang.minecraft.level.tile;

public final class StoneBlock extends Block {

    public StoneBlock(int id) {
        super(id);
    }

    @Override
    public final int getDrop() {
        return COBLESTONE.id;
    }
}
