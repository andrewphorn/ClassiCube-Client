package com.mojang.minecraft;

import com.mojang.minecraft.player.Player;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;

public class SkinDownloadThread extends Thread {
    String skinServer;
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
		connection = (HttpURLConnection) new URL(
			skinServer
				+ minecraft.session.username + ".png")
			.openConnection();

		connection.setDoInput(true);
		connection.setDoOutput(false);
		connection.setRequestProperty("Content-Type",
			"application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Language", "en-US");
		connection
			.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");

		connection.setUseCaches(false);
		connection.connect();

		if (connection.getResponseCode() != 404) {
		    Player.newTexture = ImageIO.read(connection
			    .getInputStream());

		    return;
		}
	    } catch (Exception var4) {
		var4.printStackTrace();
	    } finally {
		if (connection != null) {
		    connection.disconnect();
		}
	    }

	}
    }

    private Minecraft minecraft;
}
