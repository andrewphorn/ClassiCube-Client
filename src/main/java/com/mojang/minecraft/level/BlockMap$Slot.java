package com.mojang.minecraft.level;

import java.io.Serializable;

import com.mojang.minecraft.Entity;

class BlockMap$Slot implements Serializable {

    public static final long serialVersionUID = 0L;

    // $FF: synthetic method
    static int getXSlot(BlockMap$Slot var0) {
        return var0.xSlot;
    }

    // $FF: synthetic method
    static int getYSlot(BlockMap$Slot var0) {
        return var0.ySlot;
    }

    // $FF: synthetic method
    static int getZSlot(BlockMap$Slot var0) {
        return var0.zSlot;
    }

    private int xSlot;

    private int ySlot;

    private int zSlot;

    // $FF: synthetic field
    final BlockMap blockMap;

    public BlockMap$Slot(BlockMap var1) {
        blockMap = var1;
    }

    public void add(Entity var1) {
        if (xSlot >= 0 && ySlot >= 0 && zSlot >= 0) {
            blockMap.entityGrid[(zSlot * BlockMap.getDepth(blockMap) + ySlot)
                    * BlockMap.getWidth(blockMap) + xSlot].add(var1);
        }

    }

    public BlockMap$Slot init(float var1, float var2, float var3) {
        xSlot = (int) (var1 / 16.0F);
        ySlot = (int) (var2 / 16.0F);
        zSlot = (int) (var3 / 16.0F);
        if (xSlot < 0) {
            xSlot = 0;
        }

        if (ySlot < 0) {
            ySlot = 0;
        }

        if (zSlot < 0) {
            zSlot = 0;
        }

        if (xSlot >= BlockMap.getWidth(blockMap)) {
            xSlot = BlockMap.getWidth(blockMap) - 1;
        }

        if (ySlot >= BlockMap.getDepth(blockMap)) {
            ySlot = BlockMap.getDepth(blockMap) - 1;
        }

        if (zSlot >= BlockMap.getHeight(blockMap)) {
            zSlot = BlockMap.getHeight(blockMap) - 1;
        }

        return this;
    }

    public void remove(Entity var1) {
        if (xSlot >= 0 && ySlot >= 0 && zSlot >= 0) {
            blockMap.entityGrid[(zSlot * BlockMap.getDepth(blockMap) + ySlot)
                    * BlockMap.getWidth(blockMap) + xSlot].remove(var1);
        }

    }
}
