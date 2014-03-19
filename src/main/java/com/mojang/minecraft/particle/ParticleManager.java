package com.mojang.minecraft.particle;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.render.TextureManager;

import java.util.ArrayList;
import java.util.List;

public final class ParticleManager {

    @SuppressWarnings("rawtypes")
    public List[] particles = new List[2];
    public TextureManager textureManager;

    @SuppressWarnings("rawtypes")
    public ParticleManager(Level level, TextureManager textureManager) {
        if (level != null) {
            level.particleEngine = this;
        }

        this.textureManager = textureManager;

        for (int i = 0; i < 2; ++i) {
            particles[i] = new ArrayList();
        }

    }

    /**
     * Spawn a particle.
     * @param entity The entity spawning the particle.
     */
    @SuppressWarnings("unchecked")
    public final void spawnParticle(Entity entity) {
        Particle particle = (Particle) entity;
        int textureID = particle.getParticleTexture();
        particles[textureID].add(particle);
    }

    /**
     * A tick. Calls tick() for all particles I control.
     */
    public final void tick() {
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < particles[i].size(); ++j) {
                Particle particle = (Particle) particles[i].get(j);
                particle.tick();
                if (particle.removed) {
                    particles[i].remove(j--);
                }
            }
        }

    }
}
