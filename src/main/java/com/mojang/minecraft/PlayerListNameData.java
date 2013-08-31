package com.mojang.minecraft;

public class PlayerListNameData {
    public short nameID;
    public String playerName;
    public String listName;
    public String groupName;
    public Byte groupRank;

    public PlayerListNameData(short NameID, String PlayerName, String ListName,
	    String GroupName, Byte GroupRank) {
	this.nameID = NameID;
	this.playerName = PlayerName;
	this.listName = ListName;
	this.groupName = GroupName;
	this.groupRank = GroupRank;
    }

}
