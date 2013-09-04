package com.mojang.minecraft.player;

public class InputHandler {
    
    public float xxa = 0.0F;
    public float yya = 0.0F;
    public boolean jumping = false;

    public boolean running = false;

    public boolean flyingUp = false;

    public boolean flyingDown = false;
    public boolean flying = false;
    
    public boolean noClip = false;
    
    public float move = 0.0F;
    public float strafe = 0.0F;
    public float elevate = 0.0F;
    public float mult = 1.0F;
    public boolean fall = false;
    public boolean jump = false;
    public boolean fly = false;
    public boolean noclip = false;
    public boolean cliplock = false;
    
    public int HackState = 0;
    
    public void calc(){}

    public void updateMovement(int HackMode) {
    }

    public void resetKeys() {
    }

    public void setKeyState(int key, boolean state) {
    }
}
