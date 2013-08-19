package com.mojang.minecraft.gamemode;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.SessionData;
import com.mojang.minecraft.gui.BlockSelectScreen;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.player.Player;

public class CreativeGameMode extends GameMode
{
	public CreativeGameMode(Minecraft minecraft)
	{
		super(minecraft);

		instantBreak = true;
	}

	@Override
	public void apply(Level level)
	{
		super.apply(level);

		level.removeAllNonCreativeModeEntities();

		level.creativeMode = true;
		level.growTrees = false;
	}

	@Override
	public void openInventory()
	{
		BlockSelectScreen blockSelectScreen = new BlockSelectScreen();

		minecraft.setCurrentScreen(blockSelectScreen);
	}

	@Override
	public boolean isSurvival()
	{
		return false;
	}

	@Override
	public void apply(Player player)
	{
		player.inventory.slots[0] = Block.STONE.id;
		player.inventory.slots[1] = Block.COBBLESTONE.id;
		player.inventory.slots[2] = Block.BRICK.id;
		player.inventory.slots[3] = Block.DIRT.id;
		player.inventory.slots[4] = Block.WOOD.id;
		player.inventory.slots[5] = Block.LOG.id;
		player.inventory.slots[6] = 0;
		player.inventory.slots[7] = Block.GRASS.id;
		player.inventory.slots[8] = Block.SLAB.id;
	}
}
