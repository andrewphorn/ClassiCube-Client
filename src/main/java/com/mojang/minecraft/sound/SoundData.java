package com.mojang.minecraft.sound;

public class SoundData {
	public short[] data;

	public float length;

	public SoundData(short[] data, float length) {
		this.data = data;
		this.length = length;
	}
}
