package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.mob.ai.BasicAttackAI;
import com.mojang.minecraft.render.texture.Textures;

public class Zombie extends HumanoidMob {

    public Zombie(Level level, float posX, float posY, float posZ) {
        super(level, "zombie", posX, posY, posZ);
        textureName = Textures.MOB_ZOMBIE;
        heightOffset = 1.62F;
        ai = new BasicAttackAI();
        deathScore = 80;
        ai.defaultLookAngle = 30;
        // ai.runSpeed = 1F;
    }
}
