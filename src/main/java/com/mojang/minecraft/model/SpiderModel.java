package com.mojang.minecraft.model;

import com.mojang.util.MathHelper;

public final class SpiderModel extends Model {

    private ModelPart head = new ModelPart(32, 4);
    private ModelPart neck;
    private ModelPart body;
    private ModelPart leg1;
    private ModelPart leg2;
    private ModelPart leg3;
    private ModelPart leg4;
    private ModelPart leg5;
    private ModelPart leg6;
    private ModelPart leg7;
    private ModelPart leg8;

    public SpiderModel() {
        head.setBounds(-4F, -4F, -8F, 8, 8, 8, 0F);
        head.setPosition(0F, 16F, -3F);
        neck = new ModelPart(0, 0);
        neck.setBounds(-3F, -3F, -3F, 6, 6, 6, 0F);
        neck.setPosition(0F, 16F, 0F);
        body = new ModelPart(0, 12);
        body.setBounds(-5F, -4F, -6F, 10, 8, 12, 0F);
        body.setPosition(0F, 16F, 9F);
        leg1 = new ModelPart(18, 0);
        leg1.setBounds(-15F, -1F, -1F, 16, 2, 2, 0F);
        leg1.setPosition(-4F, 16F, 2F);
        leg2 = new ModelPart(18, 0);
        leg2.setBounds(-1F, -1F, -1F, 16, 2, 2, 0F);
        leg2.setPosition(4F, 16F, 2F);
        leg3 = new ModelPart(18, 0);
        leg3.setBounds(-15F, -1F, -1F, 16, 2, 2, 0F);
        leg3.setPosition(-4F, 16F, 1F);
        leg4 = new ModelPart(18, 0);
        leg4.setBounds(-1F, -1F, -1F, 16, 2, 2, 0F);
        leg4.setPosition(4F, 16F, 1F);
        leg5 = new ModelPart(18, 0);
        leg5.setBounds(-15F, -1F, -1F, 16, 2, 2, 0F);
        leg5.setPosition(-4F, 16F, 0F);
        leg6 = new ModelPart(18, 0);
        leg6.setBounds(-1F, -1F, -1F, 16, 2, 2, 0F);
        leg6.setPosition(4F, 16F, 0F);
        leg7 = new ModelPart(18, 0);
        leg7.setBounds(-15F, -1F, -1F, 16, 2, 2, 0F);
        leg7.setPosition(-4F, 16F, -1F);
        leg8 = new ModelPart(18, 0);
        leg8.setBounds(-1F, -1F, -1F, 16, 2, 2, 0F);
        leg8.setPosition(4F, 16F, -1F);
    }

    @Override
    public final void render(float var1, float var2, float var3,
            float yawDegrees, float pitchDegrees, float scale) {
        head.yaw = yawDegrees / (float) (180D / Math.PI);
        head.pitch = pitchDegrees / (float) (180D / Math.PI);
        yawDegrees = (float) (Math.PI / 4D);
        leg1.roll = -yawDegrees;
        leg2.roll = yawDegrees;
        leg3.roll = -yawDegrees * 0.74F;
        leg4.roll = yawDegrees * 0.74F;
        leg5.roll = -yawDegrees * 0.74F;
        leg6.roll = yawDegrees * 0.74F;
        leg7.roll = -yawDegrees;
        leg8.roll = yawDegrees;
        yawDegrees = (float) (Math.PI / 8D);
        leg1.yaw = yawDegrees * 2F;
        leg2.yaw = -yawDegrees * 2F;
        leg3.yaw = yawDegrees;
        leg4.yaw = -yawDegrees;
        leg5.yaw = -yawDegrees;
        leg6.yaw = yawDegrees;
        leg7.yaw = -yawDegrees * 2F;
        leg8.yaw = yawDegrees * 2F;
        yawDegrees = -(MathHelper.cos(var1 * 0.6662F * 2F) * 0.4F) * var2;
        pitchDegrees = -(MathHelper.cos(var1 * 0.6662F * 2F + (float) Math.PI) * 0.4F) * var2;
        float var7 = -(MathHelper.cos(var1 * 0.6662F * 2F + (float) (Math.PI / 2D)) * 0.4F) * var2;
        float var8 = -(MathHelper.cos(var1 * 0.6662F * 2F + (float) ((3 * Math.PI) / 2D)) * 0.4F) * var2;
        float var9 = Math.abs(MathHelper.sin(var1 * 0.6662F) * 0.4F) * var2;
        float var10 = Math.abs(MathHelper.sin(var1 * 0.6662F + (float) Math.PI) * 0.4F) * var2;
        float var11 = Math.abs(MathHelper.sin(var1 * 0.6662F + (float) (Math.PI / 2D)) * 0.4F) * var2;
        var2 = Math.abs(MathHelper.sin(var1 * 0.6662F + 4.712389F) * 0.4F) * var2;
        leg1.yaw += yawDegrees;
        leg2.yaw -= yawDegrees;
        leg3.yaw += pitchDegrees;
        leg4.yaw -= pitchDegrees;
        leg5.yaw += var7;
        leg6.yaw -= var7;
        leg7.yaw += var8;
        leg8.yaw -= var8;
        leg1.roll += var9;
        leg2.roll -= var9;
        leg3.roll += var10;
        leg4.roll -= var10;
        leg5.roll += var11;
        leg6.roll -= var11;
        leg7.roll += var2;
        leg8.roll -= var2;
        head.render(scale);
        neck.render(scale);
        body.render(scale);
        leg1.render(scale);
        leg2.render(scale);
        leg3.render(scale);
        leg4.render(scale);
        leg5.render(scale);
        leg6.render(scale);
        leg7.render(scale);
        leg8.render(scale);
    }
}
