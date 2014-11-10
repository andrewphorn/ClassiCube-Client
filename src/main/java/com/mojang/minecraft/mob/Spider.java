package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.mob.ai.JumpAttackAI;
import com.mojang.minecraft.render.texture.Textures;

public class Spider extends QuadrupedMob {

    public Spider(Level level, float posX, float posY, float posZ) {
        super(level, "spider", posX, posY, posZ);
        heightOffset = 0.72F;
        textureName = Textures.MOB_SPIDER;
        setSize(1.4F, 0.9F);
        this.setPos(posX, posY, posZ);
        deathScore = 105;
        bobStrength = 0F;
        ai = new JumpAttackAI();
    }
}
