package com.mojang.minecraft.mob;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.mob.ai.BasicAttackAI;

final class Creeper$1 extends BasicAttackAI {

    public static final long serialVersionUID = 0L;
    // $FF: synthetic field
    final Creeper creeper;

    Creeper$1(Creeper creeper) {
        this.creeper = creeper;
    }

    @Override
    public final boolean attack(Entity other) {
        if (!super.attack(other)) {
            return false;
        } else {
            mob.hurt(other, 6);
            return true;
        }
    }

}
