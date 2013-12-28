package com.mojang.minecraft;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.mojang.minecraft.gui.FontRenderer;

public class ChatLine {
	public String message;
	public int time;

	public static String eol = System.getProperty("line.separator");

	public ChatLine(String message) {
		this.message = message;
		time = 0;
		Calendar cal = Calendar.getInstance();
		String month = new SimpleDateFormat("MMM").format(cal.getTime());
		String serverName = ProgressBarDisplay.title.toLowerCase().contains("connecting..") ? ""
				: ProgressBarDisplay.title;
		if (serverName == "" || Minecraft.isSinglePlayer) {
			return;
		}
		serverName = FontRenderer.stripColor(serverName);
		serverName = serverName.replaceAll("[^A-Za-z0-9\\._-]+", "_");
		File logDir = new File(Minecraft.getMinecraftDirectory(), "/logs/");
		File serverDir = new File(logDir, serverName);
		File monthDir = new File(serverDir, "/" + month + "/");
		if (!logDir.exists()) {
			logDir.mkdir();
		}
		if (!serverDir.exists()) {
			serverDir.mkdir();
		}
		if (!monthDir.exists()) {
			monthDir.mkdir();
		}
		String dateStamp = new SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance()
				.getTime());
		String timeStamp = new SimpleDateFormat("HH:mm:ss")
				.format(Calendar.getInstance().getTime());
		try {
			File logFile = new File(monthDir, dateStamp + ".log");
			String str = FontRenderer.stripColor(this.message);
			FileWriter fileWriter = new FileWriter(logFile, true);
			fileWriter.write("[" + timeStamp + "] " + str + eol);
			fileWriter.close();
		} catch (IOException IOException) {
			System.out.println("Cannot log to chatlog: " + IOException);
		}

	}
}
