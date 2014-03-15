package com.mojang.minecraft;

import com.mojang.minecraft.player.Player;

import java.util.Comparator;

public class SelectionBoxDistanceComparator implements Comparator<SelectionBoxData> {

    private Player player;

    public SelectionBoxDistanceComparator(Player player) {
        this.player = player;
    }

    @Override
    public int compare(SelectionBoxData o1, SelectionBoxData o2) {
        float sqDist0 = o1.distanceSquared0(player);
        float otherSqDist0 = o2.distanceSquared0(player);
        float sqDist1 = o1.distanceSquared1(player);
        float otherSqDist1 = o2.distanceSquared1(player);

        return (int) (Math.max(otherSqDist0, otherSqDist1) - Math.max(sqDist0, sqDist1));
    }
}
