package com.mojang.util;

public final class MathHelper {

    private static float[] SIN_TABLE = new float[65536];

    static {
        for (int i = 0; i < 65536; ++i) {
            SIN_TABLE[i] = (float) Math.sin(((double) i) * Math.PI * 2D / 65536D);
        }

    }

    public static float cos(float theta) {
        return SIN_TABLE[(int) (theta * (float) (32768D / Math.PI) + 16384F) & '\uffff'];
    }

    public static float sin(float theta) {
        return SIN_TABLE[(int) (theta * (float) (32768D / Math.PI)) & '\uffff'];
    }

    public static float sqrt(float num) {
        return (float) Math.sqrt(num);
    }
}
