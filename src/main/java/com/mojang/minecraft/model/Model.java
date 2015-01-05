package com.mojang.minecraft.model;

public abstract class Model {
    public static final String HUMANOID = "humanoid";

    public float attackOffset;
    public float headOffset;

    /**
     * Sets the model's various rotation angles. For bipeds, var1 and var2 are
     * used for animating the movement of arms and legs, where var1 represents
     * the time(so that arms and legs swing back and forth) and var2 represents
     * how "far" arms and legs can swing at most.
     */
    public void render(float var1, float var2, float var3,
            float yawDegrees, float pitchDegrees, float scale) {
    }
}
