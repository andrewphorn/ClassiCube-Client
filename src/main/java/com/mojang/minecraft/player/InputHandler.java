package com.mojang.minecraft.player;

public class InputHandler {

    public float xxa = 0F;
    public float yya = 0F;
    public boolean jumping = false;

    public boolean running = false;

    public boolean flyingUp = false;

    boolean canMove = true;

    public boolean flyingDown = false;
    public boolean flying = false;

    public float move = 0F;
    public float strafe = 0F;
    public float elevate = 0F;
    public float mult = 1F;
    public boolean fall = false;
    public boolean jump = false;
    public boolean cliplock = false;

    public int HacksMode = 0; // java doesn't have package aliasing

    // eg import x as y
    // so this has to be unique from the module name
    // so that I can import that module to check status of
    // noclip, fly, and speed.

    public void resetKeys() {}

    public void setKeyState(int key, boolean state) {}

    public void updateMovement(int HackMode) {}
}
