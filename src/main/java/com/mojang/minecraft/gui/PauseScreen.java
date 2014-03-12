package com.mojang.minecraft.gui;

import com.mojang.minecraft.LogUtil;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.ProgressBarDisplay;
import com.oyasunadev.mcraft.client.core.ClassiCubeStandalone;

public final class PauseScreen extends GuiScreen {

    int greenColor = 8454016;

    int orangeColor = 16750160;

    int redColor = 16737380;
    String VersionString = "0.13";

    @Override
    protected final void onButtonClick(Button button) {
        if (minecraft.session != null) {
            if (button.id == 0) {
                minecraft.setCurrentScreen(new OptionsScreen(minecraft.settings));
            }
            if (button.id == 1) {
                minecraft.setCurrentScreen(new SaveLevelScreen(this));
            }
            if (button.id == 2) {
                minecraft.setCurrentScreen(new TextureSelectionScreen(this));
            }
            if (button.id == 3) {
                minecraft.setCurrentScreen((GuiScreen) null);
                minecraft.grabMouse();
            }
            if (button.id == 4) {
                File file = new File(Minecraft.getMinecraftDirectory(), "/Screenshots/");
                file.mkdirs();
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    // TODO: add dialog for fallback
                    LogUtil.logError("Error opening screenshots folder.", ex);
                }
            }
            if (button.id == 5) {
                File file = new File(Minecraft.getMinecraftDirectory(), "/logs/");
                file.mkdirs();
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    // TODO: add dialog for fallback
                    LogUtil.logError("Error opening chat logs folder.", ex);
                }
            }
        } else {
            if (button.id == 0) {
                minecraft.setCurrentScreen(new OptionsScreen(minecraft.settings));
            }
            if (button.id == 1) {
                minecraft.setCurrentScreen(new GenerateLevelScreen(this));
            }
            if (button.id == 2) {
                minecraft.setCurrentScreen(new SaveLevelScreen(this));
            }
            if (button.id == 3) {
                minecraft.setCurrentScreen(new LoadLevelScreen(this));
            }
            if (button.id == 4) {
                minecraft.setCurrentScreen(new TextureSelectionScreen(this));
            }
            if (button.id == 5) {
                minecraft.setCurrentScreen((GuiScreen) null);
                minecraft.grabMouse();
            }
            if (button.id == 6) {
                File file = new File(Minecraft.getMinecraftDirectory(), "/Screenshots/");
                file.mkdirs();
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    // TODO: add dialog for fallback
                    LogUtil.logError("Error opening screenshots folder.", ex);
                }
            }
            if (button.id == 7) {
                File file = new File(Minecraft.getMinecraftDirectory(), "/logs/");
                file.mkdirs();
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    // TODO: add dialog for fallback
                    LogUtil.logError("Error opening chat logs folder.", ex);
                }
            }
        }
    }

    @Override
    public final void onOpen() {
        buttons.clear();
        if (minecraft.session != null) {
            buttons.add(new Button(0, width / 2 - 100, height / 4, "Options..."));
            buttons.add(new Button(1, width / 2 - 100, height / 4 + 24, "Save level..."));
            buttons.add(new Button(2, width / 2 - 100, height / 4 + 48, "Change texture pack..."));
            buttons.add(new Button(3, width / 2 - 100, height - 36, "Back to game"));
            int w = fontRenderer.getWidth("Screenshots...");
            buttons.add(new Button(4, width - fontRenderer.getWidth("Screenshots...") - 15,
                    height - 36, fontRenderer.getWidth("Screenshots..."), "Screenshots"));
            buttons.add(new Button(5, width - w - 15, height - 58, w, "Chat Logs"));
        } else {
            buttons.add(new Button(0, width / 2 - 100, height / 4, "Options..."));
            buttons.add(new Button(1, width / 2 - 100, height / 4 + 24, "Generate new level..."));
            buttons.add(new Button(2, width / 2 - 100, height / 4 + 48, "Save level..."));
            buttons.add(new Button(3, width / 2 - 100, height / 4 + 72, "Load level..."));
            buttons.add(new Button(4, width / 2 - 100, height / 4 + 96, "Change texture pack..."));
            buttons.add(new Button(5, width / 2 - 100, height - 36, "Back to game"));
            int w = fontRenderer.getWidth("Screenshots...");
            buttons.add(new Button(6, width - fontRenderer.getWidth("Screenshots...") - 15,
                    height - 36, fontRenderer.getWidth("Screenshots..."), "Screenshots"));
            buttons.add(new Button(7, width - w - 15, height - 58, w, "Chat Logs"));
        }
    }

    @Override
    public final void render(int var1, int var2) {
        String titlePrint = ProgressBarDisplay.title;
        if (minecraft.session == null) {
            titlePrint = "Singleplayer";
        }

        drawFadingBox(0, 0, width, height, 1610941696, -1607454624);
        drawCenteredString(fontRenderer, "Game menu", width / 2, 40, 16777215);
        drawString(fontRenderer, titlePrint, width - fontRenderer.getWidth(titlePrint) - 15, 2,
                16777215);
        drawString(fontRenderer, "ClassiCube " + VersionString,
                width - fontRenderer.getWidth("ClassiCube " + VersionString) - 15, 13, 14474460);

        double cpuUsage = minecraft.monitoringThread.getAvarageUsagePerCPU();
        double roundedCpuUsage = Math.round(cpuUsage * 100.0) / 100.0;

        int colorToUse = greenColor;
        if (cpuUsage >= 21) {
            colorToUse = orangeColor;
        } else if (cpuUsage >= 32) {
            colorToUse = redColor;
        } else if (cpuUsage <= 20) {
            colorToUse = greenColor;
        }

        String s = "Average CPU: " + roundedCpuUsage + "%";
        drawString(fontRenderer, s, width - fontRenderer.getWidth(s) - 15, 24, colorToUse);

        long dMem = minecraft.monitoringThread.totalMemory - minecraft.monitoringThread.freeMemory;
        float percent = dMem * 100L / minecraft.monitoringThread.maxMemory;
        if (percent >= 75) {
            colorToUse = redColor;
        } else if (percent >= 50) {
            colorToUse = orangeColor;
        } else {
            colorToUse = greenColor;
        }

        String Usage = "Used memory: " + percent + "% (" + dMem / 1024L / 1024L + "MB)";
        drawString(fontRenderer, Usage, width - fontRenderer.getWidth(Usage) - 15, 35, colorToUse);
        String max = "Allocated memory: " + minecraft.monitoringThread.maxMemory / 1024L / 1024L
                + "MB";
        drawString(fontRenderer, max, width - fontRenderer.getWidth(max) - 15, 46, 15132260);
        super.render(var1, var2);
    }

}
