package com.mojang.minecraft.mob;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.ColorCache;
import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.mob.ai.AI;
import com.mojang.minecraft.mob.ai.BasicAI;
import com.mojang.minecraft.model.ModelManager;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.util.MathHelper;

public class Mob extends Entity {

	public static final long serialVersionUID = 0L;
	public static final int ATTACK_DURATION = 5;
	public static final int TOTAL_AIR_SUPPLY = 300;
	public static ModelManager modelCache;
	public int invulnerableDuration = 20;
	public float rot;
	public float timeOffs;
	public float speed;
	public float rotA = (float) (Math.random() + 1.0D) * 0.01F;
	protected float yBodyRot = 0.0F;
	protected float yBodyRotO = 0.0F;
	protected float oRun;
	protected float run;
	protected float animStep;
	protected float animStepO;
	protected int tickCount = 0;
	public boolean hasHair = true;
	protected String textureName = "/char.png";
	public boolean allowAlpha = true;
	public float rotOffs = 0.0F;
	public String modelName = null;
	protected float bobStrength = 1.0F;
	protected int deathScore = 0;
	public float renderOffset = 0.0F;
	public int health = 20;
	public int lastHealth;
	public int invulnerableTime = 0;
	public int airSupply = 300;
	public int hurtTime;
	public int hurtDuration;
	public float hurtDir = 0.0F;
	public int deathTime = 0;
	public int attackTime = 0;
	public float oTilt;
	public float tilt;
	protected boolean dead = false;
	public AI ai;

	public Mob(Level var1) {
		super(var1);
		this.setPos(x, y, z);
		timeOffs = (float) Math.random() * 12398.0F;
		rot = (float) (Math.random() * 3.1415927410125732D * 2.0D);
		speed = 1.0F;
		ai = new BasicAI();
		footSize = 0.5F;
	}

	public void aiStep() {
		if (ai != null) {
			ai.tick(level, this);
		}

	}

	protected void bindTexture(TextureManager var1) {
		textureId = var1.load(textureName);
		GL11.glBindTexture(3553, textureId);
	}

	@Override
	protected void causeFallDamage(float var1) {
		if (!level.creativeMode) {
			int var2;
			if ((var2 = (int) Math.ceil(var1 - 3.0F)) > 0) {
				hurt((Entity) null, var2);
			}

		}
	}

	public void die(Entity var1) {
		if (!level.creativeMode) {
			if (deathScore > 0 && var1 != null) {
				var1.awardKillScore(this, deathScore);
			}

			dead = true;
		}
	}

	public void heal(int var1) {
		if (health > 0) {
			health += var1;
			if (health > 20) {
				health = 20;
			}

			invulnerableTime = invulnerableDuration / 2;
		}
	}

	@Override
	public void hurt(Entity var1, int var2) {
		if (!level.creativeMode) {
			if (health > 0) {
				if (ai != null) {
					ai.hurt(var1, var2);
				}
				if (invulnerableTime > invulnerableDuration / 2.0F) {
					if (lastHealth - var2 >= health) {
						return;
					}

					health = lastHealth - var2;
				} else {
					lastHealth = health;
					invulnerableTime = invulnerableDuration;
					health -= var2;
					hurtTime = hurtDuration = 10;
				}

				hurtDir = 0.0F;
				if (var1 != null) {
					float var3 = var1.x - x;
					float var4 = var1.z - z;
					hurtDir = (float) (Math.atan2(var4, var3) * 180.0D / 3.1415927410125732D)
							- yRot;
					knockback(var1, var2, var3, var4);
				} else {
					hurtDir = (int) (Math.random() * 2.0D) * 180;
				}

				if (health <= 0) {
					die(var1);
				}

			}
		}
	}

	@Override
	public boolean isPickable() {
		return !removed;
	}

	@Override
	public boolean isPushable() {
		return !removed && !noPhysics;
	}

	@Override
	public boolean isShootable() {
		return true;
	}

	public void knockback(Entity var1, int var2, float var3, float var4) {
		float var5 = MathHelper.sqrt(var3 * var3 + var4 * var4);
		float var6 = 0.4F;
		xd /= 2.0F;
		yd /= 2.0F;
		zd /= 2.0F;
		xd -= var3 / var5 * var6;
		yd += 0.4F;
		zd -= var4 / var5 * var6;
		if (yd > 0.4F) {
			yd = 0.4F;
		}

	}

