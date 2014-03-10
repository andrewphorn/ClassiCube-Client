package com.mojang.minecraft;

public class PlayerListNameData {
    public short nameID;
    public String playerName;
    public String listName;
    public String groupName;
    public Byte groupRank;

    public PlayerListNameData(short NameID, String PlayerName, String ListName, String GroupName,
            Byte GroupRank) {
        nameID = NameID;
        playerName = PlayerName;
        listName = ListName;
        groupName = GroupName;
        groupRank = GroupRank;
    }
}
