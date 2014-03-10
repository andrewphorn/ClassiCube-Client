package com.mojang.minecraft.particle;

import java.util.ArrayList;
import java.util.List;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.render.TextureManager;

public final class ParticleManager {

    @SuppressWarnings("rawtypes")
    public List[] particles = new List[2];
    public TextureManager textureManager;

    @SuppressWarnings("rawtypes")
    public ParticleManager(Level var1, TextureManager var2) {
        if (var1 != null) {
            var1.particleEngine = this;
        }

        textureManager = var2;

        for (int var3 = 0; var3 < 2; ++var3) {
            particles[var3] = new ArrayList();
        }

    }

    @SuppressWarnings("unchecked")
    public final void spawnParticle(Entity var1) {
        Particle var3;
        int var2 = (var3 = (Particle) var1).getParticleTexture();
        particles[var2].add(var3);
    }

    public final void tick() {
        for (int var1 = 0; var1 < 2; ++var1) {
            for (int var2 = 0; var2 < particles[var1].size(); ++var2) {
                Particle var3;
                (var3 = (Particle) particles[var1].get(var2)).tick();
                if (var3.removed) {
                    particles[var1].remove(var2--);
                }
            }
        }

    }
}
