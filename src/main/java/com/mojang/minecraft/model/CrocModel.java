package com.mojang.minecraft.model;

import com.mojang.util.MathHelper;

public class CrocModel extends Model {
    // fields
    ModelPart tail;
    ModelPart head;
    ModelPart body;
    ModelPart leg1;
    ModelPart leg2;
    ModelPart leg4;
    ModelPart leg3;

    public CrocModel() {
        tail = new ModelPart(0, 0);
        tail.setBounds(0F, 0F, 0F, 8, 2, 17, 0F);
        tail.setPosition(-4F, 11F, 5F);
        tail.pitch = 0F;
        tail.yaw = 0F;
        tail.roll = 0F;
        tail.mirror = false;
        head = new ModelPart(0, 0);
        head.setBounds(-4F, -4F, -8F, 8, 5, 11, 0F);
        head.setPosition(0F, 15F, -9F);
        head.pitch = 0F;
        head.yaw = 0F;
        head.roll = 0F;
        head.mirror = false;
        body = new ModelPart(28, 8);
        body.setBounds(-5F, -10F, -7F, 10, 16, 8, 0F);
        body.setPosition(0F, 11F, 2F);
        body.pitch = 1.5708F;
        body.yaw = 0F;
        body.roll = 0F;
        body.mirror = false;
        leg1 = new ModelPart(0, 16);
        leg1.setBounds(-2F, 0F, -2F, 4, 6, 4, 0F);
        leg1.setPosition(-3F, 18F, 7F);
        leg1.pitch = 0F;
        leg1.yaw = 0F;
        leg1.roll = 0F;
        leg1.mirror = false;
        leg2 = new ModelPart(0, 16);
        leg2.setBounds(-2F, 0F, -2F, 4, 6, 4, 0F);
        leg2.setPosition(3F, 18F, 7F);
        leg2.pitch = 0F;
        leg2.yaw = 0F;
        leg2.roll = 0F;
        leg2.mirror = false;
        leg4 = new ModelPart(0, 16);
        leg4.setBounds(-2F, 0F, -2F, 4, 6, 4, 0F);
        leg4.setPosition(3F, 18F, -5F);
        leg4.pitch = 0F;
        leg4.yaw = 0F;
        leg4.roll = 0F;
        leg4.mirror = false;
        leg3 = new ModelPart(0, 16);
        leg3.setBounds(-2F, 18F, -2F, 4, 6, 4, 0F);
        leg3.setPosition(-3F, 0F, -5F);
        leg3.pitch = 0F;
        leg3.yaw = 0F;
        leg3.roll = 0F;
        leg3.mirror = false;
    }

    @Override
    public void render(float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5);
        tail.render(f5);
        head.render(f5);
        body.render(f5);
        leg1.render(f5);
        leg2.render(f5);
        leg4.render(f5);
        leg3.render(f5);
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5) {
        // super.setRotationAngles(f, f1, f2, f3, f4, f5);
        tail.yaw = MathHelper.cos(f / (0.9595538255F) * (float) (Math.PI / 90) * f1 + 0);
    }
}