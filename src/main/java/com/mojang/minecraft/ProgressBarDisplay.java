package com.mojang.minecraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import com.oyasunadev.mcraft.client.util.Constants;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.gui.HUDScreen;
import com.mojang.minecraft.render.ShapeRenderer;

public final class ProgressBarDisplay {

    public static String text = "";
    private Minecraft minecraft;
    public static String title = "";
    private long start = System.currentTimeMillis();

    public static String terrainId = "";
    public static String sideId = "";
    public static String edgeId = "";

    public static HashMap<String, String> serverConfig = new HashMap<>();

    public static HashMap<String, String> fetchConfig(String location) {
        HashMap<String, String> localHashMap = new HashMap<String, String>();
        try {
            URLConnection urlConnection = makeConnection(location, "");
            InputStream localInputStream = getInputStream(urlConnection);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    localInputStream));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                // LogUtil.logInfo(new
                // StringBuilder().append("Read line: ").append(str).toString());
                String[] arrayOfString = str.split("=", 2);
                if (arrayOfString.length > 1) {
                    localHashMap.put(arrayOfString[0].trim(), arrayOfString[1].trim());
                    // LogUtil.logInfo(new
                    // StringBuilder().append("Adding config ")
                    // .append(arrayOfString[0].trim()).append(" = ")
                    // .append(arrayOfString[1].trim()).toString());
                }
            }
            bufferedReader.close();
        } catch (IOException ex) {
            LogUtil.logError("Error fetching config from "+location, ex);
        }

        return localHashMap;
    }

    public static int fetchUrl(File paramFile, String paramString1, String paramString2) {
        try {
            URLConnection localURLConnection = makeConnection(paramString1, paramString2);
            InputStream localInputStream = getInputStream(localURLConnection);

            FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
            byte[] arrayOfByte = new byte[10240];
            int i = 0;
            int j = 0;
            while ((j = localInputStream.read(arrayOfByte, 0, 10240)) >= 0) {
                if (j > 0) {
                    localFileOutputStream.write(arrayOfByte, 0, j);
                    i += j;
                }
            }
            localFileOutputStream.close();
            localInputStream.close();

            return i;
        } catch (IOException ex) {
            LogUtil.logError("Error fetching "+paramString1+ " to file "+paramFile, ex);
            paramFile.delete();
        }
        return 0;
    }

    private static InputStream getInputStream(URLConnection paramURLConnection) throws IOException {
        Object localObject = paramURLConnection.getInputStream();
        String str = paramURLConnection.getContentEncoding();
        if (str != null) {
            str = str.toLowerCase();

            if (str.contains("gzip")) {
                localObject = new GZIPInputStream((InputStream) localObject);
            } else if (str.contains("deflate")) {
                localObject = new InflaterInputStream((InputStream) localObject);
            }
        }

        return (InputStream) localObject;
    }

    private static URLConnection makeConnection(String url, String body)
            throws IOException {
        return makeConnection(url, body, url);
    }

    private static URLConnection makeConnection(String url, String body, String referrer) throws IOException {
        // LogUtil.logInfo(new
        // StringBuilder().append("Making connection to ").append(url)
        // .toString());

        URLConnection localURLConnection = new URL(url).openConnection();
        localURLConnection.addRequestProperty("Referer", referrer);

        localURLConnection.setReadTimeout(40000);
        localURLConnection.setConnectTimeout(15000);
        localURLConnection.setDoInput(true);
        localURLConnection.addRequestProperty("User-Agent", Constants.USER_AGENT);
        localURLConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        localURLConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
        localURLConnection.addRequestProperty("Accept-Encoding", "gzip, deflate, compress");
        localURLConnection.addRequestProperty("Connection", "keep-alive");

        if (body.length() > 0) {
            localURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            localURLConnection.addRequestProperty("Content-Length", Integer.toString(body.length()));
            localURLConnection.setDoOutput(true);

            OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localURLConnection.getOutputStream());
            localOutputStreamWriter.write(body);
            localOutputStreamWriter.flush();
            localOutputStreamWriter.close();
        }

        localURLConnection.connect();
        return localURLConnection;
    }

    public ProgressBarDisplay(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @SuppressWarnings("deprecation")
    public boolean passServerCommand(String lineText) {
        if (lineText == null) {
            return false;
        }
        if (lineText.contains("cfg=")) {
            int i = lineText.indexOf("cfg=");
            if (i > -1) {
                String splitlineText = lineText.substring(i + 4).split(" ")[0];
                String Url = "http://" + splitlineText.replace("$U", minecraft.session.username);

                LogUtil.logInfo("Fetching config from: " + Url);
                serverConfig = fetchConfig(Url);
                if (serverConfig.containsKey("server.detail")) {
                    try {
                        text = serverConfig.get("server.detail");
                    } catch (Exception ex) {
                        LogUtil.logWarning("Error getting server.detail parameter from cfg", ex);
                    }
                }
            }
        } else {
            return false; // return false if no "cfg=" was found
        }
        if (serverConfig.containsKey("server.name")) {
            HUDScreen.ServerName = serverConfig.get("server.name");
        }
        if (serverConfig.containsKey("user.detail")) {
            HUDScreen.UserDetail = serverConfig.get("user.detail");
        }

        return true;
    }

    public final void setProgress(int progress) {
        if (!minecraft.isRunning) {
            throw new StopGameException();
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - start < 0L || currentTime - start >= 20L) {
                start = currentTime;
                int var4 = minecraft.width * 240 / minecraft.height;
                int var5 = minecraft.height * 240 / minecraft.height;
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                ShapeRenderer renderer = ShapeRenderer.instance;
                int textureId = minecraft.textureManager.load("/dirt.png");
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
                float uvScale = 32f;
                renderer.begin();
                renderer.color(0x404040);
                renderer.vertexUV(0f, var5, 0f, 0f, var5 / uvScale);
                renderer.vertexUV(var4, var5, 0f, var4 / uvScale, var5 / uvScale);
                renderer.vertexUV(var4, 0f, 0f, var4 / uvScale, 0f);
                renderer.vertexUV(0f, 0f, 0f, 0f, 0f);
                renderer.end();

                if (progress >= 0) {
                    int barX = var4 / 2 - 50;
                    int barY = var5 / 2 + 16;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    renderer.begin();
                    renderer.color(0x808080);
                    renderer.vertex(barX, barY, 0f);
                    renderer.vertex(barX, barY + 2, 0f);
                    renderer.vertex(barX + 100, barY + 2, 0f);
                    renderer.vertex(barX + 100, barY, 0f);

                    renderer.color(0x80FF80);
                    renderer.vertex(barX, barY, 0f);
                    renderer.vertex(barX, barY + 2, 0f);
                    renderer.vertex(barX + progress, barY + 2, 0f);
                    renderer.vertex(barX + progress, barY, 0f);
                    renderer.end();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                minecraft.fontRenderer.render(title, (var4 - minecraft.fontRenderer.getWidth(title)) / 2, var5 / 2 - 4 - 16, 16777215);
                minecraft.fontRenderer.render(text, (var4 - minecraft.fontRenderer.getWidth(text)) / 2, var5 / 2 - 4 + 8, 16777215);
                Display.update();

                try {
                    Thread.yield();
                } catch (Exception e) {
                }
            }
        }
    }

    public final void setText(String message) {
        if (!minecraft.isRunning) {
            throw new StopGameException();
        } else {
            text = message;
            passServerCommand(message);

            if (minecraft.session == null) {
                HackState.setAllEnabled();
                return;
            }

            String joinedString = new StringBuilder().append(title).append(" ").append(text).toString().toLowerCase();

            if (joinedString.contains("-hax")) {
                HackState.setAllDisabled();
            } else { // enable all, it's either +hax or nothing at all
                HackState.setAllEnabled();
            }
            // then we can manually disable others here
            if (joinedString.contains("+fly")) {
                HackState.Fly = true;
            } else if (joinedString.contains("-fly")) {
                HackState.Fly = false;
            }
            if (joinedString.contains("+noclip")) {
                HackState.Noclip = true;
            } else if (joinedString.contains("-noclip")) {
                HackState.Noclip = false;
            }

            if (joinedString.contains("+speed")) {
                HackState.Speed = true;
            } else if (joinedString.contains("-speed")) {
                HackState.Speed = false;
            }

            if (joinedString.contains("+respawn")) {
                HackState.Respawn = true;
            } else if (joinedString.contains("-respawn")) {
                HackState.Respawn = false;
            }

            if ((joinedString.contains("+ophax")) && minecraft.player.userType >= 100) {
                HackState.setAllEnabled();
            }
        }
        setProgress(-1);
    }

    public final void setTitle(String title) {
        if (!minecraft.isRunning) {
            throw new StopGameException();
        } else {
            ProgressBarDisplay.title = title;
            int x = minecraft.width * 240 / minecraft.height;
            int y = minecraft.height * 240 / minecraft.height;
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0D, x, y, 0D, 100D, 300D);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0F, 0F, -200F);
        }
    }
}
