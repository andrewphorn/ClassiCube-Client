package com.mojang.minecraft.mob;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.model.AnimalModel;
import com.mojang.minecraft.model.HumanoidModel;
import com.mojang.minecraft.model.Model;
import com.mojang.minecraft.render.TextureManager;
import org.lwjgl.opengl.GL11;

public class HumanoidMob extends Mob {

    public static final long serialVersionUID = 0L;
    public boolean helmet = Math.random() < 0.20000000298023224D;
    public boolean armor = Math.random() < 0.20000000298023224D;

    public HumanoidMob(Level var1, float var2, float var3, float var4) {
	super(var1);
	this.modelName = "humanoid";
	this.setPos(var2, var3, var4);
    }

    public void renderModel(TextureManager var1, float var2, float var3,
	    float var4, float var5, float var6, float var7) {
	if(this.modelName == "sheep"){
	    renderSheep(var1, var2,var3,var4,var5,var6,var7);
	    return;
	}
	super.renderModel(var1, var2, var3, var4, var5, var6, var7);
	
	Model var9 = modelCache.getModel(this.modelName);
	GL11.glEnable(3008);
	if (this.allowAlpha) {
	    GL11.glEnable(2884);
	}

	if (this.armor || this.helmet) {
	    GL11.glBindTexture(3553, var1.load("/armor/plate.png"));
	    GL11.glDisable(2884);
	    HumanoidModel var8;
	    (var8 = (HumanoidModel) modelCache.getModel("humanoid.armor")).head.render = this.helmet;
	    var8.body.render = this.armor;
	    var8.rightArm.render = this.armor;
	    var8.leftArm.render = this.armor;
	    var8.rightLeg.render = false;
	    var8.leftLeg.render = false;
	    HumanoidModel var11 = (HumanoidModel) var9;
	    var8.head.yaw = var11.head.yaw;
	    var8.head.pitch = var11.head.pitch;
	    var8.rightArm.pitch = var11.rightArm.pitch;
	    var8.rightArm.roll = var11.rightArm.roll;
	    var8.leftArm.pitch = var11.leftArm.pitch;
	    var8.leftArm.roll = var11.leftArm.roll;
	    var8.rightLeg.pitch = var11.rightLeg.pitch;
	    var8.leftLeg.pitch = var11.leftLeg.pitch;
	    var8.head.render(var7);
	    var8.body.render(var7);
	    var8.rightArm.render(var7);
	    var8.leftArm.render(var7);
	    var8.rightLeg.render(var7);
	    var8.leftLeg.render(var7);
	    GL11.glEnable(2884);
	}

	GL11.glDisable(3008);
    }
    
    public void renderSheep(TextureManager var1, float var2, float var3,
	    float var4, float var5, float var6, float var7) {
	AnimalModel var8;
	float var9 = (var8 = (AnimalModel) modelCache.getModel("sheep")).head.y;
	float var10 = var8.head.z;
	super.renderModel(var1, var2, var3, var4, var5, var6, var7);
	    GL11.glBindTexture(3553, var1.load("/mob/sheep_fur.png"));
	    AnimalModel var11;
	    (var11 = (AnimalModel) modelCache.getModel("sheep.fur")).head.yaw = var8.head.yaw;
	    var11.head.pitch = var8.head.pitch;
	    var11.head.y = var8.head.y;
	    var11.head.x = var8.head.x;
	    var11.body.yaw = var8.body.yaw;
	    var11.body.pitch = var8.body.pitch;
	    var11.leg1.pitch = var8.leg1.pitch;
	    var11.leg2.pitch = var8.leg2.pitch;
	    var11.leg3.pitch = var8.leg3.pitch;
	    var11.leg4.pitch = var8.leg4.pitch;
	    var11.head.render(var7);
	    var11.body.render(var7);
	    var11.leg1.render(var7);
	    var11.leg2.render(var7);
	    var11.leg3.render(var7);
	    var11.leg4.render(var7);

	var8.head.y = var9;
	var8.head.z = var10;
    }
}
