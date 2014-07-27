package com.mojang.minecraft.level.generator;

import java.util.ArrayList;
import java.util.Random;

import com.mojang.util.LogUtil;
import com.mojang.minecraft.ProgressBarDisplay;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.generator.noise.CombinedNoise;
import com.mojang.minecraft.level.generator.noise.OctaveNoise;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.util.MathHelper;

public final class LevelGenerator {

    private ProgressBarDisplay progressBar;
    private int width;
    private int depth;
    private int height;
    private Random random = new Random();
    private byte[] blocks;
    private int waterLevel;
    private int[] h = new int[1048576];

    public LevelGenerator(ProgressBarDisplay var1) {
        progressBar = var1;
    }

    private long flood(int var1, int var2, int var3, int var5) {
        byte var20 = (byte) var5;
        ArrayList<int[]> var21 = new ArrayList<>();
        byte var6 = 0;
        int var7 = 1;

        int var8 = 1;
        while (1 << var7 < width) {
            ++var7;
        }

        while (1 << var8 < depth) {
            ++var8;
        }

        int var9 = depth - 1;
        int var10 = width - 1;
        int var22 = var6 + 1;
        h[0] = ((var2 << var8) + var3 << var7) + var1;
        long var11 = 0L;
        var1 = width * depth;

        while (var22 > 0) {
            --var22;
            var2 = h[var22];
            if (var22 == 0 && var21.size() > 0) {
                h = var21.remove(var21.size() - 1);
                var22 = h.length;
            }

            var3 = var2 >> var7 & var9;
            int var13 = var2 >> var7 + var8;

            int var14;
            int var15;
            for (var15 = var14 = var2 & var10; var14 > 0 && blocks[var2 - 1] == 0; --var2) {
                --var14;
            }

            while (var15 < width && blocks[var2 + var15 - var14] == 0) {
                ++var15;
            }

            int var16 = var2 >> var7 & var9;
            int var17 = var2 >> var7 + var8;
            if (var16 != var3 || var17 != var13) {
                LogUtil.logWarning("Diagonal flood!?");
            }

            boolean var23 = false;
            boolean var24 = false;
            boolean var18 = false;
            var11 += var15 - var14;

            for (; var14 < var15; ++var14) {
                blocks[var2] = var20;
                boolean var19;
                if (var3 > 0) {
                    if ((var19 = blocks[var2 - width] == 0) && !var23) {
                        if (var22 == h.length) {
                            var21.add(h);
                            h = new int[1048576];
                            var22 = 0;
                        }

                        h[var22++] = var2 - width;
                    }

                    var23 = var19;
                }

                if (var3 < depth - 1) {
                    if ((var19 = blocks[var2 + width] == 0) && !var24) {
                        if (var22 == h.length) {
                            var21.add(h);
                            h = new int[1048576];
                            var22 = 0;
                        }

                        h[var22++] = var2 + width;
                    }

                    var24 = var19;
                }

                if (var13 > 0) {
                    byte var25 = blocks[var2 - var1];
                    if ((var20 == Block.LAVA.id || var20 == Block.STATIONARY_LAVA.id)
                            && (var25 == Block.WATER.id || var25 == Block.STATIONARY_WATER.id)) {
                        blocks[var2 - var1] = (byte) Block.STONE.id;
                    }

                    if ((var19 = var25 == 0) && !var18) {
                        if (var22 == h.length) {
                            var21.add(h);
                            h = new int[1048576];
                            var22 = 0;
                        }

                        h[var22++] = var2 - var1;
                    }

                    var18 = var19;
                }

                ++var2;
            }
        }

        return var11;
    }

