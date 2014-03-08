package com.mojang.minecraft.sound;

import com.mojang.minecraft.Entity;
import com.mojang.util.MathHelper;

public abstract class BaseSoundPos implements SoundPos {
    private Entity listener;

    public BaseSoundPos(Entity listener) {
        this.listener = listener;
    }

    public float getDistanceSq(float x, float y, float z) {
        x -= listener.x;
        y -= listener.y;
        float var4 = z - listener.z;

        var4 = MathHelper.sqrt(x * x + y * y + var4 * var4);

        if ((var4 = 1.0F - var4 / 32.0F) < 0.0F) {
            var4 = 0.0F;
        }

        return var4;
    }

    public float getRotationDiff(float x, float y) {
        x -= listener.x;
        y -= listener.z;

        float var3 = MathHelper.sqrt(x * x + y * y);

        x /= var3;
        y /= var3;

        if ((var3 /= 2.0F) > 1.0F) {
            var3 = 1.0F;
        }

        float var4 = MathHelper.cos(-listener.yRot * (float) (Math.PI / 180D) + (float) Math.PI);

        return (MathHelper.sin(-listener.yRot * (float) (Math.PI / 180D) + (float) Math.PI) * y - var4 * x) * var3;
    }
}
