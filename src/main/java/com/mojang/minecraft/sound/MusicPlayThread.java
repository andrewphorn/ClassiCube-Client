package com.mojang.minecraft.sound;

import java.nio.ByteBuffer;

// TODO.
final class MusicPlayThread extends Thread {

   // $FF: synthetic field
   private Music music;


   public MusicPlayThread(Music var1) {
	   super();
      this.music = var1;
      this.setPriority(10);
      this.setDaemon(true);
   }

   public final void run() {
      try {
         do {
            if(this.music.stopped) {
               return;
            }

            ByteBuffer var2;
            if(this.music.playing == null) {
               if(this.music.current != null) {
                  var2 = this.music.current;
                  this.music.playing = var2;
                  var2 = null;
                  this.music.current = null;
                  this.music.playing.clear();
               }
            }

            if(this.music.playing != null) {
               if(this.music.playing.remaining() != 0) {
                  while(true) {
                     if(this.music.playing.remaining() == 0) {
                        break;
                     }

                     var2 = this.music.playing;
                     int var10 = this.music.stream.readPcm(var2.array(), var2.position(), var2.remaining());
                     var2.position(var2.position() + var10);
                     if(var10 <= 0) {
                        this.music.finished = true;
                        this.music.stopped = true;
                        break;
                     }
                  }
               }
            }

            if(this.music.playing != null) {
               if(this.music.previous == null) {
                  this.music.playing.flip();
                  var2 = this.music.playing;
                  this.music.previous = var2;
                  var2 = null;
                  this.music.playing = var2;
               }
            }

            Thread.sleep(10L);
         } while(this.music.player.running);

         return;
      } catch (Exception var7) {
         var7.printStackTrace();
         return;
      } finally {
         this.music.finished = true;
      }

   }
}
