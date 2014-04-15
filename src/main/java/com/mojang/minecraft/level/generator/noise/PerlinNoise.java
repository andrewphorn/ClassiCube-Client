package com.mojang.minecraft.level.generator.noise;

import java.util.Random;

public class PerlinNoise extends Noise {

    private int[] noise;

    public PerlinNoise() {
        this(new Random());
    }

    public PerlinNoise(Random random) {
        noise = new int[512];

        int count = 0;
        while (count < 256) {
            noise[count] = count++;
        }

        for (count = 0; count < 256; count++) {
            int unknown0 = random.nextInt(256 - count) + count;
            int unknown1 = noise[count];

            noise[count] = noise[unknown0];
            noise[unknown0] = unknown1;
            noise[count + 256] = noise[count];
        }

    }

    private static double fade(double a) {
        return a * a * a * (a * (a * 6D - 15D) + 10D);
    }

    private static double grad(int hash, double x, double y) {
        hash &= 15;
        double u = hash < 8 ? x : y;
        double v = hash < 4 ? y : (hash != 12 && hash != 14 ? 0 : x);

        return ((hash & 1) == 0 ? u : -u) + ((hash & 2) == 0 ? v : -v);
    }

    private static double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    @Override
    public double compute(double x, double z) {
        int X = (int) Math.floor(x) & 255;
        int Z = (int) Math.floor(z) & 255;

        x -= Math.floor(x);
        z -= Math.floor(z);

        double u = fade(x);
        double v = fade(z);

        int a = noise[X] + Z;
        int aa = noise[a];
        int ab = noise[a + 1];

        int b = noise[X + 1] + Z;
        int ba = noise[b];
        int bb = noise[b + 1];

        return lerp(v, lerp(u, grad(noise[aa], x, z), grad(noise[ba], x - 1, z)),
                lerp(u, grad(noise[ab], x, z - 1), grad(noise[bb], x - 1, z - 1)));

    }
}
