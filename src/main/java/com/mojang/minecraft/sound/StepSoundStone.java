package com.mojang.minecraft.sound;

public final class StepSoundStone extends StepSound
{
    public StepSoundStone(String soundName, float soundVolume, float soundPitch)
    {
        super(soundName, soundVolume, soundPitch);
    }

    @Override
	public String getBreakSound()
    {
        return "random.glass";
    }

    @Override
	public String getPlaceSound()
    {
        return "step.stone";
    }
}
