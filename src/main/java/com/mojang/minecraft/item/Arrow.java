package com.mojang.minecraft.item;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.physics.AABB;
import com.mojang.minecraft.player.Player;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.util.MathHelper;

public class Arrow extends Entity {
    public static final long serialVersionUID = 0L;

    private float xd;
    private float yd;
    private float zd;

    private float xRot;
    private float yRot;

    private float yRotO;
    private float xRotO;
    private boolean hasHit = false;

    private int stickTime = 0;
    private Entity owner;
    private int time = 0;
    private int type = 0;

    private float gravity = 0F;

    private int damage;

    public Arrow(Level level, Entity owner, float x, float y, float z, float unknown0,
            float unknown1, float unknown2) {
        super(level);

        this.owner = owner;

        setSize(0.3F, 0.5F);

        heightOffset = bbHeight / 2F;
        damage = 3;

        if (!(owner instanceof Player)) {
            type = 1;
        } else {
            damage = 7;
        }

        heightOffset = 0.25F;

        float unknown3 = MathHelper.cos(-unknown0 * (float) (Math.PI / 180D) - (float) Math.PI);
        float unknown4 = MathHelper.sin(-unknown0 * (float) (Math.PI / 180D) - (float) Math.PI);

        unknown0 = MathHelper.cos(-unknown1 * (float) (Math.PI / 180D));
        unknown1 = MathHelper.sin(-unknown1 * (float) (Math.PI / 180D));

        slide = false;

        gravity = 1F / unknown2;

        xo -= unknown3 * 0.2F;
        zo += unknown4 * 0.2F;

        x -= unknown3 * 0.2F;
        z += unknown4 * 0.2F;

        xd = unknown4 * unknown0 * unknown2;
        yd = unknown1 * unknown2;
        zd = unknown3 * unknown0 * unknown2;

        setPos(x, y, z);

        unknown3 = MathHelper.sqrt(xd * xd + zd * zd);

        yRotO = yRot = (float) (Math.atan2(xd, zd) * 180D / Math.PI);
        xRotO = xRot = (float) (Math.atan2(yd, unknown3) * 180D / Math.PI);

        makeStepSound = false;
    }

    @Override
    public void awardKillScore(Entity entity, int score) {
        owner.awardKillScore(entity, score);
    }

    public Entity getOwner() {
        return owner;
    }

    @Override
    public void playerTouch(Entity entity) {
        Player player = (Player) entity;

        if (hasHit && owner == player && player.arrows < 99) {
            TakeEntityAnim takeEntityAnim = new TakeEntityAnim(level, this, player);
            level.addEntity(takeEntityAnim);
            player.arrows++;
            remove();
        }
    }

