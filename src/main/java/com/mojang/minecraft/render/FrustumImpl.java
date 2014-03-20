package com.mojang.minecraft.render;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.mojang.util.MathHelper;

public final class FrustumImpl extends Frustum {

    private static FrustumImpl instance = new FrustumImpl();
    private FloatBuffer projectionMatrixBuffer;
    private FloatBuffer modelViewMatrixBuffer;

    public FrustumImpl() {
        projectionMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
        modelViewMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
    }

    public static FrustumImpl getInstance() {
        instance.init();
        return instance;
    }

    private void init() {
        projectionMatrixBuffer.clear();
        modelViewMatrixBuffer.clear();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrixBuffer);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelViewMatrixBuffer);
        projectionMatrixBuffer.flip().limit(16);
        projectionMatrixBuffer.get(projectionMatrix);
        modelViewMatrixBuffer.flip().limit(16);
        modelViewMatrixBuffer.get(modelViewMatrix);
        clippingMatrix[0] = modelViewMatrix[0] * projectionMatrix[0] + modelViewMatrix[1]
                * projectionMatrix[4] + modelViewMatrix[2] * projectionMatrix[8]
                + modelViewMatrix[3] * projectionMatrix[12];
        clippingMatrix[1] = modelViewMatrix[0] * projectionMatrix[1] + modelViewMatrix[1]
                * projectionMatrix[5] + modelViewMatrix[2] * projectionMatrix[9]
                + modelViewMatrix[3] * projectionMatrix[13];
        clippingMatrix[2] = modelViewMatrix[0] * projectionMatrix[2] + modelViewMatrix[1]
                * projectionMatrix[6] + modelViewMatrix[2] * projectionMatrix[10]
                + modelViewMatrix[3] * projectionMatrix[14];
        clippingMatrix[3] = modelViewMatrix[0] * projectionMatrix[3] + modelViewMatrix[1]
                * projectionMatrix[7] + modelViewMatrix[2] * projectionMatrix[11]
                + modelViewMatrix[3] * projectionMatrix[15];
        clippingMatrix[4] = modelViewMatrix[4] * projectionMatrix[0] + modelViewMatrix[5]
                * projectionMatrix[4] + modelViewMatrix[6] * projectionMatrix[8]
                + modelViewMatrix[7] * projectionMatrix[12];
        clippingMatrix[5] = modelViewMatrix[4] * projectionMatrix[1] + modelViewMatrix[5]
                * projectionMatrix[5] + modelViewMatrix[6] * projectionMatrix[9]
                + modelViewMatrix[7] * projectionMatrix[13];
        clippingMatrix[6] = modelViewMatrix[4] * projectionMatrix[2] + modelViewMatrix[5]
                * projectionMatrix[6] + modelViewMatrix[6] * projectionMatrix[10]
                + modelViewMatrix[7] * projectionMatrix[14];
        clippingMatrix[7] = modelViewMatrix[4] * projectionMatrix[3] + modelViewMatrix[5]
                * projectionMatrix[7] + modelViewMatrix[6] * projectionMatrix[11]
                + modelViewMatrix[7] * projectionMatrix[15];
        clippingMatrix[8] = modelViewMatrix[8] * projectionMatrix[0] + modelViewMatrix[9]
                * projectionMatrix[4] + modelViewMatrix[10] * projectionMatrix[8]
                + modelViewMatrix[11] * projectionMatrix[12];
        clippingMatrix[9] = modelViewMatrix[8] * projectionMatrix[1] + modelViewMatrix[9]
                * projectionMatrix[5] + modelViewMatrix[10] * projectionMatrix[9]
                + modelViewMatrix[11] * projectionMatrix[13];
        clippingMatrix[10] = modelViewMatrix[8] * projectionMatrix[2] + modelViewMatrix[9]
                * projectionMatrix[6] + modelViewMatrix[10] * projectionMatrix[10]
                + modelViewMatrix[11] * projectionMatrix[14];
        clippingMatrix[11] = modelViewMatrix[8] * projectionMatrix[3] + modelViewMatrix[9]
                * projectionMatrix[7] + modelViewMatrix[10] * projectionMatrix[11]
                + modelViewMatrix[11] * projectionMatrix[15];
        clippingMatrix[12] = modelViewMatrix[12] * projectionMatrix[0] + modelViewMatrix[13]
                * projectionMatrix[4] + modelViewMatrix[14] * projectionMatrix[8]
                + modelViewMatrix[15] * projectionMatrix[12];
        clippingMatrix[13] = modelViewMatrix[12] * projectionMatrix[1] + modelViewMatrix[13]
                * projectionMatrix[5] + modelViewMatrix[14] * projectionMatrix[9]
                + modelViewMatrix[15] * projectionMatrix[13];
        clippingMatrix[14] = modelViewMatrix[12] * projectionMatrix[2] + modelViewMatrix[13]
                * projectionMatrix[6] + modelViewMatrix[14] * projectionMatrix[10]
                + modelViewMatrix[15] * projectionMatrix[14];
        clippingMatrix[15] = modelViewMatrix[12] * projectionMatrix[3] + modelViewMatrix[13]
                * projectionMatrix[7] + modelViewMatrix[14] * projectionMatrix[11]
                + modelViewMatrix[15] * projectionMatrix[15];
        frustum[0][0] = clippingMatrix[3] - clippingMatrix[0];
        frustum[0][1] = clippingMatrix[7] - clippingMatrix[4];
        frustum[0][2] = clippingMatrix[11] - clippingMatrix[8];
        frustum[0][3] = clippingMatrix[15] - clippingMatrix[12];
        normalize(frustum, 0);
        frustum[1][0] = clippingMatrix[3] + clippingMatrix[0];
        frustum[1][1] = clippingMatrix[7] + clippingMatrix[4];
        frustum[1][2] = clippingMatrix[11] + clippingMatrix[8];
        frustum[1][3] = clippingMatrix[15] + clippingMatrix[12];
        normalize(frustum, 1);
        frustum[2][0] = clippingMatrix[3] + clippingMatrix[1];
        frustum[2][1] = clippingMatrix[7] + clippingMatrix[5];
        frustum[2][2] = clippingMatrix[11] + clippingMatrix[9];
        frustum[2][3] = clippingMatrix[15] + clippingMatrix[13];
        normalize(frustum, 2);
        frustum[3][0] = clippingMatrix[3] - clippingMatrix[1];
        frustum[3][1] = clippingMatrix[7] - clippingMatrix[5];
        frustum[3][2] = clippingMatrix[11] - clippingMatrix[9];
        frustum[3][3] = clippingMatrix[15] - clippingMatrix[13];
        normalize(frustum, 3);
        frustum[4][0] = clippingMatrix[3] - clippingMatrix[2];
        frustum[4][1] = clippingMatrix[7] - clippingMatrix[6];
        frustum[4][2] = clippingMatrix[11] - clippingMatrix[10];
        frustum[4][3] = clippingMatrix[15] - clippingMatrix[14];
        normalize(frustum, 4);
        frustum[5][0] = clippingMatrix[3] + clippingMatrix[2];
        frustum[5][1] = clippingMatrix[7] + clippingMatrix[6];
        frustum[5][2] = clippingMatrix[11] + clippingMatrix[10];
        frustum[5][3] = clippingMatrix[15] + clippingMatrix[14];
        normalize(frustum, 5);
    }

    private void normalize(float af[][], int i) {
        float f = MathHelper.sqrt(af[i][0] * af[i][0] + af[i][1] * af[i][1] + af[i][2] * af[i][2]);
        af[i][0] /= f;
        af[i][1] /= f;
        af[i][2] /= f;
        af[i][3] /= f;
    }

}