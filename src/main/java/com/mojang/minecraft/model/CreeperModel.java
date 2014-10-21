package com.mojang.minecraft.model;

import com.mojang.util.MathHelper;

public final class CreeperModel extends Model {

    private ModelPart head = new ModelPart(0, 0);
    private ModelPart unused;
    private ModelPart body;
    private ModelPart leg1;
    private ModelPart leg2;
    private ModelPart leg3;
    private ModelPart leg4;

    public CreeperModel() {
        head.setBounds(-4F, -8F, -4F, 8, 8, 8, 0F);
        head.setPosition(0F, 4F, 0F);
        unused = new ModelPart(32, 0);
        unused.setBounds(-4F, -8F, -4F, 8, 8, 8, 0F + 0.5F);
        body = new ModelPart(16, 16);
        body.setBounds(-4F, 0F, -2F, 8, 12, 4, 0F);
        body.setPosition(0F, 4F, 0F);
        leg1 = new ModelPart(0, 16);
        leg1.setBounds(-2F, 0F, -2F, 4, 6, 4, 0F);
        leg1.setPosition(-2F, 16F, 4F);
        leg2 = new ModelPart(0, 16);
        leg2.setBounds(-2F, 0F, -2F, 4, 6, 4, 0F);
        leg2.setPosition(2F, 16F, 4F);
        leg3 = new ModelPart(0, 16);
        leg3.setBounds(-2F, 0F, -2F, 4, 6, 4, 0F);
        leg3.setPosition(-2F, 16F, -4F);
        leg4 = new ModelPart(0, 16);
        leg4.setBounds(-2F, 0F, -2F, 4, 6, 4, 0F);
        leg4.setPosition(2F, 16F, -4F);
    }

    @Override
    public final void render(float var1, float var2, float var3,
            float yawDegrees, float pitchDegrees, float scale) {
        head.yaw = yawDegrees / (float) (180D / Math.PI);
        head.pitch = pitchDegrees / (float) (180D / Math.PI);
        leg1.pitch = MathHelper.cos(var1 * 0.6662F) * 1.4F * var2;
        leg2.pitch = MathHelper.cos(var1 * 0.6662F + (float) Math.PI) * 1.4F * var2;
        leg3.pitch = MathHelper.cos(var1 * 0.6662F + (float) Math.PI) * 1.4F * var2;
        leg4.pitch = MathHelper.cos(var1 * 0.6662F) * 1.4F * var2;
        head.render(scale);
        body.render(scale);
        leg1.render(scale);
        leg2.render(scale);
        leg3.render(scale);
        leg4.render(scale);
    }
}
