package com.mojang.minecraft;

public class HackState {
	public static boolean Noclip, Speed, Fly, Respawn, OpHacks = true;

	public static void setAllDisabled() {
		Noclip = false;
		Speed = false;
		Fly = false;
		Respawn = false;
		OpHacks = false;
	}

	public static void setAllEnabled() {
		Noclip = true;
		Speed = true;
		Fly = true;
		Respawn = true;
		OpHacks = true;
	}
}
