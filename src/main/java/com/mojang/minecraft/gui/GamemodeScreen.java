package com.mojang.minecraft.gui;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gamemode.CreativeGameMode;
import com.mojang.minecraft.gamemode.GameMode;
import com.mojang.minecraft.gamemode.SurvivalGameMode;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelLoader;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;

public final class GameModeScreen extends GuiScreen {

    @Override
    protected final void onButtonClick(Button button) {
        if (button.id == 0) {
            // TODO?
        }

        if (button.id == 1) {
            minecraft.gamemode = new SurvivalGameMode(minecraft);
        }
        Level level = null;
        try {
            level = loadLevelFromLoader(minecraft.gamemode);
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

    // Removed CreativeMode and SurvivalMode distinguish code because it seems unnecessary
    public Level loadLevelFromLoader(GameMode gamemode) throws IOException {
        Level var11 = new LevelLoader().load(new File(Minecraft.mcDir, "levelc.cw"), minecraft.player);
        if (var11 != null) {
            minecraft.progressBar.setText("Loading saved map..");
            minecraft.setLevel(var11);
            Minecraft.isSinglePlayer = true;
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
