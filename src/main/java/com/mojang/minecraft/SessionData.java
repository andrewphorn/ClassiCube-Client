package com.mojang.minecraft;

import com.mojang.minecraft.level.tile.Block;
import java.util.ArrayList;
import java.util.List;

public final class SessionData {

    public static List<Block> allowedBlocks;
    public String username;
    public String sessionId;
    public String mppass;
    public boolean haspaid;

    public SessionData(String var1, String var2) {
	this.username = var1;
	this.sessionId = var2;
    }

    static {
	SetAllowedBlocks((byte) 1);
    }

    public static void SetAllowedBlocks(byte SupportLevel) {
	// latest
	if (SupportLevel == com.oyasunadev.mcraft.client.util.Constants.SupportLevel) {
	    ArrayList<Block> ab = new ArrayList<Block>();
	    for (int i = 1; i < Block.blocks.length; i++) {
		ab.add(Block.blocks[i]);
	    }
	    allowedBlocks = ab;
	}

	else if (SupportLevel == 1) {
	    ArrayList<Block> ab = new ArrayList<Block>();
	    for (int i = 1; i < 65; i++) {
		ab.add(Block.blocks[i]);
	    }
	    allowedBlocks = ab;
	} else if (SupportLevel <= 0) {
	    AddStandardMinecraftBlocks();
	}

    }

    public static void AddStandardMinecraftBlocks() {
	ArrayList<Block> ab = new ArrayList<Block>();
	for (int i = 1; i < 50; i++) { // ignore air
	    ab.add(Block.blocks[i]);
	}
	allowedBlocks = ab;
    }

}
