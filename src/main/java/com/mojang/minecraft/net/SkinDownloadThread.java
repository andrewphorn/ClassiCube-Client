package com.mojang.minecraft.net;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.util.LogUtil;
import com.oyasunadev.mcraft.client.util.Constants;

public class SkinDownloadThread extends Thread {

    private final Mob player;
    private final String URL;

    public SkinDownloadThread(Mob player, String url) {
        super();
        this.player = player;
        this.URL = url;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        try {
            URL skinUrl = new URL(this.URL);
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

            if (player.modelName.equalsIgnoreCase("printer")) {
                if (image.getWidth() < 128 || image.getWidth() < 128)
                    player.newTexture = null;
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
                                "Could not download skin from \"%s\". Server returned code %s",
                                URL, responseCode);
                        LogUtil.logWarning(logMsg);
                        return;
                    }
                } catch (IOException ex2) {
                    // If a secondary exception is raised by getResponseCode(), suppress it.
                    // Original exception (ex) should have all the info we need to log.
                }
            }
            String errorMsg = String.format(
                    "Network error while downloading skin from \"%s\": \"%s\"",
                    this.URL, ex);
            LogUtil.logWarning(errorMsg);

        } catch (Exception ex) {
            // Log unexpected errors
            String errorMsg = String.format(
                    "Unexpected error while downloading skin from \"%s\": \"%s\"",
                    this.URL, ex);
            LogUtil.logWarning(errorMsg);

        } finally {
            // Clean up after ourselves
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
