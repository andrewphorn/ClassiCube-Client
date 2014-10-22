package com.mojang.minecraft.player;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.util.ColorCache;
import com.mojang.minecraft.Entity;
import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.HackState;
import com.mojang.minecraft.ThirdPersonMode;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.level.tile.FireBlock;
import com.mojang.minecraft.level.tile.FlowerBlock;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.model.HumanoidModel;
import com.mojang.minecraft.model.Model;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.util.LogUtil;
import com.mojang.util.MathHelper;

public class Player extends Mob {

    public static final long serialVersionUID = 0L;
    public static final int MAX_HEALTH = 20;
    public static final int MAX_ARROWS = 99;
    public static boolean noPush = false;
    private static int newTextureId = -1;
    public transient GameSettings settings;
    public transient InputHandler input;
    public Inventory inventory = new Inventory();
    public byte userType = 0;
    public float oBob;
    public float bob;
    public int score = 0;
    public int arrows = 20;
    boolean isOnIce = false;

    private int jumpCount = 0;

    public Player(Level level, GameSettings gs) {
        super(level);
        if (level != null) {
            level.player = this;
            level.removeEntity(this);
            level.addEntity(this);
        }

        heightOffset = 1.62F;
        health = 20;
        modelName = "humanoid";
        rotOffs = 180F;
        ai = new PlayerAI(this);
        settings = gs;
    }

    public boolean addResource(int amount) {
        return inventory.addResource(amount);
    }

