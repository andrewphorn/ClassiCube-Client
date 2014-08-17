package com.mojang.minecraft.particle;

import com.mojang.minecraft.level.Level;

public class WaterDropParticle extends Particle {

    private static final long serialVersionUID = 1L;

    public WaterDropParticle(Level level, float x, float y, float z) {
        super(level, x, y, z, 0F, 0F, 0F);
        xd *= 0.3F;
        yd = (float) Math.random() * 0.2F + 0.1F;
        zd *= 0.3F;
        rCol = 1F;
        gCol = 1F;
        bCol = 1F;
        tex = 16;
        setSize(0.01F, 0.01F);
        lifetime = (int) (8D / (Math.random() * 0.8D + 0.2D));
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        yd = (float) (yd - 0.06D);
        move(xd, yd, zd);
        xd *= 0.98F;
        yd *= 0.98F;
        zd *= 0.98F;
        if (lifetime-- <= 0) {
            remove();
        }

        if (onGround) {
            if (Math.random() < 0.5D) {
                remove();
            }

            xd *= 0.7F;
            zd *= 0.7F;
        }

    }
}
