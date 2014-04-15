package com.mojang.minecraft;

public class HackState {
    public static boolean noclip, speed, fly, respawn, opHacks = true;

    public static void setAllDisabled() {
        noclip = false;
        speed = false;
        fly = false;
        opHacks = false;
    }

    public static void setAllEnabled() {
        noclip = true;
        speed = true;
        fly = true;
        respawn = true;
        opHacks = true;
    }
}
