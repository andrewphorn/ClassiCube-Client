package com.oyasunadev.mcraft.client.core;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.LogUtil;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.MinecraftApplet;
import com.mojang.minecraft.ResourceDownloadThread;
import com.mojang.minecraft.SessionData;

/**
 * Created with IntelliJ IDEA.
 * User: Oliver Yasuna
 * Date: 9/30/12
 * Time: 7:16 PM
 */

/**
 * Run Minecraft Classic standalone version.
 */
public class ClassiCubeStandalone {
    /**
     * A class representing the Minecraft Classic game.
     */
    public class MinecraftFrame extends JFrame {
        /**
         * Override the MinecraftApplet class because we need to fake the
         * Document Base and Code Base.
         */
        public class MCraftApplet extends MinecraftApplet {

            /**
         * 
         */
            private static final long serialVersionUID = 1L;

            /**
             * Use our own parameters map.
             */
            private Map<String, String> parameters;

            /**
             * Default constructor.
             */
            public MCraftApplet() {
                parameters = new HashMap<>();
            }

            /**
             * Fake the Code Base.
             * 
             * @return new URL("http://minecraft.net:80/")
             */
            @Override
            public URL getCodeBase() {
                try {
                    return new URL("http://minecraft.net:80/");
                } catch (MalformedURLException ex) {
                    LogUtil.logError("Error getting applet code base.", ex);
                }

                return null;
            }

            /**
             * Fake the Document Base.
             * 
             * @return new URL("http://minecraft.net:80/play.jsp")
             */
            @Override
            public URL getDocumentBase() {
                try {
                    return new URL("http://minecraft.net:80/play.jsp");
                } catch (MalformedURLException ex) {
                    LogUtil.logError("Error getting applet document base.", ex);
                }

                return null;
            }

            /**
             * Return our own parameters variable.
             * 
             * @param name
             * @return
             */
            @Override
            public String getParameter(String name) {
                return parameters.get(name);
            }
        }

        /**
         * A canvas for the Minecraft thread.
         */
        public class MinecraftCanvas extends Canvas {
            public Image image;
            private Image image2;

            private static final long serialVersionUID = 1L;

            /**
             * The Minecraft variable.
             */
            private Minecraft minecraft;

            /**
             * The Minecraft thread.
             */
            private Thread thread;

            /**
             * Default constructor.
             */
            public MinecraftCanvas() {
            }

            /**
             * Start the thread.
             */
            @Override
            public synchronized void addNotify() {
                super.addNotify();

                startThread();
            }

            public void download(String address, String localFileName) {
                OutputStream out = null;
                URLConnection connection = null;
                InputStream in = null;

                try {
                    URL url = new URL(address);
                    out = new BufferedOutputStream(new FileOutputStream(localFileName));
                    connection = url.openConnection();
                    // I HAVE to send this or the server responds with 403
                    connection.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Language", "en-US");
                    connection
                            .setRequestProperty(
                                    "User-Agent",
                                    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");

                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    in = connection.getInputStream();
                    byte[] buffer = new byte[1024];

                    int numRead;
                    while ((numRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, numRead);
                    }

                } catch (Exception ex) {
                    LogUtil.logError("Error downloading an applet resource.", ex);
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException ioe) {
                    }
                }
            }

            public Image getImage() {
                return image;
            }

            public Image getImage2() {
                return image2;
            }

            @Override
            public void paint(Graphics g) {
                if (image == null) {
                    try {
                        SetImage();
                    } catch (IOException ex) {
                        LogUtil.logError("Error setting applet background image.", ex);
                    }
                }
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Font font = new Font("Serif", Font.BOLD, 18);
                g2.setFont(font);
                if (!ResourceDownloadThread.done) {
                    g.drawImage(getImage(), 0, 0, getWidth(), getHeight(), null);
                    font = new Font("Purisa", Font.BOLD, 48);
                    g2.setFont(font);
                    g.setColor(Color.black);
                    g2.drawString("ClassiCube", 12, 50); // shadow
                    g.setColor(Color.white);
                    g2.drawString("ClassiCube", 10, 48); // normal
                    font = new Font("Serif", Font.BOLD, 18);
                    g2.setFont(font);
                    g.setColor(Color.black);
                    g2.drawString(GameSettings.PercentString, 12, 100); // shadow
                    g2.drawString(GameSettings.StatusString, 12, 80);
                    g.setColor(Color.white);
                    g2.drawString(GameSettings.PercentString, 10, 98); // normal
                    g2.drawString(GameSettings.StatusString, 10, 78);
                } else {
                    if (image2 == null) {
                        try {
                            SetImage2();
                        } catch (IOException ex) {
                            LogUtil.logError("Error setting applet background image #2.", ex);
                        }
                    }
                    g.drawImage(getImage2(), 0, 0, getWidth(), getHeight(), null);
                }
            }

            /**
             * Stop the thread.
             */
            @Override
            public synchronized void removeNotify() {
                stopThread();

                super.removeNotify();
            }

