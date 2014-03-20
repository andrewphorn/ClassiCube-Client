package com.mojang.minecraft.sound;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.mojang.minecraft.LogUtil;

// TODO.
public final class SoundManager {

    public Map<String, Object> sounds = new HashMap<>();
    public Random random = new Random();
    public long lastMusic = System.currentTimeMillis() + 60000L;
    private Map<String, Object> music = new HashMap<>();

    public final AudioInfo getAudioInfo(String var1, float var2, float var3) {
        List<?> var4 = null;
        synchronized (sounds) {
            var4 = (List<?>) sounds.get(var1);
        }

        if (var4 == null) {
            return null;
        } else {
            SoundData var7 = (SoundData) var4.get(random.nextInt(var4.size()));
            return new SoundInfo(var7, var3, var2);
        }
    }

    public boolean playMusic(SoundPlayer var1, String var2) {
        List<?> var3 = null;
        synchronized (music) {
            var3 = (List<?>) music.get(var2);
        }

        if (var3 == null) {
            return false;
        } else {
            File var8 = (File) var3.get(random.nextInt(var3.size()));

            try {
                var1.play(new Music(var1, var8.toURI().toURL()));
            } catch (Exception ex) {
                LogUtil.logError("Error queueing music to play from " + var2, ex);
            }

            return true;
        }
    }

    @SuppressWarnings("unchecked")
    public final void registerMusic(String var1, File var2) {
        synchronized (music) {
            var1 = var1.substring(0, var1.length() - 4).replaceAll("/", ".");
            while (Character.isDigit(var1.charAt(var1.length() - 1))) {
                var1 = var1.substring(0, var1.length() - 1);
            }

            Object var4 = music.get(var1);
            if (var4 == null) {
                var4 = new ArrayList<>();
                music.put(var1, var4);
            }

            ((List<File>) var4).add(var2);
        }
    }

    @SuppressWarnings("unchecked")
    public void registerSound(File var1, String var2) {
        try {
            var2 = var2.substring(0, var2.length() - 4).replaceAll("/", ".");
            while (Character.isDigit(var2.charAt(var2.length() - 1))) {
                var2 = var2.substring(0, var2.length() - 1);
            }

            SoundData var7 = SoundReader.read(var1.toURI().toURL());
            synchronized (sounds) {
                Object var4;
                if ((var4 = sounds.get(var2)) == null) {
                    var4 = new ArrayList<>();
                    sounds.put(var2, var4);
                }

                ((List<SoundData>) var4).add(var7);
            }
        } catch (Exception ex) {
            LogUtil.logError("Error registering sound " + var2 + " from " + var1, ex);
        }
    }
}
