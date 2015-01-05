package com.mojang.minecraft.model;

public final class SheepModel extends AnimalModel {

    public SheepModel() {
        super(12);
        headOffset = 0.375F;
        head = new ModelPart(0, 0);
        head.setBounds(-3F, -4F, -6F, 6, 6, 8, 0F);
        head.setPosition(0F, 6F, -8F);
        body = new ModelPart(28, 8);
        body.setBounds(-4F, -10F, -7F, 8, 16, 6, 0F);
        body.setPosition(0F, 5F, 2F);
    }
}
