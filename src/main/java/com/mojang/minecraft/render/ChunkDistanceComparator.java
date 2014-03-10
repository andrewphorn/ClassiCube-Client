package com.mojang.minecraft.render;

import java.util.Comparator;

import com.mojang.minecraft.player.Player;

public class ChunkDistanceComparator implements Comparator<Chunk> {
    private Player player;

    public ChunkDistanceComparator(Player player) {
        this.player = player;
    }

    @Override
    public int compare(Chunk chunk, Chunk other) {

        float sqDist = chunk.distanceSquared(player);
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
