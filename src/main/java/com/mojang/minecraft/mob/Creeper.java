package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.Level;

public class Creeper extends Mob {

    public static final long serialVersionUID = 0L;

    public Creeper(Level level, float posX, float posY, float posZ) {
        super(level);
        heightOffset = 1.62F;
        modelName = "creeper";
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
