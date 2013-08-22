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
		SetAllowedBlocks(true, null);
	}

	public static void SetAllowedBlocks(boolean AllBlocks, ArrayList disallowedBlocks) {
		if (AllBlocks) {
			allowedBlocks = Arrays.asList(Block.blocks);
		} else {
			List<Block> temp = new ArrayList<Block>();
			for (int i = 0; i < Block.blocks.length; i++) {
				if (!disallowedBlocks.contains(i)) {
					temp.add(Block.blocks[i]);
				}
			}
			allowedBlocks = temp;
		}
	}
	public static void SetAllowedBlocks(byte[] disallowedBlocks) {
		ArrayList temp = new ArrayList();
		for(int i = 0; i< disallowedBlocks.length; i++){
			temp.add(disallowedBlocks[i]);
		}
		SetAllowedBlocks(false, temp);
	}
}
