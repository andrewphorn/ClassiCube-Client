package com.mojang.minecraft.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.mojang.minecraft.LogUtil;
import com.mojang.minecraft.Minecraft;

public class TextureSelectionScreen extends GuiScreen implements Runnable {

    protected GuiScreen parent;
    protected String title = "Load texture";
    protected boolean saving = false;
    protected File selectedFile;
    boolean frozen = false;
    JFileChooser chooser;
    private boolean finished = false;
    private boolean loaded = false;
    private ArrayList<TexturePackData> textures = new ArrayList<>();
    private String status = "";

    public TextureSelectionScreen(GuiScreen var1) {
        parent = var1;
    }

    @Override
    protected final void onButtonClick(Button button) {
        if (!frozen && button.active) {
            if (loaded && button.id < 5) {
                this.openTexture(textures.get(button.id));
            }

            if (loaded && button.id == 6) {
                frozen = true;
                TextureDialog dialog;
                (dialog = new TextureDialog(this, minecraft)).setDaemon(true);
                SwingUtilities.invokeLater(dialog);
            }

            if (finished || loaded && button.id == 7) {
                minecraft.setCurrentScreen(parent);
            }
            if (button.id == 8) {
                minecraft.textureManager.resetAllMods();
                minecraft.textureManager.load("/terrain.png");
                minecraft.textureManager.initAtlas();
                minecraft.setCurrentScreen(null);
                minecraft.grabMouse();
                minecraft.textureManager.textures.clear();
                try {
                    minecraft.fontRenderer = new FontRenderer(minecraft.settings, "/default.png",
                            minecraft.textureManager);
                } catch (IOException ex) {
                    LogUtil.logError("Error creating default font renderer.", ex);
                }
                minecraft.settings.lastUsedTexturePack = null;
                minecraft.settings.save();
                minecraft.textureManager.registerAnimations();
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
        new Thread(this).start();

        for (int i = 0; i < 5; ++i) {
            buttons.add(new Button(i, width / 2 - 100, height / 6 + i * 24, "---"));
            buttons.get(i).visible = false;
            buttons.get(i).active = false;
        }

        buttons.add(new Button(6, width / 2 - 100, height / 6 + 120 + 12, "Load file..."));
        buttons.add(new Button(7, width / 2 - 100, height / 6 + 154 + 22, "Cancel"));
        buttons.add(new Button(8, width / 2 - 100, height / 6 + 154, "Default Texture"));
    }

    protected void openTexture(String file) {
        try {
            minecraft.textureManager.loadTexturePack(file);
            minecraft.fontRenderer = new FontRenderer(minecraft.settings, "/default.png",
                    minecraft.textureManager);
            minecraft.settings.lastUsedTexturePack = file;
            minecraft.settings.save();
        } catch (IOException ex) {
            LogUtil.logError("Error loading texture pack from " + file, ex);
        }
        minecraft.setCurrentScreen(null);
        minecraft.grabMouse();
    }

    protected void openTexture(TexturePackData data) {
        selectedFile = new File(data.location);
        openTexture(data.location);
        minecraft.setCurrentScreen(parent);
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
                LogUtil.logError("Error waiting to render TextureSelectionScreen", ex);
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
                    LogUtil.logError("Error waiting to run TextureSelectionScreen", ex);
                }
            }

            status = "Getting texture list..";
            TexturePackData data;
            for (String file : new File(Minecraft.getMinecraftDirectory() + "/texturepacks").list()) {
                if (!file.endsWith(".zip")) {
                    continue;
                }
                data = new TexturePackData(file, file.substring(0, file.indexOf(".")));
                textures.add(data);
            }
            if (textures.size() >= 1) {
                setTextures(textures);
                loaded = true;
                return;
            }

            status = "Finished loading textures";
            finished = true;
        } catch (Exception ex) {
            LogUtil.logError("Error running TextureSelectionScreen", ex);
            status = "Failed to load textures";
            finished = true;
        }

    }

    protected void setTextures(ArrayList<TexturePackData> texturePacks) {
        for (int i = 0; i < Math.min(texturePacks.size(), 5); ++i) {
            buttons.get(i).active = !texturePacks.get(i).equals("-");
            buttons.get(i).text = texturePacks.get(i).name;
            buttons.get(i).visible = true;
        }
    }

    @Override
    public final void tick() {
        super.tick();
        if (selectedFile != null) {
            selectedFile = null;
        }
    }
}
