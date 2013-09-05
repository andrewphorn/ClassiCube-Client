package com.mojang.minecraft.player;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.Entity;
import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.ProgressBarDisplay;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.model.HumanoidModel;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.util.MathHelper;

import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.util.List;

public class Player extends Mob {
    public Player(Level var1, GameSettings gs) {
	super(var1);
	if (var1 != null) {
	    var1.player = this;
	    var1.removeEntity(this);
	    var1.addEntity(this);
	}

	this.heightOffset = 1.62F;
	this.health = 20;
	this.modelName = "humanoid";
	this.rotOffs = 180.0F;
	this.ai = new Player$1(this);
	this.settings = gs;
    }

    private int nox = 0;
    private int noc = 0;
    private int nos = 0;

    private int jumpCount = 0;
    boolean HacksEnabled = true;

    public static boolean noPush = false;
    public transient GameSettings settings;

    @Override
    public void aiStep() {
	if (settings.HackType == 0) {
	    this.inventory.tick();
	    this.oBob = this.bob;
	    this.input.updateMovement(settings.HackType);
	    super.aiStep();

	    float var1 = MathHelper.sqrt(this.xd * this.xd + this.zd * this.zd);
	    float var2 = (float) Math.atan((double) (-this.yd * 0.2F)) * 15.0F;
	    if (var1 > 0.1F) {
		var1 = 0.1F;
	    }

	    if (!this.onGround || this.health <= 0) {
		var1 = 0.0F;
	    }

	    if (this.onGround || this.health <= 0) {
		var2 = 0.0F;
	    }

	    this.bob += (var1 - this.bob) * 0.4F;
	    this.tilt += (var2 - this.tilt) * 0.8F;
	    List<?> var3;
	    if (this.health > 0
		    && (var3 = this.level.findEntities(this,
			    this.bb.grow(1.0F, 0.0F, 1.0F))) != null) {
		for (int var4 = 0; var4 < var3.size(); ++var4) {
		    ((Entity) var3.get(var4)).playerTouch(this);
		}
	    }
	} else {
	    this.input.updateMovement(settings.HackType);
	    super.aiStep();
	    if ((this.nox == 0) || (this.nos == 0) || (this.noc == 0)) {
		try {
		    String title = ProgressBarDisplay.title;
		    String text = ProgressBarDisplay.text;
		    if ((title.length() > 0) && (title != "Connecting..")) {
			System.err.println(new StringBuilder()
				.append("Got server: ").append(title)
				.append(": ").append(text).toString());

			this.nos = -1;
			this.nox = -1;
			this.noc = -1;

			String joinedString = new StringBuilder().append(title)
				.append(" ").append(text).toString()
				.toLowerCase();

			if (joinedString.indexOf("-hax") > -1) {
			    this.nos = 1;
			    this.nox = 1;
			    this.noc = 1;
			} else if (joinedString.indexOf("+hax") > -1) {
			    this.nos = -1;
			    this.nox = -1;
			    this.noc = -1;
			}
			if (joinedString.indexOf("+fly") > -1)
			    this.nox = -1;
			else if (joinedString.indexOf("-fly") > -1)
			    this.nox = 1;
			if (joinedString.indexOf("+noclip") > -1)
			    this.noc = -1;
			else if (joinedString.indexOf("-noclip") > -1)
			    this.noc = 1;
			if (joinedString.indexOf("+speed") > -1)
			    this.nos = -1;
			else if (joinedString.indexOf("-speed") > -1)
			    this.nos = 1;

			if ((userType >= 100)
				&& (joinedString.indexOf("+ophax") > -1)) {
			    this.nox = -1;
			    this.noc = -1;
			    this.nos = -1;
			}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	    int i = 0;
	    int j = 0;
	    int k = 1;
	    float f1 = 1.0F;

	    if ((this.input.fly) && (this.nox < 1))
		i = 1;
	    if ((this.input.noclip) && (this.noc < 0))
		j = 1;
	    if ((this.input.mult > 1.0F) && (this.nos < 1)) {
		f1 = this.input.mult;
	    }

	    if (!HacksEnabled) {
		i = 0;
		j = 0;
		k = 0;
		f1 = 1.0F;
	    }

	    if ((this.nox > 0) || (this.nos > 0)) {
		k = 0;
	    }

	    this.xo = this.x;
	    this.yo = this.y;
	    this.zo = this.z;
	    this.xRotO = this.xRot;
	    this.yRotO = this.yRot;

	    boolean bool1 = isInWater();
	    boolean bool2 = isInLava();
	    boolean bool3 = isInOrOnRope();

	    float f2 = 0.0F;

	    this.input.calc();

	    if ((i != 0) || (j != 0))
		this.yd = this.input.elevate;

	    if ((this.onGround) || (i != 0))
		this.jumpCount = 0;

	    if (this.input.jump) {
		if (bool1) {
		    this.yd += 0.08F;
		} else if (bool3) {
		    this.yd += 0.06F;
		} else if (bool2) {
		    this.yd += 0.07F;
		} else if (i != 0) {
		    this.yd += 0.05F;
		} else if (this.onGround) {
		    if (!this.input.fall) {
			if ((!HacksEnabled) && (k != 0))
			    this.yd = 0.48F;
			else
			    this.yd = 0.35F;
			this.input.fall = true;
			this.jumpCount += 1;
		    }
		} else if (HacksEnabled && (!this.input.fall) && (k != 0)
			&& (this.jumpCount < 3)) {
		    this.yd = 0.5F;
		    this.input.fall = true;
		    this.jumpCount += 1;
		}
	    } else {
		this.input.fall = false;
	    }

	    if (HacksEnabled && (k != 0) && (this.jumpCount > 1)) {
		f1 *= 2.5F;
		f1 *= this.jumpCount;
	    }

	    if ((bool1) && (i == 0) && (j == 0)) {
		f2 = this.y;
		super.moveRelative(this.input.strafe, this.input.move,
			0.02F * f1);
		super.move(this.xd * f1, this.yd * f1, this.zd * f1);
		this.xd *= 0.8F;
		this.yd *= 0.8F;
		this.zd *= 0.8F;
		this.yd = ((float) (this.yd - 0.02D));
		if ((this.horizontalCollision)
			&& (isFree(this.xd, this.yd + 0.6F - this.y + f2,
				this.zd)))
		    this.yd = 0.3F;
		return;
	    }
	    if ((bool2) && (i == 0) && (j == 0)) {
		f2 = this.y;
		super.moveRelative(this.input.strafe, this.input.move,
			0.02F * f1);
		super.move(this.xd * f1, this.yd * f1, this.zd * f1);
		this.xd *= 0.5F;
		this.yd *= 0.5F;
		this.zd *= 0.5F;
		this.yd = ((float) (this.yd - 0.02D));
		if ((this.horizontalCollision)
			&& (isFree(this.xd, this.yd + 0.6F - this.y + f2,
				this.zd)))
		    this.yd = 0.3F;
		return;
	    }

	    if (i != 0) {
		f1 = (float) (f1 * 1.2D);
	    }

	    float f4 = 0.0F;
	    float f3;
	    if (j != 0) {
		f4 = i != 0 ? 0.72F : 0.71F;
		if (i != 0)
		    this.yd = this.input.elevate;
		f3 = 0.2F;
	    } else if ((this.onGround) || (this.jumpCount > 0) || (i != 0)) {
		f3 = 0.1F;
	    } else {
		f3 = 0.02F;
	    }

	    super.moveRelative(this.input.strafe, this.input.move, f3 * f1);

	    if ((j != 0) && ((this.xd != 0.0F) || (this.zd != 0.0F))) {
		super.moveTo(this.x + this.xd, this.y + this.yd - f4, this.z
			+ this.zd, this.yRot, this.xRot);
		this.yo = (this.y += f4);
	    } else {
		super.move(this.xd * f1, this.yd * f1, this.zd * f1);
	    }
	    this.xd *= 0.91F;
	    this.yd *= 0.98F;
	    this.zd *= 0.91F;
	    f2 = 0.6F;

	    if (i != 0) {
		this.yd *= f2 / 4.0F;
		this.walkDist = 0.0F;
	    } else {
		this.yd = ((float) (this.yd - 0.01D));
	    }
	    this.xd *= f2;
	    this.zd *= f2;
	}

    }

    @Override
    public void render(TextureManager var1, float var2) {
	if (!this.settings.thirdPersonMode)
	    return;
	if (this.modelName != null) {
	    float var3;
	    if ((var3 = (float) this.attackTime - var2) < 0.0F) {
		var3 = 0.0F;
	    }

	    while (this.yBodyRotO - this.yBodyRot < -180.0F) {
		this.yBodyRotO += 360.0F;
	    }

	    while (this.yBodyRotO - this.yBodyRot >= 180.0F) {
		this.yBodyRotO -= 360.0F;
	    }

	    while (this.xRotO - this.xRot < -180.0F) {
		this.xRotO += 360.0F;
	    }

	    while (this.xRotO - this.xRot >= 180.0F) {
		this.xRotO -= 360.0F;
	    }

	    while (this.yRotO - this.yRot < -180.0F) {
		this.yRotO += 360.0F;
	    }

	    while (this.yRotO - this.yRot >= 180.0F) {
		this.yRotO -= 360.0F;
	    }

	    float var4 = this.yBodyRotO
		    + ((this.yBodyRot - this.yBodyRotO) * var2);
	    float var5 = this.oRun + (this.run - this.oRun) * var2;
	    float var6 = this.yRotO + (this.yRot - this.yRotO) * var2;
	    float var7 = this.xRotO + (this.xRot - this.xRotO) * var2;
	    var6 -= var4;
	    GL11.glPushMatrix();
	    float var8 = this.animStepO + (this.animStep - this.animStepO)
		    * var2;
	    ColorCache c = this.getBrightnessColor(var2);

	    GL11.glColor3f(c.R, c.G, c.B);
	    float var9 = 0.0625F;
	    float var10 = -Math.abs(MathHelper.cos(var8 * 0.6662F)) * 5.0F
		    * var5 * this.bobStrength - 23.0F;
	    GL11.glTranslatef(this.xo + (this.x - this.xo) * var2, this.yo
		    + (this.y - this.yo) * var2 - 1.62F + this.renderOffset,
		    this.zo + (this.z - this.zo) * var2);
	    float var11;
	    if ((var11 = (float) this.hurtTime - var2) > 0.0F
		    || this.health <= 0) {
		if (var11 < 0.0F) {
		    var11 = 0.0F;
		} else {
		    var11 = MathHelper.sin((var11 /= (float) this.hurtDuration)
			    * var11 * var11 * var11 * 3.1415927F) * 14.0F;
		}

		float var12 = 0.0F;
		if (this.health <= 0) {
		    var12 = ((float) this.deathTime + var2) / 20.0F;
		    if ((var11 += var12 * var12 * 800.0F) > 90.0F) {
			var11 = 90.0F;
		    }
		}

		var12 = this.hurtDir;
		GL11.glRotatef(180.0F - var4 + this.rotOffs + 45, 0.0F, 1.0F,
			0.0F);
		GL11.glScalef(1.0F, 1.0F, 1.0F);
		GL11.glRotatef(-var12, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-var11, 0.0F, 0.0F, 1.0F);
		GL11.glRotatef(var12, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-(180.0F - var4 + this.rotOffs), 0.0F, 1.0F,
			0.0F);
	    }

	    GL11.glTranslatef(0.0F, -var10 * var9, 0.0F);
	    GL11.glScalef(1.0F, -1.0F, 1.0F);
	    GL11.glRotatef(180.0F - var4 + this.rotOffs, 0.0F, 1.0F, 0.0F);
	    if (!this.allowAlpha) {
		GL11.glDisable(3008);
	    } else {
		GL11.glDisable(2884);
	    }

	    GL11.glScalef(-1.0F, 1.0F, 1.0F);
	    modelCache.getModel(this.modelName).attackOffset = var3 / 5.0F;
	    this.bindTexture(var1);
	    this.renderModel(var1, var8, var2, var5, var6, var7, var9);
	    if (this.invulnerableTime > this.invulnerableDuration - 10) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 1);
		this.bindTexture(var1);
		this.renderModel(var1, var8, var2, var5, var6, var7, var9);
		GL11.glDisable(3042);
		GL11.glBlendFunc(770, 771);
	    }

	    GL11.glEnable(3008);
	    if (this.allowAlpha) {
		GL11.glEnable(2884);
	    }

	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	    GL11.glPopMatrix();
	}
    }

    public void renderModel(TextureManager var1, float var2, float var3,
	    float var4, float var5, float var6, float var7) {
	modelCache.getModel(this.modelName).render(var2, var4,
		(float) this.tickCount + var3, var5, var6, var7);
    }

    @Override
    public void bindTexture(TextureManager var1) {
	if (newTexture != null) {
	    newTextureId = var1.load(newTexture);
	    newTexture = null;
	}

	int var2;
	if (newTextureId < 0) {
	    var2 = var1.load("/char.png");
	    GL11.glBindTexture(3553, var2);
	} else {
	    var2 = newTextureId;
	    GL11.glBindTexture(3553, var2);
	}
    }

    @Override
    public void resetPos() {
	this.heightOffset = 1.62F;
	this.setSize(0.6F, 1.8F);
	super.resetPos();
	if (this.level != null) {
	    this.level.player = this;
	}

	this.health = 20;
	this.deathTime = 0;
    }

    @Override
    public void die(Entity var1) {
	this.setSize(0.2F, 0.2F);
	this.setPos(this.x, this.y, this.z);
	this.yd = 0.1F;
	if (var1 != null) {
	    this.xd = -MathHelper
		    .cos((this.hurtDir + this.yRot) * 3.1415927F / 180.0F) * 0.1F;
	    this.zd = -MathHelper
		    .sin((this.hurtDir + this.yRot) * 3.1415927F / 180.0F) * 0.1F;
	} else {
	    this.xd = this.zd = 0.0F;
	}

	this.heightOffset = 0.1F;
    }

    @Override
    public boolean isShootable() {
	return true;
    }

    @Override
    public void awardKillScore(Entity var1, int var2) {
	this.score += var2;
    }

    @Override
    public void remove() {
    }

    @Override
    public void hurt(Entity var1, int var2) {
	if (!this.level.creativeMode) {
	    super.hurt(var1, var2);
	}

    }

    @Override
    public boolean isCreativeModeAllowed() {
	return true;
    }

    public static final long serialVersionUID = 0L;
    public static final int MAX_HEALTH = 20;
    public static final int MAX_ARROWS = 99;
    public transient InputHandler input;
    public Inventory inventory = new Inventory();
    public byte userType = 0;
    public float oBob;
    public float bob;
    public int score = 0;
    public int arrows = 20;
    private static int newTextureId = -1;
    public static BufferedImage newTexture;

    public void releaseAllKeys() {
	this.input.resetKeys();
    }

    public void setKey(int var1, boolean var2) {
	this.input.setKeyState(var1, var2);
    }

    public boolean addResource(int var1) {
	return this.inventory.addResource(var1);
    }

    public int getScore() {
	return this.score;
    }

    public HumanoidModel getModel() {
	return (HumanoidModel) modelCache.getModel(this.modelName);
    }
}