package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.mob.ai.JumpAttackAI;

public class Spider extends QuadrupedMob {

	public static final long serialVersionUID = 0L;

	public Spider(Level var1, float var2, float var3, float var4) {
		super(var1, var2, var3, var4);
		heightOffset = 0.72F;
		modelName = "spider";
		textureName = "/mob/spider.png";
		setSize(1.4F, 0.9F);
		this.setPos(var2, var3, var4);
		deathScore = 105;
		bobStrength = 0F;
		ai = new JumpAttackAI();
	}
}
