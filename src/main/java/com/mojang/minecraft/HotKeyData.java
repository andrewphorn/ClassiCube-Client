package com.mojang.minecraft;

public class HotKeyData {
	public String label;
	public String action;
	public int keyCode;
	public byte keyMods;

	public HotKeyData(String label, String action, int keyCode, byte keyMods) {
		this.label = label;
		this.action = action;
		this.keyCode = keyCode;
		this.keyMods = keyMods;
	}
}
