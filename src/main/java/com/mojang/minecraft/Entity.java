package com.mojang.minecraft;

import java.io.Serializable;
import java.util.ArrayList;

import com.mojang.minecraft.level.BlockMap;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.model.Vec3D;
import com.mojang.minecraft.net.PositionUpdate;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.player.Player;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.minecraft.sound.StepSound;
import com.mojang.util.MathHelper;

public abstract class Entity implements Serializable {

	public static final long serialVersionUID = 0L;
	public Level level;
	public float xo;
	public float yo;
	public float zo;
	public float x;
	public float y;
	public float z;
	public float xd;
	public float yd;
	public float zd;
	public float yRot;
	public float xRot;
	public float yRotO;
	public float xRotO;
	/**
	 * The bounding box of this Entity.
	 */
	public AABB bb;
	public boolean onGround = false;
	public boolean horizontalCollision = false;
	public boolean collision = false;
	public boolean slide = true;
	public boolean removed = false;
	public float heightOffset = 0.0F;
	public float bbWidth = 0.6F;
	public float bbHeight = 1.8F;
	public float walkDistO = 0.0F;
	public float walkDist = 0.0F;
	public boolean makeStepSound = true;
	public float fallDistance = 0.0F;
	private int nextStep = 1;
	public BlockMap blockMap;
	public float xOld;
	public float yOld;
	public float zOld;
	public int textureId = 0;
	public float ySlideOffset = 0.0F;
	public float footSize = 0.0F;
	public boolean noPhysics = false;
	public float pushthrough = 0.0F;
	public boolean hovered = false;
	public boolean flyingMode = false;

	private int nextStepDistance;
	public float prevDistanceWalkedModified;
	public float distanceWalkedModified;
	public float distanceWalkedOnStepModified;

	public Entity(Level var1) {
		level = var1;
		this.setPos(0.0F, 0.0F, 0.0F);
	}

	public void awardKillScore(Entity var1, int var2) {
	}

	protected void causeFallDamage(float var1) {
	}

	/**
	 * Calculates the distance from this entity to the specified entity.
	 * 
	 * @param otherEntity
	 *            Entity to calculate the distance to.
	 * @return The distance between the two entities.
	 */
	public float distanceTo(Entity otherEntity) {
		return distanceTo(otherEntity.x, otherEntity.y, otherEntity.z);
	}

	/**
	 * Calculates the distance from this entity to the specified position.
	 * 
	 * @param posX
	 *            X-Coordinate of the position to calculate the distance to.
	 * @param posY
	 *            Y-Coordinate of the position to calculate the distance to.
	 * @param posZ
	 *            Z-Coordinate of the position to calculate the distance to.
	 * @return The distance between the entity and the position.
	 */
	public float distanceTo(float posX, float posY, float posZ) {
		// Euclidean distance
		final float dx = x - posX;
		final float dy = y - posY;
		final float dz = z - posZ;
		return MathHelper.sqrt((dx * dx) + (dy * dy) + (dz * dz));
	}

	/**
	 * Calculates the distance from this entity to the specified entity squared.
	 * This is basically calculating distance without using the expensive
	 * Math.sqrt function. Should only be used for relative distance.
	 * 
	 * @param otherEntity
	 *            Entity to calculate the distance to.
	 * @return The distance between the two entities squared.
	 */
	public float distanceToSqr(Entity otherEntity) {
		final float dx = x - otherEntity.x;
		final float dy = y - otherEntity.y;
		final float dz = z - otherEntity.z;
		return (dx * dx) + (dy * dy) + (dz * dz);
	}

	/**
	 * Gets the brightness of this entity
	 * 
	 * @return Brightness of the entity.
	 */
	public float getBrightness() {
		final int posX = (int) x;
		final int posY = (int) (y + heightOffset / 2.0F - 0.5F);
		final int posZ = (int) z;
		return level.getBrightness(posX, posY, posZ);
	}

	/**
	 * Gets the brightness color of this entity.
	 * 
	 * @return ColorCache containing brightness color information.
	 */
	public ColorCache getBrightnessColor() {
		final int posX = (int) x;
		final int posY = (int) (y + heightOffset / 2.0F - 0.5F);
		final int posZ = (int) z;
		return level.getBrightnessColor(posX, posY, posZ);
	}

