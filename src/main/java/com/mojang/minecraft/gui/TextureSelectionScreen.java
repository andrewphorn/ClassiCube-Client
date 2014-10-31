package com.mojang.minecraft.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.render.texture.Textures;
import com.mojang.util.LogUtil;

public final class TextureSelectionScreen extends GuiScreen {

    private static final String TITLE = "Texture Packs";
    private static final int BUTTON_LOAD_FILE = 6, BUTTON_CANCEL = 7, BUTTON_DEFAULT = 8,
            BUTTON_PREVIOUS = 9, BUTTON_NEXT = 10;
    private static final int MAX_PACKS_TO_SHOW = 5;
    private static int PAGE = 0;
    private static final String ACTIVE_PACK_INDICATOR = "*";

    private final GuiScreen parent;
    private List<TexturePackData> texturePacks;
    boolean frozen = false;
    private String status = "";

    public TextureSelectionScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    protected final void onButtonClick(Button button) {
        if (!frozen && button.active) {
            switch (button.id) {
                default:
                    this.openTexture(texturePacks.get(button.id + PAGE).location);
                    break;
                case BUTTON_LOAD_FILE:
                    frozen = true;
                    SwingUtilities.invokeLater(new ChooseFileRunnable());
                    break;
                case BUTTON_CANCEL:
                    minecraft.setCurrentScreen(parent);
                    break;
                case BUTTON_DEFAULT:
                    try {
                        // Save preference
                        minecraft.settings.lastUsedTexturePack = null;
                        minecraft.settings.save();
                        
                        // Reset the texture pack
                        minecraft.textureManager.resetAllMods();
                        minecraft.textureManager.load(Textures.TERRAIN);
                        minecraft.textureManager.initAtlas();
                        minecraft.fontRenderer = new FontRenderer(minecraft.settings,
                                "/default.png", minecraft.textureManager);
                        minecraft.textureManager.registerAnimations();
                        
                        // Return back to the main menu
                        minecraft.setCurrentScreen(parent);
                        
                    } catch (IOException ex) {
                        // If the default texture could not be loaded, something went seriously wrong
                        LogUtil.logError("Error loading default texture pack.", ex);
                        minecraft.setCurrentScreen(new ErrorScreen("Client error", "The game broke! [" + ex + "]"));
                    }
                    break;
                case BUTTON_PREVIOUS:
                    if (PAGE >= 5) {
                        PAGE = PAGE - 5;
                    }
                    break;
                case BUTTON_NEXT:
                    int packCount = Math.min(texturePacks.size(), MAX_PACKS_TO_SHOW + (PAGE * 5));
                    if (PAGE <= packCount / 5) {
                        PAGE = PAGE + 5;
                    }
                    break;
            }
        }
    }

    @Override
    public void onOpen() {
        // Index available texture packs
        texturePacks = indexTexturePacks();
        int packCount = Math.min(texturePacks.size(), MAX_PACKS_TO_SHOW);
        for (int i = 0; i < packCount; i++) {
            buttons.add(new Button(i, width / 2 - 100, height / 6 + i * 24, ""));
            buttons.get(i).text = texturePacks.get(i + PAGE).name;
        }

        buttons.add(new Button(BUTTON_LOAD_FILE, width / 2 - 100, height / 6 + 120 + 12, "Load file..."));
        buttons.add(new Button(BUTTON_CANCEL, width / 2 - 100, height / 6 + 154 + 22, "Cancel"));
        buttons.add(new Button(BUTTON_DEFAULT, width / 2 - 100, height / 6 + 154, "Default Texture"));
        int pwidth = fontRenderer.getWidth("Previous");
        buttons.add(new Button(BUTTON_PREVIOUS, width / 2 - (112 + pwidth), height / 6 + 48, pwidth + 6, "Previous"));
        buttons.add(new Button(BUTTON_NEXT, width / 2 + 106, height / 6 + 48, pwidth + 6, "Next"));
    }

