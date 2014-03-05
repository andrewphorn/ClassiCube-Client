package com.mojang.minecraft.model;

import com.mojang.util.MathHelper;

public class ChickenModel extends Model {
    public ModelPart head;
    public ModelPart body;
    public ModelPart rightLeg;
    public ModelPart leftLeg;
    public ModelPart rightWing;
    public ModelPart leftWing;
    public ModelPart bill;
    public ModelPart chin;

    public ChickenModel() {
        byte var1 = 16;
        head = new ModelPart(0, 0);
        head.setBounds(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
        head.setPosition(0.0F, -1 + var1, -4.0F);
        bill = new ModelPart(14, 0);
        bill.setBounds(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
        bill.setPosition(0.0F, -1 + var1, -4.0F);
        chin = new ModelPart(14, 4);
        chin.setBounds(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
        chin.setPosition(0.0F, -1 + var1, -4.0F);
        body = new ModelPart(0, 9);
        body.setBounds(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
        body.setPosition(0.0F, var1, 0.0F);
        rightLeg = new ModelPart(26, 0);
        rightLeg.setBounds(-1.0F, 0.0F, -3.0F, 3, 5, 3, 0.0F);
        rightLeg.setPosition(-2.0F, 3 + var1, 1.0F);
        leftLeg = new ModelPart(26, 0);
        leftLeg.setBounds(-1.0F, 0.0F, -3.0F, 3, 5, 3, 0.0F);
        leftLeg.setPosition(1.0F, 3 + var1, 1.0F);
        rightWing = new ModelPart(24, 13);
        rightWing.setBounds(0.0F, 0.0F, -3.0F, 1, 4, 6, 0.0F);
        rightWing.setPosition(-4.0F, -3 + var1, 0.0F);
        leftWing = new ModelPart(24, 13);
        leftWing.setBounds(-1.0F, 0.0F, -3.0F, 1, 4, 6, 0.0F);
        leftWing.setPosition(4.0F, -3 + var1, 0.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(float par2, float par3, float par4, float par5, float par6, float par7) {
        setRotationAngles(par2, par3, par4, par5, par6, par7);
        head.render(par7);
        bill.render(par7);
        chin.render(par7);
        body.render(par7);
        rightLeg.render(par7);
        leftLeg.render(par7);
        rightWing.render(par7);
        leftWing.render(par7);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are
     * used for animating the movement of arms and legs, where par1 represents
     * the time(so that arms and legs swing back and forth) and par2 represents
     * how "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5,
            float par6) {
        head.pitch = par5 / (180F / (float) Math.PI);
        head.yaw = par4 / (180F / (float) Math.PI);
        bill.pitch = head.pitch;
        bill.yaw = head.yaw;
        chin.pitch = head.pitch;
        chin.yaw = head.yaw;
        body.pitch = (float) Math.PI / 2F;
        rightLeg.pitch = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        leftLeg.pitch = MathHelper.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
        rightWing.roll = par3;
        leftWing.roll = -par3;
    }
}