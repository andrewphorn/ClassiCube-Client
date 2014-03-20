package com.mojang.minecraft.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

final class LevelDialog extends Thread {

    // $FF: synthetic field
    private LoadLevelScreen screen;

    LevelDialog(LoadLevelScreen screen) {
        super();
        this.screen = screen;
    }

    @Override
    public final void run() {
        JFileChooser fileChooser;
        try {
            LoadLevelScreen llScreen = screen;
            fileChooser = new JFileChooser();
            llScreen.chooser = fileChooser;
            FileNameExtensionFilter filter = new FileNameExtensionFilter("ClassicWorld format (.cw)",
                    "cw");
            screen.chooser.setFileFilter(filter);
            screen.chooser.setMultiSelectionEnabled(false);
            int chosenID;
            if (screen.saving) {
                chosenID = screen.chooser.showSaveDialog(screen.minecraft.canvas);
            } else {
                chosenID = screen.chooser.showOpenDialog(screen.minecraft.canvas);
            }

            if (chosenID == 0) {
                screen.selectedFile = screen.chooser.getSelectedFile();
                screen.selectedFile = new File(screen.selectedFile + "");
            }
        } finally {
            screen.frozen = false;
            fileChooser = null;
            screen.chooser = fileChooser;
        }
    }
}
