package com.mojang.minecraft.model;

import com.mojang.minecraft.SessionData;

/**
 * Manages the Models built into the Client.
 */
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

	/**
	 * Gets a model from its name.
	 * You can also get block models by providing their ID.
	 * @param modelName String representing the name of the model
	 * @return The Model with the name that was requested or null if not found.
	 */
	public final Model getModel(String modelName) {
		// Try to match with a Block ID
		for (int i = 1; i < SessionData.allowedBlocks.size(); i++) {
			if (modelName.equals("" + i)) {
				return new BlockModel();
			}
		}
		return modelName.equals("humanoid") ? human : modelName.equals("humanoid.armor") ? armoredHuman
				: modelName.equals("creeper") ? creeper : modelName.equals("chicken") ? chicken : modelName
						.equals("skeleton") ? skeleton : modelName.equals("printer") ? printer : modelName
						.equals("croc") ? croc : modelName.equals("zombie") ? zombie : modelName
						.equals("pig") ? pig : modelName.equals("sheep") ? sheep
						: modelName.equals("spider") ? spider : modelName.equals("sheep.fur") ? sheepFur
								: null;
	}
}
