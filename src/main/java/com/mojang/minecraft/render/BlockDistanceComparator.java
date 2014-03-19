package com.mojang.minecraft.render;

import com.mojang.minecraft.player.Player;

import java.util.Comparator;

public class BlockDistanceComparator implements Comparator<BlockData> {
    private Player player;

    public BlockDistanceComparator(Player player) {
        this.player = player;
    }

    @Override
    public int compare(BlockData block, BlockData other) {

        float sqDist = block.distanceSquared(player);
        float otherSqDist = other.distanceSquared(player);

        if (sqDist == otherSqDist) {
            return 0;
        } else if (sqDist > otherSqDist) {
            return -1;
        } else {
            return 1;
        }
    }
}
