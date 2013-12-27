package com.mojang.minecraft.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.ARBBufferObject;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.mojang.minecraft.GameSettings;

public class ShapeRenderer {
	private static boolean convertQuadsToTriangles;
	public static boolean tryVBO;
	private ByteBuffer byteBuffer;
	private IntBuffer intBuffer;
	private FloatBuffer floatBuffer;
	private int[] rawBuffer;
	private int vertexCount;
	private double textureU;
	private double textureV;
	private int color;
	private boolean hasColor;
	private boolean hasTexture;
	private boolean hasNormals;
	private int rawBufferIndex;
	private int addedVertices;
	private boolean isColorDisabled;
	private int drawMode;
	private double xOffset;
	private double yOffset;
	private double zOffset;
	private int normal;
	public static ShapeRenderer instance;
	private boolean isDrawing;
	private boolean useVBO;
	private IntBuffer vertexBuffers;
	private int vboIndex;
	private int vboCount = 10;
	private int bufferSize;

	public ShapeRenderer(int bufferSize, GameSettings gs) {
		this.bufferSize = bufferSize;
		this.byteBuffer = GLAllocation.createDirectByteBuffer(bufferSize * 4);
		this.intBuffer = this.byteBuffer.asIntBuffer();
		this.floatBuffer = this.byteBuffer.asFloatBuffer();
		this.rawBuffer = new int[bufferSize];
		this.useVBO = gs.VBOs && GLContext.getCapabilities().GL_ARB_vertex_buffer_object;

		if (this.useVBO) {
			System.out.println("GPU allows VBOs: Enabling");
			this.vertexBuffers = GLAllocation.createDirectIntBuffer(this.vboCount);
			ARBBufferObject.glGenBuffersARB(this.vertexBuffers);
		}
	}

	public void addTranslation(float xo, float yo, float zo) {
		this.xOffset += xo;
		this.yOffset += yo;
		this.zOffset += zo;
	}

	public void begin() {
		this.startDrawing(7); // quads
	}

	public void color(float r, float g, float b) {
		this.setColorOpaque((int) (r * 255.0F), (int) (g * 255.0F), (int) (b * 255.0F));
	}

	public void color(float r, float g, float b, float a) {
		this.colorClampRGBA((int) (r * 255.0F), (int) (g * 255.0F), (int) (b * 255.0F),
				(int) (a * 255.0F));
	}

	public void color(int color) {
		int r = color >> 16 & 255;
		int g = color >> 8 & 255;
		int b = color & 255;
		this.setColorOpaque(r, g, b);
	}

