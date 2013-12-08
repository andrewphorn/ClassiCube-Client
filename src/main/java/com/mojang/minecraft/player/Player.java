package com.mojang.minecraft.player;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.Entity;
import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.HackState;
import com.mojang.minecraft.ProgressBarDisplay;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.model.HumanoidModel;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.util.MathHelper;

import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.util.List;

public class Player extends Mob {
	private int flyTrig = 0;

	private int noclipTrig = 0;
	private int speedTrig = 0;
	private int jumpCount = 0;

	boolean HacksEnabled;
	boolean isOnIce = false;

	public static boolean noPush = false;

	public transient GameSettings settings;
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

	public boolean addResource(int var1) {
		return this.inventory.addResource(var1);
	}

	@Override
	public void aiStep() {
		if (settings.HackType == 0 || !(HackState.Fly || HackState.Speed || HackState.Noclip)) {
			this.inventory.tick();
			this.oBob = this.bob;
			this.input.updateMovement(0); // for the event that hacktype
			// is 1 but server has -hax.
			// Otherwise you won't be able to move without manually setting
			// your hacktype back to 'normal' in the options menu.
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
					&& (var3 = this.level.findEntities(this, this.bb.grow(1.0F, 0.0F, 1.0F))) != null) {
				for (int var4 = 0; var4 < var3.size(); ++var4) {
					((Entity) var3.get(var4)).playerTouch(this);
				}
			}
		} else {
			this.oBob = this.bob;
			this.HacksEnabled = settings.HacksEnabled;
			this.input.updateMovement(settings.HackType);
			super.aiStep();
			float fx = xd;
			float fy = yd;
			float fz = zd;
			if (fx > 0.1f)
				fx = 0.1f;
			if (fy > 0.1f)
				fy = 0.1f;
			if (fz > 0.1f)
				fz = 0.1f;

			if (fx < -0.1f)
				fx = -0.1f;
			if (fy < -0.1f)
				fy = -0.1f;
			if (fz < -0.1f)
				fz = -0.1f;

			float aaa = MathHelper.sqrt(fx * fx + fz * fz);
			float bbb = (float) Math.atan((double) (-fy * 0.2F)) * 15.0F;
			this.bob += (aaa - this.bob) * 0.4F;
			this.tilt += (bbb - this.tilt) * 0.8F;

			this.speedTrig = -1; // speed
			this.flyTrig = -1; // fly
			this.noclipTrig = -1; // noclip
			// -1 = yes, 1 = no

			if (HackState.Fly)
				flyTrig = -1;
			else
				flyTrig = 1;

			if (HackState.Speed)
				speedTrig = -1;
			else
				speedTrig = 1;

			if (HackState.Noclip)
				noclipTrig = -1;
			else
				noclipTrig = 1;
			int i = 0;
			int j = 0;
			int k = 1;
			float f1 = 1.0F;
			this.oBob = this.bob;
			if ((this.input.fly) && (this.flyTrig < 1))
				i = 1;
			if ((this.input.noclip) && (this.noclipTrig < 0))
				j = 1;
			if ((this.input.mult > 1.0F) && (this.speedTrig < 1)) {
				f1 = this.input.mult;
			}

			if (!HacksEnabled) {
				i = 0;
				j = 0;
				k = 0;
				f1 = 1.0F;
			}

			if ((this.flyTrig > 0) || (this.speedTrig > 0)) {
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

			if (!this.input.canMove) {
				this.input.resetKeys();
				//return; <- messes up flying, you mug
			}

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
				} else if (HacksEnabled && (!this.input.fall) && (k != 0) && (this.jumpCount < 3)) {
					this.yd = 0.5F;
					this.input.fall = true;
					this.jumpCount += 1;
				}
			} else {
				this.input.fall = false;
			}

			if (HacksEnabled && (k != 0) && (this.jumpCount > 1)) {
				f1 *= 2.5F;
				if (!this.isOnIce) {
					f1 *= this.jumpCount;
				} else
					this.jumpCount = 0;
			}

			if ((bool1) && (i == 0) && (j == 0)) {
				f2 = this.y;
				super.moveRelative(this.input.strafe, this.input.move, 0.02F * f1);
				super.move(this.xd * f1, this.yd * f1, this.zd * f1);
				this.xd *= 0.8F;
				this.yd *= 0.8F;
				this.zd *= 0.8F;
				this.yd = ((float) (this.yd - 0.02D));
				if ((this.horizontalCollision)
						&& (isFree(this.xd, this.yd + 0.6F - this.y + f2, this.zd)))
					this.yd = 0.3F;
				return;
			}

