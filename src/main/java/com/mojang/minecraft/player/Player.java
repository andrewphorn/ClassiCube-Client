package com.mojang.minecraft.player;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.model.HumanoidModel;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.List;

public class Player extends Mob
{
    	Direction dir;
	public Player(Level var1, Minecraft minecraft) {
		super(var1);
		this.minecraft = minecraft;
		if(var1 != null) {
			var1.player = this;
			var1.removeEntity(this);
			var1.addEntity(this);
		}

		this.heightOffset = 1.62F;
		this.health = 20;
		this.modelName = "humanoid";
		this.rotOffs = 180.0F;
		this.ai = new Player$1(this);
		this.dir = new Direction(minecraft.settings);
	}

	  private int nox = 0;
	  private int noc = 0;
	  private int nos = 0;

	  private int jumpCount = 0;
	  private boolean showStatus = false;

	  private float decayX = 0.0F;
	  private float decayY = 0.0F;
	  private long decayTime = 0L;
	  boolean HacksEnabled = true;

	  public static boolean noPush = false;
	  Minecraft minecraft;
	  

	  public void setPos(float paramFloat1, float paramFloat2, float paramFloat3)
	  {
	    super.setPos(paramFloat1, paramFloat2, paramFloat3);
	  }
	  
	  @Override
	  public final void aiStep()
	  {
	    super.aiStep();
	    if ((this.nox == 0) || (this.nos == 0) || (this.noc == 0))
	    {
	      try
	      {
	    	  String title = this.minecraft.progressBar.title;
	    	  String text = this.minecraft.progressBar.text;
	          if ((title.length() > 0) && (title != "Connecting..")) {
	            System.err.println(new StringBuilder().append("Got server: ").append(title).append(": ").append(text).toString());

	          this.nos = -1; this.nox = -1; this.noc = -1;
	          
	          String joinedString = new StringBuilder().append(title).append(" ").append(text).toString().toLowerCase();
	          
	          if (joinedString.indexOf("-hax") > -1) { 
	        	  this.nos = 1; this.nox = 1; this.noc = 1;
	          } else if (joinedString.indexOf("+hax") > -1) { 
	        	  this.nos = -1; this.nox = -1; this.noc = -1;
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

	          if ((userType >= 100) && (joinedString.indexOf("+ophax") > -1)) {
	            this.nox = -1;
	            this.noc = -1;
	            this.nos = -1;
	          }
	        }
	      }
	      catch (Exception e)
	      {
	    	  e.printStackTrace();
	      }
	    }
	    int i = 0;
	    int j = 0;
	    int k = 1;
	    int m = 0;
	    float f1 = 1.0F;
	    Object localObject = "";

	    if ((this.dir.fly) && (this.nox < 1)) i = 1;
	    if ((this.dir.noclip) && (this.noc < 0)) j = 1;
	    if ((this.dir.mult > 1.0F) && (this.nos < 1)) {
	      m = 1;
	      f1 = this.dir.mult;
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
	    boolean bool3 = isInSpiderWeb();

	    float f2 = 0.0F;

	    this.dir.calc();

	    if ((i != 0) || (j != 0)) this.yd = this.dir.elevate;

	    if ((this.onGround) || (i != 0)) this.jumpCount = 0;

	    if (this.dir.jump) {
	      if (bool1)
	      {
	        this.yd += 0.08F;
	      }
	      else if (bool3)
	      {
	        this.yd += 0.05F;
	      }
	      else if (bool2)
	      {
	        this.yd += 0.07F;
	      }
	      else if (i != 0) {
	        this.yd += 0.05F;
	      }
	      else if (this.onGround) {
	        if (!this.dir.fall) {
	          if ((!HacksEnabled) && (k != 0))
	            this.yd = 0.48F;
	          else
	            this.yd = 0.35F;
	          this.dir.fall = true;
	          this.jumpCount += 1;
	        }
	      }
	      else if (HacksEnabled && (!this.dir.fall) && (k != 0) && (this.jumpCount < 3)) {
	        this.yd = 0.5F;
	        this.dir.fall = true;
	        this.jumpCount += 1;
	      }
	    }
	    else {
	      this.dir.fall = false;
	    }

	    if (HacksEnabled && (k != 0) && (this.jumpCount > 1)) {
	      f1 *= 2.5F;
	      f1 *= this.jumpCount;
	    }

	    if ((bool1) && (i == 0) && (j == 0)) {
	      f2 = this.y;
	      moveRelative(this.dir.strafe, this.dir.move, 0.02F * f1);
	      move(this.xd * f1, this.yd * f1, this.zd * f1);
	      this.xd *= 0.8F;
	      this.yd *= 0.8F;
	      this.zd *= 0.8F;
	      this.yd = ((float)(this.yd - 0.02D));
	      if ((this.horizontalCollision) && (isFree(this.xd, this.yd + 0.6F - this.y + f2, this.zd)))
	        this.yd = 0.3F;
	      return;
	    }
	    if ((bool2) && (i == 0) && (j == 0))
	    {
	      f2 = this.y;
	      moveRelative(this.dir.strafe, this.dir.move, 0.02F * f1);
	      move(this.xd * f1, this.yd * f1, this.zd * f1);
	      this.xd *= 0.5F;
	      this.yd *= 0.5F;
	      this.zd *= 0.5F;
	      this.yd = ((float)(this.yd - 0.02D));
	      if ((this.horizontalCollision) && (isFree(this.xd, this.yd + 0.6F - this.y + f2, this.zd)))
	        this.yd = 0.3F;
	      return;
	    }

	    if (i != 0) {
	      f1 = (float)(f1 * 1.2D);
	    }

	    float f4 = 0.0F;
	    float f3;
	    if (j != 0) {
	      f4 = i != 0 ? 0.72F : 0.71F;
	      if (i != 0) this.yd = this.dir.elevate;
	      f3 = 0.2F;
	    }
	    else if ((this.onGround) || (this.jumpCount > 0) || (i != 0)) {
	      f3 = 0.1F;
	    }
	    else {
	      f3 = 0.02F;
	    }

	    moveRelative(this.dir.strafe, this.dir.move, f3 * f1);

	    if ((j != 0) && ((this.xd != 0.0F) || (this.zd != 0.0F))) {
	      moveTo(this.x + this.xd, this.y + this.yd - f4, this.z + this.zd, this.yRot, this.xRot);
	      this.yo = (this.y += f4);
	    }
	    else {
	      move(this.xd * f1, this.yd * f1, this.zd * f1);
	    }
	    this.xd *= 0.91F;
	    this.yd *= 0.98F;
	    this.zd *= 0.91F;
	    f2 = 0.6F;

	    if (i != 0) {
	      this.yd *= f2 / 4.0F;
	      this.walkDist = 0.0F;
	      try {
	        Field localField2 = Entity.class.getDeclaredField("nextStep");
	        localField2.setAccessible(true);
	        localField2.set(this, Integer.valueOf(0));
	      } catch (Exception localException2) {
	      }
	    }
	    else {
	      this.yd = ((float)(this.yd - 0.01D));
	    }
	    this.xd *= f2;
	    this.zd *= f2;
	  }

	  public void releaseAllKeys()
	  {
	    this.dir.clear();
	  }

	  public void setKey(int paramInt, boolean paramBoolean)
	  {
	    this.dir.processKey(paramInt, paramBoolean);
	  }

	  public void turn(float paramFloat1, float paramFloat2)
	  {
	      super.turn(paramFloat1, paramFloat2);
	  }

	  protected void push(float paramFloat1, float paramFloat2, float paramFloat3)
	  {
	    if (noPush) return;
	    this.xd += paramFloat1;
	    this.yd += paramFloat2;
	    this.zd += paramFloat3;
	  }

	@Override
	public void bindTexture(TextureManager var1) {
		if(newTexture != null) {
			newTextureId = var1.load(newTexture);
			newTexture = null;
		}

		int var2;
		if(newTextureId < 0) {
			var2 = var1.load("/char.png");
			GL11.glBindTexture(3553, var2);
		} else {
			var2 = newTextureId;
			GL11.glBindTexture(3553, var2);
		}
	}

	@Override
	public void render(TextureManager var1, float var2) {}

	@Override
	public void resetPos() {
		this.heightOffset = 1.62F;
		this.setSize(0.6F, 1.8F);
		super.resetPos();
		if(this.level != null) {
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
		if(var1 != null) {
			this.xd = -MathHelper.cos((this.hurtDir + this.yRot) * 3.1415927F / 180.0F) * 0.1F;
			this.zd = -MathHelper.sin((this.hurtDir + this.yRot) * 3.1415927F / 180.0F) * 0.1F;
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
	public void remove() {}

	@Override
	public void hurt(Entity var1, int var2) {
		if(!this.level.creativeMode) {
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

	public boolean addResource(int var1) {
		return this.inventory.addResource(var1);
	}

	public int getScore() {
		return this.score;
	}

	public HumanoidModel getModel() {
		return (HumanoidModel)modelCache.getModel(this.modelName);
	}
}
