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

public final class FlatLevelGenerator {

    private ProgressBarDisplay progressBar;
    private int width;
    private int depth;
    private int height;
    private byte[] blocks;
    private int waterLevel;

    public FlatLevelGenerator(ProgressBarDisplay var1) {
        progressBar = var1;
    }

    /**
     * Generates a level
     *
     * @param creator
     * @param width
     * @param depth
     * @param height Seems to be unused.
     * @return
     */
    public final Level generate(String creator, int width, int depth, int height) {
        progressBar.setTitle("Generating level");
        this.width = width;
        this.depth = depth;
        this.height = 64;
        waterLevel = 32;
        blocks = new byte[width * depth << 6];

        progressBar.setText("Soiling..");

        int z; //var 26
        int x; // var24
        int y; // var11
        int block_hash;
        int blok = 0;

        // Width
        for (x = 0; x < this.width; ++x) {
            if (x % 4 == 0) {
                this.setProgress(x * 100 / (this.width - 1));
            }
            // Depth
            for (y = 0; y < this.depth; ++y) {
                // Height
                for (z = 0; z < this.height; ++z) {
                    block_hash = (z * this.depth + y) * this.width + x;

                    if (z == this.waterLevel-1) {
                        blok = Block.GRASS.id;
                    }
                    if (z <= this.waterLevel-2) {
                        blok = Block.DIRT.id;
                    }
                    this.blocks[block_hash] = (byte) blok;
                    blok = 0; // Reset it.
                }
            }
        }

        Level level = new Level();
        level.waterLevel = waterLevel;
        level.setData(width, 64, depth, blocks);
        level.createTime = System.currentTimeMillis();
        level.creator = creator;
        level.name = "A Nice Flat World";

        return level;
    }
    private void setProgress(int percentage) {
        progressBar.setProgress(percentage);
    }
}
