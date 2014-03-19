package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.render.ShapeRenderer;
import org.lwjgl.opengl.GL11;

public final class IceBlock extends Block {

    boolean showNeighborSides = false;

    protected IceBlock(int id) {
        super(id);
        Block.liquid[id] = true;
    }

    @Override
    public final boolean canRenderSide(Level level, int x, int y, int z, int side) {
        int tile = level.getTile(x, y, z);
        return !(!showNeighborSides && tile == id) && super.canRenderSide(level, x, y, z, side);
    }

    @Override
    public final int getRenderPass() {
        return 1;
    }

    @Override
    public final boolean isOpaque() {
        return true;
    }

    @Override
    public final boolean isSolid() {
        return false;
    }

    @Override
    public void renderInside(ShapeRenderer shapeRenderer, int x, int y, int z, int side) {
        int textureID1 = getTextureId(side);

        renderSide(shapeRenderer, x, y, z, side, textureID1);
    }

    @Override
    public void renderPreview(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        for (int face = 0; face < 6; ++face) {
            /*
             * if (face == 0) { shapeRenderer.useNormal(0F, 1F, 0F); }
             *
             * if (face == 1) { shapeRenderer.useNormal(0F, -1F, 0F); }
             *
             * if (face == 2) { shapeRenderer.useNormal(0F, 0F, 1F); }
             *
             * if (face == 3) { shapeRenderer.useNormal(0F, 0F, -1F); }
             *
             * if (face == 4) { shapeRenderer.useNormal(1F, 0F, 0F); }
             *
             * if (face == 5) { shapeRenderer.useNormal(-1F, 0F, 0F); }
             */

            renderInside(shapeRenderer, 0, 0, 0, face);
        }
        GL11.glDisable(GL11.GL_BLEND);
        shapeRenderer.end();
    }
}
