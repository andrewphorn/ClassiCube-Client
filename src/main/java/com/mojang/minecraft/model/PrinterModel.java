package com.mojang.minecraft.model;

/*
 * Based on PrinterModel.java from Minechem v4.x. Minechem v4.x is licensed under Creative Commons
 * Attribution-ShareAlike 3.0 Unported. 
 * The details of the licence can be found at: http://creativecommons.org/licenses/by-sa/3.0/us/
 */
public class PrinterModel extends Model
{
  //fields
    ModelPart Base;
    ModelPart RightWall;
    ModelPart LeftWall;
    ModelPart MiddleComp;
    ModelPart WholeBase;
    ModelPart Rack;
    ModelPart Back;
    ModelPart TopLeftNobble;
    ModelPart TopRightNobble;
    ModelPart TopMiddleNobble;
    ModelPart LeftLine;
    ModelPart RightLine;
  
  public PrinterModel()
  {
   // textureWidth = 128;
   // textureHeight = 128;
      Base = new ModelPart( 0, 0 );
      Base.setBounds(0F, 0F, 0F, 16, 1, 16, 0.0f);
      Base.setPosition(-8F, 23F, -8F);
     // Base.setTextureSize(128, 128);
      Base.mirror = true;
      setRotation(Base, 0F, 0F, 0F);
      RightWall = new ModelPart( 0, 19);
      RightWall.setBounds(0F, 0F, 0F, 1, 5, 14, 0.0f);
      RightWall.setPosition(6F, 18F, -7F);
      //RightWall.setTextureSize(128, 128);
      RightWall.mirror = true;
      setRotation(RightWall, 0F, 0F, 0F);
      LeftWall = new ModelPart( 0, 19);
      LeftWall.setBounds(0F, 0F, 0F, 1, 5, 14, 0.0f);
      LeftWall.setPosition(-7F, 18F, -7F);
      //LeftWall.setTextureSize(128, 128);
      LeftWall.mirror = true;
      setRotation(LeftWall, 0F, 0F, 0F);
      MiddleComp = new ModelPart( 31, 19);
      MiddleComp.setBounds(0F, 0F, 0F, 8, 4, 11, 0.0f);
      MiddleComp.setPosition(-4F, 17.5F, -7F);
     // MiddleComp.setTextureSize(128, 128);
      MiddleComp.mirror = true;
      setRotation(MiddleComp, 0F, 0F, 0F);
      WholeBase = new ModelPart( 65, 0);
      WholeBase.setBounds(0F, 0F, 0F, 12, 5, 11, 0.0f);
      WholeBase.setPosition(-6F, 17F, -6.5F);
      //WholeBase.setTextureSize(128, 128);
      WholeBase.mirror = true;
      setRotation(WholeBase, 0F, 0F, 0F);
      Rack = new ModelPart( 0, 40);
      Rack.setBounds(0F, -1F, 0F, 12, 8, 1, 0.0f);
      Rack.setPosition(-6F, 13F, 7F);
     // Rack.setTextureSize(128, 128);
      Rack.mirror = true;
      setRotation(Rack, -0.3346075F, 0F, 0F);
      Back = new ModelPart( 0, 50);
      Back.setBounds(0F, 0F, 0F, 12, 4, 1,0.0f);
      Back.setPosition(-6F, 19F, 5.8F);
    //  Back.setTextureSize(128, 128);
      Back.mirror = true;
      setRotation(Back, 0F, 0F, 0F);
      TopLeftNobble = new ModelPart( 0, 58);
      TopLeftNobble.setBounds(0F, 0F, 0F, 3, 1, 3, 0.0f);
      TopLeftNobble.setPosition(-5F, 16F, 0F);
      //TopLeftNobble.setTextureSize(128, 128);
      TopLeftNobble.mirror = true;
      setRotation(TopLeftNobble, 0F, 0F, 0F);
      TopRightNobble = new ModelPart( 0, 58);
      TopRightNobble.setBounds(0F, 0F, 0F, 3, 1, 3, 0.0f);
      TopRightNobble.setPosition(2F, 16F, 0F);
     // TopRightNobble.setTextureSize(128, 128);
      TopRightNobble.mirror = true;
      setRotation(TopRightNobble, 0F, 0F, 0F);
      TopMiddleNobble = new ModelPart( 13, 58);
      TopMiddleNobble.setBounds(0F, 0F, 0F, 4, 1, 6, 0.0f);
      TopMiddleNobble.setPosition(-2F, 16.5F, -3F);
     // TopMiddleNobble.setTextureSize(128, 128);
      TopMiddleNobble.mirror = true;
      setRotation(TopMiddleNobble, 0F, 0F, 0F);
      LeftLine = new ModelPart( 28, 42);
      LeftLine.setBounds(0F, 0F, 0F, 1, 1, 9, 0.0f);
      LeftLine.setPosition(-4F, 16.5F, -7F);
     // LeftLine.setTextureSize(128, 128);
      LeftLine.mirror = true;
      setRotation(LeftLine, 0F, 0F, 0F);
      RightLine = new ModelPart( 28, 42);
      RightLine.setBounds(0F, 0F, 0F, 1, 1, 9, 0.0f);
      RightLine.setPosition(3F, 16.5F, -7F);
     // RightLine.setTextureSize(128, 128);
      RightLine.mirror = true;
      setRotation(RightLine, 0F, 0F, 0F);
  }
  
  public final void render(float var1, float var2, float var3, float var4,
	    float var5, float f5) {
    Base.render(f5);
    RightWall.render(f5);
    LeftWall.render(f5);
    MiddleComp.render(f5);
    WholeBase.render(f5);
    Rack.render(f5);
    Back.render(f5);
    TopLeftNobble.render(f5);
    TopRightNobble.render(f5);
    TopMiddleNobble.render(f5);
    LeftLine.render(f5);
    RightLine.render(f5);
  }
  
  private void setRotation(ModelPart model, float x, float y, float z) {
      model.pitch = x;
      model.yaw = y;
      model.roll = z;
  }
  

}
