package com.mojang.minecraft.render;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public final class ShapeRenderer {

   private FloatBuffer buffer = BufferUtils.createFloatBuffer(524288);
   private float[] data = new float[524288];
   private int vertices = 0;
   private float u;
   private float v;
   private float r;
   private float g;
   private float b;
   private boolean color = false;
   private boolean texture = false;
   private int vertexLength = 3;
   private int length = 0;
   private boolean noColor = false;
   private int numFloats = 0;
   private int numPoints = 0;
   private float textureX;
   private float textureY;
   public static ShapeRenderer instance = new ShapeRenderer();
   
   public void setColor(long paramLong) {
	    if (!this.noColor) {
	      if (!this.color)
	        this.vertexLength += 4;
	      this.color = true;

	      int j = (int)(paramLong >> 16 & 0xFF);
	      int k = (int)(paramLong >> 8 & 0xFF);
	      int m = (int)(paramLong & 0xFF);

	      this.r = (j / 255.0F);
	      this.g = (k / 255.0F);
	      this.b = (m / 255.0F);
	    }
	  }
   
   public void addBox(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8) {
	    addPoint(paramFloat1, paramFloat2 + paramFloat4, 0.0F, paramFloat5, paramFloat6 + paramFloat8);
	    addPoint(paramFloat1 + paramFloat3, paramFloat2 + paramFloat4, 0.0F, paramFloat5 + paramFloat7, paramFloat6 + paramFloat8);
	    addPoint(paramFloat1 + paramFloat3, paramFloat2, 0.0F, paramFloat5 + paramFloat7, paramFloat6);
	    addPoint(paramFloat1, paramFloat2, 0.0F, paramFloat5, paramFloat6);
	  }

	  public void addPoint(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5) {
	    if (!this.texture) {
	      this.vertexLength += 2;
	    }
	    this.texture = true;

	    this.textureX = paramFloat4;
	    this.textureY = paramFloat5;

	    addPoint(paramFloat1, paramFloat2, paramFloat3);
	  }

	  public void addPoint(float paramFloat1, float paramFloat2, float paramFloat3) {
	    this.data[(this.numFloats++)] = paramFloat1;
	    this.data[(this.numFloats++)] = paramFloat2;
	    this.data[(this.numFloats++)] = paramFloat3;

	    if (this.texture) {
	      this.data[(this.numFloats++)] = this.textureX;
	      this.data[(this.numFloats++)] = this.textureY;
	    }

	    if (this.color) {
	      this.data[(this.numFloats++)] = this.r;
	      this.data[(this.numFloats++)] = this.g;
	      this.data[(this.numFloats++)] = this.b;
	    }

	    this.numPoints += 1;

	    if ((this.numPoints % 4 == 0) && (this.numFloats + (this.vertexLength << 2) >= 524288))
	      end();
	  }


   public final void end() {
      if(this.vertices > 0) {
         this.buffer.clear();
         this.buffer.put(this.data, 0, this.length);
         this.buffer.flip();
         if(this.texture && this.color) {
            GL11.glInterleavedArrays(10794, 0, this.buffer);
         } else if(this.texture) {
            GL11.glInterleavedArrays(10791, 0, this.buffer);
         } else if(this.color) {
            GL11.glInterleavedArrays(10788, 0, this.buffer);
         } else {
            GL11.glInterleavedArrays(10785, 0, this.buffer);
         }

         GL11.glEnableClientState('\u8074');
         if(this.texture) {
            GL11.glEnableClientState('\u8078');
         }

         if(this.color) {
            GL11.glEnableClientState('\u8076');
         }

         GL11.glDrawArrays(7, 0, this.vertices);
         GL11.glDisableClientState('\u8074');
         if(this.texture) {
            GL11.glDisableClientState('\u8078');
         }

         if(this.color) {
            GL11.glDisableClientState('\u8076');
         }
      }

      this.clear();
   }

   private void clear() {
      this.vertices = 0;
      this.buffer.clear();
      this.length = 0;
   }

   public final void begin() {
      this.clear();
      this.color = false;
      this.texture = false;
      this.noColor = false;
   }

   public final void color(float var1, float var2, float var3) {
      if(!this.noColor) {
         if(!this.color) {
            this.vertexLength += 3;
         }

         this.color = true;
         this.r = var1;
         this.g = var2;
         this.b = var3;
      }
   }

   public final void vertexUV(float var1, float var2, float var3, float var4, float var5) {
      if(!this.texture) {
         this.vertexLength += 2;
      }

      this.texture = true;
      this.u = var4;
      this.v = var5;
      this.vertex(var1, var2, var3);
   }

   public final void vertex(float var1, float var2, float var3) {
      if(this.texture) {
         this.data[this.length++] = this.u;
         this.data[this.length++] = this.v;
      }

      if(this.color) {
         this.data[this.length++] = this.r;
         this.data[this.length++] = this.g;
         this.data[this.length++] = this.b;
      }

      this.data[this.length++] = var1;
      this.data[this.length++] = var2;
      this.data[this.length++] = var3;
      ++this.vertices;
      if(this.vertices % 4 == 0 && this.length >= 524288 - (this.vertexLength << 2)) {
         this.end();
      }

   }

   public final void color(int var1) {
      int var2 = var1 >> 16 & 255;
      int var3 = var1 >> 8 & 255;
      var1 &= 255;
      int var10001 = var2;
      int var10002 = var3;
      var3 = var1;
      var2 = var10002;
      var1 = var10001;
      byte var7 = (byte)var1;
      byte var4 = (byte)var2;
      byte var8 = (byte)var3;
      byte var6 = var4;
      byte var5 = var7;
      if(!this.noColor) {
         if(!this.color) {
            this.vertexLength += 3;
         }

         this.color = true;
         this.r = (float)(var5 & 255) / 255.0F;
         this.g = (float)(var6 & 255) / 255.0F;
         this.b = (float)(var8 & 255) / 255.0F;
      }

   }

   public final void noColor() {
      this.noColor = true;
   }

   public final void normal(float var1, float var2, float var3) {
      GL11.glNormal3f(var1, var2, var3);
   }

}
