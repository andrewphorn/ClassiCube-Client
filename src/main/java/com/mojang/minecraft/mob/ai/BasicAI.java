package com.mojang.minecraft.mob.ai;

import java.util.List;
import java.util.Random;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.HackState;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.player.Player;

public class BasicAI extends AI {

    public Random random = new Random();
    public float xxa;
    public float yya;
    public Level level;
    public Mob mob;
    public boolean jumping = false;
    public float runSpeed = 0.7F;
    public Entity attackTarget = null;
    public boolean running = false;
    public boolean flying = false;
    public boolean flyingUp = false;
    public boolean flyingDown = false;
    protected float yRotA;
    protected int attackDelay = 0;
    protected int noActionTime = 0;

    @Override
    public void beforeRemove() {
    }

    @Override
    public void hurt(Entity entity, int amount) {
        super.hurt(entity, amount);
        noActionTime = 0;
    }

    protected void jumpFromGround() {
        if (!running) {
            mob.yd = 0.42F;
        } else {
            mob.yd = 0.84F;
        }
    }

    @Override
    public void tick(Level level, Mob mob) {
        ++noActionTime;
        Entity player = level.getPlayer();
        if (noActionTime > 600 && random.nextInt(800) == 0 && player != null) {
            float var4 = player.x - mob.x;
            float var5 = player.y - mob.y;
            float var6 = player.z - mob.z;
            if (var4 * var4 + var5 * var5 + var6 * var6 < 1024F) {
                noActionTime = 0;
            } else {
                mob.remove();
            }
        }

        this.level = level;
        this.mob = mob;
        if (attackDelay > 0) {
            --attackDelay;
        }

        if (mob.health <= 0) {
            jumping = false;
            xxa = 0F;
            yya = 0F;
            yRotA = 0F;
        } else {
            update();
        }
        if (this.mob instanceof Player && ((Player) this.mob).input.HacksMode == 0) { // if normal hax
            if (!HackState.fly) {
                flyingDown = false;
                flyingUp = false;
                this.mob.flyingMode = false;
            }
            if (!HackState.noclip) {
                this.mob.noPhysics = false;
            }
            if (!HackState.speed) {
                running = false;
            }

            if (this.mob.flyingMode || this.mob.noPhysics) {
                mob.yd = 0;
            }
            if (this.mob.flyingMode && !this.mob.noPhysics) {
                if (flyingUp) {
                    // LogUtil.logInfo("flying up");
                    if (running) {
                        this.mob.yd = 0.08F;
                    } else {
                        this.mob.yd = 0.06F;
                    }

                } else if (flyingDown) {
                    // LogUtil.logInfo("flying down");
                    if (running) {
                        this.mob.yd = -0.08F;
                    } else {
                        this.mob.yd = -0.06F;
                    }
                } else if (jumping) {
                    if (running) {
                        this.mob.yd = 0.08F;
                    } else {
                        this.mob.yd = 0.06F;
                    }
                }
            } else if (this.mob.noPhysics && !this.mob.flyingMode) {
                if (flyingUp) {
                    if (running) {
                        this.mob.yd = 0.48F;
                    } else {
                        this.mob.yd = 0.26F;
                    }

                } else if (flyingDown) {
                    if (running) {
                        this.mob.yd = -0.48F;
                    } else {
                        this.mob.yd = -0.26F;
                    }
                } else if (jumping) {
                    if (running) {
                        this.mob.yd = 0.48F;
                    } else {
                        this.mob.yd = 0.26F;
                    }
                }
            } else if (this.mob.noPhysics && this.mob.flyingMode) {
                if (flyingUp) {
                    // LogUtil.logInfo("flying up");
                    if (running) {
                        this.mob.yd = 0.08F;
                    } else {
                        this.mob.yd = 0.06F;
                    }

                } else if (flyingDown) {
                    // LogUtil.logInfo("flying down");
                    if (running) {
                        this.mob.yd = -0.08F;
                    } else {
                        this.mob.yd = -0.06F;
                    }
                } else if (jumping) {
                    if (running) {
                        this.mob.yd = 0.08F;
                    } else {
                        this.mob.yd = 0.06F;
                    }
                }
            } else {
                if (jumping && this.mob.isInOrOnRope() && this.mob.yd > 0.02f) {
                    this.mob.yd = 0.02F;
                }
            }
        }

        if (jumping) {
            if (mob.isInWater()) {
                if (!running) {
                    mob.yd += 0.04F;
                } else {
                    mob.yd += 0.08F;
                }
            } else if (mob.isInLava()) {
                if (!running) {
                    mob.yd += 0.04F;
                } else {
                    mob.yd += 0.08F;
                }
            } else if (mob.isInOrOnRope()) {
                if (!running) {
                    mob.yd += 0.1F;
                } else {
                    mob.yd += 0.15F;
                }
            } else if (mob.onGround) { // if on the ground
                jumpFromGround();
            }
        }

        xxa *= 0.98F;
        yya *= 0.98F;
        yRotA *= 0.9F;
        mob.travel(xxa, yya);
        List<Entity> neighbourEntities = level.findEntities(mob,
                mob.boundingBox.grow(0.2F, 0F, 0.2F));
        if (neighbourEntities != null && neighbourEntities.size() > 0) {
            for (Entity entity : neighbourEntities) {
                if (entity.isPushable()) {
                    entity.push(mob);
                }
            }
        }
    }

    protected void update() {
        if (random.nextFloat() < 0.07F) {
            xxa = (random.nextFloat() - 0.5F) * runSpeed;
            yya = random.nextFloat() * runSpeed;
        }

        jumping = random.nextFloat() < 0.01F;
        if (random.nextFloat() < 0.04F) {
            yRotA = (random.nextFloat() - 0.5F) * 60F;
        }

        mob.yRot += yRotA;
        mob.xRot = defaultLookAngle;
        if (attackTarget != null) {
            yya = runSpeed;
            jumping = random.nextFloat() < 0.04F;
        }

        if (mob.isInWater() || mob.isInLava()) {
            jumping = random.nextFloat() < 0.8F;
        }
    }
}