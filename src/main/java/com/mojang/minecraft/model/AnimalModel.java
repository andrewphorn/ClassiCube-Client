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
		head.setBounds(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F);
		head.setPosition(0.0F, 18 - var1, -6.0F);
		body = new ModelPart(28, 8);
		body.setBounds(-5.0F, -10.0F, -7.0F, 10, 16, 8, 0.0F);
		body.setPosition(0.0F, 17 - var1, 2.0F);
		leg1 = new ModelPart(0, 16);
		leg1.setBounds(-2.0F, 0.0F, -2.0F, 4, var1, 4, 0.0F);
		leg1.setPosition(-3.0F, 24 - var1, 7.0F);
		leg2 = new ModelPart(0, 16);
		leg2.setBounds(-2.0F, 0.0F, -2.0F, 4, var1, 4, 0.0F);
		leg2.setPosition(3.0F, 24 - var1, 7.0F);
		leg3 = new ModelPart(0, 16);
		leg3.setBounds(-2.0F, 0.0F, -2.0F, 4, var1, 4, 0.0F);
		leg3.setPosition(-3.0F, 24 - var1, -5.0F);
		leg4 = new ModelPart(0, 16);
		leg4.setBounds(-2.0F, 0.0F, -2.0F, 4, var1, 4, 0.0F);
		leg4.setPosition(3.0F, 24 - var1, -5.0F);
	}

	@Override
	public final void render(float var1, float var2, float var3, float var4, float var5, float var6) {
		head.yaw = var4 / 57.295776F;
		head.pitch = var5 / 57.295776F;
		body.pitch = 1.5707964F;
		leg1.pitch = MathHelper.cos(var1 * 0.6662F) * 1.4F * var2;
		leg2.pitch = MathHelper.cos(var1 * 0.6662F + 3.1415927F) * 1.4F * var2;
		leg3.pitch = MathHelper.cos(var1 * 0.6662F + 3.1415927F) * 1.4F * var2;
		leg4.pitch = MathHelper.cos(var1 * 0.6662F) * 1.4F * var2;
		head.render(var6);
		body.render(var6);
		leg1.render(var6);
		leg2.render(var6);
		leg3.render(var6);
		leg4.render(var6);
	}
}
