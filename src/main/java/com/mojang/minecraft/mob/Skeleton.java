package com.mojang.minecraft.mob;

import com.mojang.minecraft.item.Arrow;
import com.mojang.minecraft.level.Level;

public class Skeleton extends Zombie {

    public static final long serialVersionUID = 0L;

    public Skeleton(Level level, float posX, float posY, float posZ) {
        super(level, posX, posY, posZ);
        modelName = "skeleton";
        textureName = "/mob/skeleton.png";
        Skeleton$1 skeletonAI = new Skeleton$1(this);
        deathScore = 120;
        skeletonAI.runSpeed = 0.3F;
        skeletonAI.damage = 8;
        ai = skeletonAI;
    }

    // $FF: synthetic method
    static void shootRandomArrow(Skeleton skeleton) {
        for (int i = 0; i < (int) ((Math.random() + Math.random()) * 3D + 4D); ++i) {
            skeleton.level.addEntity(new Arrow(skeleton.level, skeleton.level.getPlayer(), skeleton.x,
                    skeleton.y - 0.2F, skeleton.z, (float) Math.random() * 360F,
                    -((float) Math.random()) * 60F, 0.4F));
        }

    }

    public void shootArrow(Level level) {
        level.addEntity(new Arrow(level, this, x, y, z, yRot + 180F
                + (float) (Math.random() * 45D - 22.5D),
                xRot - (float) (Math.random() * 45D - 10D), 1F
        ));
    }
}
