package com.mojang.minecraft.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

public class FBO {
    private IntBuffer texture_index = BufferUtils.createIntBuffer(3);
    private IntBuffer depth_stencil_index = BufferUtils.createIntBuffer(3);
    private IntBuffer fbo_index = BufferUtils.createIntBuffer(3);
    private ByteBuffer texture = BufferUtils.createByteBuffer(16777216);
    private ByteBuffer texture_small = BufferUtils.createByteBuffer(16777216);
    private int sx;
    private int sy;
    private int smx;
    private int smy;
    private int refl_res = 2;
    private int anti_aliasing = 1;
    private int maxResolution;
    private boolean fboarb = false;
    private boolean fbocore = true;

    public FBO(int size_x, int size_y, int reflection_resolution, int aa) {
	this.refl_res = reflection_resolution;
	this.anti_aliasing = aa;

	IntBuffer maxsize = BufferUtils.createIntBuffer(16);
	GL11.glGetInteger(3379, maxsize);
	this.maxResolution = maxsize.get(0);

	if (size_x * this.anti_aliasing >= this.maxResolution) {
	    this.anti_aliasing = ((int) Math.floor(this.maxResolution / size_x));
	    System.out.println("Anti aliasing reset to " + this.anti_aliasing);
	}
	if (size_y * this.anti_aliasing >= this.maxResolution) {
	    this.anti_aliasing = ((int) Math.floor(this.maxResolution / size_y));
	    System.out.println("Anti aliasing reset to " + this.anti_aliasing);
	}

	size_x *= this.anti_aliasing;
	size_y *= this.anti_aliasing;

	this.sx = size_x;
	this.sy = size_y;
	this.smx = (size_x / this.refl_res);
	this.smy = (size_y / this.refl_res);
	this.texture = BufferUtils.createByteBuffer(this.sx * this.sy * 16);
	this.texture_small = BufferUtils.createByteBuffer(this.smx * this.smy * 16);

	if (this.fboarb)
	    this.fbocore = false;

	if (this.fbocore)
	    initcore();
	else if (this.fboarb)
	    initarb();
	else
	    init();
    }

    public void updateFBOsize(int size_x, int size_y) {
	size_x *= this.anti_aliasing;
	size_y *= this.anti_aliasing;

	if ((this.sx != size_x) && (this.sy != size_y)) {
	    if (size_x * this.anti_aliasing >= this.maxResolution) {
		this.anti_aliasing = ((int) Math.floor(this.maxResolution
			/ size_x));
		System.out.println("Anti aliasing reset to "
			+ this.anti_aliasing);
	    }
	    if (size_y * this.anti_aliasing >= this.maxResolution) {
		this.anti_aliasing = ((int) Math.floor(this.maxResolution
			/ size_y));
		System.out.println("Anti aliasing reset to "
			+ this.anti_aliasing);
	    }

	    this.sx = size_x;
	    this.sy = size_y;

	    this.smx = (size_x / this.refl_res);
	    this.smy = (size_y / this.refl_res);

	    this.texture = BufferUtils.createByteBuffer(this.sx * this.sy * 16);
	    this.texture_small =  BufferUtils.createByteBuffer(this.smx * this.smy * 16);
	    if (this.fbocore)
		initcore();
	    else if (this.fboarb)
		initarb();
	    else
		init();
	}
    }

