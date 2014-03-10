package com.mojang.minecraft.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gamemode.CreativeGameMode;
import com.mojang.minecraft.gamemode.GameMode;
import com.mojang.minecraft.gamemode.SurvivalGameMode;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelLoader;

public final class GamemodeScreen extends GuiScreen {

    @Override
    protected final void onButtonClick(Button var1) {
        if (var1.id == 0) {
        }

        if (var1.id == 1) {
            minecraft.gamemode = new SurvivalGameMode(minecraft);
        }
        Level level = null;
        try {
            level = loadLevelFromLoader(minecraft.gamemode);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        minecraft.gamemode.preparePlayer(minecraft.player);
        if (minecraft.level != null) {
            minecraft.gamemode.prepareLevel(minecraft.level);
            minecraft.gamemode.apply(minecraft.level);
        } else {
            minecraft.gamemode.prepareLevel(level);
            minecraft.gamemode.apply(level);
        }
        minecraft.gamemode.apply(minecraft.player);

        minecraft.setCurrentScreen(null);
    }

    public Level loadLevelFromLoader(GameMode gamemode) throws FileNotFoundException, IOException {
        Level var11 = null;
        if (gamemode instanceof CreativeGameMode) {
            if ((var11 = new LevelLoader().load(new File(Minecraft.mcDir, "levelc.cw"),
                    minecraft.player)) != null) {
                minecraft.progressBar.setText("Loading saved map..");
                minecraft.setLevel(var11);
                Minecraft.isSinglePlayer = true;
            }
        } else if (gamemode instanceof SurvivalGameMode) {
            if ((var11 = new LevelLoader().load(new File(Minecraft.mcDir, "levels.cw"),
                    minecraft.player)) != null) {
                minecraft.setLevel(var11);
            }
        }
        return var11;
    }

    @Override
    public final void onOpen() {
        buttons.clear();
        buttons.add(new Button(0, width / 2 - 100, height / 4 + 72, "Creative"));
        buttons.add(new Button(1, width / 2 - 100, height / 4 + 96, "Survival"));
    }

    @Override
    public final void render(int var1, int var2) {
        drawFadingBox(0, 0, width, height, 1615855616, -1602211792);
        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        drawCenteredString(fontRenderer, "Select your game mode", width / 2 / 2, 30, 16777215);
        GL11.glPopMatrix();
        super.render(var1, var2);
    }
}
