package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.mob.ai.BasicAI;
import com.mojang.util.MathHelper;

final class Sheep$1 extends BasicAI {

    private static final long serialVersionUID = 1L;
    // $FF: synthetic field
    final Sheep sheep;

    Sheep$1(Sheep var1) {
        sheep = var1;
    }

    @Override
    protected final void update() {
        float var1 = MathHelper.sin(sheep.yRot * (float) Math.PI / 180F);
        float var2 = MathHelper.cos(sheep.yRot * (float) Math.PI / 180F);
        var1 = -0.7F * var1;
        var2 = 0.7F * var2;
        int var4 = (int) (mob.x + var1);
        int var3 = (int) (mob.y - 2F);
        int var5 = (int) (mob.z + var2);
        if (sheep.grazing) {
            if (level.getTile(var4, var3, var5) != Block.GRASS.id) {
                sheep.grazing = false;
            } else {
                if (++sheep.grazingTime == 60) {
                    level.setTile(var4, var3, var5, Block.DIRT.id);
                    if (random.nextInt(5) == 0) {
                        sheep.hasFur = true;
                    }
                }

                xxa = 0F;
                yya = 0F;
                mob.xRot = 40 + sheep.grazingTime / 2 % 2 * 10;
            }
        } else {
            if (level.getTile(var4, var3, var5) == Block.GRASS.id) {
                sheep.grazing = true;
                sheep.grazingTime = 0;
            }

            super.update();
        }
    }
}
