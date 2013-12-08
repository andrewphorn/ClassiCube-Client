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
import com.mojang.minecraft.sound.StepSound;
import com.mojang.minecraft.sound.StepSoundStone;
import com.mojang.minecraft.sound.StepSoundSand;

import java.util.Random;

public class Block {
	protected static Random random = new Random();

	public static final Block[] blocks = new Block[256];

	public static final boolean[] physics = new boolean[256];
	public static final boolean[] liquid = new boolean[256];
        
        public StepSound stepSound;
      
        public static final StepSound soundNone = new StepSound("-", 0.0F, 0.0F);
        public static final StepSound soundPowderFootstep = new StepSound("stone", 1.0F, 1.0F);
        public static final StepSound soundWoodFootstep = new StepSound("wood", 1.0F, 1.0F);
        public static final StepSound soundGravelFootstep = new StepSound("gravel", 1.0F, 1.0F);
        public static final StepSound soundGrassFootstep = new StepSound("grass", 1.0F, 1.0F);
        public static final StepSound soundStoneFootstep = new StepSound("stone", 1.0F, 1.0F);
        public static final StepSound soundMetalFootstep = new StepSound("stone", 1.0F, 1.5F);
        public static final StepSound soundGlassFootstep = new StepSoundStone("stone", 1.0F, 1.0F);
        public static final StepSound soundClothFootstep = new StepSound("cloth", 1.0F, 1.0F);
        public static final StepSound soundSandFootstep = new StepSound("sand", 1.0F, 1.0F);
        public static final StepSound soundSnowFootstep = new StepSound("snow", 1.0F, 1.0F);
        public static final StepSound soundLadderFootstep = new StepSoundSand("ladder", 1.0F, 1.0F);

	public int textureId;
	public final int id;

	private int hardness;
	private boolean explodes;
	public float x1;
	public float y1;
	public float z1;
	public float x2;
	public float y2;
	public float z2;
	public float particleGravity;
        public boolean isLiquid;
        
        public static final Block stone = (new StoneBlock(1)).setTextureId(1).setStepSound(soundStoneFootstep).setParticleGravity(1.0F).setHardness(1.0F);
        public static final Block grass = (new GrassBlock(2)).setStepSound(soundGrassFootstep).setTextureId(2).setParticleGravity(1.0F).setHardness(0.6F);
        public static final Block dirt = (new DirtBlock(3)).setStepSound(soundGravelFootstep).setTextureId(2).setParticleGravity(1.0F).setHardness(0.5F);
        public static final Block cobblestone = (new CobblestoneBlock(4)).setStepSound(soundStoneFootstep).setTextureId(16).setParticleGravity(1.0F).setHardness(1.5F);
        public static final Block plank = (new Block(5)).setStepSound(soundWoodFootstep).setTextureId(4).setParticleGravity(1.0F).setHardness(1.5F); //Used to be known as 'WOOD'
        public static final Block sapling = (new FlowerBlock(6)).setStepSound(soundGrassFootstep).setTextureId(15).setParticleGravity(1.0F).setHardness(0.0F);
        public static final Block bedrock = (new Block(7)).setStepSound(soundStoneFootstep).setTextureId(17).setParticleGravity(1.0F).setHardness(999.0F);
        public static final Block water = (new LiquidBlock(8, LiquidType.water)).setParticleGravity(1.0F).setHardness(100.0F).setLiquid(true);
        public static final Block stationaryWater = (new StillLiquidBlock(9, LiquidType.water)).setParticleGravity(1.0F).setHardness(100.0F).setLiquid(true);
        public static final Block lava = (new LiquidBlock(10, LiquidType.lava)).setParticleGravity(1.0F).setHardness(100.0F).setLiquid(true);
        public static final Block stationaryLava = (new StillLiquidBlock(11, LiquidType.lava)).setParticleGravity(1.0F).setHardness(100.0F).setLiquid(true);
        public static final Block sand = (new SandBlock(12)).setStepSound(soundSandFootstep).setTextureId(18).setParticleGravity(1.0F).setHardness(0.5F);
        public static final Block gravel = (new SandBlock(13)).setStepSound(soundGravelFootstep).setTextureId(19).setParticleGravity(1.0F).setHardness(0.6F);
        public static final Block goldOre = (new OreBlock(14)).setStepSound(soundStoneFootstep).setTextureId(32).setParticleGravity(1.0F).setHardness(0.5F);
        public static final Block ironOre = (new OreBlock(15)).setStepSound(soundStoneFootstep).setTextureId(33).setParticleGravity(1.0F).setHardness(0.5F);
        public static final Block coalOre = (new OreBlock(16)).setStepSound(soundStoneFootstep).setTextureId(34).setParticleGravity(1.0F).setHardness(0.5F);
        public static final Block log = (new WoodBlock(17)).setStepSound(soundWoodFootstep).setParticleGravity(1.0F).setHardness(2.5F);
        public static final Block leaves = (new LeavesBlock(18)).setStepSound(soundGrassFootstep).setTextureId(22).setParticleGravity(0.4F).setHardness(0.2F);
        public static final Block sponge = (new SpongeBlock(19)).setStepSound(soundGrassFootstep).setTextureId(48).setParticleGravity(0.9F).setHardness(0.6F);
        