			if ((bool2) && (i == 0) && (j == 0)) {
				f2 = this.y;
				super.moveRelative(this.input.strafe, this.input.move, 0.02F * f1);
				super.move(this.xd * f1, this.yd * f1, this.zd * f1);
				this.xd *= 0.5F;
				this.yd *= 0.5F;
				this.zd *= 0.5F;
				this.yd = ((float) (this.yd - 0.02D));
				if ((this.horizontalCollision)
						&& (isFree(this.xd, this.yd + 0.6F - this.y + f2, this.zd)))
					this.yd = 0.3F;
				return;
			}

			if (i != 0) {
				f1 = (float) (f1 * 1.2D);
			}

			float f4 = 0.0F;
			float f3 = 0.0f;
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
				super.moveTo(this.x + this.xd, this.y + this.yd - f4, this.z + this.zd, this.yRot,
						this.xRot);
				this.yo = (this.y += f4);
			} else {
				super.move(this.xd * f1, this.yd * f1, this.zd * f1);
			}
			int var1 = this.level.getTile((int) this.x, (int) ((this.y) - 2.12F), (int) this.z);
			if (Block.blocks[var1] != Block.ICE) {
				if (this.jumpCount == 0) {
					this.isOnIce = false;
				}
				f2 = 0.6F;
				this.xd *= 0.91F;
				this.yd *= 0.98F;
				this.zd *= 0.91F;

				if (i != 0) {
					this.yd *= f2 / 4.0F;
					this.walkDist = 0.0F;
				} else {
					this.yd = ((float) (this.yd - 0.01D));
				}
				this.xd *= f2;
				this.zd *= f2;
				this.tilt = 0f;
			} else {
				isOnIce = true;
			}
		}
	}

	@Override
	public void awardKillScore(Entity var1, int var2) {
		this.score += var2;
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
	public void die(Entity var1) {
		this.setSize(0.2F, 0.2F);
		this.setPos(this.x, this.y, this.z);
		this.yd = 0.1F;
		if (var1 != null) {
			this.xd = -MathHelper.cos((this.hurtDir + this.yRot) * 3.1415927F / 180.0F) * 0.1F;
			this.zd = -MathHelper.sin((this.hurtDir + this.yRot) * 3.1415927F / 180.0F) * 0.1F;
		} else {
			this.xd = this.zd = 0.0F;
		}

		this.heightOffset = 0.1F;
	}

	public HumanoidModel getModel() {
		return (HumanoidModel) modelCache.getModel(this.modelName);
	}

	public int getScore() {
		return this.score;
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

	@Override
	public boolean isShootable() {
		return true;
	}

	public void releaseAllKeys() {
		this.input.resetKeys();
	}

	@Override
	public void remove() {
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

			float var4 = this.yBodyRotO + ((this.yBodyRot - this.yBodyRotO) * var2);
			float var5 = this.oRun + (this.run - this.oRun) * var2;
			float var6 = this.yRotO + (this.yRot - this.yRotO) * var2;
			float var7 = this.xRotO + (this.xRot - this.xRotO) * var2;
			var6 -= var4;
			GL11.glPushMatrix();
			float var8 = this.animStepO + (this.animStep - this.animStepO) * var2;
			ColorCache c = this.getBrightnessColor(var2);

			GL11.glColor3f(c.R, c.G, c.B);
			float var9 = 0.0625F;
			float var10 = -Math.abs(MathHelper.cos(var8 * 0.6662F)) * 5.0F * var5
					* this.bobStrength - 23.0F;
			GL11.glTranslatef(this.xo + (this.x - this.xo) * var2, this.yo + (this.y - this.yo)
					* var2 - 1.62F + this.renderOffset, this.zo + (this.z - this.zo) * var2);
			float var11;
			if ((var11 = (float) this.hurtTime - var2) > 0.0F || this.health <= 0) {
				if (var11 < 0.0F) {
					var11 = 0.0F;
				} else {
					var11 = MathHelper.sin((var11 /= (float) this.hurtDuration) * var11 * var11
							* var11 * 3.1415927F) * 14.0F;
				}

				float var12 = 0.0F;
				if (this.health <= 0) {
					var12 = ((float) this.deathTime + var2) / 20.0F;
					if ((var11 += var12 * var12 * 800.0F) > 90.0F) {
						var11 = 90.0F;
					}
				}

				var12 = this.hurtDir;
				GL11.glRotatef(180.0F - var4 + this.rotOffs + 45, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(1.0F, 1.0F, 1.0F);
				GL11.glRotatef(-var12, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(-var11, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(var12, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(-(180.0F - var4 + this.rotOffs), 0.0F, 1.0F, 0.0F);
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

	public void renderModel(TextureManager var1, float var2, float var3, float var4, float var5,
			float var6, float var7) {
		modelCache.getModel(this.modelName).render(var2, var4, (float) this.tickCount + var3, var5,
				var6, var7);
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

	public void setKey(int var1, boolean var2) {
		this.input.setKeyState(var1, var2);
	}
}