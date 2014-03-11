package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.mob.ai.JumpAttackAI;

public class Spider extends QuadrupedMob {

    public static final long serialVersionUID = 0L;

    public Spider(Level level, float posX, float posY, float posZ) {
        super(level, posX, posY, posZ);
        heightOffset = 0.72F;
        modelName = "spider";
        textureName = "/mob/spider.png";
        setSize(1.4F, 0.9F);
        this.setPos(posX, posY, posZ);
        deathScore = 105;
        bobStrength = 0F;
        ai = new JumpAttackAI();
    }
}
