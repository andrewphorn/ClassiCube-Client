package com.mojang.minecraft;

import java.io.Serializable;
import java.util.ArrayList;

import com.mojang.minecraft.level.BlockMap;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.util.Vec3D;
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
    public AABB boundingBox;
    public boolean onGround = false;
    public boolean horizontalCollision = false;
    public boolean collision = false;
    public boolean slide = true;
    public boolean removed = false;
    public float heightOffset = 0F;
    public float bbWidth = 0.6F;
    public float bbHeight = 1.8F;
    public float walkDistO = 0F;
    public float walkDist = 0F;
    public boolean makeStepSound = true;
    public float fallDistance = 0F;
    private int nextStep = 1;
    public BlockMap blockMap;
    public float xOld;
    public float yOld;
    public float zOld;
    public int textureId = 0;
    public float ySlideOffset = 0F;
    public float footSize = 0F;
    public boolean noPhysics = false;
    public float pushthrough = 0F;
    public boolean hovered = false;
    public boolean flyingMode = false;

    private int nextStepDistance;
    public float prevDistanceWalkedModified;
    public float distanceWalkedModified;
    public float distanceWalkedOnStepModified;

    public Entity(Level entityLevel) {
        level = entityLevel;
        this.setPos(0F, 0F, 0F);
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
        float dx = x - posX;
        float dy = y - posY;
        float dz = z - posZ;
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
        float dx = x - otherEntity.x;
        float dy = y - otherEntity.y;
        float dz = z - otherEntity.z;
        return (dx * dx) + (dy * dy) + (dz * dz);
    }

    /**
     * Gets the brightness of this entity
     * 
     * @return Brightness of the entity.
     */
    public float getBrightness() {
        int posX = (int) x;
        int posY = (int) (y + heightOffset / 2F - 0.5F);
        int posZ = (int) z;
        return level.getBrightness(posX, posY, posZ);
    }

    /**
     * Gets the brightness color of this entity.
     * 
     * @return ColorCache containing brightness color information.
     */
    public ColorCache getBrightnessColor() {
        int posX = (int) x;
        int posY = (int) (y + heightOffset / 2F - 0.5F);
        int posZ = (int) z;
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

    public void hurt(Entity entity, int hurtAmount) {
    }

    // TODO var1 var2
    public void interpolateTurn(float var1, float var2) {
        yRot = (float) (yRot + var1 * 0.15D);
        xRot = (float) (xRot - var2 * 0.15D);
        if (xRot < -90F) {
            xRot = -90F;
        }

        if (xRot > 90F) {
            xRot = 90F;
        }

    }

    public boolean intersects(float x0, float y0, float z0, float x1, float y1, float z1) {
        return boundingBox.intersects(x0, y0, z0, x1, y1, z1);
    }

    public boolean isCreativeModeAllowed() {
        return false;
    }

    public boolean isFree(float x, float y, float z) {
        AABB bounds = boundingBox.cloneMove(x, y, z);
        return level.getCubes(bounds).size() > 0 ? false : !level.containsAnyLiquid(bounds);
    }

    // TODO - growAmount may not be an accurate interpretation
    public boolean isFree(float x, float y, float z, float growAmount) {
        AABB bounds = boundingBox.grow(growAmount, growAmount, growAmount).cloneMove(x, y, z);
        return level.getCubes(bounds).size() > 0 ? false : !level.containsAnyLiquid(bounds);
    }

    public boolean isInLava() {
        return level.containsLiquid(boundingBox.grow(0F, -0.4F, 0F), LiquidType.lava);
    }

    public boolean isInOrOnRope() {
        return level.containsBlock(boundingBox.grow(-0.5F, 0F, -0.5F), Block.ROPE);
    }

    public boolean isInWater() {
        return level.containsLiquid(boundingBox.grow(0F, -0.4F, 0F), LiquidType.water);
    }

    public boolean isLit() {
        int iX = (int) x;
        int iY = (int) y;
        int iZ = (int) z;
        return level.isLit(iX, iY, iZ);
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
        int textureID;
        return (textureID = level.getTile((int) x, (int) (y + 0.12F), (int) z)) != 0 ? Block.blocks[textureID]
                .getLiquidType().equals(LiquidType.water) : false;
    }

    public void move(float xMove, float yMove, float zMove) {
        if (noPhysics) {
            boundingBox.move(xMove, yMove, zMove);
            x = (boundingBox.x0 + boundingBox.x1) / 2F;
            y = boundingBox.y0 + heightOffset - ySlideOffset;
            z = (boundingBox.z0 + boundingBox.z1) / 2F;
        } else {
            float var4 = x;
            float var5 = z;
            float var6 = xMove;
            float var7 = yMove;
            float var8 = zMove;
            AABB bbCopy = boundingBox.copy();
            ArrayList<AABB> cubes = level.getCubes(boundingBox.expand(xMove, yMove, zMove));

            for (int i = 0; i < cubes.size(); ++i) {
                yMove = cubes.get(i).clipYCollide(boundingBox, yMove);
            }

            boundingBox.move(0F, yMove, 0F);
            if (!slide && var7 != yMove) {
                zMove = 0F;
                yMove = 0F;
                xMove = 0F;
            }

            boolean var16 = onGround || var7 != yMove && var7 < 0F;

            for (int i = 0; i < cubes.size(); ++i) {
                xMove = cubes.get(i).clipXCollide(boundingBox, xMove);
            }

            boundingBox.move(xMove, 0F, 0F);
            if (!slide && var6 != xMove) {
                zMove = 0F;
                yMove = 0F;
                xMove = 0F;
            }

            for (int i = 0; i < cubes.size(); ++i) {
                zMove = cubes.get(i).clipZCollide(boundingBox, zMove);
            }

            boundingBox.move(0F, 0F, zMove);
            if (!slide && var8 != zMove) {
                zMove = 0F;
                yMove = 0F;
                xMove = 0F;
            }

            float var17;
            float var18;
            if (footSize > 0F && var16 && ySlideOffset < 0.05F && (var6 != xMove || var8 != zMove)) {
                var18 = xMove;
                var17 = yMove;
                float var13 = zMove;
                xMove = var6;
                yMove = footSize;
                zMove = var8;
                AABB var14 = boundingBox.copy();
                boundingBox = bbCopy.copy();
                cubes = level.getCubes(boundingBox.expand(var6, yMove, var8));

                for (int i = 0; i < cubes.size(); ++i) {
                    yMove = cubes.get(i).clipYCollide(boundingBox, yMove);
                }

                boundingBox.move(0F, yMove, 0F);
                if (!slide && var7 != yMove) {
                    zMove = 0F;
                    yMove = 0F;
                    xMove = 0F;
                }

                for (int i = 0; i < cubes.size(); ++i) {
                    xMove = cubes.get(i).clipXCollide(boundingBox, xMove);
                }

                boundingBox.move(xMove, 0F, 0F);
                if (!slide && var6 != xMove) {
                    zMove = 0F;
                    yMove = 0F;
                    xMove = 0F;
                }

                for (int i = 0; i < cubes.size(); ++i) {
                    zMove = cubes.get(i).clipZCollide(boundingBox, zMove);
                }

                boundingBox.move(0F, 0F, zMove);
                if (!slide && var8 != zMove) {
                    zMove = 0F;
                    yMove = 0F;
                    xMove = 0F;
                }

                if (var18 * var18 + var13 * var13 >= xMove * xMove + zMove * zMove) {
                    xMove = var18;
                    yMove = var17;
                    zMove = var13;
                    boundingBox = var14.copy();
                } else {
                    ySlideOffset = (float) (ySlideOffset + 0.5D);
                }
            }

            horizontalCollision = var6 != xMove || var8 != zMove;
            onGround = var7 != yMove && var7 < 0F;
            collision = horizontalCollision || var7 != yMove;
            if (onGround) {
                if (fallDistance > 0F) {
                    causeFallDamage(fallDistance / 2);
                    fallDistance = 0F;
                }
            } else if (yMove < 0F) {
                fallDistance -= yMove;
            }

            if (var6 != xMove) {
                xd = 0F;
            }

            if (var7 != yMove) {
                yd = 0F;
            }

            if (var8 != zMove) {
                zd = 0F;
            }

            x = (boundingBox.x0 + boundingBox.x1) / 2F;
            y = boundingBox.y0 + heightOffset - ySlideOffset;

            z = (boundingBox.z0 + boundingBox.z1) / 2F;
            var18 = x - var4;
            var17 = z - var5;
            walkDist = (float) (walkDist + MathHelper.sqrt(var18 * var18 + var17 * var17) * 0.6D);
        }
        int var39 = (int) Math.floor(x);
        int var30 = (int) Math.floor(y - 0.20000000298023224D - heightOffset);
        int var31 = (int) Math.floor(z);
        int var32 = level.getTile(var39, var30, var31);
        if (makeStepSound && onGround && !noPhysics) {
            if (this instanceof Player && !((Player) this).noPhysics) {
                distanceWalkedModified = (float) (distanceWalkedModified + Math.sqrt(xMove * xMove
                        + zMove * zMove) * 0.6D);
                distanceWalkedOnStepModified = (float) (distanceWalkedOnStepModified + Math
                        .sqrt(xMove * xMove + yMove * yMove + zMove * zMove) * 0.6D);

                if (distanceWalkedOnStepModified > nextStepDistance && var32 > 0) {
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
            if (var4 < 1F) {
                var4 = 1F;
            }

            var4 = z / var4;
            x *= var4;
            y *= var4;
            z = MathHelper.sin(yRot * (float) Math.PI / 180F);
            var4 = MathHelper.cos(yRot * (float) Math.PI / 180F);
            xd += x * var4 - y * z;
            zd += y * var4 + x * z;
        }
    }

    public void moveTo(float x, float y, float z, float pitch, float yaw) {
        xo = this.x = x;
        yo = this.y = y;
        zo = this.z = z;
        yRot = pitch;
        xRot = yaw;
        this.setPos(x, y, z);
    }

    public void playerTouch(Entity entity) {
    }

    public void playSound(String file, float volume, float pitch) {
        boolean footstep = false;
        level.playSound(file, this, volume, pitch, footstep);
    }

    protected void playStepSound(int tile) {
        StepSound sound = Block.blocks[tile].stepSound;

        if (!Block.blocks[tile].isLiquid()) {
            playSound(sound.getStepSound(), sound.getVolume() * 0.70F, sound.getPitch());
        }
    }

    public void push(Entity entity) {
        float dx = entity.x - x;
        float dz = entity.z - z;
        float dy;
        if ((dy = dx * dx + dz * dz) >= 0.01F) {
            dy = MathHelper.sqrt(dy);
            dx /= dy;
            dz /= dy;
            dx /= dy;
            dz /= dy;
            dx *= 0.05F;
            dz *= 0.05F;
            dx *= 1F - pushthrough;
            dz *= 1F - pushthrough;
            this.push(-dx, 0F, -dz);
            entity.push(dx, 0F, dz);
        }

    }

    protected void push(float x, float y, float z) {
        xd += x;
        yd += y;
        zd += z;
    }

    public void remove() {
        removed = true;
    }

    // TODO var2
    public void render(TextureManager textureManager, float var2) {
    }

    public void renderHover(TextureManager textureManager, float var2) {
    }

    public void resetPos() {
        if (level != null) {
            float xSpawn = level.xSpawn + 0.5F;
            float ySpawn = level.ySpawn;

            for (double zSpawn = level.zSpawn + 0.5F; ySpawn > 0F; ++ySpawn) {
                this.setPos(xSpawn, ySpawn, (float) zSpawn);
                if (level.isInBounds((int) xSpawn, (int) ySpawn, (int) zSpawn)) {
                    if (level.getCubes(boundingBox).size() == 0) {
                        break;
                    }
                } else {
                    ySpawn = level.ySpawn;
                    break;
                }
            }

        }
    }

    public void setLevel(Level entityLevel) {
        level = entityLevel;
    }

    public void setPos(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        float middleWidth = bbWidth / 2F;
        float middleHeight = bbHeight / 2F;
        boundingBox = new AABB(x - middleWidth, y - middleHeight, z - middleWidth, x + middleWidth,
                y + middleHeight, z + middleWidth);
    }

    public void setPos(PositionUpdate update) {
        if (update.position) {
            this.setPos(update.x, update.y, update.z);
        } else {
            this.setPos(x, y, z);
        }

        if (update.rotation) {
            setRot(update.yaw, update.pitch);
        } else {
            setRot(yRot, xRot);
        }
    }

    protected void setRot(float pitch, float yaw) {
        yRot = pitch;
        xRot = yaw;
    }

    public void setSize(float width, float height) {
        bbWidth = width;
        bbHeight = height;
    }

    public boolean shouldRender(Vec3D vec3d) {
        float dx = x - vec3d.x;
        float dy = y - vec3d.y;
        float dz = z - vec3d.z;
        dz = dx * dx + dy * dy + dz * dz;
        return shouldRenderAtSqrDistance(dz);
    }

    public boolean shouldRenderAtSqrDistance(float sqDist) {
        float bbSize = boundingBox.getSize() * 64F;
        return sqDist < bbSize * bbSize;
    }

    public void tick() {
        walkDistO = walkDist;
        xo = x;
        yo = y;
        zo = z;
        xRotO = xRot;
        yRotO = yRot;
    }

    public void turn(float pitchDiff, float yawDiff) {
        float oldXRot = xRot;
        float oldYRot = yRot;
        yRot = (float) (yRot + pitchDiff * 0.15D);
        xRot = (float) (xRot - yawDiff * 0.15D);
        if (xRot < -90F) {
            xRot = -90F;
        }

        if (xRot > 90F) {
            xRot = 90F;
        }

        xRotO += xRot - oldXRot;
        yRotO += yRot - oldYRot;
    }
}