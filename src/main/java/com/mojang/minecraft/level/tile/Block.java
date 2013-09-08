package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.MovingObjectPosition;
import com.mojang.minecraft.item.Item;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.model.Vec3D;
import com.mojang.minecraft.particle.ParticleManager;
import com.mojang.minecraft.particle.TerrainParticle;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.ColorCache;

import java.util.Random;

public class Block {
    protected static Random random = new Random();

    public static final Block[] blocks = new Block[256];

    public static final boolean[] physics = new boolean[256];
    public static final boolean[] liquid = new boolean[256];
    public static final Block STONE;
    public static final Block GRASS;
    public static final Block DIRT;
    public static final Block COBBLESTONE;
    public static final Block WOOD;
    public static final Block SAPLING;
    public static final Block BEDROCK;
    public static final Block WATER;
    public static final Block STATIONARY_WATER;
    public static final Block LAVA;
    public static final Block STATIONARY_LAVA;
    public static final Block SAND;
    public static final Block GRAVEL;
    public static final Block GOLD_ORE;
    public static final Block IRON_ORE;
    public static final Block COAL_ORE;
    public static final Block LOG;
    public static final Block LEAVES;
    public static final Block SPONGE;
    public static final Block GLASS;
    public static final Block RED_WOOL;
    public static final Block ORANGE_WOOL;
    public static final Block YELLOW_WOOL;
    public static final Block LIME_WOOL;
    public static final Block GREEN_WOOL;
    public static final Block AQUA_GREEN_WOOL;
    public static final Block CYAN_WOOL;
    public static final Block BLUE_WOOL;
    public static final Block PURPLE_WOOL;
    public static final Block INDIGO_WOOL;
    public static final Block VIOLET_WOOL;
    public static final Block MAGENTA_WOOL;
    public static final Block PINK_WOOL;
    public static final Block BLACK_WOOL;
    public static final Block GRAY_WOOL;
    public static final Block WHITE_WOOL;
    public static final Block DANDELION;
    public static final Block ROSE;
    public static final Block BROWN_MUSHROOM;
    public static final Block RED_MUSHROOM;
    public static final Block GOLD_BLOCK;
    public static final Block IRON_BLOCK;
    public static final Block DOUBLE_SLAB;
    public static final Block SLAB;
    public static final Block BRICK;
    public static final Block TNT;
    public static final Block BOOKSHELF;
    public static final Block MOSSY_COBBLESTONE;
    public static final Block OBSIDIAN;
    public static final Block COBBLESTONESLAB;
    public static final Block ROPE;
    public static final Block SANDSTONE;
    public static final Block SNOW;
    public static final Block FIRE;
    public static final Block LIGHT_PINK_WOOL;
    public static final Block FOREST_GREEN_WOOL;
    public static final Block BROWN_WOOL;
    public static final Block DEEP_BLUE_WOOL;
    public static final Block TURQUOISE_WOOL;
    public static final Block ICE;
    public static final Block STONEBRICK;
    public static final Block QUARTZ;
    public static final Block CRATE;
    public static final Block CERAMIC_TILE;
    public static final Block MAGMA;
    public int textureId;
    public final int id;

