package com.mojang.minecraft;

import com.mojang.minecraft.level.tile.Block;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SessionData {

	public static List allowedBlocks;
	public String username;
	public String sessionId;
	public String mppass;
	public boolean haspaid;

	public SessionData(String var1, String var2) {
		this.username = var1;
		this.sessionId = var2;
	}

	static {
		ArrayList<Block> temp = new ArrayList<Block>();
		for (int i = 1; i < Block.blocks.length; i++) {
			temp.add(Block.blocks[i]);
		}
		SetAllowedBlocks(temp);
	}

	public static void SetAllowedBlocks(ArrayList<Block> temp2) {
		ArrayList<Block> temp = new ArrayList<Block>();
		for (int i = 0; i < Block.blocks.length; i++) {
			if (temp2.contains(Block.blocks[i])) {
				temp.add(Block.blocks[i]);
			}
		}
		allowedBlocks = temp;
	}

	public static void SetAllowedBlocks(byte[] arrayBlocks) {
		ArrayList temp = new ArrayList();
		for (int i = 0; i < arrayBlocks.length; i++) {
			temp.add(arrayBlocks[i]);
		}
		SetAllowedBlocks(temp);
	}
}
