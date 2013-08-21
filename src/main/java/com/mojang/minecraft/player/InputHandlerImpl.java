package com.mojang.minecraft.player;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Minecraft;

public class InputHandlerImpl extends InputHandler
{
	public InputHandlerImpl(GameSettings gameSettings)
	{
		settings = gameSettings;
	}

	@Override
	public void updateMovement()
	{
		xxa = 0.0F;
		yya = 0.0F;

		if(keyStates[0])
		{
			yya--;
		}

		if(keyStates[1])
		{
			yya++;
		}

		if(keyStates[2])
		{
			xxa--;
		}

		if(keyStates[3])
		{
			xxa++;
		}

		jumping = keyStates[4];
		if(settings.CanSpeed){
			running = keyStates[5];
			Minecraft.PlayerIsRunning = keyStates[5];
		}
		
		flyingUp = keyStates[7];
		flyingDown = keyStates[8];
		
		if(keyStates[6]){
			flying = !flying;
		}
	}

	@Override
	public void resetKeys()
	{
		for(int i = 0; i < keyStates.length; ++i)
		{
			keyStates[i] = false;
		}

	}

	@Override
	public void setKeyState(int key, boolean state)
	{
		byte index = -1;

		if(key == settings.forwardKey.key)
		{
			index = 0;
		}

		if(key == settings.backKey.key)
		{
			index = 1;
		}

		if(key == settings.leftKey.key)
		{
			index = 2;
		}

		if(key == settings.rightKey.key)
		{
			index = 3;
		}

		if(key == settings.jumpKey.key)
		{
			index = 4;
		}

		if(key == settings.runKey.key)
		{
			index = 5;
		}
		
		if(key == settings.flyKey.key)
		{
			index = 6;
		}
		
		if(key == settings.flyUp.key)
		{
			index = 7;
		}
		if(key == settings.flyDown.key)
		{
			index = 8;
		}

		if(index >= 0)
		{
			keyStates[index] = state;
		}

	}

	private boolean[] keyStates = new boolean[100];
	private GameSettings settings;
}
