package com.mojang.minecraft.player;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.ProgressBarDisplay;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.model.HumanoidModel;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

public class PlayerExtension extends Player
{
  public static String version = "WoMClient-3.0";

  public Direction dir;

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
  public PlayerExtension(Level paramLevel, Minecraft mc)
  {
    super(paramLevel);
    Player paramPlayer = this;
    minecraft = mc;
    dir = new Direction(this.minecraft.settings);
    this.xo = paramPlayer.xo;
    this.yo = paramPlayer.yo;
    this.zo = paramPlayer.zo;
    this.xd = paramPlayer.xd;
    this.yd = paramPlayer.yd;
    this.zd = paramPlayer.zd;
    this.yRot = paramPlayer.yRot;
    this.xRot = paramPlayer.xRot;
    this.yRotO = paramPlayer.yRotO;
    this.xRotO = paramPlayer.xRotO;
    this.onGround = paramPlayer.onGround;
    this.horizontalCollision = paramPlayer.horizontalCollision;
    this.removed = paramPlayer.removed;
    this.heightOffset = paramPlayer.heightOffset;
    this.health = paramPlayer.health;
    this.modelName = paramPlayer.modelName;
    this.rotOffs = paramPlayer.rotOffs;
    this.ai = paramPlayer.ai;
    this.userType = paramPlayer.userType;
    this.input = paramPlayer.input;
    this.oBob = paramPlayer.oBob;
    this.score = paramPlayer.score;
    this.arrows = paramPlayer.arrows;

    setPos(paramPlayer.x, paramPlayer.y, paramPlayer.z);

    this.inventory = paramPlayer.inventory;
  }

  public void setPos(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    super.setPos(paramFloat1, paramFloat2, paramFloat3);
  }

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
}
	
