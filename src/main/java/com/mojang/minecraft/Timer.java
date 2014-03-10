package com.mojang.minecraft;

public class Timer {
	float tps;

	double lastHR;
	public int elapsedTicks;
	public float delta;
	public float speed = 1F;
	public float elapsedDelta = 0F;
	long lastSysClock;
	long lastHRClock;
	double adjustment = 1D;

	public Timer(float tps) {
		this.tps = tps;
		lastSysClock = System.currentTimeMillis();
		lastHRClock = System.nanoTime() / 1000000L;
	}
}
