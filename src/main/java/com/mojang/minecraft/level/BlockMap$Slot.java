package com.mojang.minecraft.level;

import java.io.Serializable;

import com.mojang.minecraft.Entity;

class BlockMap$Slot implements Serializable {

    public static final long serialVersionUID = 0L;

    // $FF: synthetic method
    static int getXSlot(BlockMap$Slot slot) {
        return slot.xSlot;
    }

    // $FF: synthetic method
    static int getYSlot(BlockMap$Slot slot) {
        return slot.ySlot;
    }

    // $FF: synthetic method
    static int getZSlot(BlockMap$Slot slot) {
        return slot.zSlot;
    }

    private int xSlot;

    private int ySlot;

    private int zSlot;

    // $FF: synthetic field
    final BlockMap blockMap;

    public BlockMap$Slot(BlockMap blockMap) {
        this.blockMap = blockMap;
    }

    public BlockMap$Slot init(float x, float y, float z) {
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

    /**
     * Adds an entity to the BlockMap slot.
     * @param entity
     */
    public void add(Entity entity) {
        if (xSlot >= 0 && ySlot >= 0 && zSlot >= 0) {
            blockMap.entityGrid[(zSlot * BlockMap.getDepth(blockMap) + ySlot)
                    * BlockMap.getWidth(blockMap) + xSlot].add(entity);
        }

    }

    /**
     * Removes an entity from the BlockMap slot.
     * @param entity
     */
    public void remove(Entity entity) {
        if (xSlot >= 0 && ySlot >= 0 && zSlot >= 0) {
            blockMap.entityGrid[(zSlot * BlockMap.getDepth(blockMap) + ySlot)
                    * BlockMap.getWidth(blockMap) + xSlot].remove(entity);
        }

    }
}
