package com.mojang.minecraft.net;

import com.mojang.minecraft.HackState;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.HUDScreen;
import com.mojang.util.LogUtil;
import com.oyasunadev.mcraft.client.util.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class WOMConfig {

    private boolean enabled;
    private static HashMap<String, String> serverConfig = new HashMap<>();
    private final Minecraft minecraft;

    public WOMConfig(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void readHax(String name, String motd) {
        String joinedString = (name + " " + motd).toLowerCase();

        if (joinedString.contains("-hax")) {
            HackState.setAllDisabled();
        } else { // enable all, it's either +hax or nothing at all
            HackState.setAllEnabled();
        }

        // then we can manually disable others here
        if (joinedString.contains("+fly")) {
            HackState.fly = true;
        } else if (joinedString.contains("-fly")) {
            HackState.fly = false;
        }

        if (joinedString.contains("+noclip")) {
            HackState.noclip = true;
        } else if (joinedString.contains("-noclip")) {
            HackState.noclip = false;
        }

        if (joinedString.contains("+speed")) {
            HackState.speed = true;
        } else if (joinedString.contains("-speed")) {
            HackState.speed = false;
        }

        if (joinedString.contains("+respawn")) {
            HackState.respawn = true;
        } else if (joinedString.contains("-respawn")) {
            HackState.respawn = false;
        }

        if ((joinedString.contains("+ophax")) && minecraft.player.userType >= 100) {
            HackState.setAllEnabled();
        }
    }

    public void readCfg(String motd) {
        int i = motd.indexOf("cfg=");
        if (i > -1) {
            enabled = true;
            String splitlineText = motd.substring(i + 4).split(" ")[0];
            String url = "http://" + splitlineText.replace("$U", minecraft.session.username);

            LogUtil.logInfo("Fetching config from: " + url);
            minecraft.progressBar.setText("Loading...");
            serverConfig = fetchConfig(url);
            if (serverConfig.containsKey("server.detail")) {
                minecraft.progressBar.setText(serverConfig.get("server.detail"));
            }
            if (serverConfig.containsKey("server.name")) {
                HUDScreen.ServerName = serverConfig.get("server.name");
            }
            if (serverConfig.containsKey("user.detail")) {
                HUDScreen.UserDetail = serverConfig.get("user.detail");
            }
        }
    }

    public boolean hasKey(String key) {
        return serverConfig.containsKey(key);
    }

    public String getValue(String key) {
        return serverConfig.get(key);
    }

    public boolean isEnabled() {
        return enabled;
    }

    // Download and parse WoM-style config from given URL.
    // Logs an error message and returns an empty HashMap on failure.
    private static HashMap<String, String> fetchConfig(String url) {
        HashMap<String, String> localHashMap = new HashMap<>();
        try {
            URLConnection urlConnection = makeConnection(url, "");
            try (BufferedReader bufferedReader
                    = new BufferedReader(new InputStreamReader(getInputStream(urlConnection)))) {
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    String[] arrayOfString = str.split("=", 2);
                    if (arrayOfString.length > 1) {
                        String key = arrayOfString[0].trim();
                        String value = arrayOfString[1].trim();
                        localHashMap.put(key, value);
                        LogUtil.logInfo(String.format("WoM Config: %s = %s", key, value));
                    }
                }
            }
        } catch (IOException ex) {
            LogUtil.logError("Error fetching config from " + url, ex);
            localHashMap.clear();
        }
        return localHashMap;
    }

    private static InputStream getInputStream(URLConnection connection) throws IOException {
        InputStream stream = connection.getInputStream();
        String encoding = connection.getContentEncoding();
        if (encoding != null) {
            encoding = encoding.toLowerCase();
            if (encoding.contains("gzip")) {
                stream = new GZIPInputStream(stream);
            } else if (encoding.contains("deflate")) {
                stream = new InflaterInputStream(stream);
            }
        }
        return stream;
    }

    private static URLConnection makeConnection(String url, String body) throws IOException {
        return makeConnection(url, body, url);
    }

    private static URLConnection makeConnection(String url, String body, String referer)
            throws IOException {
        URLConnection connection = new URL(url).openConnection();
        connection.addRequestProperty("Referer", referer);

        connection.setReadTimeout(40000);
        connection.setConnectTimeout(15000);
        connection.setDoInput(true);
        connection.addRequestProperty("User-Agent", Constants.USER_AGENT);
        connection.addRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
        connection.addRequestProperty("Accept-Encoding", "gzip, deflate, compress");
        connection.addRequestProperty("Connection", "keep-alive");

        if (body.length() > 0) {
            connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.addRequestProperty("Content-Length", Integer.toString(body.length()));
            connection.setDoOutput(true);
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                writer.write(body);
            }
        }

        connection.connect();
        return connection;
    }
}
