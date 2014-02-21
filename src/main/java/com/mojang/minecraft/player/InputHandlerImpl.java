package com.mojang.minecraft.player;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.HackState;
import com.mojang.minecraft.Minecraft;

public class InputHandlerImpl extends InputHandler {
	private boolean[] keylist = new boolean[10];

	public static final long serialVersionUID = 0L;

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
		keylist = new boolean[11];
	}

	@Override
	public void setKeyState(int key, boolean state) {
		byte index = -1;
		if (HacksMode == 0 || !(HackState.Fly || HackState.Speed || HackState.Noclip)) {

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
                        if (key == settings.toggleTPKey.key) {
                                index = 10;
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
                        if (key == settings.toggleTPKey.key) {
                                keylist[10] = state;
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
			if (settings.HacksEnabled) {
				if (settings.CanSpeed) {
					running = keyStates[5];
					Minecraft.PlayerIsRunning = keyStates[5];
				}
				flyingUp = keyStates[6];
				flyingDown = keyStates[7];
			}
		} else {
			move = 0.0F;
			strafe = 0.0F;
			elevate = 0.0F;
			if (keylist[0] != false) {
				move -= 1.0F;
			}
			if (keylist[1] != false) {
				strafe -= 1.0F;
			}
			if (keylist[2] != false) {
				move += 1.0F;
			}
			if (keylist[3] != false) {
				strafe += 1.0F;
			}
			if (player.flyingMode) {
				if (keylist[5] != false) {
					elevate += 0.3F;
				}
				if (keylist[6] != false) {
					elevate -= 0.3F;
				}
			}
			mult = 1.0F;
			if (keylist[7] != false) {
				mult = 5.0F;
			} else if (keylist[8] != false) {
				mult = 2.0F;
			}

			player.noPhysics = cliplock;
			if (keylist[9] != false) {
				player.noPhysics = !player.noPhysics;
			}

			jump = keylist[4];
		}
	}
}
