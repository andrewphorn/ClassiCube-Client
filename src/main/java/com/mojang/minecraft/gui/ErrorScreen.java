package com.mojang.minecraft.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.lwjgl.opengl.Display;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.SessionData;
import com.mojang.minecraft.level.LevelIO;

public final class ErrorScreen extends GuiScreen {

   private String title;
   private String text;


   public ErrorScreen(String var1, String var2) {
      this.title = var1;
      this.text = var2;
   }

   public final void onOpen() {
		 this.buttons.clear();
		 this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 96, this.minecraft.isOnline() ? "Try to reconnect..." : "Restart ClassiCube"));
   }

   public final void render(int var1, int var2) {
      drawFadingBox(0, 0, this.width, this.height, -12574688, -11530224);
      drawCenteredString(this.fontRenderer, this.title, this.width / 2, 90, 16777215);
      drawCenteredString(this.fontRenderer, this.text, this.width / 2, 110, 16777215);
      super.render(var1, var2);
   }
   
   protected final void onButtonClick(Button var1) {
	      if(var1.id == 0) {
	    	  Minecraft cache = this.minecraft;
	        	 this.minecraft.shutdown();
	        	 if(this.minecraft.isOnline()){
	        		 this.minecraft.networkManager.netHandler.close();
	        	 }
	        	 this.minecraft = new Minecraft(cache.canvas, cache.applet, cache.width, cache.height, false, cache.isApplet );
	        	 
	         if(cache.isOnline()){
	        	 this.minecraft.host = cache.host;
	        	 this.minecraft.port = cache.port;
	        	 this.minecraft.host = cache.host + ":" + cache.port;
	        	 this.minecraft.session = cache.session;
	        	 this.minecraft.server = cache.server;
	         }
	         this.minecraft.running = false;
        	 this.minecraft.run();
	      }
   }

   protected final void onKeyPress(char var1, int var2) {}
}
