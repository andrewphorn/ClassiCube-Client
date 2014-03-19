package com.mojang.minecraft.sound;

import com.mojang.minecraft.LogUtil;
import de.jarnbjo.ogg.LogicalOggStreamImpl;
import de.jarnbjo.ogg.OnDemandUrlStream;
import de.jarnbjo.vorbis.VorbisStream;

import java.net.URL;
import java.nio.ByteBuffer;

// TODO.
public final class Music implements Audio {

    ByteBuffer playing = ByteBuffer.allocate(176400);
    ByteBuffer current = ByteBuffer.allocate(176400);
    private ByteBuffer processing = null;
    ByteBuffer previous = null;
    VorbisStream stream;
    SoundPlayer player;
    boolean finished = false;
    boolean stopped = false;

    public Music(SoundPlayer var1, URL var2) {
        player = var1;
        try {
            LogicalOggStreamImpl var3 = new OnDemandUrlStream(var2).getLogicalStreams().iterator()
                    .next();
            stream = new VorbisStream(var3);
        } catch (Exception ex) {
            LogUtil.logError("Error loading music from " + var2, ex);
        }
        new MusicPlayThread(this).start();
    }

    @Override
    public boolean isFootStep(boolean really) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public final boolean play(int[] var1, int[] var2, int var3) {
        if (!player.settings.music) {
            stopped = true;
            return false;
        } else {
            int var4 = 0;

            while (var3 > 0 && (processing != null || previous != null)) {
                if (processing == null && previous != null) {
                    processing = previous;
                    previous = null;
                }

                if (processing != null && processing.remaining() > 0) {
                    int var5;
                    if ((var5 = processing.remaining() / 4) > var3) {
                        var5 = var3;
                    }

                    for (int var6 = 0; var6 < var5; ++var6) {
                        var1[var4 + var6] += processing.getShort();
                        var2[var4 + var6] += processing.getShort();
                    }

                    var4 += var5;
                    var3 -= var5;
                }

                if (current == null && processing != null && processing.remaining() == 0) {
                    current = processing;
                    processing = null;
                }
            }

            return processing != null || previous != null || !finished;
        }
    }
}
