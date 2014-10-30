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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.mojang.minecraft.GameSettings;
import com.mojang.util.LogUtil;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.MinecraftApplet;
import com.mojang.minecraft.ResourceDownloadThread;
import com.mojang.minecraft.SessionData;
import com.mojang.util.StreamingUtil;
import com.oyasunadev.mcraft.client.util.Constants;

/**
 * Run Minecraft Classic standalone version.
 */
public class ClassiCubeStandalone {
    private static final String CODE_BASE_URL = "http://minecraft.net:80/",
            DOCUMENT_BASE_URL = "http://minecraft.net:80/play.jsp",
            BACKGROUND_URL_1 = "http://static.classicube.net/client/rsbg.jpg",
            BACKGROUND_URL_2 = "http://static.classicube.net/client/bg.jpg";

    private ClassiCubeStandalone() {
    }

    /**
     * The game window.
     */
    public class MinecraftFrame extends JFrame {

        /**
         * Override the MinecraftApplet class because we need to fake the Document Base and Code
         * Base.
         */
        public class MCraftApplet extends MinecraftApplet {

            private static final long serialVersionUID = 1L;
            private final Map<String, String> parameters = new HashMap<>();

            @Override
            public URL getCodeBase() {
                try {
                    return new URL(CODE_BASE_URL);
                } catch (MalformedURLException ex) {
                    LogUtil.logError("Error getting applet code base.", ex);
                    return null;
                }
            }

            @Override
            public URL getDocumentBase() {
                try {
                    return new URL(DOCUMENT_BASE_URL);
                } catch (MalformedURLException ex) {
                    LogUtil.logError("Error getting applet document base.", ex);
                    return null;
                }
            }

            @Override
            public String getParameter(String name) {
                return parameters.get(name);
            }
        }

        /**
         * A canvas for the Minecraft thread.
         */
        public class MinecraftCanvas extends Canvas {

            private static final long serialVersionUID = 1L;
            public Image image;
            private Image image2;

            private Minecraft minecraft;

            /**
             * The Minecraft thread.
             */
            private Thread thread;

            /**
             * Start the thread.
             */
            @Override
            public synchronized void addNotify() {
                super.addNotify();

                startThread();
            }

            public void download(String address, String localFileName) {
                URLConnection connection;
                try {
                    URL url = new URL(address);
                    connection = url.openConnection();
                    connection.setRequestProperty("User-Agent", Constants.USER_AGENT);
                    connection.setDoInput(true);
                    try (InputStream in = connection.getInputStream()) {
                        StreamingUtil.copyStreamToFile(in, new File(localFileName));
                    }
                } catch (Exception ex) {
                    LogUtil.logError("Error downloading an applet resource.", ex);
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
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
                    download(BACKGROUND_URL_1, file.getAbsolutePath());
                }
                image = ImageIO.read(new File(file.getAbsolutePath()));

            }

            void SetImage2() throws IOException {
                File file = new File(Minecraft.getMinecraftDirectory().getPath() + "/bg.jpg");
                if (!file.exists()) {
                    download(BACKGROUND_URL_2, file.getAbsolutePath());
                }
                image2 = ImageIO.read(new File(file.getAbsolutePath()));
            }

            /**
             * Set the "minecraft" variable.
             *
             * @param minecraft The new Minecraft variable.
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
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            setLayout(new BorderLayout());

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    setVisible(false);
                    minecraft.isRunning = false;
                }
            });
        }

        /**
         * Starts Minecraft Classic
         *
         * @param player Player name
         * @param server Server address
         * @param mppass The player's MPPass
         * @param port Server port
         * @param skinServer The URL of the skin server.
         * @param fullscreen True if the game should be in fullScreen.
         */
        public void startMinecraft(String player, String server, String mppass, int port,
                String skinServer, boolean fullscreen) {

            MCraftApplet applet = new MCraftApplet();
            final MinecraftCanvas canvas = new MinecraftCanvas();

            minecraft = new Minecraft(canvas, applet, getWidth(), getHeight(), fullscreen, false);

            minecraft.session = new SessionData(player, "noidea");
            minecraft.session.mppass = mppass;
            minecraft.session.haspaid = true;
            minecraft.server = server;
            minecraft.port = port;
            if (skinServer != null) {
                minecraft.skinServer = skinServer;
            }

            if (player == null && server == null && mppass == null) {
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

            while (true) {
                if (!minecraft.isRunning) {
                    minecraft.shutdown();
                    System.exit(0);
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                }
            }
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
            classicubeStandalone.startMinecraft(player, server, mppass, port, skinServer, startFullScreen);
        }
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
