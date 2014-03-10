package com.mojang.minecraft.sound;

import com.mojang.minecraft.Entity;

public final class LevelSoundPos extends BaseSoundPos {
    private float x;

    private float y;

    private float z;

    public LevelSoundPos(float x, float y, float z, Entity listener) {
        super(listener);

        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public float getDistanceSq() {
        return super.getDistanceSq(x, y, z);
    }

    @Override
    public float getRotationDiff() {
        return super.getRotationDiff(x, z);
    }
}
