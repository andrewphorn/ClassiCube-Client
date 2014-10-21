package com.mojang.minecraft.model;

import com.mojang.util.MathHelper;

public class AnimalModel extends Model {

    public ModelPart head = new ModelPart(0, 0);
    public ModelPart body;
    public ModelPart leg1;
    public ModelPart leg2;
    public ModelPart leg3;
    public ModelPart leg4;

    public AnimalModel(int var1) {
        head.setBounds(-4F, -4F, -8F, 8, 8, 8, 0F);
        head.setPosition(0F, 18 - var1, -6F);
        body = new ModelPart(28, 8);
        body.setBounds(-5F, -10F, -7F, 10, 16, 8, 0F);
        body.setPosition(0F, 17 - var1, 2F);
        leg1 = new ModelPart(0, 16);
        leg1.setBounds(-2F, 0F, -2F, 4, var1, 4, 0F);
        leg1.setPosition(-3F, 24 - var1, 7F);
        leg2 = new ModelPart(0, 16);
        leg2.setBounds(-2F, 0F, -2F, 4, var1, 4, 0F);
        leg2.setPosition(3F, 24 - var1, 7F);
        leg3 = new ModelPart(0, 16);
        leg3.setBounds(-2F, 0F, -2F, 4, var1, 4, 0F);
        leg3.setPosition(-3F, 24 - var1, -5F);
        leg4 = new ModelPart(0, 16);
        leg4.setBounds(-2F, 0F, -2F, 4, var1, 4, 0F);
        leg4.setPosition(3F, 24 - var1, -5F);
    }

    @Override
    public final void render(float var1, float var2, float var3, float var4, float var5, float scale) {
        head.yaw = var4 / (float) (180D / Math.PI);
        head.pitch = var5 / (float) (180D / Math.PI);
        body.pitch = (float) (Math.PI / 2D);
        leg1.pitch = MathHelper.cos(var1 * 0.6662F) * 1.4F * var2;
        leg2.pitch = MathHelper.cos(var1 * 0.6662F + (float) Math.PI) * 1.4F * var2;
        leg3.pitch = MathHelper.cos(var1 * 0.6662F + (float) Math.PI) * 1.4F * var2;
        leg4.pitch = MathHelper.cos(var1 * 0.6662F) * 1.4F * var2;
        head.render(scale);
        body.render(scale);
        leg1.render(scale);
        leg2.render(scale);
        leg3.render(scale);
        leg4.render(scale);
    }
}
