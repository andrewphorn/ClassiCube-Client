package com.mojang.minecraft.model;

import com.mojang.util.MathHelper;

import org.lwjgl.opengl.GL11;

public class ChickenModel extends Model
{
    public ModelPart head;
    public ModelPart body;
    public ModelPart rightLeg;
    public ModelPart leftLeg;
    public ModelPart rightWing;
    public ModelPart leftWing;
    public ModelPart bill;
    public ModelPart chin;

    public ChickenModel()
    {
        byte var1 = 16;
        this.head = new ModelPart(0, 0);
        this.head.setBounds(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
        this.head.setPosition(0.0F, (float)(-1 + var1), -4.0F);
        this.bill = new ModelPart(14, 0);
        this.bill.setBounds(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
        this.bill.setPosition(0.0F, (float)(-1 + var1), -4.0F);
        this.chin = new ModelPart(14, 4);
        this.chin.setBounds(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
        this.chin.setPosition(0.0F, (float)(-1 + var1), -4.0F);
        this.body = new ModelPart( 0, 9);
        this.body.setBounds(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
        this.body.setPosition(0.0F, (float)var1, 0.0F);
        this.rightLeg = new ModelPart( 26, 0);
        this.rightLeg.setBounds(-1.0F, 0.0F, -3.0F, 3, 5, 3, 0.0F);
        this.rightLeg.setPosition(-2.0F, (float)(3 + var1), 1.0F);
        this.leftLeg = new ModelPart( 26, 0);
        this.leftLeg.setBounds(-1.0F, 0.0F, -3.0F, 3, 5, 3, 0.0F);
        this.leftLeg.setPosition(1.0F, (float)(3 + var1), 1.0F);
        this.rightWing = new ModelPart( 24, 13);
        this.rightWing.setBounds(0.0F, 0.0F, -3.0F, 1, 4, 6, 0.0F);
        this.rightWing.setPosition(-4.0F, (float)(-3 + var1), 0.0F);
        this.leftWing = new ModelPart( 24, 13);
        this.leftWing.setBounds(-1.0F, 0.0F, -3.0F, 1, 4, 6, 0.0F);
        this.leftWing.setPosition(4.0F, (float)(-3 + var1), 0.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    public void render(float par2, float par3, float par4, float par5, float par6, float par7)
    {
        this.setRotationAngles(par2, par3, par4, par5, par6, par7 );
            this.head.render(par7);
            this.bill.render(par7);
            this.chin.render(par7);
            this.body.render(par7);
            this.rightLeg.render(par7);
            this.leftLeg.render(par7);
            this.rightWing.render(par7);
            this.leftWing.render(par7);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6 )
    {
        this.head.pitch = par5 / (180F / (float)Math.PI);
        this.head.yaw = par4 / (180F / (float)Math.PI);
        this.bill.pitch = this.head.pitch;
        this.bill.yaw = this.head.yaw;
        this.chin.pitch = this.head.pitch;
        this.chin.yaw = this.head.yaw;
        this.body.pitch = ((float)Math.PI / 2F);
        this.rightLeg.pitch = MathHelper.cos(par1 * 0.6662F) * 1.4F * par2;
        this.leftLeg.pitch = MathHelper.cos(par1 * 0.6662F + (float)Math.PI) * 1.4F * par2;
        this.rightWing.roll = par3;
        this.leftWing.roll = -par3;
    }
}