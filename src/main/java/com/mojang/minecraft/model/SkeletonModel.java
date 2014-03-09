package com.mojang.minecraft.model;

public final class SkeletonModel extends ZombieModel {

    public SkeletonModel() {
        rightArm = new ModelPart(40, 16);
        rightArm.setBounds(-1F, -2F, -1F, 2, 12, 2, 0F);
        rightArm.setPosition(-5F, 2F, 0F);
        leftArm = new ModelPart(40, 16);
        leftArm.mirror = true;
        leftArm.setBounds(-1F, -2F, -1F, 2, 12, 2, 0F);
        leftArm.setPosition(5F, 2F, 0F);
        rightLeg = new ModelPart(0, 16);
        rightLeg.setBounds(-1F, 0F, -1F, 2, 12, 2, 0F);
        rightLeg.setPosition(-2F, 12F, 0F);
        leftLeg = new ModelPart(0, 16);
        leftLeg.mirror = true;
        leftLeg.setBounds(-1F, 0F, -1F, 2, 12, 2, 0F);
        leftLeg.setPosition(2F, 12F, 0F);
                head.allowTransparency = true;
                body.allowTransparency = true;
    }
}
