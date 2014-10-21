package com.mojang.util;

public class Timer {
    public static final double NANOSEC_PER_SEC = 1000000000D;
    public float tps;

    public double firstHR;
    public double lastHR;
    public int elapsedTicks;
    public float delta;
    public float speed = 1F;
    public float elapsedDelta = 0F;
    
    public double lastFrameDuration;

    public Timer(float tps) {
        this.tps = tps;
        this.firstHR = System.nanoTime()/NANOSEC_PER_SEC;
    }
}
