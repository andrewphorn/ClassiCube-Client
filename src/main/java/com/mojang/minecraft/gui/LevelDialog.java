package com.mojang.minecraft.gui;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

final class LevelDialog extends Thread {

   // $FF: synthetic field
   private LoadLevelScreen screen;


   LevelDialog(LoadLevelScreen var1) {
	   super();
      this.screen = var1;
   }

   public final void run() {
      JFileChooser var1;
      try {
         LoadLevelScreen var10000 = this.screen;
         var1 = new JFileChooser();
         var10000.chooser = var1;
         FileNameExtensionFilter var3 = new FileNameExtensionFilter("Minecraft levels", new String[]{"mine"});
         this.screen.chooser.setFileFilter(var3);
         this.screen.chooser.setMultiSelectionEnabled(false);
         int var7;
         if(this.screen.saving) {
            var7 = this.screen.chooser.showSaveDialog(this.screen.minecraft.canvas);
         } else {
            var7 = this.screen.chooser.showOpenDialog(this.screen.minecraft.canvas);
         }

         if(var7 == 0) {
            (this.screen).selectedFile = this.screen.chooser.getSelectedFile();
         }
      } finally {
         this.screen.frozen = false;
         var1 = null;
         this.screen.chooser = var1;
      }

   }
}
