package com.mojang.minecraft.player;

import java.awt.image.BufferedImage;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.Entity;
import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.HackState;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.level.tile.BlockModelRenderer;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.model.HumanoidModel;
import com.mojang.minecraft.model.Model;
import static com.mojang.minecraft.net.NetworkPlayer.isInteger;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.util.MathHelper;

public class Player extends Mob {
    private int flyTrig = 0;

    private int noclipTrig = 0;
    private int speedTrig = 0;
    private int jumpCount = 0;

    boolean HacksEnabled;
    boolean isOnIce = false;

    public static boolean noPush = false;

    public transient GameSettings settings;
    public static final long serialVersionUID = 0L;

    public static final int MAX_HEALTH = 20;

    public static final int MAX_ARROWS = 99;

    public transient InputHandler input;

    public Inventory inventory = new Inventory();

    public byte userType = 0;

    public float oBob;

    public float bob;

    public int score = 0;

    public int arrows = 20;

    private static int newTextureId = -1;

    public static BufferedImage newTexture;

    public Player(Level var1, GameSettings gs) {
        super(var1);
        if (var1 != null) {
            var1.player = this;
            var1.removeEntity(this);
            var1.addEntity(this);
        }

        heightOffset = 1.62F;
        health = 20;
        modelName = "humanoid";
        rotOffs = 180F;
        ai = new Player$1(this);
        settings = gs;
    }

    public boolean addResource(int var1) {
        return inventory.addResource(var1);
    }

