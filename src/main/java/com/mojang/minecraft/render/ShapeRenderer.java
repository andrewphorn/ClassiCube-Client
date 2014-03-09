package com.mojang.minecraft.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.ARBBufferObject;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.GL11;

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
        byteBuffer = GLAllocation.createDirectByteBuffer(bufferSize * 4);
        intBuffer = byteBuffer.asIntBuffer();
        floatBuffer = byteBuffer.asFloatBuffer();
        rawBuffer = new int[bufferSize];
        useVBO = false; // GLContext.getCapabilities().GL_ARB_vertex_buffer_object;

        if (useVBO) {
            System.out.println("GPU allows VBOs: Enabling");
            vertexBuffers = GLAllocation.createDirectIntBuffer(vboCount);
            ARBBufferObject.glGenBuffersARB(vertexBuffers);
        }
    }

    public void addTranslation(float xo, float yo, float zo) {
        xOffset += xo;
        yOffset += yo;
        zOffset += zo;
    }

    public void begin() {
        startDrawing(7); // quads
    }

    public void color(float r, float g, float b) {
        setColorOpaque((int) (r * 255F), (int) (g * 255F), (int) (b * 255F));
    }

    public void color(float r, float g, float b, float a) {
        colorClampRGBA((int) (r * 255F), (int) (g * 255F), (int) (b * 255F),
                (int) (a * 255F));
    }

    public void color(int color) {
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        setColorOpaque(r, g, b);
    }

    public void colorClampRGBA(int r, int g, int b, int a) {
        if (!isColorDisabled) {
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

            hasColor = true;

            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                color = a << 24 | b << 16 | g << 8 | r;
            } else {
                color = r << 24 | g << 16 | b << 8 | a;
            }
        }
    }

    public int end() {
        if (!isDrawing) {
            throw new IllegalStateException("Not tesselating!");
        } else {
            isDrawing = false;

            if (vertexCount > 0) {

                intBuffer.clear();
                intBuffer.put(rawBuffer, 0, rawBufferIndex);
                byteBuffer.position(0);
                byteBuffer.limit(rawBufferIndex * 4);

                if (useVBO) {
                    vboIndex = (vboIndex + 1) % vboCount;
                    ARBBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB,
                            vertexBuffers.get(vboIndex));
                    ARBBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB,
                            byteBuffer, ARBBufferObject.GL_STREAM_DRAW_ARB);
                }

                if (hasTexture) {
                    if (useVBO) {
                        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 12L);
                    } else {
                        floatBuffer.position(3);
                        GL11.glTexCoordPointer(2, 32, floatBuffer);
                    }
                    GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (hasColor) {
                    if (useVBO) {
                        GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 32, 20L);
                    } else {
                        byteBuffer.position(20);
                        GL11.glColorPointer(4, true, 32, byteBuffer);
                    }

                    GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (hasNormals) {
                    if (useVBO) {
                        GL11.glNormalPointer(GL11.GL_UNSIGNED_BYTE, 32, 24L);
                    } else {
                        byteBuffer.position(24);
                        GL11.glNormalPointer(32, byteBuffer);
                    }

                    GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                }

                if (useVBO) {
                    GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0L);
                } else {
                    floatBuffer.position(0);
                    GL11.glVertexPointer(3, 32, floatBuffer);
                }

                GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

                if (drawMode == 7 && convertQuadsToTriangles) {
                    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
                } else {
                    GL11.glDrawArrays(drawMode, 0, vertexCount);
                }

                GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);

                if (hasTexture) {
                    GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                }

                if (hasColor) {
                    GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
                }

                if (hasNormals) {
                    GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
                }
            }

            int var1 = rawBufferIndex * 4;
            reset();
            return var1;
        }
    }

    public void noColor() {
        isColorDisabled = true;
    }

    public final void normal(float nx, float ny, float nz) {
        GL11.glNormal3f(nx, ny, nz);
    }

    private void reset() {
        vertexCount = 0;
        byteBuffer.clear();
        rawBufferIndex = 0;
        addedVertices = 0;
    }

    public void setColorOpaque(int r, int g, int b) {
        colorClampRGBA(r, g, b, 255);
    }

    public void setColorRGBA_I(int color, int a) {
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        colorClampRGBA(r, g, b, a);
    }

    public void setTextureUV(double u, double v) {
        hasTexture = true;
        textureU = u;
        textureV = v;
    }

    public void setTranslation(double xOffSet, double yOffSet, double zOffSet) {
        xOffset = xOffSet;
        yOffset = yOffSet;
        zOffset = zOffSet;
    }

    public void startDrawing(int drawMode) {
        if (isDrawing) {
            throw new IllegalStateException("Already tesselating!");
        } else {
            isDrawing = true;
            reset();
            this.drawMode = drawMode;
            hasNormals = false;
            hasColor = false;
            hasTexture = false;
            isColorDisabled = false;
        }
    }

    public void useNormal(float x, float y, float z) {
        hasNormals = true;
        byte nx = (byte) (int) (x * 127F);
        byte ny = (byte) (int) (y * 127F);
        byte nz = (byte) (int) (z * 127F);
        normal = nx & 255 | (ny & 255) << 8 | (nz & 255) << 16;
    }

    public void vertex(double vx, double vy, double vz) {
        ++addedVertices;

        if (drawMode == 7 && convertQuadsToTriangles && addedVertices % 4 == 0) {
            for (int var7 = 0; var7 < 2; ++var7) {
                int var8 = 8 * (3 - var7);

                if (hasTexture) {
                    rawBuffer[rawBufferIndex + 3] = rawBuffer[rawBufferIndex - var8 + 3];
                    rawBuffer[rawBufferIndex + 4] = rawBuffer[rawBufferIndex - var8 + 4];
                }

                if (hasColor) {
                    rawBuffer[rawBufferIndex + 5] = rawBuffer[rawBufferIndex - var8 + 5];
                }

                rawBuffer[rawBufferIndex + 0] = rawBuffer[rawBufferIndex - var8 + 0];
                rawBuffer[rawBufferIndex + 1] = rawBuffer[rawBufferIndex - var8 + 1];
                rawBuffer[rawBufferIndex + 2] = rawBuffer[rawBufferIndex - var8 + 2];
                ++vertexCount;
                rawBufferIndex += 8;
            }
        }

        if (hasTexture) {
            rawBuffer[rawBufferIndex + 3] = Float.floatToRawIntBits((float) textureU);
            rawBuffer[rawBufferIndex + 4] = Float.floatToRawIntBits((float) textureV);
        }

        if (hasColor) {
            rawBuffer[rawBufferIndex + 5] = color;
        }

        if (hasNormals) {
            rawBuffer[rawBufferIndex + 6] = normal;
        }

        rawBuffer[rawBufferIndex + 0] = Float.floatToRawIntBits((float) (vx + xOffset));
        rawBuffer[rawBufferIndex + 1] = Float.floatToRawIntBits((float) (vy + yOffset));
        rawBuffer[rawBufferIndex + 2] = Float.floatToRawIntBits((float) (vz + zOffset));
        rawBufferIndex += 8;
        ++vertexCount;

        if (vertexCount % 4 == 0 && rawBufferIndex >= bufferSize - 32) {
            end();
            isDrawing = true;
        }
    }

    public void vertexUV(double vx, double vy, double vz, double u, double v) {
        setTextureUV(u, v);
        vertex(vx, vy, vz);
    }
}