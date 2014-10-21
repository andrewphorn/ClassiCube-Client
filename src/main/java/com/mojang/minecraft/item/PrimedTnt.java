package com.mojang.minecraft.item;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.particle.SmokeParticle;
import com.mojang.minecraft.particle.TerrainParticle;
import com.mojang.minecraft.player.Player;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.util.MathHelper;

public class PrimedTnt extends Entity {
    public static final long serialVersionUID = 0L;
    public int life = 0;
    private float xd;
    private float yd;
    private float zd;
    private boolean defused;

    public PrimedTnt(Level level, float x, float y, float z) {
        super(level);

        setSize(0.98F, 0.98F);

        heightOffset = bbHeight / 2F;

        setPos(x, y, z);

        float unknown0 = (float) (Math.random() * Math.PI * 2D);

        xd = -MathHelper.sin(unknown0 * (float) Math.PI / 180F) * 0.02F;
        yd = 0.2F;
        zd = -MathHelper.cos(unknown0 * (float) Math.PI / 180F) * 0.02F;

        makeStepSound = false;

        life = 40;

        xo = x;
        yo = y;
        zo = z;
    }

    @Override
    public void hurt(Entity entity, int damage) {
        if (!removed) {
            super.hurt(entity, damage);

            if (entity instanceof Player) {
                remove();
                Item item = new Item(level, x, y, z, Block.TNT.id);
                level.addEntity(item);
            }

        }
    }

    @Override
    public boolean isPickable() {
        return !removed;
    }

    @Override
    public void playerTouch(Entity entity) {
        if (defused) {
            Player player = (Player) entity;
            if (player.addResource(Block.TNT.id)) {
                TakeEntityAnim takeEntityAnim = new TakeEntityAnim(level, this, player);
                level.addEntity(takeEntityAnim);
                remove();
            }

        }
    }

    @Override
    public void render(TextureManager textureManager, float delta) {
        int textureID = textureManager.load("/terrain.png");

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        float brightness = level.getBrightness((int) x, (int) y, (int) z);

        GL11.glPushMatrix();
        GL11.glColor4f(brightness, brightness, brightness, 1F);
        GL11.glTranslatef(xo + (x - xo) * delta - 0.5F,
                yo + (y - yo) * delta - 0.5F,
                zo + (z - zo) * delta - 0.5F);
        GL11.glPushMatrix();

        ShapeRenderer shapeRenderer = ShapeRenderer.instance;

        Block.TNT.renderPreview(shapeRenderer);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor4f(1F, 1F, 1F, (life / 4 + 1) % 2 * 0.4F);

        if (life <= 16) {
            GL11.glColor4f(1F, 1F, 1F, (life + 1) % 2 * 0.6F);
        }

        if (life <= 2) {
            GL11.glColor4f(1F, 1F, 1F, 0.9F);
        }

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        Block.TNT.renderPreview(shapeRenderer);

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;

        yd -= 0.04F;

        move(xd, yd, zd);

        xd *= 0.98F;
        yd *= 0.98F;
        zd *= 0.98F;

        if (onGround) {
            xd *= 0.7F;
            zd *= 0.7F;
            yd *= -0.5F;
        }

        if (!defused) {
            if (life-- > 0) {
                SmokeParticle smokeParticle = new SmokeParticle(level, x, y + 0.6F, z);
                level.particleEngine.spawnParticle(smokeParticle);
            } else {
                remove();

                Random random = new Random();
                float radius = 4F;
                level.explode(null, x, y, z, radius);

                for (int i = 0; i < 100; i++) {
                    float unknown0 = (float) random.nextGaussian() * radius / 4F;
                    float unknown1 = (float) random.nextGaussian() * radius / 4F;
                    float unknown2 = (float) random.nextGaussian() * radius / 4F;
                    float unknown3 = MathHelper.sqrt(unknown0 * unknown0
                            + unknown1 * unknown1 + unknown2 * unknown2);
                    float unknown4 = unknown0 / unknown3 / unknown3;
                    float unknown5 = unknown1 / unknown3 / unknown3;

                    unknown3 = unknown2 / unknown3 / unknown3;

                    TerrainParticle terrainParticle = new TerrainParticle(level, x + unknown0, y
                            + unknown1, z + unknown2, unknown4, unknown5, unknown3, Block.TNT);
                    level.particleEngine.spawnParticle(terrainParticle);
                }
            }
        }
    }
}
