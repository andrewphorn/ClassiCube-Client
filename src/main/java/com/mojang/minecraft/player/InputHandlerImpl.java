package com.mojang.minecraft.player;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.HackState;
import com.mojang.minecraft.Minecraft;

public class InputHandlerImpl extends InputHandler {
	private boolean[] keylist = new boolean[10];

	public static final long serialVersionUID = 0L;

	private boolean[] keyStates = new boolean[100];

	private transient GameSettings settings;

	public InputHandlerImpl(GameSettings gameSettings) {
		settings = gameSettings;
	}

	@Override
	public final void calc() {
		if (this.settings.minecraft.currentScreen != null)
			return;
		this.move = 0.0F;
		this.strafe = 0.0F;
		this.elevate = 0.0F;
		if (this.keylist[0] != false)
			this.move -= 1.0F;
		if (this.keylist[1] != false)
			this.strafe -= 1.0F;
		if (this.keylist[2] != false)
			this.move += 1.0F;
		if (this.keylist[3] != false)
			this.strafe += 1.0F;
		if (this.fly) {
			if (this.keylist[5] != false)
				this.elevate += 0.3F;
			if (this.keylist[6] != false)
				this.elevate -= 0.3F;
		}
		this.mult = 1.0F;
		if (this.keylist[7] != false)
			this.mult = 5.0F;
		else if (this.keylist[8] != false)
			this.mult = 2.0F;

		this.noclip = this.cliplock;
		if (this.keylist[9] != false) {
			this.noclip = (!this.noclip);
		}

		this.jump = this.keylist[4];
	}

	@Override
	public void resetKeys() {
		for (int i = 0; i < keyStates.length; ++i) {
			keyStates[i] = false;
		}
		for (int i = 0; i < 10; i++)
			this.keylist[i] = false;
	}

	@Override
	public void setKeyState(int key, boolean state) {
		if(this.settings.minecraft.currentScreen!=null)
			canMove = false;
		else 
			canMove = true;
		byte index = -1;
		if (this.HacksMode == 0 || !(HackState.Fly || HackState.Speed || HackState.Noclip)) {

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
			if (key == settings.forwardKey.key)
				this.keylist[0] = state;
			if (key == settings.leftKey.key)
				this.keylist[1] = state;
			if (key == settings.backKey.key)
				this.keylist[2] = state;
			if (key == settings.rightKey.key)
				this.keylist[3] = state;
			if (key == 57)
				this.keylist[4] = state;
			if (key == 16)
				this.keylist[5] = state;
			if (key == 18)
				this.keylist[6] = state;
			if (key == 42)
				this.keylist[7] = state;
			if (key == 29)
				this.keylist[8] = state;
			if (key == 45)
				this.keylist[9] = state;
			if ((key == 60) && (state))
				this.cliplock = (!this.cliplock);
			if ((key == settings.flyKey.key) && (state))
				this.fly = (!this.fly);
		}
	}

	@Override
	public void updateMovement(int hackMode) {
		HacksMode = hackMode;
		
		if(this.settings.minecraft.currentScreen!=null)
			canMove = false;
		else 
			canMove = true;
		
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
			if (this.settings.HacksEnabled) {
				if (settings.CanSpeed) {
					running = keyStates[5];
					Minecraft.PlayerIsRunning = keyStates[5];
				}
				flyingUp = keyStates[6];
				flyingDown = keyStates[7];
			}
		}
	}
}
