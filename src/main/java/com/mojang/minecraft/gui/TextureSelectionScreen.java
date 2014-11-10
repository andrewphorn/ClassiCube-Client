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
    private static final int BUTTON_LOAD_FILE = 5, BUTTON_CANCEL = 6, BUTTON_DEFAULT = 7,
            BUTTON_PREVIOUS = 8, BUTTON_NEXT = 9;
    private static final int PACKS_PER_PAGE = 5;
    private static int page = 0;
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
        if (!frozen && button.active && button.visible) {
            switch (button.id) {
                default:
                    this.openTexture(texturePacks.get(button.id + page).location);
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
                        LogUtil.logInfo("Resetting texture pack to default.");
                        // Save preference
                        minecraft.settings.lastUsedTexturePack = null;
                        minecraft.settings.save();

                        // Reset the texture pack
                        minecraft.textureManager.reloadTexturePack();

                        // Return back to the main menu
                        minecraft.setCurrentScreen(parent);

                    } catch (IOException ex) {
                        // If the default texture could not be loaded, something went seriously wrong
                        LogUtil.logError("Error loading default texture pack.", ex);
                        minecraft.setCurrentScreen(new ErrorScreen("Client error", "The game broke! [" + ex + "]"));
                    }
                    break;
                case BUTTON_PREVIOUS:
                    setOffset(page - PACKS_PER_PAGE);
                    break;
                case BUTTON_NEXT:
                    setOffset(page + PACKS_PER_PAGE);
                    break;
            }
        }
    }

    @Override
    public void onOpen() {
        // Index available texture packs
        texturePacks = indexTexturePacks();
        for (int i = 0; i < PACKS_PER_PAGE; i++) {
            buttons.add(new Button(i, width / 2 - 100, height / 6 + i * 24, ""));
        }

        buttons.add(new Button(BUTTON_LOAD_FILE, width / 2 - 100, height / 6 + 120 + 12, "Load file..."));
        buttons.add(new Button(BUTTON_CANCEL, width / 2 - 100, height / 6 + 154 + 22, "Cancel"));
        buttons.add(new Button(BUTTON_DEFAULT, width / 2 - 100, height / 6 + 154, "Default Texture"));
        int pwidth = fontRenderer.getWidth("Previous");
        buttons.add(new Button(BUTTON_PREVIOUS, width / 2 - (112 + pwidth), height / 6 + 48, pwidth + 6, "Previous"));
        buttons.add(new Button(BUTTON_NEXT, width / 2 + 106, height / 6 + 48, pwidth + 6, "Next"));

        setOffset(0);
    }

    void setOffset(int offset) {
        for (int i = 0; i < PACKS_PER_PAGE; i++) {
            Button button = buttons.get(i);
            if (i + offset < texturePacks.size()) {
                TexturePackData pack = texturePacks.get(i + offset);
                button.text = pack.name;
                button.visible = true;
            } else {
                button.visible = false;
            }
        }
        buttons.get(BUTTON_PREVIOUS).visible = (offset > 0);
        buttons.get(BUTTON_NEXT).visible = (offset + PACKS_PER_PAGE < texturePacks.size());
        page = offset;
    }

    protected void openTexture(String file) {
        LogUtil.logInfo("Loading texture pack from " + file);
        try {
            minecraft.textureManager.loadTexturePack(file);
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
                    1 + page, Math.min(5 + page, texturePacks.size()), texturePacks.size());
            drawCenteredString(fontRenderer, morePacksText, width / 2, height / 6 + 120, 16777215);

            if (status != null) {
                // Show an error message if packs could not be indexed
                drawCenteredString(fontRenderer, status, width / 2, height / 2 - 4, 16777215);
            }

            // Render buttons
            super.render(mouseX, mouseY);

            // Show an indicator (asterisk) for the currently-selected texture pack
            if (minecraft.settings.lastUsedTexturePack != null && texturePacks != null) {
                for (int i = 0; i < PACKS_PER_PAGE; ++i) {
                    if (page + i < texturePacks.size()) {
                        if (minecraft.settings.lastUsedTexturePack.equals(texturePacks.get(page + i).location)) {
                            drawString(fontRenderer, ACTIVE_PACK_INDICATOR,
                                    width / 2 + 100 - fontRenderer.getWidth(ACTIVE_PACK_INDICATOR) - 2,
                                    height / 6 + i * 24 + 2, 16777215);
                            break;
                        }
                    }
                }
            } else {
                // Default texture pack selected.
                drawString(fontRenderer, ACTIVE_PACK_INDICATOR,
                        width / 2 + 100 - fontRenderer.getWidth(ACTIVE_PACK_INDICATOR) - 2,
                        height / 6 + 154 + 2, 16777215);
            }
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
