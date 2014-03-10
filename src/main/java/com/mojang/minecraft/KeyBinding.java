package com.mojang.minecraft;

import java.io.Serializable;

/**
 * Holds information about a keybinding
 * 
 */
public class KeyBinding implements Serializable {
    private static final long serialVersionUID = 1L;

    public String name;

    public int key;

    public KeyBinding(String name, int key) {
        this.name = name;
        this.key = key;
    }
}
