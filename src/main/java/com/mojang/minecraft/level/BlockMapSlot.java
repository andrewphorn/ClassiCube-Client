package com.mojang.minecraft.level;

import com.mojang.minecraft.Entity;
import java.io.Serializable;

class BlockMapSlot implements Serializable {

    public static final long serialVersionUID = 0L;
    final BlockMap blockMap;
    private int xSlot;
    private int ySlot;
    private int zSlot;

    public BlockMapSlot(BlockMap blockMap) {
        this.blockMap = blockMap;
    }

    static int getXSlot(BlockMapSlot slot) {
        return slot.xSlot;
    }

    static int getYSlot(BlockMapSlot slot) {
        return slot.ySlot;
    }

    static int getZSlot(BlockMapSlot slot) {
        return slot.zSlot;
    }

    public BlockMapSlot init(float x, float y, float z) {
        xSlot = (int) (x / 16F);
        ySlot = (int) (y / 16F);
        zSlot = (int) (z / 16F);
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

    public void add(Entity entity) {
        if (xSlot >= 0 && ySlot >= 0 && zSlot >= 0) {
            blockMap.entityGrid[(zSlot * BlockMap.getDepth(blockMap) + ySlot)
                    * BlockMap.getWidth(blockMap) + xSlot].add(entity);
        }
    }

    public void remove(Entity entity) {
        if (xSlot >= 0 && ySlot >= 0 && zSlot >= 0) {
            blockMap.entityGrid[(zSlot * BlockMap.getDepth(blockMap) + ySlot)
                    * BlockMap.getWidth(blockMap) + xSlot].remove(entity);
        }
    }
}
