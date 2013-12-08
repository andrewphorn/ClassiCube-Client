package com.mojang.minecraft.sound;

public class StepSound
{
    public final String stepSoundName;
    public final float stepSoundVolume;
    public final float stepSoundPitch;

    public StepSound(String soundName, float soundVolume, float soundPitch)
    {
        this.stepSoundName = soundName;
        this.stepSoundVolume = soundVolume;
        this.stepSoundPitch = soundPitch;
    }


    public float getVolume()
    {
        return this.stepSoundVolume;
    }

    public float getPitch()
    {
        return this.stepSoundPitch;
    }


    public String getStepSound()
    {
        return "step." + this.stepSoundName;
    }
    
    public String getBreakSound()
    {
        return "dig." + this.stepSoundName;
    }

    public String getPlaceSound()
    {
        return this.getBreakSound();
    }
}
