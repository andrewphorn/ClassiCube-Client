package com.mojang.minecraft.particle;

import java.util.ArrayList;
import java.util.List;

import com.mojang.minecraft.Entity;

public final class ParticleManager {

    public List<Particle> particles0 = new ArrayList<>();
    public List<Particle> particles1 = new ArrayList<>();

    /**
     * Spawn a particle.
     *
     * @param entity The entity spawning the particle.
     */
    public final void spawnParticle(Entity entity) {
        Particle particle = (Particle) entity;
        int textureID = particle.getParticleTexture();
        if (textureID == 0) {
            particles0.add(particle);
        } else {
            particles1.add(particle);
        }
    }

    /**
     * A tick. Calls tick() for all particles I control.
     */
    public final void tick() {
        tick(particles0);
        tick(particles1);
    }

    private static void tick(List<Particle> particles) {
        for (int j = 0; j < particles.size(); ++j) {
            Particle particle = particles.get(j);
            particle.tick();
            if (particle.removed) {
                particles.remove(j--);
            }
        }
    }

    public void clear() {
        particles0.clear();
        particles1.clear();
    }
}
