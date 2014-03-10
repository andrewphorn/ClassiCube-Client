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

    /**
     * Constructs a new chatline, logs to the userdata aswell
     * 
     * @param message
     *            The chatline
     */
    public ChatLine(String message) {
        this.message = message;
        time = 0;
        Calendar cal = Calendar.getInstance();
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        String serverName = ProgressBarDisplay.title.toLowerCase().contains("connecting..") ? ""
                : ProgressBarDisplay.title;
        if ("".equals(serverName) || Minecraft.isSinglePlayer) {
            return;
        }
        serverName = FontRenderer.stripColor(serverName);
        serverName = serverName.replaceAll("[^A-Za-z0-9\\._-]+", "_");
        File logDir = new File(Minecraft.getMinecraftDirectory(), "/logs/");
        File serverDir = new File(logDir, serverName);
        File monthDir = new File(serverDir, "/" + month + "/");
        monthDir.mkdirs();
        String dateStamp = new SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance()
                .getTime());
        String timeStamp = new SimpleDateFormat("HH:mm:ss")
                .format(Calendar.getInstance().getTime());
        File logFile = new File(monthDir, dateStamp + ".log");
        try {
            String str = FontRenderer.stripColor(this.message);
            try (FileWriter fileWriter = new FileWriter(logFile, true)) {
                fileWriter.write("[" + timeStamp + "] " + str + eol);
            }
        } catch (IOException ex) {
            LogUtil.logError("Error logging a chat message to " + logFile, ex);
        }
    }
}
