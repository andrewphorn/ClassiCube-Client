package com.mojang.minecraft.player;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.HackState;
import com.mojang.minecraft.Minecraft;

public class InputHandlerImpl extends InputHandler {
    public static final long serialVersionUID = 0L;
    private boolean[] keylist = new boolean[10];
    private boolean[] keyStates = new boolean[100];

    private transient GameSettings settings;
    private Player player;

    public InputHandlerImpl(GameSettings gameSettings, Player player) {
        settings = gameSettings;
        this.player = player;
    }

    @Override
    public void resetKeys() {
        keyStates = new boolean[100];
        keylist = new boolean[10];
    }

    @Override
    public void setKeyState(int key, boolean state) {
        byte index = -1;
        if (HacksMode == 0 || !(HackState.fly || HackState.speed || HackState.noclip)) {

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
            if (key == settings.flyUp.key) {
                index = 6;
            }
            if (key == settings.flyDown.key) {
                index = 7;
            }
            if (index >= 0) {
                keyStates[index] = state;
            }
        } else {
            if (key == settings.forwardKey.key) {
                keylist[0] = state;
            }
            if (key == settings.leftKey.key) {
                keylist[1] = state;
            }
            if (key == settings.backKey.key) {
                keylist[2] = state;
            }
            if (key == settings.rightKey.key) {
                keylist[3] = state;
            }
            if (key == 57) {
                keylist[4] = state;
            }
            if (key == 16) {
                keylist[5] = state;
            }
            if (key == 18) {
                keylist[6] = state;
            }
            if (key == 42) {
                keylist[7] = state;
            }
            if (key == 29) {
                keylist[8] = state;
            }
            if (key == 45) {
                keylist[9] = state;
            }
            if (key == 67 && state) {
                cliplock = !cliplock;
            }
            if (key == settings.flyKey.key && state) {
                player.flyingMode = !player.flyingMode;
            }
        }
    }

    @Override
    public void updateMovement(int hackMode) {
        HacksMode = hackMode;
        if (settings.minecraft.currentScreen == null) {
            canMove = true;
        } else {
            resetKeys();
            canMove = false;
        }
        if (HacksMode == 0) {
            xxa = 0F;
            yya = 0F;

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
            if (settings.HacksEnabled) {
                if (settings.CanSpeed) {
                    running = keyStates[5];
                    Minecraft.playerIsRunning = keyStates[5];
                }
                flyingUp = keyStates[6];
                flyingDown = keyStates[7];
            }
        } else {
            move = 0F;
            strafe = 0F;
            elevate = 0F;
            if (keylist[0]) {
                move -= 1F;
            }
            if (keylist[1]) {
                strafe -= 1F;
            }
            if (keylist[2]) {
                move += 1F;
            }
            if (keylist[3]) {
                strafe += 1F;
            }
            if (player.flyingMode) {
                if (keylist[5]) {
                    elevate += 0.3F;
                }
                if (keylist[6]) {
                    elevate -= 0.3F;
                }
            }
            mult = 1F;
            if (keylist[7]) {
                mult = 5F;
            } else if (keylist[8]) {
                mult = 2F;
            }

            player.noPhysics = cliplock;
            if (keylist[9]) {
                player.noPhysics = !player.noPhysics;
            }

            jump = keylist[4];
        }
    }
}
