package com.mojang.minecraft.render;

public class Frustrum {

	public Frustrum() {
		frustum = new float[16][16];
		projectionMatrix = new float[16];
		modelviewMatrix = new float[16];
		clippingMatrix = new float[16];
	}

	public boolean isBoxInFrustum(float F, float F1, float F2, float F3, float F4, float F5) {
		int i = 0;
			if ((float) frustum[i][0] * F + (float) frustum[i][1] * F1 + (float) frustum[i][2]
					* F2 + (float) frustum[i][3] <= 0.0F
					&& (float) frustum[i][0] * F3 + (float) frustum[i][1] * F1
							+ (float) frustum[i][2] * F2 + (float) frustum[i][3] <= 0.0F
					&& (float) frustum[i][0] * F + (float) frustum[i][1] * F4
							+ (float) frustum[i][2] * F2 + (float) frustum[i][3] <= 0.0F
					&& (float) frustum[i][0] * F3 + (float) frustum[i][1] * F4
							+ (float) frustum[i][2] * F2 + (float) frustum[i][3] <= 0.0F
					&& (float) frustum[i][0] * F + (float) frustum[i][1] * F1
							+ (float) frustum[i][2] * F5 + (float) frustum[i][3] <= 0.0F
					&& (float) frustum[i][0] * F3 + (float) frustum[i][1] * F1
							+ (float) frustum[i][2] * F5 + (float) frustum[i][3] <= 0.0F
					&& (float) frustum[i][0] * F + (float) frustum[i][1] * F4
							+ (float) frustum[i][2] * F5 + (float) frustum[i][3] <= 0.0F
					&& (float) frustum[i][0] * F3 + (float) frustum[i][1] * F4
							+ (float) frustum[i][2] * F5 + (float) frustum[i][3] <= 0.0F) {
				return false;
		}

		return true;
	}

	public float frustum[][];
	public float projectionMatrix[];
	public float modelviewMatrix[];
	public float clippingMatrix[];
}