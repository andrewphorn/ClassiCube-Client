package com.mojang.minecraft.render;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.MovingObjectPosition;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.player.Player;
import com.mojang.util.MathHelper;
import com.mojang.util.Vec3D;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public final class Renderer {
    // TODO: adaptive chunk update rate, based on framerate
    public static final int MAX_CHUNK_UPDATES_PER_FRAME = 4;

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
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0D, var1, var2, 0D, 100D, 300D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
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
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
            float ambientBrightness = 0.7F;
            float diffuseBrightness = 0.3F;
            Vec3D sunPosition = new Vec3D(0F, -1F, 0.5F).normalize();
            GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION,
                    createBuffer(sunPosition.x, sunPosition.y, sunPosition.z, 0F));
            GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE,
                    createBuffer(diffuseBrightness, diffuseBrightness, diffuseBrightness, 1F));
            GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, createBuffer(0F, 0F, 0F, 1F));
            GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT,
                    createBuffer(ambientBrightness, ambientBrightness, ambientBrightness, 1F));
        }
    }

    public void updateFog() {
        Player player = minecraft.player;
        GL11.glFog(GL11.GL_FOG_COLOR, createBuffer(fogRed, fogBlue, fogGreen, 1F));
        GL11.glNormal3f(0F, -1F, 0F);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Block headBlock = Block.blocks[minecraft.level.getTile((int) player.x, (int) (player.y + 0.12F), (int) player.z)];
        if (headBlock != null && headBlock.getLiquidType() != LiquidType.notLiquid) {
            // Colored fog when inside water/lava
            LiquidType var6 = headBlock.getLiquidType();
            GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
            float red, green, blue;
            if (var6 == LiquidType.water) {
                GL11.glFogf(GL11.GL_FOG_DENSITY, 0.1F);
                red = 0.4F;
                green = 0.4F;
                blue = 0.9F;
                GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, createBuffer(red, green, blue, 1F));
            } else if (var6 == LiquidType.lava) {
                GL11.glFogf(GL11.GL_FOG_DENSITY, 2F);
                red = 0.4F;
                green = 0.3F;
                blue = 0.3F;
                GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, createBuffer(red, green, blue, 1F));
            }
        } else {
            // Regular fog, when not in liquid
            GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
            GL11.glFogf(GL11.GL_FOG_START, 0F);
            GL11.glFogf(GL11.GL_FOG_END, fogEnd);
            GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, createBuffer(1F, 1F, 1F, 1F));
        }

        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT);
    }

    public void setCamera(float delta, MovingObjectPosition selected) {
        GameSettings settings = minecraft.settings;
        Player player = minecraft.player;
        applyBobbing(delta, settings.viewBobbing);

        float cameraDistance = -5.1F;
        if (selected != null && settings.thirdPersonMode == 2) {
            cameraDistance = -(selected.vec.distance(getPlayerVector(delta)) - 0.51F);
            if (cameraDistance < -5.1F) {
                cameraDistance = -5.1F;
            }
        }

        if (settings.thirdPersonMode == 0) {
            GL11.glTranslatef(0F, 0F, -0.1F);
        } else {
            GL11.glTranslatef(0F, 0F, cameraDistance);
        }
        if (settings.thirdPersonMode == 2) {
            GL11.glRotatef(-player.xRotO + (player.xRot - player.xRotO) * delta, 1F, 0F, 0F);
            GL11.glRotatef(player.yRotO + (player.yRot - player.yRotO) * delta + 180, 0F, 1F, 0F);
        } else {
            GL11.glRotatef(player.xRotO + (player.xRot - player.xRotO) * delta, 1F, 0F, 0F);
            GL11.glRotatef(player.yRotO + (player.yRot - player.yRotO) * delta, 0F, 1F, 0F);
        }
        float cameraX = player.xo + (player.x - player.xo) * delta;
        float cameraY = player.yo + (player.y - player.yo) * delta;
        float cameraZ = player.zo + (player.z - player.zo) * delta;
        GL11.glTranslatef(-cameraX, -cameraY, -cameraZ);
    }

    public void drawWeather(float delta, ShapeRenderer shapeRenderer) {
        // set up OpenGL state for drawing weather
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glNormal3f(0F, 1F, 0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float speed = 1F;
        if (minecraft.isRaining) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.textureManager.load("/rain.png"));
        } else if (minecraft.isSnowing) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, minecraft.textureManager.load("/snow.png"));
            speed = 0.2F;
        }
        int playerX = (int) minecraft.player.x;
        int playerY = (int) minecraft.player.y;
        int playerZ = (int) minecraft.player.z;
        // Go through all tile columns within 5 blocks of the player
        for (int x = playerX - 5; x <= playerX + 5; ++x) {
            for (int z = playerZ - 5; z <= playerZ + 5; ++z) {
                int groundLevel = minecraft.level.getHighestTile(x, z);
                int lowestTile = playerY - 5;
                int highestTile = playerY + 5;

                if (lowestTile < groundLevel) {
                    lowestTile = groundLevel;
                }
                if (highestTile < groundLevel) {
                    highestTile = groundLevel;
                }
                if (lowestTile == highestTile) {
                    // No weather needs to be drawn for this column
                    continue;
                }

                float var74 = ((levelTicks + x * 3121 + z * 418711) % 32 + delta) / 32F * speed;
                float var124 = x + 0.5F - minecraft.player.x;
                float var35 = z + 0.5F - minecraft.player.z;
                float var92 = MathHelper.sqrt(var124 * var124 + var35 * var35) / 5;
                GL11.glColor4f(1F, 1F, 1F, (1F - var92 * var92) * 0.7F);
                shapeRenderer.begin();
                shapeRenderer.vertexUV(x, lowestTile, z, 0F,
                        lowestTile * 2F / 8F + var74 * 2F);
                shapeRenderer.vertexUV(x + 1, lowestTile, z + 1,
                        2F, lowestTile * 2F / 8F + var74 * 2F);
                shapeRenderer.vertexUV(x + 1, highestTile, z + 1,
                        2F, highestTile * 2F / 8F + var74 * 2F);
                shapeRenderer.vertexUV(x, highestTile, z,
                        0F, highestTile * 2F / 8F + var74 * 2F);
                shapeRenderer.vertexUV(x, lowestTile, z + 1,
                        0F, lowestTile * 2F / 8F + var74 * 2F);
                shapeRenderer.vertexUV(x + 1, lowestTile, z,
                        2F, lowestTile * 2F / 8F + var74 * 2F);
                shapeRenderer.vertexUV(x + 1, highestTile, z,
                        2F, highestTile * 2F / 8F + var74 * 2F);
                shapeRenderer.vertexUV(x, highestTile, z + 1,
                        0F, highestTile * 2F / 8F + var74 * 2F);
                shapeRenderer.end();
            }
        }

        // Restore OpenGL state after drawing weather
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
