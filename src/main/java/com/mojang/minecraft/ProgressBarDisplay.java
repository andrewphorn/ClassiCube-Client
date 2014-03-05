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

    public static HashMap<String, String> serverConfig = new HashMap<String, String>();

    public static void copyFile(File paramFile1, File paramFile2) {
        FileChannel fileChannel1 = null;
        FileChannel fileChannel2 = null;

        // System.out.println("Copy " + paramFile1 + " to " + paramFile2);
        try {
            if (!paramFile2.exists()) {
                paramFile2.createNewFile();
            }

            fileChannel1 = new FileInputStream(paramFile1).getChannel();
            fileChannel2 = new FileOutputStream(paramFile2).getChannel();
            fileChannel2.transferFrom(fileChannel1, 0L, fileChannel1.size());
        } catch (IOException ex) {
            paramFile2.delete();
            System.out.println("IO Error copying file: " + ex);
        } finally {
            try {
                if (fileChannel1 != null) {
                    fileChannel1.close();
                }
            } catch (IOException ex) {
            }
            try {
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    public static HashMap<String, String> fetchConfig(String location) {
        HashMap<String, String> localHashMap = new HashMap<String, String>();
        try {
            URLConnection urlConnection = makeConnection(location, "");
            InputStream localInputStream = getInputStream(urlConnection);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    localInputStream));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                // System.out
                // .println(new
                // StringBuilder().append("Read line: ").append(str).toString());
                String[] arrayOfString = str.split("=", 2);
                if (arrayOfString.length > 1) {
                    localHashMap.put(arrayOfString[0].trim(), arrayOfString[1].trim());
                    // System.out.println(new
                    // StringBuilder().append("Adding config ")
                    // .append(arrayOfString[0].trim()).append(" = ")
                    // .append(arrayOfString[1].trim()).toString());
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println(new StringBuilder().append("Caught exception: ").append(e)
                    .toString());
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
        } catch (IOException localIOException) {
            System.out.println(new StringBuilder().append("Error fetching ").append(paramString1)
                    .append(" to file: ").append(paramFile).append(": ").append(localIOException)
                    .toString());

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

    private static URLConnection makeConnection(String paramString1, String paramString2)
            throws IOException {
        return makeConnection(paramString1, paramString2, paramString1, true);
    }

    private static URLConnection makeConnection(String url, String s1, String s2,
            boolean AddWomProperty) throws IOException {
        // System.out.println(new
        // StringBuilder().append("Making connection to ").append(url)
        // .toString());

        URLConnection localURLConnection = new URL(url).openConnection();
        localURLConnection.addRequestProperty("Referer", s2);

        localURLConnection.setReadTimeout(40000);
        localURLConnection.setConnectTimeout(15000);
        localURLConnection.setDoInput(true);

        if (AddWomProperty) {
            localURLConnection.addRequestProperty("X-Wom-Version", "WoMClient-2.0.8");
            localURLConnection.addRequestProperty("X-Wom-Username", "Greg0001");
            localURLConnection.addRequestProperty("User-Agent", new StringBuilder().append("WoM/")
                    .append("WoMClient-2.0.8").toString());
        } else {
            localURLConnection
                    .addRequestProperty("User-Agent",
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:6.0) Gecko/20100101 Firefox/6.0 FirePHP/0.5");
        }

        localURLConnection.addRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        localURLConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
        localURLConnection.addRequestProperty("Accept-Encoding", "gzip, deflate, compress");
        localURLConnection.addRequestProperty("Connection", "keep-alive");

        if (s1.length() > 0) {
            localURLConnection.addRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            localURLConnection.addRequestProperty("Content-Length", Integer.toString(s1.length()));
            localURLConnection.setDoOutput(true);

            OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(
                    localURLConnection.getOutputStream());
            localOutputStreamWriter.write(s1);
            localOutputStreamWriter.flush();
            localOutputStreamWriter.close();
        }

        localURLConnection.connect();

        return localURLConnection;
    }

    public ProgressBarDisplay(Minecraft var1) {
        minecraft = var1;
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

                // System.out.println("Fetching config from: " + Url);
                serverConfig = fetchConfig(Url);
                if (serverConfig.containsKey("server.detail")) {
                    try {
                        String str = serverConfig.get("server.detail");
                        text = str;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
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

    public final void setProgress(int var1) {
        if (!minecraft.isRunning) {
            throw new StopGameException();
        } else {
            long var2;
            if ((var2 = System.currentTimeMillis()) - start < 0L || var2 - start >= 20L) {
                start = var2;
                int var4 = minecraft.width * 240 / minecraft.height;
                int var5 = minecraft.height * 240 / minecraft.height;
                GL11.glClear(16640);
                ShapeRenderer var6 = ShapeRenderer.instance;
                int var7 = minecraft.textureManager.load("/dirt.png");
                GL11.glBindTexture(3553, var7);
                float var10 = 32.0F;
                var6.begin();
                var6.color(4210752);
                var6.vertexUV(0.0F, var5, 0.0F, 0.0F, var5 / var10);
                var6.vertexUV(var4, var5, 0.0F, var4 / var10, var5 / var10);
                var6.vertexUV(var4, 0.0F, 0.0F, var4 / var10, 0.0F);
                var6.vertexUV(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                var6.end();
                if (var1 >= 0) {
                    var7 = var4 / 2 - 50;
                    int var8 = var5 / 2 + 16;
                    GL11.glDisable(3553);
                    var6.begin();
                    var6.color(8421504);
                    var6.vertex(var7, var8, 0.0F);
                    var6.vertex(var7, var8 + 2, 0.0F);
                    var6.vertex(var7 + 100, var8 + 2, 0.0F);
                    var6.vertex(var7 + 100, var8, 0.0F);
                    var6.color(8454016);
                    var6.vertex(var7, var8, 0.0F);
                    var6.vertex(var7, var8 + 2, 0.0F);
                    var6.vertex(var7 + var1, var8 + 2, 0.0F);
                    var6.vertex(var7 + var1, var8, 0.0F);
                    var6.end();
                    GL11.glEnable(3553);
                }

                minecraft.fontRenderer.render(title,
                        (var4 - minecraft.fontRenderer.getWidth(title)) / 2, var5 / 2 - 4 - 16,
                        16777215);
                minecraft.fontRenderer.render(text,
                        (var4 - minecraft.fontRenderer.getWidth(text)) / 2, var5 / 2 - 4 + 8,
                        16777215);
                Display.update();

                try {
                    Thread.yield();
                } catch (Exception var9) {
                    ;
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

            String joinedString = new StringBuilder().append(title).append(" ").append(text)
                    .toString().toLowerCase();

            if (joinedString.indexOf("-hax") > -1) {
                HackState.setAllDisabled();
            } else { // enable all, it's either +hax or nothing at all
                HackState.setAllEnabled();
            }
            // then we can manually disable others here
            if (joinedString.indexOf("+fly") > -1) {
                HackState.Fly = true;
            } else if (joinedString.indexOf("-fly") > -1) {
                HackState.Fly = false;
            }
                        
            if (joinedString.indexOf("+noclip") > -1) {
                HackState.Noclip = true;
            } else if (joinedString.indexOf("-noclip") > -1) {
                HackState.Noclip = false;
            }
                        
            if (joinedString.indexOf("+speed") > -1) {
                HackState.Speed = true;
            } else if (joinedString.indexOf("-speed") > -1) {
                HackState.Speed = false;
            }
                        
                        if (joinedString.indexOf("+respawn") > -1) {
                            HackState.Respawn = true;
            } else if (joinedString.indexOf("-respawn") > -1) {
                HackState.Respawn = false;
            }
                        
            if ((joinedString.indexOf("+ophax") > -1) 
                                && minecraft.player.userType >= 100) {
                HackState.setAllEnabled();
            }
        }
        setProgress(-1);
    }

    public final void setTitle(String var1) {
        if (!minecraft.isRunning) {
            throw new StopGameException();
        } else {
            title = var1;
            int var3 = minecraft.width * 240 / minecraft.height;
            int var2 = minecraft.height * 240 / minecraft.height;
            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0D, var3, var2, 0.0D, 100.0D, 300.0D);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -200.0F);
        }
    }
}
