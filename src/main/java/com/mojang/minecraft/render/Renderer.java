package com.mojang.minecraft.render;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.MovingObjectPosition;
import com.mojang.minecraft.SelectionBoxData;
import com.mojang.minecraft.ThirdPersonMode;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.physics.AABB;
import com.mojang.minecraft.physics.CustomAABB;
import com.mojang.minecraft.player.Player;
import com.mojang.util.ColorCache;
import com.mojang.util.MathHelper;
import com.mojang.util.Vec3D;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public final class Renderer {
    // Chunk update timing
    public static final int MIN_CHUNK_UPDATES_PER_FRAME = 4;
    public int dynamicChunkUpdateLimit = MIN_CHUNK_UPDATES_PER_FRAME;
    public boolean everBackedOffFromChunkUpdates= false;
    
    // fog
    private static final float LAVA_FOG_DENSITY = 1.8F;
    private static final float WATER_FOG_DENSITY = 0.1F;
    public float fogRed, fogBlue, fogGreen;
    public float fogEnd = 0F;
    
    public Minecraft minecraft;
    public float fogColorMultiplier = 1F;
    public boolean displayActive = false;
    public HeldBlock heldBlock;
    public int levelTicks;
    public Entity entity = null;
    public Random random = new Random();
    private final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

    public Renderer(Minecraft minecraft) {
        this.minecraft = minecraft;
        heldBlock = new HeldBlock(minecraft);
    }

    public void applyBobbing(float delta, boolean isEnabled) {
        Player player = minecraft.player;
        float var2 = player.walkDist - player.walkDistO;
        var2 = player.walkDist + var2 * delta;
        float newBob = player.oBob + (player.bob - player.oBob) * delta;
        float newTilt = player.oTilt + (player.tilt - player.oTilt) * delta;
        if (isEnabled) {
            GL11.glTranslatef(MathHelper.sin(var2 * (float) Math.PI) * newBob * 0.5F,
                    -Math.abs(MathHelper.cos(var2 * (float) Math.PI) * newBob), 0F);
            GL11.glRotatef(MathHelper.sin(var2 * (float) Math.PI) * newBob * 3F, 0F, 0F, 1F);
            GL11.glRotatef(Math.abs(MathHelper.cos(var2 * (float) Math.PI + 0.2F) * newBob) * 5F, 1F,
                    0F, 0F);
        }
        GL11.glRotatef(newTilt, 1F, 0F, 0F);
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

    public Vec3D getPlayerVector(float delta) {
        Player player = minecraft.player;
        float newX = player.xo + (player.x - player.xo) * delta;
        float newY = player.yo + (player.y - player.yo) * delta;
        float newZ = player.zo + (player.z - player.zo) * delta;
        return new Vec3D(newX, newY, newZ);
    }

    // SURVIVAL: hurt effect
    public void hurtEffect(float delta) {
        Player player = minecraft.player;
        float var2 = player.hurtTime - delta;
        if (player.health <= 0) {
            delta += player.deathTime;
            GL11.glRotatef(40F - 8000F / (delta + 200F), 0F, 0F, 1F);
        }

        if (var2 >= 0F) {
            var2 /= player.hurtDuration;
            var2 = MathHelper.sin(var2 * var2 * var2 * var2 * (float) Math.PI);
            delta = player.hurtDir;
            GL11.glRotatef(-player.hurtDir, 0F, 1F, 0F);
            GL11.glRotatef(-var2 * 14F, 0F, 0F, 1F);
            GL11.glRotatef(delta, 0F, 1F, 0F);
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

    // Sets fog parameters. Note: does NOT enable GL_FOG
    public void updateFog() {
        Player player = minecraft.player;
        GL11.glFog(GL11.GL_FOG_COLOR, createBuffer(fogRed, fogBlue, fogGreen, 1F));
        GL11.glNormal3f(0F, -1F, 0F);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Block headBlock = Block.blocks[minecraft.level.getTile((int) player.x, (int) (player.y + 0.12F), (int) player.z)];
        if (headBlock != null && headBlock.getLiquidType() != LiquidType.notLiquid) {
            // Colored fog when inside water/lava
            LiquidType liquidType = headBlock.getLiquidType();
            GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
            if (liquidType == LiquidType.water) {
                GL11.glFogf(GL11.GL_FOG_DENSITY, WATER_FOG_DENSITY);
                float red = 0.4F;
                float green = 0.4F;
                float blue = 0.9F;
                GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, createBuffer(red, green, blue, 1F));
            } else if (liquidType == LiquidType.lava) {
                GL11.glFogf(GL11.GL_FOG_DENSITY, LAVA_FOG_DENSITY);
                float red = 0.4F;
                float green = 0.3F;
                float blue = 0.3F;
                GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, createBuffer(red, green, blue, 1F));
            }
        } else {
            // Regular fog, when not in liquid
            GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
            GL11.glFogf(GL11.GL_FOG_START, fogEnd / 2);
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
        if (selected != null && settings.thirdPersonMode == ThirdPersonMode.FRONT_FACING) {
            cameraDistance = -(selected.vec.distance(getPlayerVector(delta)) - 0.51F);
            if (cameraDistance < -5.1F) {
                cameraDistance = -5.1F;
            }
        }

        if (settings.thirdPersonMode == ThirdPersonMode.NONE) {
            GL11.glTranslatef(0F, 0F, -0.1F);
        } else {
            GL11.glTranslatef(0F, 0F, cameraDistance);
        }
        if (settings.thirdPersonMode == ThirdPersonMode.FRONT_FACING) {
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

    public void drawWireframeBox(AABB aabb) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0F, 0F, 0F, 0.4F);
        GL11.glLineWidth(2F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3f(aabb.maxX, aabb.maxY, aabb.maxZ);
        GL11.glVertex3f(aabb.minX, aabb.maxY, aabb.maxZ);
        GL11.glVertex3f(aabb.minX, aabb.maxY, aabb.minZ);
        GL11.glVertex3f(aabb.maxX, aabb.maxY, aabb.minZ);
        GL11.glVertex3f(aabb.maxX, aabb.maxY, aabb.maxZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3f(aabb.maxX, aabb.minY, aabb.maxZ);
        GL11.glVertex3f(aabb.minX, aabb.minY, aabb.maxZ);
        GL11.glVertex3f(aabb.minX, aabb.minY, aabb.minZ);
        GL11.glVertex3f(aabb.maxX, aabb.minY, aabb.minZ);
        GL11.glVertex3f(aabb.maxX, aabb.minY, aabb.maxZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3f(aabb.maxX, aabb.maxY, aabb.maxZ);
        GL11.glVertex3f(aabb.maxX, aabb.minY, aabb.maxZ);
        GL11.glVertex3f(aabb.minX, aabb.maxY, aabb.maxZ);
        GL11.glVertex3f(aabb.minX, aabb.minY, aabb.maxZ);
        GL11.glVertex3f(aabb.minX, aabb.maxY, aabb.minZ);
        GL11.glVertex3f(aabb.minX, aabb.minY, aabb.minZ);
        GL11.glVertex3f(aabb.maxX, aabb.maxY, aabb.minZ);
        GL11.glVertex3f(aabb.maxX, aabb.minY, aabb.minZ);
        GL11.glEnd();

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    public void drawSelectionCuboid(SelectionBoxData box, ShapeRenderer shapeRenderer) {
        CustomAABB bounds = box.bounds;
        ColorCache color = box.color;
        GL11.glColor4f(color.R, color.G, color.B, color.A);

        // Front Face
        // Bottom Left
        shapeRenderer.begin();
        shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.minZ);
        // Bottom Right
        shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.minZ);
        // Top Right
        shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.minZ);
        // Top Left
        shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.minZ);

        // Back Face
        // Bottom Right
        shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ);
        // Top Right
        shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.maxZ);
        // Top Left
        shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.maxZ);
        // Bottom Left
        shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.maxZ);

        // Top Face
        // Top Left
        // Bottom Left
        shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.maxZ);
        shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.minZ);
        // Bottom Right
        shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.minZ);
        // Top Right
        shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.maxZ);

        // Bottom Face
        // Top Right
        shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ);
        // Top Left
        shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.maxZ);
        // Bottom Left
        shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.minZ);
        // Bottom Right
        shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.minZ);

        // Right face
        // Bottom Right
        shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.maxZ);
        // Top Right
        shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.maxZ);
        // Top Left
        shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.minZ);
        // Bottom Left
        shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.minZ);

        // Left Face
        // Bottom Left
        shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ);
        // Bottom Right
        shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.minZ);
        // Top Right
        shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.minZ);
        // Top Left
        shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.maxZ);
        shapeRenderer.end();

        GL11.glColor4f(color.R, color.G, color.B, color.A + 0.2F);

        shapeRenderer.startDrawing(3);
        shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ);
        shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.maxZ);
        shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.minZ);
        shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.minZ);
        shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ);
        shapeRenderer.end();

        shapeRenderer.startDrawing(3);
        shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.maxZ);
        shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.maxZ);
        shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.minZ);
        shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.minZ);
        shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.maxZ);
        shapeRenderer.end();

        shapeRenderer.startDrawing(1);
        shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ);
        shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.maxZ);
        shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.maxZ);
        shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.maxZ);
        shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.minZ);
        shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.minZ);
        shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.minZ);
        shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.minZ);
        shapeRenderer.end();
    }
}
