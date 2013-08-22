package com.mojang.minecraft.level.tile;

public enum BlockID {
			Undefined ( 255 ), // for error checking

	        Air ( 0 ),
	        Stone ( 1 ),
	        Grass ( 2 ),
	        Dirt ( 3 ),
	        Cobblestone ( 4 ),
	        Wood ( 5 ),
	        Plant ( 6 ),
	        Admincrete ( 7 ),
	        Water ( 8 ),
	        StillWater ( 9 ),
	        Lava ( 10 ),
	        StillLava ( 11 ),
	        Sand ( 12 ),
	        Gravel ( 13 ),
	        GoldOre ( 14 ),
	        IronOre ( 15 ),
	        Coal ( 16 ),
	        Log ( 17 ),
	        Leaves ( 18 ),
	        Sponge ( 19 ),
	        Glass ( 20 ),

	        Red ( 21 ),
	        Orange ( 22 ),
	        Yellow ( 23 ),
	        Lime ( 24 ),
	        Green ( 25 ),
	        Teal ( 26 ),
	        Aqua ( 27 ),
	        Cyan ( 28 ),
	        Blue ( 29 ),
	        Indigo ( 30 ),
	        Violet ( 31 ),
	        Magenta ( 32 ),
	        Pink ( 33 ),
	        Black ( 34 ),
	        Gray ( 35 ),
	        White ( 36 ),

	        YellowFlower ( 37 ),
	        RedFlower ( 38 ),
	        BrownMushroom ( 39 ),
	        RedMushroom ( 40 ),

	        Gold ( 41 ),
	        Iron ( 42 ),
	        DoubleStair ( 4 ),
	        Stair ( 44 ),
	        Brick ( 45 ),
	        TNT ( 46 ),
	        Books ( 47 ),
	        MossyRocks ( 48 ),
	        Obsidian ( 49 ),
	        CobblestoneSlab ( 50 ),
	        Butter ( 51 ),
	        DarkGrass ( 52 ),
	        SpiderWeb ( 53 ),
	        LightPink ( 54 ),
	        ForestGreen ( 55 ),
	        Brown ( 56 ),
	        DeepBlue ( 57 ),
	        Turquoise ( 58 ),
	        Sandstone (59),
	        WhiteClay(60),
	        PurpleClay(61),
	        BrownClay(62),
	        GreenClay(63),
	        RedClay(64),
	        YellowClay(65),
	        LimeClay(66),
	        PinkClay(67),
	        GreyClay(68),
	        CreamClay(69),
	        OrangeClay(70),
	        PeachClay(71),
	        IndigoClay(72),
	        BlueClay(73),
	        CyanClay(74),
	        MagentaClay(75),
	        BlackClay(76),
	        CookedClay(77)
	        
	        
	        ;

	private int number;

    private BlockID(int number) {
       this.number = number;
    }

    public int getNumber() {
        return number;
    }
}