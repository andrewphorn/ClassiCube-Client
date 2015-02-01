package com.mojang.minecraft.model;

import org.lwjgl.opengl.GL11;

import com.mojang.util.Vector3f;
import com.mojang.util.Vertex;

public final class ModelPart {

    public Vertex[] vertices;
    public TexturedQuad[] quads;
    public float x;
    public float y;
    public float z;
    public float pitch;
    public float yaw;
    public float roll;
    public boolean hasList = false;
    public boolean allowTransparency = true;
    public int list = 0;
    public boolean mirror = false;
    public boolean render = true;
    private final int u;
    private final int v;

    public ModelPart(int var1, int var2) {
        u = var1;
        v = var2;
    }

    public void generateList(float scale) {
        list = GL11.glGenLists(1);
        GL11.glNewList(list, GL11.GL_COMPILE);
        GL11.glBegin(GL11.GL_QUADS);

        for (TexturedQuad quad : quads) {
            Vector3f edge1 = quad.vertices[1].vector.subtract(quad.vertices[0].vector).normalize();
            Vector3f edge2 = quad.vertices[1].vector.subtract(quad.vertices[2].vector).normalize();
            Vector3f normal = new Vector3f(edge1.y * edge2.z - edge1.z * edge2.y,
                    edge1.z * edge2.x - edge1.x * edge2.z,
                    edge1.x * edge2.y - edge1.y * edge2.x).normalize();
            GL11.glNormal3f(normal.x, normal.y, normal.z);

            for (int vIndex = 0; vIndex < 4; ++vIndex) {
                Vertex vertex = quad.vertices[vIndex];
                GL11.glTexCoord2f(vertex.u, vertex.v);
                GL11.glVertex3f(vertex.vector.x * scale, vertex.vector.y * scale, vertex.vector.z * scale);
            }
        }

        GL11.glEnd();
        GL11.glEndList();
        hasList = true;
    }

    public final void render(float scale) {
        if (render) {
            if (!hasList) {
                generateList(scale);
            }

            if (allowTransparency) {
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_CULL_FACE);
            }

            if (pitch == 0F && yaw == 0F && roll == 0F) {
                if (x == 0F && y == 0F && z == 0F) {
                    GL11.glCallList(list);
                } else {
                    GL11.glTranslatef(x * scale, y * scale, z * scale);
                    GL11.glCallList(list);
                    GL11.glTranslatef(-x * scale, -y * scale, -z * scale);
                }
            } else {
                GL11.glPushMatrix();
                GL11.glTranslatef(x * scale, y * scale, z * scale);
                if (roll != 0F) {
                    GL11.glRotatef(roll * (float) (180D / Math.PI), 0F, 0F, 1F);
                }

                if (yaw != 0F) {
                    GL11.glRotatef(yaw * (float) (180D / Math.PI), 0F, 1F, 0F);
                }

                if (pitch != 0F) {
                    GL11.glRotatef(pitch * (float) (180D / Math.PI), 1F, 0F, 0F);
                }

                GL11.glCallList(list);
                GL11.glPopMatrix();
            }
            if (allowTransparency) {
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
            }
        }
    }

    public final void setBounds(float var1, float var2, float var3, int var4, int var5, int var6,
            float var7) {
        vertices = new Vertex[8];
        quads = new TexturedQuad[6];
        float var8 = var1 + var4;
        float var9 = var2 + var5;
        float var10 = var3 + var6;
        var1 -= var7;
        var2 -= var7;
        var3 -= var7;
        var8 += var7;
        var9 += var7;
        var10 += var7;
        if (mirror) {
            var7 = var8;
            var8 = var1;
            var1 = var7;
        }

        Vertex var20 = new Vertex(var1, var2, var3, 0F, 0F);
        Vertex var11 = new Vertex(var8, var2, var3, 0F, 8F);
        Vertex var12 = new Vertex(var8, var9, var3, 8F, 8F);
        Vertex var18 = new Vertex(var1, var9, var3, 8F, 0F);
        Vertex var13 = new Vertex(var1, var2, var10, 0F, 0F);
        Vertex var15 = new Vertex(var8, var2, var10, 0F, 8F);
        Vertex var21 = new Vertex(var8, var9, var10, 8F, 8F);
        Vertex var14 = new Vertex(var1, var9, var10, 8F, 0F);
        vertices[0] = var20;
        vertices[1] = var11;
        vertices[2] = var12;
        vertices[3] = var18;
        vertices[4] = var13;
        vertices[5] = var15;
        vertices[6] = var21;
        vertices[7] = var14;
        quads[0] = new TexturedQuad(new Vertex[]{var15, var11, var12, var21},
                u + var6 + var4, v + var6, u + var6 + var4 + var6, v + var6 + var5);
        quads[1] = new TexturedQuad(new Vertex[]{var20, var13, var14, var18},
                u, v + var6, u + var6, v + var6 + var5);
        quads[2] = new TexturedQuad(new Vertex[]{var15, var13, var20, var11},
                u + var6, v, u + var6 + var4, v + var6);
        quads[3] = new TexturedQuad(new Vertex[]{var12, var18, var14, var21},
                u + var6 + var4, v, u + var6 + var4 + var4, v + var6);
        quads[4] = new TexturedQuad(new Vertex[]{var11, var20, var18, var12},
                u + var6, v + var6, u + var6 + var4, v + var6 + var5);
        quads[5] = new TexturedQuad(new Vertex[]{var13, var15, var21, var14},
                u + var6 + var4 + var6, v + var6, u + var6 + var4 + var6 + var4, v + var6 + var5);
        if (mirror) {
            for (TexturedQuad quad : quads) {
                Vertex[] newVertices = new Vertex[quad.vertices.length];

                for (int vIndex = 0; vIndex < quad.vertices.length; ++vIndex) {
                    newVertices[vIndex] = quad.vertices[quad.vertices.length - vIndex - 1];
                }

                quad.vertices = newVertices;
            }
        }

    }

    public final void setPosition(float newX, float newY, float newZ) {
        x = newX;
        y = newY;
        z = newZ;
    }
}