    @Override
    public void aiStep() {
        if (settings.hackType == 0 || !(HackState.fly || HackState.speed || HackState.noclip)
                && input.canMove) {
            inventory.tick();
            oBob = bob;
            input.updateMovement(0); // for the event that hacktype
            // is 1 but server has -hax.
            // Otherwise you won't be able to move without manually setting
            // your hacktype back to 'normal' in the options menu.
            super.aiStep();

            float horizDist = MathHelper.sqrt(xd * xd + zd * zd);
            float var2 = (float) Math.atan(-yd * 0.2F) * 15F;
            if (horizDist > 0.1F) {
                horizDist = 0.1F;
            }

            if (!onGround || health <= 0) {
                horizDist = 0F;
            }

            if (onGround || health <= 0) {
                var2 = 0F;
            }
            bob += (horizDist - bob) * 0.4F;
            tilt += (var2 - tilt) * 0.8F;
            List<?> neighbourEntities = level.findEntities(this, boundingBox.grow(1F, 0F, 1F));
            if (health > 0 && neighbourEntities != null) {
                for (Object neighbour : neighbourEntities) {
                    ((Entity) neighbour).playerTouch(this);
                }
            }
        } else {
            oBob = bob;
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

            boolean isFlying = false;
            boolean isNoClipping = false;
            boolean isSpeeding = true;
            float speedMult = 1F;
            oBob = bob;
            if (flyingMode && HackState.fly) {
                isFlying = true;
            }
            if (noPhysics && HackState.noclip) {
                isNoClipping = true;
            }
            if (input.mult > 1F && HackState.speed) {
                speedMult = input.mult;
            }

            if (!settings.hacksEnabled) {
                isFlying = false;
                isNoClipping = false;
                isSpeeding = false;
                speedMult = 1F;
            }

            if (!HackState.fly || !HackState.speed) {
                isSpeeding = false;
            }

            xo = x;
            yo = y;
            zo = z;
            xRotO = xRot;
            yRotO = yRot;

            boolean inWater = isInWater();
            boolean inLava = isInLava();
            boolean onRope = isInOrOnRope();

            // this.input.updateMovement(1);
            if (isFlying || isNoClipping) {
                yd = input.elevate;
            }

            if (onGround || isFlying) {
                jumpCount = 0;
            }

            if (input.jump) {
                if (inWater) {
                    yd += 0.08F;
                } else if (onRope) {
                    yd += 0.06F;
                } else if (inLava) {
                    yd += 0.07F;
                } else if (isFlying) {
                    yd += 0.05F;
                } else if (onGround) {
                    if (!input.fall) {
                        if (!settings.hacksEnabled && isSpeeding) {
                            yd = 0.48F;
                        } else {
                            yd = 0.35F;
                        }
                        input.fall = true;
                        jumpCount += 1;
                    }
                } else if (settings.hacksEnabled && !input.fall && isSpeeding && jumpCount < 3) {
                    yd = 0.5F;
                    input.fall = true;
                    jumpCount += 1;
                }
            } else {
                input.fall = false;
            }

            if (settings.hacksEnabled && isSpeeding && jumpCount > 1) {
                speedMult *= 2.5F;
                if (!isOnIce) {
                    speedMult *= jumpCount;
                } else {
                    jumpCount = 0;
                }
            }

            if (inWater && !isFlying && !isNoClipping) {
                float oldY = y;
                super.moveRelative(input.strafe, input.move, 0.02F * speedMult);
                super.move(xd * speedMult, yd * speedMult, zd * speedMult);
                xd *= 0.8F;
                yd *= 0.8F;
                zd *= 0.8F;
                yd = (float) (yd - 0.02D);
                if (horizontalCollision && isFree(xd, yd + 0.6F - y + oldY, zd)) {
                    yd = 0.3F;
                }
                return;
            }

            if (inLava && !isFlying && !isNoClipping) {
                float oldY = y;
                super.moveRelative(input.strafe, input.move, 0.02F * speedMult);
                super.move(xd * speedMult, yd * speedMult, zd * speedMult);
                xd *= 0.5F;
                yd *= 0.5F;
                zd *= 0.5F;
                yd = (float) (yd - 0.02D);
                if (horizontalCollision && isFree(xd, yd + 0.6F - y + oldY, zd)) {
                    yd = 0.3F;
                }
                return;
            }

            if (isFlying) {
                speedMult *= 1.2f;
            }

            float f4 = 0F;
            float speedScale;
            if (isNoClipping) {
                f4 = isFlying ? 0.72F : 0.71F;
                if (isFlying) {
                    yd = input.elevate;
                }
                speedScale = 0.2F;
            } else if (onGround || jumpCount > 0 || isFlying) {
                speedScale = 0.1F;
            } else {
                speedScale = 0.02F;
            }

            super.moveRelative(input.strafe, input.move, speedScale * speedMult);

            if (isNoClipping && (xd != 0F || zd != 0F)) {
                super.moveTo(x + xd, y + yd - f4, z + zd, yRot, xRot);
                yo = y += f4;
            } else {
                super.move(xd * speedMult, yd * speedMult, zd * speedMult);
            }
            int tileBelow = level.getTile((int) x, (int) (y - 2.12F), (int) z);
            if (Block.blocks[tileBelow] != Block.ICE) {
                if (jumpCount == 0) {
                    isOnIce = false;
                }
                float f2 = 0.6F;
                xd *= 0.91F;
                yd *= 0.98F;
                zd *= 0.91F;

                if (isFlying) {
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

    // SURVIVAL: scoring
    @Override
    public void awardKillScore(Entity victim, int score) {
        this.score += score;
    }

    @Override
    public void bindTexture(TextureManager textureManager) {
        if (newTexture != null) {
            hasHair = checkForHat(newTexture);

            //if (modelName.equals("humanoid")) {
            newTextureId = textureManager.load(newTexture);
            //}
            newTexture = null;
        }

        // modelName is a block number
        if (isInteger(modelName)) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.load("/terrain.png"));
            return;
        }

        int boundTextureId;
        if (newTextureId < 0) {
            if (modelName.equals("humanoid") || defaultTexture) {
                boundTextureId = textureManager.load("/char.png");
            } else {
                boundTextureId = textureManager.load("/mob/" + modelName.replace('.', '_') + ".png");
            }
        } else {
            boundTextureId = newTextureId;
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTextureId);
    }

    @Override
    public void die(Entity killedBy) {
        setSize(0.2F, 0.2F);
        this.setPos(x, y, z);
        yd = 0.1F;
        if (killedBy != null) {
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
    public void hurt(Entity entity, int amount) {
        if (!level.creativeMode) {
            super.hurt(entity, amount);
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
    public void render(TextureManager textureManager, float delta) {
        // A body only needs to be rendered when player is in third-person view
        // and modelName is set.
        if (settings.thirdPersonMode == ThirdPersonMode.NONE || modelName == null) {
            return;
        }

        float var3 = attackTime - delta;
        if (var3 < 0F) {
            var3 = 0F;
        }

        // Clip all rotation variables to [-180,180) range.
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

        // Update rotation angles
        float var4 = yBodyRotO + (yBodyRot - yBodyRotO) * delta;
        float var5 = oRun + (run - oRun) * delta;
        float yawDegrees = yRotO + (yRot - yRotO) * delta;
        float pitchDegrees = xRotO + (xRot - xRotO) * delta;
        yawDegrees -= var4;
        float var8 = animStepO + (animStep - animStepO) * delta;

        GL11.glPushMatrix();
        ColorCache c = getBrightnessColor();
        GL11.glColor3f(c.R, c.G, c.B);

        float scale = 0.0625F; // 1 / 16
        float var10 = -Math.abs(MathHelper.cos(var8 * 0.6662F)) * 5F * var5 * bobStrength - 23F;
        GL11.glTranslatef(xo + (x - xo) * delta,
                yo + (y - yo) * delta - 1.62F + renderOffset,
                zo + (z - zo) * delta);

        // SURVIVAL: hurt/death effect
        float var11 = hurtTime - delta;
        if (var11 > 0F || health <= 0) {
            if (var11 < 0F) {
                var11 = 0F;
            } else {
                var11 /= hurtDuration;
                var11 = MathHelper.sin(var11 * var11 * var11 * var11 * (float) Math.PI) * 14F;
            }

            if (health <= 0) {
                float var12 = (deathTime + delta) / 20F;
                var11 += var12 * var12 * 800F;
                if (var11 > 90F) {
                    var11 = 90F;
                }
            }

            GL11.glRotatef(180F - var4 + rotOffs + 45, 0F, 1F, 0F);
            GL11.glScalef(1F, 1F, 1F);
            GL11.glRotatef(-hurtDir, 0F, 1F, 0F);
            GL11.glRotatef(-var11, 0F, 0F, 1F);
            GL11.glRotatef(hurtDir, 0F, 1F, 0F);
            GL11.glRotatef(-(180F - var4 + rotOffs), 0F, 1F, 0F);
        }

        GL11.glTranslatef(0F, -var10 * scale, 0F);
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
        renderModel(textureManager, var8, delta, var5, yawDegrees, pitchDegrees, scale);
        if (invulnerableTime > invulnerableDuration - 10) {
            GL11.glColor4f(1F, 1F, 1F, 0.75F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            bindTexture(textureManager);
            renderModel(textureManager, var8, delta, var5, yawDegrees, pitchDegrees, scale);
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

    @Override
    public void renderModel(TextureManager textures, float var2, float var3, float var4,
            float yawDegrees, float pitchDegrees, float scale) {
        
        // Render block model
        if (isInteger(modelName)) {
            renderBlock(textures);
            return;
        }
        
        // Render the rest of the model
        Model model = modelCache.getModel(modelName);
        model.render(var2, var4, tickCount + var3, yawDegrees, pitchDegrees, scale);
        
        // If model is humanoid, render its outer layer ("hair")
        if (hasHair && model instanceof HumanoidModel) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            HumanoidModel modelHeadwear = (HumanoidModel) model;
            modelHeadwear.headwear.yaw = modelHeadwear.head.yaw;
            modelHeadwear.headwear.pitch = modelHeadwear.head.pitch;
            modelHeadwear.headwear.render(scale);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
    }

    private void renderBlock(TextureManager textures) {
        try {
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glPushMatrix();
            
            // These are here to revert the scalef calls in Mob.java.
            // While those calls are useful for entity models, they cause the
            // block models to be rendered upside down.
            GL11.glScalef(-1F, 1F, 1F);
            GL11.glScalef(1F, -1F, 1F);
            Block block = Block.blocks[Integer.parseInt(modelName)];
            // TODO: Implement proper detection of which blocks need translation.
            float yTranslation = -1.4F;
            if (block instanceof FlowerBlock || block instanceof FireBlock) {
                yTranslation = -1.8F;
            }
            GL11.glTranslatef(-0.5F, yTranslation, -0.2F);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.load("/terrain.png"));
            
            block.renderPreview(ShapeRenderer.instance);
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_BLEND);
        } catch (Exception e) {
            String msg = String.format(
                    "Could not use block model \"%s\"; using humanoid model instead.",
                    modelName);
            LogUtil.logWarning(msg, e);
            modelName = "humanoid";
        }
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

    public void setKey(int key, boolean state) {
        input.setKeyState(key, state);
    }
}
