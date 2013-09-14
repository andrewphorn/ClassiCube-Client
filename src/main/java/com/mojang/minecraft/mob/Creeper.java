package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.Level;
import com.mojang.util.MathHelper;

public class Creeper extends Mob {

	public static final long serialVersionUID = 0L;

	public Creeper(Level var1, float var2, float var3, float var4) {
		super(var1);
		this.heightOffset = 1.62F;
		this.modelName = "creeper";
		this.textureName = "/mob/creeper.png";
		this.ai = new Creeper$1(this);
		this.ai.defaultLookAngle = 45;
		this.deathScore = 200;
		this.setPos(var2, var3, var4);
	}

	public float getBrightness(float var1) {
		float var2 = (float) (20 - this.health) / 20.0F;
		return 80;
	}
}
