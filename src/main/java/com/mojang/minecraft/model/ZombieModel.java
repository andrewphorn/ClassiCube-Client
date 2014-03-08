package com.mojang.minecraft.model;

import com.mojang.util.MathHelper;

public class ZombieModel extends HumanoidModel {

    @Override
    public final void setRotationAngles(float var1, float var2, float var3, float var4, float var5,
            float var6) {
        super.setRotationAngles(var1, var2, var3, var4, var5, var6);
        var1 = MathHelper.sin(attackOffset * (float) Math.PI);
        var2 = MathHelper.sin((1.0F - (1.0F - attackOffset) * (1.0F - attackOffset)) * (float) Math.PI);
        rightArm.roll = 0.0F;
        leftArm.roll = 0.0F;
        rightArm.yaw = -(0.1F - var1 * 0.6F);
        leftArm.yaw = 0.1F - var1 * 0.6F;
        rightArm.pitch = -(float) (Math.PI / 2D);
        leftArm.pitch = -(float) (Math.PI / 2D);
        rightArm.pitch -= var1 * 1.2F - var2 * 0.4F;
        leftArm.pitch -= var1 * 1.2F - var2 * 0.4F;
        rightArm.roll += MathHelper.cos(var3 * 0.09F) * 0.05F + 0.05F;
        leftArm.roll -= MathHelper.cos(var3 * 0.09F) * 0.05F + 0.05F;
        rightArm.pitch += MathHelper.sin(var3 * 0.067F) * 0.05F;
        leftArm.pitch -= MathHelper.sin(var3 * 0.067F) * 0.05F;
    }
}
