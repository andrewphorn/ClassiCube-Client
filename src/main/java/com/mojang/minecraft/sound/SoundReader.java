package com.mojang.minecraft.sound;

import java.net.URL;

import com.mojang.minecraft.LogUtil;

import de.jarnbjo.ogg.LogicalOggStreamImpl;
import de.jarnbjo.ogg.OnDemandUrlStream;
import de.jarnbjo.vorbis.IdentificationHeader;
import de.jarnbjo.vorbis.VorbisStream;

// TODO.
public final class SoundReader {

	public static SoundData read(URL var0) {
		VorbisStream vorbisStream = null;
		try {
			LogicalOggStreamImpl OggStream = new OnDemandUrlStream(var0).getLogicalStreams()
					.iterator().next();
			vorbisStream = new VorbisStream(OggStream);
		} catch (Exception ex) {
			LogUtil.logWarning("Error loading sound stream from " + var0, ex);
		}
		byte[] var2 = new byte[4096];
		int var3 = 0;
		int var4 = vorbisStream.getIdentificationHeader().getChannels();
		short[] var5 = new short[4096];
		int var6 = 0;

		label51: while (var3 >= 0) {
			int var15 = 0;

			while (true) {
				try {
					if (var15 < var2.length
							&& (var3 = vorbisStream.readPcm(var2, var15, var2.length - var15)) > 0) {
						var15 += var3;
						continue;
					}
				} catch (Exception var10) {
					var3 = -1;
				}

				if (var15 <= 0) {
					break;
				}

				int var16 = 0;

				while (true) {
					if (var16 >= var15) {
						continue label51;
					}

					int var8 = 0;

					for (int var9 = 0; var9 < var4; ++var9) {
						var8 += var2[var16++] << 8 | var2[var16++] & 255;
					}

					if (var6 == var5.length) {
						short[] var18 = var5;
						var5 = new short[var5.length << 1];
						System.arraycopy(var18, 0, var5, 0, var6);
					}

					var5[var6++] = (short) (var8 / var4);
				}
			}
		}

		if (var6 != var5.length) {
			short[] var17 = var5;
			var5 = new short[var6];
			System.arraycopy(var17, 0, var5, 0, var6);
		}

		@SuppressWarnings("unused")
		IdentificationHeader var13;
		return new SoundData(var5, (var13 = vorbisStream.getIdentificationHeader()).getSampleRate());
	}
}
