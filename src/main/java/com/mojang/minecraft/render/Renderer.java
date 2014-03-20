package com.mojang.minecraft.render;

import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.player.Player;
import com.mojang.util.MathHelper;
import com.mojang.util.Vec3D;

public final class Renderer {

    public Minecraft minecraft;
    public float fogColorMultiplier = 1F;
    public boolean displayActive = false;
    public float fogEnd = 0F;
    public HeldBlock heldBlock;
    public int levelTicks;
    public Entity entity = null;
    public Random random = new Random();
    public float fogRed;
    public float fogBlue;
    public float fogGreen;
    private FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

    public Renderer(Minecraft minecraft) {
        this.minecraft = minecraft;
        heldBlock = new HeldBlock(minecraft);
    }

    public void applyBobbing(float var1, boolean enabled) {
        Player player = minecraft.player;
        float var2 = player.walkDist - player.walkDistO;
        var2 = player.walkDist + var2 * var1;
        float var3 = player.oBob + (player.bob - player.oBob) * var1;
        float var5 = player.oTilt + (player.tilt - player.oTilt) * var1;
        if (enabled) {
            GL11.glTranslatef(MathHelper.sin(var2 * (float) Math.PI) * var3 * 0.5F,
                    -Math.abs(MathHelper.cos(var2 * (float) Math.PI) * var3), 0F);
            GL11.glRotatef(MathHelper.sin(var2 * (float) Math.PI) * var3 * 3F, 0F, 0F, 1F);
            GL11.glRotatef(Math.abs(MathHelper.cos(var2 * (float) Math.PI + 0.2F) * var3) * 5F, 1F,
                    0F, 0F);
        }
        GL11.glRotatef(var5, 1F, 0F, 0F);
    }

    private FloatBuffer createBuffer(float var1, float var2, float var3, float var4) {
        buffer.clear();
        buffer.put(var1).put(var2).put(var3).put(var4);
        buffer.flip();
        return buffer;
    }

    public final void enableGuiMode() {
        int var1 = minecraft.width * 240 / minecraft.height;
        int var2 = minecraft.height * 240 / minecraft.height;
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0D, var1, var2, 0D, 100D, 300D);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0F, 0F, -200F);
    }

    public Vec3D getPlayerVector(float var1) {
        Player player = minecraft.player;
        float var2 = player.xo + (player.x - player.xo) * var1;
        float var3 = player.yo + (player.y - player.yo) * var1;
        float var5 = player.zo + (player.z - player.zo) * var1;
        return new Vec3D(var2, var3, var5);
    }

    public void hurtEffect(float var1) {
        Player var3;
        float var2 = (var3 = minecraft.player).hurtTime - var1;
        if (var3.health <= 0) {
            var1 += var3.deathTime;
            GL11.glRotatef(40F - 8000F / (var1 + 200F), 0F, 0F, 1F);
        }

        if (var2 >= 0F) {
            var2 = MathHelper.sin((var2 /= var3.hurtDuration) * var2 * var2 * var2
                    * (float) Math.PI);
            var1 = var3.hurtDir;
            GL11.glRotatef(-var3.hurtDir, 0F, 1F, 0F);
            GL11.glRotatef(-var2 * 14F, 0F, 0F, 1F);
            GL11.glRotatef(var1, 0F, 1F, 0F);
        }
    }

    public final void setLighting(boolean var1) {
        if (!var1) {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_LIGHT0);
        } else {
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_LIGHT0);
            GL11.glEnable(2903);
            GL11.glColorMaterial(1032, 5634);
            float var4 = 0.7F;
            float var2 = 0.3F;
            Vec3D var3 = new Vec3D(0F, -1F, 0.5F).normalize();
            GL11.glLight(16384, 4611, createBuffer(var3.x, var3.y, var3.z, 0F));
            GL11.glLight(16384, 4609, createBuffer(var2, var2, var2, 1F));
            GL11.glLight(16384, 4608, createBuffer(0F, 0F, 0F, 1F));
            GL11.glLightModel(2899, createBuffer(var4, var4, var4, 1F));
        }
    }

    public void updateFog() {
        Level var1 = minecraft.level;
        Player var2 = minecraft.player;
        GL11.glFog(2918, createBuffer(fogRed, fogBlue, fogGreen, 1F));
        GL11.glNormal3f(0F, -1F, 0F);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Block var5;
        if ((var5 = Block.blocks[var1.getTile((int) var2.x, (int) (var2.y + 0.12F), (int) var2.z)]) != null
                && var5.getLiquidType() != LiquidType.notLiquid) {
            LiquidType var6 = var5.getLiquidType();
            GL11.glFogi(2917, 2048);
            float var3;
            float var7;
            float var8;
            if (var6 == LiquidType.water) {
                GL11.glFogf(2914, 0.1F);
                var7 = 0.4F;
                var8 = 0.4F;
                var3 = 0.9F;
                GL11.glLightModel(2899, createBuffer(var7, var8, var3, 1F));
            } else if (var6 == LiquidType.lava) {
                GL11.glFogf(2914, 2F);
                var7 = 0.4F;
                var8 = 0.3F;
                var3 = 0.3F;
                GL11.glLightModel(2899, createBuffer(var7, var8, var3, 1F));
            }
        } else {
            GL11.glFogi(2917, 9729);
            GL11.glFogf(2915, 0F);
            GL11.glFogf(2916, fogEnd);
            GL11.glLightModel(2899, createBuffer(1F, 1F, 1F, 1F));
        }

        GL11.glEnable(2903);
        GL11.glColorMaterial(1028, 4608);
    }
}
