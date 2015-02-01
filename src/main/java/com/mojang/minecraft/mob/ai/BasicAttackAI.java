package com.mojang.minecraft.mob.ai;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.item.Arrow;
import com.mojang.util.MathHelper;
import com.mojang.util.Vector3f;

public class BasicAttackAI extends BasicAI {

    public int damage = 6;

    public boolean attack(Entity var1) {
        if (level.clip(new Vector3f(mob.x, mob.y, mob.z), new Vector3f(var1.x, var1.y, var1.z)) != null) {
            return false;
        } else {
            mob.attackTime = 5;
            attackDelay = random.nextInt(20) + 10;
            int var2 = (int) ((random.nextFloat() + random.nextFloat()) / 2F * damage + 1F);
            var1.hurt(mob, var2);
            noActionTime = 0;
            return true;
        }
    }

    protected void doAttack() {
        Entity var1 = level.getPlayer();
        float var2 = 16F;
        if (attackTarget != null && attackTarget.removed) {
            attackTarget = null;
        }

        float var3;
        float var4;
        float var5;
        if (var1 != null && attackTarget == null) {
            var3 = var1.x - mob.x;
            var4 = var1.y - mob.y;
            var5 = var1.z - mob.z;
            if (var3 * var3 + var4 * var4 + var5 * var5 < var2 * var2) {
                attackTarget = var1;
            }
        }

        if (attackTarget != null) {
            var3 = attackTarget.x - mob.x;
            var4 = attackTarget.y - mob.y;
            var5 = attackTarget.z - mob.z;
            float var6;
            if ((var6 = var3 * var3 + var4 * var4 + var5 * var5) > var2 * var2 * 2F * 2F
                    && random.nextInt(100) == 0) {
                attackTarget = null;
            }

            if (attackTarget != null) {
                var6 = MathHelper.sqrt(var6);
                mob.yRot = (float) (Math.atan2(var5, var3) * 180D / 3.1415927410125732D) - 90F;
                mob.xRot = -((float) (Math.atan2(var4, var6) * 180D / 3.1415927410125732D));
                if (MathHelper.sqrt(var3 * var3 + var4 * var4 + var5 * var5) < 2F
                        && attackDelay == 0) {
                    attack(attackTarget);
                }
            }

        }
    }

    @Override
    public void hurt(Entity other, int amount) {
        super.hurt(other, amount);
        if (other instanceof Arrow) {
            other = ((Arrow) other).getOwner();
        }

        if (other != null && !other.getClass().equals(mob.getClass())) {
            attackTarget = other;
        }

    }

    @Override
    protected void update() {
        super.update();
        if (mob.health > 0) {
            doAttack();
        }

    }
}
