package com.mojang.minecraft.render;

public class Frustrum {

	public float frustum[][];

	public float projectionMatrix[];

	public float modelviewMatrix[];
	public float clippingMatrix[];

	public Frustrum() {
		frustum = new float[16][16];
		projectionMatrix = new float[16];
		modelviewMatrix = new float[16];
		clippingMatrix = new float[16];
	}

	public boolean isBoxInFrustum(float F, float F1, float F2, float F3, float F4, float F5) {
		int i = 0;
		if (frustum[i][0] * F + frustum[i][1] * F1 + frustum[i][2] * F2 + frustum[i][3] <= 0F
				&& frustum[i][0] * F3 + frustum[i][1] * F1 + frustum[i][2] * F2 + frustum[i][3] <= 0F
				&& frustum[i][0] * F + frustum[i][1] * F4 + frustum[i][2] * F2 + frustum[i][3] <= 0F
				&& frustum[i][0] * F3 + frustum[i][1] * F4 + frustum[i][2] * F2 + frustum[i][3] <= 0F
				&& frustum[i][0] * F + frustum[i][1] * F1 + frustum[i][2] * F5 + frustum[i][3] <= 0F
				&& frustum[i][0] * F3 + frustum[i][1] * F1 + frustum[i][2] * F5 + frustum[i][3] <= 0F
				&& frustum[i][0] * F + frustum[i][1] * F4 + frustum[i][2] * F5 + frustum[i][3] <= 0F
				&& frustum[i][0] * F3 + frustum[i][1] * F4 + frustum[i][2] * F5 + frustum[i][3] <= 0F) {
			return false;
		}

		return true;
	}
}