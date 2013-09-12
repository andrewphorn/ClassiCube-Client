package com.mojang.minecraft.sound;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

// TODO.
public final class SoundManager {

	public Map<String, Object> sounds = new HashMap<String, Object>();
	private Map<String, Object> music = new HashMap<String, Object>();
	public Random random = new Random();
	public long lastMusic = System.currentTimeMillis() + 60000L;

	public final AudioInfo getAudioInfo(String var1, float var2, float var3) {
		List<?> var4 = null;
		synchronized (this.sounds) {
			var4 = (List<?>) this.sounds.get(var1);
		}

		if (var4 == null) {
			return null;
		} else {
			SoundData var7 = (SoundData) var4.get(this.random.nextInt(var4.size()));
			return new SoundInfo(var7, var3, var2);
		}
	}

	public boolean playMusic(SoundPlayer var1, String var2) {
		List<?> var3 = null;
		synchronized (this.music) {
			var3 = (List<?>) this.music.get(var2);
		}

		if (var3 == null) {
			return false;
		} else {
			File var8 = (File) var3.get(this.random.nextInt(var3.size()));

			try {
				var1.play(new Music(var1, var8.toURI().toURL()));
			} catch (MalformedURLException var5) {
				var5.printStackTrace();
			} catch (@SuppressWarnings("hiding") IOException var6) {
				var6.printStackTrace();
			}

			return true;
		}
	}

	@SuppressWarnings("unchecked")
	public final void registerMusic(String var1, File var2) {
		synchronized (this.music) {
			for (var1 = var1.substring(0, var1.length() - 4).replaceAll("/", "."); Character
					.isDigit(var1.charAt(var1.length() - 1)); var1 = var1.substring(0,
					var1.length() - 1)) {
				;
			}

			Object var4;
			if ((var4 = (List<?>) this.music.get(var1)) == null) {
				var4 = new ArrayList<Object>();
				this.music.put(var1, var4);
			}

			((List<File>) var4).add(var2);
		}
	}

	@SuppressWarnings("unchecked")
	public void registerSound(File var1, String var2) {
		try {
			for (var2 = var2.substring(0, var2.length() - 4).replaceAll("/", "."); Character
					.isDigit(var2.charAt(var2.length() - 1)); var2 = var2.substring(0,
					var2.length() - 1)) {
				;
			}

			SoundData var7 = SoundReader.read(var1.toURI().toURL());
			synchronized (this.sounds) {
				Object var4;
				if ((var4 = (List<?>) this.sounds.get(var2)) == null) {
					var4 = new ArrayList<Object>();
					this.sounds.put(var2, var4);
				}

				((List<SoundData>) var4).add(var7);
			}
		} catch (Exception var6) {
			var6.printStackTrace();
		}

	}
}
