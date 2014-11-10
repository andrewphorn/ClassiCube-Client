package com.mojang.minecraft.mob.ai;

public class JumpAttackAI extends BasicAttackAI {

    public JumpAttackAI() {
        // this.runSpeed *= 0.8F;
    }

    @Override
    protected void jumpFromGround() {
        if (attackTarget == null) {
            super.jumpFromGround();
        } else {
            mob.xd = 0F;
            mob.zd = 0F;
            mob.moveRelative(0F, 1F, 0.6F);
            mob.yd = 0.5F;
        }
    }
}
