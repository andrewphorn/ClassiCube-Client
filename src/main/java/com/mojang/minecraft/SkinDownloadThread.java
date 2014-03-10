package com.mojang.minecraft;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import com.mojang.minecraft.player.Player;
import com.oyasunadev.mcraft.client.util.Constants;

import java.awt.image.BufferedImage;

public class SkinDownloadThread extends Thread {
    String skinServer;

    private Minecraft minecraft;

    public SkinDownloadThread(Minecraft minecraft, String skinServer) {
        super();
        this.skinServer = skinServer;
        this.minecraft = minecraft;
    }

    @Override
    public void run() {
        if (minecraft.session != null) {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) new URL(skinServer + minecraft.session.username + ".png").openConnection();
                connection.addRequestProperty("User-Agent", Constants.USER_AGENT);
                connection.setDoInput(true);
                connection.setDoOutput(false);
                connection.setUseCaches(false);
                connection.connect();

                if (connection.getResponseCode() != 404) {
                    BufferedImage image = ImageIO.read(connection.getInputStream());
                    if (image.getHeight() == image.getWidth()) {
                        Player.newTexture = image.getSubimage(0, 0, image.getWidth(), image.getHeight() / 2);
                    } else {
                        Player.newTexture = image.getSubimage(0, 0, image.getWidth(), image.getHeight());
                    }
                }
            } catch (Exception ex) {
                LogUtil.logWarning("Error downloading skin.", ex);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

        }
    }
}
