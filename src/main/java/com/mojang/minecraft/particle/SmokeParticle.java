package com.mojang.minecraft.particle;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.render.ShapeRenderer;

public class SmokeParticle extends Particle {

    private static final long serialVersionUID = 1L;

    public SmokeParticle(Level var1, float var2, float var3, float var4) {
        super(var1, var2, var3, var4, 0F, 0F, 0F);
        xd *= 0.1F;
        yd *= 0.1F;
        zd *= 0.1F;
        rCol = gCol = bCol = (float) (Math.random() * 0.30000001192092896D);
        lifetime = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
        noPhysics = true;
    }

    @Override
    public void render(ShapeRenderer var1, float var2, float var3, float var4, float var5,
            float var6, float var7) {
        super.render(var1, var2, var3, var4, var5, var6, var7);
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
