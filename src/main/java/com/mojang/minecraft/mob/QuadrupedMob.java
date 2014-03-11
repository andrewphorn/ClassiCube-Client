package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.Level;

public class QuadrupedMob extends Mob {

    public static final long serialVersionUID = 0L;

    public QuadrupedMob(Level level, float posX, float posY, float posZ) {
        super(level);
        setSize(1.4F, 1.2F);
        this.setPos(posX, posY, posZ);
        // TODO wat
        modelName = "pig";
    }
}