	/**
	 * Gets the texture ID of this entity.
	 * 
	 * @return Entity's Texture ID.
	 */
	public int getTexture() {
		return textureId;
	}

	public void hurt(Entity var1, int var2) {
	}

	public void interpolateTurn(float var1, float var2) {
		yRot = (float) (yRot + var1 * 0.15D);
		xRot = (float) (xRot - var2 * 0.15D);
		if (xRot < -90.0F) {
			xRot = -90.0F;
		}

		if (xRot > 90.0F) {
			xRot = 90.0F;
		}

	}

	public boolean intersects(float var1, float var2, float var3, float var4,
			float var5, float var6) {
		return bb.intersects(var1, var2, var3, var4, var5, var6);
	}

	public boolean isCreativeModeAllowed() {
		return false;
	}

	public boolean isFree(float var1, float var2, float var3) {
		final AABB var4 = bb.cloneMove(var1, var2, var3);
		return level.getCubes(var4).size() > 0 ? false : !level
				.containsAnyLiquid(var4);
	}

	public boolean isFree(float var1, float var2, float var3, float var4) {
		final AABB var5 = bb.grow(var4, var4, var4).cloneMove(var1, var2, var3);
		return level.getCubes(var5).size() > 0 ? false : !level
				.containsAnyLiquid(var5);
	}

	public boolean isInLava() {
		return level
				.containsLiquid(bb.grow(0.0F, -0.4F, 0.0F), LiquidType.lava);
	}

	public boolean isInOrOnRope() {
		return level.containsBlock(bb.grow(-0.5F, 0.0F, -0.5F), Block.ROPE);
	}

	public boolean isInWater() {
		return level.containsLiquid(bb.grow(0.0F, -0.4F, 0.0F),
				LiquidType.water);
	}
	
	public boolean isOnIce() {
		final AABB body = bb.copy();
		body.move(-0.00001F, -0.3F, -0.00001F);
		return level.containsBlock(body.grow(-0.01F, 0, -0.01F), Block.ICE);
	}

	public boolean isLit() {
		final int var1 = (int) x;
		final int var2 = (int) y;
		final int var3 = (int) z;
		return level.isLit(var1, var2, var3);
	}

	public boolean isPickable() {
		return false;
	}

	public boolean isPushable() {
		return false;
	}

	public boolean isShootable() {
		return false;
	}

	public boolean isUnderWater() {
		int var1;
		return (var1 = level.getTile((int) x, (int) (y + 0.12F), (int) z)) != 0 ? Block.blocks[var1]
				.getLiquidType().equals(LiquidType.water) : false;
	}

