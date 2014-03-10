package com.mojang.minecraft.mob;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.item.Item;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.model.AnimalModel;
import com.mojang.minecraft.player.Player;
import com.mojang.minecraft.render.TextureManager;

public class Sheep extends QuadrupedMob {

    public static final long serialVersionUID = 0L;
    public boolean hasFur = true;
    public boolean grazing = false;
    public int grazingTime = 0;
    public float graze;
    public float grazeO;

    public Sheep(Level var1, float var2, float var3, float var4) {
        super(var1, var2, var3, var4);
        setSize(1.4F, 1.72F);
        this.setPos(var2, var3, var4);
        heightOffset = 1.72F;
        modelName = "sheep";
        textureName = "/mob/sheep.png";
        ai = new Sheep$1(this);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        grazeO = graze;
        if (grazing) {
            graze += 0.2F;
        } else {
            graze -= 0.2F;
        }

        if (graze < 0F) {
            graze = 0F;
        }

        if (graze > 1F) {
            graze = 1F;
        }

    }

    @Override
    public void die(Entity var1) {
        if (var1 != null) {
            var1.awardKillScore(this, 10);
        }

        int var2 = (int) (Math.random() + Math.random() + 1D);

        for (int var3 = 0; var3 < var2; ++var3) {
            level.addEntity(new Item(level, x, y, z, Block.BROWN_MUSHROOM.id));
        }

        super.die(var1);
    }

    @Override
    public void hurt(Entity var1, int var2) {
        if (hasFur && var1 instanceof Player) {
            hasFur = false;
            int var3 = (int) (Math.random() * 3D + 1D);

            for (var2 = 0; var2 < var3; ++var2) {
                level.addEntity(new Item(level, x, y, z, Block.WHITE_WOOL.id));
            }

        } else {
            super.hurt(var1, var2);
        }
    }

    @Override
    public void renderModel(TextureManager var1, float var2, float var3, float var4, float var5,
            float var6, float var7) {
        AnimalModel var8;
        float var9 = (var8 = (AnimalModel) modelCache.getModel("sheep")).head.y;
        float var10 = var8.head.z;
        var8.head.y += (grazeO + (graze - grazeO) * var3) * 8F;
        var8.head.z -= grazeO + (graze - grazeO) * var3;
        super.renderModel(var1, var2, var3, var4, var5, var6, var7);
        if (hasFur || modelName.equals("sheep.fur")) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, var1.load("/mob/sheep_fur.png"));
            GL11.glDisable(GL11.GL_CULL_FACE);
            AnimalModel var11;
            (var11 = (AnimalModel) modelCache.getModel("sheep.fur")).head.yaw = var8.head.yaw;
            var11.head.pitch = var8.head.pitch;
            var11.head.y = var8.head.y;
            var11.head.x = var8.head.x;
            var11.body.yaw = var8.body.yaw;
            var11.body.pitch = var8.body.pitch;
            var11.leg1.pitch = var8.leg1.pitch;
            var11.leg2.pitch = var8.leg2.pitch;
            var11.leg3.pitch = var8.leg3.pitch;
            var11.leg4.pitch = var8.leg4.pitch;
            var11.head.render(var7);
            var11.body.render(var7);
            var11.leg1.render(var7);
            var11.leg2.render(var7);
            var11.leg3.render(var7);
            var11.leg4.render(var7);
        }

        var8.head.y = var9;
        var8.head.z = var10;
    }
}
