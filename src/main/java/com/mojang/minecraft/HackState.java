package com.mojang.minecraft;

public class HackState {
	public static boolean Noclip, Speed, Fly, OpHacks = true;

	public static void setAllDisabled() {
		Noclip = false;
		Speed = false;
		Fly = false;
		OpHacks = false;
	}

	public static void setAllEnabled() {
		Noclip = true;
		Speed = true;
		Fly = true;
		OpHacks = true;
	}
}
