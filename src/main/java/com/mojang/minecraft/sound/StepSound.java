package com.mojang.minecraft.sound;

public class StepSound {
    public final String stepSoundName;
    public final float stepSoundVolume;
    public final float stepSoundPitch;

    public StepSound(String soundName, float soundVolume, float soundPitch) {
        stepSoundName = soundName;
        stepSoundVolume = soundVolume;
        stepSoundPitch = soundPitch;
    }

    public String getBreakSound() {
        return "dig." + stepSoundName;
    }

    public float getPitch() {
        return stepSoundPitch;
    }

    public String getPlaceSound() {
        return getBreakSound();
    }

    public String getStepSound() {
        return "step." + stepSoundName;
    }

    public float getVolume() {
        return stepSoundVolume;
    }
}
