/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oyasunadev.mcraft.client.core;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.ResourceDownloadThread;
import com.mojang.minecraft.SessionData;
import com.mojang.util.LogUtil;
import com.mojang.util.StreamingUtil;
import com.oyasunadev.mcraft.client.util.Constants;
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
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * The game window.
 */
public class MinecraftFrame extends JFrame {

    private static final String BACKGROUND_URL_1 = "http://static.classicube.net/client/rsbg.jpg",
            BACKGROUND_URL_2 = "http://static.classicube.net/client/bg.jpg";

    /**
     * A canvas for the Minecraft thread.
     */
    public class MinecraftCanvas extends Canvas {
        final Font logoFont = new Font("Purisa", Font.BOLD, 48);
        final Font statusFont = new Font("Serif", Font.BOLD, 18);
        public Image preloadImage;
        private Image loadingImage;
        
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
                try (final InputStream in = connection.getInputStream()) {
                    StreamingUtil.copyStreamToFile(in, new File(localFileName));
                }
            } catch (Exception ex) {
                LogUtil.logError("Error downloading an applet resource.", ex);
            }
        }

        @Override
        public void paint(Graphics g) {
            //if(ResourceDownloadThread.done)return;
            if (!ResourceDownloadThread.done) {
                if (preloadImage == null) {
                    try {
                        loadPreloadBackground();
                    } catch (IOException ex) {
                        LogUtil.logError("Error setting applet background image.", ex);
                    }
                }
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.drawImage(preloadImage, 0, 0, getWidth(), getHeight(), null);
                g2.setFont(logoFont);
                g.setColor(Color.black);
                g2.drawString("ClassiCube", 12, 50); // shadow
                g.setColor(Color.white);
                g2.drawString("ClassiCube", 10, 48); // normal

                g2.setFont(statusFont);
                g.setColor(Color.black);
                g2.drawString(GameSettings.PercentString, 12, 100); // shadow
                g2.drawString(GameSettings.StatusString, 12, 80);
                g.setColor(Color.white);
                g2.drawString(GameSettings.PercentString, 10, 98); // normal
                g2.drawString(GameSettings.StatusString, 10, 78);
            } else {
                if (loadingImage == null) {
                    try {
                        loadLoadingBackground();
                    } catch (IOException ex) {
                        LogUtil.logError("Error setting applet background image #2.", ex);
                    }
                }
                g.drawImage(loadingImage, 0, 0, getWidth(), getHeight(), null);
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

        void loadPreloadBackground() throws IOException {
            File file = new File(Minecraft.getMinecraftDirectory().getPath() + "/rsbg.jpg");
            if (!file.exists()) {
                download(BACKGROUND_URL_1, file.getAbsolutePath());
            }
            preloadImage = ImageIO.read(new File(file.getAbsolutePath()));
        }

        void loadLoadingBackground() throws IOException {
            File file = new File(Minecraft.getMinecraftDirectory().getPath() + "/bg.jpg");
            if (!file.exists()) {
                download(BACKGROUND_URL_2, file.getAbsolutePath());
            }
            loadingImage = ImageIO.read(new File(file.getAbsolutePath()));
        }

        /**
         * Start the Minecraft client thread.
         */
        public synchronized void startThread() {
            if (thread == null) {
                thread = new Thread(minecraft, "GameLoop-Standalone");
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
    private Minecraft minecraft;

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
    public void startMinecraft(String player, String server, String mppass, int port, String skinServer, boolean fullscreen) {
        //  Set up Minecraft instance
        MCraftApplet applet = new MCraftApplet();
        MinecraftCanvas canvas = new MinecraftCanvas();
        minecraft = new Minecraft(canvas, applet, getWidth(), getHeight(), fullscreen, false);

        if (player != null || server != null || mppass != null) {
            minecraft.session = new SessionData(player, "noidea");
            minecraft.session.mppass = mppass;
            minecraft.session.haspaid = true;
        }
        minecraft.server = server;
        minecraft.port = port;
        if (skinServer != null) {
            minecraft.skinServer = skinServer;
        }

        // finish layout and center the frame
        canvas.setFocusable(true);
        canvas.setSize(getSize());
        add(canvas, BorderLayout.CENTER);
        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);

        // at least, show the game window! This starts the render loop.
        setVisible(true);

        // Idle the main thread until MC has shut down
        while (true) {
            if (!minecraft.isRunning) {
                minecraft.shutdown();
                System.exit(0);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }

}
