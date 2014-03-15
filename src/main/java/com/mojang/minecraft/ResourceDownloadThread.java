package com.mojang.minecraft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceDownloadThread extends Thread {

    private static final String[] resourceFiles = new String[] { "music/calm1.ogg",
            "music/calm2.ogg", "music/calm3.ogg", "newmusic/hal1.ogg", "newmusic/hal2.ogg",
            "newmusic/hal3.ogg", "newmusic/hal4.ogg", "newsound/step/grass1.ogg",
            "newsound/step/grass2.ogg", "newsound/step/grass3.ogg", "newsound/step/grass4.ogg",
            "newsound/step/gravel1.ogg", "newsound/step/gravel2.ogg", "newsound/step/gravel3.ogg",
            "newsound/step/gravel4.ogg", "newsound/step/stone1.ogg", "newsound/step/stone2.ogg",
            "newsound/step/stone3.ogg", "newsound/step/stone4.ogg", "newsound/step/wood1.ogg",
            "newsound/step/wood2.ogg", "newsound/step/wood3.ogg", "newsound/step/wood4.ogg",
            "newsound/step/cloth1.ogg", "newsound/step/cloth2.ogg", "newsound/step/cloth3.ogg",
            "newsound/step/cloth4.ogg", "newsound/step/sand1.ogg", "newsound/step/sand2.ogg",
            "newsound/step/sand3.ogg", "newsound/step/sand4.ogg", "newsound/step/snow1.ogg",
            "newsound/step/snow2.ogg", "newsound/step/snow3.ogg", "newsound/step/snow4.ogg",
            "sound3/dig/grass1.ogg", "sound3/dig/grass2.ogg", "sound3/dig/grass3.ogg",
            "sound3/dig/grass4.ogg", "sound3/dig/gravel1.ogg", "sound3/dig/gravel2.ogg",
            "sound3/dig/gravel3.ogg", "sound3/dig/gravel4.ogg", "sound3/dig/stone1.ogg",
            "sound3/dig/stone2.ogg", "sound3/dig/stone3.ogg", "sound3/dig/stone4.ogg",
            "sound3/dig/wood1.ogg", "sound3/dig/wood2.ogg", "sound3/dig/wood3.ogg",
            "sound3/dig/wood4.ogg", "sound3/dig/cloth1.ogg", "sound3/dig/cloth2.ogg",
            "sound3/dig/cloth3.ogg", "sound3/dig/cloth4.ogg", "sound3/dig/sand1.ogg",
            "sound3/dig/sand2.ogg", "sound3/dig/sand3.ogg", "sound3/dig/sand4.ogg",
            "sound3/dig/snow1.ogg", "sound3/dig/snow2.ogg", "sound3/dig/snow3.ogg",
            "sound3/dig/snow4.ogg", "sound3/random/glass1.ogg", "sound3/random/glass2.ogg",
            "sound3/random/glass3.ogg" };

    private final File dir;
    private final Minecraft minecraft;
    private boolean finished = false;
    public static boolean done = false;
    boolean running = false;

    public ResourceDownloadThread(File minecraftFolder, Minecraft minecraft) {
        this.minecraft = minecraft;

        setName("Resource download thread");
        setDaemon(true);

        dir = new File(minecraftFolder, "resources/");

        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + dir);
        }
    }

    public boolean isFinished() {
        return finished;
    }

    @Override
    public void run() {
        File musicFolder = new File(dir, "music");
        File stepsFolder = new File(new File(dir, "newsound"), "step");
        File digFolder = new File(new File(dir, "sound3"), "dig");
        File randomFolder = new File(new File(dir, "sound3"), "random");
        File newMusicFolder = new File(dir, "newmusic");

        try {
            GameSettings.PercentString = "5%";
            GameSettings.StatusString = "Downloading music and sounds...";
            LogUtil.logInfo("Downloading music and sounds...");

            int percent = 5;
            for (String fileName : resourceFiles) {
                if (percent >= 80) {
                    percent = 80;
                }
                percent += 3;
                File file = new File(dir, fileName);
                if (!file.exists()) {
                    GameSettings.PercentString = percent + "%";
                    GameSettings.StatusString = "Downloading https://s3.amazonaws.com/MinecraftResources/"
                            + fileName + "...";
                    LogUtil.logInfo("Downloading https://s3.amazonaws.com/MinecraftResources/"
                            + fileName);
                    URL url = new URL("https://s3.amazonaws.com/MinecraftResources/" + fileName);
                    try (InputStream is = url.openStream()) {
                        StreamingUtil.copyStreamToFile(is, file);
                    }

                    GameSettings.StatusString = "Downloaded https://s3.amazonaws.com/MinecraftResources/"
                            + fileName + "!";
                    LogUtil.logInfo("Downloaded https://s3.amazonaws.com/MinecraftResources/"
                            + fileName);
                }
            }
            GameSettings.PercentString = "85%";
            GameSettings.StatusString = "Downloaded music and sounds!";
            LogUtil.logInfo("Done downloading music and sounds!");
            GameSettings.StatusString = "";
            GameSettings.PercentString = "";
            done = true;
        } catch (Exception ex) {
            LogUtil.logError("Error downloading music and sounds!", ex);
        }

        for (int i = 1; i <= 3; i++) {
            minecraft.sound.registerMusic("calm" + i + ".ogg", new File(musicFolder, "calm" + i
                    + ".ogg"));
            minecraft.sound.registerSound(new File(randomFolder, "glass" + i + ".ogg"),
                    "random/glass" + i + ".ogg");
        }

        for (int i = 1; i <= 4; i++) {
            minecraft.sound.registerMusic("calm" + i + ".ogg", new File(newMusicFolder, "hal" + i
                    + ".ogg"));
            minecraft.sound.registerSound(new File(stepsFolder, "grass" + i + ".ogg"), "step/grass"
                    + i + ".ogg");
            minecraft.sound.registerSound(new File(stepsFolder, "gravel" + i + ".ogg"),
                    "step/gravel" + i + ".ogg");
            minecraft.sound.registerSound(new File(stepsFolder, "stone" + i + ".ogg"), "step/stone"
                    + i + ".ogg");
            minecraft.sound.registerSound(new File(stepsFolder, "wood" + i + ".ogg"), "step/wood"
                    + i + ".ogg");
            minecraft.sound.registerSound(new File(stepsFolder, "cloth" + i + ".ogg"), "step/cloth"
                    + i + ".ogg");
            minecraft.sound.registerSound(new File(stepsFolder, "sand" + i + ".ogg"), "step/sand"
                    + i + ".ogg");
            minecraft.sound.registerSound(new File(stepsFolder, "snow" + i + ".ogg"), "step/snow"
                    + i + ".ogg");
            minecraft.sound.registerSound(new File(digFolder, "grass" + i + ".ogg"), "dig/grass"
                    + i + ".ogg");
            minecraft.sound.registerSound(new File(digFolder, "gravel" + i + ".ogg"), "dig/gravel"
                    + i + ".ogg");
            minecraft.sound.registerSound(new File(digFolder, "stone" + i + ".ogg"), "dig/stone"
                    + i + ".ogg");
            minecraft.sound.registerSound(new File(digFolder, "wood" + i + ".ogg"), "dig/wood" + i
                    + ".ogg");
            minecraft.sound.registerSound(new File(digFolder, "cloth" + i + ".ogg"), "dig/cloth"
                    + i + ".ogg");
            minecraft.sound.registerSound(new File(digFolder, "sand" + i + ".ogg"), "dig/sand" + i
                    + ".ogg");
            minecraft.sound.registerSound(new File(digFolder, "snow" + i + ".ogg"), "dig/snow" + i
                    + ".ogg");
        }

        finished = true;
    }
}
