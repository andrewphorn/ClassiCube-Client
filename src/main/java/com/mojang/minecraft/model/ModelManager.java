package com.mojang.minecraft.model;

import com.mojang.minecraft.SessionData;

public final class ModelManager {

	private HumanoidModel human = new HumanoidModel(0.0F);
	private HumanoidModel armoredHuman = new HumanoidModel(1.0F);
	private CreeperModel creeper = new CreeperModel();
	private SkeletonModel skeleton = new SkeletonModel();
	private ZombieModel zombie = new ZombieModel();
	private AnimalModel pig = new PigModel();
	private AnimalModel sheep = new SheepModel();
	private SpiderModel spider = new SpiderModel();
	private SheepFurModel sheepFur = new SheepFurModel();
	private ChickenModel chicken = new ChickenModel();
	private PrinterModel printer = new PrinterModel();
	private CrocModel croc = new CrocModel();

	public final Model getModel(String var1) {
		for (int i = 1; i < SessionData.allowedBlocks.size(); i++) {
			if (var1.equals("" + i)) {
				return new BlockModel();
			}
		}
		return var1.equals("humanoid") ? human : var1.equals("humanoid.armor") ? armoredHuman
				: var1.equals("creeper") ? creeper : var1.equals("chicken") ? chicken : var1
						.equals("skeleton") ? skeleton : var1.equals("printer") ? printer : var1
						.equals("croc") ? croc : var1.equals("zombie") ? zombie : var1
						.equals("pig") ? pig : var1.equals("sheep") ? sheep
						: var1.equals("spider") ? spider : var1.equals("sheep.fur") ? sheepFur
								: null;
	}
}
