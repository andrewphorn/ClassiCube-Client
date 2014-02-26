package com.mojang.minecraft.model;

public final class SkeletonModel extends ZombieModel {

	public SkeletonModel() {
		rightArm = new ModelPart(40, 16);
		rightArm.setBounds(-1.0F, -2.0F, -1.0F, 2, 12, 2, 0.0F);
		rightArm.setPosition(-5.0F, 2.0F, 0.0F);
		leftArm = new ModelPart(40, 16);
		leftArm.mirror = true;
		leftArm.setBounds(-1.0F, -2.0F, -1.0F, 2, 12, 2, 0.0F);
		leftArm.setPosition(5.0F, 2.0F, 0.0F);
		rightLeg = new ModelPart(0, 16);
		rightLeg.setBounds(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0.0F);
		rightLeg.setPosition(-2.0F, 12.0F, 0.0F);
		leftLeg = new ModelPart(0, 16);
		leftLeg.mirror = true;
		leftLeg.setBounds(-1.0F, 0.0F, -1.0F, 2, 12, 2, 0.0F);
		leftLeg.setPosition(2.0F, 12.0F, 0.0F);
                head.allowTransparency = true;
                body.allowTransparency = true;
	}
}
