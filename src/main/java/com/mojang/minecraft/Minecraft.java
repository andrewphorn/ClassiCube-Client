package com.mojang.minecraft;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.mojang.minecraft.gamemode.CreativeGameMode;
import com.mojang.minecraft.gamemode.GameMode;
import com.mojang.minecraft.gui.BlockSelectScreen;
import com.mojang.minecraft.gui.ChatInputScreen;
import com.mojang.minecraft.gui.ErrorScreen;
import com.mojang.minecraft.gui.FontRenderer;
import com.mojang.minecraft.gui.GameOverScreen;
import com.mojang.minecraft.gui.GuiScreen;
import com.mojang.minecraft.gui.HUDScreen;
import com.mojang.minecraft.gui.PauseScreen;
import com.mojang.minecraft.item.Arrow;
import com.mojang.minecraft.item.Item;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelLoader;
import com.mojang.minecraft.level.LevelSerializer;
import com.mojang.minecraft.level.generator.LevelGenerator;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.level.tile.TextureSide;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.model.ModelManager;
import com.mojang.minecraft.model.ModelPart;
import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.NetworkPlayer;
import com.mojang.minecraft.net.PacketType;
import com.mojang.minecraft.net.SkinDownloadThread;
import com.mojang.minecraft.particle.Particle;
import com.mojang.minecraft.particle.ParticleManager;
import com.mojang.minecraft.particle.WaterDropParticle;
import com.mojang.minecraft.physics.AABB;
import com.mojang.minecraft.physics.CustomAABB;
import com.mojang.minecraft.player.InputHandlerImpl;
import com.mojang.minecraft.player.Player;
import com.mojang.minecraft.render.Chunk;
import com.mojang.minecraft.render.ChunkDirtyDistanceComparator;
import com.mojang.minecraft.render.Frustum;
import com.mojang.minecraft.render.FrustumImpl;
import com.mojang.minecraft.render.HeldBlock;
import com.mojang.minecraft.render.LevelRenderer;
import com.mojang.minecraft.render.Renderer;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.minecraft.render.texture.TextureFX;
import com.mojang.minecraft.sound.SoundManager;
import com.mojang.minecraft.sound.SoundPlayer;
import com.mojang.net.NetworkHandler;
import com.mojang.util.ColorCache;
import com.mojang.util.LogUtil;
import com.mojang.util.MathHelper;
import com.mojang.util.StreamingUtil;
import com.mojang.util.Timer;
import com.mojang.util.Vec3D;
import com.oyasunadev.mcraft.client.util.Constants;
import com.oyasunadev.mcraft.client.util.ExtData;

public final class Minecraft implements Runnable {

    // mouse button index constants
    private static final int MB_LEFT = 0, MB_RIGHT = 1, MB_MIDDLE = 2;
    /**
     * True if the player is running, false if otherwise.
     */
    public static boolean playerIsRunning = false;
    /**
     * The Minecraft directory.
     */
    public static File mcDir;
    /**
     * Are we playing single player mode?
     */
    public static boolean isSinglePlayer = false;
    /**
     * True if the application is waiting for something.
     */
    public volatile boolean isWaiting = false;
    /**
     * Is the game running?
     */
    public volatile boolean isRunning;
    /**
     * True if we are in full screen mode, false if otherwise.
     */
    public boolean isFullScreen = false;
    /**
     * The url of the skin server where the skins are located.
     */
    public String skinServer = "http://www.classicube.net/static/skins/";
    /**
     * The current GameMode.
     */
    public GameMode gamemode = new CreativeGameMode(this);
    /**
     * The width of the playing window.
     */
    public int width;
    /**
     * The height of the playing window.
     */
    public int height;
    /**
     * The level we are playing in.
     */
    public Level level;
    /**
     * The renderer of the level we are in.
     */
    public LevelRenderer levelRenderer;
    /**
     * The main player (Us) who is playing.
     */
    public Player player;
    /**
     * Manages the particle system.
     */
    public ParticleManager particleManager;
    /**
     * SessionData for when playing online.
     */
    public SessionData session = null;
    /**
     * The host we are connected to.
     */
    public String host;
    /**
     * The main screen canvas.
     */
    public Canvas canvas;
    /**
     * True if the level has been loaded.
     */
    public boolean isLevelLoaded = false;
    /**
     * Manages the textures of the game.
     */
    public TextureManager textureManager;
    /**
     * Renders the font of the game.
     */
    public FontRenderer fontRenderer;
    /**
     * The current screen
     */
    public GuiScreen currentScreen = null;
    /**
     * Used to display progress when needed.
     */
    public ProgressBarDisplay progressBar = new ProgressBarDisplay(this);
    /**
     * This is used to render whatever we need to render.
     */
    public Renderer renderer = new Renderer(this);
    /**
     * Manages the sound.
     */
    public SoundManager sound;
    /**
     * The name of the current level.
     */
    public String levelName;
    /**
     * The ID of the current level.
     */
    public int levelId;
    /**
     * Can do automated tasks.
     */
    public Robot robot;
    /**
     * The game's HUD.
     */
    public HUDScreen hud;
    /**
     * True if the player is online.
     */
    public boolean isOnline;
    /**
     * Manages networking.
     */
    public NetworkManager networkManager;
    /**
     * Plays sounds.
     */
    public SoundPlayer soundPlayer;
    /**
     * The position of the selected object we are looking at.
     */
    public MovingObjectPosition selected;
    /**
     * The settings of the game.
     */
    public GameSettings settings;
    /**
     * True if the application is an applet.
     */
    public boolean isApplet;
    /**
     * Address of the server we are playing in.
     */
    public String server;
    /**
     * Port of the server we are playing in.
     */
    public int port;
    /**
     * Set this to whatever you want to show as debug information in the HUD. It will occupy one
     * line. Right now it shows FPS and Chunk Updates.
     */
    public String debug;
    /**
     * True if the application has the mouse's focus.
     */
    public boolean hasMouse;
    /**
     * True if it is raining.
     */
    public boolean isRaining;
    /**
     * True if it snowing.
     */
    public boolean isSnowing;
    /**
     * The applet of this game.
     */
    public MinecraftApplet applet;
    public List<SelectionBoxData> selectionBoxes = new ArrayList<>();
    public List<HotKeyData> hotKeys = new ArrayList<>();
    public HackState hackState; // TODO Never used
    public List<PlayerListNameData> playerListNameData = new ArrayList<>();
    public List<Block> disallowedPlacementBlocks = new ArrayList<>();
    public List<Block> DisallowedBreakingBlocks = new ArrayList<>();
    public MonitoringThread monitoringThread;
    public int tempDisplayWidth;
    public int tempDisplayHeight;
    public boolean canRenderGUI = true;
    float cameraDistance = -0.1F; // TODO Never used
    int receivedExtensionLength;
    boolean isShuttingDown = false;
    boolean canSendHeldBlock = false;
    boolean serverSupportsMessages = false;
    int[] inventoryCache;
    boolean isLoadingMap = false;
    /**
     * This timer determines how much time will pass between block modifications. It is used to
     * prevent really fast block spamming.
     */
    private Timer timer = new Timer(20F);
    private ResourceDownloadThread resourceThread;
    private int ticks;
    private int punchingCooldown; // survival
    private int lastClick;
    /**
     * The cursor instance.
     */
    private Cursor cursor;

