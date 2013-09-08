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
	this.jumping = player.input.jumping;
	this.running = player.input.running;
	this.flying = player.input.flying;
	this.flyingUp = player.input.flyingUp;
	this.flyingDown = player.input.flyingDown;
	this.xxa = player.input.xxa;
	this.yya = player.input.yya;
    }
}