        public static final Block glass = (new GlassBlock(20)).setStepSound(soundGlassFootstep).setTextureId(49).setParticleGravity(1.0F).setHardness(0.3F);
        public static final Block woolRed = (new Block(21)).setStepSound(soundClothFootstep).setTextureId(64).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolOrange = (new Block(22)).setStepSound(soundClothFootstep).setTextureId(65).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolYellow = (new Block(23)).setStepSound(soundClothFootstep).setTextureId(66).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolLime = (new Block(24)).setStepSound(soundClothFootstep).setTextureId(67).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolGreen = (new Block(25)).setStepSound(soundClothFootstep).setTextureId(68).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolAquaGreen = (new Block(26)).setStepSound(soundClothFootstep).setTextureId(69).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolCyan = (new Block(27)).setStepSound(soundClothFootstep).setTextureId(70).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolBlue = (new Block(28)).setStepSound(soundClothFootstep).setTextureId(71).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolPurple = (new Block(29)).setStepSound(soundClothFootstep).setTextureId(72).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolIndigo = (new Block(30)).setStepSound(soundClothFootstep).setTextureId(73).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolViolet = (new Block(31)).setStepSound(soundClothFootstep).setTextureId(74).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolMagenta = (new Block(32)).setStepSound(soundClothFootstep).setTextureId(75).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolPink = (new Block(33)).setStepSound(soundClothFootstep).setTextureId(76).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolBlack = (new Block(34)).setStepSound(soundClothFootstep).setTextureId(77).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolGray = (new Block(35)).setStepSound(soundClothFootstep).setTextureId(78).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolWhite = (new Block(36)).setStepSound(soundClothFootstep).setTextureId(79).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block dandelion = (new FlowerBlock(37)).setStepSound(soundGrassFootstep).setTextureId(13).setParticleGravity(1.0F).setHardness(0.0F);
        public static final Block rose = (new FlowerBlock(38)).setStepSound(soundGrassFootstep).setTextureId(12).setParticleGravity(1.0F).setHardness(0.0F);
        public static final Block brownMushroom = (new MushroomBlock(39)).setStepSound(soundGrassFootstep).setTextureId(29).setParticleGravity(1.0F).setHardness(0.0F);
        public static final Block redMushroom = (new MushroomBlock(40)).setStepSound(soundGrassFootstep).setTextureId(28).setParticleGravity(1.0F).setHardness(0.0F);
        public static final Block goldBlock = (new Block(41)).setStepSound(soundMetalFootstep).setTextureId(24).setParticleGravity(1.0F).setHardness(3.0F);
        public static final Block ironBlock = (new Block(42)).setStepSound(soundMetalFootstep).setTextureId(23).setParticleGravity(1.0F).setHardness(5.0F);
        public static final Block stoneDoubleSlab = (new SlabBlock(43, true)).setStepSound(soundStoneFootstep).setParticleGravity(1.0F).setHardness(2.0F);
        public static final Block stoneSlab = (new SlabBlock(44, false)).setStepSound(soundStoneFootstep).setParticleGravity(1.0F).setHardness(2.0F);
        public static final Block brick = (new Block(45)).setStepSound(soundStoneFootstep).setTextureId(7).setParticleGravity(1.0F).setHardness(2.0F);
        public static final Block tnt = (new TNTBlock(46)).setStepSound(soundGrassFootstep).setTextureId(8).setParticleGravity(1.0F).setHardness(0.0F);
        public static final Block bookshelf = (new BookshelfBlock(47)).setStepSound(soundWoodFootstep).setTextureId(35).setParticleGravity(1.0F).setHardness(1.5F);
        public static final Block cobblestoneMossy = (new Block(48)).setStepSound(soundStoneFootstep).setTextureId(36).setParticleGravity(1.0F).setHardness(1.0F);
        public static final Block obsidian = (new StoneBlock(49)).setStepSound(soundStoneFootstep).setTextureId(37).setParticleGravity(1.0F).setHardness(10.0F);
        public static final Block slabCobblestone = (new CobblestoneSlabBlock(50, false)).setStepSound(soundStoneFootstep).setTextureId(6).setParticleGravity(1.0F).setHardness(1.0F);