    /**
     * Creates a new Minecraft instance.
     *
     * @param canvas Canvas to use for drawing.
     * @param applet Applet of this instance
     * @param width Width of the window
     * @param height Height of the window
     * @param fullscreen True if game should be in fullscreen
     * @param isApplet True if the game is running as an applet
     */
    public Minecraft(Canvas canvas, MinecraftApplet applet, int width, int height,
            boolean fullscreen, boolean isApplet) {
        this.applet = applet;
        this.canvas = canvas;
        this.width = width;
        this.height = height;
        this.isFullScreen = fullscreen;
        this.isApplet = isApplet;
        sound = new SoundManager();
        ticks = 0;
        punchingCooldown = 0;
        levelName = null;
        levelId = 0;
        isOnline = false;
        selected = null;
        server = null;
        port = 0;
        isRunning = false;
        debug = "";
        hasMouse = false;
        lastClick = 0;
        isRaining = false;
        isSnowing = false;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            LogUtil.logWarning("Failed to set UI look and feel.", ex);
        }
        if (canvas != null) {
            try {
                robot = new Robot();
            } catch (AWTException ex) {
                LogUtil.logError("Failed to create the AWT Robot!", ex);
            }
        }

    }

    private static void checkGLError(String context) {
        int error = GL11.glGetError();
        if (error != GL11.GL_NO_ERROR) {
            String errorString = GLU.gluErrorString(error);
            LogUtil.logError("########## GL ERROR ##########");
            LogUtil.logError("@ " + context);
            LogUtil.logError(error + ": " + errorString);
            System.exit(1);
        }
    }

    public static boolean doesUrlExistAndIsImage(URL url) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("HEAD");
            boolean result = (con.getResponseCode() == HttpURLConnection.HTTP_OK)
                    && con.getContentType().contains("image");
            con.disconnect();
            return result;
        } catch (Exception ex) {
            LogUtil.logWarning("Failed to check for an image at " + url, ex);
            return false;
        }
    }

    public static File getMinecraftDirectory() {
        if (mcDir != null) {
            return mcDir;
        }
        String folder = ".net.classicube.client";
        String home = System.getProperty("user.home");
        File minecraftFolder;
        OperatingSystem os = OperatingSystem.detect();
        switch (os) {
            case LINUX:
            case SOLARIS:
                minecraftFolder = new File(home, folder + '/');
                break;
            case WINDOWS:
                String appData = System.getenv("APPDATA");

                if (appData != null) {
                    minecraftFolder = new File(appData, folder + '/');
                } else {
                    minecraftFolder = new File(home, folder + '/');
                }
                break;
            case MAC_OS_X:
                minecraftFolder = new File(home, "Library/Application Support/" + folder);
                break;
            default:
                minecraftFolder = new File(home, folder + '/');
        }

        if (!minecraftFolder.exists() && !minecraftFolder.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: "
                    + minecraftFolder);
        }

        return minecraftFolder;
    }

    void downloadImage(URL url, File dest) {
        try {
            if (!doesUrlExistAndIsImage(url)) {
                return;
            }
            try (InputStream is = url.openStream()) {
                StreamingUtil.copyStreamToFile(is, dest);
            }
        } catch (Exception ex) {
            LogUtil.logWarning("Failed download an image from " + url + " to " + dest, ex);
        }
    }

    public byte[] flipPixels(byte[] originalBuffer, int width, int height) {
        byte[] flippedBuffer = null;
        int stride = width * 3;
        if (originalBuffer != null) {
            flippedBuffer = new byte[originalBuffer.length];// There are 3 bytes per cell
            for (int y = 0; y < height; y++) {
                System.arraycopy(originalBuffer, y * stride, flippedBuffer, (height - y - 1)
                        * stride, stride);
            }
        }
        return flippedBuffer;
    }

    // Scale of 0 is 128x128 level. Incrementing the scale doubles the level size.
    public final void generateLevel(int scale) {
        String username = (session != null ? session.username : "anonymous");
        Level newLevel = new LevelGenerator(progressBar).generate(username, 128 << scale,
                128 << scale, 64);
        gamemode.prepareLevel(newLevel);
        setLevel(newLevel);
    }

    public String getHash(String urlString) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] urlBytes = urlString.getBytes();
        byte[] hashBytes = md.digest(urlBytes);
        return new BigInteger(1, hashBytes).toString(16);
    }

    public final void grabMouse() {
        if (!hasMouse) {
            hasMouse = true;
            if (isLevelLoaded) {
                try {
                    Mouse.setNativeCursor(cursor);
                    Mouse.setCursorPosition(width / 2, height / 2);
                } catch (LWJGLException ex) {
                    LogUtil.logError("Failed grab mouse!", ex);
                }
            } else {
                Mouse.setGrabbed(true);
            }
            setCurrentScreen(null);
            lastClick = ticks + 10000;
        }
    }

    public final boolean isOnline() {
        return networkManager != null;
    }

    private boolean isSurvival() {
        return !(gamemode instanceof CreativeGameMode);
    }

    private void onMouseClick(int button) {
        if (button == MB_LEFT && punchingCooldown > 0) {
            // enforce punching delay (survival)
            return;
        }

        if (button == MB_LEFT) {
            // Trigger the punch/block-wave animation on left-click
            renderer.heldBlock.offset = -1;
            renderer.heldBlock.moving = true;
        }

        if (button == MB_RIGHT) {
            int selectedBlockId = player.inventory.getSelected();
            if (selectedBlockId > 0 && gamemode.useItem(player, selectedBlockId)) {
                // Player used an item from inventory (survival)
                renderer.heldBlock.pos = 0;
                return;
            }
        }

        if (selected == null) {
            // The cursor is not on any block
            if (button == MB_LEFT && isSurvival()) {
                // Set a 10-tick punching cooldown (survival)
                punchingCooldown = 10;
            }
            return;
        }

        if (selected.hasEntity) {
            // Player punched something that belongs to an entity (survival)
            if (button == MB_LEFT) {
                selected.entity.hurt(player, 4);
            }

        } else {
            // Player clicked on a block
            int x = selected.x;
            int y = selected.y;
            int z = selected.z;
            if (button != MB_LEFT) {
                // When right-clicking on side of a block, figure out where to
                // place the new block.
                if (selected.face == 0) {
                    --y; // below
                }
                if (selected.face == 1) {
                    ++y; // above
                }
                if (selected.face == 2) {
                    --z;
                }
                if (selected.face == 3) {
                    ++z;
                }
                if (selected.face == 4) {
                    --x;
                }
                if (selected.face == 5) {
                    ++x;
                }
            }

            Block block;
            if (level != null) {
                block = Block.blocks[level.getTile(x, y, z)];
            } else {
                // Ignore clicks if no level is loaded
                return;
            }

            if (button == MB_LEFT) {
                // on left-click: delete a block (if allowed)
                if ((block != Block.BEDROCK || player.userType >= 100)
                        && !DisallowedBreakingBlocks.contains(block)) {
                    gamemode.hitBlock(x, y, z);
                }
                return;
            }

            // on right-click: build a block
            int blockID = player.inventory.getSelected();
            if (blockID <= 0 || disallowedPlacementBlocks.contains(Block.blocks[blockID])) {
                return; // if air or not allowed, return
            }
            AABB aabb = Block.blocks[blockID].getCollisionBox(x, y, z);
            boolean isAirOrLiquid = (block == null || block == Block.WATER
                    || block == Block.STATIONARY_WATER || block == Block.LAVA || block == Block.STATIONARY_LAVA);
            if (!isAirOrLiquid
                    || (aabb != null
                    && (!(!player.boundingBox.intersects(aabb) && level.isFree(aabb))))) {
                return;
            }

            if (!gamemode.canPlace(blockID)) {
                // Ignore if gameMode does not allow placing this block type
                // (survival)
                return;
            }

            if (session == null) {
                // Singleplayer-only snow behavior code.
                Block toCheck = Block.blocks[level.getTile(x, y - 1, z)];
                if (toCheck != null && toCheck.id > 0 && (toCheck == Block.SNOW)
                        && selected.face == 1) {
                    if (block == Block.SNOW) {
                        // Ignore placing snow-on-snow. Snow blocks don't stack,
                        // they just merge.
                        return;
                    } else {
                        // When clicking on top face of a snow block,
                        // replace it instead of stacking another block on top
                        // of it
                        y -= 1;
                    }
                }
            }

            if (isOnline()) {
                networkManager.sendBlockChange(x, y, z, button, blockID);
            }

            // Update local copy of the map
            level.netSetTile(x, y, z, blockID);
            renderer.heldBlock.pos = 0F;
            Block.blocks[blockID].onPlace(level, x, y, z);
        }
    }

    public final void pause() {
        if (currentScreen == null) {
            setCurrentScreen(new PauseScreen());
        }
    }

    public void resize() {
        width = Display.getDisplayMode().getWidth();
        height = Display.getDisplayMode().getHeight();

        if (width <= 0) {
            width = 1;
        }
        if (height <= 0) {
            height = 1;
        }
        if (hud != null) {
            hud.width = width * 240 / height;
            hud.height = height * 240 / height;
        }

        if (currentScreen != null) {
            currentScreen.width = width * 240 / height;
            currentScreen.height = height * 240 / height;
            currentScreen.clearButtons();
            currentScreen.onOpen();
        }
    }

    // Starts up the client. Called from Minecraft.run()
    private void initialize() throws Exception {
        mcDir = getMinecraftDirectory();

        resourceThread = new ResourceDownloadThread(mcDir, this);
        resourceThread.run(); // TODO: run asynchronously

        if (!isApplet) {
            System.setProperty("org.lwjgl.librarypath", mcDir + "/natives");
            System.setProperty("net.java.games.input.librarypath", mcDir + "/natives");
        }
        
        LogUtil.logInfo("LWJGL version: " + Sys.getVersion());

        if (session == null) {
            isSinglePlayer = true;
            SessionData.setAllowedBlocks((byte) 1);
        } else {
            if (isApplet) {
                if (session.mppass == null || port < 0) {
                    SessionData.setAllowedBlocks((byte) 1);
                    isSinglePlayer = true;
                }
            }
        }

        if (canvas != null) {
            Display.setParent(canvas);
        } else if (isFullScreen) {
            setDisplayMode();
            Display.setFullscreen(true);
            width = Display.getDisplayMode().getWidth();
            height = Display.getDisplayMode().getHeight();
            tempDisplayWidth = width;
            tempDisplayHeight = height;
        } else {
            Display.setDisplayMode(new DisplayMode(width, height));
        }
        
        Display.setResizable(true);
        Display.setTitle("ClassiCube");

        try {
            Display.create();
        } catch (LWJGLException ex) {
            LogUtil.logError("Failed to create the OpenGL context.", ex);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ex2) {
            }
            Display.create();
        }
        
        logSystemInfo();

        Keyboard.create();
        Mouse.create();

        checkGLError("Pre startup");

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearDepth(1D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        checkGLError("Startup");

        settings = new GameSettings(this, mcDir);
        ShapeRenderer.instance = new ShapeRenderer(2097152, settings); // 2MB
        textureManager = new TextureManager(settings, isApplet);
        textureManager.registerAnimations();

        if (settings.lastUsedTexturePack != null) {
            // Try to load custom texture pack
            File texturePack = new File(getMinecraftDirectory(), "texturepacks/"
                    + settings.lastUsedTexturePack);

            if (texturePack.exists()) {
                textureManager.loadTexturePack(settings.lastUsedTexturePack);
            } else {
                settings.lastUsedTexturePack = null;
                settings.save();
            }
        }

        fontRenderer = new FontRenderer(settings, "/default.png", textureManager);
        monitoringThread = new MonitoringThread(1000); // 1s refresh
        textureManager.initAtlas();

        levelRenderer = new LevelRenderer(this, textureManager);
        Item.initModels();
        Mob.modelCache = new ModelManager();
        GL11.glViewport(0, 0, width, height);
        if (server != null && session != null) {
            // We're in multiplayer, connecting to a server!
            // Create a tiny temporary empty level while we wait for map to be
            // sent
            Level defaultLevel = new Level();
            defaultLevel.setData(8, 8, 8, new byte[512]);
            setLevel(defaultLevel);
        } else {
            // We're in singleplayer!
            try {
                if (!isLevelLoaded) {
                    // Try to load a previously-saved level
                    Level level = new LevelLoader().load(new File(mcDir, "levelc.cw"), player);
                    if (level != null) {
                        if (isSurvival()) {
                            setLevel(level);
                        } else {
                            progressBar.setText("Loading saved map...");
                            setLevel(level);
                            isSinglePlayer = true;
                        }
                    }
                }
            } catch (Exception ex) {
                LogUtil.logError("Failed to load a saved singleplayer level.", ex);
            }

            if (level == null) {
                // If loading failed, generate a new level.
                generateLevel(1);
            }
        }

        particleManager = new ParticleManager(textureManager);
        if (isLevelLoaded) {
            try {
                cursor = new Cursor(16, 16, 0, 0, 1, BufferUtils.createIntBuffer(256), null);
            } catch (LWJGLException ex) {
                LogUtil.logWarning("Failed to create a transparent native cursor.", ex);
            }
        }

        // Start the sound player
        soundPlayer = new SoundPlayer(settings);
        try {
            AudioFormat soundFormat = new AudioFormat(44100F, 16, 2, true, true);
            soundPlayer.dataLine = AudioSystem.getSourceDataLine(soundFormat);
            soundPlayer.dataLine.open(soundFormat, 4410);
            soundPlayer.dataLine.start();
            soundPlayer.running = true;
            Thread soundPlayerThread = new Thread(soundPlayer);
            soundPlayerThread.setDaemon(true);
            soundPlayerThread.setPriority(Thread.MAX_PRIORITY);
            soundPlayerThread.start();
        } catch (Exception ex) {
            soundPlayer.running = false;
            LogUtil.logWarning("Failed to start the sound player.", ex);
        }

        checkGLError("Post startup");
        hud = new HUDScreen(this, width, height);
        if(session != null){
            new SkinDownloadThread(player, session.username, skinServer).start();
        }
        if (server != null && session != null) {
            networkManager = new NetworkManager(
                    this, server, port, session.username, session.mppass);
        }
    }

    private void logSystemInfo() {
        LogUtil.logInfo(String.format(
                "GPU Vendor: %s | Renderer: %s | OpenGL version: %s",
                GL11.glGetString(GL11.GL_VENDOR),
                GL11.glGetString(GL11.GL_RENDERER),
                GL11.glGetString(GL11.GL_VERSION)));
    }

    @Override
    public final void run() {
        isRunning = true;

        try {
            initialize();
        } catch (Exception ex) {
            LogUtil.logError("Failed to start ClassiCube!", ex);
            JOptionPane.showMessageDialog(null, ex.toString(), "Failed to start ClassiCube",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        long fpsUpdateTimer = System.currentTimeMillis();
        int fps = 0;
        try {
            // Main loop!
            while (isRunning) {
                if (isWaiting) {
                    Thread.sleep(100L);
                } else {
                    onFrame();

                    fps++;
                    while (System.currentTimeMillis() >= fpsUpdateTimer + 1000L) {
                        debug = fps + " fps, " + Chunk.chunkUpdates + " chunk updates";
                        Chunk.chunkUpdates = 0;
                        fpsUpdateTimer += 1000L;
                        fps = 0;
                    }
                }
            }
        } catch (StopGameException ex) {
        } catch (Exception ex) {
            LogUtil.logError("Fatal error in main loop (run)", ex);
        } finally {
            shutdown();
        }
    }

    // Called by run() every frame. Handles timing and rendering. Calls tick().
    private void onFrame() {
        // For all your looping needs
        if (canvas == null && Display.isCloseRequested()) {
            isRunning = false;
        }

        // Check if window was resized last frame
        if (!Display.isFullscreen()
                && (canvas.getWidth() != Display.getDisplayMode().getWidth()
                || canvas.getHeight() != Display.getDisplayMode().getHeight())) {
            DisplayMode displayMode = new DisplayMode(canvas.getWidth(), canvas.getHeight());
            try {
                Display.setDisplayMode(displayMode);
            } catch (LWJGLException ex) {
                LogUtil.logError("Error resizing the OpenGL context.", ex);
            }
            resize();
        }

        try {
            // Get current time in seconds 
            double now = System.nanoTime() / 1000000000D;
            double secondsPassed = (now - timer.lastHR);
            timer.lastHR = now;

            // Cap seconds-passed to range [0,1]
            if (secondsPassed < 0D) {
                secondsPassed = 0D;
            }
            if (secondsPassed > 1D) {
                secondsPassed = 1D;
            }

            // Figure out how many ticks took place since last frame
            timer.elapsedDelta = (float) (timer.elapsedDelta + secondsPassed * timer.speed * timer.tps);
            timer.elapsedTicks = (int) timer.elapsedDelta;
            if (timer.elapsedTicks > 100) {
                timer.elapsedTicks = 100;
            }
            timer.elapsedDelta -= timer.elapsedTicks;
            timer.delta = timer.elapsedDelta;

            for (int tick = 0; tick < timer.elapsedTicks; ++tick) {
                ++ticks;
                tick();
            }

            checkGLError("Pre render");
            GL11.glEnable(GL11.GL_TEXTURE_2D);

            if (!isOnline) {
                gamemode.applyCracks(timer.delta);
                if (renderer.displayActive && !Display.isActive()) {
                    pause();
                }

                renderer.displayActive = Display.isActive();
                int var86;
                int var81;
                if (hasMouse) {
                    var81 = 0;
                    var86 = 0;
                    if (isLevelLoaded) {
                        if (canvas != null) {
                            Point mouseLocation = canvas.getLocationOnScreen();
                            int mouseX = mouseLocation.x + width / 2;
                            int mouseY = mouseLocation.y + height / 2;
                            Point var75 = MouseInfo.getPointerInfo().getLocation();
                            var81 = var75.x - mouseX;
                            var86 = -(var75.y - mouseY);
                            robot.mouseMove(mouseX, mouseY);
                        } else {
                            Mouse.setCursorPosition(width / 2, height / 2);
                        }
                    } else {
                        var81 = Mouse.getDX();
                        var86 = Mouse.getDY();
                    }

                    byte mouseDirection = 1;
                    if (settings.invertMouse) {
                        mouseDirection = -1;
                    }

                    player.turn(var81, var86 * mouseDirection);
                }

                if (!isOnline) {
                    var81 = width * 240 / height;
                    var86 = height * 240 / height;
                    int mouseX = Mouse.getX() * var81 / width;
                    int mouseY = var86 - Mouse.getY() * var86 / height - 1;
                    if (level != null && player != null) {
                        float delta = timer.delta;
                        float var29 = player.xRotO + (player.xRot - player.xRotO) * timer.delta;
                        float var30 = player.yRotO + (player.yRot - player.yRotO) * timer.delta;
                        Vec3D var31 = renderer.getPlayerVector(timer.delta);
                        float var32 = MathHelper.cos((float) ((double) -var30 * (Math.PI / 180D) - Math.PI));
                        float var69 = MathHelper.sin((float) ((double) -var30 * (Math.PI / 180D) - Math.PI));
                        float var74 = MathHelper.cos(-var29 * (float) (Math.PI / 180D));
                        float var33 = MathHelper.sin(-var29 * (float) (Math.PI / 180D));
                        float var34 = var69 * var74;
                        float var87 = var32 * var74;
                        float reachDistance = gamemode.getReachDistance();
                        Vec3D vec3D = var31.add(var34 * reachDistance, var33 * reachDistance,
                                var87 * reachDistance);
                        selected = level.clip(var31, vec3D);
                        var74 = reachDistance;
                        if (selected != null) {
                            var74 = selected.vec.distance(
                                    renderer.getPlayerVector(timer.delta));
                        }

                        var31 = renderer.getPlayerVector(timer.delta);
                        if (isSurvival()) {
                            reachDistance = var74;
                        } else {
                            reachDistance = 32F;
                        }

                        vec3D = var31.add(var34 * reachDistance, var33 * reachDistance,
                                var87 * reachDistance);
                        renderer.entity = null;
                        List<Entity> var37 = level.blockMap.getEntities(
                                player,
                                player.boundingBox.expand(var34 * reachDistance,
                                        var33 * reachDistance, var87 * reachDistance)
                        );
                        float var35 = 0F;

                        for (int i = 0; i < var37.size(); ++i) {
                            Entity var88 = var37.get(i);
                            if (var88.isPickable()) {
                                var74 = 0.1F;
                                MovingObjectPosition var78 = var88.boundingBox.grow(var74, var74,
                                        var74).clip(var31, vec3D);
                                if (var78 != null) {
                                    var74 = var31.distance(var78.vec);
                                    if (var74 < var35 || var35 == 0F) {
                                        renderer.entity = var88;
                                        var35 = var74;
                                    }
                                }
                            }
                        }

                        if (renderer.entity != null && isSurvival()) {
                            selected = new MovingObjectPosition(renderer.entity);
                        }

                        GL11.glViewport(0, 0, width, height);
                        float viewDistanceFactor = 1F - (float) (Math.pow(
                                (1F / (4 - settings.viewDistance)), 0.25D));
                        float skyColorRed = (level.skyColor >> 16 & 255) / 255F;
                        float skyColorBlue = (level.skyColor >> 8 & 255) / 255F;
                        float skyColorGreen = (level.skyColor & 255) / 255F;
                        renderer.fogRed = (level.fogColor >> 16 & 255) / 255F;
                        renderer.fogBlue = (level.fogColor >> 8 & 255) / 255F;
                        renderer.fogGreen = (level.fogColor & 255) / 255F;
                        renderer.fogRed += (skyColorRed - renderer.fogRed) * viewDistanceFactor;
                        renderer.fogBlue += (skyColorBlue - renderer.fogBlue) * viewDistanceFactor;
                        renderer.fogGreen += (skyColorGreen - renderer.fogGreen) * viewDistanceFactor;
                        renderer.fogRed *= renderer.fogColorMultiplier;
                        renderer.fogBlue *= renderer.fogColorMultiplier;
                        renderer.fogGreen *= renderer.fogColorMultiplier;
                        Block block = Block.blocks[level.getTile((int) this.player.x,
                                (int) (this.player.y + 0.12F), (int) this.player.z)];
                        if (block != null && block.getLiquidType() != LiquidType.notLiquid) {
                            LiquidType liquidType = block.getLiquidType();
                            if (liquidType == LiquidType.water) {
                                renderer.fogRed = 0.02F;
                                renderer.fogBlue = 0.02F;
                                renderer.fogGreen = 0.2F;
                            } else if (liquidType == LiquidType.lava) {
                                renderer.fogRed = 0.6F;
                                renderer.fogBlue = 0.1F;
                                renderer.fogGreen = 0F;
                            }
                        }

                        GL11.glClearColor(renderer.fogRed, renderer.fogBlue, renderer.fogGreen, 0F);
                        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                        renderer.fogColorMultiplier = 1F;
                        GL11.glEnable(GL11.GL_CULL_FACE);
                        renderer.fogEnd = 512 >> (settings.viewDistance << 1);
                        GL11.glMatrixMode(GL11.GL_PROJECTION);
                        GL11.glLoadIdentity();
                        var29 = 0.07F;

                        var69 = 70F;
                        if (player.health <= 0) {
                            var74 = player.deathTime + delta;
                            var69 /= (1F - 500F / (var74 + 500F)) * 2F + 1F;
                        }

                        GLU.gluPerspective(var69, (float) width / (float) height, 0.05F, renderer.fogEnd);
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GL11.glLoadIdentity();

                        renderer.hurtEffect(delta);
                        renderer.applyBobbing(delta, settings.viewBobbing);

                        float cameraDistance = -5.1F;
                        if (selected != null && settings.thirdPersonMode == 2) {
                            cameraDistance = -(selected.vec.distance(renderer.getPlayerVector(timer.delta)) - 0.51F);
                            if (cameraDistance < -5.1F) {
                                cameraDistance = -5.1F;
                            }
                        }

                        if (settings.thirdPersonMode == 0) {
                            GL11.glTranslatef(0F, 0F, -0.1F);
                        } else {
                            GL11.glTranslatef(0F, 0F, cameraDistance);
                        }
                        if (settings.thirdPersonMode == 2) {
                            GL11.glRotatef(-player.xRotO + (player.xRot - player.xRotO) * delta,
                                    1F, 0F, 0F);
                            GL11.glRotatef(
                                    (player.yRotO + (player.yRot - player.yRotO) * delta) + 180,
                                    0F, 1F, 0F);
                        } else {
                            GL11.glRotatef(player.xRotO + (player.xRot - player.xRotO) * delta, 1F,
                                    0F, 0F);
                            GL11.glRotatef(player.yRotO + (player.yRot - player.yRotO) * delta, 0F,
                                    1F, 0F);
                        }
                        var69 = player.xo + (player.x - player.xo) * delta;
                        var74 = player.yo + (player.y - player.yo) * delta;
                        var33 = player.zo + (player.z - player.zo) * delta;
                        GL11.glTranslatef(-var69, -var74, -var33);
                        Frustum frustum = FrustumImpl.getInstance();

                        for (int i = 0; i < levelRenderer.chunkCache.length; ++i) {
                            levelRenderer.chunkCache[i].clip(frustum);
                        }

                        Collections.sort(levelRenderer.chunks,
                                new ChunkDirtyDistanceComparator(player));
                        int var98 = levelRenderer.chunks.size() - 1;
                        int var105 = levelRenderer.chunks.size();
                        if (var105 > 4) {
                            var105 = 4;
                        }

                        for (int i = 0; i < var105; ++i) {
                            Chunk chunkToUpdate = levelRenderer.chunks.remove(var98 - i);
                            chunkToUpdate.update();
                            chunkToUpdate.loaded = false;
                        }

                        renderer.updateFog();
                        GL11.glEnable(GL11.GL_FOG);
                        levelRenderer.sortChunks(player, 0);
                        int var83;
                        ShapeRenderer shapeRenderer = ShapeRenderer.instance;
                        int var120;
                        if (level.isSolid(player.x, player.y, player.z, 0.1F)) {
                            int playerX = (int) player.x;
                            int playerY = (int) player.y;
                            int playerZ = (int) player.z;

                            for (int x = playerX - 1; x <= playerX + 1; ++x) {
                                for (int y = playerY - 1; y <= playerY + 1; ++y) {
                                    for (int z = playerZ - 1; z <= playerZ + 1; ++z) {
                                        int var104 = levelRenderer.level.getTile(x, y, z);
                                        if (var104 != 0 && Block.blocks[var104].isSolid()) {
                                            GL11.glColor4f(0.2F, 0.2F, 0.2F, 1F);
                                            GL11.glDepthFunc(513);

                                            shapeRenderer.begin();

                                            for (int side = 0; side < 6; ++side) {
                                                Block.blocks[var104].renderInside(shapeRenderer,
                                                        x, y, z, side);
                                            }

                                            shapeRenderer.end();
                                            GL11.glCullFace(1028);
                                            shapeRenderer.begin();

                                            for (int side = 0; side < 6; ++side) {
                                                Block.blocks[var104].renderInside(shapeRenderer,
                                                        x, y, z, side);
                                            }

                                            shapeRenderer.end();
                                            GL11.glCullFace(1029);
                                            GL11.glDepthFunc(515);
                                        }
                                    }
                                }
                            }
                        }

                        renderer.setLighting(true);
                        Vec3D var103 = renderer.getPlayerVector(delta);
                        levelRenderer.level.blockMap.render(var103, frustum, levelRenderer.textureManager, delta);
                        renderer.setLighting(false);
                        renderer.updateFog();
                        var29 = -MathHelper.cos(player.yRot * (float) Math.PI / 180F);
                        float var117 = -(var30 = -MathHelper.sin(player.yRot * (float) Math.PI / 180F))
                                * MathHelper.sin(player.xRot * (float) Math.PI / 180F);
                        var32 = var29 * MathHelper.sin(player.xRot * (float) Math.PI / 180F);
                        var69 = MathHelper.cos(player.xRot * (float) Math.PI / 180F);

                        for (var83 = 0; var83 < 2; ++var83) {
                            if (!particleManager.particles[var83].isEmpty()) {
                                int textureId = 0;
                                if (var83 == 0) {
                                    textureId = particleManager.textureManager.load("/particles.png");
                                }

                                if (var83 == 1) {
                                    textureId = particleManager.textureManager.load("/terrain.png");
                                }

                                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
                                shapeRenderer.begin();

                                for (int i = 0; i < particleManager.particles[var83].size(); ++i) {
                                    ((Particle) particleManager.particles[var83].get(i)).render(
                                            shapeRenderer, delta, var29, var69, var30, var117,
                                            var32);
                                }

                                shapeRenderer.end();
                            }
                        }

                        GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                                levelRenderer.textureManager.load("/rock.png"));
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        GL11.glCallList(levelRenderer.listId); // rock
                        // edges
                        renderer.updateFog();

                        GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                                levelRenderer.textureManager.load("/clouds.png"));
                        GL11.glColor4f(1F, 1F, 1F, 1F);
                        float cloudColorRed = (levelRenderer.level.cloudColor >> 16 & 255) / 255F;
                        float cloudColorBlue = (levelRenderer.level.cloudColor >> 8 & 255) / 255F;
                        float cloudColorGreen = (levelRenderer.level.cloudColor & 255) / 255F;

                        if (level.cloudLevel < 0) {
                            level.cloudLevel = levelRenderer.level.height + 2;
                        }
                        int cloudLevel = level.cloudLevel;

                        float unknownCloud = 1F / 2048F;
                        float cloudTickOffset = (levelRenderer.ticks + delta) * unknownCloud * 0.03F;
                        if (settings.showClouds) {
                            shapeRenderer.begin();
                            shapeRenderer.color(cloudColorRed, cloudColorBlue, cloudColorGreen);
                            //shapeRenderer.color(0, 0, 0);
                            for (int x = -2048; x < levelRenderer.level.width + 2048; x += 512) {
                                for (int y = -2048; y < levelRenderer.level.length + 2048; y += 512) {
                                    shapeRenderer.vertexUV(x, cloudLevel, y + 512,
                                            x * unknownCloud + cloudTickOffset,
                                            (y + 512) * unknownCloud
                                    );
                                    shapeRenderer.vertexUV(x + 512, cloudLevel, y + 512,
                                            (x + 512) * unknownCloud + cloudTickOffset,
                                            (y + 512) * unknownCloud
                                    );
                                    shapeRenderer.vertexUV(x + 512, cloudLevel, y,
                                            (x + 512) * unknownCloud + cloudTickOffset,
                                            y * unknownCloud
                                    );
                                    shapeRenderer.vertexUV(x, cloudLevel, y,
                                            x * unknownCloud + cloudTickOffset, y * unknownCloud
                                    );
                                    shapeRenderer.vertexUV(x, cloudLevel, y,
                                            x * unknownCloud + cloudTickOffset, y * unknownCloud
                                    );
                                    shapeRenderer.vertexUV(x + 512, cloudLevel, y,
                                            (x + 512) * unknownCloud + cloudTickOffset,
                                            y * unknownCloud
                                    );
                                    shapeRenderer.vertexUV(x + 512, cloudLevel, y + 512,
                                            (x + 512) * unknownCloud + cloudTickOffset,
                                            (y + 512) * unknownCloud
                                    );
                                    shapeRenderer.vertexUV(x, cloudLevel, y + 512,
                                            x * unknownCloud + cloudTickOffset,
                                            (y + 512) * unknownCloud
                                    );
                                }
                            }

                            shapeRenderer.end();
                        }
                        GL11.glDisable(GL11.GL_TEXTURE_2D);

                        shapeRenderer.begin();
                        shapeRenderer.color(skyColorRed, skyColorBlue, skyColorGreen);
                        int levelHeight = levelRenderer.level.height + 10;
                        if (player.y > levelRenderer.level.height) {
                            levelHeight = (int) (player.y + 10);
                        }

                        for (int x = -2048; x < levelRenderer.level.width + 2048; x += 512) {
                            for (int y = -2048; y < levelRenderer.level.length + 2048; y += 512) {
                                shapeRenderer.vertex(x, levelHeight, y);
                                shapeRenderer.vertex(x + 512, levelHeight, y);
                                shapeRenderer.vertex(x + 512, levelHeight, y + 512);
                                shapeRenderer.vertex(x, levelHeight, y + 512);
                            }
                        }

                        shapeRenderer.end();
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        renderer.updateFog();
                        if (selected != null) {
                            GL11.glDisable(GL11.GL_ALPHA_TEST);
                            var105 = player.inventory.getSelected();
                            MovingObjectPosition var102 = selected;

                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glEnable(GL11.GL_ALPHA_TEST);
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                            GL11.glColor4f(1F, 1F, 1F,
                                    (MathHelper.sin(System.currentTimeMillis() / 100F) * 0.2F + 0.4F) * 0.5F);
                            if (levelRenderer.cracks > 0F) {
                                GL11.glBlendFunc(774, 768);
                                GL11.glBindTexture(GL11.GL_TEXTURE_2D, levelRenderer.textureManager.load("/terrain.png"));
                                GL11.glColor4f(1F, 1F, 1F, 0.5F);
                                GL11.glPushMatrix();
                                
                                int blockId = levelRenderer.level.getTile(var102.x, var102.y, var102.z);
                                block = (blockId > 0 ? Block.blocks[blockId] : null);
                                float blockXAverage = (block.maxX + block.minX) / 2F;
                                float blockYAverage = (block.maxY + block.minY) / 2F;
                                float blockZAverage = (block.maxZ + block.minZ) / 2F;
                                GL11.glTranslatef(var102.x + blockXAverage,
                                        var102.y + blockYAverage, var102.z + blockZAverage);
                                GL11.glScalef(1F, 1.01F, 1.01F);
                                GL11.glTranslatef(-(var102.x + blockXAverage),
                                        -(var102.y + blockYAverage), -(var102.z + blockZAverage));
                                shapeRenderer.begin();
                                shapeRenderer.noColor();
                                GL11.glDepthMask(false);
                                // Do the sides
                                for (int side = 0; side < 6; ++side) {
                                    block.renderSide(shapeRenderer, var102.x, var102.y, var102.z,
                                            side, 240 + (int) (levelRenderer.cracks * 10F));
                                }

                                shapeRenderer.end();
                                GL11.glDepthMask(true);
                                GL11.glPopMatrix();
                            }

                            GL11.glDisable(GL11.GL_BLEND);
                            GL11.glDisable(GL11.GL_ALPHA_TEST);
                            // TODO ???
                            player.inventory.getSelected();
                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            GL11.glColor4f(0F, 0F, 0F, 0.4F);
                            GL11.glLineWidth(2F);
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            GL11.glDepthMask(false);
                            int var104 = levelRenderer.level.getTile(selected.x, selected.y, selected.z);
                            if (var104 > 0) {
                                AABB aabb = Block.blocks[var104].getSelectionBox(
                                        selected.x, selected.y, selected.z).grow(
                                                0.002F, 0.002F, 0.002F);
                                GL11.glBegin(GL11.GL_LINE_STRIP);
                                GL11.glVertex3f(aabb.maxX, aabb.maxY, aabb.maxZ);
                                GL11.glVertex3f(aabb.minX, aabb.maxY, aabb.maxZ);
                                GL11.glVertex3f(aabb.minX, aabb.maxY, aabb.minZ);
                                GL11.glVertex3f(aabb.maxX, aabb.maxY, aabb.minZ);
                                GL11.glVertex3f(aabb.maxX, aabb.maxY, aabb.maxZ);
                                GL11.glEnd();
                                GL11.glBegin(GL11.GL_LINE_STRIP);
                                GL11.glVertex3f(aabb.maxX, aabb.minY, aabb.maxZ);
                                GL11.glVertex3f(aabb.minX, aabb.minY, aabb.maxZ);
                                GL11.glVertex3f(aabb.minX, aabb.minY, aabb.minZ);
                                GL11.glVertex3f(aabb.maxX, aabb.minY, aabb.minZ);
                                GL11.glVertex3f(aabb.maxX, aabb.minY, aabb.maxZ);
                                GL11.glEnd();
                                GL11.glBegin(GL11.GL_LINES);
                                GL11.glVertex3f(aabb.maxX, aabb.maxY, aabb.maxZ);
                                GL11.glVertex3f(aabb.maxX, aabb.minY, aabb.maxZ);
                                GL11.glVertex3f(aabb.minX, aabb.maxY, aabb.maxZ);
                                GL11.glVertex3f(aabb.minX, aabb.minY, aabb.maxZ);
                                GL11.glVertex3f(aabb.minX, aabb.maxY, aabb.minZ);
                                GL11.glVertex3f(aabb.minX, aabb.minY, aabb.minZ);
                                GL11.glVertex3f(aabb.maxX, aabb.maxY, aabb.minZ);
                                GL11.glVertex3f(aabb.maxX, aabb.minY, aabb.minZ);
                                GL11.glEnd();
                            }

                            GL11.glDepthMask(true);
                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                            GL11.glDisable(GL11.GL_BLEND);
                            GL11.glEnable(GL11.GL_ALPHA_TEST);
                        }

                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        renderer.updateFog();
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                                levelRenderer.textureManager.load("/water.png"));

                        GL11.glCallList(levelRenderer.listId + 1);
                        GL11.glEnable(GL11.GL_BLEND);

                        levelRenderer.sortChunks(player, 1);

                        GL11.glDepthMask(true);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glDisable(GL11.GL_FOG);
                        // -------------------

                        Collections.sort(selectionBoxes, new SelectionBoxDistanceComparator(this.player));
                        for (int i = 0; i < selectionBoxes.size(); i++) {
                            CustomAABB bounds = selectionBoxes.get(i).bounds;
                            ColorCache color = selectionBoxes.get(i).color;
                            GL11.glLineWidth(2);

                            GL11.glDisable(GL11.GL_BLEND);
                            GL11.glDisable(GL11.GL_ALPHA_TEST);
                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            GL11.glColor4f(color.R, color.G, color.B, color.A);
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            GL11.glDepthMask(false);
                            GL11.glDisable(GL11.GL_CULL_FACE);
                            // GL11.glBegin(GL11.GL_QUADS);

                            // Front Face
                            // Bottom Left
                            shapeRenderer.begin();
                            shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.minZ);
                            // Bottom Right
                            shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.minZ);
                            // Top Right
                            shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.minZ);
                            // Top Left
                            shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.minZ);

                            // Back Face
                            // Bottom Right
                            shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ);
                            // Top Right
                            shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.maxZ);
                            // Top Left
                            shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.maxZ);
                            // Bottom Left
                            shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.maxZ);

                            // Top Face
                            // Top Left
                            // Bottom Left
                            shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.maxZ);
                            shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.minZ);
                            // Bottom Right
                            shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.minZ);
                            // Top Right
                            shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.maxZ);

                            // Bottom Face
                            // Top Right
                            shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ);
                            // Top Left
                            shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.maxZ);
                            // Bottom Left
                            shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.minZ);
                            // Bottom Right
                            shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.minZ);

                            // Right face
                            // Bottom Right
                            shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.maxZ);
                            // Top Right
                            shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.maxZ);
                            // Top Left
                            shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.minZ);
                            // Bottom Left
                            shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.minZ);

                            // Left Face
                            // Bottom Left
                            shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ);
                            // Bottom Right
                            shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.minZ);
                            // Top Right
                            shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.minZ);
                            // Top Left
                            shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.maxZ);
                            shapeRenderer.end();

                            GL11.glColor4f(color.R, color.G, color.B, color.A + 0.2F);

                            shapeRenderer.startDrawing(3);
                            shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ);
                            shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.maxZ);
                            shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.minZ);
                            shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.minZ);
                            shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ);
                            shapeRenderer.end();

                            shapeRenderer.startDrawing(3);
                            shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.maxZ);
                            shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.maxZ);
                            shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.minZ);
                            shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.minZ);
                            shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.maxZ);
                            shapeRenderer.end();

                            shapeRenderer.startDrawing(1);
                            shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.maxZ);
                            shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.maxZ);
                            shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.maxZ);
                            shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.maxZ);
                            shapeRenderer.vertex(bounds.minX, bounds.maxY, bounds.minZ);
                            shapeRenderer.vertex(bounds.minX, bounds.minY, bounds.minZ);
                            shapeRenderer.vertex(bounds.maxX, bounds.maxY, bounds.minZ);
                            shapeRenderer.vertex(bounds.maxX, bounds.minY, bounds.minZ);
                            shapeRenderer.end();

                            GL11.glDepthMask(true);
                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                            GL11.glDisable(GL11.GL_BLEND);
                            GL11.glEnable(GL11.GL_ALPHA_TEST);

                            GL11.glEnable(GL11.GL_CULL_FACE);

                            // ------------------
                        }

                        if (isRaining || isSnowing) {
                            float speed = 1F;
                            int playerX = (int) this.player.x;
                            int playerY = (int) this.player.y;
                            int playerZ = (int) this.player.z;
                            GL11.glDisable(GL11.GL_CULL_FACE);
                            GL11.glNormal3f(0F, 1F, 0F);
                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            if (isRaining) {
                                GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                                        textureManager.load("/rain.png"));
                            } else if (isSnowing) {
                                GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                                        textureManager.load("/snow.png"));
                                speed = 0.2F;
                            }

                            for (int x = playerX - 5; x <= playerX + 5; ++x) {
                                for (int z = playerZ - 5; z <= playerZ + 5; ++z) {
                                    var120 = level.getHighestTile(x, z);
                                    var86 = playerY - 5;
                                    int abovePlayerY = playerY + 5;
                                    if (var86 < var120) {
                                        var86 = var120;
                                    }

                                    if (abovePlayerY < var120) {
                                        abovePlayerY = var120;
                                    }

                                    if (var86 != abovePlayerY) {
                                        var74 = ((renderer.levelTicks + x * 3121 + z * 418711) % 32 + delta)
                                                / 32F * speed;
                                        float var124 = x + 0.5F - this.player.x;
                                        var35 = z + 0.5F - this.player.z;
                                        float var92 = MathHelper.sqrt(var124 * var124 + var35
                                                * var35) / 5;
                                        GL11.glColor4f(1F, 1F, 1F, (1F - var92 * var92) * 0.7F);
                                        shapeRenderer.begin();
                                        shapeRenderer.vertexUV(x, var86, z, 0F, var86
                                                * 2F / 8F + var74 * 2F);
                                        shapeRenderer.vertexUV(x + 1, var86, z + 1, 2F,
                                                var86 * 2F / 8F + var74 * 2F);
                                        shapeRenderer.vertexUV(x + 1, abovePlayerY, z + 1, 2F,
                                                abovePlayerY * 2F / 8F + var74 * 2F);
                                        shapeRenderer.vertexUV(x, abovePlayerY, z, 0F, abovePlayerY
                                                * 2F / 8F + var74 * 2F);
                                        shapeRenderer.vertexUV(x, var86, z + 1, 0F, var86
                                                * 2F / 8F + var74 * 2F);
                                        shapeRenderer.vertexUV(x + 1, var86, z, 2F, var86
                                                * 2F / 8F + var74 * 2F);
                                        shapeRenderer.vertexUV(x + 1, abovePlayerY, z, 2F,
                                                abovePlayerY * 2F / 8F + var74 * 2F);
                                        shapeRenderer.vertexUV(x, abovePlayerY, z + 1, 0F,
                                                abovePlayerY * 2F / 8F + var74 * 2F);
                                        shapeRenderer.end();
                                    }
                                }
                            }

                            GL11.glEnable(GL11.GL_CULL_FACE);
                            GL11.glDisable(GL11.GL_BLEND);
                        }
                        if (!isSinglePlayer && networkManager != null
                                && networkManager.players != null
                                && networkManager.players.size() > 0) {
                            if ((settings.ShowNames == 2 || settings.ShowNames == 3)
                                    && this.player.userType >= 100) {
                                for (int n = 0; n < networkManager.players.values().size(); n++) {
                                    NetworkPlayer np = (NetworkPlayer) networkManager.players
                                            .values().toArray()[n];
                                    if (np != null) {
                                        np.renderHover(textureManager);
                                    }
                                }
                            } else {
                                if (renderer.entity != null) {
                                    renderer.entity.renderHover(textureManager);
                                }
                            }
                        }

                        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
                        GL11.glLoadIdentity();

                        renderer.hurtEffect(delta);
                        renderer.applyBobbing(delta, settings.viewBobbing);

                        HeldBlock heldBlock = renderer.heldBlock;
                        var117 = renderer.heldBlock.lastPos + (heldBlock.pos - heldBlock.lastPos)
                                * delta;
                        player = heldBlock.minecraft.player;
                        GL11.glPushMatrix();
                        GL11.glRotatef(player.xRotO + (player.xRot - player.xRotO) * delta, 1F, 0F,
                                0F);
                        GL11.glRotatef(player.yRotO + (player.yRot - player.yRotO) * delta, 0F, 1F,
                                0F);
                        heldBlock.minecraft.renderer.setLighting(true);
                        GL11.glPopMatrix();
                        GL11.glPushMatrix();
                        var69 = 0.8F;
                        if (heldBlock.moving) {
                            var33 = MathHelper
                                    .sin((var74 = (heldBlock.offset + delta) / 7F) * (float) Math.PI);
                            GL11.glTranslatef(
                                    -MathHelper.sin(MathHelper.sqrt(var74) * (float) Math.PI) * 0.4F,
                                    MathHelper.sin(MathHelper.sqrt(var74) * (float) Math.PI * 2F) * 0.2F,
                                    -var33 * 0.2F);
                        }

                        GL11.glTranslatef(0.7F * var69, -0.65F * var69 - (1F - var117) * 0.6F,
                                -0.9F * var69);
                        GL11.glRotatef(45F, 0F, 1F, 0F);
                        GL11.glEnable(GL11.GL_NORMALIZE);
                        if (heldBlock.moving) {
                            var33 = MathHelper.sin((var74 = (heldBlock.offset + delta) / 7F)
                                    * var74 * (float) Math.PI);
                            GL11.glRotatef(
                                    MathHelper.sin(MathHelper.sqrt(var74) * (float) Math.PI) * 80F, 0F,
                                    1F, 0F);
                            GL11.glRotatef(-var33 * 20F, 1F, 0F, 0F);
                        }

                        ColorCache color = heldBlock.minecraft.level.getBrightnessColor(
                                (int) player.x, (int) player.y, (int) player.z);
                        GL11.glColor4f(color.R, color.G, color.B, 1F);

                        if (heldBlock.block != null) {
                            GL11.glScalef(0.4F, 0.4F, 0.4F);
                            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                            if (settings.thirdPersonMode == 0 && canRenderGUI) {
                                GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                                        heldBlock.minecraft.textureManager.load("/terrain.png"));
                                heldBlock.block.renderPreview(shapeRenderer);
                            }
                        } else {
                            player.bindTexture(heldBlock.minecraft.textureManager);
                            GL11.glScalef(1F, -1F, -1F);
                            GL11.glTranslatef(0F, 0.2F, 0F);
                            GL11.glRotatef(-120F, 0F, 0F, 1F);
                            ModelPart leftArm = heldBlock.minecraft.player.getModel().leftArm;
                            if (!leftArm.hasList) {
                                leftArm.generateList(0.0625F); // 1/16
                            }

                            GL11.glCallList(leftArm.list);
                        }

                        GL11.glDisable(GL11.GL_NORMALIZE);
                        GL11.glPopMatrix();
                        heldBlock.minecraft.renderer.setLighting(false);

                        hud.render(timer.delta,
                                currentScreen != null, mouseX, mouseY);
                    } else {
                        GL11.glViewport(0, 0, width, renderer.minecraft.height);
                        GL11.glClearColor(0F, 0F, 0F, 0F);
                        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                        GL11.glMatrixMode(GL11.GL_PROJECTION);
                        GL11.glLoadIdentity();
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GL11.glLoadIdentity();
                        renderer.enableGuiMode();
                    }

                    if (renderer.minecraft.currentScreen != null) {
                        renderer.minecraft.currentScreen.render(mouseX, mouseY);
                    }

                    Thread.yield();
                    Display.update();
                }
            }

            if (settings.limitFramerate) {
                Display.sync(60);
            }

            checkGLError("Post render");
        } catch (Exception ex) {
            LogUtil.logError("Fatal error in main loop (onFrame)", ex);
            setCurrentScreen(new ErrorScreen("Client error", "The game broke! [" + ex + "]"));
        }
    }

    public final void setCurrentScreen(GuiScreen newScreen) {
        if (currentScreen != null) {
            currentScreen.onClose();
        }

        if (newScreen == null && player.health <= 0) {
            newScreen = new GameOverScreen();
        }

        currentScreen = newScreen;
        if (newScreen != null) {
            if (hasMouse) {
                player.releaseAllKeys();
                hasMouse = false;
                if (isLevelLoaded) {
                    try {
                        Mouse.setNativeCursor(null);
                    } catch (LWJGLException ex) {
                        LogUtil.logError("Error showing the mouse cursor.", ex);
                    }
                } else {
                    Mouse.setGrabbed(false);
                }
            }

            int var2 = width * 240 / height;
            int var3 = height * 240 / height;
            newScreen.open(this, var2, var3);
            isOnline = false;
            return;
        }
        grabMouse();
    }

    private void setDisplayMode() throws LWJGLException {
        DisplayMode desktopMode = Display.getDesktopDisplayMode();
        Display.setDisplayMode(desktopMode);
        width = desktopMode.getWidth();
        height = desktopMode.getHeight();
    }

    public final void setLevel(Level theLevel) {
        if (applet == null || !applet.getDocumentBase().getHost().equalsIgnoreCase("minecraft.net")
                && !applet.getDocumentBase().getHost().equalsIgnoreCase("www.minecraft.net")
                || !applet.getCodeBase().getHost().equalsIgnoreCase("minecraft.net")
                && !applet.getCodeBase().getHost().equalsIgnoreCase("www.minecraft.net")) {
            theLevel = null;
        }

        level = theLevel;
        if (player != null && player.inventory != null) {
            inventoryCache = player.inventory.slots.clone();
        }
        if (theLevel != null) {
            theLevel.initTransient();
            gamemode.apply(theLevel);
            theLevel.font = fontRenderer;
            theLevel.rendererContext = this;
            if (!isOnline()) { // if not online (singleplayer)
                player = (Player) theLevel.findSubclassOf(Player.class);
                if (player == null) {
                    player = new Player(theLevel, settings);
                }
                player.settings = settings;
                player.resetPos();
            } else if (player != null) { // if online
                player.resetPos();
                gamemode.preparePlayer(player);
                theLevel.player = player;
                theLevel.addEntity(player);
            }
        }

        if (player == null) {
            player = new Player(theLevel, settings);
            player.resetPos();
            gamemode.preparePlayer(player);
            if (theLevel != null) {
                theLevel.player = player;
            }
        }

        if (player != null) {
            player.input = new InputHandlerImpl(settings, player);
            gamemode.apply(player);
        }

        if (levelRenderer != null) {
            LevelRenderer var3 = levelRenderer;
            if (levelRenderer.level != null) {
                var3.level.removeListener(var3);
            }

            var3.level = theLevel;
            if (theLevel != null) {
                theLevel.addListener(var3);
                var3.refresh();
            }
        }

        if (particleManager != null) {
            ParticleManager var5 = particleManager;
            if (theLevel != null) {
                theLevel.particleEngine = var5;
            }

            for (int var4 = 0; var4 < 2; ++var4) {
                var5.particles[var4].clear();
            }
        }

        if (inventoryCache != null) {
            player.inventory.slots = inventoryCache;
        }

        System.gc();
    }

    public final void shutdown() {
        if (isShuttingDown) {
            return;
        }
        isShuttingDown = true;
        try {
            if (soundPlayer != null) {
                soundPlayer.running = false;
            }

            if (resourceThread != null) {
                resourceThread.running = true;
            }
        } catch (Exception ex) {
            LogUtil.logError("Error shutting down threads.", ex);
        }

        if (!isLevelLoaded) {
            try {
                if (level != null && isSinglePlayer) {
                    if (level.creativeMode) {
                        new LevelSerializer(level).saveMap("levelc");
                    } else {
                        new LevelSerializer(level).saveMap("levels");
                    }
                }
            } catch (Exception ex) {
                LogUtil.logError("Error saving single-player level.", ex);
            }
        }

        Mouse.destroy();
        Keyboard.destroy();
    }

    public void takeAndSaveScreenshot(int width, int height) {
        try {
            if (isLoadingMap) {
                // Ignore attempts to screenshot while we're still connecting
                return;
            }
            int size = width * height * 3;

            int packAlignment = GL11.glGetInteger(GL11.GL_PACK_ALIGNMENT);
            int unpackAlignment = GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT);
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1); // Byte alignment.
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

            GL11.glReadBuffer(GL11.GL_FRONT);
            ByteBuffer buffer = ByteBuffer.allocateDirect(size);
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);

            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, packAlignment);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, unpackAlignment);

            byte[] pixels = new byte[size];
            buffer.get(pixels);
            pixels = flipPixels(pixels, width, height);

            ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            int[] bitsPerPixel = {8, 8, 8};
            int[] colOffsets = {0, 1, 2};

            ComponentColorModel colorComp = new ComponentColorModel(colorSpace, bitsPerPixel,
                    false, false, 3, DataBuffer.TYPE_BYTE);

            WritableRaster raster = Raster.createInterleavedRaster(new DataBufferByte(pixels,
                    pixels.length), width, height, width * 3, 3, colOffsets, null);

            BufferedImage image = new BufferedImage(colorComp, raster, false, null);

            Calendar cal = Calendar.getInstance();
            String str = String.format("screenshot_%1$tY%1$tm%1$td%1$tH%1$tM%1$tS.png", cal);

            String month = new SimpleDateFormat("MMM").format(cal.getTime());
            String serverName = ProgressBarDisplay.title.toLowerCase().contains("connecting..") ? ""
                    : ProgressBarDisplay.title;
            if (isSinglePlayer) {
                serverName = "Singleplayer";
            }
            serverName = FontRenderer.stripColor(serverName);
            serverName = serverName.replaceAll("[^A-Za-z0-9\\._-]+", "_");
            File logDir = new File(Minecraft.getMinecraftDirectory(), "/Screenshots/");
            File serverDir = new File(logDir, serverName);
            File monthDir = new File(serverDir, "/" + month + "/");
            monthDir.mkdirs();
            if (ImageIO.write(image, "png", new File(monthDir, str))) {
                hud.addChat("&2Screenshot saved into the Screenshots folder");
            }
        } catch (Exception ex) {
            LogUtil.logError("Error taking a screenshot.", ex);
        }
    }

    private void tick() {
        if (soundPlayer != null) {
            SoundPlayer var1 = soundPlayer;
            SoundManager var2 = sound;
            if (System.currentTimeMillis() > var2.lastMusic && var2.playMusic(var1, "calm")) {
                var2.lastMusic = System.currentTimeMillis() + var2.random.nextInt(900000) + 300000L;
            }
        }

        gamemode.spawnMob();
        int i;
        if (canRenderGUI) {
            ++this.hud.ticks;

            for (i = 0; i < this.hud.chat.size(); ++i) {
                ++this.hud.chat.get(i).time;
            }
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.load("/terrain.png"));
        TextureManager texManager = textureManager;

        for (i = 0; i < texManager.animations.size(); ++i) {
            TextureFX texFX = texManager.animations.get(i);
            texFX.animate();
            if (texManager.textureBuffer.capacity() != texFX.textureData.length) {
                texManager.textureBuffer = BufferUtils.createByteBuffer(texFX.textureData.length);
            } else {
                texManager.textureBuffer.clear();
            }
            texManager.textureBuffer.put(texFX.textureData);
            texManager.textureBuffer.position(0).limit(texFX.textureData.length);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, texFX.textureId % 16 << 4,
                    texFX.textureId / 16 << 4, 16, 16, 6408, 5121, texManager.textureBuffer);
        }

        if (networkManager != null && !(currentScreen instanceof ErrorScreen)) {
            if (!networkManager.isConnected()) {
                progressBar.setTitle("Connecting..");
                progressBar.setProgress(0);
                isLoadingMap = true;
            } else {
                NetworkManager var20 = networkManager;
                if (networkManager.successful) {
                    if (var20.netHandler.connected) {
                        try {
                            NetworkHandler networkHandler = var20.netHandler;
                            var20.netHandler.channel.read(networkHandler.in);
                            for( int packetsReceived = 0;
                                    networkHandler.in.position() > 0 && packetsReceived < 100;
                                    packetsReceived++ ){
                                networkHandler.in.flip();
                                byte id = networkHandler.in.get(0);
                                PacketType packetType;
                                if ((packetType = PacketType.packets[id]) == null) {
                                    throw new IOException("Bad command: " + id);
                                }
                                if (networkHandler.in.remaining() < packetType.length + 1) {
                                    networkHandler.in.compact();
                                    break;
                                }
                                networkHandler.in.get();
                                Object[] packetParams = new Object[packetType.params.length];

                                for (i = 0; i < packetParams.length; ++i) {
                                    packetParams[i] = networkHandler
                                            .readObject(packetType.params[i]);
                                }

                                NetworkManager networkManager = networkHandler.netManager;
                                if (networkHandler.netManager.successful) {
                                    if (packetType == PacketType.EXT_INFO) {
                                        String AppName = (String) packetParams[0];
                                        short ExtensionCount = (Short) packetParams[1];
                                        LogUtil.logInfo("Connecting to AppName: " + AppName
                                                + " with extension count: " + ExtensionCount);
                                        receivedExtensionLength = ExtensionCount;
                                        Constants.SERVER_SUPPORTED_EXTENSIONS.clear();
                                    } else if (packetType == PacketType.EXT_ENTRY) {
                                        String ExtName = (String) packetParams[0];
                                        Integer Version = (Integer) packetParams[1];
                                        Constants.SERVER_SUPPORTED_EXTENSIONS.add(new ExtData(
                                                ExtName, Version));

                                        if (ExtName.toLowerCase().contains("heldblock")) {
                                            canSendHeldBlock = true;
                                        }
                                        if (ExtName.toLowerCase().contains("messagetypes")) {
                                            serverSupportsMessages = true;
                                        }

                                        if (receivedExtensionLength == Constants.SERVER_SUPPORTED_EXTENSIONS
                                                .size()) {
                                            LogUtil.logInfo("Sending client's supported Extensions");
                                            List<ExtData> temp = new ArrayList<>();
                                            for (int j = 0; j < PacketType.packets.length - 1; j++) {
                                                if (PacketType.packets[j] != null
                                                        && !PacketType.packets[j].extName.equals("")) {
                                                    temp.add(new ExtData(
                                                            PacketType.packets[j].extName,
                                                            PacketType.packets[j].Version));
                                                }
                                            }
                                            String AppName = "ClassiCube Client";
                                            Object[] toSendParams = new Object[]{AppName,
                                                (short) temp.size()};
                                            networkManager.netHandler.send(PacketType.EXT_INFO, toSendParams);
                                            for (ExtData aTemp : temp) {
                                                LogUtil.logInfo("Sending ext: " + aTemp.Name
                                                        + " with version: " + aTemp.Version);
                                                toSendParams = new Object[]{aTemp.Name,
                                                    aTemp.Version};
                                                networkManager.netHandler.send(
                                                        PacketType.EXT_ENTRY, toSendParams);
                                            }
                                        }
                                    } else if (packetType == PacketType.SELECTION_CUBOID) {
                                        byte ID = (Byte) packetParams[0];
                                        String Name = (String) packetParams[1];
                                        Short X1 = (Short) packetParams[2];
                                        Short Y1 = (Short) packetParams[3];
                                        Short Z1 = (Short) packetParams[4];
                                        Short X2 = (Short) packetParams[5];
                                        Short Y2 = (Short) packetParams[6];
                                        Short Z2 = (Short) packetParams[7];
                                        Short r = (Short) packetParams[8];
                                        Short g = (Short) packetParams[9];
                                        Short b = (Short) packetParams[10];
                                        Short a = (Short) packetParams[11];

                                        // LogUtil.logInfo(ID + " " + Name +
                                        // " " + X1 + " " + Y1
                                        // + " " + Z1 + " " + X2 + " " + Y2 +
                                        // " " + Z2);
                                        SelectionBoxData data = new SelectionBoxData(ID, Name,
                                                new ColorCache(r / 255F, g / 255F, b / 255F, a / 255F),
                                                new CustomAABB(X1, Y1, Z1, X2, Y2, Z2)
                                        );
                                        selectionBoxes.add(data);
                                    } else if (packetType == PacketType.REMOVE_SELECTION_CUBOID) {
                                        byte ID = (Byte) packetParams[0];
                                        List<SelectionBoxData> cache = selectionBoxes;
                                        for (int q = 0; q < selectionBoxes.size(); q++) {
                                            if (selectionBoxes.get(q).id == ID) {
                                                cache.remove(q);
                                            }
                                        }
                                        selectionBoxes = cache;
                                    } else if (packetType == PacketType.ENV_SET_COLOR) {
                                        byte Variable = (Byte) packetParams[0];
                                        Short r = (Short) packetParams[1];
                                        Short g = (Short) packetParams[2];
                                        Short b = (Short) packetParams[3];
                                        int dec = (r & 0x0ff) << 16 | (g & 0x0ff) << 8 | b & 0x0ff;
                                        switch (Variable) {
                                            case 0: // sky
                                                level.skyColor = dec;
                                                break;
                                            case 1: // cloud
                                                level.cloudColor = dec;
                                                break;
                                            case 2: // fog
                                                level.fogColor = dec;
                                                break;
                                            case 3: // ambient light
                                                level.customShadowColour = new ColorCache(r / 255F,
                                                        g / 255F, b / 255F);
                                                levelRenderer.refresh();
                                                break;
                                            case 4: // diffuse color
                                                level.customLightColour = new ColorCache(r / 255F,
                                                        g / 255F, b / 255F);
                                                levelRenderer.refresh();
                                                break;
                                        }
                                    } else if (packetType == PacketType.ENV_SET_MAP_APPEARANCE) {
                                        String textureUrl = (String) packetParams[0];
                                        byte sideBlock = (Byte) packetParams[1];
                                        byte edgeBlock = (Byte) packetParams[2];
                                        short sideLevel = (Short) packetParams[3];

                                        if (settings.canServerChangeTextures) {
                                            if (sideBlock == -1) {
                                                textureManager.customSideBlock = null;
                                            } else if (sideBlock < Block.blocks.length) {
                                                int ID = Block.blocks[sideBlock].textureId;
                                                textureManager.customSideBlock = textureManager.textureAtlas
                                                        .get(ID);
                                            }
                                            if (edgeBlock == -1) {
                                                textureManager.customEdgeBlock = null;
                                            } else if (edgeBlock < Block.blocks.length) {
                                                Block block = Block.blocks[edgeBlock];
                                                int ID = block.getTextureId(TextureSide.Top);
                                                textureManager.customEdgeBlock = textureManager.textureAtlas
                                                        .get(ID);
                                            }
                                            if (textureUrl.length() > 0) {
                                                File path = new File(getMinecraftDirectory(),
                                                        "/skins/terrain");
                                                if (!path.exists()) {
                                                    path.mkdirs();
                                                }
                                                String hash = getHash(textureUrl);
                                                if (hash != null) {
                                                    File file = new File(path, hash + ".png");
                                                    BufferedImage image;
                                                    if (!file.exists()) {
                                                        downloadImage(new URL(textureUrl), file);
                                                    }
                                                    image = ImageIO.read(file);
                                                    if (image.getWidth() % 16 == 0
                                                            && image.getHeight() % 16 == 0) {
                                                        textureManager.animations.clear();
                                                        textureManager.currentTerrainPng = image;
                                                    }
                                                }
                                            } else {
                                                try {
                                                    textureManager.currentTerrainPng = ImageIO
                                                            .read(TextureManager.class
                                                                    .getResourceAsStream("/terrain.png"));
                                                } catch (IOException ex2) {
                                                    LogUtil.logError(
                                                            "Error reading default terrain texture.",
                                                            ex2);
                                                }
                                            }
                                            level.waterLevel = sideLevel;
                                            levelRenderer.refresh();
                                        }
                                    } else if (packetType == PacketType.CLICK_DISTANCE) {
                                        short Distance = (Short) packetParams[0];
                                        gamemode.reachDistance = Distance / 32;
                                    } else if (packetType == PacketType.HOLDTHIS) {
                                        byte blockToHold = (Byte) packetParams[0];
                                        byte preventChange = (Byte) packetParams[1];
                                        boolean canPreventChange = preventChange > 0;

                                        if (canPreventChange) {
                                            GameSettings.CanReplaceSlot = false;
                                        }

                                        player.inventory.selected = 0;
                                        player.inventory.replaceSlot(Block.blocks[blockToHold]);

                                        if (!canPreventChange) {
                                            GameSettings.CanReplaceSlot = true;
                                        }
                                    } else if (packetType == PacketType.SET_TEXT_HOTKEY) {
                                        String Label = (String) packetParams[0];
                                        String Action = (String) packetParams[1];
                                        int keyCode = (Integer) packetParams[2];
                                        byte KeyMods = (Byte) packetParams[3];
                                        HotKeyData data = new HotKeyData(Label, Action, keyCode,
                                                KeyMods);
                                        hotKeys.add(data);

                                    } else if (packetType == PacketType.EXT_ADD_PLAYER_NAME) {
                                        Short NameId = (Short) packetParams[0];
                                        String playerName = (String) packetParams[1];
                                        String listName = (String) packetParams[2];
                                        String groupName = (String) packetParams[3];
                                        byte unusedRank = (Byte) packetParams[4];

                                        int playerIndex = -1;

                                        for (PlayerListNameData b : playerListNameData) {
                                            if (b.nameID == NameId) { // --
                                                // Already exists, update the
                                                // entry.
                                                playerIndex = playerListNameData.indexOf(b);
                                                break;
                                            }
                                        }

                                        if (playerIndex == -1) {
                                            playerListNameData.add(new PlayerListNameData(NameId,
                                                    playerName, listName, groupName, unusedRank));
                                        } else {
                                            playerListNameData.set(playerIndex,
                                                    new PlayerListNameData(NameId, playerName,
                                                            listName, groupName, unusedRank)
                                            );
                                        }

                                        Collections.sort(playerListNameData,
                                                new PlayerListComparator());
                                    } else if (packetType == PacketType.EXT_ADD_ENTITY) {
                                        byte playerID = (Byte) packetParams[0];
                                        String skinName = (String) packetParams[2];

                                        NetworkPlayer player = networkManager.players.get(playerID);
                                        if (player != null) {
                                            player.SkinName = skinName;
                                            player.downloadSkin();
                                        }
                                    } else if (packetType == PacketType.EXT_REMOVE_PLAYER_NAME) {
                                        Short NameId = (Short) packetParams[0];
                                        List<PlayerListNameData> cache = playerListNameData;
                                        for (int q = 0; q < playerListNameData.size(); q++) {
                                            if (playerListNameData.get(q).nameID == NameId) {
                                                cache.remove(q);
                                            }
                                        }
                                        playerListNameData = cache;
                                    } else if (packetType == PacketType.CUSTOM_BLOCK_SUPPORT_LEVEL) {
                                        LogUtil.logInfo("Custom blocks packet received");
                                        byte SupportLevel = (Byte) packetParams[0];
                                        networkManager.netHandler.send(
                                                PacketType.CUSTOM_BLOCK_SUPPORT_LEVEL,
                                                Constants.CUSTOM_BLOCK_SUPPORT_LEVEL);
                                        SessionData.setAllowedBlocks(SupportLevel);
                                    } else if (packetType == PacketType.SET_BLOCK_PERMISSIONS) {
                                        byte BlockType = (Byte) packetParams[0];
                                        byte AllowPlacement = (Byte) packetParams[1];
                                        byte AllowDeletion = (Byte) packetParams[2];
                                        Block block = Block.blocks[BlockType];
                                        if (block == null) {
                                            return;
                                        }
                                        if (AllowPlacement == 0) {
                                            if (!disallowedPlacementBlocks.contains(block)) {
                                                disallowedPlacementBlocks.add(block);
                                                LogUtil.logInfo("DisallowingPlacement block: "
                                                        + block);
                                            }
                                        } else {
                                            if (disallowedPlacementBlocks.contains(block)) {
                                                disallowedPlacementBlocks.remove(block);
                                                LogUtil.logInfo("AllowingPlacement block: " + block);
                                            }
                                        }
                                        if (AllowDeletion == 0) {
                                            if (!DisallowedBreakingBlocks.contains(block)) {
                                                DisallowedBreakingBlocks.add(block);
                                                LogUtil.logInfo("DisallowingDeletion block: "
                                                        + block);
                                            }
                                        } else {
                                            if (DisallowedBreakingBlocks.contains(block)) {
                                                DisallowedBreakingBlocks.remove(block);
                                                LogUtil.logInfo("AllowingDeletion block: " + block);
                                            }
                                        }
                                    } else if (packetType == PacketType.CHANGE_MODEL) {
                                        byte PlayerID = (Byte) packetParams[0];
                                        String ModelName = (String) packetParams[1];
                                        if (PlayerID >= 0) {
                                            NetworkPlayer netPlayer;
                                            if ((netPlayer = networkManager.players.get(Byte
                                                    .valueOf(PlayerID))) != null) {
                                                ModelManager m = new ModelManager();
                                                if (m.getModel(ModelName.toLowerCase()) == null) {
                                                    netPlayer.modelName = "humanoid";
                                                } else {
                                                    netPlayer.modelName = ModelName.toLowerCase();
                                                }
                                                netPlayer.bindTexture(textureManager);
                                            }
                                        } else if (PlayerID == -1) {
                                            Player thisPlayer = player;
                                            ModelManager m = new ModelManager();
                                            if (m.getModel(ModelName.toLowerCase()) == null) {
                                                thisPlayer.modelName = "humanoid";
                                            } else {
                                                thisPlayer.modelName = ModelName.toLowerCase();
                                            }
                                            thisPlayer.bindTexture(textureManager);
                                        }
                                    } else if (packetType == PacketType.ENV_SET_WEATHER_TYPE) {
                                        byte Weather = (Byte) packetParams[0];
                                        if (Weather == 0) {
                                            isRaining = false;
                                            isSnowing = false;
                                        } else if (Weather == 1) {
                                            isRaining = !isRaining;
                                            isSnowing = false;
                                        } else if (Weather == 2) {
                                            isSnowing = !isSnowing;
                                            isRaining = false;
                                        }
                                    } else if (packetType == PacketType.IDENTIFICATION) {
                                        networkManager.minecraft.progressBar.setTitle(packetParams[1].toString());
                                        networkManager.minecraft.player.userType = (Byte) packetParams[3];
                                        networkManager.minecraft.progressBar.setText(packetParams[2].toString());
                                    } else if (packetType == PacketType.LEVEL_INIT) {
									    selectionBoxes.clear();
                                        networkManager.minecraft.setLevel(null);
                                        networkManager.levelData = new ByteArrayOutputStream();
                                    } else if (packetType == PacketType.LEVEL_DATA) {
                                        short chunkLength = (Short) packetParams[0];
                                        byte[] chunkData = (byte[]) packetParams[1];
                                        byte percentComplete = (Byte) packetParams[2];
                                        networkManager.minecraft.progressBar.setProgress(percentComplete);
                                        isLoadingMap = false;
                                        networkManager.levelData.write(chunkData, 0, chunkLength);
                                    } else if (packetType == PacketType.LEVEL_FINALIZE) {
                                        try {
                                            networkManager.levelData.close();
                                        } catch (IOException ex) {
                                            LogUtil.logError("Error receiving level data.", ex);
                                        }

                                        byte[] decompressedStream = LevelLoader
                                                .decompress(new ByteArrayInputStream(
                                                                networkManager.levelData.toByteArray()));
                                        networkManager.levelData = null;
                                        short xSize = (Short) packetParams[0];
                                        short ySize = (Short) packetParams[1];
                                        short zSize = (Short) packetParams[2];
                                        Level level = new Level();
                                        level.setNetworkMode(true);
                                        level.setData(xSize, ySize, zSize, decompressedStream);
                                        networkManager.minecraft.setLevel(level);
                                        networkManager.minecraft.isOnline = false;
                                        networkManager.levelLoaded = true;
                                        // ProgressBarDisplay.InitEnv(this);
                                        // this.levelRenderer.refresh();
                                    } else if (packetType == PacketType.BLOCK_CHANGE) {
                                        if (networkManager.minecraft.level != null) {
                                            networkManager.minecraft.level.netSetTile(
                                                    (Short) packetParams[0],
                                                    (Short) packetParams[1],
                                                    (Short) packetParams[2],
                                                    (Byte) packetParams[3]);
                                        }
                                    } else {
                                        byte var9;
                                        String var34;
                                        NetworkPlayer var33;
                                        short var36;
                                        short var10004;
                                        byte var10001;
                                        short var47;
                                        short var10003;
                                        if (packetType == PacketType.SPAWN_PLAYER) {
                                            var10001 = (Byte) packetParams[0];
                                            String var10002 = (String) packetParams[1];
                                            var10003 = (Short) packetParams[2];
                                            var10004 = (Short) packetParams[3];
                                            short var10005 = (Short) packetParams[4];
                                            byte var10006 = (Byte) packetParams[5];
                                            byte var58 = (Byte) packetParams[6];
                                            var9 = var10006;
                                            var47 = var10004;
                                            var36 = var10003;
                                            var34 = var10002;
                                            byte var5 = var10001;
                                            if (var5 >= 0) {
                                                var9 = (byte) (var9 + 128);
                                                var47 = (short) (var47 - 22);
                                                var33 = new NetworkPlayer(networkManager.minecraft,
                                                        var34, var36, var47, var10005,
                                                        var58 * 360 / 256F, var9 * 360 / 256F);
                                                networkManager.players.put(var5, var33);
                                                networkManager.minecraft.level.addEntity(var33);
                                            } else {
                                                networkManager.minecraft.level.setSpawnPos(
                                                        var36 / 32, var47 / 32, var10005 / 32,
                                                        var9 * 320 / 256);
                                                networkManager.minecraft.player.moveTo(var36 / 32F,
                                                        var47 / 32F, var10005 / 32F,
                                                        var9 * 360 / 256F, var58 * 360 / 256F);
                                            }
                                        } else {
                                            byte var53;
                                            NetworkPlayer networkPlayer;
                                            byte var69;
                                            if (packetType == PacketType.POSITION_ROTATION) {
                                                var10001 = (Byte) packetParams[0];
                                                short var66 = (Short) packetParams[1];
                                                var10003 = (Short) packetParams[2];
                                                var10004 = (Short) packetParams[3];
                                                var69 = (Byte) packetParams[4];
                                                var9 = (Byte) packetParams[5];
                                                var53 = var69;
                                                var47 = var10004;
                                                var36 = var10003;
                                                byte var5 = var10001;
                                                if (var5 < 0) {
                                                    networkManager.minecraft.player.moveTo(
                                                            var66 / 32F, var36 / 32F, var47 / 32F,
                                                            var53 * 360 / 256F, var9 * 360 / 256F);
                                                } else {
                                                    var53 = (byte) (var53 + 128);
                                                    var36 = (short) (var36 - 22);
                                                    if ((networkPlayer = networkManager.players
                                                            .get(Byte.valueOf(var5))) != null) {
                                                        networkPlayer.teleport(var66, var36, var47,
                                                                var9 * 360 / 256F, var53 * 360 / 256F
                                                        );
                                                    }
                                                }
                                            } else {
                                                byte var37;
                                                byte var44;
                                                byte var49;
                                                byte var65;
                                                byte var67;
                                                if (packetType == PacketType.POSITION_ROTATION_UPDATE) {
                                                    byte playerID = (Byte) packetParams[0];
                                                    var37 = (Byte) packetParams[1];
                                                    var44 = (Byte) packetParams[2];
                                                    var49 = (Byte) packetParams[3];
                                                    var53 = (Byte) packetParams[4];
                                                    var9 = (Byte) packetParams[5];
                                                    if (playerID >= 0) {
                                                        var53 = (byte) (var53 + 128);
                                                        NetworkPlayer networkPlayerInstance = networkManager.players.get(Byte.valueOf(playerID));
                                                        if (networkPlayerInstance != null) {
                                                            networkPlayerInstance.queue(var37, var44,
                                                                    var49, var9 * 360 / 256F, var53 * 360 / 256F
                                                            );
                                                        }
                                                    }
                                                } else if (packetType == PacketType.ROTATION_UPDATE) {
                                                    byte playerID = (Byte) packetParams[0];
                                                    var37 = (Byte) packetParams[1];
                                                    var44 = (Byte) packetParams[2];
                                                    if (playerID >= 0) {
                                                        var37 = (byte) (var37 + 128);
                                                        NetworkPlayer networkPlayerInstance = networkManager.players.get(Byte.valueOf(playerID));
                                                        if (networkPlayerInstance != null) {
                                                            networkPlayerInstance.queue(var44 * 360 / 256F, var37 * 360 / 256F
                                                            );
                                                        }
                                                    }
                                                } else if (packetType == PacketType.POSITION_UPDATE) {
                                                    byte playerID = (Byte) packetParams[0];
                                                    NetworkPlayer networkPlayerInstance = networkManager.players.get(Byte.valueOf(playerID));
                                                    if (playerID >= 0 && networkPlayerInstance != null) {
                                                        networkPlayerInstance.queue((Byte) packetParams[1], (Byte) packetParams[2], (Byte) packetParams[3]);
                                                    }
                                                } else if (packetType == PacketType.DESPAWN_PLAYER) {
                                                    byte playerID = (Byte) packetParams[0];
                                                    var33 = networkManager.players.remove(Byte.valueOf(playerID));
                                                    if (playerID >= 0 && var33 != null) {
                                                        var33.clear();
                                                        networkManager.minecraft.level.removeEntity(var33);
                                                    }
                                                } else if (packetType == PacketType.CHAT_MESSAGE) {
                                                    byte messageType = (Byte) packetParams[0];
                                                    String message = (String) packetParams[1];
                                                    if (messageType < 0) {
                                                        networkManager.minecraft.hud.addChat("&e" + message);
                                                    } else if (messageType > 0 && serverSupportsMessages) {
                                                        switch (messageType) {
                                                            case 1:
                                                                HUDScreen.ServerName = message;
                                                                break;
                                                            case 2:
                                                                HUDScreen.Compass = message;
                                                                break;
                                                            case 3:
                                                                HUDScreen.UserDetail = message;
                                                                break;
                                                            case 11:
                                                                HUDScreen.BottomRight1 = message;
                                                                break;
                                                            case 12:
                                                                HUDScreen.BottomRight2 = message;
                                                                break;
                                                            case 13:
                                                                HUDScreen.BottomRight3 = message;
                                                                break;
                                                            case 21:
                                                                break;
                                                            case 100:
                                                                HUDScreen.Announcement = message;
                                                                break;
                                                            default:
                                                                networkManager.players.get(messageType);
                                                                networkManager.minecraft.hud.addChat(message);
                                                                break;
                                                        }
                                                    } else {
                                                        networkManager.players.get(messageType);
                                                        networkManager.minecraft.hud.addChat(message);
                                                    }
                                                } else if (packetType == PacketType.DISCONNECT) {
                                                    networkManager.netHandler.close();
                                                    networkManager.minecraft.setCurrentScreen(new ErrorScreen(
                                                            "Connection lost",
                                                            (String) packetParams[0]));
                                                } else if (packetType == PacketType.UPDATE_PLAYER_TYPE) {
                                                    networkManager.minecraft.player.userType = (Byte) packetParams[0];
                                                }
                                            }
                                        }
                                    }
                                }

                                if (!networkHandler.connected) {
                                    break;
                                }

                                networkHandler.in.compact();
                            }

                            if (networkHandler.out.position() > 0) {
                                networkHandler.out.flip();
                                networkHandler.channel.write(networkHandler.out);
                                networkHandler.out.compact();
                            }
                        } catch (Exception ex) {
                            LogUtil.logWarning("Error in network handling code.", ex);
                            var20.minecraft.setCurrentScreen(new ErrorScreen("Disconnected!",
                                    "You\'ve lost connection to the server"));
                            var20.minecraft.isOnline = false;
                            var20.netHandler.close();
                            var20.minecraft.networkManager = null;
                        }
                    }
                }

                var20 = networkManager;
                if (networkManager.levelLoaded) {
                    int playerXUnits = (int) (player.x * 32F);
                    int playerYUnits = (int) (player.y * 32F);
                    int playerZUnits = (int) (player.z * 32F);
                    int playerYRotation = (int) (player.yRot * 256F / 360F) & 255;
                    int playerXRotation = (int) (player.xRot * 256F / 360F) & 255;
                    var20.netHandler.send(
                            PacketType.POSITION_ROTATION,
                            canSendHeldBlock ? player.inventory.getSelected() : -1, playerXUnits,
                            playerYUnits, playerZUnits,
                            playerYRotation, playerXRotation);
                }
            }
        }

        if (isLoadingMap) {
            while (Keyboard.next()) {
                if (Keyboard.getEventKeyState()) {
                    if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                        pause();
                    }
                }
            }
            return;
        }

        if (currentScreen == null && player != null && player.health <= 0) {
            setCurrentScreen(null);
        }

        handleInput();

        if (level != null && player != null) {
            ++renderer.levelTicks;
            HeldBlock var41 = renderer.heldBlock;
            renderer.heldBlock.lastPos = var41.pos;
            if (var41.moving) {
                ++var41.offset;
                if (var41.offset == 7) {
                    var41.offset = 0;
                    var41.moving = false;
                }
            }

            int heldBlockId = player.inventory.getSelected(); // TODO WTF?
            Block heldBlock = null;
            if (heldBlockId > 0) {
                heldBlock = Block.blocks[heldBlockId];
            }

            float var48 = 0.4F;
            float var50 = (heldBlock == var41.block ? 1F : 0F) - var41.pos;
            if (var50 < -var48) {
                var50 = -var48;
            }

            if (var50 > var48) {
                var50 = var48;
            }

            var41.pos += var50;
            if (var41.pos < 0.1F) {
                var41.block = heldBlock;
            }

            // Render rainfall
            if (renderer.minecraft.isRaining) {
                int playerX = (int) player.x;
                int playerY = (int) player.y;
                int playerZ = (int) player.z;

                for (i = 0; i < 50; ++i) {
                    int var60 = playerX + renderer.random.nextInt(9) - 4;
                    int var52 = playerZ + renderer.random.nextInt(9) - 4;
                    int var57 = level.getHighestTile(var60, var52);
                    if (var57 <= playerY + 4 && var57 >= playerY - 4) {
                        float offsetX = renderer.random.nextFloat();
                        float offsetZ = renderer.random.nextFloat();
                        particleManager.spawnParticle(new WaterDropParticle(
                                level, var60 + offsetX, var57 + 0.1F, var52 + offsetZ));
                    }
                }
            }

            ++levelRenderer.ticks;
            level.tickEntities();
            if (!isOnline()) {
                level.tick();
            }

            particleManager.tick();
        }
    }

    private void handleInput() {
        if (currentScreen instanceof BlockSelectScreen) {
            while (Mouse.next()) {
                int mouseScroll = Mouse.getEventDWheel();
                if (mouseScroll != 0) {
                    // React to mouse-scrolling while in block selection
                    player.inventory.swapPaint(mouseScroll);
                    break;
                }
                currentScreen.mouseEvent();
            }
            while (Keyboard.next()) {
                if (Keyboard.getEventKey() >= Keyboard.KEY_1
                        && Keyboard.getEventKey() <= Keyboard.KEY_9) {
                    if (GameSettings.CanReplaceSlot) {
                        player.inventory.selected = Keyboard.getEventKey() - 2;
                        break;
                    }
                }
                // If the player presses lots of keys and presses the left mouse button,
                // sometimes the screen is closed even though there are still keys left to process.
                if (currentScreen != null) {
                    currentScreen.keyboardEvent();
                }
            }
        } else if (currentScreen == null) {
            while (Mouse.next()) {
                if (currentScreen == null) {
                    int mouseScroll = Mouse.getEventDWheel();
                    if (mouseScroll != 0) {
                        // React to mouse-scrolling while in-game
                        player.inventory.swapPaint(mouseScroll);
                    }

                    // Send mouse event to game
                    if (!hasMouse && Mouse.getEventButtonState()) {
                        grabMouse();
                    } else {
                        if (Mouse.getEventButton() == MB_LEFT && Mouse.getEventButtonState()) {
                            onMouseClick(MB_LEFT);
                            lastClick = ticks;
                        }

                        if (Mouse.getEventButton() == MB_RIGHT && Mouse.getEventButtonState()) {
                            onMouseClick(MB_RIGHT);
                            lastClick = ticks;
                        }

                        if (Mouse.getEventButton() == MB_MIDDLE && Mouse.getEventButtonState()
                                && selected != null) {
                            int var16 = level.getTile(selected.x, selected.y, selected.z);
                            player.inventory.grabTexture(var16, !isSurvival());
                        }
                    }
                }

                // Note sure if needed:
                // This only triggers if currentScreen is set while handling
                // mouse input in-game (?)
                if (currentScreen != null) {
                    currentScreen.mouseEvent();
                }
            }
            if (punchingCooldown > 0) {
                // Decrement punching cooldown (survival)
                --punchingCooldown;
            }
            while (Keyboard.next()) {
                player.setKey(Keyboard.getEventKey(), Keyboard.getEventKeyState());
                if (Keyboard.getEventKeyState()) {
                    if (currentScreen != null) {
                        currentScreen.keyboardEvent();
                    }
                    if (currentScreen == null) {
                        if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                            pause();
                        }

                        if (!isSurvival()) {
                            if (HackState.respawn) {
                                if (Keyboard.getEventKey() == settings.loadLocationKey.key) {
                                    if (!(currentScreen instanceof ChatInputScreen)) {
                                        player.resetPos();
                                    }
                                }

                                if (Keyboard.getEventKey() == settings.saveLocationKey.key) {
                                    level.setSpawnPos((int) player.x, (int) player.y,
                                            (int) player.z, player.yRot);
                                    player.resetPos();
                                }
                            }
                        }

                        // Handle hardcoded keys (including F-keys)
                        switch (Keyboard.getEventKey()) {
                            case Keyboard.KEY_F1:
                                canRenderGUI = !canRenderGUI;
                                break;

                            case Keyboard.KEY_F2:
                                takeAndSaveScreenshot(width, height);
                                break;

                            case Keyboard.KEY_F3:
                                settings.showDebug = !settings.showDebug;
                                break;

                            case Keyboard.KEY_F4:
                                isSnowing = !isSnowing;
                                isRaining = false;
                                break;

                            case Keyboard.KEY_F5:
                                isRaining = !isRaining;
                                isSnowing = false;
                                break;

                            case Keyboard.KEY_F6:
                                if (HackState.noclip) {
                                    ++settings.thirdPersonMode;
                                    if (settings.thirdPersonMode > 2) {
                                        settings.thirdPersonMode = 0;
                                    }
                                }
                                break;

                            case Keyboard.KEY_F11:
                                toggleFullscreen();
                                break;

                            case Keyboard.KEY_SLASH:
                                player.releaseAllKeys();
                                ChatInputScreen s = new ChatInputScreen();
                                setCurrentScreen(s);
                                s.inputLine = "/";
                                s.caretPos++;
                                break;
                        }

                        if (settings.HacksEnabled) {
                            // Check for hack toggle keys
                            if (settings.HackType == 0) {
                                if (Keyboard.getEventKey() == settings.noClip.key) {
                                    if (HackState.noclip || HackState.noclip
                                            && player.userType >= 100) {
                                        player.noPhysics = !player.noPhysics;
                                        player.hovered = !player.hovered;
                                    }
                                }
                                if (Keyboard.getEventKey() == Keyboard.KEY_Z) {
                                    if (HackState.fly) {
                                        player.flyingMode = !player.flyingMode;
                                    }
                                }
                            }
                        } else {
                            player.flyingMode = false;
                            player.noPhysics = false;
                            player.hovered = false;
                        }

                        if (Keyboard.getEventKey() == Keyboard.KEY_TAB && isSurvival()
                                && player.arrows > 0) {
                            // Shoot arrows (survival)
                            level.addEntity(new Arrow(level, player, player.x, player.y, player.z,
                                    player.yRot, player.xRot, 1.2F));
                            --player.arrows;
                        }

                        if (Keyboard.getEventKey() == settings.inventoryKey.key) {
                            gamemode.openInventory();
                        }

                        if (Keyboard.getEventKey() == settings.chatKey.key) {
                            player.releaseAllKeys();
                            setCurrentScreen(new ChatInputScreen());
                        }
                    }
                    for (int i = 0; i < 9; ++i) {
                        if (Keyboard.getEventKey() == i + 2) {
                            if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
                                // tabbing player list
                                return;
                            } else if (GameSettings.CanReplaceSlot) {
                                player.inventory.selected = i;
                            }
                        }
                    }
                    if (Keyboard.getEventKey() == settings.toggleFogKey.key) {
                        boolean shiftDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
                                || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
                        settings.toggleSetting(Setting.RENDER_DISTANCE, shiftDown ? -1 : 1);
                    }
                }
            }
            if (currentScreen == null) {
                if (Mouse.isButtonDown(MB_LEFT) && ticks - lastClick >= timer.tps / 4F && hasMouse) {
                    onMouseClick(MB_LEFT);
                    lastClick = ticks;
                }

                if (Mouse.isButtonDown(MB_RIGHT) && ticks - lastClick >= timer.tps / 4F && hasMouse) {
                    onMouseClick(MB_RIGHT);
                    lastClick = ticks;
                }
            }
            if (!gamemode.instantBreak && punchingCooldown <= 0) {
                // survival: slow block-breaking
                if ((currentScreen == null) && Mouse.isButtonDown(MB_LEFT) && hasMouse
                        && selected != null && !selected.hasEntity) {
                    gamemode.hitBlock(selected.x, selected.y, selected.z, selected.face);
                } else {
                    gamemode.resetHits();
                }
            }
        }
        if (currentScreen != null) {
            lastClick = ticks + 10000;
        }
        if (currentScreen != null) {
            currentScreen.doInput();
            if (currentScreen != null) {
                currentScreen.tick();
            }
        }
    }

    /**
     * Toggles FullScreen on or off.
     */
    public void toggleFullscreen() {
        try {
            isFullScreen = !isFullScreen;

            if (isFullScreen) {
                setDisplayMode();

                width = Display.getDisplayMode().getWidth();
                height = Display.getDisplayMode().getHeight();
                if (width <= 0) {
                    width = 1;
                }

                if (height <= 0) {
                    height = 1;
                }
            } else {
                Display.setDisplayMode(new DisplayMode(tempDisplayWidth, tempDisplayHeight));
                width = tempDisplayWidth;
                height = tempDisplayHeight;

                if (width <= 0) {
                    width = 1;
                }

                if (height <= 0) {
                    height = 1;
                }
            }

            resize();

            Display.setFullscreen(isFullScreen);
            Display.setVSyncEnabled(settings.limitFramerate);
            Display.update();
        } catch (Exception ex) {
            LogUtil.logWarning("Error toggling fullscreen " + (isFullScreen ? "ON" : "OFF"), ex);
        }
    }
}
