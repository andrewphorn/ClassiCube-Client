package com.mojang.minecraft.net;

import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import com.mojang.util.LogUtil;
import com.oyasunadev.mcraft.client.util.Constants;

public class SkinDownloadThread extends Thread {

    String skinServer;

    private NetworkPlayer player;

    public SkinDownloadThread(NetworkPlayer networkPlayer, String skinServer) {
        super();

        player = networkPlayer;
        this.skinServer = skinServer;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;

        try {
            String skinName = (player.SkinName == null ? player.name : player.SkinName);
            URL skinUrl = new URL(skinServer + skinName + ".png");
            connection = (HttpURLConnection) skinUrl.openConnection();
            connection.addRequestProperty("User-Agent", Constants.USER_AGENT);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                // Don't throw errors on 404 errors
                return;
            }

            BufferedImage image = ImageIO.read(connection.getInputStream());
            if (image.getHeight() == image.getWidth()) {
                player.newTexture = image
                        .getSubimage(0, 0, image.getWidth(), image.getHeight() / 2);
            } else {
                player.newTexture = image.getSubimage(0, 0, image.getWidth(), image.getHeight());
            }
        } catch (Exception ex) {
            LogUtil.logWarning("Error downloading a player skin.", ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
