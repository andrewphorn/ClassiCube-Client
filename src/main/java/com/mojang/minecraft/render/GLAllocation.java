package com.mojang.minecraft.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

public class GLAllocation {
    private static final Map<Integer, Integer> displayLists = new HashMap<>();
    private static final List<Integer> textures = new ArrayList<>();

    public static synchronized ByteBuffer createDirectByteBuffer(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }

    public static FloatBuffer createDirectFloatBuffer(int size) {
        return createDirectByteBuffer(size << 2).asFloatBuffer();
    }

    public static IntBuffer createDirectIntBuffer(int size) {
        return createDirectByteBuffer(size << 2).asIntBuffer();
    }

    public static synchronized void deleteDisplayLists(int listID) {
        GL11.glDeleteLists(listID, displayLists.remove(listID));
    }

    public static synchronized void deleteTextures() {
    for (int i : textures) {
        GL11.glDeleteTextures(i);
    }

    textures.clear();
}

    public static synchronized void deleteTexturesAndDisplayLists() {
        for (Entry<Integer, Integer> i: displayLists.entrySet()) {
            Entry<Integer, Integer> entity = i;
            GL11.glDeleteLists(entity.getKey(), entity.getValue());
        }

        displayLists.clear();
        deleteTextures();
    }

    public static synchronized int generateDisplayLists(int listID) {
        int listKey = GL11.glGenLists(listID);
        displayLists.put(listKey, listID);
        return listKey;
    }
}