    protected void openTexture(String file) {
        try {
            minecraft.textureManager.loadTexturePack(file);
            minecraft.fontRenderer = new FontRenderer(minecraft.settings, "/default.png",
                    minecraft.textureManager);
            minecraft.settings.lastUsedTexturePack = file;
            minecraft.settings.save();
            minecraft.setCurrentScreen(parent);
        } catch (IOException ex) {
            status = "Texture pack could not be loaded!";
            LogUtil.logError("Error loading texture pack from " + file, ex);
        }
    }

    @Override
    public void render(int mouseX, int mouseY) {
        drawFadingBox(0, 0, width, height, 1610941696, -1607454624);
        drawCenteredString(fontRenderer, TITLE, width / 2, 20, 16777215);
        if (frozen) {
            drawCenteredString(fontRenderer, "Selecting file...",
                    width / 2 - 100, height / 6 + 120 + 12, 16777215);
        } else {
            String morePacksText = String.format("%s-%s out of %s",
                    1 + PAGE, 5 + PAGE, texturePacks.size());
            drawCenteredString(fontRenderer, morePacksText, width / 2, height / 6 + 120, 16777215);

            if (status != null) {
                // Show an error message if packs could not be indexed
                drawCenteredString(fontRenderer, status, width / 2, height / 2 - 4, 16777215);
            }

            // Render buttons
            super.render(mouseX, mouseY);

            // Show an indicator (asterisk) for the currently-selected texture pack
            if (minecraft.settings.lastUsedTexturePack != null) {
                if (texturePacks != null) {
                    for (int i = 0; i < Math.min(texturePacks.size(), MAX_PACKS_TO_SHOW); ++i) {
                        if (minecraft.settings.lastUsedTexturePack != null
                                && minecraft.settings.lastUsedTexturePack.equals(texturePacks.get(i + PAGE).location)) {
                            drawString(fontRenderer, ACTIVE_PACK_INDICATOR,
                                    width / 2 + 100 - fontRenderer.getWidth(ACTIVE_PACK_INDICATOR) - 2,
                                    height / 6 + i * 24 + 2, 16777215);
                            break;
                        }
                    }
                }
            } else {
                drawString(fontRenderer, ACTIVE_PACK_INDICATOR,
                        width / 2 + 100 - fontRenderer.getWidth(ACTIVE_PACK_INDICATOR) - 2,
                        height / 6 + 154 + 2, 16777215);
            }
        }
        // Update texturepack names based on the page
        int packCount = Math.min(texturePacks.size(), MAX_PACKS_TO_SHOW);
        for (int i = 0; i < packCount; i++) {
            buttons.get(i).text = texturePacks.get(i + PAGE).name;
        }
    }

    // Index available texture packs under "texturepacks" folder.
    private List<TexturePackData> indexTexturePacks() {
        List<TexturePackData> loadedPacks = new ArrayList<>();
        try {
            TexturePackData data;
            String[] files = new File(Minecraft.getMinecraftDirectory() + "/texturepacks").list();
            for (String fileName : files) {
                if (!fileName.toLowerCase().endsWith(".zip")) {
                    continue;
                }
                try {
                    data = new TexturePackData(fileName, fileName.substring(0, fileName.indexOf(".")));
                    loadedPacks.add(data);
                } catch (Exception ex) {
                    LogUtil.logError("Error loading texture pack from " + fileName, ex);
                }
            }
        } catch (Exception ex) {
            LogUtil.logError("Error loading texture packs", ex);
        }
        return loadedPacks;
    }

    // Method that shows the "load texture pack" dialog.
    // This needs to be called asynchronously via SwingUtilities.invokeLater to avoid locking up.
    private final class ChooseFileRunnable implements Runnable {

        @Override
        public void run() {
            try {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter zipFilter
                        = new FileNameExtensionFilter("Texture Packs (.zip)", "zip");
                chooser.setFileFilter(zipFilter);
                chooser.setMultiSelectionEnabled(false);
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    // User has selected a file. Attempt to load it.
                    openTexture(chooser.getSelectedFile().getAbsolutePath());
                }
            } catch (Exception ex) {
                LogUtil.logError("Error selecting texture pack.", ex);
            } finally {
                frozen = false;
            }
        }
    }
}