    /**
     * Generates a level
     *
     * @param creator
     * @param width
     * @param depth
     * @param height  Seems to be unused.
     * @return
     */
    public final Level generate(String creator, int width, int depth, int height) {
        progressBar.setTitle("Generating level");
        this.width = width;
        this.depth = depth;
        this.height = 64;
        waterLevel = 32;
        blocks = new byte[width * depth << 6];
        progressBar.setText("Raising..");
        CombinedNoise noise1 = new CombinedNoise(new OctaveNoise(random, 8),
                new OctaveNoise(random, 8));
        CombinedNoise noise2 = new CombinedNoise(new OctaveNoise(random, 8),
                new OctaveNoise(random, 8));
        OctaveNoise var8 = new OctaveNoise(random, 6);
        int[] var9 = new int[this.width * this.depth];
        float var10 = 1.3F;

        int var11;
        int var12;
        for (var11 = 0; var11 < this.width; ++var11) {
            this.setProgress(var11 * 100 / (this.width - 1));

            for (var12 = 0; var12 < this.depth; ++var12) {
                double var13 = noise1.compute(var11 * var10, var12 * var10) / 6D + -4;
                double var15 = noise2.compute(var11 * var10, var12 * var10) / 5D + 10D + -4;
                if (var8.compute(var11, var12) / 8D > 0D) {
                    var15 = var13;
                }

                double var19;
                if ((var19 = Math.max(var13, var15) / 2D) < 0D) {
                    var19 *= 0.8D;
                }

                var9[var11 + var12 * this.width] = (int) var19;
            }
        }

        progressBar.setText("Eroding..");
        int[] var42 = var9;
        CombinedNoise noise3 = new CombinedNoise(new OctaveNoise(random, 8), new OctaveNoise(random, 8));
        CombinedNoise var49 = new CombinedNoise(new OctaveNoise(random, 8), new OctaveNoise(random,
                8));

        int var23;
        int var51;
        int var54;
        for (var51 = 0; var51 < this.width; ++var51) {
            this.setProgress(var51 * 100 / (this.width - 1));

            for (var54 = 0; var54 < this.depth; ++var54) {
                double var21 = noise3.compute(var51 << 1, var54 << 1) / 8D;
                var12 = var49.compute(var51 << 1, var54 << 1) > 0D ? 1 : 0;
                if (var21 > 2D) {
                    var23 = ((var42[var51 + var54 * this.width] - var12) / 2 << 1) + var12;
                    var42[var51 + var54 * this.width] = var23;
                }
            }
        }

        progressBar.setText("Soiling..");
        var42 = var9;
        int var46 = this.width;
        int var48 = this.depth;
        var51 = this.height;
        OctaveNoise noise4 = new OctaveNoise(random, 8);

        int var25;
        int var24;
        int var27;
        int var26;
        int var28;
        for (var24 = 0; var24 < var46; ++var24) {
            this.setProgress(var24 * 100 / (this.width - 1));

            for (var11 = 0; var11 < var48; ++var11) {
                var12 = (int) (noise4.compute(var24, var11) / 24D) - 4;
                var25 = (var23 = var42[var24 + var11 * var46] + this.waterLevel) + var12;
                var42[var24 + var11 * var46] = Math.max(var23, var25);
                if (var42[var24 + var11 * var46] > var51 - 2) {
                    var42[var24 + var11 * var46] = var51 - 2;
                }

                if (var42[var24 + var11 * var46] < 1) {
                    var42[var24 + var11 * var46] = 1;
                }

                for (var26 = 0; var26 < var51; ++var26) {
                    var27 = (var26 * this.depth + var11) * this.width + var24;
                    var28 = 0;
                    if (var26 <= var23) {
                        var28 = Block.DIRT.id;
                    }

                    if (var26 <= var25) {
                        var28 = Block.STONE.id;
                    }

                    if (var26 == 0) {
                        var28 = Block.LAVA.id;
                    }

                    this.blocks[var27] = (byte) var28;
                }
            }
        }

        progressBar.setText("Carving..");
        var48 = this.width;
        var51 = this.depth;
        var54 = this.height;
        var24 = var48 * var51 * var54 / 256 / 64 << 1;

        for (var11 = 0; var11 < var24; ++var11) {
            this.setProgress(var11 * 100 / (var24 - 1) / 4);
            float var55 = this.random.nextFloat() * var48;
            float var59 = this.random.nextFloat() * var54;
            float var56 = this.random.nextFloat() * var51;
            var26 = (int) ((this.random.nextFloat() + this.random.nextFloat()) * 200F);
            float var61 = this.random.nextFloat() * (float) Math.PI * 2F;
            float var64 = 0F;
            float var29 = this.random.nextFloat() * (float) Math.PI * 2F;
            float var30 = 0F;
            float var31 = this.random.nextFloat() * this.random.nextFloat();

            for (int var32 = 0; var32 < var26; ++var32) {
                var55 += MathHelper.sin(var61) * MathHelper.cos(var29);
                var56 += MathHelper.cos(var61) * MathHelper.cos(var29);
                var59 += MathHelper.sin(var29);
                var61 += var64 * 0.2F;
                var64 = (var64 *= 0.9F) + (this.random.nextFloat() - this.random.nextFloat());
                var29 = (var29 + var30 * 0.5F) * 0.5F;
                var30 = (var30 *= 0.75F) + (this.random.nextFloat() - this.random.nextFloat());
                if (this.random.nextFloat() >= 0.25F) {
                    float var43 = var55 + (this.random.nextFloat() * 4F - 2F) * 0.2F;
                    float var50 = var59 + (this.random.nextFloat() * 4F - 2F) * 0.2F;
                    float var33 = var56 + (this.random.nextFloat() * 4F - 2F) * 0.2F;
                    float var34 = (this.height - var50) / this.height;
                    var34 = 1.2F + (var34 * 3.5F + 1F) * var31;
                    var34 = MathHelper.sin(var32 * (float) Math.PI / var26) * var34;

                    for (int var35 = (int) (var43 - var34); var35 <= (int) (var43 + var34); ++var35) {
                        for (int var36 = (int) (var50 - var34); var36 <= (int) (var50 + var34); ++var36) {
                            for (int var37 = (int) (var33 - var34); var37 <= (int) (var33 + var34); ++var37) {
                                float var38 = var35 - var43;
                                float var39 = var36 - var50;
                                float var40 = var37 - var33;
                                if (var38 * var38 + var39 * var39 * 2F + var40 * var40 < var34 * var34
                                        && var35 >= 1
                                        && var36 >= 1
                                        && var37 >= 1
                                        && var35 < this.width - 1
                                        && var36 < this.height - 1
                                        && var37 < this.depth - 1) {
                                    int var66 = (var36 * this.depth + var37) * this.width + var35;
                                    if (this.blocks[var66] == Block.STONE.id) {
                                        this.blocks[var66] = 0;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        populateOre(Block.COAL_ORE.id, 90, 1, 4);
        populateOre(Block.IRON_ORE.id, 70, 2, 4);
        populateOre(Block.GOLD_ORE.id, 50, 3, 4);
        progressBar.setText("Watering..");
        var51 = Block.STATIONARY_WATER.id;
        setProgress(0);

        for (var54 = 0; var54 < this.width; ++var54) {
            this.flood(var54, this.height / 2 - 1, 0, var51);
            this.flood(var54, this.height / 2 - 1, this.depth - 1, var51);
        }

        for (var54 = 0; var54 < this.depth; ++var54) {
            this.flood(0, this.height / 2 - 1, var54, var51);
            this.flood(this.width - 1, this.height / 2 - 1, var54, var51);
        }

        var54 = this.width * this.depth / 8000;

        for (var24 = 0; var24 < var54; ++var24) {
            if (var24 % 100 == 0) {
                this.setProgress(var24 * 100 / (var54 - 1));
            }

            var11 = this.random.nextInt(this.width);
            var12 = this.waterLevel - 1 - this.random.nextInt(2);
            var23 = this.random.nextInt(this.depth);
            if (this.blocks[(var12 * this.depth + var23) * this.width + var11] == 0) {
                this.flood(var11, var12, var23, var51);
            }
        }

        this.setProgress(100);
        progressBar.setText("Melting..");
        var46 = this.width * this.depth * this.height / 20000;

        for (var48 = 0; var48 < var46; ++var48) {
            if (var48 % 100 == 0) {
                this.setProgress(var48 * 100 / (var46 - 1));
            }

            var51 = this.random.nextInt(this.width);
            var54 = (int) (this.random.nextFloat() * this.random.nextFloat() * (this.waterLevel - 3));
            var24 = this.random.nextInt(this.depth);
            if (this.blocks[(var54 * this.depth + var24) * this.width + var51] == 0) {
                this.flood(var51, var54, var24, Block.STATIONARY_LAVA.id);
            }
        }

        this.setProgress(100);
        progressBar.setText("Growing..");
        var42 = var9;
        var46 = this.width;
        var48 = this.depth;
        var51 = this.height;
        noise4 = new OctaveNoise(random, 8);
        OctaveNoise var58 = new OctaveNoise(random, 8);

        int var63;
        for (var11 = 0; var11 < var46; ++var11) {
            this.setProgress(var11 * 100 / (this.width - 1));

            for (var12 = 0; var12 < var48; ++var12) {
                boolean var60 = noise4.compute(var11, var12) > 8D;
                boolean var57 = var58.compute(var11, var12) > 12D;
                var27 = ((var26 = var42[var11 + var12 * var46]) * this.depth + var12) * this.width
                        + var11;
                if (((var28 = this.blocks[((var26 + 1) * this.depth + var12) * this.width + var11] & 255) == Block.WATER.id || var28 == Block.STATIONARY_WATER.id)
                        && var26 <= var51 / 2 - 1 && var57) {
                    this.blocks[var27] = (byte) Block.GRAVEL.id;
                }

                if (var28 == 0) {
                    var63 = Block.GRASS.id;
                    if (var26 <= var51 / 2 - 1 && var60) {
                        var63 = Block.SAND.id;
                    }

                    this.blocks[var27] = (byte) var63;
                }
            }
        }

        progressBar.setText("Planting..");
        var42 = var9;
        var46 = this.width;
        var48 = this.width * this.depth / 3000;

        for (var51 = 0; var51 < var48; ++var51) {
            var54 = this.random.nextInt(2);
            this.setProgress(var51 * 50 / (var48 - 1));
            var24 = this.random.nextInt(this.width);
            var11 = this.random.nextInt(this.depth);

            for (var12 = 0; var12 < 10; ++var12) {
                var23 = var24;
                var25 = var11;

                for (var26 = 0; var26 < 5; ++var26) {
                    var23 += this.random.nextInt(6) - this.random.nextInt(6);
                    var25 += this.random.nextInt(6) - this.random.nextInt(6);
                    if ((var54 < 2 || this.random.nextInt(4) == 0) && var23 >= 0 && var25 >= 0
                            && var23 < this.width && var25 < this.depth) {
                        var27 = var42[var23 + var25 * var46] + 1;
                        if ((this.blocks[(var27 * this.depth + var25) * this.width + var23] & 255) == 0) {
                            var63 = (var27 * this.depth + var25) * this.width + var23;
                            if ((this.blocks[((var27 - 1) * this.depth + var25) * this.width
                                    + var23] & 255) == Block.GRASS.id) {
                                if (var54 == 0) {
                                    this.blocks[var63] = (byte) Block.DANDELION.id;
                                } else if (var54 == 1) {
                                    this.blocks[var63] = (byte) Block.ROSE.id;
                                }
                            }
                        }
                    }
                }
            }
        }

        var42 = var9;
        var46 = this.width;
        var51 = this.width * this.depth * this.height / 2000;

        for (var54 = 0; var54 < var51; ++var54) {
            var24 = this.random.nextInt(2);
            this.setProgress(var54 * 50 / (var51 - 1) + 50);
            var11 = this.random.nextInt(this.width);
            var12 = this.random.nextInt(this.height);
            var23 = this.random.nextInt(this.depth);

            for (var25 = 0; var25 < 20; ++var25) {
                var26 = var11;
                var27 = var12;
                var28 = var23;

                for (var63 = 0; var63 < 5; ++var63) {
                    var26 += this.random.nextInt(6) - this.random.nextInt(6);
                    var27 += this.random.nextInt(2) - this.random.nextInt(2);
                    var28 += this.random.nextInt(6) - this.random.nextInt(6);
                    if ((var24 < 2 || this.random.nextInt(4) == 0)
                            && var26 >= 0
                            && var28 >= 0
                            && var27 >= 1
                            && var26 < this.width
                            && var28 < this.depth
                            && var27 < var42[var26 + var28 * var46] - 1
                            && (this.blocks[(var27 * this.depth + var28) * this.width + var26] & 255) == 0) {
                        int var62 = (var27 * this.depth + var28) * this.width + var26;
                        if ((this.blocks[((var27 - 1) * this.depth + var28) * this.width + var26] & 255) == Block.STONE.id) {
                            if (var24 == 0) {
                                this.blocks[var62] = (byte) Block.BROWN_MUSHROOM.id;
                            } else if (var24 == 1) {
                                this.blocks[var62] = (byte) Block.RED_MUSHROOM.id;
                            }
                        }
                    }
                }
            }
        }

        Level level = new Level();
        level.waterLevel = waterLevel;
        level.setData(width, 64, depth, blocks);
        level.createTime = System.currentTimeMillis();
        level.creator = creator;
        level.name = "A Nice World";
        var48 = this.width;
        var51 = this.width * this.depth / 4000;

        for (var54 = 0; var54 < var51; ++var54) {
            this.setProgress(var54 * 50 / (var51 - 1) + 50);
            var24 = this.random.nextInt(this.width);
            var11 = this.random.nextInt(this.depth);

            for (var12 = 0; var12 < 20; ++var12) {
                var23 = var24;
                var25 = var11;

                for (var26 = 0; var26 < 20; ++var26) {
                    var23 += this.random.nextInt(6) - this.random.nextInt(6);
                    var25 += this.random.nextInt(6) - this.random.nextInt(6);
                    if (var23 >= 0 && var25 >= 0 && var23 < this.width && var25 < this.depth) {
                        var27 = var9[var23 + var25 * var48] + 1;
                        if (this.random.nextInt(4) == 0) {
                            level.maybeGrowTree(var23, var27, var25);
                        }
                    }
                }
            }
        }

        return level;
    }

    private void populateOre(int var1, int var2, int var3, int var4) {
        byte var25 = (byte) var1;
        var4 = width;
        int var5 = depth;
        int var6 = height;
        int var7 = var4 * var5 * var6 / 256 / 64 * var2 / 100;

        for (int var8 = 0; var8 < var7; ++var8) {
            setProgress(var8 * 100 / (var7 - 1) / 4 + var3 * 100 / 4);
            float var9 = random.nextFloat() * var4;
            float var10 = random.nextFloat() * var6;
            float var11 = random.nextFloat() * var5;
            int var12 = (int) ((random.nextFloat() + random.nextFloat()) * 75F * var2 / 100F);
            float var13 = random.nextFloat() * (float) Math.PI * 2F;
            float var14 = 0F;
            float var15 = random.nextFloat() * (float) Math.PI * 2F;
            float var16 = 0F;

            for (int var17 = 0; var17 < var12; ++var17) {
                var9 += MathHelper.sin(var13) * MathHelper.cos(var15);
                var11 += MathHelper.cos(var13) * MathHelper.cos(var15);
                var10 += MathHelper.sin(var15);
                var13 += var14 * 0.2F;
                var14 = (var14 *= 0.9F) + (random.nextFloat() - random.nextFloat());
                var15 = (var15 + var16 * 0.5F) * 0.5F;
                var16 = (var16 *= 0.9F) + (random.nextFloat() - random.nextFloat());
                float var18 = MathHelper.sin(var17 * (float) Math.PI / var12) * var2 / 100F + 1F;

                for (int var19 = (int) (var9 - var18); var19 <= (int) (var9 + var18); ++var19) {
                    for (int var20 = (int) (var10 - var18); var20 <= (int) (var10 + var18); ++var20) {
                        for (int var21 = (int) (var11 - var18); var21 <= (int) (var11 + var18); ++var21) {
                            float var22 = var19 - var9;
                            float var23 = var20 - var10;
                            float var24 = var21 - var11;
                            if (var22 * var22 + var23 * var23 * 2F + var24 * var24 < var18 * var18
                                    && var19 >= 1 && var20 >= 1 && var21 >= 1 && var19 < width - 1
                                    && var20 < height - 1 && var21 < depth - 1) {
                                int var26 = (var20 * depth + var21) * width + var19;
                                if (blocks[var26] == Block.STONE.id) {
                                    blocks[var26] = var25;
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private void setProgress(int percentage) {
        progressBar.setProgress(percentage);
    }
}
