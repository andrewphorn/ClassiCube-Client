package com.mojang.minecraft.model;

import com.mojang.util.Vertex;

public final class TexturedQuad {

    public Vertex[] vertices;

    private TexturedQuad(Vertex[] vertices) {
        this.vertices = vertices;
    }

    public TexturedQuad(Vertex[] vertices, float var2, float var3, float var4, float var5) {
        this(vertices);
        vertices[0] = vertices[0].create(var4, var3);
        vertices[1] = vertices[1].create(var2, var3);
        vertices[2] = vertices[2].create(var2, var5);
        vertices[3] = vertices[3].create(var4, var5);
    }

    public TexturedQuad(Vertex[] vertices, int var2, int var3, int var4, int var5) {
        this(vertices);
        float var7 = 0.0015625F; // 1 / 640
        float var6 = 0.003125F; // 1 / 320
        vertices[0] = vertices[0].create(var4 / 64F - var7, var3 / 32F + var6);
        vertices[1] = vertices[1].create(var2 / 64F + var7, var3 / 32F + var6);
        vertices[2] = vertices[2].create(var2 / 64F + var7, var5 / 32F - var6);
        vertices[3] = vertices[3].create(var4 / 64F - var7, var5 / 32F - var6);
    }
}
