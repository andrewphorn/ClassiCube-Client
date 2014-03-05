package com.mojang.minecraft.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

public class FBO {
    private IntBuffer texture_index = GLAllocation.createDirectIntBuffer(3);
    private IntBuffer depth_stencil_index = GLAllocation.createDirectIntBuffer(3);
    private IntBuffer fbo_index = GLAllocation.createDirectIntBuffer(3);
    private ByteBuffer texture = GLAllocation.createDirectByteBuffer(16777216);
    private ByteBuffer texture_small = GLAllocation.createDirectByteBuffer(16777216);
    private int sx;
    private int sy;
    private int smx;
    private int smy;
    private int refl_res = 2;
    private int anti_aliasing = 1;
    private int maxResolution;
    private boolean fboarb = false;
    private boolean fbocore = true;

    public FBO(int size_x, int size_y, int aa) {
        anti_aliasing = aa;

        IntBuffer maxsize = GLAllocation.createDirectIntBuffer(16);
        GL11.glGetInteger(3379, maxsize);
        maxResolution = maxsize.get(0);

        if (size_x * anti_aliasing >= maxResolution) {
            anti_aliasing = (int) Math.floor(maxResolution / size_x);
            System.out.println("Anti aliasing reset to " + anti_aliasing);
        }
        if (size_y * anti_aliasing >= maxResolution) {
            anti_aliasing = (int) Math.floor(maxResolution / size_y);
            System.out.println("Anti aliasing reset to " + anti_aliasing);
        }

        size_x *= anti_aliasing;
        size_y *= anti_aliasing;

        sx = size_x;
        sy = size_y;
        smx = size_x / refl_res;
        smy = size_y / refl_res;
        texture = GLAllocation.createDirectByteBuffer(sx * sy * 16);
        texture_small = GLAllocation.createDirectByteBuffer(smx * smy * 16);

        if (fboarb) {
            fbocore = false;
        }

        if (fbocore) {
            initcore();
        } else if (fboarb) {
            initarb();
        } else {
            init();
        }
    }

    public void bind(int index) {
        if (!fboarb) {
            EXTFramebufferObject.glBindFramebufferEXT(36160, fbo_index.get(index));
        } else if (!fbocore) {
            ARBFramebufferObject.glBindFramebuffer(36160, fbo_index.get(index));
        } else {
            GL30.glBindFramebuffer(36160, fbo_index.get(index));
        }
        GL11.glPushAttrib(2048);
        if (index == 2) {
            GL11.glViewport(0, 0, smx, smy);
        } else {
            GL11.glViewport(0, 0, sx, sy);
        }
        GL11.glClear(17664);

        GL11.glPushMatrix();
    }

    public void bind_texture(int index) {
        GL11.glBindTexture(3553, texture_index.get(index));
    }

    private void init() {
        if (!GLContext.getCapabilities().GL_EXT_framebuffer_object) {
            throw new RuntimeException("No FBO Extension supported");
        }

        GL11.glGenTextures(texture_index);

        EXTFramebufferObject.glGenFramebuffersEXT(fbo_index);

        EXTFramebufferObject.glGenRenderbuffersEXT(depth_stencil_index);

        GL11.glBindTexture(3553, texture_index.get(0));
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);

        GL11.glTexParameteri(3553, 10242, 33648);
        GL11.glTexParameteri(3553, 10243, 33648);
        GL11.glTexImage2D(3553, 0, 32849, sx, sy, 0, 6408, 5125, texture);

