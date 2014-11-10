package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.mob.ai.BasicAttackAI;

public class Zombie extends HumanoidMob {

    public Zombie(Level level, float posX, float posY, float posZ) {
        super(level, posX, posY, posZ);
        modelName = "zombie";
        textureName = "/mob/zombie.png";
        heightOffset = 1.62F;
        BasicAttackAI ai = new BasicAttackAI();
        deathScore = 80;
        ai.defaultLookAngle = 30;
        // ai.runSpeed = 1F;
        this.ai = ai;
    }
}