    public Tile$SoundType stepsound;
    private int hardness;
    private boolean explodes;
    public float x1;
    public float y1;
    public float z1;
    public float x2;
    public float y2;
    public float z2;
    public float particleGravity;
    static {
	Block block = (new StoneBlock(1, 1)).setData(Tile$SoundType.stone,
		1.0F, 1.0F, 1.0F);
	Block blockCache = block;
	block.explodes = false;
	STONE = blockCache;
	GRASS = (new GrassBlock(2)).setData(Tile$SoundType.grass, 0.9F, 1.0F,
		0.6F);
	DIRT = (new DirtBlock(3, 2)).setData(Tile$SoundType.grass, 0.8F, 1.0F,
		0.5F);
	block = (new Block(4, 16)).setData(Tile$SoundType.stone, 1.0F, 1.0F,
		1.5F);
	blockCache = block;
	block.explodes = false;
	COBBLESTONE = blockCache;
	WOOD = (new Block(5, 4)).setData(Tile$SoundType.wood, 1.0F, 1.0F, 1.5F);
	SAPLING = (new SaplingBlock(6, 15)).setData(Tile$SoundType.none, 0.7F,
		1.0F, 0.0F);
	block = (new Block(7, 17)).setData(Tile$SoundType.stone, 1.0F, 1.0F,
		999.0F);
	blockCache = block;
	block.explodes = false;
	BEDROCK = blockCache;
	WATER = (new LiquidBlock(8, LiquidType.WATER)).setData(
		Tile$SoundType.none, 1.0F, 1.0F, 100.0F);
	STATIONARY_WATER = (new StillLiquidBlock(9, LiquidType.WATER)).setData(
		Tile$SoundType.none, 1.0F, 1.0F, 100.0F);
	LAVA = (new LiquidBlock(10, LiquidType.LAVA)).setData(
		Tile$SoundType.none, 1.0F, 1.0F, 100.0F);
	STATIONARY_LAVA = (new StillLiquidBlock(11, LiquidType.LAVA)).setData(
		Tile$SoundType.none, 1.0F, 1.0F, 100.0F);
	SAND = (new SandBlock(12, 18)).setData(Tile$SoundType.gravel, 0.8F,
		1.0F, 0.5F);
	GRAVEL = (new SandBlock(13, 19)).setData(Tile$SoundType.gravel, 0.8F,
		1.0F, 0.6F);
	block = (new OreBlock(14, 32)).setData(Tile$SoundType.stone, 1.0F,
		1.0F, 3.0F);
	blockCache = block;
	block.explodes = false;
	GOLD_ORE = blockCache;
	block = (new OreBlock(15, 33)).setData(Tile$SoundType.stone, 1.0F,
		1.0F, 3.0F);
	blockCache = block;
	block.explodes = false;
	IRON_ORE = blockCache;
	block = (new OreBlock(16, 34)).setData(Tile$SoundType.stone, 1.0F,
		1.0F, 3.0F);
	blockCache = block;
	block.explodes = false;
	COAL_ORE = blockCache;
	LOG = (new WoodBlock(17))
		.setData(Tile$SoundType.wood, 1.0F, 1.0F, 2.5F);
	LEAVES = (new LeavesBlock(18, 22)).setData(Tile$SoundType.grass, 1.0F,
		0.4F, 0.2F);
	SPONGE = (new SpongeBlock(19)).setData(Tile$SoundType.cloth, 1.0F,
		0.9F, 0.6F);
	GLASS = (new GlassBlock(20, 49)).setData(Tile$SoundType.metal, 1.0F,
		1.0F, 0.3F);
	RED_WOOL = (new Block(21, 64)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	ORANGE_WOOL = (new Block(22, 65)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	YELLOW_WOOL = (new Block(23, 66)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	LIME_WOOL = (new Block(24, 67)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	GREEN_WOOL = (new Block(25, 68)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	AQUA_GREEN_WOOL = (new Block(26, 69)).setData(Tile$SoundType.cloth,
		1.0F, 1.0F, 0.8F);
	CYAN_WOOL = (new Block(27, 70)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	BLUE_WOOL = (new Block(28, 71)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	PURPLE_WOOL = (new Block(29, 72)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	INDIGO_WOOL = (new Block(30, 73)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	VIOLET_WOOL = (new Block(31, 74)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	MAGENTA_WOOL = (new Block(32, 75)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	PINK_WOOL = (new Block(33, 76)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	BLACK_WOOL = (new Block(34, 77)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	GRAY_WOOL = (new Block(35, 78)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	WHITE_WOOL = (new Block(36, 79)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	DANDELION = (new FlowerBlock(37, 13)).setData(Tile$SoundType.none,
		0.7F, 1.0F, 0.0F);
	ROSE = (new FlowerBlock(38, 12)).setData(Tile$SoundType.none, 0.7F,
		1.0F, 0.0F);
	BROWN_MUSHROOM = (new MushroomBlock(39, 29)).setData(
		Tile$SoundType.none, 0.7F, 1.0F, 0.0F);
	RED_MUSHROOM = (new MushroomBlock(40, 28)).setData(Tile$SoundType.none,
		0.7F, 1.0F, 0.0F);
	block = (new Block(41, 24)).setData(Tile$SoundType.metal, 0.7F, 1.0F,
		3.0F);
	blockCache = block;
	block.explodes = false;
	GOLD_BLOCK = blockCache;
	block = (new Block(42, 23)).setData(Tile$SoundType.metal, 0.7F, 1.0F,
		5.0F);
	blockCache = block;
	block.explodes = false;
	IRON_BLOCK = blockCache;
	block = (new SlabBlock(43, true)).setData(Tile$SoundType.stone, 1.0F,
		1.0F, 2.0F);
	blockCache = block;
	block.explodes = false;
	DOUBLE_SLAB = blockCache;
	block = (new SlabBlock(44, false)).setData(Tile$SoundType.stone, 1.0F,
		1.0F, 2.0F);
	blockCache = block;
	block.explodes = false;
	SLAB = blockCache;
	block = (new Block(45, 7)).setData(Tile$SoundType.stone, 1.0F, 1.0F,
		2.0F);
	blockCache = block;
	block.explodes = false;
	BRICK = blockCache;
	TNT = (new TNTBlock(46, 8)).setData(Tile$SoundType.cloth, 1.0F, 1.0F,
		0.0F);
	BOOKSHELF = (new BookshelfBlock(47, 35)).setData(Tile$SoundType.wood,
		1.0F, 1.0F, 1.5F);
	block = (new Block(48, 36)).setData(Tile$SoundType.stone, 1.0F, 1.0F,
		1.0F);
	blockCache = block;
	block.explodes = false;
	MOSSY_COBBLESTONE = blockCache;
	block = (new StoneBlock(49, 37)).setData(Tile$SoundType.stone, 1.0F,
		1.0F, 10.0F);
	blockCache = block;
	block.explodes = false;
	OBSIDIAN = blockCache;
	block = (new CobblestoneSlabBlock(50, false)).setData(
		Tile$SoundType.stone, 1.0F, 1.0F, 2.0F);
	blockCache = block;
	block.explodes = false;
	COBBLESTONESLAB = blockCache;
	ROPE = (new RopeBlock(51, 11)).setData(Tile$SoundType.none, 0.7F, 1.0F,
		0.0F);
	block = (new SandStoneBlock(52, 41)).setData(Tile$SoundType.stone,
		0.7F, 1.0F, 3.0F);
	blockCache = block;
	block.explodes = false;
	SANDSTONE = blockCache;
	block = new SnowBlock(53, 50).setData(Tile$SoundType.none, 0.7F, 1.0F,
		3.0F);
	blockCache = block;
	block.explodes = false;
	SNOW = blockCache;
	FIRE = new FireBlock(54, 38).setData(Tile$SoundType.none, 0.7F, 1.0F,
		0.0F);
	LIGHT_PINK_WOOL = (new Block(55, 80)).setData(Tile$SoundType.cloth,
		1.0F, 1.0F, 0.8F);
	FOREST_GREEN_WOOL = (new Block(56, 81)).setData(Tile$SoundType.cloth,
		1.0F, 1.0F, 0.8F);
	BROWN_WOOL = (new Block(57, 82)).setData(Tile$SoundType.cloth, 1.0F,
		1.0F, 0.8F);
	DEEP_BLUE_WOOL = (new Block(58, 83)).setData(Tile$SoundType.cloth,
		1.0F, 1.0F, 0.8F);
	TURQUOISE_WOOL = (new Block(59, 84)).setData(Tile$SoundType.cloth,
		1.0F, 1.0F, 0.8F);
	ICE = (new IceBlock(60, 51)).setData(Tile$SoundType.metal, 1.0F, 1.0F,
		0.8F);

	CERAMIC_TILE = (new Block(61, 54)).setData(Tile$SoundType.metal, 1.0F,
		1.0F, 0.8F);
	MAGMA = (new MagmaBlock(62, 86)).setData(Tile$SoundType.metal, 1.0F,
		1.0F, 0.8F);
	QUARTZ = (new MetalBlock(63, 42)).setData(Tile$SoundType.metal, 1.0F,
		1.0F, 0.8F);
	CRATE = (new Block(64, 53)).setData(Tile$SoundType.wood, 1.0F, 1.0F,
		0.8F);
	STONEBRICK = (new Block(65, 52)).setData(Tile$SoundType.stone, 1.0F,
		1.0F, 0.8F);
    }
    public Block(int id) {
	explodes = true;
	blocks[id] = this;
	this.id = id;

	setBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

	liquid[id] = false;
    }

    protected Block(int id, int textureID) {
	this(id);

	textureId = textureID;
    }

    public final boolean canExplode() {
	return this.explodes;
    }

    public boolean canRenderSide(Level level, int x, int y, int z, int side) {
	return !level.isSolidTile(x, y, z);
    }

    public final MovingObjectPosition clip(int var1, int var2, int var3,
	    Vec3D var4, Vec3D var5) {
	var4 = var4.add((float) (-var1), (float) (-var2), (float) (-var3));
	var5 = var5.add((float) (-var1), (float) (-var2), (float) (-var3));
	Vec3D var6 = var4.getXIntersection(var5, this.x1);
	Vec3D var7 = var4.getXIntersection(var5, this.x2);
	Vec3D var8 = var4.getYIntersection(var5, this.y1);
	Vec3D var9 = var4.getYIntersection(var5, this.y2);
	Vec3D var10 = var4.getZIntersection(var5, this.z1);
	var5 = var4.getZIntersection(var5, this.z2);
	if (!this.xIntersects(var6)) {
	    var6 = null;
	}

	if (!this.xIntersects(var7)) {
	    var7 = null;
	}

	if (!this.yIntersects(var8)) {
	    var8 = null;
	}

	if (!this.yIntersects(var9)) {
	    var9 = null;
	}

	if (!this.zIntersects(var10)) {
	    var10 = null;
	}

	if (!this.zIntersects(var5)) {
	    var5 = null;
	}

	Vec3D var11 = null;
	if (var6 != null) {
	    var11 = var6;
	}

	if (var7 != null
		&& (var11 == null || var4.distance(var7) < var4.distance(var11))) {
	    var11 = var7;
	}

	if (var8 != null
		&& (var11 == null || var4.distance(var8) < var4.distance(var11))) {
	    var11 = var8;
	}

	if (var9 != null
		&& (var11 == null || var4.distance(var9) < var4.distance(var11))) {
	    var11 = var9;
	}

	if (var10 != null
		&& (var11 == null || var4.distance(var10) < var4
			.distance(var11))) {
	    var11 = var10;
	}

	if (var5 != null
		&& (var11 == null || var4.distance(var5) < var4.distance(var11))) {
	    var11 = var5;
	}

	if (var11 == null) {
	    return null;
	} else {
	    byte var12 = -1;
	    if (var11 == var6) {
		var12 = 4;
	    }

	    if (var11 == var7) {
		var12 = 5;
	    }

	    if (var11 == var8) {
		var12 = 0;
	    }

	    if (var11 == var9) {
		var12 = 1;
	    }

	    if (var11 == var10) {
		var12 = 2;
	    }

	    if (var11 == var5) {
		var12 = 3;
	    }

	    return new MovingObjectPosition(var1, var2, var3, var12, var11.add(
		    (float) var1, (float) var2, (float) var3));
	}
    }

    public void dropItems(Level var1, int var2, int var3, int var4, float var5) {
	if (!var1.creativeMode) {
	    int var6 = this.getDropCount();

	    for (int var7 = 0; var7 < var6; ++var7) {
		if (random.nextFloat() <= var5) {
		    float var8 = 0.7F;
		    float var9 = random.nextFloat() * var8 + (1.0F - var8)
			    * 0.5F;
		    float var10 = random.nextFloat() * var8 + (1.0F - var8)
			    * 0.5F;
		    var8 = random.nextFloat() * var8 + (1.0F - var8) * 0.5F;
		    var1.addEntity(new Item(var1, (float) var2 + var9,
			    (float) var3 + var10, (float) var4 + var8, this
				    .getDrop()));
		}
	    }

	}
    }

    public void explode(Level var1, int var2, int var3, int var4) {
    }

    protected ColorCache getBrightness(Level level, int x, int y, int z) {
	return level.getBrightnessColor(x, y, z);
    }

    public AABB getCollisionBox(int x, int y, int z) {
	AABB aabb = new AABB((float) x + x1, (float) y + y1, (float) z + z1,
		(float) x + x2, (float) y + y2, (float) z + z2);
	;

	return aabb;
    }

    public int getDrop() {
	return this.id;
    }

    public int getDropCount() {
	return 1;
    }

    public final int getHardness() {
	return this.hardness;
    }

    public LiquidType getLiquidType() {
	return LiquidType.NOT_LIQUID;
    }

    public int getRenderPass() {
	return 0;
    }

    public AABB getSelectionBox(int x, int y, int z) {
	AABB aabb = new AABB((float) x + x1, (float) y + y1, (float) z + z1,
		(float) x + x2, (float) y + y2, (float) z + z2);
	;

	return aabb;
    }

    protected int getTextureId(int texture) {
	return textureId;
    }

    public int getTickDelay() {
	return 0;
    }

    public boolean isCube() {
	return true;
    }

    public boolean isOpaque() {
	return true;
    }

    public boolean isSolid() {
	return true;
    }

    public void onAdded(Level level, int x, int y, int z) {
    }

    public void onBreak(Level var1, int var2, int var3, int var4) {
	this.dropItems(var1, var2, var3, var4, 1.0F);
    }

    // TODO.
    public void onNeighborChange(Level var1, int var2, int var3, int var4,
	    int var5) {
    }

    public void onPlace(Level level, int x, int y, int z) {
    }

    // TODO past here.

    public void onRemoved(Level var1, int var2, int var3, int var4) {
    }

    public boolean render(Level var1, int var2, int var3, int var4,
	    ShapeRenderer var5) {
	boolean var6 = false;
	float var7 = 0.5F;
	float var8 = 0.8F;
	float var9 = 0.6F;
	ColorCache var10;
	if (this.canRenderSide(var1, var2, var3 - 1, var4, 0)) {
	    var10 = this.getBrightness(var1, var2, var3 - 1, var4);
	    var5.color(var7 * var10.R, var7 * var10.G, var7 * var10.B);
	    this.renderInside(var5, var2, var3, var4, 0);
	    var6 = true;
	}

	if (this.canRenderSide(var1, var2, var3 + 1, var4, 1)) {
	    var10 = this.getBrightness(var1, var2, var3 + 1, var4);
	    var5.color(var10.R * 1.0F, var10.G * 1.0F, var10.B * 1.0F);
	    this.renderInside(var5, var2, var3, var4, 1);
	    var6 = true;
	}

	if (this.canRenderSide(var1, var2, var3, var4 - 1, 2)) {
	    var10 = this.getBrightness(var1, var2, var3, var4 - 1);
	    var5.color(var8 * var10.R, var8 * var10.G, var8 * var10.B);
	    this.renderInside(var5, var2, var3, var4, 2);
	    var6 = true;
	}

	if (this.canRenderSide(var1, var2, var3, var4 + 1, 3)) {
	    var10 = this.getBrightness(var1, var2, var3, var4 + 1);
	    var5.color(var8 * var10.R, var8 * var10.G, var8 * var10.B);
	    this.renderInside(var5, var2, var3, var4, 3);
	    var6 = true;
	}

	if (this.canRenderSide(var1, var2 - 1, var3, var4, 4)) {
	    var10 = this.getBrightness(var1, var2 - 1, var3, var4);
	    var5.color(var9 * var10.R, var9 * var10.G, var9 * var10.B);
	    this.renderInside(var5, var2, var3, var4, 4);
	    var6 = true;
	}

	if (this.canRenderSide(var1, var2 + 1, var3, var4, 5)) {
	    var10 = this.getBrightness(var1, var2 + 1, var3, var4);
	    var5.color(var9 * var10.R, var9 * var10.G, var9 * var10.B);
	    this.renderInside(var5, var2, var3, var4, 5);
	    var6 = true;
	}

	return var6;
    }

    public void renderFullbright(ShapeRenderer shapeRenderer) {
	float red = 0.5F;
	float green = 0.8F;
	float blue = 0.6F;

	shapeRenderer.color(red, red, red);
	renderInside(shapeRenderer, -2, 0, 0, 0);

	shapeRenderer.color(1.0F, 1.0F, 1.0F);
	renderInside(shapeRenderer, -2, 0, 0, 1);

	shapeRenderer.color(green, green, green);
	renderInside(shapeRenderer, -2, 0, 0, 2);

	shapeRenderer.color(green, green, green);
	renderInside(shapeRenderer, -2, 0, 0, 3);

	shapeRenderer.color(blue, blue, blue);
	renderInside(shapeRenderer, -2, 0, 0, 4);

	shapeRenderer.color(blue, blue, blue);
	renderInside(shapeRenderer, -2, 0, 0, 5);
    }

    public void renderInside(ShapeRenderer shapeRenderer, int x, int y, int z,
	    int side) {
	int textureID1 = getTextureId(side);

	renderSide(shapeRenderer, x, y, z, side, textureID1);
    }

    public void renderPreview(ShapeRenderer var1) {
	var1.begin();

	for (int var2 = 0; var2 < 6; ++var2) {
	    if (var2 == 0) {
		var1.normal(0.0F, 1.0F, 0.0F);
	    }

	    if (var2 == 1) {
		var1.normal(0.0F, -1.0F, 0.0F);
	    }

	    if (var2 == 2) {
		var1.normal(0.0F, 0.0F, 1.0F);
	    }

	    if (var2 == 3) {
		var1.normal(0.0F, 0.0F, -1.0F);
	    }

	    if (var2 == 4) {
		var1.normal(1.0F, 0.0F, 0.0F);
	    }

	    if (var2 == 5) {
		var1.normal(-1.0F, 0.0F, 0.0F);
	    }

	    this.renderInside(var1, 0, 0, 0, var2);
	}

	var1.end();
    }

    // TODO.
    public void renderSide(ShapeRenderer var1, int var2, int var3, int var4,
	    int var5) {

	int var6;
	float var7;
	float var8 = (var7 = (float) ((var6 = this.getTextureId(var5)) % 16) / 16.0F) + 0.0624375F;
	float var16;
	float var9 = (var16 = (float) (var6 / 16) / 16.0F) + 0.0624375F;
	float var10 = (float) var2 + this.x1;
	float var14 = (float) var2 + this.x2;
	float var11 = (float) var3 + this.y1;
	float var15 = (float) var3 + this.y2;
	float var12 = (float) var4 + this.z1;
	float var13 = (float) var4 + this.z2;
	if (var5 == 0) {
	    var1.vertexUV(var14, var11, var13, var8, var9);
	    var1.vertexUV(var14, var11, var12, var8, var16);
	    var1.vertexUV(var10, var11, var12, var7, var16);
	    var1.vertexUV(var10, var11, var13, var7, var9);
	}

	if (var5 == 1) {
	    var1.vertexUV(var10, var15, var13, var7, var9);
	    var1.vertexUV(var10, var15, var12, var7, var16);
	    var1.vertexUV(var14, var15, var12, var8, var16);
	    var1.vertexUV(var14, var15, var13, var8, var9);
	}

	if (var5 == 2) {
	    var1.vertexUV(var10, var11, var12, var8, var9);
	    var1.vertexUV(var14, var11, var12, var7, var9);
	    var1.vertexUV(var14, var15, var12, var7, var16);
	    var1.vertexUV(var10, var15, var12, var8, var16);
	}

	if (var5 == 3) {
	    var1.vertexUV(var14, var15, var13, var8, var16);
	    var1.vertexUV(var14, var11, var13, var8, var9);
	    var1.vertexUV(var10, var11, var13, var7, var9);
	    var1.vertexUV(var10, var15, var13, var7, var16);
	}

	if (var5 == 4) {
	    var1.vertexUV(var10, var11, var13, var8, var9);
	    var1.vertexUV(var10, var11, var12, var7, var9);
	    var1.vertexUV(var10, var15, var12, var7, var16);
	    var1.vertexUV(var10, var15, var13, var8, var16);
	}

	if (var5 == 5) {
	    var1.vertexUV(var14, var15, var13, var7, var16);
	    var1.vertexUV(var14, var15, var12, var8, var16);
	    var1.vertexUV(var14, var11, var12, var8, var9);
	    var1.vertexUV(var14, var11, var13, var7, var9);
	}

    }

    // TODO.
    public void renderSide(ShapeRenderer shapeRenderer, int x, int y, int z,
	    int side, int textureID) {
	int var7 = textureID % 16 << 4;
	int var8 = textureID / 16 << 4;
	float var9 = (float) var7 / 256.0F;
	float var17 = ((float) var7 + 15.99F) / 256.0F;
	float var10 = (float) var8 / 256.0F;
	float var11 = ((float) var8 + 15.99F) / 256.0F;
	if (side >= 2 && textureID < 240) {
	    if (this.y1 >= 0.0F && this.y2 <= 1.0F) {
		var10 = ((float) var8 + this.y1 * 15.99F) / 256.0F;
		var11 = ((float) var8 + this.y2 * 15.99F) / 256.0F;
	    } else {
		var10 = (float) var8 / 256.0F;
		var11 = ((float) var8 + 15.99F) / 256.0F;
	    }
	}

	float var16 = (float) x + this.x1;
	float var14 = (float) x + this.x2;
	float var18 = (float) y + this.y1;
	float var15 = (float) y + this.y2;
	float var12 = (float) z + this.z1;
	float var13 = (float) z + this.z2;
	if (side == 0) {
	    shapeRenderer.vertexUV(var16, var18, var13, var9, var11);
	    shapeRenderer.vertexUV(var16, var18, var12, var9, var10);
	    shapeRenderer.vertexUV(var14, var18, var12, var17, var10);
	    shapeRenderer.vertexUV(var14, var18, var13, var17, var11);
	} else if (side == 1) {
	    shapeRenderer.vertexUV(var14, var15, var13, var17, var11);
	    shapeRenderer.vertexUV(var14, var15, var12, var17, var10);
	    shapeRenderer.vertexUV(var16, var15, var12, var9, var10);
	    shapeRenderer.vertexUV(var16, var15, var13, var9, var11);
	} else if (side == 2) {
	    shapeRenderer.vertexUV(var16, var15, var12, var17, var10);
	    shapeRenderer.vertexUV(var14, var15, var12, var9, var10);
	    shapeRenderer.vertexUV(var14, var18, var12, var9, var11);
	    shapeRenderer.vertexUV(var16, var18, var12, var17, var11);
	} else if (side == 3) {
	    shapeRenderer.vertexUV(var16, var15, var13, var9, var10);
	    shapeRenderer.vertexUV(var16, var18, var13, var9, var11);
	    shapeRenderer.vertexUV(var14, var18, var13, var17, var11);
	    shapeRenderer.vertexUV(var14, var15, var13, var17, var10);
	} else if (side == 4) {
	    shapeRenderer.vertexUV(var16, var15, var13, var17, var10);
	    shapeRenderer.vertexUV(var16, var15, var12, var9, var10);
	    shapeRenderer.vertexUV(var16, var18, var12, var9, var11);
	    shapeRenderer.vertexUV(var16, var18, var13, var17, var11);
	} else if (side == 5) {
	    shapeRenderer.vertexUV(var14, var18, var13, var9, var11);
	    shapeRenderer.vertexUV(var14, var18, var12, var17, var11);
	    shapeRenderer.vertexUV(var14, var15, var12, var17, var10);
	    shapeRenderer.vertexUV(var14, var15, var13, var9, var10);
	}
    }

    protected void setBounds(float x1, float y1, float z1, float x2, float y2,
	    float z2) {
	this.x1 = x1;
	this.y1 = y1;
	this.z1 = z1;
	this.x2 = x2;
	this.y2 = y2;
	this.z2 = z2;
    }

    protected Block setData(Tile$SoundType soundType, float var2,
	    float particleGravity, float hardness) {
	this.particleGravity = particleGravity;
	this.stepsound = soundType;
	this.hardness = (int) (hardness * 20.0F);

	if (this instanceof FlowerBlock) {
	    stepsound = Tile$SoundType.grass;
	}

	return this;
    }

    protected void setPhysics(boolean physics) {
	Block.physics[id] = physics;
    }

    // TODO.
    public final void spawnBlockParticles(Level var1, int var2, int var3,
	    int var4, int var5, ParticleManager var6) {
	float var7 = 0.1F;
	float var8 = (float) var2 + random.nextFloat()
		* (this.x2 - this.x1 - var7 * 2.0F) + var7 + this.x1;
	float var9 = (float) var3 + random.nextFloat()
		* (this.y2 - this.y1 - var7 * 2.0F) + var7 + this.y1;
	float var10 = (float) var4 + random.nextFloat()
		* (this.z2 - this.z1 - var7 * 2.0F) + var7 + this.z1;
	if (var5 == 0) {
	    var9 = (float) var3 + this.y1 - var7;
	}

	if (var5 == 1) {
	    var9 = (float) var3 + this.y2 + var7;
	}

	if (var5 == 2) {
	    var10 = (float) var4 + this.z1 - var7;
	}

	if (var5 == 3) {
	    var10 = (float) var4 + this.z2 + var7;
	}

	if (var5 == 4) {
	    var8 = (float) var2 + this.x1 - var7;
	}

	if (var5 == 5) {
	    var8 = (float) var2 + this.x2 + var7;
	}

	var6.spawnParticle((new TerrainParticle(var1, var8, var9, var10, 0.0F,
		0.0F, 0.0F, this)).setPower(0.2F).scale(0.6F));
    }

    // TODO.
    public void spawnBreakParticles(Level level, int x, int y, int z,
	    ParticleManager particleManager) {
	for (int var6 = 0; var6 < 4; ++var6) {
	    for (int var7 = 0; var7 < 4; ++var7) {
		for (int var8 = 0; var8 < 4; ++var8) {
		    float var9 = (float) x + ((float) var6 + 0.5F) / (float) 4;
		    float var10 = (float) y + ((float) var7 + 0.5F) / (float) 4;
		    float var11 = (float) z + ((float) var8 + 0.5F) / (float) 4;

		    particleManager.spawnParticle(new TerrainParticle(level,
			    var9, var10, var11, var9 - (float) x - 0.5F, var10
				    - (float) y - 0.5F, var11 - (float) z
				    - 0.5F, this));
		}
	    }
	}

    }

    public void update(Level level, int x, int y, int z, Random rand) {
    }

    private boolean xIntersects(Vec3D var1) {
	return var1 == null ? false : var1.y >= this.y1 && var1.y <= this.y2
		&& var1.z >= this.z1 && var1.z <= this.z2;
    }

    private boolean yIntersects(Vec3D var1) {
	return var1 == null ? false : var1.x >= this.x1 && var1.x <= this.x2
		&& var1.z >= this.z1 && var1.z <= this.z2;
    }

    private boolean zIntersects(Vec3D var1) {
	return var1 == null ? false : var1.x >= this.x1 && var1.x <= this.x2
		&& var1.y >= this.y1 && var1.y <= this.y2;
    }
}
