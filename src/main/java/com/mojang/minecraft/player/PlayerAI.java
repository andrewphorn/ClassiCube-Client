package com.mojang.minecraft.player;

import com.mojang.minecraft.mob.ai.BasicAI;

// PlayerAI
public class PlayerAI extends BasicAI {
    private final Player player;

    public PlayerAI(Player player) {
        this.player = player;
    }

    @Override
    protected void update() {
        jumping = player.input.jumping;
        running = player.input.running;
        flying = player.input.flying;
        flyingUp = player.input.flyingUp;
        flyingDown = player.input.flyingDown;
        xxa = player.input.xxa;
        yya = player.input.yya;
    }
}