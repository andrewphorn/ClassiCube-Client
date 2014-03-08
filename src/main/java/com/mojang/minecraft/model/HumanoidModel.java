package com.mojang.minecraft.model;

import com.mojang.util.MathHelper;

public class HumanoidModel extends Model {

    public ModelPart head;
    public ModelPart headwear;
    public ModelPart body;
    public ModelPart rightArm;
    public ModelPart leftArm;
    public ModelPart rightLeg;
    public ModelPart leftLeg;

    /**
     * Creates a new humanoid model with the default scaling.
     */
    public HumanoidModel() {
        this(0.0F);
    }

    /**
     * Creates a new humanoid model with the specified scaling.
     * The scaling seems to make the model wider but still making it occupy the same space.
     * @param var1 Scale value to use for the model.
     */
    public HumanoidModel(float var1) {
        head = new ModelPart(0, 0);
        head.setBounds(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1);
                head.allowTransparency = false;
        headwear = new ModelPart(32, 0);
        headwear.setBounds(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1 + 0.5F);
        body = new ModelPart(16, 16);
        body.setBounds(-4.0F, 0.0F, -2.0F, 8, 12, 4, var1);
                body.allowTransparency = false;
        rightArm = new ModelPart(40, 16);
        rightArm.setBounds(-3.0F, -2.0F, -2.0F, 4, 12, 4, var1);
        rightArm.setPosition(-5.0F, 2.0F, 0.0F);
                rightArm.allowTransparency = false;
        leftArm = new ModelPart(40, 16);
        leftArm.mirror = true;
        leftArm.setBounds(-1.0F, -2.0F, -2.0F, 4, 12, 4, var1);
        leftArm.setPosition(5.0F, 2.0F, 0.0F);
                leftArm.allowTransparency = false;
        rightLeg = new ModelPart(0, 16);
        rightLeg.setBounds(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
        rightLeg.setPosition(-2.0F, 12.0F, 0.0F);
                rightLeg.allowTransparency = false;
        leftLeg = new ModelPart(0, 16);
        leftLeg.mirror = true;
        leftLeg.setBounds(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
        leftLeg.setPosition(2.0F, 12.0F, 0.0F);
                leftLeg.allowTransparency = false;
    }

    @Override
    public final void render(float var1, float var2, float var3, float var4, float var5, float var6) {
        setRotationAngles(var1, var2, var3, var4, var5, var6);
        head.render(var6);
        body.render(var6);
        rightArm.render(var6);
        leftArm.render(var6);
        rightLeg.render(var6);
        leftLeg.render(var6);
    }

    public void setRotationAngles(float var1, float var2, float var3, float var4, float var5,
            float var6) {
        head.yaw = var4 / (float) (180D / Math.PI);
        head.pitch = var5 / (float) (180D / Math.PI);
        rightArm.pitch = MathHelper.cos(var1 * 0.6662F + (float) Math.PI) * 2.0F * var2;
        rightArm.roll = (MathHelper.cos(var1 * 0.2312F) + 1.0F) * var2;
        leftArm.pitch = MathHelper.cos(var1 * 0.6662F) * 2.0F * var2;
        leftArm.roll = (MathHelper.cos(var1 * 0.2812F) - 1.0F) * var2;
        rightLeg.pitch = MathHelper.cos(var1 * 0.6662F) * 1.4F * var2;
        leftLeg.pitch = MathHelper.cos(var1 * 0.6662F + (float) Math.PI) * 1.4F * var2;
        rightArm.roll += MathHelper.cos(var3 * 0.09F) * 0.05F + 0.05F;
        leftArm.roll -= MathHelper.cos(var3 * 0.09F) * 0.05F + 0.05F;
        rightArm.pitch += MathHelper.sin(var3 * 0.067F) * 0.05F;
        leftArm.pitch -= MathHelper.sin(var3 * 0.067F) * 0.05F;
    }
}
