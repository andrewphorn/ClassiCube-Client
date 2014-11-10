package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.Level;

public class QuadrupedMob extends Mob {

    protected QuadrupedMob(Level level, String modelName, float posX, float posY, float posZ) {
        super(level, modelName);
        setSize(1.4F, 1.2F);
        this.setPos(posX, posY, posZ);
    }
}