    private void initarb() {
	if (!GLContext.getCapabilities().GL_ARB_framebuffer_object) {
	    System.out.println("Swapping to FBO EXT");

	    this.fboarb = false;
	    this.fbocore = false;

	    init();

	    return;
	}

	GL11.glGenTextures(this.texture_index);

	ARBFramebufferObject.glGenFramebuffers(this.fbo_index);

	ARBFramebufferObject.glGenRenderbuffers(this.depth_stencil_index);

	GL11.glBindTexture(3553, this.texture_index.get(0));
	GL11.glTexParameteri(3553, 10241, 9729);
	GL11.glTexParameteri(3553, 10240, 9729);

	GL11.glTexParameteri(3553, 10242, 33648);
	GL11.glTexParameteri(3553, 10243, 33648);
	GL11.glTexImage2D(3553, 0, 32849, this.sx, this.sy, 0, 6408, 5125,
		this.texture);

	ARBFramebufferObject.glBindFramebuffer(36160, this.fbo_index.get(0));

	ARBFramebufferObject.glFramebufferTexture2D(36160, 36064, 3553,
		this.texture_index.get(0), 0);

	ARBFramebufferObject.glBindRenderbuffer(36161,
		this.depth_stencil_index.get(0));

	ARBFramebufferObject.glRenderbufferStorage(36161, 6402, this.sx,
		this.sy);

	ARBFramebufferObject.glFramebufferRenderbuffer(36160, 36096, 36161,
		this.depth_stencil_index.get(0));

	GL11.glBindTexture(3553, this.texture_index.get(1));
	GL11.glTexParameteri(3553, 10241, 9729);
	GL11.glTexParameteri(3553, 10240, 9729);
	GL11.glTexParameteri(3553, 10242, 33648);
	GL11.glTexParameteri(3553, 10243, 33648);
	GL11.glTexImage2D(3553, 0, 32849, this.sx, this.sy, 0, 6408, 5125,
		this.texture);

	ARBFramebufferObject.glBindFramebuffer(36160, this.fbo_index.get(1));

	ARBFramebufferObject.glFramebufferTexture2D(36160, 36064, 3553,
		this.texture_index.get(1), 0);

	ARBFramebufferObject.glBindRenderbuffer(36161,
		this.depth_stencil_index.get(1));

	ARBFramebufferObject.glRenderbufferStorage(36161, 6402, this.sx,
		this.sy);

	ARBFramebufferObject.glFramebufferRenderbuffer(36160, 36096, 36161,
		this.depth_stencil_index.get(1));

	GL11.glBindTexture(3553, this.texture_index.get(2));
	GL11.glTexParameteri(3553, 10241, 9729);
	GL11.glTexParameteri(3553, 10240, 9729);
	GL11.glTexParameteri(3553, 10242, 33648);
	GL11.glTexParameteri(3553, 10243, 33648);
	GL11.glTexImage2D(3553, 0, 32849, this.smx, this.smy, 0, 6408, 5125,
		this.texture_small);

	ARBFramebufferObject.glBindFramebuffer(36160, this.fbo_index.get(2));

	ARBFramebufferObject.glFramebufferTexture2D(36160, 36064, 3553,
		this.texture_index.get(2), 0);

	ARBFramebufferObject.glBindRenderbuffer(36161,
		this.depth_stencil_index.get(2));

	ARBFramebufferObject.glRenderbufferStorage(36161, 6402, this.smx,
		this.smy);

	ARBFramebufferObject.glFramebufferRenderbuffer(36160, 36096, 36161,
		this.depth_stencil_index.get(2));

	int framebuffer = ARBFramebufferObject.glCheckFramebufferStatus(36160);
	switch (framebuffer) {
	case 36053:
	    break;
	case 36054:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT exception");
	case 36055:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT exception");
	case 36059:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER exception");
	case 36060:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER exception");
	case 36056:
	case 36057:
	case 36058:
	default:
	    throw new RuntimeException(
		    "Unexpected reply from glCheckFramebufferStatus: "
			    + framebuffer);
	}

	ARBFramebufferObject.glBindFramebuffer(36160, 0);
    }

