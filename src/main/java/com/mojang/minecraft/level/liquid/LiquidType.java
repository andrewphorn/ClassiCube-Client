package com.mojang.minecraft.level.liquid;

public class LiquidType {
    private LiquidType[] values;

    public static final LiquidType notLiquid = new LiquidType(0);

    public static final LiquidType water = new LiquidType(1);
    public static final LiquidType lava = new LiquidType(2);
    public static final LiquidType snow = new LiquidType(3);

    private LiquidType(int type) {
        values = new LiquidType[5];

        values[type] = this;
    }
}
