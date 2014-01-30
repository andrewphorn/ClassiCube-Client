package com.mojang.minecraft;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import com.mojang.minecraft.player.Player;

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
				connection = (HttpURLConnection) new URL(skinServer + minecraft.session.username
						+ ".png").openConnection();
				connection.addRequestProperty("User-Agent", "Mozilla/4.76");
				connection.setDoInput(true);
				connection.setDoOutput(false);
				

				connection.setUseCaches(false);
				connection.connect();

				if (connection.getResponseCode() != 404) {
					Player.newTexture = ImageIO.read(connection.getInputStream()).getSubimage(0, 0, 64, 32);

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
}