	public void move(float var1, float var2, float var3) {
		if (noPhysics) {
			bb.move(var1, var2, var3);
			x = (bb.x0 + bb.x1) / 2.0F;
			// if((this.bb.y0 + this.heightOffset - this.ySlideOffset) > y){
			y = bb.y0 + heightOffset - ySlideOffset;
			// }
			z = (bb.z0 + bb.z1) / 2.0F;
			// this.yd = 0;
		} else {
			final float var4 = x;
			final float var5 = z;
			final float var6 = var1;
			final float var7 = var2;
			final float var8 = var3;
			final AABB var9 = bb.copy();
			ArrayList<AABB> var10 = level.getCubes(bb.expand(var1, var2, var3));

			for (int var11 = 0; var11 < var10.size(); ++var11) {
				var2 = var10.get(var11).clipYCollide(bb, var2);
			}

			bb.move(0.0F, var2, 0.0F);
			if (!slide && var7 != var2) {
				var3 = 0.0F;
				var2 = 0.0F;
				var1 = 0.0F;
			}

			final boolean var16 = onGround || var7 != var2 && var7 < 0.0F;

			int var12;
			for (var12 = 0; var12 < var10.size(); ++var12) {
				var1 = var10.get(var12).clipXCollide(bb, var1);
			}

			bb.move(var1, 0.0F, 0.0F);
			if (!slide && var6 != var1) {
				var3 = 0.0F;
				var2 = 0.0F;
				var1 = 0.0F;
			}

			for (var12 = 0; var12 < var10.size(); ++var12) {
				var3 = var10.get(var12).clipZCollide(bb, var3);
			}

			bb.move(0.0F, 0.0F, var3);
			if (!slide && var8 != var3) {
				var3 = 0.0F;
				var2 = 0.0F;
				var1 = 0.0F;
			}

			float var17;
			float var18;
			if (footSize > 0.0F && var16 && ySlideOffset < 0.05F
					&& (var6 != var1 || var8 != var3)) {
				var18 = var1;
				var17 = var2;
				final float var13 = var3;
				var1 = var6;
				var2 = footSize;
				var3 = var8;
				final AABB var14 = bb.copy();
				bb = var9.copy();
				var10 = level.getCubes(bb.expand(var6, var2, var8));

				int var15;
				for (var15 = 0; var15 < var10.size(); ++var15) {
					var2 = var10.get(var15).clipYCollide(bb, var2);
				}

				bb.move(0.0F, var2, 0.0F);
				if (!slide && var7 != var2) {
					var3 = 0.0F;
					var2 = 0.0F;
					var1 = 0.0F;
				}

				for (var15 = 0; var15 < var10.size(); ++var15) {
					var1 = var10.get(var15).clipXCollide(bb, var1);
				}

				bb.move(var1, 0.0F, 0.0F);
				if (!slide && var6 != var1) {
					var3 = 0.0F;
					var2 = 0.0F;
					var1 = 0.0F;
				}

				for (var15 = 0; var15 < var10.size(); ++var15) {
					var3 = var10.get(var15).clipZCollide(bb, var3);
				}

				bb.move(0.0F, 0.0F, var3);
				if (!slide && var8 != var3) {
					var3 = 0.0F;
					var2 = 0.0F;
					var1 = 0.0F;
				}

				if (var18 * var18 + var13 * var13 >= var1 * var1 + var3 * var3) {
					var1 = var18;
					var2 = var17;
					var3 = var13;
					bb = var14.copy();
				} else {
					ySlideOffset = (float) (ySlideOffset + 0.5D);
				}
			}

			horizontalCollision = var6 != var1 || var8 != var3;
			onGround = var7 != var2 && var7 < 0.0F;
			collision = horizontalCollision || var7 != var2;
			if (onGround) {
				if (fallDistance > 0.0F) {
					causeFallDamage(fallDistance / 2);
					fallDistance = 0.0F;
				}
			} else if (var2 < 0.0F) {
				fallDistance -= var2;
			}

			if (var6 != var1) {
				xd = 0.0F;
			}

			if (var7 != var2) {
				yd = 0.0F;
			}

			if (var8 != var3) {
				zd = 0.0F;
			}

			x = (bb.x0 + bb.x1) / 2.0F;
			y = bb.y0 + heightOffset - ySlideOffset;

			z = (bb.z0 + bb.z1) / 2.0F;
			var18 = x - var4;
			var17 = z - var5;
			walkDist = (float) (walkDist + MathHelper.sqrt(var18 * var18
					+ var17 * var17) * 0.6D);
		}
		final int var39 = (int) Math.floor(x);
		final int var30 = (int) Math.floor(y - 0.20000000298023224D - heightOffset);
		final int var31 = (int) Math.floor(z);
		final int var32 = level.getTile(var39, var30, var31);
		if (makeStepSound && onGround && !noPhysics) {
			if (this instanceof Player && !((Player) this).noPhysics) {
				distanceWalkedModified = (float) (distanceWalkedModified + Math
						.sqrt(var1 * var1 + var3 * var3) * 0.6D);
				distanceWalkedOnStepModified = (float) (distanceWalkedOnStepModified + Math
						.sqrt(var1 * var1 + var2 * var2 + var3 * var3) * 0.6D);

				if (distanceWalkedOnStepModified > nextStepDistance
						&& var32 > 0) {
					nextStepDistance = (int) distanceWalkedOnStepModified + 1;

					if (onGround) {
						playStepSound(var32);

					}
				}
			}
		}

		if (walkDist > nextStep && var32 > 0) {
			++nextStep;
		}
		ySlideOffset *= 0.4F;

	}

	public void moveRelative(float x, float y, float z) {

		float var4;
		if ((var4 = MathHelper.sqrt(x * x + y * y)) >= 0.01F) {
			if (var4 < 1.0F) {
				var4 = 1.0F;
			}

			var4 = z / var4;
			x *= var4;
			y *= var4;
			z = MathHelper.sin(yRot * 3.1415927F / 180.0F);
			var4 = MathHelper.cos(yRot * 3.1415927F / 180.0F);
			xd += x * var4 - y * z;
			zd += y * var4 + x * z;
		}
	}

