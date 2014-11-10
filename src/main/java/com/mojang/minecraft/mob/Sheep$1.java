package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.mob.ai.BasicAI;
import com.mojang.util.MathHelper;

final class Sheep$1 extends BasicAI {

    // $FF: synthetic field
    final Sheep sheep;

    Sheep$1(Sheep sheep) {
        this.sheep = sheep;
    }

    @Override
    protected final void update() {
        float rotation = sheep.yRot * (float) Math.PI / 180F;
        // Calculate which tile the sheep is on
        int x = (int) (mob.x + -0.7F * MathHelper.sin(rotation));
        int y = (int) (mob.y - 2F);
        int z = (int) (mob.z + 0.7F * MathHelper.cos(rotation));
        if (sheep.grazing) {
            if (level.getTile(x, y, z) != Block.GRASS.id) {
                sheep.grazing = false;
            } else {
                if (++sheep.grazingTime == 60) {
                    level.setTile(x, y, z, Block.DIRT.id);
                    if (random.nextInt(5) == 0) {
                        sheep.hasFur = true;
                    }
                }

                xxa = 0F;
                yya = 0F;
                mob.xRot = 40 + sheep.grazingTime / 2 % 2 * 10;
            }
        } else {
            if (level.getTile(x, y, z) == Block.GRASS.id) {
                sheep.grazing = true;
                sheep.grazingTime = 0;
            }

            super.update();
        }
    }
}
