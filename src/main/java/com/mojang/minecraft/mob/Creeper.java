package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.Level;

public class Creeper extends Mob {

    public Creeper(Level level, float posX, float posY, float posZ) {
        super(level, "creeper");
        heightOffset = 1.62F;
        textureName = "/mob/creeper.png";
        ai = new Creeper$1(this);
        ai.defaultLookAngle = 45;
        deathScore = 200;
        this.setPos(posX, posY, posZ);
    }

    @Override
    public float getBrightness() {
        return 80;
    }
}
