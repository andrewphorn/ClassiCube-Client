package com.mojang.minecraft.model;

public final class SheepFurModel extends AnimalModel {

    public SheepFurModel() {
        super(12);
        head = new ModelPart(0, 0);
        head.setBounds(-3.0F, -4.0F, -4.0F, 6, 6, 6, 0.6F);
        head.setPosition(0.0F, 6.0F, -8.0F);
        body = new ModelPart(28, 8);
        body.setBounds(-4.0F, -10.0F, -7.0F, 8, 16, 6, 1.75F);
        body.setPosition(0.0F, 5.0F, 2.0F);
        float var1 = 0.5F;
        leg1 = new ModelPart(0, 16);
        leg1.setBounds(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
        leg1.setPosition(-3.0F, 12.0F, 7.0F);
        leg2 = new ModelPart(0, 16);
        leg2.setBounds(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
        leg2.setPosition(3.0F, 12.0F, 7.0F);
        leg3 = new ModelPart(0, 16);
        leg3.setBounds(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
        leg3.setPosition(-3.0F, 12.0F, -5.0F);
        leg4 = new ModelPart(0, 16);
        leg4.setBounds(-2.0F, 0.0F, -2.0F, 4, 6, 4, var1);
        leg4.setPosition(3.0F, 12.0F, -5.0F);
    }
}
