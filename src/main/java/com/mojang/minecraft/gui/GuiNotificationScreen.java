package com.mojang.minecraft.gui;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.Minecraft;

public final class GuiNotificationScreen extends GuiScreen {

	String title;
	String message;
	public boolean fadeOut = false;
	public GuiNotificationScreen(String Title, String Message){
		this.title = Title;
		this.message = Message;
	}
	
   public final void onOpen() {
      this.buttons.clear();
      this.grabsMouse = true;
   }
   
   int widthOffset = -200;

   public final void render(int var1, int var2) {
	   if(!fadeOut){
	   if(widthOffset < 0 ) {
		   widthOffset+=13;
		   if(widthOffset > 0)
			   widthOffset = 0;
	   	}
	   }
	  GL11.glPushMatrix();
	  GL11.glTranslatef(this.width * 0.7F - 2 + widthOffset, this.height * 0.7F - 21, 0.0F);
	  GL11.glScalef(0.3F, 0.3F, 0.3F);
	  drawBox(0, 0, this.width - 2, this.height - 2,
				Integer.MIN_VALUE);
      
      GL11.glScalef(2F, 2F, 2F);
      drawCenteredString(this.fontRenderer, title, (this.width / 2 / 2) , 20, 16777215);
      
      int lastSubstring = 0;
      int lastWidth = 0;
      for(int i = 0; i<= 41*6; i+=41){
    	  try{
    		  drawCenteredString(this.fontRenderer, message.substring(lastSubstring, Math.min(i, message.length())), (this.width / 2 / 2) , 22 + lastWidth, 7992295);
    		  lastSubstring = i;
    		  lastWidth +=12;
    	  }catch(Exception e){}
      }
      
      GL11.glPopMatrix();
   }
}
