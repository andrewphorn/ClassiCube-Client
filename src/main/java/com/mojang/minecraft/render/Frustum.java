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
     *
     * @param F0
     * @param F1
     * @param F2
     * @param F3
     * @param F4
     * @param F5
     * @return boolean
     */
    // TODO Looks like some kind of product - simplify?
    public boolean isBoxInFrustum(float F0, float F1, float F2, float F3, float F4, float F5) {
        return !(frustum[0][0] * F0 + frustum[0][1] * F1 + frustum[0][2] * F2 + frustum[0][3] <= 0F
                && frustum[0][0] * F3 + frustum[0][1] * F1 + frustum[0][2] * F2 + frustum[0][3] <= 0F
                && frustum[0][0] * F0 + frustum[0][1] * F4 + frustum[0][2] * F2 + frustum[0][3] <= 0F
                && frustum[0][0] * F3 + frustum[0][1] * F4 + frustum[0][2] * F2 + frustum[0][3] <= 0F
                && frustum[0][0] * F0 + frustum[0][1] * F1 + frustum[0][2] * F5 + frustum[0][3] <= 0F
                && frustum[0][0] * F3 + frustum[0][1] * F1 + frustum[0][2] * F5 + frustum[0][3] <= 0F
                && frustum[0][0] * F0 + frustum[0][1] * F4 + frustum[0][2] * F5 + frustum[0][3] <= 0F
                && frustum[0][0] * F3 + frustum[0][1] * F4 + frustum[0][2] * F5 + frustum[0][3] <= 0F);
    }
}