            void SetImage() throws IOException {
                File file = new File(Minecraft.getMinecraftDirectory().getPath() + "/rsbg.jpg");
                if (!file.exists()) {
                    download("http://classicube.net/static/client/rsbg.jpg", file.getAbsolutePath());
                }
                image = ImageIO.read(new File(file.getAbsolutePath()));

            }

            void SetImage2() throws IOException {
                File file = new File(Minecraft.getMinecraftDirectory().getPath() + "/bg.jpg");
                if (!file.exists()) {
                    download("http://classicube.net/static/client/bg.jpg", file.getAbsolutePath());
                }
                image2 = ImageIO.read(new File(file.getAbsolutePath()));
            }

            /**
             * Set the "minecraft" variable.
             * 
             * @param minecraft
             *            The new Minecraft variable.
             */
            public void setMinecraft(Minecraft minecraft) {
                this.minecraft = minecraft;
            }

            /**
             * Start the Minecraft client thread.
             */
            public synchronized void startThread() {
                if (thread == null) {
                    thread = new Thread(minecraft, "Client");

                    thread.start();
                }
            }

            /**
             * Stop the Minecraft client.
             */
            private synchronized void stopThread() {
                if (thread != null) {
                    minecraft.isRunning = false;

                    try {
                        thread.join();
                    } catch (InterruptedException ex) {
                        LogUtil.logWarning("Interrupted while waiting for Minecraft to shut down.",
                                ex);
                        minecraft.shutdown();
                    }

                    thread = null;
                }
            }
        }

        /**
     * 
     */
        private static final long serialVersionUID = 1L;

        /**
         * Minecraft reference.
         */
        private Minecraft minecraft;

        /**
         * Default constructor.
         */
        public MinecraftFrame() {
            setSize(1024, 512);
            // setResizable(false);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    minecraft.isRunning = false;
                }
            });
        }

        /**
         * Starts Minecraft Classic
         * 
         * @param Player
         *            Player name
         * @param Server
         *            Server address
         * @param Mppass
         *            The player's MPPass
         * @param Port
         *            Server port
         * @param skinServer
         *            The url of the skin server.
         * @param fullscreen
         *            True if the game should be in fullScreen.
         */
        public void startMinecraft(String Player, String Server, String Mppass, int Port,
                String skinServer, boolean fullscreen) {

            MCraftApplet applet = new MCraftApplet();
            final MinecraftCanvas canvas = new MinecraftCanvas();

            minecraft = new Minecraft(canvas, applet, getWidth(), getHeight(), fullscreen, true);

            minecraft.session = new SessionData(Player, "noidea");
            minecraft.session.mppass = Mppass;
            minecraft.session.haspaid = true;
            minecraft.server = Server;
            minecraft.port = Port;
            if (skinServer != null) {
                minecraft.skinServer = skinServer;
            }

            if (Player == null && Server == null && Mppass == null) {
                minecraft.session = null;
            }

            canvas.setMinecraft(minecraft);
            canvas.setSize(getSize());

            add(canvas, "Center");

            canvas.setFocusable(true);

            pack();
            setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
                    (Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
            setVisible(true);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (!minecraft.isRunning) {
                            minecraft.shutdown();
                            dispose();
                        }

                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException ex) {
                            LogUtil.logWarning("Interrupted while running Minecraft from applet.",
                                    ex);
                        }
                    }
                }
            }).start();

            boolean pass = false;

            while (!pass) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    // TODO: figure out wtf we're doing here
                    LogUtil.logWarning("Something happened.", ex);
                }

                if (minecraft.isRunning) {
                    pass = true;
                }
            }

            // DO SHIT...?
        }
    }

    public static String[] storedArgs;

    public static void main(String[] args) {
        storedArgs = args;
        String player = null;
        String server = null;
        int port = 0;
        String mppass = null;
        String skinServer = null;
        boolean startFullScreen = false;
        if (args != null && args.length > 3) {
            try {
                server = args[0];
                port = Integer.parseInt(args[1]);
                player = args[2];
                mppass = args[3];
                skinServer = args[4];
                if (args.length >= 4) {
                    startFullScreen = Boolean.parseBoolean(args[5]);
                }
            } catch (Exception e) {
            }
        }
        ClassiCubeStandalone classicubeStandalone = new ClassiCubeStandalone();
        if (player == null || server == null || mppass == null || port <= 0) {
            classicubeStandalone.startMinecraft(null, null, null, 0, skinServer, startFullScreen);
        } else {
            classicubeStandalone.startMinecraft(player, server, mppass, port, skinServer,
                    startFullScreen);
        }
    }

    /**
     * Default constructor.
     */
    public ClassiCubeStandalone() {

    }

    public void startMinecraft() {
        MinecraftFrame minecraftFrame = new MinecraftFrame();

        minecraftFrame.startMinecraft(null, null, null, 0, null, false);
    }

    public void startMinecraft(String Player, String Server, String Mppass, int Port,
            String skinServer, boolean fullscreen) {
        MinecraftFrame minecraftFrame = new MinecraftFrame();

        minecraftFrame.startMinecraft(Player, Server, Mppass, Port, skinServer, fullscreen);
    }
}
