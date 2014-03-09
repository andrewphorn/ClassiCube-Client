package com.mojang.minecraft.level.generator;

import java.util.ArrayList;
import java.util.Random;

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
        ArrayList<int[]> var21 = new ArrayList<int[]>();
        byte var6 = 0;
        int var7 = 1;

        int var8;
        for (var8 = 1; 1 << var7 < width; ++var7) {
            ;
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
                System.out.println("Diagonal flood!?");
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

    public final Level generate(String var1, int var2, int var3, int var4) {
        progressBar.setTitle("Generating level");
        width = var2;
        depth = var3;
        height = 64;
        waterLevel = 32;
        blocks = new byte[var2 * var3 << 6];
        progressBar.setText("Raising..");
        LevelGenerator var5 = this;
        CombinedNoise var6 = new CombinedNoise(new OctaveNoise(random, 8), new OctaveNoise(random,
                8));
        CombinedNoise var7 = new CombinedNoise(new OctaveNoise(random, 8), new OctaveNoise(random,
                8));
        OctaveNoise var8 = new OctaveNoise(random, 6);
        int[] var9 = new int[width * depth];
        float var10 = 1.3F;

        int var11;
        int var12;
        for (var11 = 0; var11 < var5.width; ++var11) {
            var5.setProgress(var11 * 100 / (var5.width - 1));

            for (var12 = 0; var12 < var5.depth; ++var12) {
                double var13 = var6.compute(var11 * var10, var12 * var10) / 6.0D + -4;
                double var15 = var7.compute(var11 * var10, var12 * var10) / 5.0D + 10.0D + -4;
                if (var8.compute(var11, var12) / 8.0D > 0.0D) {
                    var15 = var13;
                }

                double var19;
                if ((var19 = Math.max(var13, var15) / 2.0D) < 0.0D) {
                    var19 *= 0.8D;
                }

                var9[var11 + var12 * var5.width] = (int) var19;
            }
        }

        progressBar.setText("Eroding..");
        int[] var42 = var9;
        var5 = this;
        var7 = new CombinedNoise(new OctaveNoise(random, 8), new OctaveNoise(random, 8));
        CombinedNoise var49 = new CombinedNoise(new OctaveNoise(random, 8), new OctaveNoise(random,
                8));

        int var23;
        int var51;
        int var54;
        for (var51 = 0; var51 < var5.width; ++var51) {
            var5.setProgress(var51 * 100 / (var5.width - 1));

            for (var54 = 0; var54 < var5.depth; ++var54) {
                double var21 = var7.compute(var51 << 1, var54 << 1) / 8.0D;
                var12 = var49.compute(var51 << 1, var54 << 1) > 0.0D ? 1 : 0;
                if (var21 > 2.0D) {
                    var23 = ((var42[var51 + var54 * var5.width] - var12) / 2 << 1) + var12;
                    var42[var51 + var54 * var5.width] = var23;
                }
            }
        }

        progressBar.setText("Soiling..");
        var42 = var9;
        var5 = this;
        int var46 = width;
        int var48 = depth;
        var51 = height;
        OctaveNoise var53 = new OctaveNoise(random, 8);

        int var25;
        int var24;
        int var27;
        int var26;
        int var28;
        for (var24 = 0; var24 < var46; ++var24) {
            var5.setProgress(var24 * 100 / (var5.width - 1));

            for (var11 = 0; var11 < var48; ++var11) {
                var12 = (int) (var53.compute(var24, var11) / 24.0D) - 4;
                var25 = (var23 = var42[var24 + var11 * var46] + var5.waterLevel) + var12;
                var42[var24 + var11 * var46] = Math.max(var23, var25);
                if (var42[var24 + var11 * var46] > var51 - 2) {
                    var42[var24 + var11 * var46] = var51 - 2;
                }

                if (var42[var24 + var11 * var46] < 1) {
                    var42[var24 + var11 * var46] = 1;
                }

                for (var26 = 0; var26 < var51; ++var26) {
                    var27 = (var26 * var5.depth + var11) * var5.width + var24;
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

                    var5.blocks[var27] = (byte) var28;
                }
            }
        }

        progressBar.setText("Carving..");
        var5 = this;
        var48 = width;
        var51 = depth;
        var54 = height;
        var24 = var48 * var51 * var54 / 256 / 64 << 1;

        for (var11 = 0; var11 < var24; ++var11) {
            var5.setProgress(var11 * 100 / (var24 - 1) / 4);
            float var55 = var5.random.nextFloat() * var48;
            float var59 = var5.random.nextFloat() * var54;
            float var56 = var5.random.nextFloat() * var51;
            var26 = (int) ((var5.random.nextFloat() + var5.random.nextFloat()) * 200F);
            float var61 = var5.random.nextFloat() * (float) Math.PI * 2F;
            float var64 = 0F;
            float var29 = var5.random.nextFloat() * (float) Math.PI * 2F;
            float var30 = 0F;
            float var31 = var5.random.nextFloat() * var5.random.nextFloat();

            for (int var32 = 0; var32 < var26; ++var32) {
                var55 += MathHelper.sin(var61) * MathHelper.cos(var29);
                var56 += MathHelper.cos(var61) * MathHelper.cos(var29);
                var59 += MathHelper.sin(var29);
                var61 += var64 * 0.2F;
                var64 = (var64 *= 0.9F) + (var5.random.nextFloat() - var5.random.nextFloat());
                var29 = (var29 + var30 * 0.5F) * 0.5F;
                var30 = (var30 *= 0.75F) + (var5.random.nextFloat() - var5.random.nextFloat());
                if (var5.random.nextFloat() >= 0.25F) {
                    float var43 = var55 + (var5.random.nextFloat() * 4F - 2F) * 0.2F;
                    float var50 = var59 + (var5.random.nextFloat() * 4F - 2F) * 0.2F;
                    float var33 = var56 + (var5.random.nextFloat() * 4F - 2F) * 0.2F;
                    float var34 = (var5.height - var50) / var5.height;
                    var34 = 1.2F + (var34 * 3.5F + 1F) * var31;
                    var34 = MathHelper.sin(var32 * (float) Math.PI / var26) * var34;

                    for (int var35 = (int) (var43 - var34); var35 <= (int) (var43 + var34); ++var35) {
                        for (int var36 = (int) (var50 - var34); var36 <= (int) (var50 + var34); ++var36) {
                            for (int var37 = (int) (var33 - var34); var37 <= (int) (var33 + var34); ++var37) {
                                float var38 = var35 - var43;
                                float var39 = var36 - var50;
                                float var40 = var37 - var33;
                                if (var38 * var38 + var39 * var39 * 2F + var40 * var40 < var34
                                        * var34
                                        && var35 >= 1
                                        && var36 >= 1
                                        && var37 >= 1
                                        && var35 < var5.width - 1
                                        && var36 < var5.height - 1
                                        && var37 < var5.depth - 1) {
                                    int var66 = (var36 * var5.depth + var37) * var5.width + var35;
                                    if (var5.blocks[var66] == Block.STONE.id) {
                                        var5.blocks[var66] = 0;
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
        var5 = this;
        var51 = Block.STATIONARY_WATER.id;
        setProgress(0);

        for (var54 = 0; var54 < var5.width; ++var54) {
            var5.flood(var54, var5.height / 2 - 1, 0, var51);
            var5.flood(var54, var5.height / 2 - 1, var5.depth - 1, var51);
        }

        for (var54 = 0; var54 < var5.depth; ++var54) {
            var5.flood(0, var5.height / 2 - 1, var54, var51);
            var5.flood(var5.width - 1, var5.height / 2 - 1, var54, var51);
        }

        var54 = var5.width * var5.depth / 8000;

        for (var24 = 0; var24 < var54; ++var24) {
            if (var24 % 100 == 0) {
                var5.setProgress(var24 * 100 / (var54 - 1));
            }

            var11 = var5.random.nextInt(var5.width);
            var12 = var5.waterLevel - 1 - var5.random.nextInt(2);
            var23 = var5.random.nextInt(var5.depth);
            if (var5.blocks[(var12 * var5.depth + var23) * var5.width + var11] == 0) {
                var5.flood(var11, var12, var23, var51);
            }
        }

        var5.setProgress(100);
        progressBar.setText("Melting..");
        var5 = this;
        var46 = width * depth * height / 20000;

        for (var48 = 0; var48 < var46; ++var48) {
            if (var48 % 100 == 0) {
                var5.setProgress(var48 * 100 / (var46 - 1));
            }

            var51 = var5.random.nextInt(var5.width);
            var54 = (int) (var5.random.nextFloat() * var5.random.nextFloat() * (var5.waterLevel - 3));
            var24 = var5.random.nextInt(var5.depth);
            if (var5.blocks[(var54 * var5.depth + var24) * var5.width + var51] == 0) {
                var5.flood(var51, var54, var24, Block.STATIONARY_LAVA.id);
            }
        }

        var5.setProgress(100);
        progressBar.setText("Growing..");
        var42 = var9;
        var5 = this;
        var46 = width;
        var48 = depth;
        var51 = height;
        var53 = new OctaveNoise(random, 8);
        OctaveNoise var58 = new OctaveNoise(random, 8);

        int var63;
        for (var11 = 0; var11 < var46; ++var11) {
            var5.setProgress(var11 * 100 / (var5.width - 1));

            for (var12 = 0; var12 < var48; ++var12) {
                boolean var60 = var53.compute(var11, var12) > 8.0D;
                boolean var57 = var58.compute(var11, var12) > 12.0D;
                var27 = ((var26 = var42[var11 + var12 * var46]) * var5.depth + var12) * var5.width
                        + var11;
                if (((var28 = var5.blocks[((var26 + 1) * var5.depth + var12) * var5.width + var11] & 255) == Block.WATER.id || var28 == Block.STATIONARY_WATER.id)
                        && var26 <= var51 / 2 - 1 && var57) {
                    var5.blocks[var27] = (byte) Block.GRAVEL.id;
                }

                if (var28 == 0) {
                    var63 = Block.GRASS.id;
                    if (var26 <= var51 / 2 - 1 && var60) {
                        var63 = Block.SAND.id;
                    }

                    var5.blocks[var27] = (byte) var63;
                }
            }
        }

        progressBar.setText("Planting..");
        var42 = var9;
        var5 = this;
        var46 = width;
        var48 = width * depth / 3000;

        for (var51 = 0; var51 < var48; ++var51) {
            var54 = var5.random.nextInt(2);
            var5.setProgress(var51 * 50 / (var48 - 1));
            var24 = var5.random.nextInt(var5.width);
            var11 = var5.random.nextInt(var5.depth);

            for (var12 = 0; var12 < 10; ++var12) {
                var23 = var24;
                var25 = var11;

                for (var26 = 0; var26 < 5; ++var26) {
                    var23 += var5.random.nextInt(6) - var5.random.nextInt(6);
                    var25 += var5.random.nextInt(6) - var5.random.nextInt(6);
                    if ((var54 < 2 || var5.random.nextInt(4) == 0) && var23 >= 0 && var25 >= 0
                            && var23 < var5.width && var25 < var5.depth) {
                        var27 = var42[var23 + var25 * var46] + 1;
                        if ((var5.blocks[(var27 * var5.depth + var25) * var5.width + var23] & 255) == 0) {
                            var63 = (var27 * var5.depth + var25) * var5.width + var23;
                            if ((var5.blocks[((var27 - 1) * var5.depth + var25) * var5.width
                                    + var23] & 255) == Block.GRASS.id) {
                                if (var54 == 0) {
                                    var5.blocks[var63] = (byte) Block.DANDELION.id;
                                } else if (var54 == 1) {
                                    var5.blocks[var63] = (byte) Block.ROSE.id;
                                }
                            }
                        }
                    }
                }
            }
        }

        var42 = var9;
        var5 = this;
        var46 = width;
        var51 = width * depth * height / 2000;

        for (var54 = 0; var54 < var51; ++var54) {
            var24 = var5.random.nextInt(2);
            var5.setProgress(var54 * 50 / (var51 - 1) + 50);
            var11 = var5.random.nextInt(var5.width);
            var12 = var5.random.nextInt(var5.height);
            var23 = var5.random.nextInt(var5.depth);

            for (var25 = 0; var25 < 20; ++var25) {
                var26 = var11;
                var27 = var12;
                var28 = var23;

                for (var63 = 0; var63 < 5; ++var63) {
                    var26 += var5.random.nextInt(6) - var5.random.nextInt(6);
                    var27 += var5.random.nextInt(2) - var5.random.nextInt(2);
                    var28 += var5.random.nextInt(6) - var5.random.nextInt(6);
                    if ((var24 < 2 || var5.random.nextInt(4) == 0)
                            && var26 >= 0
                            && var28 >= 0
                            && var27 >= 1
                            && var26 < var5.width
                            && var28 < var5.depth
                            && var27 < var42[var26 + var28 * var46] - 1
                            && (var5.blocks[(var27 * var5.depth + var28) * var5.width + var26] & 255) == 0) {
                        int var62 = (var27 * var5.depth + var28) * var5.width + var26;
                        if ((var5.blocks[((var27 - 1) * var5.depth + var28) * var5.width + var26] & 255) == Block.STONE.id) {
                            if (var24 == 0) {
                                var5.blocks[var62] = (byte) Block.BROWN_MUSHROOM.id;
                            } else if (var24 == 1) {
                                var5.blocks[var62] = (byte) Block.RED_MUSHROOM.id;
                            }
                        }
                    }
                }
            }
        }

        Level var65;
        (var65 = new Level()).waterLevel = waterLevel;
        var65.setData(var2, 64, var3, blocks);
        var65.createTime = System.currentTimeMillis();
        var65.creator = var1;
        var65.name = "A Nice World";
        int[] var52 = var9;
        Level var47 = var65;
        var5 = this;
        var48 = width;
        var51 = width * depth / 4000;

        for (var54 = 0; var54 < var51; ++var54) {
            var5.setProgress(var54 * 50 / (var51 - 1) + 50);
            var24 = var5.random.nextInt(var5.width);
            var11 = var5.random.nextInt(var5.depth);

            for (var12 = 0; var12 < 20; ++var12) {
                var23 = var24;
                var25 = var11;

                for (var26 = 0; var26 < 20; ++var26) {
                    var23 += var5.random.nextInt(6) - var5.random.nextInt(6);
                    var25 += var5.random.nextInt(6) - var5.random.nextInt(6);
                    if (var23 >= 0 && var25 >= 0 && var23 < var5.width && var25 < var5.depth) {
                        var27 = var52[var23 + var25 * var48] + 1;
                        if (var5.random.nextInt(4) == 0) {
                            var47.maybeGrowTree(var23, var27, var25);
                        }
                    }
                }
            }
        }

        return var65;
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
                            if (var22 * var22 + var23 * var23 * 2F + var24 * var24 < var18
                                    * var18
                                    && var19 >= 1
                                    && var20 >= 1
                                    && var21 >= 1
                                    && var19 < width - 1
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

    private void setProgress(int var1) {
        progressBar.setProgress(var1);
    }
}
