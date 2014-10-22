package com.mojang.minecraft.mob;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.level.tile.FireBlock;
import com.mojang.minecraft.level.tile.FlowerBlock;
import com.mojang.minecraft.model.AnimalModel;
import com.mojang.minecraft.model.HumanoidModel;
import com.mojang.minecraft.model.Model;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.render.TextureManager;

public class HumanoidMob extends Mob {

    public static final long serialVersionUID = 0L;
    public boolean helmet = Math.random() < 0.20000000298023224D;
    public boolean armor = Math.random() < 0.20000000298023224D;

    public HumanoidMob(Level level, float posX, float posY, float posZ) {
        super(level);
        modelName = "humanoid";
        this.setPos(posX, posY, posZ);
    }

    @Override
    public void renderModel(TextureManager textureManager, float var2, float var3, float var4, float var5,
                            float var6, float scale) {
        if (modelName.equals("sheep")) {
            renderSheep(textureManager, var2, var3, var4, var5, var6, scale);
            return;
            
        } else if (isInteger(modelName)) {
            // Model name is a block number
            try {
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glPushMatrix();

                // These are here to revert the scalef calls in Mob.java.
                // While those calls are useful for entity models, they cause the
                // block models to be rendered upside down.
                GL11.glScalef(-1F, 1F, 1F);
                GL11.glScalef(1F, -1F, 1F);
                Block block = Block.blocks[Integer.parseInt(modelName)];
                // TODO: Implement proper detection of which blocks need translation.
                float yTranslation = -1.4F;
                if (block instanceof FlowerBlock || block instanceof FireBlock) {
                    yTranslation = -1.8F;
                }
                GL11.glTranslatef(-0.5F, yTranslation, -0.2F);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.load("/terrain.png"));

                block.renderPreview(ShapeRenderer.instance);
                GL11.glPopMatrix();
                GL11.glDisable(GL11.GL_BLEND);
            } catch (Exception e) {
                modelName = "humanoid";
            }
            return;
        }
        super.renderModel(textureManager, var2, var3, var4, var5, var6, scale);
        Model model = modelCache.getModel(modelName);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        if (allowAlpha) {
            GL11.glEnable(GL11.GL_CULL_FACE);
        }

        if (hasHair && model instanceof HumanoidModel) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            HumanoidModel modelHeadwear = (HumanoidModel) model;
            modelHeadwear.headwear.yaw = modelHeadwear.head.yaw;
            modelHeadwear.headwear.pitch = modelHeadwear.head.pitch;
            modelHeadwear.headwear.render(scale);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }

        if (armor || helmet) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.load("/armor/plate.png"));
            GL11.glDisable(GL11.GL_CULL_FACE);
            HumanoidModel modelArmour = (HumanoidModel) modelCache.getModel("humanoid.armor");
            modelArmour.head.render = helmet;
            modelArmour.body.render = armor;
            modelArmour.rightArm.render = armor;
            modelArmour.leftArm.render = armor;
            modelArmour.rightLeg.render = false;
            modelArmour.leftLeg.render = false;
            HumanoidModel humanoidModel = (HumanoidModel) model;
            modelArmour.head.yaw = humanoidModel.head.yaw;
            modelArmour.head.pitch = humanoidModel.head.pitch;
            modelArmour.rightArm.pitch = humanoidModel.rightArm.pitch;
            modelArmour.rightArm.roll = humanoidModel.rightArm.roll;
            modelArmour.leftArm.pitch = humanoidModel.leftArm.pitch;
            modelArmour.leftArm.roll = humanoidModel.leftArm.roll;
            modelArmour.rightLeg.pitch = humanoidModel.rightLeg.pitch;
            modelArmour.leftLeg.pitch = humanoidModel.leftLeg.pitch;
            modelArmour.head.render(scale);
            modelArmour.body.render(scale);
            modelArmour.rightArm.render(scale);
            modelArmour.leftArm.render(scale);
            modelArmour.rightLeg.render(scale);
            modelArmour.leftLeg.render(scale);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }

        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    public void renderSheep(TextureManager textureManager, float var2, float var3, float var4, float var5,
                            float var6, float scale) {
        AnimalModel model = (AnimalModel) modelCache.getModel("sheep");
        float headY = model.head.y;
        float headZ = model.head.z;
        super.renderModel(textureManager, var2, var3, var4, var5, var6, scale);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.load("/mob/sheep_fur.png"));
        AnimalModel sheepModel = (AnimalModel) modelCache.getModel("sheep.fur");
        sheepModel.head.yaw = model.head.yaw;
        sheepModel.head.pitch = model.head.pitch;
        sheepModel.head.y = model.head.y;
        sheepModel.head.x = model.head.x;
        sheepModel.body.yaw = model.body.yaw;
        sheepModel.body.pitch = model.body.pitch;
        sheepModel.leg1.pitch = model.leg1.pitch;
        sheepModel.leg2.pitch = model.leg2.pitch;
        sheepModel.leg3.pitch = model.leg3.pitch;
        sheepModel.leg4.pitch = model.leg4.pitch;
        sheepModel.head.render(scale);
        sheepModel.body.render(scale);
        sheepModel.leg1.render(scale);
        sheepModel.leg2.render(scale);
        sheepModel.leg3.render(scale);
        sheepModel.leg4.render(scale);

        model.head.y = headY;
        model.head.z = headZ;
    }
}
