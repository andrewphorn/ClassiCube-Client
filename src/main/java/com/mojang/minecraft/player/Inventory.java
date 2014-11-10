package com.mojang.minecraft.player;

import java.io.Serializable;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.SessionData;
import com.mojang.minecraft.level.tile.Block;

public class Inventory implements Serializable {

    public static final int POP_TIME_DURATION = 5;
    public int[] slots = new int[9];
    public int[] count = new int[9];
    public int[] popTime = new int[9];
    public int selected = 0;

    public Inventory() {
        for (int var1 = 0; var1 < 9; ++var1) {
            slots[var1] = -1;
            count[var1] = 0;
        }

    }

    public boolean addResource(int var1) {
        int var2 = getSlot(var1);
        if (var2 < 0) {
            var2 = getSlot(-1);
        }

        if (var2 < 0 || count[var2] >= 99) {
            return false;
        } else {
            slots[var2] = var1;
            ++count[var2];
            popTime[var2] = 5;
            return true;
        }
    }

    public int getSelected() {
        return slots[selected];
    }

    private int getSlot(int var1) {
        for (int var2 = 0; var2 < slots.length; ++var2) {
            if (var1 == slots[var2]) {
                return var2;
            }
        }

        return -1;
    }

    public void grabTexture(int var1, boolean var2) {
        if (GameSettings.CanReplaceSlot) {
            int var3 = getSlot(var1);
            if (var3 >= 0) {
                selected = var3;
            } else {
                if (var2 && var1 > 0 && SessionData.allowedBlocks.contains(Block.blocks[var1])) {
                    this.replaceSlot(Block.blocks[var1]);
                }
            }

        }
    }

    public boolean removeResource(int var1) {
        if ((var1 = getSlot(var1)) < 0) {
            return false;
        } else {
            if (--count[var1] <= 0) {
                slots[var1] = -1;
            }

            return true;
        }
    }

    public void replaceSlot(Block var1) {
        if (GameSettings.CanReplaceSlot && var1 != null) {
            int var2 = getSlot(var1.id);
            if (var2 >= 0) {
                slots[var2] = slots[selected];
            }

            slots[selected] = var1.id;
        }
    }

    public void replaceSlot(int var1) {
        if (GameSettings.CanReplaceSlot && var1 >= 0) {
            this.replaceSlot(SessionData.allowedBlocks.get(var1));
        }

    }

    public void swapPaint(int var1) {
        if (GameSettings.CanReplaceSlot) {
            if (var1 > 0) {
                var1 = 1;
            }

            if (var1 < 0) {
                var1 = -1;
            }

            selected -= var1;
            while (selected < 0) {
                selected += slots.length;
            }

            while (selected >= slots.length) {
                selected -= slots.length;
            }
        }

    }

    public void tick() {
        for (int var1 = 0; var1 < popTime.length; ++var1) {
            if (popTime[var1] > 0) {
                --popTime[var1];
            }
        }

    }
}
