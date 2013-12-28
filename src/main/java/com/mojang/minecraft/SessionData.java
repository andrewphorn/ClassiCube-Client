package com.mojang.minecraft;

import java.util.ArrayList;
import java.util.List;

import com.mojang.minecraft.level.tile.Block;

public final class SessionData {

	public static List<Block> allowedBlocks;

	public static void AddStandardMinecraftBlocks() {
		ArrayList<Block> ab = new ArrayList<Block>();
		for (int i = 1; i < 50; i++) { // ignore air
			ab.add(Block.blocks[i]);
		}
		if (Minecraft.isSinglePlayer) {
			ab.remove(Block.BEDROCK); // players cant delete this
		}
		allowedBlocks = ab;
	}

	public static void SetAllowedBlocks(byte SupportLevel) {
		// latest
		if (SupportLevel == com.oyasunadev.mcraft.client.util.Constants.SupportLevel) {
			ArrayList<Block> ab = new ArrayList<Block>();
			for (int i = 1; i < Block.blocks.length; i++) {
				ab.add(Block.blocks[i]);
			}
			if (Minecraft.isSinglePlayer) {
				ab.remove(Block.BEDROCK);
			}
			allowedBlocks = ab;
		}

		else if (SupportLevel == 1) { // level 1
			ArrayList<Block> ab = new ArrayList<Block>();
			for (int i = 1; i < 65; i++) {
				ab.add(Block.blocks[i]);
			}
			if (Minecraft.isSinglePlayer) {
				ab.remove(Block.BEDROCK);
			}
			allowedBlocks = ab;
		} else if (SupportLevel <= 0) { // minecraft
			AddStandardMinecraftBlocks();
		}
	}

	public String username;
	public String sessionId;

	public String mppass;

	public boolean haspaid;

	static {
		AddStandardMinecraftBlocks(); // init
	}

	public SessionData(String username, String sessionID) {
		this.username = username;
		sessionId = sessionID;
	}
}
