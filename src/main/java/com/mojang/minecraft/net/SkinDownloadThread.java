package com.mojang.minecraft.net;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import com.mojang.minecraft.mob.Mob;
import com.mojang.util.LogUtil;
import com.oyasunadev.mcraft.client.util.Constants;

public class SkinDownloadThread extends Thread {

    private final String skinServer;
    private final String skinName;
    private final Mob player;

    public SkinDownloadThread(Mob player, String skinName, String skinServer) {
        super();
        this.player = player;
        this.skinName = skinName;
        this.skinServer = skinServer;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        try {
            URL skinUrl = new URL(skinServer + skinName + ".png");
            connection = (HttpURLConnection) skinUrl.openConnection();
            connection.addRequestProperty("User-Agent", Constants.USER_AGENT);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();

            int responseCode = connection.getResponseCode(); // may throw IOException
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND
                    || responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                // Don't throw errors on 404 (file not found) errors.
                // Minecraft.net returns 403 for missing skins, for some reason. Skip those too.
                return;
            }

            BufferedImage image = ImageIO.read(connection.getInputStream());
            if (image.getHeight() == image.getWidth()) {
                player.newTexture = image
                        .getSubimage(0, 0, image.getWidth(), image.getHeight() / 2);
            } else {
                player.newTexture = image.getSubimage(0, 0, image.getWidth(), image.getHeight());
            }

        } catch (IOException ex) {
            // Log connection errors
            if (connection != null) {
                try {
                    // It's possible that an IOException was thrown by getResponseCode()
                    // after a 4xx or 5xx response code was returned by the server.
                    // This appears to be a bug in JRE 7u51 (and possibly other versions).
                    // Calling getResponseCode() again should not throw a second exception, although
                    // this behavior is implementation-dependent, and we don't rely on it.
                    int responseCode = connection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        String logMsg = String.format(
                                "Could not download skin for \"%s\". Server at %s returned code %s",
                                skinName, skinServer, responseCode);
                        LogUtil.logWarning(logMsg);
                        return;
                    }
                } catch (IOException ex2) {
                    // If a secondary exception is raised by getResponseCode(), suppress it.
                    // Original exception (ex) should have all the info we need to log.
                }
            }
            String errorMsg = String.format(
                    "Network error while downloading skin for \"%s\" from %s: %s",
                    skinName, skinServer, ex);
            LogUtil.logWarning(errorMsg);

        } catch (Exception ex) {
            // Log unexpected errors
            String errorMsg = String.format(
                    "Unexpected error while downloading skin for \"%s\" from %s: %s",
                    skinName, skinServer, ex);
            LogUtil.logWarning(errorMsg);

        } finally {
            // Clean up after ourselves
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
