package com.mojang.minecraft.sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.SourceDataLine;

import com.mojang.minecraft.GameSettings;

// TODO.
public final class SoundPlayer implements Runnable {

    public boolean running = false;
    public SourceDataLine dataLine;
    private List<Audio> audioQueue = new ArrayList<Audio>();
    public GameSettings settings;

    public SoundPlayer(GameSettings var1) {
        settings = var1;
    }

    public void clear() {
        audioQueue.clear();
    }

    public final void play(Audio var1) {
        if (running) {
            synchronized (audioQueue) {
                audioQueue.add(var1);
            }
        }
    }

    public final void play(AudioInfo var1, SoundPos var2) {
        this.play(new Sound(var1, var2));
    }

    @Override
    public final void run() {
        int[] var1 = new int[4410];
        int[] var2 = new int[4410];

        for (byte[] var3 = new byte[17640]; running; dataLine.write(var3, 0, 17640)) {
            try {
                Thread.sleep(1L);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Arrays.fill(var1, 0, 4410, 0);
            Arrays.fill(var2, 0, 4410, 0);
            int[] var5 = var2;
            int[] var6 = var1;
            synchronized (audioQueue) {
                int i = 0;
                while (true) {
                    if (i >= audioQueue.size()) {
                        break;
                    }
                    if (!audioQueue.get(i).play(var6, var5, 4410)) {
                        audioQueue.remove(i--);
                    }

                    ++i;
                }
            }

            int i;
            if (!settings.music && !settings.sound) {
                for (i = 0; i < 4410; ++i) {
                    var3[i << 2] = 0;
                    var3[(i << 2) + 1] = 0;
                    var3[(i << 2) + 2] = 0;
                    var3[(i << 2) + 3] = 0;
                }
            } else {
                for (i = 0; i < 4410; ++i) {
                    int var15 = var1[i];
                    int var14 = var2[i];
                    if (var15 < -32000) {
                        var15 = -32000;
                    }

                    if (var14 < -32000) {
                        var14 = -32000;
                    }

                    if (var15 >= 32000) {
                        var15 = 32000;
                    }

                    if (var14 >= 32000) {
                        var14 = 32000;
                    }

                    var3[i << 2] = (byte) (var15 >> 8);
                    var3[(i << 2) + 1] = (byte) var15;
                    var3[(i << 2) + 2] = (byte) (var14 >> 8);
                    var3[(i << 2) + 3] = (byte) var14;
                }
            }
        }
    }
}
