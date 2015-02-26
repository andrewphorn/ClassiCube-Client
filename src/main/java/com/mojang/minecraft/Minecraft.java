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
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import com.mojang.minecraft.mob.Mob;
import static com.mojang.minecraft.mob.Mob.modelCache;
import com.mojang.minecraft.model.ModelManager;
import com.mojang.minecraft.model.ModelPart;
import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.NetworkPlayer;
import com.mojang.minecraft.net.PacketHandler;
import com.mojang.minecraft.net.PacketType;
import com.mojang.minecraft.net.ProtocolExtension;
import com.mojang.minecraft.net.WOMConfig;
import com.mojang.minecraft.particle.Particle;
import com.mojang.minecraft.particle.ParticleManager;
import com.mojang.minecraft.particle.WaterDropParticle;
import com.mojang.minecraft.physics.AABB;
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
import com.mojang.minecraft.render.texture.Textures;
import com.mojang.minecraft.sound.SoundManager;
import com.mojang.minecraft.sound.SoundPlayer;
import com.mojang.util.ColorCache;
import com.mojang.util.LogUtil;
import com.mojang.util.MathHelper;
import com.mojang.util.StreamingUtil;
import com.mojang.util.Timer;
import com.mojang.util.Vec3D;
import com.oyasunadev.mcraft.client.util.Constants;
import java.security.NoSuchAlgorithmException;

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
    public static String skinServer = "http://www.classicube.net/static/skins/";
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

    public WOMConfig womConfig = new WOMConfig(this);
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
     * True if the player is connecting to a server, from the moment connection is established and
     * until LEVEL_FINALIZE packet is received.
     */
    public boolean isConnecting;
    /**
     * Manages networking.
     */
    public NetworkManager networkManager;

    /**
     * Reads and writes packets (via the network manager).
     */
    private PacketHandler packetHandler = new PacketHandler(this);

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
    public HashMap<Byte, SelectionBoxData> selectionBoxes = new HashMap<>();
    public List<HotKeyData> hotKeys = new ArrayList<>();
    public HackState hackState; // TODO Never used
    public List<PlayerListNameData> playerListNameData = new ArrayList<>();
    public HashSet<Block> disallowedPlacementBlocks = new HashSet<>();
    public HashSet<Block> disallowedBreakingBlocks = new HashSet<>();
    public MonitoringThread monitoringThread;
    public int tempDisplayWidth;
    public int tempDisplayHeight;
    public boolean canRenderGUI = true;
    boolean isShuttingDown = false;
    int[] inventoryCache;
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
     * @param applet applet of this instance
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
        isConnecting = false;
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
        File minecraftFolder = OperatingSystem.detect().getMinecraftFolder(home, folder);

        if (!minecraftFolder.exists() && !minecraftFolder.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: "
                    + minecraftFolder);
        }

        return minecraftFolder;
    }

    public void downloadImage(URL url, File dest) {
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
        Level newLevel = new LevelGenerator(progressBar)
                .generate(username, 128 << scale, 128 << scale, 64);
        gamemode.prepareLevel(newLevel);
        setLevel(newLevel);
    }

    public String getHash(String urlString) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] urlBytes = urlString.getBytes();
            byte[] hashBytes = md.digest(urlBytes);
            return new BigInteger(1, hashBytes).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            LogUtil.logError("MD5 implementation not found? Very strange!", ex);
            shutdown();
            return null;
        }
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
                        && !disallowedBreakingBlocks.contains(block)) {
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

        if (!isApplet) {
            System.setProperty("org.lwjgl.librarypath", mcDir + "/natives");
            System.setProperty("net.java.games.input.librarypath", mcDir + "/natives");
        }

        // if LWJGL dependencies are missing, NoClassDefFoundError or UnsatisfiedLinkError will be thrown here
        LogUtil.logInfo("LWJGL version: " + Sys.getVersion());

        resourceThread = new ResourceDownloadThread(mcDir, this);
        resourceThread.run(); // TODO: run asynchronously

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

        Display.create();

        logSystemInfo();

        Keyboard.create();
        Mouse.create();

        checkGLError("Pre startup");

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_FLAT);
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
        settings.capRefreshRate(Display.getDisplayMode().getFrequency());

        ShapeRenderer.instance = new ShapeRenderer(2097152, settings); // 2MB
        textureManager = new TextureManager(settings, isApplet);

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

        fontRenderer = new FontRenderer(settings, textureManager);
        monitoringThread = new MonitoringThread(1000); // 1s refresh

        levelRenderer = new LevelRenderer(this, textureManager);
        Item.initModels();
        Mob.modelCache = new ModelManager();
        GL11.glViewport(0, 0, width, height);
        if (server != null && session != null) {
            // We're in multiplayer, connecting to a server!
            // Create a tiny temporary empty level while we wait for map to be sent
            Level defaultLevel = new Level();
            defaultLevel.setData(8, 8, 8, new byte[512]);
            setLevel(defaultLevel);
        } else {
            // We're in singleplayer!
            try {
                if (!isLevelLoaded) {
                    // Try to load a previously-saved level
                    Level newLevel = new LevelLoader().load(new File(mcDir, "levelc.cw"), player);
                    if (newLevel != null) {
                        if (isSurvival()) {
                            setLevel(newLevel);
                        } else {
                            progressBar.setText("Loading saved map...");
                            setLevel(newLevel);
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

        particleManager = new ParticleManager();
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
            Thread soundPlayerThread = new Thread(soundPlayer, "SoundPlayer");
            soundPlayerThread.setDaemon(true);
            soundPlayerThread.setPriority(Thread.MAX_PRIORITY);
            soundPlayerThread.start();
        } catch (Exception ex) {
            soundPlayer.running = false;
            LogUtil.logWarning("Failed to start the sound player.", ex);
        }

        checkGLError("Post startup");
        hud = new HUDScreen(this, width, height);
        if (session != null) {
            player.setSkin(session.username);
        }
        if (server != null && session != null) {
            networkManager = new NetworkManager(this);
            networkManager.beginConnect(server, port);
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
        } catch (Exception | NoClassDefFoundError | UnsatisfiedLinkError ex) {
            LogUtil.logError("Failed to start ClassiCube!", ex);
            JOptionPane.showMessageDialog(null, ex.toString(), "Failed to start ClassiCube",
                    JOptionPane.ERROR_MESSAGE);
            isRunning = false;
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
            double now = System.nanoTime() / Timer.NANOSEC_PER_SEC;
            double secondsPassed = (now - timer.lastHR);
            timer.lastHR = now;

            // Cap seconds-passed to range [0,1]
            if (secondsPassed < 0D) {
                secondsPassed = 0D;
            }
            if (secondsPassed > 1D) {
                secondsPassed = 1D;
            }
            timer.lastFrameDuration = timer.lastFrameDuration * 0.5 + secondsPassed * 0.5;

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

            if (!isConnecting) {
                gamemode.applyCracks(timer.delta);
                if (renderer.displayActive && !Display.isActive()) {
                    pause();
                }

                renderer.displayActive = Display.isActive();
                if (hasMouse) {
                    int mouseDX = 0;
                    int mouseDY = 0;
                    if (isLevelLoaded) {
                        if (canvas != null) {
                            Point mouseLocation = canvas.getLocationOnScreen();
                            int mouseX = mouseLocation.x + width / 2;
                            int mouseY = mouseLocation.y + height / 2;
                            Point pointerLocation = MouseInfo.getPointerInfo().getLocation();
                            mouseDX = pointerLocation.x - mouseX;
                            mouseDY = -(pointerLocation.y - mouseY);
                            robot.mouseMove(mouseX, mouseY);
                        } else {
                            Mouse.setCursorPosition(width / 2, height / 2);
                        }
                    } else {
                        mouseDX = Mouse.getDX();
                        mouseDY = Mouse.getDY();
                    }

                    int mouseDirection = 1;
                    if (settings.invertMouse) {
                        mouseDirection = -1;
                    }

                    player.turn(mouseDX, mouseDY * mouseDirection);
                }

                if (!isConnecting) {
                    int var81 = width * 240 / height;
                    int var86 = height * 240 / height;
                    int mouseX = Mouse.getX() * var81 / width;
                    int mouseY = var86 - Mouse.getY() * var86 / height - 1;

                    // Don't render the world while a disconnect or error screen is showing.
                    boolean guiIsTransparent = (currentScreen == null || !currentScreen.isOpaque);

                    if (level != null && player != null && guiIsTransparent) {
                        float delta = timer.delta;
                        float newXRot = player.xRotO + (player.xRot - player.xRotO) * delta;
                        float newYRot = player.yRotO + (player.yRot - player.yRotO) * delta;
                        Vec3D newPlayerVector = renderer.getPlayerVector(delta);
                        float var32 = MathHelper.cos((float) ((double) -newYRot * (Math.PI / 180D) - Math.PI));
                        float var69 = MathHelper.sin((float) ((double) -newYRot * (Math.PI / 180D) - Math.PI));
                        float var174 = MathHelper.cos(-newXRot * (float) (Math.PI / 180D));
                        float var33 = MathHelper.sin(-newXRot * (float) (Math.PI / 180D));
                        float var34 = var69 * var174;
                        float var87 = var32 * var174;
                        float reachDistance = gamemode.getReachDistance();
                        Vec3D vec3D = newPlayerVector.add(var34 * reachDistance, var33 * reachDistance, var87 * reachDistance);
                        selected = level.clip(newPlayerVector, vec3D);
                        float var74 = reachDistance;
                        if (selected != null) {
                            var74 = selected.vec.distance(renderer.getPlayerVector(delta));
                        }

                        newPlayerVector = renderer.getPlayerVector(delta);
                        if (isSurvival()) {
                            reachDistance = var74;
                        } else {
                            reachDistance = 32F;
                        }

                        vec3D = newPlayerVector.add(var34 * reachDistance, var33 * reachDistance,
                                var87 * reachDistance);

                        renderer.entity = null;
                        List<Entity> nearbyEntities = level.blockMap.getEntities(
                                player,
                                player.boundingBox.expand(var34 * reachDistance,
                                        var33 * reachDistance, var87 * reachDistance)
                        );

                        // Find the closest entity (player)
                        final float growFactor = 0.1F;
                        float closestDist = 0F;
                        for (Entity entity : nearbyEntities) {
                            if (entity.isPickable()) {
                                MovingObjectPosition var78 = entity.boundingBox
                                        .grow(growFactor, growFactor, growFactor)
                                        .clip(newPlayerVector, vec3D);
                                if (var78 != null) {
                                    float distanceToPlayer = newPlayerVector.distance(var78.vec);
                                    if (distanceToPlayer < closestDist || closestDist == 0F) {
                                        renderer.entity = entity;
                                        closestDist = distanceToPlayer;
                                    }
                                }
                            }
                        }

                        // SURVIVAL: target entity
                        if (renderer.entity != null && isSurvival()) {
                            selected = new MovingObjectPosition(renderer.entity);
                        }

                        GL11.glViewport(0, 0, width, height);

                        // Set view distance, sky color, and fog color
                        float viewDistanceFactor
                                = 1F - (float) Math.pow(1F / (settings.viewDistance + 1), 0.25D);
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

                        // Adjust fog color if underwater or in lava
                        int blockTypeAroundHead = level.getTile((int) this.player.x,
                                (int) (this.player.y + 0.12F - modelCache.getModel(player.getModelName()).headOffset), (int) this.player.z);
                        Block blockAroundHead = Block.blocks[blockTypeAroundHead];
                        if (blockAroundHead != null && blockAroundHead.getLiquidType() != LiquidType.notLiquid) {
                            LiquidType liquidType = blockAroundHead.getLiquidType();
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

                        // Draw the sky
                        GL11.glClearColor(renderer.fogRed, renderer.fogBlue, renderer.fogGreen, 0F);
                        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                        renderer.fogColorMultiplier = 1F;
                        GL11.glEnable(GL11.GL_CULL_FACE);
                        // Formula chosen to have fog choices of {12, 32, 84, 208, 512, 1254}
                        renderer.fogEnd = (float) Math.pow(2, settings.viewDistance + 3);

                        // Set up the perspective
                        GL11.glMatrixMode(GL11.GL_PROJECTION);
                        GL11.glLoadIdentity();

                        // SURVIVAL: adjust FoV for dead players
                        float fovy = 70F;
                        if (player.health <= 0) {
                            var74 = player.deathTime + delta;
                            fovy /= (1F - 500F / (var74 + 500F)) * 2F + 1F;
                        }
                        // TODO: adjustable FoV
                        GLU.gluPerspective(fovy, (float) width / (float) height, 0.05F, renderer.fogEnd);

                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GL11.glLoadIdentity();

                        // SURVIVAL: hurt effect
                        renderer.hurtEffect(delta);

                        renderer.setCamera(delta, selected);

                        Frustum frustum = FrustumImpl.getInstance();
                        // Check visibility of chunks
                        for (int i = 0; i < levelRenderer.chunkCache.length; ++i) {
                            levelRenderer.chunkCache[i].clip(frustum);
                        }

                        Collections.sort(levelRenderer.chunksToUpdate,
                                new ChunkDirtyDistanceComparator(player));
                        int chunkUpdates = levelRenderer.chunksToUpdate.size();

                        if (chunkUpdates > 0) {
                            // Update the closest chunk first
                            int lastChunkId = chunkUpdates - 1;

                            // Calculate the limit on chunk-updates-per-frame
                            int maxUpdates;
                            if (settings.framerateLimit == 0) {
                                maxUpdates = Renderer.MIN_CHUNK_UPDATES_PER_FRAME;
                            } else {
                                maxUpdates = Math.max(renderer.dynamicChunkUpdateLimit, Renderer.MIN_CHUNK_UPDATES_PER_FRAME);
                            }
                            chunkUpdates = Math.min(chunkUpdates, maxUpdates);

                            // Actually update the chunks. Measure how long it takes.
                            for (int i = 0; i < chunkUpdates; ++i) {
                                Chunk chunk = levelRenderer.chunksToUpdate.remove(lastChunkId - i);
                                chunk.update();
                                chunk.loaded = false;
                            }

                            if (settings.framerateLimit > 0) {
                                // Adjust chunks-per-frame based on framerate. Back off is under 30fps.
                                double minDesiredFramerate = Math.max(20, settings.framerateLimit / 2);
                                //int tempFps = (int) Math.floor(1 / timer.lastFrameDuration);
                                //String fpsStr = "[" + tempFps + " / " + minDesiredFramerate + "] ";
                                if (timer.lastFrameDuration > 1 / minDesiredFramerate) {
                                    renderer.everBackedOffFromChunkUpdates = (renderer.dynamicChunkUpdateLimit > Renderer.MIN_CHUNK_UPDATES_PER_FRAME);
                                    //LogUtil.logInfo(fpsStr + "backing off from " + renderer.dynamicChunkUpdateLimit + " to " + Math.max(Renderer.MIN_CHUNK_UPDATES_PER_FRAME, renderer.dynamicChunkUpdateLimit - 2));
                                    renderer.dynamicChunkUpdateLimit = Math.max(Renderer.MIN_CHUNK_UPDATES_PER_FRAME, renderer.dynamicChunkUpdateLimit - 2);
                                } else if (renderer.everBackedOffFromChunkUpdates) {
                                    //LogUtil.logInfo(fpsStr + "ramping up from " + renderer.dynamicChunkUpdateLimit + " to " + (renderer.dynamicChunkUpdateLimit + 1));
                                    renderer.dynamicChunkUpdateLimit += 1;
                                } else {
                                    //LogUtil.logInfo(fpsStr + "ramping up from " + renderer.dynamicChunkUpdateLimit + " to " + (renderer.dynamicChunkUpdateLimit + 3));
                                    renderer.dynamicChunkUpdateLimit += 3;
                                }
                            }
                        } else {
                            renderer.dynamicChunkUpdateLimit = Renderer.MIN_CHUNK_UPDATES_PER_FRAME;
                            renderer.everBackedOffFromChunkUpdates = false;
                        }

                        // Mark fog-obscured chunks as invisible
                        if (levelRenderer.chunkCache != null) {
                            for (Chunk aChunkCache : levelRenderer.chunkCache) {
                                if (Math.sqrt(aChunkCache.distanceSquared(player)) - 32 > renderer.fogEnd) {
                                    aChunkCache.visible = false;
                                } else {
                                    aChunkCache.visible = true;
                                }
                            }
                        }

                        // Set fog color/density/etc
                        renderer.updateFog();
                        GL11.glEnable(GL11.GL_FOG);

                        levelRenderer.sortChunks(player, 0);
                        ShapeRenderer shapeRenderer = ShapeRenderer.instance;
                        // If player is inside a solid block (noclip?)
                        if (level.isSolid(player.x, player.y, player.z, 0.1F)) {
                            if (!player.noPhysics || !HackState.noclip){
                                int playerX = (int) player.x;
                                int playerY = (int) player.y;
                                int playerZ = (int) player.z;

                                for (int x = playerX - 1; x <= playerX + 1; ++x) {
                                    for (int y = playerY - 1; y <= playerY + 1; ++y) {
                                        for (int z = playerZ - 1; z <= playerZ + 1; ++z) {
                                            int var104 = levelRenderer.level.getTile(x, y, z);
                                            if (var104 != 0 && Block.blocks[var104].isSolid()) {
                                                GL11.glColor4f(0.2F, 0.2F, 0.2F, 1F);
                                                GL11.glDepthFunc(GL11.GL_LESS);

                                                shapeRenderer.begin();

                                                for (int side = 0; side < 6; ++side) {
                                                    Block.blocks[var104].renderInside(shapeRenderer,
                                                            x, y, z, side);
                                                }

                                                shapeRenderer.end();
                                                GL11.glCullFace(GL11.GL_FRONT);
                                                shapeRenderer.begin();

                                                for (int side = 0; side < 6; ++side) {
                                                    Block.blocks[var104].renderInside(shapeRenderer,
                                                            x, y, z, side);
                                                }
                                            
                                                shapeRenderer.end();
                                                GL11.glCullFace(GL11.GL_BACK);
                                                GL11.glDepthFunc(GL11.GL_LEQUAL);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        renderer.setLighting(true);
                        Vec3D playerVector = renderer.getPlayerVector(delta);
                        // TODO: investigate if this render pass is necessary
                        level.blockMap.render(playerVector, frustum, levelRenderer.textureManager, delta);
                        renderer.setLighting(false);
                        renderer.updateFog();
                        float var123 = -MathHelper.cos(player.yRot * (float) Math.PI / 180F);
                        float var117 = -(newYRot = -MathHelper.sin(player.yRot * (float) Math.PI / 180F))
                                * MathHelper.sin(player.xRot * (float) Math.PI / 180F);
                        var32 = var123 * MathHelper.sin(player.xRot * (float) Math.PI / 180F);
                        var69 = MathHelper.cos(player.xRot * (float) Math.PI / 180F);

                        for (int pass = 0; pass < 2; ++pass) {
                            List<Particle> particles = (pass == 0 ? particleManager.particles0 : particleManager.particles1);

                            if (!particles.isEmpty()) {
                                int textureId = 0;
                                if (pass == 0) {
                                    textureId = textureManager.load(Textures.PARTICLES);
                                } else if (pass == 1) {
                                    textureId = textureManager.load(Textures.TERRAIN);
                                }

                                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
                                shapeRenderer.begin();

                                for (Particle particle : particles) {
                                    particle.render(
                                            shapeRenderer, delta, var123, var69, newYRot, var117,
                                            var32);
                                }

                                shapeRenderer.end();
                            }
                        }

                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.load(Textures.MAP_SIDE));
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        levelRenderer.renderBedrock();
                        renderer.updateFog();

                        if (settings.showClouds) {
                            levelRenderer.drawClouds(delta, shapeRenderer);
                        }
                        levelRenderer.drawSky(shapeRenderer, player.y, skyColorRed, skyColorBlue, skyColorGreen);

                        renderer.updateFog();
                        if (selected != null) {
                            GL11.glDisable(GL11.GL_ALPHA_TEST);

                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glEnable(GL11.GL_ALPHA_TEST);
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                            GL11.glColor4f(1F, 1F, 1F,
                                    (MathHelper.sin(System.currentTimeMillis() / 100F) * 0.2F + 0.4F) * 0.5F);
                            // SURVIVAL: draw cracks on sides of the block being broken
                            if (levelRenderer.cracks > 0F) {
                                GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
                                GL11.glBindTexture(GL11.GL_TEXTURE_2D, levelRenderer.textureManager.load(Textures.TERRAIN));
                                GL11.glColor4f(1F, 1F, 1F, 0.5F);
                                GL11.glPushMatrix();

                                int blockId = levelRenderer.level.getTile(selected.x, selected.y, selected.z);
                                blockAroundHead = (blockId > 0 ? Block.blocks[blockId] : null);
                                float blockXAverage = (blockAroundHead.maxX + blockAroundHead.minX) / 2F;
                                float blockYAverage = (blockAroundHead.maxY + blockAroundHead.minY) / 2F;
                                float blockZAverage = (blockAroundHead.maxZ + blockAroundHead.minZ) / 2F;
                                GL11.glTranslatef(selected.x + blockXAverage,
                                        selected.y + blockYAverage, selected.z + blockZAverage);
                                GL11.glScalef(1F, 1.01F, 1.01F);
                                GL11.glTranslatef(-(selected.x + blockXAverage),
                                        -(selected.y + blockYAverage), -(selected.z + blockZAverage));
                                shapeRenderer.begin();
                                shapeRenderer.noColor();
                                GL11.glDepthMask(false);
                                // Do the sides
                                for (int side = 0; side < 6; ++side) {
                                    blockAroundHead.renderSide(shapeRenderer, selected.x, selected.y, selected.z,
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
                            int pointedBlockType = levelRenderer.level.getTile(selected.x, selected.y, selected.z);
                            // If player is pointing at a block (anything other than air),
                            // draw a wireframe box around it.
                            if (pointedBlockType > 0) {
                                AABB aabb = Block.blocks[pointedBlockType]
                                        .getSelectionBox(selected.x, selected.y, selected.z)
                                        .grow(0.002F, 0.002F, 0.002F);
                                renderer.drawWireframeBox(aabb);
                            }
                        }

                        // -------------------
                        // Render water (?)
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        renderer.updateFog();
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                                levelRenderer.textureManager.load(Textures.MAP_EDGE));
                        levelRenderer.renderOutsideWater();
                        GL11.glColorMask(false, false, false, false);

                        int chunksRemaining = levelRenderer.sortChunks(player, 1);
                        GL11.glColorMask(true, true, true, true);

                        if (chunksRemaining > 0) {
                            GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                                    levelRenderer.textureManager.load(Textures.TERRAIN));
                            GL11.glCallLists(levelRenderer.buffer);
                        }

                        GL11.glDepthMask(true);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glDisable(GL11.GL_FOG);
                        // -------------------

                        if (!selectionBoxes.isEmpty()) {
                            SelectionBoxData[] boxes = new SelectionBoxData[selectionBoxes.size()];
                            boxes = selectionBoxes.values().toArray(boxes);
                            Arrays.sort(boxes, new SelectionBoxDistanceComparator(this.player));

                            // Set up OpenGL state for drawing selection boxes
                            GL11.glLineWidth(2);
                            GL11.glDisable(GL11.GL_ALPHA_TEST);
                            GL11.glEnable(GL11.GL_BLEND);
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            GL11.glDepthMask(false);
                            GL11.glDisable(GL11.GL_CULL_FACE);

                            for (SelectionBoxData box : boxes) {
                                renderer.drawSelectionCuboid(box, shapeRenderer);
                            }

                            // Restore OpenGL state
                            GL11.glEnable(GL11.GL_CULL_FACE);
                            GL11.glDepthMask(true);
                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                            // TODO: restore blend func?
                            GL11.glDisable(GL11.GL_BLEND);
                            GL11.glEnable(GL11.GL_ALPHA_TEST);
                            GL11.glLineWidth(1);
                        }

                        if (isRaining || isSnowing) {
                            renderer.drawWeather(delta, shapeRenderer);
                        }
                        if (!isSinglePlayer && networkManager != null) {
                            // Render other players' names
                            if ((settings.showNames == 2 || settings.showNames == 3) && this.player.userType >= 100) {
                                // Render all names
                                for (NetworkPlayer np : networkManager.getPlayers()) {
                                    np.renderHover(textureManager);
                                }
                            } else if (renderer.entity != null) {
                                // Render on-hover
                                renderer.entity.renderHover(textureManager);
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
                            if (settings.thirdPersonMode == ThirdPersonMode.NONE && canRenderGUI) {
                                GL11.glBindTexture(GL11.GL_TEXTURE_2D,
                                        heldBlock.minecraft.textureManager.load(Textures.TERRAIN));
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

                        hud.render(timer.delta, currentScreen != null, mouseX, mouseY);
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

            if (settings.framerateLimit != 0) {
                Display.sync(settings.framerateLimit);

                double fps = (1 / timer.lastFrameDuration);
                if (fps < settings.framerateLimit / 2) {
                    if (vsync) {
                        Display.setVSyncEnabled(false);
                        vsync = false;
                    }
                } else {
                    if (!vsync) {
                        Display.setVSyncEnabled(true);
                        vsync = true;
                    }
                }
            }

            checkGLError("Post render");
        } catch (Exception ex) {
            LogUtil.logError("Fatal error in main loop (onFrame)", ex);
            setCurrentScreen(new ErrorScreen("Client error", "The game broke! [" + ex + "]"));
        }
    }

    boolean vsync = false;

    public final void setCurrentScreen(GuiScreen newScreen) {
        if (currentScreen != null) {
            currentScreen.onClose();
        }
        HUDScreen.chatLocation = 0;

        // SURVIVAL: Game over
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
            isConnecting = false;
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

    public final void setLevel(Level newLevel) {
        if (applet == null || !applet.getDocumentBase().getHost().equalsIgnoreCase("minecraft.net")
                && !applet.getDocumentBase().getHost().equalsIgnoreCase("www.minecraft.net")
                || !applet.getCodeBase().getHost().equalsIgnoreCase("minecraft.net")
                && !applet.getCodeBase().getHost().equalsIgnoreCase("www.minecraft.net")) {
            newLevel = null;
        }

        level = newLevel;
        if (player != null && player.inventory != null) {
            inventoryCache = player.inventory.slots.clone();
        }
        if (newLevel != null) {
            newLevel.initTransient();
            gamemode.apply(newLevel);
            newLevel.font = fontRenderer;
            newLevel.minecraft = this;
            if (!isOnline()) { // if not online (singleplayer)
                player = (Player) newLevel.findSubclassOf(Player.class);
                if (player == null) {
                    player = new Player(newLevel, settings);
                    newLevel.player = player;
                    if (session != null) {
                        player.lastHumanoidSkinName = session.username;
                    }
                }
                player.settings = settings;
                player.resetPos();
            } else if (player != null) { // if online
                player.resetPos();
                gamemode.preparePlayer(player);
                newLevel.player = player;
                newLevel.addEntity(player);
            }
        }

        if (player == null) {
            player = new Player(newLevel, settings);
            player.lastHumanoidSkinName = session.username;
            player.resetPos();
            gamemode.preparePlayer(player);
            if (newLevel != null) {
                newLevel.player = player;
            }
        }

        if (player != null) {
            player.input = new InputHandlerImpl(settings, player);
            gamemode.apply(player);
        }

        if (levelRenderer != null) {
            if (levelRenderer.level != null) {
                levelRenderer.level.removeListener(levelRenderer);
            }

            levelRenderer.level = newLevel;
            if (newLevel != null) {
                newLevel.addListener(levelRenderer);
                levelRenderer.refresh();
            }
        }

        if (particleManager != null) {
            if (newLevel != null) {
                newLevel.particleEngine = particleManager;
            }
            particleManager.clear();
        }

        if (inventoryCache != null) {
            player.inventory.slots = inventoryCache;
        }
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
            if (packetHandler.isLoadingLevel) {
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
            if (System.currentTimeMillis() > sound.lastMusic && sound.playMusic(soundPlayer, "calm")) {
                sound.lastMusic = System.currentTimeMillis() + sound.random.nextInt(900000) + 300000L;
            }
        }

        gamemode.spawnMob();
        if (canRenderGUI) {
            ++this.hud.ticks;
            for (int i = 0; i < this.hud.chat.size(); ++i) {
                ++this.hud.chat.get(i).time;
            }
        }

        renderAnimatedTextures();

        if (networkManager != null && !(currentScreen instanceof ErrorScreen)) {
            if (networkManager.isConnected()) {
                doNetworking();
            } else {
                progressBar.setTitle("Connecting..");
                progressBar.setProgress(0);
            }
        }

        // SURVIVAL: Show game over screen
        if (currentScreen == null && player != null && player.health <= 0) {
            setCurrentScreen(null);
        }

        handleInput();

        if (level != null && player != null) {
            ++renderer.levelTicks;
            HeldBlock hotBar = renderer.heldBlock;
            renderer.heldBlock.lastPos = hotBar.pos;
            if (hotBar.moving) {
                ++hotBar.offset;
                if (hotBar.offset == 7) {
                    hotBar.offset = 0;
                    hotBar.moving = false;
                }
            }

            int heldBlockId = player.inventory.getSelected(); // TODO WTF?
            Block heldBlock = null;
            if (heldBlockId > 0) {
                heldBlock = Block.blocks[heldBlockId];
            }

            float var50 = (heldBlock == hotBar.block ? 1F : 0F) - hotBar.pos;
            if (var50 < -0.4F) {
                var50 = -0.4F;
            }

            if (var50 > 0.4F) {
                var50 = 0.4F;
            }

            hotBar.pos += var50;
            if (hotBar.pos < 0.1F) {
                hotBar.block = heldBlock;
            }

            // If it's raining, spawn raindrop particles on ground
            if (renderer.minecraft.isRaining) {
                int playerX = (int) player.x;
                int playerY = (int) player.y;
                int playerZ = (int) player.z;

                for (int i = 0; i < 50; ++i) {
                    int raindropBlockX = playerX + renderer.random.nextInt(9) - 4;
                    int raindropBlockY = playerZ + renderer.random.nextInt(9) - 4;
                    int groundLevel = level.getHighestTile(raindropBlockX, raindropBlockY);
                    if (groundLevel <= playerY + 4 && groundLevel >= playerY - 4) {
                        float offsetX = renderer.random.nextFloat();
                        float offsetZ = renderer.random.nextFloat();
                        particleManager.spawnParticle(
                                new WaterDropParticle(level, raindropBlockX + offsetX,
                                        groundLevel + 0.1F, raindropBlockY + offsetZ));
                    }
                }
            }

            if (HUDScreen.AnnouncementTimer != 0) {
                if ((System.currentTimeMillis() - HUDScreen.AnnouncementTimer) >= 10000) {
                    HUDScreen.Announcement = "";
                    HUDScreen.AnnouncementTimer = 0;
                }
            }

            ++levelRenderer.ticks;
            if (level.blockMap != null) {
                level.tickEntities();
            }
            if (!isOnline()) {
                level.tick();
            }

            particleManager.tick();
        }
    }

    private void renderAnimatedTextures() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureManager.load(Textures.TERRAIN));
        for (int i = 0; i < textureManager.animations.size(); ++i) {
            // Animate textures, like lava and water
            TextureFX texFX = textureManager.animations.get(i);
            texFX.animate();
            if (textureManager.textureBuffer.capacity() != texFX.textureData.length) {
                textureManager.textureBuffer = BufferUtils.createByteBuffer(texFX.textureData.length);
            } else {
                textureManager.textureBuffer.clear();
            }
            textureManager.textureBuffer.put(texFX.textureData);
            textureManager.textureBuffer.position(0).limit(texFX.textureData.length);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, texFX.textureId % 16 << 4,
                    texFX.textureId / 16 << 4, 16, 16,
                    GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureManager.textureBuffer);
        }
    }

    private void doNetworking() {
        // Do network communication
        try {
            if (!networkManager.handshakeSent) {
                networkManager.send(
                        PacketType.IDENTIFICATION,
                        Constants.PROTOCOL_VERSION, session.username, session.mppass,
                        (int) Constants.CLIENT_TYPE);
                networkManager.handshakeSent = true;
            }

            do {
                networkManager.channel.read(networkManager.in);
                for (int packetsReceived = 0;
                        packetsReceived < NetworkManager.MAX_PACKETS_PER_TICK
                        && networkManager.in.position() > 0;
                        packetsReceived++) {
                    if (!packetHandler.handlePacket(networkManager)) {
                        break;
                    }
                }
                networkManager.writeOut();

                if (packetHandler.isLoadingLevel) {
                    // Ignore all keyboard input while loading map, unless Esc is pressed.
                    while (Keyboard.next()) {
                        if (Keyboard.getEventKeyState()) {
                            if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                                pause();
                            }
                        }
                    }
                }
            } while (packetHandler.isLoadingLevel);

            // Send player position to the server -- level should be loaded by now.
            int playerXUnits = (int) (player.x * 32F);
            int playerYUnits = (int) (player.y * 32F);
            int playerZUnits = (int) (player.z * 32F);
            int playerYRotation = (int) (player.yRot * 256F / 360F) & 255;
            int playerXRotation = (int) (player.xRot * 256F / 360F) & 255;
            networkManager.send(
                    PacketType.POSITION_ROTATION,
                    networkManager.isExtEnabled(ProtocolExtension.HELD_BLOCK) ? player.inventory.getSelected() : -1,
                    playerXUnits, playerYUnits, playerZUnits,
                    playerYRotation, playerXRotation);
        } catch (Exception ex) {
            LogUtil.logWarning("Error in network handling code.", ex);
            setCurrentScreen(new ErrorScreen("Disconnected!",
                    "You\'ve lost connection to the server"));
            isConnecting = false;
            networkManager.close();
            networkManager = null;
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
        } else if (currentScreen instanceof GuiScreen) {
            while (Mouse.next()) {
                int mouseScroll = Mouse.getEventDWheel();
                if (mouseScroll != 0) {
                    if (mouseScroll > 0) {
                        if (HUDScreen.chat.size() - HUDScreen.chatLocation < 20) {
                            HUDScreen.chatLocation = HUDScreen.chatLocation;
                            break;
                        }
                        mouseScroll = 1;
                    }
                    if (mouseScroll < 0) {
                        mouseScroll = -1;
                    }
                    HUDScreen.chatLocation += mouseScroll;
                    if (HUDScreen.chatLocation < 0) {
                        HUDScreen.chatLocation = 0;
                    }
                    break;
                }
                currentScreen.mouseEvent();
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

                // Not sure if needed:
                // This only triggers if currentScreen is set while handling mouse input in-game (?)
                if (currentScreen != null) {
                    currentScreen.mouseEvent();
                }
            }
            if (punchingCooldown > 0) {
                // SURVIVAL: Decrement punching cooldown
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
                                    settings.thirdPersonMode = settings.thirdPersonMode.next();
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

                        if (settings.hacksEnabled) {
                            // Check for hack toggle keys
                            if (settings.hackType == 0) {
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
                            // SURVIVAL: Shoot arrows
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
                        settings.toggleSetting(Setting.VIEW_DISTANCE, shiftDown ? -1 : 1);
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
                // SURVIVAL: slow block-breaking
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

    public void toggleFullscreen() {
        try {
            isFullScreen = !isFullScreen;

            if (isFullScreen) {
                setDisplayMode();
                width = Display.getDisplayMode().getWidth();
                height = Display.getDisplayMode().getHeight();
            } else {
                Display.setDisplayMode(new DisplayMode(tempDisplayWidth, tempDisplayHeight));
                width = tempDisplayWidth;
                height = tempDisplayHeight;
            }

            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }

            resize();
            Display.setFullscreen(isFullScreen);
            settings.capRefreshRate(Display.getDisplayMode().getFrequency());
            Display.setVSyncEnabled(settings.framerateLimit != 0);
            Display.update();

        } catch (Exception ex) {
            LogUtil.logWarning("Error toggling fullscreen " + (isFullScreen ? "ON" : "OFF"), ex);
        }
    }

    public void restartSinglePlayer() {
        try {
            if (!isLevelLoaded) {
                // Try to load a previously-saved level
                Level newLevel = new LevelLoader().load(new File(Minecraft.mcDir, "levelc.cw"), player);
                if (newLevel != null) {
                    progressBar.setText("Loading saved map...");
                    setLevel(newLevel);
                    Minecraft.isSinglePlayer = true;
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

    public void reconnect() {
        // Reset networking and reconnect
        networkManager = new NetworkManager(this);
        packetHandler = new PacketHandler(this);
        womConfig = new WOMConfig(this);
        networkManager.beginConnect(server, port);        
        playerListNameData.clear();
        networkManager.enabledExtensions.clear();
        HUDScreen.Compass = "";
        HUDScreen.ServerName = "";
        HUDScreen.UserDetail = "";
        HUDScreen.BottomRight1 = "";
        HUDScreen.BottomRight2 = "";
        HUDScreen.BottomRight3 = "";
        HUDScreen.Announcement = "";
    }
}
