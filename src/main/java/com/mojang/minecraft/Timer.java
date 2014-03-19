package com.mojang.minecraft;

public class Timer {
    public float tps;

    public double lastHR;
    public int elapsedTicks;
    public float delta;
    public float speed = 1F;
    public float elapsedDelta = 0F;
    public long lastSysClock;
    public long lastHRClock;
    public double adjustment = 1D;

    public Timer(float tps) {
        this.tps = tps;
        lastSysClock = System.currentTimeMillis();
        lastHRClock = System.nanoTime() / 1000000L;
    }
}
