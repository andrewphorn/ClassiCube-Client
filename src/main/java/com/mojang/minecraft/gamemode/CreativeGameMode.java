package com.mojang.minecraft.gamemode;

import com.mojang.minecraft.GameSettings;
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

		if(GameSettings.CanReplaceSlot)
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
		//default starting blocks
		Block[] blocks = new Block[]{
				Block.STONE,Block.COBBLESTONE, Block.BRICK, 
				Block.DIRT, Block.WOOD, Block.LOG, 
				Block.LEAVES,Block.GRASS,Block.SLAB
		};
		
		boolean CanProceed = true;
		for(int i=0; i< blocks.length; i++){
			if(!SessionData.allowedBlocks.contains(blocks[i])){
				CanProceed = false;
			}
		}
		
		//if one of them is banned, instead pick 9 blocks from allowed blocks
		if(!CanProceed)
		{
			blocks = new Block[]{};
			for(int i = 0; i< blocks.length; i++){
				blocks[i] = (Block) SessionData.allowedBlocks.get(i);
			}
		}
		
		//set them
		for(int i = 0; i< blocks.length; i++){
			player.inventory.slots[i] = blocks[i].id;
		}
	}
}
