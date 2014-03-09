package com.mojang.minecraft.mob.ai;

import java.util.List;
import java.util.Random;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.HackState;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.player.Player;

public class BasicAI extends AI {

    public static final long serialVersionUID = 0L;
    public Random random = new Random();
    public float xxa;
    public float yya;
    protected float yRotA;
    public Level level;
    public Mob mob;
    public boolean jumping = false;
    protected int attackDelay = 0;
    public float runSpeed = 0.7F;
    protected int noActionTime = 0;
    public Entity attackTarget = null;

    public boolean running = false;

    public boolean flying = false;

    public boolean flyingUp = false;

    public boolean flyingDown = false;

    @Override
    public void beforeRemove() {
    }

    @Override
    public void hurt(Entity var1, int var2) {
        super.hurt(var1, var2);
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
    public void tick(Level var1, Mob var2) {
        ++noActionTime;
        Entity var3;
        if (noActionTime > 600 && random.nextInt(800) == 0 && (var3 = var1.getPlayer()) != null) {
            float var4 = var3.x - var2.x;
            float var5 = var3.y - var2.y;
            float var6 = var3.z - var2.z;
            if (var4 * var4 + var5 * var5 + var6 * var6 < 1024F) {
                noActionTime = 0;
            } else {
                var2.remove();
            }
        }

        level = var1;
        mob = var2;
        if (attackDelay > 0) {
            --attackDelay;
        }

        if (var2.health <= 0) {
            jumping = false;
            xxa = 0F;
            yya = 0F;
            yRotA = 0F;
        } else {
            update();
        }
        if (mob instanceof Player && ((Player) mob).input.HacksMode == 0) { // if
                                                                            // normal
                                                                            // hax
            if (!HackState.Fly) {
                flyingDown = false;
                flyingUp = false;
                mob.flyingMode = false;
            }
            if (!HackState.Noclip) {
                mob.noPhysics = false;
            }
            if (!HackState.Speed) {
                running = false;
            }

            if (mob.flyingMode || mob.noPhysics) {
                var2.yd = 0;
            }
            if (mob.flyingMode && !mob.noPhysics) {
                if (flyingUp) {
                    // System.out.println("flying up");
                    if (running) {
                        mob.yd = 0.08F;
                    } else {
                        mob.yd = 0.06F;
                    }

                } else if (flyingDown) {
                    // System.out.println("flying down");
                    if (running) {
                        mob.yd = -0.08F;
                    } else {
                        mob.yd = -0.06F;
                    }
                } else if (jumping) {
                    if (running) {
                        mob.yd = 0.08F;
                    } else {
                        mob.yd = 0.06F;
                    }
                }
            }

            else if (mob.noPhysics && !mob.flyingMode) {
                if (flyingUp) {
                    if (running) {
                        mob.yd = 0.48F;
                    } else {
                        mob.yd = 0.26F;
                    }

                } else if (flyingDown) {
                    if (running) {
                        mob.yd = -0.48F;
                    } else {
                        mob.yd = -0.26F;
                    }
                } else if (jumping) {
                    if (running) {
                        mob.yd = 0.48F;
                    } else {
                        mob.yd = 0.26F;
                    }
                }
            }

            else if (mob.noPhysics && mob.flyingMode) {
                if (flyingUp) {
                    // System.out.println("flying up");
                    if (running) {
                        mob.yd = 0.08F;
                    } else {
                        mob.yd = 0.06F;
                    }

                } else if (flyingDown) {
                    // System.out.println("flying down");
                    if (running) {
                        mob.yd = -0.08F;
                    } else {
                        mob.yd = -0.06F;
                    }
                } else if (jumping) {
                    if (running) {
                        mob.yd = 0.08F;
                    } else {
                        mob.yd = 0.06F;
                    }
                }
            } else {
                if (jumping && mob.isInOrOnRope() && mob.yd > 0.02f) {
                    mob.yd = 0.02F;
                }
            }
        }

        boolean var7 = var2.isInWater();
        boolean isInWater = var7; // Unsure if other files use "var7" - will fix
                                  // later?
        boolean isInLava = var2.isInLava();
        boolean isInOrOnRope = var2.isInOrOnRope();
        if (jumping) {
            if (isInWater) { // if in water
                if (!running) {
                    var2.yd += 0.04F;
                } else {
                    var2.yd += 0.08F;
                }
            } else if (isInLava) {
                if (!running) {
                    var2.yd += 0.04F;
                } else {
                    var2.yd += 0.08F;
                }
            } else if (isInOrOnRope) {
                if (!running) {
                    var2.yd += 0.1F;
                } else {
                    var2.yd += 0.15F;
                }
            }

            else if (var2.onGround) { // if on the ground
                jumpFromGround();
            }
        }

        xxa *= 0.98F;
        yya *= 0.98F;
        yRotA *= 0.9F;
        var2.travel(xxa, yya);
        List<Entity> var11;
        if ((var11 = var1.findEntities(var2, var2.bb.grow(0.2F, 0F, 0.2F))) != null
                && var11.size() > 0) {
            for (int var8 = 0; var8 < var11.size(); ++var8) {
                Entity var10;
                if ((var10 = var11.get(var8)).isPushable()) {
                    var10.push(var2);
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

        boolean var1 = mob.isInWater();
        boolean isInLava = mob.isInLava();
        if (var1 || isInLava) {
            jumping = random.nextFloat() < 0.8F;
        }
    }
}