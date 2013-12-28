package com.mojang.minecraft.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

public class GLAllocation {
	private static final Map<Integer, Integer> displayLists = new HashMap<Integer, Integer>();
	private static final List<Integer> textures = new ArrayList<Integer>();

	public static synchronized ByteBuffer createDirectByteBuffer(int par0) {
		return ByteBuffer.allocateDirect(par0).order(ByteOrder.nativeOrder());
	}

	public static FloatBuffer createDirectFloatBuffer(int par0) {
		return createDirectByteBuffer(par0 << 2).asFloatBuffer();
	}

	public static IntBuffer createDirectIntBuffer(int par0) {
		return createDirectByteBuffer(par0 << 2).asIntBuffer();
	}

	public static synchronized void deleteDisplayLists(int par0) {
		GL11.glDeleteLists(par0, displayLists.remove(Integer.valueOf(par0)).intValue());
	}

	public static synchronized void deleteTextures() {
		for (int var0 = 0; var0 < textures.size(); ++var0) {
			GL11.glDeleteTextures(((Integer) textures.get(var0)).intValue());
		}

		textures.clear();
	}

	public static synchronized void deleteTexturesAndDisplayLists() {
		Iterator<?> var0 = displayLists.entrySet().iterator();

		while (var0.hasNext()) {
			Entry<?, ?> var1 = (Entry<?, ?>) var0.next();
			GL11.glDeleteLists(((Integer) var1.getKey()).intValue(),
					((Integer) var1.getValue()).intValue());
		}

		displayLists.clear();
		deleteTextures();
	}

	public static synchronized int generateDisplayLists(int par0) {
		int var1 = GL11.glGenLists(par0);
		displayLists.put(Integer.valueOf(var1), Integer.valueOf(par0));
		return var1;
	}
}