	public void colorClampRGBA(int r, int g, int b, int a) {
		if (!this.isColorDisabled) {
			if (r > 255) {
				r = 255;
			}

			if (g > 255) {
				g = 255;
			}

			if (b > 255) {
				b = 255;
			}

			if (a > 255) {
				a = 255;
			}

			if (r < 0) {
				r = 0;
			}

			if (g < 0) {
				g = 0;
			}

			if (b < 0) {
				b = 0;
			}

			if (a < 0) {
				a = 0;
			}

			this.hasColor = true;

			if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
				this.color = a << 24 | b << 16 | g << 8 | r;
			} else {
				this.color = r << 24 | g << 16 | b << 8 | a;
			}
		}
	}

	public int end() {
		if (!this.isDrawing) {
			throw new IllegalStateException("Not tesselating!");
		} else {
			this.isDrawing = false;

			if (this.vertexCount > 0) {

				this.intBuffer.clear();
				this.intBuffer.put(this.rawBuffer, 0, this.rawBufferIndex);
				this.byteBuffer.position(0);
				this.byteBuffer.limit(this.rawBufferIndex * 4);

				if (this.useVBO) {
					this.vboIndex = (this.vboIndex + 1) % this.vboCount;
					ARBBufferObject.glBindBufferARB(
							ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB,
							this.vertexBuffers.get(this.vboIndex));
					ARBBufferObject.glBufferDataARB(
							ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, this.byteBuffer,
							ARBBufferObject.GL_STREAM_DRAW_ARB);
				}

				if (this.hasTexture) {
					if (this.useVBO) {
						GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 12L);
					} else {
						this.floatBuffer.position(3);
						GL11.glTexCoordPointer(2, 32, this.floatBuffer);
					}
					GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
				}

				if (this.hasColor) {
					if (this.useVBO) {
						GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 32, 20L);
					} else {
						this.byteBuffer.position(20);
						GL11.glColorPointer(4, true, 32, this.byteBuffer);
					}

					GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
				}

				if (this.hasNormals) {
					if (this.useVBO) {
						GL11.glNormalPointer(GL11.GL_UNSIGNED_BYTE, 32, 24L);
					} else {
						this.byteBuffer.position(24);
						GL11.glNormalPointer(32, this.byteBuffer);
					}

					GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
				}

				if (this.useVBO) {
					GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0L);
				} else {
					this.floatBuffer.position(0);
					GL11.glVertexPointer(3, 32, this.floatBuffer);
				}

				GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

				if (this.drawMode == 7 && convertQuadsToTriangles) {
					GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.vertexCount);
				} else {
					GL11.glDrawArrays(this.drawMode, 0, this.vertexCount);
				}

				GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

				if (this.hasTexture) {
					GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
				}

				if (this.hasColor) {
					GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
				}

				if (this.hasNormals) {
					GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
				}
			}

			int var1 = this.rawBufferIndex * 4;
			this.reset();
			return var1;
		}
	}

	public void noColor() {
		this.isColorDisabled = true;
	}

	public final void normal(float nx, float ny, float nz) {
		GL11.glNormal3f(nx, ny, nz);
	}

	private void reset() {
		this.vertexCount = 0;
		this.byteBuffer.clear();
		this.rawBufferIndex = 0;
		this.addedVertices = 0;
	}

	public void setColorOpaque(int r, int g, int b) {
		this.colorClampRGBA(r, g, b, 255);
	}

	public void setColorRGBA_I(int color, int a) {
		int r = color >> 16 & 255;
		int g = color >> 8 & 255;
		int b = color & 255;
		this.colorClampRGBA(r, g, b, a);
	}

	public void setTextureUV(double u, double v) {
		this.hasTexture = true;
		this.textureU = u;
		this.textureV = v;
	}

	public void setTranslation(double xOffSet, double yOffSet, double zOffSet) {
		this.xOffset = xOffSet;
		this.yOffset = yOffSet;
		this.zOffset = zOffSet;
	}

	public void startDrawing(int drawMode) {
		if (this.isDrawing) {
			throw new IllegalStateException("Already tesselating!");
		} else {
			this.isDrawing = true;
			this.reset();
			this.drawMode = drawMode;
			this.hasNormals = false;
			this.hasColor = false;
			this.hasTexture = false;
			this.isColorDisabled = false;
		}
	}

	public void useNormal(float x, float y, float z) {
		this.hasNormals = true;
		byte nx = (byte) ((int) (x * 127.0F));
		byte ny = (byte) ((int) (y * 127.0F));
		byte nz = (byte) ((int) (z * 127.0F));
		this.normal = nx & 255 | (ny & 255) << 8 | (nz & 255) << 16;
	}

	public void vertex(double vx, double vy, double vz) {
		++this.addedVertices;

		if (this.drawMode == 7 && convertQuadsToTriangles && this.addedVertices % 4 == 0) {
			for (int var7 = 0; var7 < 2; ++var7) {
				int var8 = 8 * (3 - var7);

				if (this.hasTexture) {
					this.rawBuffer[this.rawBufferIndex + 3] = this.rawBuffer[this.rawBufferIndex
							- var8 + 3];
					this.rawBuffer[this.rawBufferIndex + 4] = this.rawBuffer[this.rawBufferIndex
							- var8 + 4];
				}

				if (this.hasColor) {
					this.rawBuffer[this.rawBufferIndex + 5] = this.rawBuffer[this.rawBufferIndex
							- var8 + 5];
				}

				this.rawBuffer[this.rawBufferIndex + 0] = this.rawBuffer[this.rawBufferIndex - var8
						+ 0];
				this.rawBuffer[this.rawBufferIndex + 1] = this.rawBuffer[this.rawBufferIndex - var8
						+ 1];
				this.rawBuffer[this.rawBufferIndex + 2] = this.rawBuffer[this.rawBufferIndex - var8
						+ 2];
				++this.vertexCount;
				this.rawBufferIndex += 8;
			}
		}

		if (this.hasTexture) {
			this.rawBuffer[this.rawBufferIndex + 3] = Float
					.floatToRawIntBits((float) this.textureU);
			this.rawBuffer[this.rawBufferIndex + 4] = Float
					.floatToRawIntBits((float) this.textureV);
		}

		if (this.hasColor) {
			this.rawBuffer[this.rawBufferIndex + 5] = this.color;
		}

		if (this.hasNormals) {
			this.rawBuffer[this.rawBufferIndex + 6] = this.normal;
		}

		this.rawBuffer[this.rawBufferIndex + 0] = Float
				.floatToRawIntBits((float) (vx + this.xOffset));
		this.rawBuffer[this.rawBufferIndex + 1] = Float
				.floatToRawIntBits((float) (vy + this.yOffset));
		this.rawBuffer[this.rawBufferIndex + 2] = Float
				.floatToRawIntBits((float) (vz + this.zOffset));
		this.rawBufferIndex += 8;
		++this.vertexCount;

		if (this.vertexCount % 4 == 0 && this.rawBufferIndex >= this.bufferSize - 32) {
			this.end();
			this.isDrawing = true;
		}
	}

	public void vertexUV(double vx, double vy, double vz, double u, double v) {
		this.setTextureUV(u, v);
		this.vertex(vx, vy, vz);
	}
}