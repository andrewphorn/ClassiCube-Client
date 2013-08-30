package com.mojang.minecraft.player;

import com.mojang.minecraft.GameSettings;

public class Direction
{
	public Direction(com.mojang.minecraft.GameSettings gameSettings)
	{
		this.settings = gameSettings;
	}
	public GameSettings settings;
  public float move = 0.0F;
  public float strafe = 0.0F;
  public float elevate = 0.0F;
  public float mult = 1.0F;
  public boolean fall = false;
  public boolean jump = false;
  public boolean fly = false;
  public boolean noclip = false;
  public boolean cliplock = false;

  private boolean[] keylist = new boolean[10];

  public final void processKey(int paramInt, boolean paramBoolean)
  {
    if (paramInt == settings.forwardKey.key) this.keylist[0] = paramBoolean;
    if (paramInt == settings.leftKey.key) this.keylist[1] = paramBoolean;
    if (paramInt == settings.backKey.key) this.keylist[2] = paramBoolean;
    if (paramInt == settings.rightKey.key) this.keylist[3] = paramBoolean;
    if (paramInt == 57) this.keylist[4] = paramBoolean;
    if (paramInt == 16) this.keylist[5] = paramBoolean;
    if (paramInt == 18) this.keylist[6] = paramBoolean;
    if (paramInt == 42) this.keylist[7] = paramBoolean;
    if (paramInt == 29) this.keylist[8] = paramBoolean;
    if (paramInt == 45) this.keylist[9] = paramBoolean;
    if ((paramInt == 60) && (paramBoolean)) 
    	this.cliplock = (!this.cliplock);
    if ((paramInt == 44) && (paramBoolean)) 
    	this.fly = (!this.fly);

  }

  public final void clear()
  {
    for (int i = 0; i < 10; i++)
      this.keylist[i] = false;
  }

	public static final long serialVersionUID = 0L;
  public final void calc()
  {
    this.move = 0.0F;
    this.strafe = 0.0F;
    this.elevate = 0.0F;
    if (this.keylist[0] != false) this.move -= 1.0F;
    if (this.keylist[1] != false) this.strafe -= 1.0F;
    if (this.keylist[2] != false) this.move += 1.0F;
    if (this.keylist[3] != false) this.strafe += 1.0F;
    if (this.fly) {
      if (this.keylist[5] != false) this.elevate += 0.3F;
      if (this.keylist[6] != false) this.elevate -= 0.3F;
    }
    this.mult = 1.0F;
    if (this.keylist[7] != false) this.mult = 5.0F;
    else if (this.keylist[8] != false) this.mult = 2.0F;

    this.noclip = this.cliplock;
    if (this.keylist[9] != false) {
      this.noclip = (!this.noclip);
    }

    this.jump = this.keylist[4];
  }
}