        public static final Block rope = (new RopeBlock(51)).setStepSound(soundLadderFootstep).setTextureId(11).setParticleGravity(1.0F).setHardness(0.0F);
        public static final Block sandstone = (new SandStoneBlock(52)).setStepSound(soundStoneFootstep).setTextureId(41).setParticleGravity(1.0F).setHardness(3.0F);
        public static final Block snow = new SnowBlock(53).setStepSound(soundSnowFootstep).setTextureId(50).setParticleGravity(1.0F).setHardness(3.0F);
        public static final Block fire = new FireBlock(54).setStepSound(soundWoodFootstep).setTextureId(38).setParticleGravity(1.0F).setHardness(0.0F);
        public static final Block woolLightPink = (new Block(55)).setStepSound(soundClothFootstep).setTextureId(80).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolForestGreen = (new Block(56)).setStepSound(soundClothFootstep).setTextureId(81).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolBrown = (new Block(57)).setStepSound(soundClothFootstep).setTextureId(82).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block woolDeepBlue = (new Block(58)).setStepSound(soundClothFootstep).setTextureId(83).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block wollTurqoise = (new Block(59)).setStepSound(soundClothFootstep).setTextureId(84).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block ice = (new IceBlock(60)).setStepSound(soundGlassFootstep).setTextureId(51).setParticleGravity(1.0F).setHardness(0.8F);

        public static final Block ceramicTile = (new Block(61)).setStepSound(soundStoneFootstep).setTextureId(54).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block magma = (new MagmaBlock(62)).setStepSound(soundStoneFootstep).setTextureId(86).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block quartz = (new MetalBlock(63)).setStepSound(soundStoneFootstep).setTextureId(42).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block crate = (new Block(64)).setStepSound(soundWoodFootstep).setTextureId(53).setParticleGravity(1.0F).setHardness(0.8F);
        public static final Block stonebrick = (new Block(65)).setStepSound(soundStoneFootstep).setTextureId(52).setParticleGravity(1.0F).setHardness(0.8F);

	public Block(int id) {
		explodes = true;
		blocks[id] = this;
		this.id = id;

		setBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

		liquid[id] = false;
                isLiquid = false;
	}

	protected Block(int id, int textureID) {
		this(id);

		textureId = textureID;
	}
        
        protected Block setTextureId(int var1)
        {
        this.textureId = var1;
        return this;
        }
        
        protected Block setHardness(float var1)
        {
        this.hardness = (int) (var1 * 20.0F);
        return this;
        }
        
        protected Block setLiquid(boolean var1)
        {
        this.isLiquid = var1;
        return this;
        }
        
        public final boolean isLiquid() {
		return this.isLiquid;
	}
        
        protected Block setParticleGravity(float var1)
        {
        this.particleGravity = var1;
        return this;
        }
        
        protected Block setStepSound(StepSound par1StepSound)
        {
        this.stepSound = par1StepSound;
        return this;
        }

	public final boolean canExplode() {
		return this.explodes;
	}

	public boolean canRenderSide(Level level, int x, int y, int z, int side) {
		return !level.isSolidTile(x, y, z);
	}