	public void moveTo(float var1, float var2, float var3, float var4,
			float var5) {
		xo = x = var1;
		yo = y = var2;
		zo = z = var3;
		yRot = var4;
		xRot = var5;
		this.setPos(var1, var2, var3);
	}

	public void playerTouch(Entity var1) {
	}

	public void playSound(String var1, float var2, float var3) {
		level.playSound(var1, this, var2, var3, false);
	}

	protected void playStepSound(int var1) {
		final StepSound var2 = Block.blocks[var1].stepSound;

		if (!Block.blocks[var1].isLiquid()) {
			playSound(var2.getStepSound(), var2.getVolume() * 0.70F,
					var2.getPitch());
		}
	}

	public void push(Entity var1) {
		float var2 = var1.x - x;
		float var3 = var1.z - z;
		float var4;
		if ((var4 = var2 * var2 + var3 * var3) >= 0.01F) {
			var4 = MathHelper.sqrt(var4);
			var2 /= var4;
			var3 /= var4;
			var2 /= var4;
			var3 /= var4;
			var2 *= 0.05F;
			var3 *= 0.05F;
			var2 *= 1.0F - pushthrough;
			var3 *= 1.0F - pushthrough;
			this.push(-var2, 0.0F, -var3);
			var1.push(var2, 0.0F, var3);
		}

	}

	protected void push(float var1, float var2, float var3) {
		xd += var1;
		yd += var2;
		zd += var3;
	}

	public void remove() {
		removed = true;
	}

	public void render(TextureManager var1, float var2) {
	}

	public void renderHover(TextureManager var1, float var2) {
	}

	public void resetPos() {
		if (level != null) {
			final float var1 = level.xSpawn + 0.5F;
			float var2 = level.ySpawn;

			for (final double var3 = level.zSpawn + 0.5F; var2 > 0.0F; ++var2) {
				this.setPos(var1, var2, (float) var3);
				if (level.isInBounds((int) var1, (int) var2, (int) var3)) {
					if (level.getCubes(bb).size() == 0) {
						break;
					}
				} else {
					var2 = level.ySpawn;
					break;
				}
			}

		}
	}

	public void setLevel(Level var1) {
		level = var1;
	}

	public void setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		final float var4 = bbWidth / 2.0F;
		final float var5 = bbHeight / 2.0F;
		bb = new AABB(x - var4, y - var5, z - var4, x + var4, y + var5, z
				+ var4);
	}

	public void setPos(PositionUpdate var1) {
		if (var1.position) {
			this.setPos(var1.x, var1.y, var1.z);
		} else {
			this.setPos(x, y, z);
		}

		if (var1.rotation) {
			setRot(var1.yaw, var1.pitch);
		} else {
			setRot(yRot, xRot);
		}
	}

	protected void setRot(float var1, float var2) {
		yRot = var1;
		xRot = var2;
	}

	public void setSize(float var1, float var2) {
		bbWidth = var1;
		bbHeight = var2;
	}

	public boolean shouldRender(Vec3D var1) {
		final float var2 = x - var1.x;
		final float var3 = y - var1.y;
		float var4 = z - var1.z;
		var4 = var2 * var2 + var3 * var3 + var4 * var4;
		return shouldRenderAtSqrDistance(var4);
	}

	public boolean shouldRenderAtSqrDistance(float var1) {
		final float var2 = bb.getSize() * 64.0F;
		return var1 < var2 * var2;
	}

	public void tick() {
		walkDistO = walkDist;
		xo = x;
		yo = y;
		zo = z;
		xRotO = xRot;
		yRotO = yRot;
	}

	public void turn(float var1, float var2) {
		final float var3 = xRot;
		final float var4 = yRot;
		yRot = (float) (yRot + var1 * 0.15D);
		xRot = (float) (xRot - var2 * 0.15D);
		if (xRot < -90.0F) {
			xRot = -90.0F;
		}

		if (xRot > 90.0F) {
			xRot = 90.0F;
		}

		xRotO += xRot - var3;
		yRotO += yRot - var4;
	}
}