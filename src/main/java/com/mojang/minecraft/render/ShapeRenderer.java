package com.mojang.minecraft.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
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
    private ShortBuffer shortBuffer;
    private int[] rawBuffer;
    private int vertexCount;
    private double textureU;
    private double textureV;
    private int brightness;
    private int color;
    private boolean hasColor;
    private boolean hasTexture;
    private boolean hasBrightness;
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

    public ShapeRenderer(int var1, GameSettings gs) {
	this.bufferSize = var1;
	this.byteBuffer = GLAllocation.createDirectByteBuffer(var1 * 4);
	this.intBuffer = this.byteBuffer.asIntBuffer();
	this.floatBuffer = this.byteBuffer.asFloatBuffer();
	this.shortBuffer = this.byteBuffer.asShortBuffer();
	this.rawBuffer = new int[var1];
	this.useVBO = gs.VBOs
		&& GLContext.getCapabilities().GL_ARB_vertex_buffer_object;

	if (this.useVBO) {
	    System.out.println("GPU allows VBOs: Enabling");
	    this.vertexBuffers = GLAllocation
		    .createDirectIntBuffer(this.vboCount);
	    ARBVertexBufferObject.glGenBuffersARB(this.vertexBuffers);
	}
    }

    public void addTranslation(float var1, float var2, float var3) {
	this.xOffset += (double) var1;
	this.yOffset += (double) var2;
	this.zOffset += (double) var3;
    }

    public void begin() {
	this.startDrawing(7);
    }

    public void color(float var1, float var2, float var3) {
	this.setColorOpaque((int) (var1 * 255.0F), (int) (var2 * 255.0F),
		(int) (var3 * 255.0F));
    }

    public void color(float var1, float var2, float var3, float var4) {
	this.colorClampRGBA((int) (var1 * 255.0F), (int) (var2 * 255.0F),
		(int) (var3 * 255.0F), (int) (var4 * 255.0F));
    }

    public void color(int var1) {
	int var2 = var1 >> 16 & 255;
	int var3 = var1 >> 8 & 255;
	int var4 = var1 & 255;
	this.setColorOpaque(var2, var3, var4);
    }

    public void colorClampRGBA(int var1, int var2, int var3, int var4) {
	if (!this.isColorDisabled) {
	    if (var1 > 255) {
		var1 = 255;
	    }

	    if (var2 > 255) {
		var2 = 255;
	    }

	    if (var3 > 255) {
		var3 = 255;
	    }

	    if (var4 > 255) {
		var4 = 255;
	    }

	    if (var1 < 0) {
		var1 = 0;
	    }

	    if (var2 < 0) {
		var2 = 0;
	    }

	    if (var3 < 0) {
		var3 = 0;
	    }

	    if (var4 < 0) {
		var4 = 0;
	    }

	    this.hasColor = true;

	    if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
		this.color = var4 << 24 | var3 << 16 | var2 << 8 | var1;
	    } else {
		this.color = var1 << 24 | var2 << 16 | var3 << 8 | var4;
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
		    ARBVertexBufferObject.glBindBufferARB(
			    ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB,
			    this.vertexBuffers.get(this.vboIndex));
		    ARBVertexBufferObject.glBufferDataARB(
			    ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB,
			    this.byteBuffer,
			    ARBVertexBufferObject.GL_STREAM_DRAW_ARB);
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

		if (this.hasBrightness) {
		    OpenGlHelper
			    .setClientActiveTexture(OpenGlHelper.lightmapTexUnit);

		    if (this.useVBO) {
			GL11.glTexCoordPointer(2, GL11.GL_SHORT, 32, 28L);
		    } else {
			this.shortBuffer.position(14);
			GL11.glTexCoordPointer(2, 32, this.shortBuffer);
		    }

		    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		    OpenGlHelper
			    .setClientActiveTexture(OpenGlHelper.defaultTexUnit);
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

		if (this.hasBrightness) {
		    OpenGlHelper
			    .setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
		    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		    OpenGlHelper
			    .setClientActiveTexture(OpenGlHelper.defaultTexUnit);
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

    public final void normal(float var1, float var2, float var3) {
	GL11.glNormal3f(var1, var2, var3);
    }

    private void reset() {
	this.vertexCount = 0;
	this.byteBuffer.clear();
	this.rawBufferIndex = 0;
	this.addedVertices = 0;
    }

    public void setBrightness(int var1) {
	this.hasBrightness = true;
	this.brightness = var1;
    }

    public void setColorOpaque(int var1, int var2, int var3) {
	this.colorClampRGBA(var1, var2, var3, 255);
    }

    public void setColorRGBA_I(int var1, int var2) {
	int var3 = var1 >> 16 & 255;
	int var4 = var1 >> 8 & 255;
	int var5 = var1 & 255;
	this.colorClampRGBA(var3, var4, var5, var2);
    }

    public void setTextureUV(double var1, double var3) {
	this.hasTexture = true;
	this.textureU = var1;
	this.textureV = var3;
    }

    public void setTranslation(double var1, double var3, double var5) {
	this.xOffset = var1;
	this.yOffset = var3;
	this.zOffset = var5;
    }

    public void startDrawing(int var1) {
	if (this.isDrawing) {
	    throw new IllegalStateException("Already tesselating!");
	} else {
	    this.isDrawing = true;
	    this.reset();
	    this.drawMode = var1;
	    this.hasNormals = false;
	    this.hasColor = false;
	    this.hasTexture = false;
	    this.hasBrightness = false;
	    this.isColorDisabled = false;
	}
    }

    public void useNormal(float var1, float var2, float var3) {
	this.hasNormals = true;
	byte var4 = (byte) ((int) (var1 * 127.0F));
	byte var5 = (byte) ((int) (var2 * 127.0F));
	byte var6 = (byte) ((int) (var3 * 127.0F));
	this.normal = var4 & 255 | (var5 & 255) << 8 | (var6 & 255) << 16;
    }

    public void vertex(double var1, double var3, double var5) {
	++this.addedVertices;

	if (this.drawMode == 7 && convertQuadsToTriangles
		&& this.addedVertices % 4 == 0) {
	    for (int var7 = 0; var7 < 2; ++var7) {
		int var8 = 8 * (3 - var7);

		if (this.hasTexture) {
		    this.rawBuffer[this.rawBufferIndex + 3] = this.rawBuffer[this.rawBufferIndex
			    - var8 + 3];
		    this.rawBuffer[this.rawBufferIndex + 4] = this.rawBuffer[this.rawBufferIndex
			    - var8 + 4];
		}

		if (this.hasBrightness) {
		    this.rawBuffer[this.rawBufferIndex + 7] = this.rawBuffer[this.rawBufferIndex
			    - var8 + 7];
		}

		if (this.hasColor) {
		    this.rawBuffer[this.rawBufferIndex + 5] = this.rawBuffer[this.rawBufferIndex
			    - var8 + 5];
		}

		this.rawBuffer[this.rawBufferIndex + 0] = this.rawBuffer[this.rawBufferIndex
			- var8 + 0];
		this.rawBuffer[this.rawBufferIndex + 1] = this.rawBuffer[this.rawBufferIndex
			- var8 + 1];
		this.rawBuffer[this.rawBufferIndex + 2] = this.rawBuffer[this.rawBufferIndex
			- var8 + 2];
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

	if (this.hasBrightness) {
	    this.rawBuffer[this.rawBufferIndex + 7] = this.brightness;
	}

	if (this.hasColor) {
	    this.rawBuffer[this.rawBufferIndex + 5] = this.color;
	}

	if (this.hasNormals) {
	    this.rawBuffer[this.rawBufferIndex + 6] = this.normal;
	}

	this.rawBuffer[this.rawBufferIndex + 0] = Float
		.floatToRawIntBits((float) (var1 + this.xOffset));
	this.rawBuffer[this.rawBufferIndex + 1] = Float
		.floatToRawIntBits((float) (var3 + this.yOffset));
	this.rawBuffer[this.rawBufferIndex + 2] = Float
		.floatToRawIntBits((float) (var5 + this.zOffset));
	this.rawBufferIndex += 8;
	++this.vertexCount;

	if (this.vertexCount % 4 == 0
		&& this.rawBufferIndex >= this.bufferSize - 32) {
	    this.end();
	    this.isDrawing = true;
	}
    }

    public void vertexUV(double var1, double var3, double var5, double var7,
	    double var9) {
	this.setTextureUV(var7, var9);
	this.vertex(var1, var3, var5);
    }

}