    @Override
    public void aiStep() {
        if (settings.HackType == 0 || !(HackState.fly || HackState.speed || HackState.noclip)
                && input.canMove) {
            inventory.tick();
            oBob = bob;
            input.updateMovement(0); // for the event that hacktype
            // is 1 but server has -hax.
            // Otherwise you won't be able to move without manually setting
            // your hacktype back to 'normal' in the options menu.
            super.aiStep();

            float var1 = MathHelper.sqrt(xd * xd + zd * zd);
            float var2 = (float) Math.atan(-yd * 0.2F) * 15F;
            if (var1 > 0.1F) {
                var1 = 0.1F;
            }

            if (!onGround || health <= 0) {
                var1 = 0F;
            }

            if (onGround || health <= 0) {
                var2 = 0F;
            }
            bob += (var1 - bob) * 0.4F;
            tilt += (var2 - tilt) * 0.8F;
            List<?> var3;
            if (health > 0
                    && (var3 = level.findEntities(this, boundingBox.grow(1F, 0F, 1F))) != null) {
                for (int var4 = 0; var4 < var3.size(); ++var4) {
                    ((Entity) var3.get(var4)).playerTouch(this);
                }
            }
        } else {
            oBob = bob;
            HacksEnabled = settings.HacksEnabled;
            input.updateMovement(1);
            super.aiStep();
            float fx = xd;
            float fy = yd;
            float fz = zd;
            if (fx > 0.1f) {
                fx = 0.1f;
            }
            if (fy > 0.1f) {
                fy = 0.1f;
            }
            if (fz > 0.1f) {
                fz = 0.1f;
            }

            if (fx < -0.1f) {
                fx = -0.1f;
            }
            if (fy < -0.1f) {
                fy = -0.1f;
            }
            if (fz < -0.1f) {
                fz = -0.1f;
            }

            float aaa = MathHelper.sqrt(fx * fx + fz * fz);
            float bbb = (float) Math.atan(-fy * 0.2F) * 15F;
            bob += (aaa - bob) * 0.4F;
            tilt += (bbb - tilt) * 0.8F;

            speedTrig = -1; // speed
            flyTrig = -1; // fly
            noclipTrig = -1; // noclip
            // -1 = yes, 1 = no

            if (HackState.fly) {
                flyTrig = -1;
            } else {
                flyTrig = 1;
            }

            if (HackState.speed) {
                speedTrig = -1;
            } else {
                speedTrig = 1;
            }

            if (HackState.noclip) {
                noclipTrig = -1;
            } else {
                noclipTrig = 1;
            }
            int i = 0;
            int j = 0;
            int k = 1;
            float f1 = 1F;
            oBob = bob;
            if (flyingMode && flyTrig < 1) {
                i = 1;
            }
            if (noPhysics && noclipTrig < 0) {
                j = 1;
            }
            if (input.mult > 1F && speedTrig < 1) {
                f1 = input.mult;
            }

            if (!HacksEnabled) {
                i = 0;
                j = 0;
                k = 0;
                f1 = 1F;
            }

            if (flyTrig > 0 || speedTrig > 0) {
                k = 0;
            }

            xo = x;
            yo = y;
            zo = z;
            xRotO = xRot;
            yRotO = yRot;

            boolean bool1 = isInWater();
            boolean bool2 = isInLava();
            boolean bool3 = isInOrOnRope();

            float f2 = 0F;

            // this.input.updateMovement(1);

            if (i != 0 || j != 0) {
                yd = input.elevate;
            }

            if (onGround || i != 0) {
                jumpCount = 0;
            }

            if (input.jump) {
                if (bool1) {
                    yd += 0.08F;
                } else if (bool3) {
                    yd += 0.06F;
                } else if (bool2) {
                    yd += 0.07F;
                } else if (i != 0) {
                    yd += 0.05F;
                } else if (onGround) {
                    if (!input.fall) {
                        if (!HacksEnabled && k != 0) {
                            yd = 0.48F;
                        } else {
                            yd = 0.35F;
                        }
                        input.fall = true;
                        jumpCount += 1;
                    }
                } else if (HacksEnabled && !input.fall && k != 0 && jumpCount < 3) {
                    yd = 0.5F;
                    input.fall = true;
                    jumpCount += 1;
                }
            } else {
                input.fall = false;
            }

            if (HacksEnabled && k != 0 && jumpCount > 1) {
                f1 *= 2.5F;
                if (!isOnIce) {
                    f1 *= jumpCount;
                } else {
                    jumpCount = 0;
                }
            }

            if (bool1 && i == 0 && j == 0) {
                f2 = y;
                super.moveRelative(input.strafe, input.move, 0.02F * f1);
                super.move(xd * f1, yd * f1, zd * f1);
                xd *= 0.8F;
                yd *= 0.8F;
                zd *= 0.8F;
                yd = (float) (yd - 0.02D);
                if (horizontalCollision && isFree(xd, yd + 0.6F - y + f2, zd)) {
                    yd = 0.3F;
                }
                return;
            }

            if (bool2 && i == 0 && j == 0) {
                f2 = y;
                super.moveRelative(input.strafe, input.move, 0.02F * f1);
                super.move(xd * f1, yd * f1, zd * f1);
                xd *= 0.5F;
                yd *= 0.5F;
                zd *= 0.5F;
                yd = (float) (yd - 0.02D);
                if (horizontalCollision && isFree(xd, yd + 0.6F - y + f2, zd)) {
                    yd = 0.3F;
                }
                return;
            }

            if (i != 0) {
                f1 = (float) (f1 * 1.2D);
            }

            float f4 = 0F;
            float f3 = 0F;
            if (j != 0) {
                f4 = i != 0 ? 0.72F : 0.71F;
                if (i != 0) {
                    yd = input.elevate;
                }
                f3 = 0.2F;
            } else if (onGround || jumpCount > 0 || i != 0) {
                f3 = 0.1F;
            } else {
                f3 = 0.02F;
            }

            super.moveRelative(input.strafe, input.move, f3 * f1);

            if (j != 0 && (xd != 0F || zd != 0F)) {
                super.moveTo(x + xd, y + yd - f4, z + zd, yRot, xRot);
                yo = y += f4;
            } else {
                super.move(xd * f1, yd * f1, zd * f1);
            }
            int var1 = level.getTile((int) x, (int) (y - 2.12F), (int) z);
            if (Block.blocks[var1] != Block.ICE) {
                if (jumpCount == 0) {
                    isOnIce = false;
                }
                f2 = 0.6F;
                xd *= 0.91F;
                yd *= 0.98F;
                zd *= 0.91F;

                if (i != 0) {
                    yd *= f2 / 4F;
                    walkDist = 0F;
                } else {
                    yd = (float) (yd - 0.01D);
                }
                xd *= f2;
                zd *= f2;
                tilt = 0f;
            } else {
                isOnIce = true;
            }
        }
    }

