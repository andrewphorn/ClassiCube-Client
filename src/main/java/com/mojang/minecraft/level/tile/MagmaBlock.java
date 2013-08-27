package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.ColorCache;

public final class MagmaBlock extends Block {

   protected MagmaBlock(int var1, int var2) {
      super(var1, var2);
   }
   protected final ColorCache getBrightness(Level level, int x, int y, int z) {
	      return new ColorCache(100F/255, 100F/255, 100F/255);
   }
}
