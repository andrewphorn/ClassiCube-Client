package com.mojang.minecraft.sound;

// TODO.
public final class SoundInfo extends AudioInfo {

    private SoundData data;
    private float seek = 0F;
    private float pitch;

    public SoundInfo(SoundData var1, float var2, float var3) {
        data = var1;
        pitch = var2 * 44100F / var1.length;
        volume = var3;
    }

    @Override
    public final int update(short[] var1, int var2) {
        if (seek >= data.data.length) {
            return 0;
        } else {
            for (int var3 = 0; var3 < var2; ++var3) {
                int var4 = (int) seek;
                short var5 = data.data[var4];
                short var6 = var4 < data.data.length - 1 ? data.data[var4 + 1] : 0;
                var1[var3] = (short) (int) (var5 + (var6 - var5) * (seek - var4));
                seek += pitch;
                if (seek >= data.data.length) {
                    return var3;
                }
            }

            return var2;
        }
    }
}
