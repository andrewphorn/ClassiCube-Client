package com.mojang.minecraft.gui;

public class TexturePackData {
	String location;
	String name;

	public TexturePackData(String fileLocation, String texturePackName) {
		name = texturePackName;
		location = fileLocation;
	}
}
