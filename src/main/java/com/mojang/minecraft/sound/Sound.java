package com.mojang.minecraft.sound;

// TODO.
public final class Sound implements Audio {

    private AudioInfo info;
    private SoundPos pos;
    private float pitch = 0.0F;
    private float volume = 1.0F;
    private short[] data = new short[1];

    public Sound(AudioInfo var1, SoundPos var2) {
        info = var1;
        pos = var2;
        pitch = var2.getRotationDiff();
        volume = var2.getDistanceSq() * var1.volume;
    }

    @Override
    public boolean isFootStep(boolean really) {
        // TODO Auto-generated method stub
        return really;
    }

    @Override
    public final boolean play(int[] var1, int[] var2, int var3) {
        if (data.length < var3) {
            data = new short[var3];
        }

        int var4;
        boolean var5 = (var4 = info.update(data, var3)) > 0;
        float var6 = pos.getRotationDiff();
        float var7 = pos.getDistanceSq() * info.volume;
        int var8 = (int) ((pitch > 0.0F ? 1.0F - pitch : 1.0F) * volume * 65536.0F);
        int var9 = (int) ((pitch < 0.0F ? 1.0F + pitch : 1.0F) * volume * 65536.0F);
        int var10 = (int) ((var6 > 0.0F ? 1.0F - var6 : 1.0F) * var7 * 65536.0F);
        int var11 = (int) ((var6 < 0.0F ? var6 + 1.0F : 1.0F) * var7 * 65536.0F);
        var10 -= var8;
        var11 -= var9;
        int var12;
        int var13;
        int var14;
        if (var10 == 0 && var11 == 0) {
            if (var8 >= 0 || var9 != 0) {
                var12 = var8;
                var13 = var9;

                for (var14 = 0; var14 < var4; ++var14) {
                    var1[var14] += data[var14] * var12 >> 16;
                    var2[var14] += data[var14] * var13 >> 16;
                }
            }
        } else {
            for (var12 = 0; var12 < var4; ++var12) {
                var13 = var8 + var10 * var12 / var3;
                var14 = var9 + var11 * var12 / var3;
                var1[var12] += data[var12] * var13 >> 16;
                var2[var12] += data[var12] * var14 >> 16;
            }
        }

        pitch = var6;
        volume = var7;
        return var5;
    }

}
