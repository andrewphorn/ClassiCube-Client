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
        headOffset = 1F;
        byte var1 = 16;
        head = new ModelPart(0, 0);
        head.setBounds(-2F, -6F, -2F, 4, 6, 3, 0F);
        head.setPosition(0F, -1 + var1, -4F);
        bill = new ModelPart(14, 0);
        bill.setBounds(-2F, -4F, -4F, 4, 2, 2, 0F);
        bill.setPosition(0F, -1 + var1, -4F);
        chin = new ModelPart(14, 4);
        chin.setBounds(-1F, -2F, -3F, 2, 2, 2, 0F);
        chin.setPosition(0F, -1 + var1, -4F);
        body = new ModelPart(0, 9);
        body.setBounds(-3F, -4F, -3F, 6, 8, 6, 0F);
        body.setPosition(0F, var1, 0F);
        rightLeg = new ModelPart(26, 0);
        rightLeg.setBounds(-1F, 0F, -3F, 3, 5, 3, 0F);
        rightLeg.setPosition(-2F, 3 + var1, 1F);
        leftLeg = new ModelPart(26, 0);
        leftLeg.setBounds(-1F, 0F, -3F, 3, 5, 3, 0F);
        leftLeg.setPosition(1F, 3 + var1, 1F);
        rightWing = new ModelPart(24, 13);
        rightWing.setBounds(0F, 0F, -3F, 1, 4, 6, 0F);
        rightWing.setPosition(-4F, -3 + var1, 0F);
        leftWing = new ModelPart(24, 13);
        leftWing.setBounds(-1F, 0F, -3F, 1, 4, 6, 0F);
        leftWing.setPosition(4F, -3 + var1, 0F);
    }

    @Override
    public void render(float par2, float par3, float par4, float yawDegrees, float pitchDegrees, float scale) {
        setRotationAngles(par2, par3, par4, yawDegrees, pitchDegrees, scale);
        head.render(scale);
        bill.render(scale);
        chin.render(scale);
        body.render(scale);
        rightLeg.render(scale);
        leftLeg.render(scale);
        rightWing.render(scale);
        leftWing.render(scale);
    }

    public void setRotationAngles(float par1, float par2, float par3,
            float yawDegrees, float pitchDegrees, float scale) {
        head.pitch = pitchDegrees / (180F / (float) Math.PI);
        head.yaw = yawDegrees / (180F / (float) Math.PI);
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
