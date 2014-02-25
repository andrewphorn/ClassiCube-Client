package com.mojang.minecraft.level.tile;

public enum TextureSide {
	Bottom(0),
	Top(1),
	
	/*
	 * Not very sure about these values.
	 */
	Front(2),
	Back(3),
	Left(4),
	Right(5);
	
	private int id;
	private TextureSide(int id){
		this.id = id;
	}
	public int getID(){
		return id;
	}
}
