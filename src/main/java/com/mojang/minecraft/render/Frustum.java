package com.mojang.minecraft.render;

public class Frustum {

    public float frustum[][];

    public float projectionMatrix[];

    public float modelViewMatrix[];
    public float clippingMatrix[];

    public Frustum() {
        frustum = new float[16][16];
        projectionMatrix = new float[16];
        modelViewMatrix = new float[16];
        clippingMatrix = new float[16];
    }

    /**
     * Returns true if the box is inside all 6 clipping planes, otherwise returns false.
     */
    public boolean isBoxInFrustum(float F0, float F1, float F2, float F3, float F4, float F5) {
        final float frustum00F0 = frustum[0][0] * F0;
        final float frustum01F1 = frustum[0][1] * F1;
        final float frustum02F2 = frustum[0][2] * F2;
        return !(frustum00F0 + frustum01F1 + frustum02F2 + frustum[0][3] <= 0
                && frustum[0][0] * F3 + frustum01F1 + frustum02F2 + frustum[0][3] <= 0
                && frustum00F0 + frustum[0][1] * F4 + frustum02F2 + frustum[0][3] <= 0
                && frustum[0][0] * F3 + frustum[0][1] * F4 + frustum02F2 + frustum[0][3] <= 0
                && frustum00F0 + frustum01F1 + frustum[0][2] * F5 + frustum[0][3] <= 0
                && frustum[0][0] * F3 + frustum01F1 + frustum[0][2] * F5 + frustum[0][3] <= 0
                && frustum00F0 + frustum[0][1] * F4 + frustum[0][2] * F5 + frustum[0][3] <= 0
                && frustum[0][0] * F3 + frustum[0][1] * F4 + frustum[0][2] * F5 + frustum[0][3] <= 0);
    }
}