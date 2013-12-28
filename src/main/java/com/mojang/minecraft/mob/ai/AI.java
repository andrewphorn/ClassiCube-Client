package com.mojang.minecraft.mob.ai;

import java.io.Serializable;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.mob.Mob;

public abstract class AI implements Serializable {

	public static final long serialVersionUID = 0L;
	public int defaultLookAngle = 0;

	public void beforeRemove() {
	}

	public void hurt(Entity var1, int var2) {
	}

	public void tick(Level var1, Mob var2) {
	}
}
