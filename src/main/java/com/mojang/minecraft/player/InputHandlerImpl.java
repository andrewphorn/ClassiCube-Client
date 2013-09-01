package com.mojang.minecraft.player;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Minecraft;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputHandlerImpl extends InputHandler {
    public InputHandlerImpl(GameSettings gameSettings) {
	settings = gameSettings;
    }

    @Override
    public void updateMovement() {

    // wow this shouldn't be neccesary.
    if (Mouse.isGrabbed()) {
	    keyStates[0] = Keyboard.isKeyDown(settings.forwardKey.key);
	    keyStates[1] = Keyboard.isKeyDown(settings.backKey.key);
	    keyStates[2] = Keyboard.isKeyDown(settings.leftKey.key);
	    keyStates[3] = Keyboard.isKeyDown(settings.rightKey.key);
	    keyStates[4] = Keyboard.isKeyDown(settings.jumpKey.key);
	    keyStates[5] = Keyboard.isKeyDown(settings.runKey.key);
    	// this also apparently fixes the mouse bug? such confusion.
	}
	xxa = 0.0F;
	yya = 0.0F;

	if (keyStates[0]) {
	    yya--;
	}

	if (keyStates[1]) {
	    yya++;
	}

	if (keyStates[2]) {
	    xxa--;
	}

	if (keyStates[3]) {
	    xxa++;
	}

	jumping = keyStates[4];
	if (settings.CanSpeed) {
	    running = keyStates[5];
	    Minecraft.PlayerIsRunning = keyStates[5];
	}
    }

    @Override
    public void resetKeys() {
	for (int i = 0; i < keyStates.length; ++i) {
	    keyStates[i] = false;
	}

    }

    @Override
    public void setKeyState(int key, boolean state) {
	byte index = -1;

	if (key == settings.forwardKey.key) {
	    index = 0;
	}

	if (key == settings.backKey.key) {
	    index = 1;
	}

	if (key == settings.leftKey.key) {
	    index = 2;
	}

	if (key == settings.rightKey.key) {
	    index = 3;
	}

	if (key == settings.jumpKey.key) {
	    index = 4;
	}
	if (key == settings.runKey.key) {
	    index = 5;
	}
	if (index >= 0) {
	    keyStates[index] = state;
	}

    }

    private boolean[] keyStates = new boolean[100];
    private GameSettings settings;
}
