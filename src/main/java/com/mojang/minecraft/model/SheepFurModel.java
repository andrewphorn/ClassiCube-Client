package com.mojang.minecraft.model;

public final class SheepFurModel extends AnimalModel {

	public SheepFurModel() {
		super(12);
		head = new ModelPart(0, 0);
		head.setBounds(-3F, -4F, -4F, 6, 6, 6, 0.6F);
		head.setPosition(0F, 6F, -8F);
		body = new ModelPart(28, 8);
		body.setBounds(-4F, -10F, -7F, 8, 16, 6, 1.75F);
		body.setPosition(0F, 5F, 2F);
		float var1 = 0.5F;
		leg1 = new ModelPart(0, 16);
		leg1.setBounds(-2F, 0F, -2F, 4, 6, 4, var1);
		leg1.setPosition(-3F, 12F, 7F);
		leg2 = new ModelPart(0, 16);
		leg2.setBounds(-2F, 0F, -2F, 4, 6, 4, var1);
		leg2.setPosition(3F, 12F, 7F);
		leg3 = new ModelPart(0, 16);
		leg3.setBounds(-2F, 0F, -2F, 4, 6, 4, var1);
		leg3.setPosition(-3F, 12F, -5F);
		leg4 = new ModelPart(0, 16);
		leg4.setBounds(-2F, 0F, -2F, 4, 6, 4, var1);
		leg4.setPosition(3F, 12F, -5F);
	}
}
