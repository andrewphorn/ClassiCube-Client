package com.mojang.minecraft.mob;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.mob.ai.BasicAttackAI;
import com.mojang.minecraft.particle.TerrainParticle;
import com.mojang.util.MathHelper;

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

    @Override
    public final void beforeRemove() {
        float radius = 4F;
        level.explode(mob, mob.x, mob.y, mob.z, radius);

        for (int i = 0; i < 500; ++i) {
            float var3 = (float) random.nextGaussian() * radius / 4F;
            float var4 = (float) random.nextGaussian() * radius / 4F;
            float var5 = (float) random.nextGaussian() * radius / 4F;
            float var6 = MathHelper.sqrt(var3 * var3 + var4 * var4 + var5 * var5);
            float var7 = var3 / var6 / var6;
            float var8 = var4 / var6 / var6;
            var6 = var5 / var6 / var6;
            level.particleEngine.spawnParticle(new TerrainParticle(level, mob.x + var3, mob.y
                    + var4, mob.z + var5, var7, var8, var6, Block.LEAVES));
        }

    }
}
