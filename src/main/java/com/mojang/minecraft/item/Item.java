package com.mojang.minecraft.item;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.player.Player;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.minecraft.render.texture.Textures;
import com.mojang.util.MathHelper;

public class Item extends Entity {
    private static final ItemModel[] models = new ItemModel[256];
    private float xd;
    private float yd;
    private float zd;
    private float rot;
    private final int resource;
    private int tickCount;
    private int age = 0;

    public Item(Level level, float x, float y, float z, int block) {
        super(level);

        setSize(0.25F, 0.25F);

        heightOffset = bbHeight / 2F;

        setPos(x, y, z);

        resource = block;

        rot = (float) (Math.random() * 360D);

        xd = (float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D);
        yd = 0.2F;
        zd = (float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D);

        makeStepSound = false;
    }

    public static void initModels() {
        for (int validBlocks = 0; validBlocks < 256; validBlocks++) {
            Block block = Block.blocks[validBlocks];

            if (block != null) {
                models[validBlocks] = new ItemModel(block.textureId);
            }
        }

    }

    @Override
    public void playerTouch(Entity entity) {
        Player player = (Player) entity;

        if (player.addResource(resource)) {
            TakeEntityAnim takeEntityAnim = new TakeEntityAnim(level, this, player);
            level.addEntity(takeEntityAnim);
            remove();
        }

    }

    @Override
    public void render(TextureManager textureManager, float delta) {
        textureId = textureManager.load(Textures.TERRAIN);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        float brightness = level.getBrightness((int) x, (int) y, (int) z);
        float unknown1 = rot + (tickCount + delta) * 3F;

        GL11.glPushMatrix();
        GL11.glColor4f(brightness, brightness, brightness, 1F);

        float unknown2 = (brightness = MathHelper.sin(unknown1 / 10F)) * 0.1F + 0.1F;

        GL11.glTranslatef(xo + (x - xo) * delta, yo + (y - yo) * delta + unknown2, zo
                + (z - zo) * delta);
        GL11.glRotatef(unknown1, 0F, 1F, 0F);

        models[resource].generateList();

        brightness = (brightness = (brightness = brightness * 0.5F + 0.5F) * brightness)
                * brightness;

        GL11.glColor4f(1F, 1F, 1F, brightness * 0.4F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        models[resource].generateList();

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
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

        tickCount++;

        age++;

        if (age >= 6000) {
            remove();
        }
    }
}
