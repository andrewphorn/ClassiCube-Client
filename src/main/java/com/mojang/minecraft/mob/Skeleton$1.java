package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.mob.ai.BasicAttackAI;

final class Skeleton$1 extends BasicAttackAI {

    // $FF: synthetic field
    final Skeleton parent;

    Skeleton$1(Skeleton skeleton) {
        parent = skeleton;
    }

    @Override
    public final void beforeRemove() {
        Skeleton.shootRandomArrow(parent);
    }

    @Override
    public final void tick(Level level, Mob mob) {
        super.tick(level, mob);
        if (mob.health > 0 && random.nextInt(30) == 0 && attackTarget != null) {
            parent.shootArrow(level);
        }

    }
}
