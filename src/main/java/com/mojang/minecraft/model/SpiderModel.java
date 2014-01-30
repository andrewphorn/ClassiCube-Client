package com.mojang.minecraft.model;

import com.mojang.util.MathHelper;

public final class SpiderModel extends Model {

	private ModelPart head = new ModelPart(32, 4);
	private ModelPart neck;
	private ModelPart body;
	private ModelPart leg1;
	private ModelPart leg2;
	private ModelPart leg3;
	private ModelPart leg4;
	private ModelPart leg5;
	private ModelPart leg6;
	private ModelPart leg7;
	private ModelPart leg8;

	public SpiderModel() {
		head.setBounds(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F);
		head.setPosition(0.0F, 16.0F, -3.0F);
		neck = new ModelPart(0, 0);
		neck.setBounds(-3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F);
		neck.setPosition(0.0F, 16.0F, 0.0F);
		body = new ModelPart(0, 12);
		body.setBounds(-5.0F, -4.0F, -6.0F, 10, 8, 12, 0.0F);
		body.setPosition(0.0F, 16.0F, 9.0F);
		leg1 = new ModelPart(18, 0);
		leg1.setBounds(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		leg1.setPosition(-4.0F, 16.0F, 2.0F);
		leg2 = new ModelPart(18, 0);
		leg2.setBounds(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		leg2.setPosition(4.0F, 16.0F, 2.0F);
		leg3 = new ModelPart(18, 0);
		leg3.setBounds(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		leg3.setPosition(-4.0F, 16.0F, 1.0F);
		leg4 = new ModelPart(18, 0);
		leg4.setBounds(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		leg4.setPosition(4.0F, 16.0F, 1.0F);
		leg5 = new ModelPart(18, 0);
		leg5.setBounds(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		leg5.setPosition(-4.0F, 16.0F, 0.0F);
		leg6 = new ModelPart(18, 0);
		leg6.setBounds(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		leg6.setPosition(4.0F, 16.0F, 0.0F);
		leg7 = new ModelPart(18, 0);
		leg7.setBounds(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		leg7.setPosition(-4.0F, 16.0F, -1.0F);
		leg8 = new ModelPart(18, 0);
		leg8.setBounds(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
		leg8.setPosition(4.0F, 16.0F, -1.0F);
	}

	@Override
	public final void render(float var1, float var2, float var3, float var4, float var5, float var6) {
		head.yaw = var4 / 57.295776F;
		head.pitch = var5 / 57.295776F;
		var4 = 0.7853982F;
		leg1.roll = -var4;
		leg2.roll = var4;
		leg3.roll = -var4 * 0.74F;
		leg4.roll = var4 * 0.74F;
		leg5.roll = -var4 * 0.74F;
		leg6.roll = var4 * 0.74F;
		leg7.roll = -var4;
		leg8.roll = var4;
		var4 = 0.3926991F;
		leg1.yaw = var4 * 2.0F;
		leg2.yaw = -var4 * 2.0F;
		leg3.yaw = var4;
		leg4.yaw = -var4;
		leg5.yaw = -var4;
		leg6.yaw = var4;
		leg7.yaw = -var4 * 2.0F;
		leg8.yaw = var4 * 2.0F;
		var4 = -(MathHelper.cos(var1 * 0.6662F * 2.0F) * 0.4F) * var2;
		var5 = -(MathHelper.cos(var1 * 0.6662F * 2.0F + 3.1415927F) * 0.4F) * var2;
		float var7 = -(MathHelper.cos(var1 * 0.6662F * 2.0F + 1.5707964F) * 0.4F) * var2;
		float var8 = -(MathHelper.cos(var1 * 0.6662F * 2.0F + 4.712389F) * 0.4F) * var2;
		float var9 = Math.abs(MathHelper.sin(var1 * 0.6662F) * 0.4F) * var2;
		float var10 = Math.abs(MathHelper.sin(var1 * 0.6662F + 3.1415927F) * 0.4F) * var2;
		float var11 = Math.abs(MathHelper.sin(var1 * 0.6662F + 1.5707964F) * 0.4F) * var2;
		var2 = Math.abs(MathHelper.sin(var1 * 0.6662F + 4.712389F) * 0.4F) * var2;
		leg1.yaw += var4;
		leg2.yaw -= var4;
		leg3.yaw += var5;
		leg4.yaw -= var5;
		leg5.yaw += var7;
		leg6.yaw -= var7;
		leg7.yaw += var8;
		leg8.yaw -= var8;
		leg1.roll += var9;
		leg2.roll -= var9;
		leg3.roll += var10;
		leg4.roll -= var10;
		leg5.roll += var11;
		leg6.roll -= var11;
		leg7.roll += var2;
		leg8.roll -= var2;
		head.render(var6);
		neck.render(var6);
		body.render(var6);
		leg1.render(var6);
		leg2.render(var6);
		leg3.render(var6);
		leg4.render(var6);
		leg5.render(var6);
		leg6.render(var6);
		leg7.render(var6);
		leg8.render(var6);
	}
}