	public final MovingObjectPosition clip(int var1, int var2, int var3, Vec3D var4, Vec3D var5) {
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

		if (var7 != null && (var11 == null || var4.distance(var7) < var4.distance(var11))) {
			var11 = var7;
		}

		if (var8 != null && (var11 == null || var4.distance(var8) < var4.distance(var11))) {
			var11 = var8;
		}

		if (var9 != null && (var11 == null || var4.distance(var9) < var4.distance(var11))) {
			var11 = var9;
		}

		if (var10 != null && (var11 == null || var4.distance(var10) < var4.distance(var11))) {
			var11 = var10;
		}

		if (var5 != null && (var11 == null || var4.distance(var5) < var4.distance(var11))) {
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

			return new MovingObjectPosition(var1, var2, var3, var12, var11.add((float) var1,
					(float) var2, (float) var3));
		}
	}

	public void dropItems(Level var1, int var2, int var3, int var4, float var5) {
		if (!var1.creativeMode) {
			int var6 = this.getDropCount();

			for (int var7 = 0; var7 < var6; ++var7) {
				if (random.nextFloat() <= var5) {
					float var8 = 0.7F;
					float var9 = random.nextFloat() * var8 + (1.0F - var8) * 0.5F;
					float var10 = random.nextFloat() * var8 + (1.0F - var8) * 0.5F;
					var8 = random.nextFloat() * var8 + (1.0F - var8) * 0.5F;
					var1.addEntity(new Item(var1, (float) var2 + var9, (float) var3 + var10,
							(float) var4 + var8, this.getDrop()));
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
		AABB aabb = new AABB((float) x + x1, (float) y + y1, (float) z + z1, (float) x + x2,
				(float) y + y2, (float) z + z2);
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
		return LiquidType.notLiquid;
	}

	public int getRenderPass() {
		return 0;
	}

	public AABB getSelectionBox(int x, int y, int z) {
		AABB aabb = new AABB((float) x + x1, (float) y + y1, (float) z + z1, (float) x + x2,
				(float) y + y2, (float) z + z2);
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
	public void onNeighborChange(Level var1, int var2, int var3, int var4, int var5) {
	}

	public void onPlace(Level level, int x, int y, int z) {
	}

	// TODO past here.

	public void onRemoved(Level var1, int var2, int var3, int var4) {
	}

	public boolean render(Level var1, int var2, int var3, int var4, ShapeRenderer var5) {
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

	public void renderInside(ShapeRenderer shapeRenderer, int x, int y, int z, int side) {
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
	public void renderSide(ShapeRenderer var1, int var2, int var3, int var4, int var5) {
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
	public void renderSide(ShapeRenderer shapeRenderer, int x, int y, int z, int side, int textureID) {
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

	protected void setBounds(float x1, float y1, float z1, float x2, float y2, float z2) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}

	protected void setPhysics(boolean physics) {
		Block.physics[id] = physics;
	}

	// TODO.
	public final void spawnBlockParticles(Level var1, int var2, int var3, int var4, int var5,
			ParticleManager var6) {
		float var7 = 0.1F;
		float var8 = (float) var2 + random.nextFloat() * (this.x2 - this.x1 - var7 * 2.0F) + var7
				+ this.x1;
		float var9 = (float) var3 + random.nextFloat() * (this.y2 - this.y1 - var7 * 2.0F) + var7
				+ this.y1;
		float var10 = (float) var4 + random.nextFloat() * (this.z2 - this.z1 - var7 * 2.0F) + var7
				+ this.z1;
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

		var6.spawnParticle((new TerrainParticle(var1, var8, var9, var10, 0.0F, 0.0F, 0.0F, this))
				.setPower(0.2F).scale(0.6F));
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

					particleManager.spawnParticle(new TerrainParticle(level, var9, var10, var11,
							var9 - (float) x - 0.5F, var10 - (float) y - 0.5F, var11 - (float) z
									- 0.5F, this));
				}
			}
		}

	}

	public void update(Level level, int x, int y, int z, Random rand) {
	}

	private boolean xIntersects(Vec3D var1) {
		return var1 == null ? false : var1.y >= this.y1 && var1.y <= this.y2 && var1.z >= this.z1
				&& var1.z <= this.z2;
	}

	private boolean yIntersects(Vec3D var1) {
		return var1 == null ? false : var1.x >= this.x1 && var1.x <= this.x2 && var1.z >= this.z1
				&& var1.z <= this.z2;
	}

	private boolean zIntersects(Vec3D var1) {
		return var1 == null ? false : var1.x >= this.x1 && var1.x <= this.x2 && var1.y >= this.y1
				&& var1.y <= this.y2;
	}

}
