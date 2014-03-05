package com.mojang.minecraft.player;

import com.mojang.minecraft.mob.ai.BasicAI;

// PlayerAI
public class Player$1 extends BasicAI {
    public static final long serialVersionUID = 0L;

    private Player player;

    public Player$1(Player player) {
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