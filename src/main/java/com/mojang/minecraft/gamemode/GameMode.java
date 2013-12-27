package com.mojang.minecraft.gamemode;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.player.Player;

public class GameMode {
	public Minecraft minecraft;

	public boolean instantBreak;

	public float reachDistance = 5.0F;

	public GameMode(Minecraft minecraft) {
		this.minecraft = minecraft;

		instantBreak = false;
	}

	public void apply(Level level) {
		level.creativeMode = false;
		level.growTrees = true;
	}

	public void apply(Player player) {
	}

	public void applyCracks(float time) {
	}

	public void breakBlock(int x, int y, int z) {
		Level level = minecraft.level;
		Block block = Block.blocks[level.getTile(x, y, z)];

		boolean success = level.netSetTile(x, y, z, 0);

		if (block != null && success) {
			if (minecraft.isOnline()) {
				minecraft.networkManager.sendBlockChange(x, y, z, 0,
						minecraft.player.inventory.getSelected());
			}

			if (block.stepSound != Block.soundNone) {
				level.playSound(block.stepSound.getBreakSound(), x + 0.5F, y + 0.5F, z + 0.5F, (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);       
			}

			block.spawnBreakParticles(level, x, y, z, minecraft.particleManager);
		}

	}

	public boolean canPlace(int block) {
		return true;
	}

	public float getReachDistance() {
		return reachDistance;
	}

	public void hitBlock(int x, int y, int z) {
		this.breakBlock(x, y, z);
	}

	public void hitBlock(int x, int y, int z, int side) {
	}

	public boolean isSurvival() {
		return true;
	}

	public void openInventory() {
	}

	public void prepareLevel(Level level) {
	}

	public void preparePlayer(Player player) {
	}

	public void resetHits() {
	}

	public void spawnMob() {
	}

	public boolean useItem(Player player, int type) {
		return false;
	}
}