    @Override
    public void awardKillScore(Entity var1, int score) {
        this.score += score;
    }

    @Override
    public void bindTexture(TextureManager textureManager) {
        if (newTexture != null) {
            BufferedImage var2 = newTexture;
            int[] var3 = new int[512];
            var2.getRGB(32, 0, 32, 16, var3, 0, 32);
            int var5 = 0;

            boolean var10001;
            while (true) {
                if (var5 >= var3.length) {
                    var10001 = false;
                    break;
                }
                if (var3[var5] >>> 24 < 128) {
                    var10001 = true;
                    break;
                }
                ++var5;
            }
            hasHair = var10001;

            if (modelName.equals("humanoid")) {
                newTextureId = textureManager.load(newTexture);
            }
            newTexture = null;
        }
        if (isInteger(modelName)) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.load("/terrain.png"));
            return;
        } else if (!modelName.startsWith("humanoid")) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                    textureManager.load("/mob/" + modelName.replace('.', '_') + ".png"));
            return;
        }
        int var2;
        if (newTextureId < 0) {
            var2 = textureManager.load("/char.png");
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, var2);
        } else {
            var2 = newTextureId;
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, var2);
        }
    }

    @Override
    public void die(Entity var1) {
        setSize(0.2F, 0.2F);
        this.setPos(x, y, z);
        yd = 0.1F;
        if (var1 != null) {
            xd = -MathHelper.cos((hurtDir + yRot) * (float) Math.PI / 180F) * 0.1F;
            zd = -MathHelper.sin((hurtDir + yRot) * (float) Math.PI / 180F) * 0.1F;
        } else {
            xd = zd = 0F;
        }

        heightOffset = 0.1F;
    }

    public HumanoidModel getModel() {
        return (HumanoidModel) modelCache.getModel(modelName);
    }

    public int getScore() {
        return score;
    }

    @Override
    public void hurt(Entity var1, int var2) {
        if (!level.creativeMode) {
            super.hurt(var1, var2);
        }

    }

    @Override
    public boolean isCreativeModeAllowed() {
        return true;
    }

    @Override
    public boolean isShootable() {
        return true;
    }

    public void releaseAllKeys() {
        input.resetKeys();
        input.canMove = false;
    }

    @Override
    public void remove() {
    }

    @Override
    public void render(TextureManager textureManager, float var2) {
        if (settings.thirdPersonMode == 0) {
            return;
        }
        if (modelName != null) {
            float var3;
            if ((var3 = attackTime - var2) < 0F) {
                var3 = 0F;
            }

            while (yBodyRotO - yBodyRot < -180F) {
                yBodyRotO += 360F;
            }

            while (yBodyRotO - yBodyRot >= 180F) {
                yBodyRotO -= 360F;
            }

            while (xRotO - xRot < -180F) {
                xRotO += 360F;
            }

            while (xRotO - xRot >= 180F) {
                xRotO -= 360F;
            }

            while (yRotO - yRot < -180F) {
                yRotO += 360F;
            }

            while (yRotO - yRot >= 180F) {
                yRotO -= 360F;
            }

            float var4 = yBodyRotO + (yBodyRot - yBodyRotO) * var2;
            float var5 = oRun + (run - oRun) * var2;
            float var6 = yRotO + (yRot - yRotO) * var2;
            float var7 = xRotO + (xRot - xRotO) * var2;
            var6 -= var4;
            GL11.glPushMatrix();
            float var8 = animStepO + (animStep - animStepO) * var2;
            ColorCache c = getBrightnessColor();

            GL11.glColor3f(c.R, c.G, c.B);
            float var9 = 0.0625F; // 1 / 16
            float var10 = -Math.abs(MathHelper.cos(var8 * 0.6662F)) * 5F * var5 * bobStrength - 23F;
            GL11.glTranslatef(xo + (x - xo) * var2, yo + (y - yo) * var2 - 1.62F + renderOffset, zo
                    + (z - zo) * var2);
            float var11;
            if ((var11 = hurtTime - var2) > 0F || health <= 0) {
                if (var11 < 0F) {
                    var11 = 0F;
                } else {
                    var11 /= hurtDuration;
                    var11 = MathHelper.sin(var11 * var11 * var11 * var11 * (float) Math.PI) * 14F;
                }

                float var12 = 0F;
                if (health <= 0) {
                    var12 = (deathTime + var2) / 20F;
                    var11 += var12 * var12 * 800F;
                    if (var11 > 90F) {
                        var11 = 90F;
                    }
                }

                var12 = hurtDir;
                GL11.glRotatef(180F - var4 + rotOffs + 45, 0F, 1F, 0F);
                GL11.glScalef(1F, 1F, 1F);
                GL11.glRotatef(-var12, 0F, 1F, 0F);
                GL11.glRotatef(-var11, 0F, 0F, 1F);
                GL11.glRotatef(var12, 0F, 1F, 0F);
                GL11.glRotatef(-(180F - var4 + rotOffs), 0F, 1F, 0F);
            }

            GL11.glTranslatef(0F, -var10 * var9, 0F);
            GL11.glScalef(1F, -1F, 1F);
            GL11.glRotatef(180F - var4 + rotOffs, 0F, 1F, 0F);
            if (!allowAlpha) {
                GL11.glDisable(GL11.GL_ALPHA_TEST);
            } else {
                GL11.glDisable(GL11.GL_CULL_FACE);
            }

            GL11.glScalef(-1F, 1F, 1F);
            modelCache.getModel(modelName).attackOffset = var3 / 5F;
            bindTexture(textureManager);
            renderModel(textureManager, var8, var2, var5, var6, var7, var9);
            if (invulnerableTime > invulnerableDuration - 10) {
                GL11.glColor4f(1F, 1F, 1F, 0.75F);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                bindTexture(textureManager);
                renderModel(textureManager, var8, var2, var5, var6, var7, var9);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            if (allowAlpha) {
                GL11.glEnable(GL11.GL_CULL_FACE);
            }

            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPopMatrix();
        }
    }

    BlockModelRenderer block;

    @Override
    public void renderModel(TextureManager var1, float var2, float var3, float var4, float var5,
            float var6, float var7) {
        if (isInteger(modelName)) {
            try {
                block = new BlockModelRenderer(Block.blocks[Integer.parseInt(modelName)].textureId);
                GL11.glPushMatrix();
                GL11.glTranslatef(-0.5F, 0.4F, -0.5F);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, var1.load("/terrain.png"));
                block.renderPreview(ShapeRenderer.instance);
                GL11.glPopMatrix();
            } catch (Exception e) {
                modelName = "humanoid";
            }
            return;
        }
        Model model = modelCache.getModel(modelName);
        if (hasHair && model instanceof HumanoidModel) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            HumanoidModel modelHeadwear = null;
            (modelHeadwear = (HumanoidModel) model).headwear.yaw = modelHeadwear.head.yaw;
            modelHeadwear.headwear.pitch = modelHeadwear.head.pitch;
            modelHeadwear.headwear.render(var7);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
        modelCache.getModel(modelName).render(var2, var4, tickCount + var3, var5, var6, var7);
    }

    @Override
    public void resetPos() {
        heightOffset = 1.62F;
        setSize(0.6F, 1.8F);
        super.resetPos();
        if (level != null) {
            level.player = this;
        }

        health = 20;
        deathTime = 0;
    }

    public void setKey(int var1, boolean var2) {
        input.setKeyState(var1, var2);
    }
}