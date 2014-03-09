package com.mojang.minecraft.level;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.ProgressBarDisplay;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.mob.Creeper;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.mob.Pig;
import com.mojang.minecraft.mob.Sheep;
import com.mojang.minecraft.mob.Skeleton;
import com.mojang.minecraft.mob.Spider;
import com.mojang.minecraft.mob.Zombie;

public final class MobSpawner {

    public Level level;
    public boolean HasStopped;

    // public static int Count = 0;
    public MobSpawner(Level var1) {
        this.level = var1;
        HasStopped = false;
        // Count++;
    }

    public final int spawn(int var1, Entity var2, ProgressBarDisplay var3) {
        // System.out.println(Count);
        if (HasStopped) {
            return 0;
        }
        int var4 = 0;

        for (int var5 = 0; var5 < var1; ++var5) {
            if (HasStopped) {
                break;
            }
            if (var3 != null) {
                var3.setProgress(var5 * 100 / (var1 - 1));
            }

            int var6 = this.level.random.nextInt(7);
            int var7 = this.level.random.nextInt(this.level.width);
            int var8 = (int) (Math
                    .min(this.level.random.nextFloat(), this.level.random.nextFloat()) * this.level.height);
            int var9 = this.level.random.nextInt(this.level.length);
            if (!this.level.isSolidTile(var7, var8, var9)
                    && this.level.getLiquid(var7, var8, var9) == LiquidType.notLiquid
                    && (!this.level.isLit(var7, var8, var9) || this.level.random.nextInt(5) == 0)) {
                for (int var10 = 0; var10 < 3; ++var10) {
                    if (HasStopped) {
                        break;
                    }
                    int var11 = var7;
                    int var12 = var8;
                    int var13 = var9;

                    for (int var14 = 0; var14 < 3; ++var14) {
                        if (HasStopped) {
                            break;
                        }
                        var11 += this.level.random.nextInt(6) - this.level.random.nextInt(6);
                        var12 += this.level.random.nextInt(1) - this.level.random.nextInt(1);
                        var13 += this.level.random.nextInt(6) - this.level.random.nextInt(6);
                        if (var11 >= 0 && var13 >= 1 && var12 >= 0 && var12 < this.level.height - 2
                                && var11 < this.level.width && var13 < this.level.length
                                && this.level.isSolidTile(var11, var12 - 1, var13)
                                && !this.level.isSolidTile(var11, var12, var13)
                                && !this.level.isSolidTile(var11, var12 + 1, var13)) {
                            float var15 = var11 + 0.5F;
                            float var16 = var12 + 1F;
                            float var17 = var13 + 0.5F;
                            float var19;
                            float var18;
                            float var20;
                            if (var2 != null) {
                                var18 = var15 - var2.x;
                                var19 = var16 - var2.y;
                                var20 = var17 - var2.z;
                                if (var18 * var18 + var19 * var19 + var20 * var20 < 256F) {
                                    continue;
                                }
                            } else {
                                var18 = var15 - this.level.xSpawn;
                                var19 = var16 - this.level.ySpawn;
                                var20 = var17 - this.level.zSpawn;
                                if (var18 * var18 + var19 * var19 + var20 * var20 < 256F) {
                                    continue;
                                }
                            }

                            Object var21 = null;
                            if (!this.HasStopped) {

                                if (var6 == 0) {
                                    var21 = new Zombie(this.level, var15, var16, var17);
                                }

                                if (var6 == 1) {
                                    var21 = new Skeleton(this.level, var15, var16, var17);
                                }

                                if (var6 == 3) {
                                    var21 = new Creeper(this.level, var15, var16, var17);
                                }

                                if (var6 == 4) {
                                    var21 = new Spider(this.level, var15, var16, var17);
                                }

                                if (var6 == 2) {
                                    var21 = new Pig(this.level, var15, var16, var17);
                                }

                                if (var6 == 5) {
                                    var21 = new Sheep(this.level, var15, var16, var17);
                                }

                                if (var21 != null) {

                                    if (this.level.isFree(((Mob) var21).bb)
                                            && !this.level.creativeMode) {
                                        ++var4;
                                        this.level.addEntity((Entity) var21);
                                        // System.out.println("Added mob");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return var4;
    }

}
