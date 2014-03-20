package com.mojang.minecraft;

import java.util.ArrayList;
import java.util.Arrays
import java.util.List;

import com.mojang.minecraft.level.tile.Block;
import com.oyasunadev.mcraft.client.util.Constants;

public final class SessionData {

    public static List<Block> allowedBlocks;
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

    public static void addStandardMinecraftBlocks() {
        ArrayList<Block> ab = new ArrayList<>();
        ab.addAll(Arrays.asList(Block.blocks).subList(1, 50));
        if (Minecraft.isSinglePlayer) {
            ab.remove(Block.BEDROCK); // players can't delete this
        }
        allowedBlocks = ab;
    }

    public static void setAllowedBlocks(byte supportLevel) {
        // latest
        if (supportLevel == Constants.CUSTOM_BLOCK_SUPPORT_LEVEL) {
            ArrayList<Block> ab = new ArrayList<>();
            ab.addAll(Arrays.asList(Block.blocks).subList(1, Block.blocks.length));
            if (Minecraft.isSinglePlayer) {
                ab.remove(Block.BEDROCK);
            }
            allowedBlocks = ab;
        } else if (supportLevel == 1) { // level 1
            ArrayList<Block> ab = new ArrayList<>();
            ab.addAll(Arrays.asList(Block.blocks).subList(1, 65));
            if (Minecraft.isSinglePlayer) {
                ab.remove(Block.BEDROCK);
            }
            allowedBlocks = ab;
        } else if (supportLevel <= 0) { // minecraft
            addStandardMinecraftBlocks();
        }
    }
}
