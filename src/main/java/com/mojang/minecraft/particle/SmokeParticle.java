package com.mojang.minecraft.particle;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.render.ShapeRenderer;

public class SmokeParticle extends Particle {

    private static final long serialVersionUID = 1L;

    public SmokeParticle(Level level, float x, float y, float z) {
        super(level, x, y, z, 0F, 0F, 0F);
        xd *= 0.1F;
        yd *= 0.1F;
        zd *= 0.1F;
        rCol = gCol = bCol = (float) (Math.random() * 0.30000001192092896D);
        lifetime = (int) (8D / (Math.random() * 0.8D + 0.2D));
        noPhysics = true;
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime) {
            remove();
        }

        tex = 7 - (age << 3) / lifetime;
        yd = (float) (yd + 0.004D);
        move(xd, yd, zd);
        xd *= 0.96F;
        yd *= 0.96F;
        zd *= 0.96F;
        if (onGround) {
            xd *= 0.7F;
            zd *= 0.7F;
        }

    }
}
