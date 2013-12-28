package com.mojang.minecraft.sound;

import java.nio.ByteBuffer;

// TODO.
final class MusicPlayThread extends Thread {

	// $FF: synthetic field
	private Music music;

	public MusicPlayThread(Music var1) {
		super();
		music = var1;
		setPriority(10);
		setDaemon(true);
	}

	@Override
	public final void run() {
		try {
			do {
				if (music.stopped) {
					return;
				}

				ByteBuffer var2;
				if (music.playing == null && music.current != null) {
					var2 = music.current;
					music.playing = var2;
					var2 = null;
					music.current = null;
					music.playing.clear();
				}

				if (music.playing != null && music.playing.remaining() != 0) {
					while (true) {
						if (music.playing.remaining() == 0) {
							break;
						}

						var2 = music.playing;
						int var10 = music.stream.readPcm(var2.array(), var2.position(),
								var2.remaining());
						var2.position(var2.position() + var10);
						if (var10 <= 0) {
							music.finished = true;
							music.stopped = true;
							break;
						}
					}
				}

				if (music.playing != null && music.previous == null) {
					music.playing.flip();
					var2 = music.playing;
					music.previous = var2;
					var2 = null;
					music.playing = var2;
				}

				Thread.sleep(10L);
			} while (music.player.running);

			return;
		} catch (Exception var7) {
			var7.printStackTrace();
			return;
		} finally {
			music.finished = true;
		}

	}
}