    private void initcore() {
	String version = GL11.glGetString(7938);

	if (Integer.valueOf(version.substring(0, 1)).intValue() < 3) {
	    System.out.println("Swapping to FBO ARB");

	    this.fboarb = true;
	    this.fbocore = false;

	    initarb();

	    return;
	}

	GL11.glGenTextures(this.texture_index);

	GL30.glGenFramebuffers(this.fbo_index);

	GL30.glGenRenderbuffers(this.depth_stencil_index);

	GL11.glBindTexture(3553, this.texture_index.get(0));
	GL11.glTexParameteri(3553, 10241, 9729);
	GL11.glTexParameteri(3553, 10240, 9729);

	GL11.glTexParameteri(3553, 10242, 33648);
	GL11.glTexParameteri(3553, 10243, 33648);
	GL11.glTexImage2D(3553, 0, 32849, this.sx, this.sy, 0, 6408, 5125,
		this.texture);

	GL30.glBindFramebuffer(36160, this.fbo_index.get(0));

	GL30.glFramebufferTexture2D(36160, 36064, 3553,
		this.texture_index.get(0), 0);

	GL30.glBindRenderbuffer(36161, this.depth_stencil_index.get(0));

	GL30.glRenderbufferStorage(36161, 6402, this.sx, this.sy);

	GL30.glFramebufferRenderbuffer(36160, 36096, 36161,
		this.depth_stencil_index.get(0));

	GL11.glBindTexture(3553, this.texture_index.get(1));
	GL11.glTexParameteri(3553, 10241, 9729);
	GL11.glTexParameteri(3553, 10240, 9729);
	GL11.glTexParameteri(3553, 10242, 33648);
	GL11.glTexParameteri(3553, 10243, 33648);
	GL11.glTexImage2D(3553, 0, 32849, this.sx, this.sy, 0, 6408, 5125,
		this.texture);

	GL30.glBindFramebuffer(36160, this.fbo_index.get(1));

	GL30.glFramebufferTexture2D(36160, 36064, 3553,
		this.texture_index.get(1), 0);

	GL30.glBindRenderbuffer(36161, this.depth_stencil_index.get(1));

	GL30.glRenderbufferStorage(36161, 6402, this.sx, this.sy);

	GL30.glFramebufferRenderbuffer(36160, 36096, 36161,
		this.depth_stencil_index.get(1));

	GL11.glBindTexture(3553, this.texture_index.get(2));
	GL11.glTexParameteri(3553, 10241, 9729);
	GL11.glTexParameteri(3553, 10240, 9729);
	GL11.glTexParameteri(3553, 10242, 33648);
	GL11.glTexParameteri(3553, 10243, 33648);
	GL11.glTexImage2D(3553, 0, 32849, this.smx, this.smy, 0, 6408, 5125,
		this.texture_small);

	GL30.glBindFramebuffer(36160, this.fbo_index.get(2));

	GL30.glFramebufferTexture2D(36160, 36064, 3553,
		this.texture_index.get(2), 0);

	GL30.glBindRenderbuffer(36161, this.depth_stencil_index.get(2));

	GL30.glRenderbufferStorage(36161, 6402, this.smx, this.smy);

	GL30.glFramebufferRenderbuffer(36160, 36096, 36161,
		this.depth_stencil_index.get(2));

	int framebuffer = GL30.glCheckFramebufferStatus(36160);
	switch (framebuffer) {
	case 36053:
	    break;
	case 36054:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT exception");
	case 36055:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT exception");
	case 36059:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER exception");
	case 36060:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER exception");
	case 36056:
	case 36057:
	case 36058:
	default:
	    throw new RuntimeException(
		    "Unexpected reply from glCheckFramebufferStatus: "
			    + framebuffer);
	}

	GL30.glBindFramebuffer(36160, 0);
    }

    private void init() {
	if (!GLContext.getCapabilities().GL_EXT_framebuffer_object) {
	    throw new RuntimeException("No FBO Extension supported");
	}

	GL11.glGenTextures(this.texture_index);

	EXTFramebufferObject.glGenFramebuffersEXT(this.fbo_index);

	EXTFramebufferObject.glGenRenderbuffersEXT(this.depth_stencil_index);

	GL11.glBindTexture(3553, this.texture_index.get(0));
	GL11.glTexParameteri(3553, 10241, 9729);
	GL11.glTexParameteri(3553, 10240, 9729);

	GL11.glTexParameteri(3553, 10242, 33648);
	GL11.glTexParameteri(3553, 10243, 33648);
	GL11.glTexImage2D(3553, 0, 32849, this.sx, this.sy, 0, 6408, 5125,
		this.texture);

	EXTFramebufferObject.glBindFramebufferEXT(36160, this.fbo_index.get(0));

	EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553,
		this.texture_index.get(0), 0);

