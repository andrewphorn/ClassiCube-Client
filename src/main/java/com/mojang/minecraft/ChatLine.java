package com.mojang.minecraft;

public class ChatLine {
    public String message;

    public int time;
    public ChatLine(String message) {
	this.message = message;
	this.time = 0;
    }
}
