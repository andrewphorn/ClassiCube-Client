package com.mojang.minecraft.model;

import com.mojang.util.MathHelper;

public final class CreeperModel extends Model {

	private ModelPart head = new ModelPart(0, 0);
	private ModelPart unused;
	private ModelPart body;
	private ModelPart leg1;
	private ModelPart leg2;
	private ModelPart leg3;
	private ModelPart leg4;

	public CreeperModel() {
		head.setBounds(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		head.setPosition(0.0F, 4.0F, 0.0F);
		unused = new ModelPart(32, 0);
		unused.setBounds(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F + 0.5F);
		body = new ModelPart(16, 16);
		body.setBounds(-4.0F, 0.0F, -2.0F, 8, 12, 4, 0.0F);
		body.setPosition(0.0F, 4.0F, 0.0F);
		leg1 = new ModelPart(0, 16);
		leg1.setBounds(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		leg1.setPosition(-2.0F, 16.0F, 4.0F);
		leg2 = new ModelPart(0, 16);
		leg2.setBounds(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		leg2.setPosition(2.0F, 16.0F, 4.0F);
		leg3 = new ModelPart(0, 16);
		leg3.setBounds(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		leg3.setPosition(-2.0F, 16.0F, -4.0F);
		leg4 = new ModelPart(0, 16);
		leg4.setBounds(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.0F);
		leg4.setPosition(2.0F, 16.0F, -4.0F);
	}

	@Override
	public final void render(float var1, float var2, float var3, float var4, float var5, float var6) {
		head.yaw = var4 / 57.295776F;
		head.pitch = var5 / 57.295776F;
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
