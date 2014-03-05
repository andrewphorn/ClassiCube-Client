package com.mojang.minecraft.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.mojang.minecraft.Minecraft;

public class TextureSelectionScreen extends GuiScreen implements Runnable {

    protected GuiScreen parent;
    private boolean finished = false;
    private boolean loaded = false;
    private ArrayList<TexturePackData> textures = new ArrayList<TexturePackData>();
    private String status = "";
    protected String title = "Load texture";
    boolean frozen = false;
    JFileChooser chooser;
    protected boolean saving = false;
    protected File selectedFile;

    public TextureSelectionScreen(GuiScreen var1) {
        parent = var1;
    }

    @Override
    protected final void onButtonClick(Button var1) {
        if (!frozen && var1.active) {
            if (loaded && var1.id < 5) {
                this.openTexture(textures.get(var1.id));
            }

            if (loaded && var1.id == 6) {
                frozen = true;
                TextureDialog var2;
                (var2 = new TextureDialog(this, minecraft)).setDaemon(true);
                SwingUtilities.invokeLater(var2);
            }

            if (finished || loaded && var1.id == 7) {
                minecraft.setCurrentScreen(parent);
            }
            if (var1.id == 8) {
                minecraft.textureManager.resetAllMods();
                minecraft.textureManager.load("/terrain.png");
                minecraft.textureManager.initAtlas();
                minecraft.setCurrentScreen((GuiScreen) null);
                minecraft.grabMouse();
                minecraft.textureManager.textures.clear();
                // this.minecraft.levelRenderer.refresh();
                try {
                    minecraft.fontRenderer = new FontRenderer(minecraft.settings, "/default.png",
                            minecraft.textureManager);
                } catch (IOException e) {
                    e.printStackTrace();
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

        for (int var1 = 0; var1 < 5; ++var1) {
            buttons.add(new Button(var1, width / 2 - 100, height / 6 + var1 * 24, "---"));
            buttons.get(var1).visible = false;
            buttons.get(var1).active = false;
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        minecraft.setCurrentScreen((GuiScreen) null);
        minecraft.grabMouse();
    }

    protected void openTexture(TexturePackData var1) {
        selectedFile = new File(var1.location);
        openTexture(var1.location);
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
            } catch (InterruptedException var3) {
                var3.printStackTrace();
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
                } catch (InterruptedException var2) {
                    var2.printStackTrace();
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
        } catch (Exception var3) {
            var3.printStackTrace();
            status = "Failed to load textures";
            finished = true;
        }

    }

    protected void setTextures(ArrayList<TexturePackData> var1) {
        for (int var2 = 0; var2 < Math.min(var1.size(), 5); ++var2) {

            buttons.get(var2).active = !var1.get(var2).equals("-");
            buttons.get(var2).text = var1.get(var2).name;
            buttons.get(var2).visible = true;
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
