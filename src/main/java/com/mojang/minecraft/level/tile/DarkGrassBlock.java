package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;

import java.util.Random;

public final class DarkGrassBlock extends Block {

   protected DarkGrassBlock(int var1) {
      super(51);
      this.textureId = 47;
      this.setPhysics(true);
   }

   protected final int getTextureId(int texture) {
      return texture == 1?31:(texture == 31?2:47);
   }

   public final void update(Level level, int x, int y, int z, Random rand) {
      if(rand.nextInt(4) == 0) {
         if(!level.isLit(x, y, z)) {
            level.setTile(x, y, z, DIRT.id);
         } else {
            for(int var9 = 0; var9 < 4; ++var9) {
               int var6 = x + rand.nextInt(3) - 1;
               int var7 = y + rand.nextInt(5) - 3;
               int var8 = z + rand.nextInt(3) - 1;
               if(level.getTile(var6, var7, var8) == DIRT.id && level.isLit(var6, var7, var8)) {
                  //level.setTile(var6, var7, var8, DARKGRASS.id);
               }
            }

         }
      }
   }

   public final int getDrop() {
      return DIRT.getDrop();
   }
}
