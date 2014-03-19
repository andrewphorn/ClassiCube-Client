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

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean helmet = Math.random() < 0.20000000298023224D;

    public boolean armor = Math.random() < 0.20000000298023224D;

    public HumanoidMob(Level level, float posX, float posY, float posZ) {
        super(level);
        modelName = "humanoid";
        this.setPos(posX, posY, posZ);
    }

    @Override
    public void renderModel(TextureManager textureManager, float var2, float var3, float var4, float var5,
            float var6, float var7) {
        if (modelName.equals("sheep")) {
            renderSheep(textureManager, var2, var3, var4, var5, var6, var7);
            return;
        } else if (isInteger(modelName)) {
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
        super.renderModel(textureManager, var2, var3, var4, var5, var6, var7);
        Model model = modelCache.getModel(modelName);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        if (allowAlpha) {
            GL11.glEnable(GL11.GL_CULL_FACE);
        }

        if (hasHair && model instanceof HumanoidModel) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            HumanoidModel modelHeadwear = null;
            (modelHeadwear = (HumanoidModel) model).headwear.yaw = modelHeadwear.head.yaw;
            modelHeadwear.headwear.pitch = modelHeadwear.head.pitch;
            modelHeadwear.headwear.render(var7);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }

        if (armor || helmet) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.load("/armor/plate.png"));
            GL11.glDisable(GL11.GL_CULL_FACE);
            HumanoidModel modelArmour;
            (modelArmour = (HumanoidModel) modelCache.getModel("humanoid.armor")).head.render = helmet;
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
            modelArmour.head.render(var7);
            modelArmour.body.render(var7);
            modelArmour.rightArm.render(var7);
            modelArmour.leftArm.render(var7);
            modelArmour.rightLeg.render(var7);
            modelArmour.leftLeg.render(var7);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }

        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    public void renderSheep(TextureManager textureManager, float var2, float var3, float var4, float var5,
            float var6, float var7) {
        AnimalModel model;
        float var9 = (model = (AnimalModel) modelCache.getModel("sheep")).head.y;
        float var10 = model.head.z;
        super.renderModel(textureManager, var2, var3, var4, var5, var6, var7);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.load("/mob/sheep_fur.png"));
        AnimalModel var11 = (AnimalModel) modelCache.getModel("sheep.fur");
        var11.head.yaw = model.head.yaw;
        var11.head.pitch = model.head.pitch;
        var11.head.y = model.head.y;
        var11.head.x = model.head.x;
        var11.body.yaw = model.body.yaw;
        var11.body.pitch = model.body.pitch;
        var11.leg1.pitch = model.leg1.pitch;
        var11.leg2.pitch = model.leg2.pitch;
        var11.leg3.pitch = model.leg3.pitch;
        var11.leg4.pitch = model.leg4.pitch;
        var11.head.render(var7);
        var11.body.render(var7);
        var11.leg1.render(var7);
        var11.leg2.render(var7);
        var11.leg3.render(var7);
        var11.leg4.render(var7);

        model.head.y = var9;
        model.head.z = var10;
    }
}
