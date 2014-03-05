package com.mojang.minecraft.model;

import org.lwjgl.opengl.GL11;

public final class ModelPart {

	public Vertex[] vertices;
	public TexturedQuad[] quads;
	private int u;
	private int v;
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

	public ModelPart(int var1, int var2) {
		u = var1;
		v = var2;
	}

	public void generateList(float var1) {
		list = GL11.glGenLists(1);
		GL11.glNewList(list, 4864);
		GL11.glBegin(7);

		for (int var2 = 0; var2 < quads.length; ++var2) {
			TexturedQuad var10000 = quads[var2];
			float var3 = var1;
			TexturedQuad var4 = var10000;
			Vec3D var5 = var10000.vertices[1].vector.subtract(var4.vertices[0].vector).normalize();
			Vec3D var6 = var4.vertices[1].vector.subtract(var4.vertices[2].vector).normalize();
			GL11.glNormal3f((var5 = new Vec3D(var5.y * var6.z - var5.z * var6.y, var5.z * var6.x
					- var5.x * var6.z, var5.x * var6.y - var5.y * var6.x).normalize()).x, var5.y,
					var5.z);

			for (int var7 = 0; var7 < 4; ++var7) {
				Vertex var8;
				GL11.glTexCoord2f((var8 = var4.vertices[var7]).u, var8.v);
				GL11.glVertex3f(var8.vector.x * var3, var8.vector.y * var3, var8.vector.z * var3);
			}
		}

		GL11.glEnd();
		GL11.glEndList();
		hasList = true;
	}

	public final void render(float var1) {
		if (render) {
			
			if (!hasList) 
				generateList(var1);
			
			if (allowTransparency) {
								GL11.glEnable(3008);
								GL11.glDisable(GL11.GL_CULL_FACE);
						}
			
			if (pitch == 0.0F && yaw == 0.0F && roll == 0.0F) {
				if (x == 0.0F && y == 0.0F && z == 0.0F) {
					GL11.glCallList(list);
				} else {
					GL11.glTranslatef(x * var1, y * var1, z * var1);
					GL11.glCallList(list);
					GL11.glTranslatef(-x * var1, -y * var1, -z * var1);
				}
			} else {
				GL11.glPushMatrix();
				GL11.glTranslatef(x * var1, y * var1, z * var1);
				if (roll != 0.0F) {
					GL11.glRotatef(roll * 57.295776F, 0.0F, 0.0F, 1.0F);
				}

				if (yaw != 0.0F) {
					GL11.glRotatef(yaw * 57.295776F, 0.0F, 1.0F, 0.0F);
				}

				if (pitch != 0.0F) {
					GL11.glRotatef(pitch * 57.295776F, 1.0F, 0.0F, 0.0F);
				}

				GL11.glCallList(list);
				GL11.glPopMatrix();
			}
			if (allowTransparency){
								GL11.glEnable(GL11.GL_CULL_FACE);
								GL11.glDisable(3008);
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

		Vertex var20 = new Vertex(var1, var2, var3, 0.0F, 0.0F);
		Vertex var11 = new Vertex(var8, var2, var3, 0.0F, 8.0F);
		Vertex var12 = new Vertex(var8, var9, var3, 8.0F, 8.0F);
		Vertex var18 = new Vertex(var1, var9, var3, 8.0F, 0.0F);
		Vertex var13 = new Vertex(var1, var2, var10, 0.0F, 0.0F);
		Vertex var15 = new Vertex(var8, var2, var10, 0.0F, 8.0F);
		Vertex var21 = new Vertex(var8, var9, var10, 8.0F, 8.0F);
		Vertex var14 = new Vertex(var1, var9, var10, 8.0F, 0.0F);
		vertices[0] = var20;
		vertices[1] = var11;
		vertices[2] = var12;
		vertices[3] = var18;
		vertices[4] = var13;
		vertices[5] = var15;
		vertices[6] = var21;
		vertices[7] = var14;
		quads[0] = new TexturedQuad(new Vertex[] { var15, var11, var12, var21 }, u + var6 + var4, v
				+ var6, u + var6 + var4 + var6, v + var6 + var5);
		quads[1] = new TexturedQuad(new Vertex[] { var20, var13, var14, var18 }, u, v + var6, u
				+ var6, v + var6 + var5);
		quads[2] = new TexturedQuad(new Vertex[] { var15, var13, var20, var11 }, u + var6, v, u
				+ var6 + var4, v + var6);
		quads[3] = new TexturedQuad(new Vertex[] { var12, var18, var14, var21 }, u + var6 + var4,
				v, u + var6 + var4 + var4, v + var6);
		quads[4] = new TexturedQuad(new Vertex[] { var11, var20, var18, var12 }, u + var6,
				v + var6, u + var6 + var4, v + var6 + var5);
		quads[5] = new TexturedQuad(new Vertex[] { var13, var15, var21, var14 }, u + var6 + var4
				+ var6, v + var6, u + var6 + var4 + var6 + var4, v + var6 + var5);
		if (mirror) {
			for (int var16 = 0; var16 < quads.length; ++var16) {
				TexturedQuad var17;
				Vertex[] var19 = new Vertex[(var17 = quads[var16]).vertices.length];

				for (var4 = 0; var4 < var17.vertices.length; ++var4) {
					var19[var4] = var17.vertices[var17.vertices.length - var4 - 1];
				}

				var17.vertices = var19;
			}
		}

	}

	public final void setPosition(float var1, float var2, float var3) {
		x = var1;
		y = var2;
		z = var3;
	}
}
