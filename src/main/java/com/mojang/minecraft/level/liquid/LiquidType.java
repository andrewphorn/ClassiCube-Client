package com.mojang.minecraft.level.liquid;

public class LiquidType {
	private LiquidType[] values;

	public static final LiquidType NOT_LIQUID = new LiquidType(0);

	public static final LiquidType WATER = new LiquidType(1);
	public static final LiquidType LAVA = new LiquidType(2);
	public static final LiquidType SNOW = new LiquidType(3);

	private LiquidType(int type) {
		values = new LiquidType[5];

		values[type] = this;
	}
}
