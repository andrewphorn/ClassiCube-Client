package com.mojang.minecraft.gui;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.mojang.util.LogUtil;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelLoader;

public class LoadLevelScreen extends GuiScreen implements Runnable {

    protected GuiScreen parent;
    protected String title = "Load level";
    protected boolean saving = false;
    protected File selectedFile;
    boolean frozen = false;
    JFileChooser chooser;
    private boolean finished = false;
    private boolean loaded = false;
    private String[] levels = null;
    private String status = "";

    public LoadLevelScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    protected final void onButtonClick(Button button) {
        if (!frozen) {
            if (button.active) {
                if (loaded && button.id < 5) {
                    this.openLevel(button.id);
                }

                // if (finished || loaded && var1.id == 5) {
                // frozen = true;
                // LevelDialog var2;
                // (var2 = new LevelDialog(this)).setDaemon(true);
                // SwingUtilities.invokeLater(var2);
                // }
                if (finished || loaded && button.id == 6) {
                    minecraft.setCurrentScreen(parent);
                }
            }
        }
    }

    @Override
    public final void onClose() {
        super.onClose();
        if (chooser != null) {
            chooser.cancelSelection();
        }

    }

    @Override
    public void onOpen() {
        new Thread(this, "LoadLevelScreen").start();

        for (int i = 0; i < 5; ++i) {
            buttons.add(new Button(i, width / 2 - 100, height / 6 + i * 24, "---"));
            buttons.get(i).visible = false;
            buttons.get(i).active = false;
        }

        // buttons.add(new Button(5, width / 2 - 100, height / 6 + 120 + 12,
        // "Load file..."));
        buttons.add(new Button(6, width / 2 - 100, height / 6 + 168, "Cancel"));
        frozen = true;
        LevelDialog dialog = new LevelDialog(this);
        dialog.setDaemon(true);
        SwingUtilities.invokeLater(dialog);
    }

    protected void openLevel(File file) {
        try {
            Level level = new LevelLoader().load(file, this.minecraft.player);
            if (level != null) {
                minecraft.setLevel(level);
            }
        } catch (IOException ex) {
            // TODO Auto-generated catch block
            LogUtil.logError("Error loading level from file.", ex);
        }

        minecraft.setCurrentScreen(parent);
    }

    protected void openLevel(int var1) {
        // this.minecraft.loadOnlineLevel(this.minecraft.session.username,
        // var1);
        minecraft.setCurrentScreen(null);
        minecraft.grabMouse();
    }

    @Override
    public void render(int var1, int var2) {
        drawFadingBox(0, 0, width, height, 1610941696, -1607454624);
        drawCenteredString(fontRenderer, title, width / 2, 20, 16777215);
        if (frozen) {
            drawCenteredString(fontRenderer, "Selecting file..", width / 2, height / 2 - 4,
                    16777215);

            try {
                Thread.sleep(20L);
            } catch (InterruptedException ex) {
                LogUtil.logError("Error waiting to render LoadLevelScreen.", ex);
            }
        } else {
            if (!loaded) {
                drawCenteredString(fontRenderer, status, width / 2, height / 2 - 4, 16777215);
            }

            super.render(var1, var2);
        }
    }

    @Override
    public void run() {
        try {
            if (frozen) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException ex) {
                    LogUtil.logError("Error waiting to run LoadLevelScreen.", ex);
                }
            }
            levels = new String[]{""};
            if (levels.length >= 5) {
                setLevels(levels);
                loaded = true;
                return;
            }

            status = levels[0];
            finished = true;
        } catch (Exception ex) {
            LogUtil.logError("Error while running LoadLevelScreen.", ex);
            status = "Failed to load levels";
            // this.finished = true;
        }

    }

    protected void setLevels(String[] var1) {
        for (int var2 = 0; var2 < 5; ++var2) {
            buttons.get(var2).active = !var1[var2].equals("-");
            buttons.get(var2).text = var1[var2];
            buttons.get(var2).visible = true;
        }

    }

    @Override
    public final void tick() {
        super.tick();
        if (selectedFile != null) {
            this.openLevel(selectedFile);
            selectedFile = null;
        }

    }
}
