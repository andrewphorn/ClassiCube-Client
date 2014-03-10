package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.Level;

public class Creeper extends Mob {

    public static final long serialVersionUID = 0L;

    public Creeper(Level var1, float var2, float var3, float var4) {
        super(var1);
        heightOffset = 1.62F;
        modelName = "creeper";
        textureName = "/mob/creeper.png";
        ai = new Creeper$1(this);
        ai.defaultLookAngle = 45;
        deathScore = 200;
        this.setPos(var2, var3, var4);
    }

    @Override
    public float getBrightness() {
        return 80;
    }
}
