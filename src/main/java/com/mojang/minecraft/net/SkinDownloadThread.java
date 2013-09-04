package com.mojang.minecraft.net;

import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;

public class SkinDownloadThread extends Thread {
    String skinServer;
    
    public SkinDownloadThread(NetworkPlayer networkPlayer, String skinServer) {
	super();

	this.player = networkPlayer;
	this.skinServer = skinServer;
    }

    @Override
    public void run() {
	HttpURLConnection connection = null;

	try {
	    connection = (HttpURLConnection) new URL(
		    skinServer
			    + (player.SkinName == null ? player.name
				    : player.SkinName) + ".png")
		    .openConnection();
	    connection.setRequestProperty("Content-Type",
		    "application/x-www-form-urlencoded");
	    connection.setRequestProperty("Content-Language", "en-US");
	    connection
		    .setRequestProperty(
			    "User-Agent",
			    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");

	    connection.setUseCaches(false);
	    connection.setDoInput(true);
	    connection.setDoOutput(false);

	    connection.connect();

	    if (connection.getResponseCode() == 404) {
		return;
	    }

	    player.newTexture = ImageIO.read(connection.getInputStream());
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (connection != null) {
		connection.disconnect();
	    }
	}
    }

    private NetworkPlayer player;
}