        EXTFramebufferObject.glBindFramebufferEXT(36160, fbo_index.get(0));

        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, texture_index.get(0), 0);

        EXTFramebufferObject.glBindRenderbufferEXT(36161, depth_stencil_index.get(0));

        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 6402, sx, sy);

        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161,
                depth_stencil_index.get(0));

        GL11.glBindTexture(3553, texture_index.get(1));
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10242, 33648);
        GL11.glTexParameteri(3553, 10243, 33648);
        GL11.glTexImage2D(3553, 0, 32849, sx, sy, 0, 6408, 5125, texture);

        EXTFramebufferObject.glBindFramebufferEXT(36160, fbo_index.get(1));

        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, texture_index.get(1), 0);

        EXTFramebufferObject.glBindRenderbufferEXT(36161, depth_stencil_index.get(1));

        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 6402, sx, sy);

        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161,
                depth_stencil_index.get(1));

        GL11.glBindTexture(3553, texture_index.get(2));
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10242, 33648);
        GL11.glTexParameteri(3553, 10243, 33648);
        GL11.glTexImage2D(3553, 0, 32849, smx, smy, 0, 6408, 5125, texture_small);

        EXTFramebufferObject.glBindFramebufferEXT(36160, fbo_index.get(2));

        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, texture_index.get(2), 0);

        EXTFramebufferObject.glBindRenderbufferEXT(36161, depth_stencil_index.get(2));

        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 6402, smx, smy);

        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161,
                depth_stencil_index.get(2));

        int framebuffer = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
        switch (framebuffer) {
        case 36053:
            break;
        case 36054:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception");
        case 36055:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception");
        case 36057:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception");
        case 36059:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception");
        case 36058:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception");
        case 36060:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception");
        case 36056:
        default:
            throw new RuntimeException("Unexpected reply from glCheckFramebufferStatusEXT: "
                    + framebuffer);
        }

        EXTFramebufferObject.glBindFramebufferEXT(36160, 0);
    }

    private void initarb() {
        if (!GLContext.getCapabilities().GL_ARB_framebuffer_object) {
            System.out.println("Swapping to FBO EXT");

            fboarb = false;
            fbocore = false;

            init();

            return;
        }

        GL11.glGenTextures(texture_index);

        ARBFramebufferObject.glGenFramebuffers(fbo_index);

        ARBFramebufferObject.glGenRenderbuffers(depth_stencil_index);

        GL11.glBindTexture(3553, texture_index.get(0));
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);

        GL11.glTexParameteri(3553, 10242, 33648);
        GL11.glTexParameteri(3553, 10243, 33648);
        GL11.glTexImage2D(3553, 0, 32849, sx, sy, 0, 6408, 5125, texture);

        ARBFramebufferObject.glBindFramebuffer(36160, fbo_index.get(0));

        ARBFramebufferObject.glFramebufferTexture2D(36160, 36064, 3553, texture_index.get(0), 0);

        ARBFramebufferObject.glBindRenderbuffer(36161, depth_stencil_index.get(0));

        ARBFramebufferObject.glRenderbufferStorage(36161, 6402, sx, sy);

        ARBFramebufferObject.glFramebufferRenderbuffer(36160, 36096, 36161,
                depth_stencil_index.get(0));

        GL11.glBindTexture(3553, texture_index.get(1));
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10242, 33648);
        GL11.glTexParameteri(3553, 10243, 33648);
        GL11.glTexImage2D(3553, 0, 32849, sx, sy, 0, 6408, 5125, texture);

        ARBFramebufferObject.glBindFramebuffer(36160, fbo_index.get(1));

        ARBFramebufferObject.glFramebufferTexture2D(36160, 36064, 3553, texture_index.get(1), 0);

        ARBFramebufferObject.glBindRenderbuffer(36161, depth_stencil_index.get(1));

        ARBFramebufferObject.glRenderbufferStorage(36161, 6402, sx, sy);

        ARBFramebufferObject.glFramebufferRenderbuffer(36160, 36096, 36161,
                depth_stencil_index.get(1));

        GL11.glBindTexture(3553, texture_index.get(2));
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10242, 33648);
        GL11.glTexParameteri(3553, 10243, 33648);
        GL11.glTexImage2D(3553, 0, 32849, smx, smy, 0, 6408, 5125, texture_small);

        ARBFramebufferObject.glBindFramebuffer(36160, fbo_index.get(2));

        ARBFramebufferObject.glFramebufferTexture2D(36160, 36064, 3553, texture_index.get(2), 0);

        ARBFramebufferObject.glBindRenderbuffer(36161, depth_stencil_index.get(2));

        ARBFramebufferObject.glRenderbufferStorage(36161, 6402, smx, smy);

        ARBFramebufferObject.glFramebufferRenderbuffer(36160, 36096, 36161,
                depth_stencil_index.get(2));

        int framebuffer = ARBFramebufferObject.glCheckFramebufferStatus(36160);
        switch (framebuffer) {
        case 36053:
            break;
        case 36054:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT exception");
        case 36055:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT exception");
        case 36059:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER exception");
        case 36060:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER exception");
        case 36056:
        case 36057:
        case 36058:
        default:
            throw new RuntimeException("Unexpected reply from glCheckFramebufferStatus: "
                    + framebuffer);
        }

        ARBFramebufferObject.glBindFramebuffer(36160, 0);
    }

    private void initcore() {
        String version = GL11.glGetString(7938);

        if (Integer.valueOf(version.substring(0, 1)).intValue() < 3) {
            System.out.println("Swapping to FBO ARB");

            fboarb = true;
            fbocore = false;

            initarb();

            return;
        }

        GL11.glGenTextures(texture_index);

        GL30.glGenFramebuffers(fbo_index);

        GL30.glGenRenderbuffers(depth_stencil_index);

        GL11.glBindTexture(3553, texture_index.get(0));
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);

        GL11.glTexParameteri(3553, 10242, 33648);
        GL11.glTexParameteri(3553, 10243, 33648);
        GL11.glTexImage2D(3553, 0, 32849, sx, sy, 0, 6408, 5125, texture);

        GL30.glBindFramebuffer(36160, fbo_index.get(0));

        GL30.glFramebufferTexture2D(36160, 36064, 3553, texture_index.get(0), 0);

        GL30.glBindRenderbuffer(36161, depth_stencil_index.get(0));

        GL30.glRenderbufferStorage(36161, 6402, sx, sy);

        GL30.glFramebufferRenderbuffer(36160, 36096, 36161, depth_stencil_index.get(0));

        GL11.glBindTexture(3553, texture_index.get(1));
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10242, 33648);
        GL11.glTexParameteri(3553, 10243, 33648);
        GL11.glTexImage2D(3553, 0, 32849, sx, sy, 0, 6408, 5125, texture);

        GL30.glBindFramebuffer(36160, fbo_index.get(1));

        GL30.glFramebufferTexture2D(36160, 36064, 3553, texture_index.get(1), 0);

        GL30.glBindRenderbuffer(36161, depth_stencil_index.get(1));

        GL30.glRenderbufferStorage(36161, 6402, sx, sy);

        GL30.glFramebufferRenderbuffer(36160, 36096, 36161, depth_stencil_index.get(1));

        GL11.glBindTexture(3553, texture_index.get(2));
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10242, 33648);
        GL11.glTexParameteri(3553, 10243, 33648);
        GL11.glTexImage2D(3553, 0, 32849, smx, smy, 0, 6408, 5125, texture_small);

        GL30.glBindFramebuffer(36160, fbo_index.get(2));

        GL30.glFramebufferTexture2D(36160, 36064, 3553, texture_index.get(2), 0);

        GL30.glBindRenderbuffer(36161, depth_stencil_index.get(2));

        GL30.glRenderbufferStorage(36161, 6402, smx, smy);

        GL30.glFramebufferRenderbuffer(36160, 36096, 36161, depth_stencil_index.get(2));

        int framebuffer = GL30.glCheckFramebufferStatus(36160);
        switch (framebuffer) {
        case 36053:
            break;
        case 36054:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT exception");
        case 36055:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT exception");
        case 36059:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER exception");
        case 36060:
            throw new RuntimeException("FrameBuffer: " + fbo_index
                    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER exception");
        case 36056:
        case 36057:
        case 36058:
        default:
            throw new RuntimeException("Unexpected reply from glCheckFramebufferStatus: "
                    + framebuffer);
        }

        GL30.glBindFramebuffer(36160, 0);
    }

    public void unbind() {
        if (!fboarb) {
            EXTFramebufferObject.glBindFramebufferEXT(36160, 0);
        } else if (!fbocore) {
            ARBFramebufferObject.glBindFramebuffer(36160, 0);
        } else {
            GL30.glBindFramebuffer(36160, 0);
        }
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public void unbind_texture() {
        GL11.glBindTexture(3553, 0);
    }

    public void updateFBOsize(int size_x, int size_y) {
        size_x *= anti_aliasing;
        size_y *= anti_aliasing;

        if (sx != size_x && sy != size_y) {
            if (size_x * anti_aliasing >= maxResolution) {
                anti_aliasing = (int) Math.floor(maxResolution / size_x);
                System.out.println("Anti aliasing reset to " + anti_aliasing);
            }
            if (size_y * anti_aliasing >= maxResolution) {
                anti_aliasing = (int) Math.floor(maxResolution / size_y);
                System.out.println("Anti aliasing reset to " + anti_aliasing);
            }

            sx = size_x;
            sy = size_y;

            smx = size_x / refl_res;
            smy = size_y / refl_res;

            texture = GLAllocation.createDirectByteBuffer(sx * sy * 16);
            texture_small = GLAllocation.createDirectByteBuffer(smx * smy * 16);
            if (fbocore) {
                initcore();
            } else if (fboarb) {
                initarb();
            } else {
                init();
            }
        }
    }
}