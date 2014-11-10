package com.mojang.util;

import java.awt.Color;
import java.io.Serializable;

/**
 * Custom class used to store data compatible with GL11.Color3f Each color (R G
 * B A) is fixed to become a maximum of 1f
 *
 * @author Jon
 */
public class ColorCache implements Serializable {

    public float R;
    public float G;
    public float B;
    public float A;

    public ColorCache(float r, float g, float b) {
        R = FixColor(r);
        G = FixColor(g);
        B = FixColor(b);
        A = 1F;
    }

    public ColorCache(float r, float g, float b, float a) {
        R = FixColor(r);
        G = FixColor(g);
        B = FixColor(b);
        A = a;
    }

    public static ColorCache parseHex(String hex) {
        Color col = Color.decode("#" + hex);
        float r = col.getRed() / 255f;
        float g = col.getGreen() / 255f;
        float b = col.getBlue() / 255f;
        return new ColorCache(r, g, b);
    }

    private float FixColor(float color) {
        if (color > 1f) {
            return 1f;
        }
        if (color < 0f) {
            return 0f;
        }
        return color;
    }
}