    @Override
    public void render(TextureManager textureManager, float unknown0) {
        textureId = textureManager.load("/item/arrows.png");

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        float brightness = level.getBrightness((int) x, (int) y, (int) z);

        GL11.glPushMatrix();
        GL11.glColor4f(brightness, brightness, brightness, 1F);
        GL11.glTranslatef(xo + (x - xo) * unknown0, yo + (y - yo) * unknown0 - heightOffset / 2F,
                zo + (z - zo) * unknown0);
        GL11.glRotatef(yRotO + (yRot - yRotO) * unknown0 - 90F, 0F, 1F, 0F);
        GL11.glRotatef(xRotO + (xRot - xRotO) * unknown0, 0F, 0F, 1F);
        GL11.glRotatef(45F, 1F, 0F, 0F);

        ShapeRenderer shapeRenderer = ShapeRenderer.instance;

        unknown0 = 0.5F;

        float unknown1 = (type * 10) / 32F;
        float unknown2 = (5 + type * 10) / 32F;
        float unknown3 = 0.15625F;

        float unknown4 = (5 + type * 10) / 32F;
        float unknown5 = (10 + type * 10) / 32F;
        float unknown6 = 0.05625F;

        GL11.glScalef(0.05625F, unknown6, unknown6);

        GL11.glNormal3f(unknown6, 0F, 0F);

        shapeRenderer.begin();
        shapeRenderer.vertexUV(-7F, -2F, -2F, 0F, unknown4);
        shapeRenderer.vertexUV(-7F, -2F, 2F, unknown3, unknown4);
        shapeRenderer.vertexUV(-7F, 2F, 2F, unknown3, unknown5);
        shapeRenderer.vertexUV(-7F, 2F, -2F, 0F, unknown5);
        shapeRenderer.end();

        GL11.glNormal3f(-unknown6, 0F, 0F);

        shapeRenderer.begin();
        shapeRenderer.vertexUV(-7F, 2F, -2F, 0F, unknown4);
        shapeRenderer.vertexUV(-7F, 2F, 2F, unknown3, unknown4);
        shapeRenderer.vertexUV(-7F, -2F, 2F, unknown3, unknown5);
        shapeRenderer.vertexUV(-7F, -2F, -2F, 0F, unknown5);
        shapeRenderer.end();

        shapeRenderer.begin();
        for (int unknown7 = 0; unknown7 < 4; unknown7++) {
            GL11.glRotatef(90F, 1F, 0F, 0F);

            GL11.glNormal3f(0F, -unknown6, 0F);

            shapeRenderer.vertexUV(-8F, -2F, 0F, 0F, unknown1);
            shapeRenderer.vertexUV(8F, -2F, 0F, unknown0, unknown1);
            shapeRenderer.vertexUV(8F, 2F, 0F, unknown0, unknown2);
            shapeRenderer.vertexUV(-8F, 2F, 0F, 0F, unknown2);
        }
        shapeRenderer.end();

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glPopMatrix();
    }

    @Override
    public void tick() {
        time++;

        xRotO = xRot;
        yRotO = yRot;

        xo = x;
        yo = y;
        zo = z;

        if (hasHit) {
            stickTime++;

            if (type == 0) {
                if (stickTime >= 300 && Math.random() < 0.009999999776482582D) {
                    remove();
                }
            } else if (type == 1 && stickTime >= 20) {
                remove();
            }
        } else {
            xd *= 0.998F;
            yd *= 0.998F;
            zd *= 0.998F;

            yd -= 0.02F * gravity;

            int unknown0 = (int) (MathHelper.sqrt(xd * xd + yd * yd + zd * zd) / 0.2F + 1F);

            float x0 = xd / unknown0;
            float y0 = yd / unknown0;
            float z0 = zd / unknown0;

            for (int unknown4 = 0; unknown4 < unknown0 && !collision; unknown4++) {
                AABB unknown5 = boundingBox.expand(x0, y0, z0);

                if (level.getCubes(unknown5).size() > 0) {
                    collision = true;
                }

                for (Entity entity : level.blockMap.getEntities(this, unknown5)) {
                    if (entity.isShootable() && (entity != owner || time > 5)) {
                        entity.hurt(this, damage);
                        collision = true;
                        remove();
                        return;
                    }
                }

                if (!collision) {
                    boundingBox.move(x0, y0, z0);

                    x += x0;
                    y += y0;
                    z += z0;

                    blockMap.moved(this);
                }
            }

            if (collision) {
                hasHit = true;

                xd = yd = zd = 0F;
            }

            if (!hasHit) {
                float unknown6 = MathHelper.sqrt(xd * xd + zd * zd);

                yRot = (float) (Math.atan2(xd, zd) * 180D / Math.PI);

                for (xRot = (float) (Math.atan2(yd, unknown6) * 180D / Math.PI);
                     xRot - xRotO < -180F;
                     xRotO -= 360F) {
                    //System.out.println("test");
                    // TODO: ?.
                }

                while (xRot - xRotO >= 180F) {
                    xRotO += 360F;
                }

                while (yRot - yRotO < -180F) {
                    yRotO -= 360F;
                }

                while (yRot - yRotO >= 180F) {
                    yRotO += 360F;
                }
            }
        }
    }
}
