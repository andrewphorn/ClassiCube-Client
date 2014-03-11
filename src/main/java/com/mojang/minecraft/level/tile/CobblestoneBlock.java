package com.mojang.minecraft.level.tile;

public final class CobblestoneBlock extends Block {

    public CobblestoneBlock(int id) {
        super(id);
    }

    @Override
    public final int getDrop() {
        return COBLESTONE.id;
    }
}
