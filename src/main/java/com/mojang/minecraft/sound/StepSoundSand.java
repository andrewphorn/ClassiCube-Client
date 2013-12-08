package com.mojang.minecraft.sound;

public final class StepSoundSand extends StepSound
{
    public StepSoundSand(String soundName, float soundVolume, float soundPitch)
    {
        super(soundName, soundVolume, soundPitch);
    }

    public String getBreakSound()
    {
        return "dig.wood";
    }
}