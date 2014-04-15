package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.item.PrimedTnt;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.particle.ParticleManager;

public final class TNTBlock extends Block {

    public TNTBlock(int id) {
        super(id);
    }

    @Override
    public final void explode(Level var1, int x, int y, int z) {
        if (!var1.creativeMode) {
            PrimedTnt primedTnt = new PrimedTnt(var1, x + 0.5F, y + 0.5F, z + 0.5F);
            primedTnt.life = random.nextInt(primedTnt.life / 4) + primedTnt.life / 8;
            var1.addEntity(primedTnt);
        }

    }

    @Override
    public final int getDropCount() {
        return 0;
    }

    @Override
    public final int getTextureId(int texture) {
        return texture == 0 ? textureId + 2 : texture == 1 ? textureId + 1 : textureId;
    }

    @Override
    public final void spawnBreakParticles(Level level, int x, int y, int z,
                                          ParticleManager particleManager) {
        if (!level.creativeMode) {
            level.addEntity(new PrimedTnt(level, x + 0.5F, y + 0.5F, z + 0.5F));
        } else {
            super.spawnBreakParticles(level, x, y, z, particleManager);
        }
    }
}