	EXTFramebufferObject.glBindRenderbufferEXT(36161,
		this.depth_stencil_index.get(0));

	EXTFramebufferObject.glRenderbufferStorageEXT(36161, 6402, this.sx,
		this.sy);

	EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161,
		this.depth_stencil_index.get(0));

	GL11.glBindTexture(3553, this.texture_index.get(1));
	GL11.glTexParameteri(3553, 10241, 9729);
	GL11.glTexParameteri(3553, 10240, 9729);
	GL11.glTexParameteri(3553, 10242, 33648);
	GL11.glTexParameteri(3553, 10243, 33648);
	GL11.glTexImage2D(3553, 0, 32849, this.sx, this.sy, 0, 6408, 5125,
		this.texture);

	EXTFramebufferObject.glBindFramebufferEXT(36160, this.fbo_index.get(1));

	EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553,
		this.texture_index.get(1), 0);

	EXTFramebufferObject.glBindRenderbufferEXT(36161,
		this.depth_stencil_index.get(1));

	EXTFramebufferObject.glRenderbufferStorageEXT(36161, 6402, this.sx,
		this.sy);

	EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161,
		this.depth_stencil_index.get(1));

	GL11.glBindTexture(3553, this.texture_index.get(2));
	GL11.glTexParameteri(3553, 10241, 9729);
	GL11.glTexParameteri(3553, 10240, 9729);
	GL11.glTexParameteri(3553, 10242, 33648);
	GL11.glTexParameteri(3553, 10243, 33648);
	GL11.glTexImage2D(3553, 0, 32849, this.smx, this.smy, 0, 6408, 5125,
		this.texture_small);

	EXTFramebufferObject.glBindFramebufferEXT(36160, this.fbo_index.get(2));

	EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553,
		this.texture_index.get(2), 0);

	EXTFramebufferObject.glBindRenderbufferEXT(36161,
		this.depth_stencil_index.get(2));

	EXTFramebufferObject.glRenderbufferStorageEXT(36161, 6402, this.smx,
		this.smy);

	EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161,
		this.depth_stencil_index.get(2));

	int framebuffer = EXTFramebufferObject
		.glCheckFramebufferStatusEXT(36160);
	switch (framebuffer) {
	case 36053:
	    break;
	case 36054:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception");
	case 36055:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception");
	case 36057:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception");
	case 36059:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception");
	case 36058:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception");
	case 36060:
	    throw new RuntimeException(
		    "FrameBuffer: "
			    + this.fbo_index
			    + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception");
	case 36056:
	default:
	    throw new RuntimeException(
		    "Unexpected reply from glCheckFramebufferStatusEXT: "
			    + framebuffer);
	}

	EXTFramebufferObject.glBindFramebufferEXT(36160, 0);
    }

    public void bind(int index) {
	if (!this.fboarb)
	    EXTFramebufferObject.glBindFramebufferEXT(36160,
		    this.fbo_index.get(index));
	else if (!this.fbocore)
	    ARBFramebufferObject.glBindFramebuffer(36160,
		    this.fbo_index.get(index));
	else {
	    GL30.glBindFramebuffer(36160, this.fbo_index.get(index));
	}
	GL11.glPushAttrib(2048);
	if (index == 2)
	    GL11.glViewport(0, 0, this.smx, this.smy);
	else
	    GL11.glViewport(0, 0, this.sx, this.sy);
	GL11.glClear(17664);

	GL11.glPushMatrix();
    }

    public void unbind() {
	if (!this.fboarb)
	    EXTFramebufferObject.glBindFramebufferEXT(36160, 0);
	else if (!this.fbocore)
	    ARBFramebufferObject.glBindFramebuffer(36160, 0);
	else {
	    GL30.glBindFramebuffer(36160, 0);
	}
	GL11.glPopMatrix();
	GL11.glPopAttrib();
    }

    public void bind_texture(int index) {
	GL11.glBindTexture(3553, this.texture_index.get(index));
    }

    public void unbind_texture() {
	GL11.glBindTexture(3553, 0);
    }
}