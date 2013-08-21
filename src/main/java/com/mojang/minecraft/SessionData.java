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
		SetAllowedBlocks(true);
	}

	public static void SetAllowedBlocks(boolean SinglePlayer) {
		if (SinglePlayer) {
			allowedBlocks = Arrays.asList(Block.blocks);
		} else {
			List<Block> temp = new ArrayList<Block>();
			for (int i = 0; i < Block.blocks.length; i++) {
				if (i != 9 && i != 11) {
					temp.add(Block.blocks[i]);
				}
			}
			allowedBlocks = temp;
		}
	}
}
