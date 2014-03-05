package com.mojang.minecraft;

import java.util.Comparator;

public class PlayerListComparator implements Comparator<PlayerListNameData> {

    @Override
    public int compare(PlayerListNameData o1, PlayerListNameData o2) {
        // TODO Auto-generated method stub
        return o1.groupName.compareToIgnoreCase(o2.groupName);
    }
}
