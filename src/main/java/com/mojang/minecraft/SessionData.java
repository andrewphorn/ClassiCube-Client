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
		allowedBlocks = Arrays.asList(Block.blocks);
		
	}
}
