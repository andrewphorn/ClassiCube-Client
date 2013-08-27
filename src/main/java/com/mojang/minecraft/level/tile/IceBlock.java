package com.mojang.minecraft.level.tile;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.render.ShapeRenderer;


public final class IceBlock extends Block {

	int ID;
	boolean showNeighborSides = false;
   protected IceBlock(int var1, int var2) {
	   super(var1, var2);
	  ID = var1;
	  Block.liquid[var1] = true;
   }
   public final boolean isOpaque() {
	      return true;
	   }

	   public final boolean isSolid() {
	      return false;
	   }

	   public final boolean canRenderSide(Level level, int x, int y, int z, int side) {
	      int var6 = level.getTile(x, y, z);
	      return !this.showNeighborSides && var6 == this.id?false:super.canRenderSide(level, x, y, z, side);
	   }
	   
	   public final int getRenderPass() {
		      return 1;
	   }
	  
	   
	   @Override
	   public void renderPreview(ShapeRenderer var1) {
			var1.begin();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(770,771);
			for(int var2 = 0; var2 < 6; ++var2) {
				if(var2 == 0) {
					var1.normal(0.0F, 1.0F, 0.0F);
				}

				if(var2 == 1) {
					var1.normal(0.0F, -1.0F, 0.0F);
				}

				if(var2 == 2) {
					var1.normal(0.0F, 0.0F, 1.0F);
				}

				if(var2 == 3) {
					var1.normal(0.0F, 0.0F, -1.0F);
				}

				if(var2 == 4) {
					var1.normal(1.0F, 0.0F, 0.0F);
				}

				if(var2 == 5) {
					var1.normal(-1.0F, 0.0F, 0.0F);
				}

				this.renderInside(var1, 0, 0, 0, var2);
			}
			GL11.glDisable(GL11.GL_BLEND);
			var1.end();
		}
}
