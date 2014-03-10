package com.mojang.minecraft;

import java.util.ArrayList;
import java.util.List;

import com.mojang.minecraft.level.tile.Block;

public final class SessionData {

    public static List<Block> allowedBlocks;

    public static void addStandardMinecraftBlocks() {
        ArrayList<Block> ab = new ArrayList<>();
        for (int i = 1; i < 50; i++) { // ignore air
            ab.add(Block.blocks[i]);
        }
        if (Minecraft.isSinglePlayer) {
            ab.remove(Block.BEDROCK); // players can't delete this
        }
        allowedBlocks = ab;
    }

    public static void setAllowedBlocks(byte supportLevel) {
        // latest
        if (supportLevel == com.oyasunadev.mcraft.client.util.Constants.CUSTOM_BLOCK_SUPPORT_LEVEL) {
            ArrayList<Block> ab = new ArrayList<>();
            for (int i = 1; i < Block.blocks.length; i++) {
                ab.add(Block.blocks[i]);
            }
            if (Minecraft.isSinglePlayer) {
                ab.remove(Block.BEDROCK);
            }
            allowedBlocks = ab;
        }

        else if (supportLevel == 1) { // level 1
            ArrayList<Block> ab = new ArrayList<>();
            for (int i = 1; i < 65; i++) {
                ab.add(Block.blocks[i]);
            }
            if (Minecraft.isSinglePlayer) {
                ab.remove(Block.BEDROCK);
            }
            allowedBlocks = ab;
        } else if (supportLevel <= 0) { // minecraft
            addStandardMinecraftBlocks();
        }
    }

    public String username;
    public String sessionId;

    public String mppass;

    public boolean haspaid;

    static {
        addStandardMinecraftBlocks(); // init
    }

    public SessionData(String username, String sessionID) {
        this.username = username;
        sessionId = sessionID;
    }
}
