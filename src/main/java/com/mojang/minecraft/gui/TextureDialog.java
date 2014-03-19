package com.mojang.minecraft.gui;

import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mojang.minecraft.LogUtil;
import com.mojang.minecraft.Minecraft;

final class TextureDialog extends Thread {

    private TextureSelectionScreen screen;
    private Minecraft mc;

    TextureDialog(TextureSelectionScreen screen, Minecraft minecraft) {
        super();
        this.screen = screen;
        mc = minecraft;
    }

    protected void openTexture(String file) {
        try {
            mc.textureManager.loadTexturePack(file);
        } catch (IOException ex) {
            LogUtil.logError("Error loading texture pack from " + file, ex);
        }
        mc.setCurrentScreen(null);
        mc.grabMouse();
    }

    @Override
    public final void run() {
        JFileChooser fileChooser;
        try {
            fileChooser = new JFileChooser();
            screen.chooser = fileChooser;
            FileNameExtensionFilter var3 = new FileNameExtensionFilter(".Zip Texture Packs",
                    "zip");
            screen.chooser.setFileFilter(var3);
            screen.chooser.setMultiSelectionEnabled(false);
            int var7;
            if (screen.saving) {
                var7 = screen.chooser.showSaveDialog(screen.minecraft.canvas);
            } else {
                var7 = screen.chooser.showOpenDialog(screen.minecraft.canvas);
            }

            if (var7 == 0) {
                screen.selectedFile = screen.chooser.getSelectedFile();
                openTexture(screen.chooser.getSelectedFile().getName());

            }
        } finally {
            screen.frozen = false;
            fileChooser = null;
            screen.chooser = fileChooser;
        }

    }
}