	@Override
	public void render(TextureManager var1, float var2) {
		if (modelName != null) {
			float var3;
			if ((var3 = attackTime - var2) < 0.0F) {
				var3 = 0.0F;
			}

			while (yBodyRotO - yBodyRot < -180.0F) {
				yBodyRotO += 360.0F;
			}

			while (yBodyRotO - yBodyRot >= 180.0F) {
				yBodyRotO -= 360.0F;
			}

			while (xRotO - xRot < -180.0F) {
				xRotO += 360.0F;
			}

			while (xRotO - xRot >= 180.0F) {
				xRotO -= 360.0F;
			}

			while (yRotO - yRot < -180.0F) {
				yRotO += 360.0F;
			}

			while (yRotO - yRot >= 180.0F) {
				yRotO -= 360.0F;
			}

			float var4 = yBodyRotO + (yBodyRot - yBodyRotO) * var2;
			float var5 = oRun + (run - oRun) * var2;
			float var6 = yRotO + (yRot - yRotO) * var2;
			float var7 = xRotO + (xRot - xRotO) * var2;
			var6 -= var4;
			GL11.glPushMatrix();
			float var8 = animStepO + (animStep - animStepO) * var2;
			ColorCache varaa = getBrightnessColor();
			GL11.glColor3f(varaa.R, varaa.G, varaa.B);
			float var9 = 0.0625F;
			float var10 = -Math.abs(MathHelper.cos(var8 * 0.6662F)) * 5.0F * var5 * bobStrength
					- 23.0F;
			GL11.glTranslatef(xo + (x - xo) * var2, yo + (y - yo) * var2 - 1.62F + renderOffset, zo
					+ (z - zo) * var2);
			float var11;
			if ((var11 = hurtTime - var2) > 0.0F || health <= 0) {
				if (var11 < 0.0F) {
					var11 = 0.0F;
				} else {
					var11 = MathHelper.sin((var11 /= hurtDuration) * var11 * var11 * var11
							* 3.1415927F) * 14.0F;
				}

				float var12 = 0.0F;
				if (health <= 0) {
					var12 = (deathTime + var2) / 20.0F;
					if ((var11 += var12 * var12 * 800.0F) > 90.0F) {
						var11 = 90.0F;
					}
				}

				var12 = hurtDir;
				GL11.glRotatef(180.0F - var4 + rotOffs, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(1.0F, 1.0F, 1.0F);
				GL11.glRotatef(-var12, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(-var11, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(var12, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(-(180.0F - var4 + rotOffs), 0.0F, 1.0F, 0.0F);
			}

			GL11.glTranslatef(0.0F, -var10 * var9, 0.0F);
			GL11.glScalef(1.0F, -1.0F, 1.0F);
			GL11.glRotatef(180.0F - var4 + rotOffs, 0.0F, 1.0F, 0.0F);
			if (!allowAlpha) {
				GL11.glDisable(3008);
			} else {
				GL11.glDisable(2884);
			}

			GL11.glScalef(-1.0F, 1.0F, 1.0F);
			modelCache.getModel(modelName).attackOffset = var3 / 5.0F;
			bindTexture(var1);
			renderModel(var1, var8, var2, var5, var6, var7, var9);
			if (invulnerableTime > invulnerableDuration - 10) {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.75F);
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 1);
				bindTexture(var1);
				renderModel(var1, var8, var2, var5, var6, var7, var9);
				GL11.glDisable(3042);
				GL11.glBlendFunc(770, 771);
			}

			GL11.glEnable(3008);
			if (allowAlpha) {
				GL11.glEnable(2884);
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}

	public void renderModel(TextureManager var1, float var2, float var3, float var4, float var5,
			float var6, float var7) {
		modelCache.getModel(modelName).render(var2, var4, tickCount + var3, var5, var6, var7);
	}

	@Override
	public final void tick() {
		super.tick();
		oTilt = tilt;
		if (attackTime > 0) {
			--attackTime;
		}

		if (hurtTime > 0) {
			--hurtTime;
		}

		if (invulnerableTime > 0) {
			--invulnerableTime;
		}

		if (health <= 0) {
			++deathTime;
			if (deathTime > 20) {
				if (ai != null) {
					ai.beforeRemove();
				}

				remove();
			}
		}

		if (isUnderWater()) {
			if (airSupply > 0) {
				--airSupply;
			} else {
				hurt((Entity) null, 2);
			}
		} else {
			airSupply = 300;
		}

		if (isInWater()) {
			fallDistance = 0.0F;
		}

		if (isInLava()) {
			hurt((Entity) null, 10);
		}

		animStepO = animStep;
		yBodyRotO = yBodyRot;
		yRotO = yRot;
		xRotO = xRot;
		++tickCount;
		aiStep();
		float var1 = x - xo;
		float var2 = z - zo;
		float var3 = MathHelper.sqrt(var1 * var1 + var2 * var2);
		float var4 = yBodyRot;
		float var5 = 0.0F;
		oRun = run;
		float var6 = 0.0F;
		if (var3 > 0.05F) {
			var6 = 1.0F;
			var5 = var3 * 3.0F;
			var4 = (float) Math.atan2(var2, var1) * 180.0F / 3.1415927F - 90.0F;
		}

		if (!onGround) {
			var6 = 0.0F;
		}

		run += (var6 - run) * 0.3F;

		for (var1 = var4 - yBodyRot; var1 < -180.0F; var1 += 360.0F) {
			;
		}

		while (var1 >= 180.0F) {
			var1 -= 360.0F;
		}

		yBodyRot += var1 * 0.1F;

		for (var1 = yRot - yBodyRot; var1 < -180.0F; var1 += 360.0F) {
			;
		}

		while (var1 >= 180.0F) {
			var1 -= 360.0F;
		}

		boolean var7 = var1 < -90.0F || var1 >= 90.0F;
		if (var1 < -75.0F) {
			var1 = -75.0F;
		}

		if (var1 >= 75.0F) {
			var1 = 75.0F;
		}

		yBodyRot = yRot - var1;
		yBodyRot += var1 * 0.1F;
		if (var7) {
			var5 = -var5;
		}

		while (yRot - yRotO < -180.0F) {
			yRotO -= 360.0F;
		}

		while (yRot - yRotO >= 180.0F) {
			yRotO += 360.0F;
		}

		while (yBodyRot - yBodyRotO < -180.0F) {
			yBodyRotO -= 360.0F;
		}

		while (yBodyRot - yBodyRotO >= 180.0F) {
			yBodyRotO += 360.0F;
		}

		while (xRot - xRotO < -180.0F) {
			xRotO -= 360.0F;
		}

		while (xRot - xRotO >= 180.0F) {
			xRotO += 360.0F;
		}

		animStep += var5;
	}

	public void travel(float yya, float xxa) {
		float y1;
		float multiply = 1.0F;

		if (ai instanceof BasicAI) {
			BasicAI ai1 = (BasicAI) ai;
			if (!flyingMode) {
				if (ai1.running) {
					multiply = 10F; // 6x with momentum
				} else {
					multiply = 1.0F; // 1x
				}
			} else if (flyingMode && ai1.running) {
				multiply = 90F; // 6x
			} else {
				multiply = 15F; // 1x
			}
		}

		if (isInWater() && !flyingMode && !noPhysics) {
			y1 = y;

			moveRelative(yya, xxa * multiply, 0.02F * multiply);
			move(xd, yd, zd);

			xd *= 0.8F;
			yd *= 0.8F;
			zd *= 0.8F;

			yd = (float) (yd - 0.02D);

			if (horizontalCollision && isFree(xd, yd + 0.6F - y + y1, zd)) {
				yd = 0.3F;
			}

		} else if (isInLava() && !flyingMode && !noPhysics) {
			y1 = y;

			moveRelative(yya, xxa * multiply, 0.02F * multiply);
			move(xd, yd, zd);

			xd *= 0.5F;
			yd *= 0.5F;
			zd *= 0.5F;

			yd = (float) (yd - 0.02D);

			if (horizontalCollision && isFree(xd, yd + 0.6F - y + y1, zd)) {
				yd = 0.3F;
			}

		} else if (isInOrOnRope() && !flyingMode && !noPhysics) {
			y1 = y;
			if (multiply >= 5) {
				multiply = 2.5F;
			} else {
				multiply = 1.7f;
			}
			moveRelative(yya, xxa, 0.02F * multiply);
			move(xd, yd, zd);

			xd *= 0.5F;
			yd *= 0.5F;
			zd *= 0.5F;

			yd = (float) (yd - 0.02D) * multiply;

			if (horizontalCollision && isFree(xd, yd + 0.6F - y + y1, zd)) {
				yd = 0.3F;
			}

		} else {
			if (!flyingMode) {
				moveRelative(yya, xxa, (onGround ? 0.1F : 0.02F) * multiply);
			} else {
				moveRelative(yya, xxa, 0.02F * multiply);
			}
			float m = multiply / 5;
			if (m < 1) {
				m = 1;
			}
			move(xd, yd * m, zd);
			int var1 = level.getTile((int) x, (int) (y - 2.12F), (int) z);

			xd *= 0.91F;
			yd *= 0.98F;
			zd *= 0.91F;
			yd = (float) (yd - 0.08D);
			if (Block.blocks[var1] != Block.ICE) {

				if (flyingMode) {
					y1 = 0.0F;
					xd *= y1;
					zd *= y1;
				}
				if (onGround && !flyingMode) {
					y1 = 0.6F;

					xd *= y1;
					zd *= y1;
				}
			} else {
				double limit = 0.246D;
				if (xd > limit || xd < -limit || zd < -limit || zd > limit) {
					tilt = -20f;
				}
				if (xd > limit) {
					xd = (float) limit;
				}
				if (xd < -limit) {
					xd = (float) -limit;
				}
				if (zd < -limit) {
					zd = (float) -limit;
				}
				if (zd > limit) {
					zd = (float) limit;
				}
			}
		}
	}
}