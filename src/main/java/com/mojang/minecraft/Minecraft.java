package com.mojang.minecraft;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
import com.mojang.minecraft.gamemode.SurvivalGameMode;
import com.mojang.minecraft.gui.BlockSelectScreen;
import com.mojang.minecraft.gui.ChatInputScreen;
import com.mojang.minecraft.gui.ChatInputScreenExtension;
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
import com.mojang.minecraft.model.HumanoidModel;
import com.mojang.minecraft.model.ModelManager;
import com.mojang.minecraft.model.ModelPart;
import com.mojang.minecraft.model.Vec3D;
import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.NetworkPlayer;
import com.mojang.minecraft.net.PacketType;
import com.mojang.minecraft.particle.Particle;
import com.mojang.minecraft.particle.ParticleManager;
import com.mojang.minecraft.particle.WaterDropParticle;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.player.InputHandlerImpl;
import com.mojang.minecraft.player.Player;
import com.mojang.minecraft.render.Renderer;
import com.mojang.minecraft.render.Chunk;
import com.mojang.minecraft.render.ChunkDirtyDistanceComparator;
import com.mojang.minecraft.render.Frustrum;
import com.mojang.minecraft.render.FrustrumImpl;
import com.mojang.minecraft.render.HeldBlock;
import com.mojang.minecraft.render.LevelRenderer;
import com.mojang.minecraft.render.ShapeRenderer;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.minecraft.render.texture.TextureFX;
import com.mojang.minecraft.sound.SoundManager;
import com.mojang.minecraft.sound.SoundPlayer;
import com.mojang.net.NetworkHandler;
import com.mojang.util.MathHelper;
import com.oyasunadev.mcraft.client.util.ExtData;

public final class Minecraft implements Runnable {
	/**
	 * True if the application is waiting for something.
	 */
	public volatile boolean isWaiting = false;
	/**
	 * Is the game running?
	 */
	public volatile boolean isRunning;

	/**
	 * True if the player is running, false if otherwise.
	 */
	public static boolean PlayerIsRunning = false;
	/**
	 * The Minecraft directory.
	 */
	public static File mcDir;
	/**
	 * List of display modes.
	 */
	private static final List<DisplayMode> displayModes = new ArrayList<DisplayMode>();

	/**
	 * True if we are in full screen mode, false if otherwise.
	 */
	public boolean isFullScreen = false;
	/**
	 * This timer determines how much time will pass between block
	 * modifications. It is used to prevent really fast block spamming.
	 */
	private Timer timer = new Timer(20.0F);
	private ResourceDownloadThread resourceThread;
	private int ticks;
	private int blockHitTime;
	private int lastClick;
	private Cursor cursor;

	/**
	 * Are we playing single player mode?
	 */
	public static boolean isSinglePlayer = false;
	/**
	 * The url of the skin server where the skins are located.
	 */
	public String skinServer = "http://www.classicube.net/static/skins/";
	/**
	 * The current Gamemode.
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
	 * Set this to whatever you want to show as debug information in the HUD. It
	 * will occupy one line. Right now it shows FPS and Chunk Updates.
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

	public List<SelectionBoxData> selectionBoxes = new ArrayList<SelectionBoxData>();
	public List<HotKeyData> hotKeys = new ArrayList<HotKeyData>();
	public HackState hackState;
	public List<PlayerListNameData> playerListNameData = new ArrayList<PlayerListNameData>();

	public List<Block> disallowedPlacementBlocks = new ArrayList<Block>();
	public List<Block> DisallowedBreakingBlocks = new ArrayList<Block>();
	public MonitoringThread monitoringThread;
	public int tempDisplayWidth;
	public int tempDisplayHeight;
	public boolean canRenderGUI = true;        
        public int flyToggleTimer = 7;

	private static void checkGLError(String var0) {
		int var1;
		if ((var1 = GL11.glGetError()) != 0) {
			String var2 = GLU.gluErrorString(var1);
			System.out.println("########## GL ERROR ##########");
			System.out.println("@ " + var0);
			System.out.println(var1 + ": " + var2);
			System.exit(0);
		}

	}

	public static boolean doesUrlExistAndIsImage(String URLName) {
		try {
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection con = (HttpURLConnection) new URL(URLName)
					.openConnection();
			con.setRequestMethod("HEAD");
			return con.getResponseCode() == HttpURLConnection.HTTP_OK
					&& con.getContentType().contains("image");
		} catch (Exception e) {
			e.printStackTrace();
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
		Minecraft$OS os = getOs();
		switch (os.id) {
		case 0:
			minecraftFolder = new File(home, folder + '/');
			break;
		case 1:
			minecraftFolder = new File(home, folder + '/');
			break;
		case 2:
			String appData = System.getenv("APPDATA");

			if (appData != null) {
				minecraftFolder = new File(appData, folder + '/');
			} else {
				minecraftFolder = new File(home, folder + '/');
			}
			break;
		case 3:
			minecraftFolder = new File(home, "Library/Application Support/"
					+ folder);
			break;
		default:
			minecraftFolder = new File(home, folder + '/');
		}

		if (!minecraftFolder.exists() && !minecraftFolder.mkdirs()) {
			throw new RuntimeException(
					"The working directory could not be created: "
							+ minecraftFolder);
		}

		return minecraftFolder;

	}

	private static Minecraft$OS getOs() {
		String s = System.getProperty("os.name").toLowerCase();
		if (s.contains("win")) {
			return Minecraft$OS.windows;
		}
		if (s.contains("mac")) {
			return Minecraft$OS.macos;
		}
		if (s.contains("solaris")) {
			return Minecraft$OS.solaris;
		}
		if (s.contains("sunos")) {
			return Minecraft$OS.solaris;
		}
		if (s.contains("linux")) {
			return Minecraft$OS.linux;
		}
		if (s.contains("unix")) {
			return Minecraft$OS.linux;
		} else {
			return Minecraft$OS.unknown;
		}
	}

	float cameraDistance = -0.1F;

	int recievedExtensionLength;

	boolean isShuttingDown = false;

	boolean canSendHeldBlock = false;

	boolean serverSupportsMessages = false;

	int[] inventoryCache;

	boolean isLoadingMap = false;

	/**
	 * Creates a new Minecraft instance.
	 * 
	 * @param canvas
	 *            Canvas to use for drawing.
	 * @param applet
	 *            Applet of this instance
	 * @param width
	 *            Width of the window
	 * @param height
	 *            Height of the window
	 * @param fullscreen
	 *            True if game should be in fullscreen
	 * @param isApplet
	 *            True if the game is running as an applet
	 */
	public Minecraft(Canvas canvas, MinecraftApplet applet, int width,
			int height, boolean fullscreen, boolean isApplet) {
		this.applet = applet;
		this.canvas = canvas;
		this.width = width;
		this.height = height;
		this.isFullScreen = fullscreen;
		this.isApplet = isApplet;
		sound = new SoundManager();
		ticks = 0;
		blockHitTime = 0;
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
		} catch (Exception var7) {
			var7.printStackTrace();
		}
		new SleepForeverThread();
		new HumanoidModel();
		if (canvas != null) {
			try {
				robot = new Robot();
				return;
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}

	}

	void downloadImage(String source, String dest) {
		URL url;
		try {
			if (!doesUrlExistAndIsImage(source)) {
				return;
			}
			url = new URL(source);

			InputStream in = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while (-1 != (n = in.read(buf))) {
				out.write(buf, 0, n);
			}
			out.close();
			in.close();
			byte[] response = out.toByteArray();
			FileOutputStream fos = new FileOutputStream(dest);
			fos.write(response);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public byte[] flipPixels(byte[] paramArrayOfByte, int paramInt1,
			int paramInt2) {
		paramInt1 *= 3;
		byte[] arrayOfByte = null;
		if (paramArrayOfByte != null) {
			arrayOfByte = new byte[paramInt1 * paramInt2];
			for (int i = 0; i < paramInt2; i++) {
				for (int j = 0; j < paramInt1; j++) {
					arrayOfByte[(paramInt2 - i - 1) * paramInt1 + j] = paramArrayOfByte[i
							* paramInt1 + j];
				}
			}
		}
		return arrayOfByte;
	}

	public final void generateLevel(int var1) {
		String var2 = session != null ? session.username : "anonymous";
		Level var4 = new LevelGenerator(progressBar).generate(var2,
				128 << var1, 128 << var1, 64);
		gamemode.prepareLevel(var4);
		setLevel(var4);
	}

	public String getHash(String urlString) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] urlBytes = urlString.getBytes();
		byte[] hashBytes = md.digest(urlBytes);
		return new BigInteger(1, hashBytes).toString(16);
	}

	public String getOSfolderName(String s) {
		if (s.contains("win")) {
			return "windows";
		}
		if (s.contains("mac")) {
			return "macosx";
		}
		if (s.contains("solaris")) {
			return "solaris";
		}
		if (s.contains("sunos")) {
			return "solaris";
		}
		if (s.contains("linux")) {
			return "linux";
		}
		if (s.contains("unix")) {
			return "linux";
		} else {
			return "linux";
		}
	}

	public final void grabMouse() {
		if (!hasMouse) {
			hasMouse = true;
			if (isLevelLoaded) {
				try {
					Mouse.setNativeCursor(cursor);
					Mouse.setCursorPosition(width / 2, height / 2);
				} catch (LWJGLException var2) {
					var2.printStackTrace();
				}
			} else {
				Mouse.setGrabbed(true);
			}
			setCurrentScreen((GuiScreen) null);
			lastClick = ticks + 10000;
		}
	}

	public final boolean isOnline() {
		return networkManager != null;
	}

	private boolean isSystemShuttingDown() {
		try {
			java.lang.reflect.Field running = Class.forName(
					"java.lang.Shutdown").getDeclaredField("RUNNING");
			java.lang.reflect.Field state = Class.forName("java.lang.Shutdown")
					.getDeclaredField("state");

			running.setAccessible(true);
			state.setAccessible(true);

			return state.getInt(null) > running.getInt(null);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private void onMouseClick(int var1) {
		if (var1 != 0 || blockHitTime <= 0) {
			HeldBlock var2;
			if (var1 == 0) {
				var2 = renderer.heldBlock;
				renderer.heldBlock.offset = -1;
				var2.moving = true;
			}

			int x;
			if (var1 == 1 && (x = player.inventory.getSelected()) > 0
					&& gamemode.useItem(player, x)) {
				var2 = renderer.heldBlock;
				renderer.heldBlock.pos = 0.0F;
			} else if (selected == null) {
				if (var1 == 0 && !(gamemode instanceof CreativeGameMode)) {
					blockHitTime = 10;
				}

			} else {
				if (selected.entityPos == 1) {
					if (var1 == 0) {
						selected.entity.hurt(player, 4);
						return;
					}
				} else if (selected.entityPos == 0) {
					x = selected.x;
					int y = selected.y;
					int z = selected.z;
					if (var1 != 0) {
						if (selected.face == 0) {
							--y;
						}

						if (selected.face == 1) {
							++y;
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
					Block block = Block.blocks[0];
					if (level != null) {
						block = Block.blocks[level.getTile(x, y, z)];
					} else {
						return;
					}
					// if mouse click left
					if (var1 == 0) {
						if (block != Block.BEDROCK || player.userType >= 100) {
							if (!DisallowedBreakingBlocks.contains(block)) {
								gamemode.hitBlock(x, y, z);
								return;
							}
						}
						// else if its right click
					} else {
						int blockID = player.inventory.getSelected();
						if (blockID <= 0
								|| disallowedPlacementBlocks
										.contains(Block.blocks[blockID])) {
							return; // if air or not allowed, return
						}
						AABB aabb = Block.blocks[blockID].getCollisionBox(x, y,
								z);
						if ((block == null || block == Block.WATER
								|| block == Block.STATIONARY_WATER
								|| block == Block.LAVA || block == Block.STATIONARY_LAVA)
								&& (aabb == null || (player.bb.intersects(aabb) ? false
										: level.isFree(aabb)))) {
							if (!gamemode.canPlace(blockID)) {
								return;
							}
							if (session == null) {
								Block toCheck = Block.blocks[level.getTile(x,
										y - 1, z)];
								if (toCheck != null) {
									if (toCheck.id > 0) {
										if (toCheck == Block.SNOW) {
											if (selected.face == 1) {
												if (block == Block.SNOW) {
													return;
												} else {
													y -= 1;
												}
											}
										}
									}
								}
							}

							if (isOnline()) {
								networkManager.sendBlockChange(x, y, z, var1,
										blockID);
							}

							level.netSetTile(x, y, z, blockID);
							var2 = renderer.heldBlock;
							renderer.heldBlock.pos = 0.0F;
							Block.blocks[blockID].onPlace(level, x, y, z);
						}
					}
				}
			}
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

			currentScreen.onOpen();
		}
	}

	@Override
	public final void run() {
		isRunning = true;

		mcDir = getMinecraftDirectory();

		try {
			resourceThread = new ResourceDownloadThread(mcDir, this);
			resourceThread.run();

			if (!isApplet) {
				System.setProperty("org.lwjgl.librarypath", mcDir + "/natives");
				System.setProperty("net.java.games.input.librarypath", mcDir
						+ "/natives");
			}
			if (session == null) {
				isSinglePlayer = true;
				SessionData.SetAllowedBlocks((byte) 1);
			} else { // try parse applet coz sessiondata is set
				if (isApplet) {
					if (session.mppass == null || port < 0) {
						SessionData.SetAllowedBlocks((byte) 1);
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

			System.out.println("Using LWJGL Version: " + Sys.getVersion());
			Display.setResizable(true);
			Display.setTitle("ClassiCube");

			try {
				Display.create();
			} catch (LWJGLException var57) {
				var57.printStackTrace();
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException var56) {
					;
				}

				Display.create();
			}

			Keyboard.create();
			Mouse.create();

			checkGLError("Pre startup");

			GL11.glEnable(3553);
			GL11.glShadeModel(7425);
			GL11.glClearDepth(1.0D);
			GL11.glEnable(2929);
			GL11.glDepthFunc(515);
			GL11.glEnable(3008);
			GL11.glAlphaFunc(516, 0.5F);
			GL11.glCullFace(1029);
			GL11.glMatrixMode(5889);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(5888);

			checkGLError("Startup");

			settings = new GameSettings(this, mcDir);
			ShapeRenderer.instance = new ShapeRenderer(2097152, settings);
			textureManager = new TextureManager(settings, isApplet);
			textureManager.registerAnimations();

			if (settings.lastUsedTexturePack != null) {
				File file = new File(getMinecraftDirectory().getAbsolutePath()
						+ "/texturepacks/" + settings.lastUsedTexturePack);

				if (file.exists()) {
					textureManager
							.loadTexturePack(settings.lastUsedTexturePack);
				} else {
					settings.lastUsedTexturePack = null;
					settings.save();
				}
			}

			fontRenderer = new FontRenderer(settings, "/default.png",
					textureManager);
			monitoringThread = new MonitoringThread(1000); // 1s refresh
			textureManager.initAtlas();

			IntBuffer var9;
			(var9 = BufferUtils.createIntBuffer(256)).clear().limit(256);
			levelRenderer = new LevelRenderer(this, textureManager);
			Item.initModels();
			Mob.modelCache = new ModelManager();
			GL11.glViewport(0, 0, width, height);
			if (server != null && session != null) {
				Level var85;
				(var85 = new Level()).setData(8, 8, 8, new byte[512]);
				setLevel(var85);
			} else {
				try {
					if (!isLevelLoaded) {
						Level var11 = null;
						if (gamemode instanceof CreativeGameMode) {
							if ((var11 = new LevelLoader().load(new File(mcDir,
									"levelc.cw"), player)) != null) {
								progressBar.setText("Loading saved map..");
								setLevel(var11);
								isSinglePlayer = true;
							}
						} else if (gamemode instanceof SurvivalGameMode) {
							if ((var11 = new LevelLoader().load(new File(mcDir,
									"levels.cw"), player)) != null) {
								setLevel(var11);
							}
						}
					}
				} catch (Exception var54) {
					var54.printStackTrace();
				}

				if (level == null) {
					generateLevel(1);
				}
			}

			particleManager = new ParticleManager(level, textureManager);
			if (isLevelLoaded) {
				try {
					cursor = new Cursor(16, 16, 0, 0, 1, var9, (IntBuffer) null);
				} catch (LWJGLException var53) {
					var53.printStackTrace();
				}
			}
			try {
				soundPlayer = new SoundPlayer(settings);
				SoundPlayer var4 = soundPlayer;

				try {
					AudioFormat var67 = new AudioFormat(44100.0F, 16, 2, true,
							true);
					var4.dataLine = AudioSystem.getSourceDataLine(var67);
					var4.dataLine.open(var67, 4410);
					var4.dataLine.start();
					var4.running = true;
					Thread var72;
					(var72 = new Thread(var4)).setDaemon(true);
					var72.setPriority(10);
					var72.start();
				} catch (Exception var51) {
					var51.printStackTrace();
					var4.running = false;
				}

			} catch (Exception var52) {
				;
			}

			checkGLError("Post startup");
			hud = new HUDScreen(this, width, height);
			new SkinDownloadThread(this, skinServer).start();
			if (server != null && session != null) {
				networkManager = new NetworkManager(this, server, port,
						session.username, session.mppass);
			}
		} catch (Exception var62) {
			var62.printStackTrace();
			JOptionPane.showMessageDialog((Component) null, var62.toString(),
					"Failed to start ClassiCube", 0);
			return;
		}

		long var13 = System.currentTimeMillis();
		int var15 = 0;
		try {
			while (isRunning) {
				if (isWaiting) {
					Thread.sleep(100L);
				} else {
					if (canvas == null && Display.isCloseRequested()) {
						isRunning = false;
					}

					if (!Display.isFullscreen()
							&& (canvas.getWidth() != Display.getDisplayMode()
									.getWidth() || canvas.getHeight() != Display
									.getDisplayMode().getHeight())) {
						DisplayMode displayMode = new DisplayMode(
								canvas.getWidth(), canvas.getHeight());
						try {
							Display.setDisplayMode(displayMode);
						} catch (LWJGLException e) {
							e.printStackTrace();
						}

						resize();
					}

					try {
						Timer timerCopy = timer;
						long var16;
						long var18 = (var16 = System.currentTimeMillis())
								- timerCopy.lastSysClock;
						long var20 = System.nanoTime() / 1000000L;
						double var24;
						if (var18 > 1000L) {
							long var22 = var20 - timerCopy.lastHRClock;
							var24 = (double) var18 / (double) var22;
							timerCopy.adjustment += (var24 - timerCopy.adjustment) * 0.20000000298023224D;
							timerCopy.lastSysClock = var16;
							timerCopy.lastHRClock = var20;
						}

						if (var18 < 0L) {
							timerCopy.lastSysClock = var16;
							timerCopy.lastHRClock = var20;
						}

						double var95;
						var24 = ((var95 = var20 / 1000.0D) - timerCopy.lastHR)
								* timerCopy.adjustment;
						timerCopy.lastHR = var95;
						if (var24 < 0.0D) {
							var24 = 0.0D;
						}

						if (var24 > 1.0D) {
							var24 = 1.0D;
						}

						timerCopy.elapsedDelta = (float) (timerCopy.elapsedDelta + var24
								* timerCopy.speed * timerCopy.tps);
						timerCopy.elapsedTicks = (int) timerCopy.elapsedDelta;
						if (timerCopy.elapsedTicks > 100) {
							timerCopy.elapsedTicks = 100;
						}

						timerCopy.elapsedDelta -= timerCopy.elapsedTicks;
						timerCopy.delta = timerCopy.elapsedDelta;

						for (int var64 = 0; var64 < timer.elapsedTicks; ++var64) {
							++ticks;
							tick();
						}

						checkGLError("Pre render");
						GL11.glEnable(3553);

						if (!isOnline) {
							gamemode.applyCracks(timer.delta);
							float var65 = timer.delta;
							if (renderer.displayActive && !Display.isActive()) {
								renderer.minecraft.pause();
							}

							renderer.displayActive = Display.isActive();
							int var68;
							int var70;
							int var86;
							int var81;
							if (renderer.minecraft.hasMouse) {
								var81 = 0;
								var86 = 0;
								if (renderer.minecraft.isLevelLoaded) {
									if (renderer.minecraft.canvas != null) {
										Point var90;
										var70 = (var90 = renderer.minecraft.canvas
												.getLocationOnScreen()).x
												+ renderer.minecraft.width / 2;
										var68 = var90.y
												+ renderer.minecraft.height / 2;
										Point var75;
										var81 = (var75 = MouseInfo
												.getPointerInfo().getLocation()).x
												- var70;
										var86 = -(var75.y - var68);
										renderer.minecraft.robot.mouseMove(
												var70, var68);
									} else {
										Mouse.setCursorPosition(
												renderer.minecraft.width / 2,
												renderer.minecraft.height / 2);
									}
								} else {
									var81 = Mouse.getDX();
									var86 = Mouse.getDY();
								}

								byte var91 = 1;
								if (renderer.minecraft.settings.invertMouse) {
									var91 = -1;
								}

								renderer.minecraft.player.turn(var81, var86
										* var91);
							}

							if (!renderer.minecraft.isOnline) {
								var81 = renderer.minecraft.width * 240
										/ renderer.minecraft.height;
								var86 = renderer.minecraft.height * 240
										/ renderer.minecraft.height;
								int var94 = Mouse.getX() * var81
										/ renderer.minecraft.width;
								var70 = var86 - Mouse.getY() * var86
										/ renderer.minecraft.height - 1;
								if (renderer.minecraft.level != null
										&& player != null) {
									float var80 = var65;
									float var29 = player.xRotO
											+ (player.xRot - player.xRotO)
											* var65;
									float var30 = player.yRotO
											+ (player.yRot - player.yRotO)
											* var65;
									Vec3D var31 = renderer
											.getPlayerVector(var65);
									float var32 = MathHelper
											.cos(-var30 * 0.017453292F - 3.1415927F);
									float var69 = MathHelper
											.sin(-var30 * 0.017453292F - 3.1415927F);
									float var74 = MathHelper
											.cos(-var29 * 0.017453292F);
									float var33 = MathHelper
											.sin(-var29 * 0.017453292F);
									float var34 = var69 * var74;
									float var87 = var32 * var74;
									float reachDistance = renderer.minecraft.gamemode
											.getReachDistance();
									Vec3D vec3D = var31.add(var34
											* reachDistance, var33
											* reachDistance, var87
											* reachDistance);
									renderer.minecraft.selected = renderer.minecraft.level
											.clip(var31, vec3D);
									var74 = reachDistance;
									if (renderer.minecraft.selected != null) {
										var74 = renderer.minecraft.selected.vec
												.distance(renderer
														.getPlayerVector(var65));
									}

									var31 = renderer.getPlayerVector(var65);
									if (renderer.minecraft.gamemode instanceof CreativeGameMode) {
										reachDistance = 32.0F;
									} else {
										reachDistance = var74;
									}

									vec3D = var31.add(var34 * reachDistance,
											var33 * reachDistance, var87
													* reachDistance);
									renderer.entity = null;
									List<Entity> var37 = renderer.minecraft.level.blockMap
											.getEntities(
													player,
													player.bb
															.expand(var34
																	* reachDistance,
																	var33
																			* reachDistance,
																	var87
																			* reachDistance));
									float var35 = 0.0F;

									for (var81 = 0; var81 < var37.size(); ++var81) {
										Entity var88;
										if ((var88 = var37.get(var81))
												.isPickable()) {
											var74 = 0.1F;
											MovingObjectPosition var78;
											if ((var78 = var88.bb.grow(var74,
													var74, var74).clip(var31,
													vec3D)) != null
													&& ((var74 = var31
															.distance(var78.vec)) < var35 || var35 == 0.0F)) {
												renderer.entity = var88;
												var35 = var74;
											}
										}
									}

									if (renderer.entity != null
											&& !(renderer.minecraft.gamemode instanceof CreativeGameMode)) {
										renderer.minecraft.selected = new MovingObjectPosition(
												renderer.entity);
									}

									int var77 = 0;

									while (true) {
										if (var77 >= 2) {
											GL11.glColorMask(true, true, true,
													false);
											break;
										}

										if (renderer.minecraft.settings.anaglyph) {
											if (var77 == 0) {
												GL11.glColorMask(false, true,
														true, false);
											} else {
												GL11.glColorMask(true, false,
														false, false);
											}
										}

										Player var126 = renderer.minecraft.player;
										Level var119 = renderer.minecraft.level;
										LevelRenderer var89 = renderer.minecraft.levelRenderer;
										ParticleManager var93 = renderer.minecraft.particleManager;
										GL11.glViewport(0, 0,
												renderer.minecraft.width,
												renderer.minecraft.height);
										Level var26 = renderer.minecraft.level;
										var29 = 1.0F / (4 - renderer.minecraft.settings.viewDistance);
										var29 = 1.0F - (float) Math.pow(var29,
												0.25D);
										var30 = (var26.skyColor >> 16 & 255) / 255.0F;
										float var117 = (var26.skyColor >> 8 & 255) / 255.0F;
										var32 = (var26.skyColor & 255) / 255.0F;
										renderer.fogRed = (var26.fogColor >> 16 & 255) / 255.0F;
										renderer.fogBlue = (var26.fogColor >> 8 & 255) / 255.0F;
										renderer.fogGreen = (var26.fogColor & 255) / 255.0F;
										renderer.fogRed += (var30 - renderer.fogRed)
												* var29;
										renderer.fogBlue += (var117 - renderer.fogBlue)
												* var29;
										renderer.fogGreen += (var32 - renderer.fogGreen)
												* var29;
										renderer.fogRed *= renderer.fogColorMultiplier;
										renderer.fogBlue *= renderer.fogColorMultiplier;
										renderer.fogGreen *= renderer.fogColorMultiplier;
										Block var73;
										if ((var73 = Block.blocks[var26
												.getTile(
														(int) player.x,
														(int) (player.y + 0.12F),
														(int) player.z)]) != null
												&& var73.getLiquidType() != LiquidType.notLiquid) {
											LiquidType var79;
											if ((var79 = var73.getLiquidType()) == LiquidType.water) {
												renderer.fogRed = 0.02F;
												renderer.fogBlue = 0.02F;
												renderer.fogGreen = 0.2F;
											} else if (var79 == LiquidType.lava) {
												renderer.fogRed = 0.6F;
												renderer.fogBlue = 0.1F;
												renderer.fogGreen = 0.0F;
											}
										}

										if (renderer.minecraft.settings.anaglyph) {
											var74 = (renderer.fogRed * 30.0F
													+ renderer.fogBlue * 59.0F + renderer.fogGreen * 11.0F) / 100.0F;
											var33 = (renderer.fogRed * 30.0F + renderer.fogBlue * 70.0F) / 100.0F;
											var34 = (renderer.fogRed * 30.0F + renderer.fogGreen * 70.0F) / 100.0F;
											renderer.fogRed = var74;
											renderer.fogBlue = var33;
											renderer.fogGreen = var34;
										}

										GL11.glClearColor(renderer.fogRed,
												renderer.fogBlue,
												renderer.fogGreen, 0.0F);
										GL11.glClear(16640);
										renderer.fogColorMultiplier = 1.0F;
										GL11.glEnable(2884);
										renderer.fogEnd = 512 >> (renderer.minecraft.settings.viewDistance << 1);
										GL11.glMatrixMode(5889);
										GL11.glLoadIdentity();
										var29 = 0.07F;
										if (renderer.minecraft.settings.anaglyph) {
											GL11.glTranslatef(
													-((var77 << 1) - 1) * var29,
													0.0F, 0.0F);
										}

										Player var116 = renderer.minecraft.player;
										var69 = 70.0F;
										if (var116.health <= 0) {
											var74 = var116.deathTime + var80;
											var69 /= (1.0F - 500.0F / (var74 + 500.0F)) * 2.0F + 1.0F;
										}

										GLU.gluPerspective(
												var69,
												(float) renderer.minecraft.width
														/ (float) renderer.minecraft.height,
												0.05F, renderer.fogEnd);
										GL11.glMatrixMode(5888);
										GL11.glLoadIdentity();
										if (renderer.minecraft.settings.anaglyph) {
											GL11.glTranslatef(
													((var77 << 1) - 1) * 0.1F,
													0.0F, 0.0F);
										}

										renderer.hurtEffect(var80);
										renderer.applyBobbing(
												var80,
												renderer.minecraft.settings.viewBobbing);

										var116 = renderer.minecraft.player;
										GL11.glTranslatef(0.0F, 0.0F,
												cameraDistance);

										GL11.glRotatef(var116.xRotO
												+ (var116.xRot - var116.xRotO)
												* var80, 1.0F, 0.0F, 0.0F);
										GL11.glRotatef(var116.yRotO
												+ (var116.yRot - var116.yRotO)
												* var80, 0.0F, 1.0F, 0.0F);
										var69 = var116.xo
												+ (var116.x - var116.xo)
												* var80;
										var74 = var116.yo
												+ (var116.y - var116.yo)
												* var80;
										var33 = var116.zo
												+ (var116.z - var116.zo)
												* var80;
										GL11.glTranslatef(-var69, -var74,
												-var33);
										Frustrum var76 = FrustrumImpl
												.getInstance();
										Frustrum var100 = var76;
										LevelRenderer var101 = renderer.minecraft.levelRenderer;

										int var98;
										for (var98 = 0; var98 < var101.chunkCache.length; ++var98) {
											var101.chunkCache[var98]
													.clip(var100);
										}

										var101 = renderer.minecraft.levelRenderer;
										Collections
												.sort(renderer.minecraft.levelRenderer.chunks,
														new ChunkDirtyDistanceComparator(
																var126));
										var98 = var101.chunks.size() - 1;
										int var105;
										if ((var105 = var101.chunks.size()) > 4) {
											var105 = 4;
										}

										int var104;
										for (var104 = 0; var104 < var105; ++var104) {
											Chunk chunkToUpdate = var101.chunks
													.remove(var98 - var104);
											chunkToUpdate.update();
											chunkToUpdate.loaded = false;
										}

										renderer.updateFog();
										GL11.glEnable(2912);
										var89.sortChunks(var126, 0);
										int var83;
										int var110;
										ShapeRenderer shapeRenderer = ShapeRenderer.instance;
										int var114;
										int var125;
										int var122;
										int var120;
										if (var119.isSolid(var126.x, var126.y,
												var126.z, 0.1F)) {
											var120 = (int) var126.x;
											var83 = (int) var126.y;
											var110 = (int) var126.z;

											for (var122 = var120 - 1; var122 <= var120 + 1; ++var122) {
												for (var125 = var83 - 1; var125 <= var83 + 1; ++var125) {
													for (int var38 = var110 - 1; var38 <= var110 + 1; ++var38) {
														var105 = var38;
														var98 = var125;
														int var99 = var122;
														if ((var104 = var89.level
																.getTile(
																		var122,
																		var125,
																		var38)) != 0
																&& Block.blocks[var104]
																		.isSolid()) {
															GL11.glColor4f(
																	0.2F, 0.2F,
																	0.2F, 1.0F);
															GL11.glDepthFunc(513);

															shapeRenderer
																	.begin();

															for (var114 = 0; var114 < 6; ++var114) {
																Block.blocks[var104]
																		.renderInside(
																				shapeRenderer,
																				var99,
																				var98,
																				var105,
																				var114);
															}

															shapeRenderer.end();
															GL11.glCullFace(1028);
															shapeRenderer
																	.begin();

															for (var114 = 0; var114 < 6; ++var114) {
																Block.blocks[var104]
																		.renderInside(
																				shapeRenderer,
																				var99,
																				var98,
																				var105,
																				var114);
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
										Vec3D var103 = renderer
												.getPlayerVector(var80);
										var89.level.blockMap.render(var103,
												var76, var89.textureManager,
												var80);
										renderer.setLighting(false);
										renderer.updateFog();
										float var107 = var80;
										ParticleManager var96 = var93;
										var29 = -MathHelper
												.cos(var126.yRot * 3.1415927F / 180.0F);
										var117 = -(var30 = -MathHelper
												.sin(var126.yRot * 3.1415927F / 180.0F))
												* MathHelper
														.sin(var126.xRot * 3.1415927F / 180.0F);
										var32 = var29
												* MathHelper
														.sin(var126.xRot * 3.1415927F / 180.0F);
										var69 = MathHelper
												.cos(var126.xRot * 3.1415927F / 180.0F);

										for (var83 = 0; var83 < 2; ++var83) {
											if (var96.particles[var83].size() != 0) {
												var110 = 0;
												if (var83 == 0) {
													var110 = var96.textureManager
															.load("/particles.png");
												}

												if (var83 == 1) {
													var110 = var96.textureManager
															.load("/terrain.png");
												}

												GL11.glBindTexture(3553, var110);
												shapeRenderer.begin();

												for (var120 = 0; var120 < var96.particles[var83]
														.size(); ++var120) {
													((Particle) var96.particles[var83]
															.get(var120))
															.render(shapeRenderer,
																	var107,
																	var29,
																	var69,
																	var30,
																	var117,
																	var32);
												}

												shapeRenderer.end();
											}
										}

										GL11.glBindTexture(3553,
												var89.textureManager
														.load("/rock.png"));
										GL11.glEnable(3553);
										GL11.glCallList(var89.listId); // rock
										// edges
										renderer.updateFog();
										var101 = var89;

										GL11.glBindTexture(3553,
												var89.textureManager
														.load("/clouds.png"));
										GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
										var107 = (var89.level.cloudColor >> 16 & 255) / 255.0F;
										var29 = (var89.level.cloudColor >> 8 & 255) / 255.0F;
										var30 = (var89.level.cloudColor & 255) / 255.0F;
										if (var89.minecraft.settings.anaglyph) {
											var117 = (var107 * 30.0F + var29
													* 59.0F + var30 * 11.0F) / 100.0F;
											var32 = (var107 * 30.0F + var29 * 70.0F) / 100.0F;
											var69 = (var107 * 30.0F + var30 * 70.0F) / 100.0F;
											var107 = var117;
											var29 = var32;
											var30 = var69;
										}

										var74 = 0.0F;
										var33 = 4.8828125E-4F;
                                                                                if (level.cloudLevel < 0) level.cloudLevel = var89.level.height + 2;
										var74 = level.cloudLevel;
										var34 = (var89.ticks + var80) * var33
												* 0.03F;
										var35 = 0.0F;
                                                                                if (settings.showClouds)
                                                                                {
                                                                                        shapeRenderer.begin();
                                                                                        shapeRenderer.color(var107, var29,
                                                                                                        var30);

                                                                                        for (var86 = -2048; var86 < var101.level.width + 2048; var86 += 512) {
                                                                                                for (var125 = -2048; var125 < var101.level.length + 2048; var125 += 512) {
                                                                                                        shapeRenderer.vertexUV(var86,
                                                                                                                        var74, var125 + 512,
                                                                                                                        var86 * var33 + var34,
                                                                                                                        (var125 + 512) * var33);
                                                                                                        shapeRenderer.vertexUV(
                                                                                                                        var86 + 512, var74,
                                                                                                                        var125 + 512,
                                                                                                                        (var86 + 512) * var33
                                                                                                                                        + var34,
                                                                                                                        (var125 + 512) * var33);
                                                                                                        shapeRenderer.vertexUV(
                                                                                                                        var86 + 512, var74,
                                                                                                                        var125,
                                                                                                                        (var86 + 512) * var33
                                                                                                                                        + var34, var125
                                                                                                                                        * var33);
                                                                                                        shapeRenderer.vertexUV(var86,
                                                                                                                        var74, var125,
                                                                                                                        var86 * var33 + var34,
                                                                                                                        var125 * var33);
                                                                                                        shapeRenderer.vertexUV(var86,
                                                                                                                        var74, var125,
                                                                                                                        var86 * var33 + var34,
                                                                                                                        var125 * var33);
                                                                                                        shapeRenderer.vertexUV(
                                                                                                                        var86 + 512, var74,
                                                                                                                        var125,
                                                                                                                        (var86 + 512) * var33
                                                                                                                                        + var34, var125
                                                                                                                                        * var33);
                                                                                                        shapeRenderer.vertexUV(
                                                                                                                        var86 + 512, var74,
                                                                                                                        var125 + 512,
                                                                                                                        (var86 + 512) * var33
                                                                                                                                        + var34,
                                                                                                                        (var125 + 512) * var33);
                                                                                                        shapeRenderer.vertexUV(var86,
                                                                                                                        var74, var125 + 512,
                                                                                                                        var86 * var33 + var34,
                                                                                                                        (var125 + 512) * var33);
                                                                                                }
                                                                                        }

                                                                                        shapeRenderer.end();
                                                                                }
										GL11.glDisable(3553);

										shapeRenderer.begin();
										var34 = (var101.level.skyColor >> 16 & 255) / 255.0F;
										var35 = (var101.level.skyColor >> 8 & 255) / 255.0F;
										var87 = (var101.level.skyColor & 255) / 255.0F;
										if (var101.minecraft.settings.anaglyph) {
											reachDistance = (var34 * 30.0F
													+ var35 * 59.0F + var87 * 11.0F) / 100.0F;
											var69 = (var34 * 30.0F + var35 * 70.0F) / 100.0F;
											var74 = (var34 * 30.0F + var87 * 70.0F) / 100.0F;
											var34 = reachDistance;
											var35 = var69;
											var87 = var74;
										}

										shapeRenderer
												.color(var34, var35, var87);
										var74 = var101.level.height + 10;

										for (var125 = -2048; var125 < var101.level.width + 2048; var125 += 512) {
											for (var68 = -2048; var68 < var101.level.length + 2048; var68 += 512) {
												shapeRenderer.vertex(var125,
														var74, var68);
												shapeRenderer.vertex(
														var125 + 512, var74,
														var68);
												shapeRenderer.vertex(
														var125 + 512, var74,
														var68 + 512);
												shapeRenderer.vertex(var125,
														var74, var68 + 512);
											}
										}

										shapeRenderer.end();
										GL11.glEnable(3553);
										renderer.updateFog();
										int var108;
										if (renderer.minecraft.selected != null) {
											GL11.glDisable(3008);
											MovingObjectPosition var10001 = renderer.minecraft.selected;
											var105 = var126.inventory
													.getSelected();
											MovingObjectPosition var102 = var10001;
											var101 = var89;

											GL11.glEnable(3042);
											GL11.glEnable(3008);
											GL11.glBlendFunc(770, 1);
											GL11.glColor4f(
													1.0F,
													1.0F,
													1.0F,
													(MathHelper.sin(System
															.currentTimeMillis() / 100.0F) * 0.2F + 0.4F) * 0.5F);
											if (var89.cracks > 0.0F) {
												GL11.glBlendFunc(774, 768);
												var108 = var89.textureManager
														.load("/terrain.png");
												GL11.glBindTexture(3553, var108);
												GL11.glColor4f(1.0F, 1.0F,
														1.0F, 0.5F);
												GL11.glPushMatrix();
												Block var10000 = (var114 = var89.level
														.getTile(var102.x,
																var102.y,
																var102.z)) > 0 ? Block.blocks[var114]
														: null;
												var73 = var10000;
												var74 = (var10000.x1 + var73.x2) / 2.0F;
												var33 = (var73.y1 + var73.y2) / 2.0F;
												var34 = (var73.z1 + var73.z2) / 2.0F;
												GL11.glTranslatef(var102.x
														+ var74, var102.y
														+ var33, var102.z
														+ var34);
												var35 = 1.01F;
												GL11.glScalef(1.0F, var35,
														var35);
												GL11.glTranslatef(
														-(var102.x + var74),
														-(var102.y + var33),
														-(var102.z + var34));
												shapeRenderer.begin();
												shapeRenderer.noColor();
												GL11.glDepthMask(false);
												for (var86 = 0; var86 < 6; ++var86) {
													var73.renderSide(
															shapeRenderer,
															var102.x,
															var102.y,
															var102.z,
															var86,
															240 + (int) (var101.cracks * 10.0F));
												}

												shapeRenderer.end();
												GL11.glDepthMask(true);
												GL11.glPopMatrix();
											}

											GL11.glDisable(3042);
											GL11.glDisable(3008);
											var10001 = renderer.minecraft.selected;
											var126.inventory.getSelected();
											var102 = var10001;
											GL11.glEnable(3042);
											GL11.glBlendFunc(770, 771);
											GL11.glColor4f(0.0F, 0.0F, 0.0F,
													0.4F);
											GL11.glLineWidth(2.0F);
											GL11.glDisable(3553);
											GL11.glDepthMask(false);
											var29 = 0.002F;
											if ((var104 = var89.level.getTile(
													var102.x, var102.y,
													var102.z)) > 0) {
												AABB var111 = Block.blocks[var104]
														.getSelectionBox(
																var102.x,
																var102.y,
																var102.z).grow(
																var29, var29,
																var29);
												GL11.glBegin(3);
												GL11.glVertex3f(var111.x0,
														var111.y0, var111.z0);
												GL11.glVertex3f(var111.x1,
														var111.y0, var111.z0);
												GL11.glVertex3f(var111.x1,
														var111.y0, var111.z1);
												GL11.glVertex3f(var111.x0,
														var111.y0, var111.z1);
												GL11.glVertex3f(var111.x0,
														var111.y0, var111.z0);
												GL11.glEnd();
												GL11.glBegin(3);
												GL11.glVertex3f(var111.x0,
														var111.y1, var111.z0);
												GL11.glVertex3f(var111.x1,
														var111.y1, var111.z0);
												GL11.glVertex3f(var111.x1,
														var111.y1, var111.z1);
												GL11.glVertex3f(var111.x0,
														var111.y1, var111.z1);
												GL11.glVertex3f(var111.x0,
														var111.y1, var111.z0);
												GL11.glEnd();
												GL11.glBegin(1);
												GL11.glVertex3f(var111.x0,
														var111.y0, var111.z0);
												GL11.glVertex3f(var111.x0,
														var111.y1, var111.z0);
												GL11.glVertex3f(var111.x1,
														var111.y0, var111.z0);
												GL11.glVertex3f(var111.x1,
														var111.y1, var111.z0);
												GL11.glVertex3f(var111.x1,
														var111.y0, var111.z1);
												GL11.glVertex3f(var111.x1,
														var111.y1, var111.z1);
												GL11.glVertex3f(var111.x0,
														var111.y0, var111.z1);
												GL11.glVertex3f(var111.x0,
														var111.y1, var111.z1);
												GL11.glEnd();
											}

											GL11.glDepthMask(true);
											GL11.glEnable(3553);
											GL11.glDisable(3042);
											GL11.glEnable(3008);
										}

										GL11.glBlendFunc(770, 771);
										renderer.updateFog();
										GL11.glEnable(3553);
										GL11.glEnable(3042);
										GL11.glBindTexture(3553,
												var89.textureManager
														.load("/water.png"));

										GL11.glCallList(var89.listId + 1);
										GL11.glDisable(3042);
										GL11.glEnable(3042);
										GL11.glColorMask(false, false, false,
												false);

										var120 = var89.sortChunks(var126, 1);
										GL11.glColorMask(true, true, true, true);
										if (renderer.minecraft.settings.anaglyph) {
											if (var77 == 0) {
												GL11.glColorMask(false, true,
														true, false);
											} else {
												GL11.glColorMask(true, false,
														false, false);
											}
										}

										if (var120 > 0) {
											GL11.glBindTexture(
													3553,
													var89.textureManager
															.load("/terrain.png"));
											GL11.glCallLists(var89.buffer);
										}

										GL11.glDepthMask(true);
										GL11.glDisable(3042);
										GL11.glDisable(2912);
										// -------------------

										Collections
												.sort(selectionBoxes,
														new SelectionBoxDistanceComparator(
																player));
										for (int i = 0; i < selectionBoxes
												.size(); i++) {
											CustomAABB bounds = selectionBoxes
													.get(i).Bounds;
											ColorCache color = selectionBoxes
													.get(i).Color;
											GL11.glLineWidth(2);

											GL11.glDisable(3042);
											GL11.glDisable(3008);
											GL11.glEnable(3042);
											GL11.glBlendFunc(770, 771);
											GL11.glColor4f(color.R, color.G,
													color.B, color.A);
											GL11.glDisable(3553);
											GL11.glDepthMask(false);
											GL11.glDisable(GL11.GL_CULL_FACE);
											// GL11.glBegin(GL11.GL_QUADS);

											// Front Face

											// Bottom Left
											shapeRenderer.begin();
											shapeRenderer.vertex(bounds.x0,
													bounds.y0, bounds.z1);
											// Bottom Right
											shapeRenderer.vertex(bounds.x1,
													bounds.y0, bounds.z1);
											// Top Right
											shapeRenderer.vertex(bounds.x1,
													bounds.y1, bounds.z1);
											// Top Left
											shapeRenderer.vertex(bounds.x0,
													bounds.y1, bounds.z1);

											// Back Face

											// Bottom Right
											shapeRenderer.vertex(bounds.x0,
													bounds.y0, bounds.z0);
											// Top Right
											shapeRenderer.vertex(bounds.x0,
													bounds.y1, bounds.z0);
											// Top Left
											shapeRenderer.vertex(bounds.x1,
													bounds.y1, bounds.z0);
											// Bottom Left
											shapeRenderer.vertex(bounds.x1,
													bounds.y0, bounds.z0);

											// Top Face
											// Top Left

											// Bottom Left
											shapeRenderer.vertex(bounds.x0,
													bounds.y1, bounds.z0);
											shapeRenderer.vertex(bounds.x0,
													bounds.y1, bounds.z1);
											// Bottom Right
											shapeRenderer.vertex(bounds.x1,
													bounds.y1, bounds.z1);
											// Top Right
											shapeRenderer.vertex(bounds.x1,
													bounds.y1, bounds.z0);

											// Bottom Face

											// Top Right
											shapeRenderer.vertex(bounds.x0,
													bounds.y0, bounds.z0);
											// Top Left
											shapeRenderer.vertex(bounds.x1,
													bounds.y0, bounds.z0);
											// Bottom Left
											shapeRenderer.vertex(bounds.x1,
													bounds.y0, bounds.z1);
											// Bottom Right
											shapeRenderer.vertex(bounds.x0,
													bounds.y0, bounds.z1);

											// Right face

											// Bottom Right
											shapeRenderer.vertex(bounds.x1,
													bounds.y0, bounds.z0);
											// Top Right
											shapeRenderer.vertex(bounds.x1,
													bounds.y1, bounds.z0);
											// Top Left
											shapeRenderer.vertex(bounds.x1,
													bounds.y1, bounds.z1);
											// Bottom Left
											shapeRenderer.vertex(bounds.x1,
													bounds.y0, bounds.z1);

											// Left Face

											// Bottom Left
											shapeRenderer.vertex(bounds.x0,
													bounds.y0, bounds.z0);
											// Bottom Right
											shapeRenderer.vertex(bounds.x0,
													bounds.y0, bounds.z1);
											// Top Right
											shapeRenderer.vertex(bounds.x0,
													bounds.y1, bounds.z1);
											// Top Left
											shapeRenderer.vertex(bounds.x0,
													bounds.y1, bounds.z0);
											shapeRenderer.end();

											GL11.glColor4f(color.R, color.G,
													color.B, color.A + 0.2F);

											shapeRenderer.startDrawing(3);
											shapeRenderer.vertex(bounds.x0,
													bounds.y0, bounds.z0);
											shapeRenderer.vertex(bounds.x1,
													bounds.y0, bounds.z0);
											shapeRenderer.vertex(bounds.x1,
													bounds.y0, bounds.z1);
											shapeRenderer.vertex(bounds.x0,
													bounds.y0, bounds.z1);
											shapeRenderer.vertex(bounds.x0,
													bounds.y0, bounds.z0);
											shapeRenderer.end();
											shapeRenderer.startDrawing(3);
											shapeRenderer.vertex(bounds.x0,
													bounds.y1, bounds.z0);
											shapeRenderer.vertex(bounds.x1,
													bounds.y1, bounds.z0);
											shapeRenderer.vertex(bounds.x1,
													bounds.y1, bounds.z1);
											shapeRenderer.vertex(bounds.x0,
													bounds.y1, bounds.z1);
											shapeRenderer.vertex(bounds.x0,
													bounds.y1, bounds.z0);
											shapeRenderer.end();
											shapeRenderer.startDrawing(1);
											shapeRenderer.vertex(bounds.x0,
													bounds.y0, bounds.z0);
											shapeRenderer.vertex(bounds.x0,
													bounds.y1, bounds.z0);
											shapeRenderer.vertex(bounds.x1,
													bounds.y0, bounds.z0);
											shapeRenderer.vertex(bounds.x1,
													bounds.y1, bounds.z0);
											shapeRenderer.vertex(bounds.x1,
													bounds.y0, bounds.z1);
											shapeRenderer.vertex(bounds.x1,
													bounds.y1, bounds.z1);
											shapeRenderer.vertex(bounds.x0,
													bounds.y0, bounds.z1);
											shapeRenderer.vertex(bounds.x0,
													bounds.y1, bounds.z1);
											shapeRenderer.end();

											GL11.glDepthMask(true);
											GL11.glEnable(3553);
											GL11.glDisable(3042);
											GL11.glEnable(3008);

											GL11.glEnable(GL11.GL_CULL_FACE);

											// ------------------
										}

										if (renderer.minecraft.isRaining
												|| renderer.minecraft.isSnowing) {
											float var97 = var80;
											float speed = 1.0F;
											Level var109 = renderer.minecraft.level;
											var104 = (int) player.x;
											var108 = (int) player.y;
											var114 = (int) player.z;
											GL11.glDisable(2884);
											GL11.glNormal3f(0.0F, 1.0F, 0.0F);
											GL11.glEnable(3042);
											GL11.glBlendFunc(770, 771);
											if (renderer.minecraft.isRaining) {
												GL11.glBindTexture(
														3553,
														renderer.minecraft.textureManager
																.load("/rain.png"));
											} else if (renderer.minecraft.isSnowing) {
												GL11.glBindTexture(
														3553,
														renderer.minecraft.textureManager
																.load("/snow.png"));
												speed = 0.2F;
											}

											for (var110 = var104 - 5; var110 <= var104 + 5; ++var110) {
												for (var122 = var114 - 5; var122 <= var114 + 5; ++var122) {
													var120 = var109
															.getHighestTile(
																	var110,
																	var122);
													var86 = var108 - 5;
													var125 = var108 + 5;
													if (var86 < var120) {
														var86 = var120;
													}

													if (var125 < var120) {
														var125 = var120;
													}

													if (var86 != var125) {
														var74 = ((renderer.levelTicks
																+ var110 * 3121 + var122 * 418711) % 32 + var97)
																/ 32.0F * speed;
														float var124 = var110
																+ 0.5F
																- player.x;
														var35 = var122 + 0.5F
																- player.z;
														float var92 = MathHelper
																.sqrt(var124
																		* var124
																		+ var35
																		* var35) / 5;
														GL11.glColor4f(
																1.0F,
																1.0F,
																1.0F,
																(1.0F - var92
																		* var92) * 0.7F);
														shapeRenderer.begin();
														shapeRenderer.vertexUV(
																var110, var86,
																var122, 0.0F,
																var86 * 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														shapeRenderer.vertexUV(
																var110 + 1,
																var86,
																var122 + 1,
																2.0F, var86
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														shapeRenderer.vertexUV(
																var110 + 1,
																var125,
																var122 + 1,
																2.0F, var125
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														shapeRenderer.vertexUV(
																var110, var125,
																var122, 0.0F,
																var125 * 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														shapeRenderer.vertexUV(
																var110, var86,
																var122 + 1,
																0.0F, var86
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														shapeRenderer.vertexUV(
																var110 + 1,
																var86, var122,
																2.0F, var86
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														shapeRenderer.vertexUV(
																var110 + 1,
																var125, var122,
																2.0F, var125
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														shapeRenderer.vertexUV(
																var110, var125,
																var122 + 1,
																0.0F, var125
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														shapeRenderer.end();
													}
												}
											}

											GL11.glEnable(2884);
											GL11.glDisable(3042);
										}
										if (!isSinglePlayer
												&& networkManager != null
												&& networkManager.players != null
												&& networkManager.players
														.size() > 0) {
											if (settings.ShowNames == 1
													&& player.userType >= 100) {
												for (int n = 0; n < networkManager.players
														.values().size(); n++) {
													NetworkPlayer np = (NetworkPlayer) networkManager.players
															.values().toArray()[n];
													if (np != null) {
														np.renderHover(
																renderer.minecraft.textureManager,
																var80);
													}
												}
											} else {
												if (renderer.entity != null) {
													renderer.entity
															.renderHover(
																	renderer.minecraft.textureManager,
																	var80);
												}
											}
										}

										GL11.glClear(256);
										GL11.glLoadIdentity();
										if (renderer.minecraft.settings.anaglyph) {
											GL11.glTranslatef(
													((var77 << 1) - 1) * 0.1F,
													0.0F, 0.0F);
										}

										renderer.hurtEffect(var80);
										renderer.applyBobbing(
												var80,
												renderer.minecraft.settings.viewBobbing);

										HeldBlock heldBlock = renderer.heldBlock;
										var117 = renderer.heldBlock.lastPos
												+ (heldBlock.pos - heldBlock.lastPos)
												* var80;
										var116 = heldBlock.minecraft.player;
										GL11.glPushMatrix();
										GL11.glRotatef(var116.xRotO
												+ (var116.xRot - var116.xRotO)
												* var80, 1.0F, 0.0F, 0.0F);
										GL11.glRotatef(var116.yRotO
												+ (var116.yRot - var116.yRotO)
												* var80, 0.0F, 1.0F, 0.0F);
										heldBlock.minecraft.renderer
												.setLighting(true);
										GL11.glPopMatrix();
										GL11.glPushMatrix();
										var69 = 0.8F;
										if (heldBlock.moving) {
											var33 = MathHelper
													.sin((var74 = (heldBlock.offset + var80) / 7.0F) * 3.1415927F);
											GL11.glTranslatef(
													-MathHelper
															.sin(MathHelper
																	.sqrt(var74) * 3.1415927F) * 0.4F,
													MathHelper.sin(MathHelper
															.sqrt(var74) * 3.1415927F * 2.0F) * 0.2F,
													-var33 * 0.2F);
										}

										GL11.glTranslatef(0.7F * var69, -0.65F
												* var69 - (1.0F - var117)
												* 0.6F, -0.9F * var69);
										GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
										GL11.glEnable(2977);
										if (heldBlock.moving) {
											var33 = MathHelper
													.sin((var74 = (heldBlock.offset + var80) / 7.0F)
															* var74
															* 3.1415927F);
											GL11.glRotatef(
													MathHelper
															.sin(MathHelper
																	.sqrt(var74) * 3.1415927F) * 80.0F,
													0.0F, 1.0F, 0.0F);
											GL11.glRotatef(-var33 * 20.0F,
													1.0F, 0.0F, 0.0F);
										}

										ColorCache color = heldBlock.minecraft.level
												.getBrightnessColor(
														(int) var116.x,
														(int) var116.y,
														(int) var116.z);
										GL11.glColor4f(color.R, color.G,
												color.B, 1.0F);

										if (heldBlock.block != null) {
											var34 = 0.4F;
											GL11.glScalef(0.4F, var34, var34);
											GL11.glTranslatef(-0.5F, -0.5F,
													-0.5F);
											if (!settings.thirdPersonMode
													&& canRenderGUI) {
												GL11.glBindTexture(
														3553,
														heldBlock.minecraft.textureManager
																.load("/terrain.png"));
												heldBlock.block
														.renderPreview(shapeRenderer);
											}
										} else {
											var116.bindTexture(heldBlock.minecraft.textureManager);
											GL11.glScalef(1.0F, -1.0F, -1.0F);
											GL11.glTranslatef(0.0F, 0.2F, 0.0F);
											GL11.glRotatef(-120.0F, 0.0F, 0.0F,
													1.0F);
											GL11.glScalef(1.0F, 1.0F, 1.0F);
											var34 = 0.0625F;
											ModelPart var127;
											if (!(var127 = heldBlock.minecraft.player
													.getModel().leftArm).hasList) {
												var127.generateList(var34);
											}

											GL11.glCallList(var127.list);
										}

										GL11.glDisable(2977);
										GL11.glPopMatrix();
										heldBlock.minecraft.renderer
												.setLighting(false);
										if (!renderer.minecraft.settings.anaglyph) {
											break;
										}

										++var77;
									}
									if (currentScreen != null || canRenderGUI) {
										renderer.minecraft.hud
												.render(var65,
														renderer.minecraft.currentScreen != null,
														var94, var70);
									}
								} else {
									GL11.glViewport(0, 0,
											renderer.minecraft.width,
											renderer.minecraft.height);
									GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
									GL11.glClear(16640);
									GL11.glMatrixMode(5889);
									GL11.glLoadIdentity();
									GL11.glMatrixMode(5888);
									GL11.glLoadIdentity();
									renderer.enableGuiMode();
								}

								if (renderer.minecraft.currentScreen != null) {
									renderer.minecraft.currentScreen.render(
											var94, var70);
								}

								Thread.yield();
								Display.update();
							}
						}

						if (settings.limitFramerate) {
							Thread.sleep(5L);
						}

						checkGLError("Post render");
						++var15;
					} catch (Exception var58) {
						setCurrentScreen(new ErrorScreen("Client error",
								"The game broke! [" + var58 + "]"));
						var58.printStackTrace();
					}

					while (System.currentTimeMillis() >= var13 + 1000L) {
						debug = var15 + " fps, " + Chunk.chunkUpdates
								+ " chunk updates";
						Chunk.chunkUpdates = 0;
						var13 += 1000L;
						var15 = 0;
					}
				}
			}

			return;
		} catch (StopGameException var59) {
			;
		} catch (Exception var60) {
			var60.printStackTrace();
			return;
		} finally {
			shutdown();
		}

	}

	public final void setCurrentScreen(GuiScreen var1) {
		if (!(currentScreen instanceof ErrorScreen)) {
			if (currentScreen != null) {
				currentScreen.onClose();
			}

			if (var1 == null && player.health <= 0) {
				var1 = new GameOverScreen();
			}

			currentScreen = var1;
			if (var1 != null) {
				if (hasMouse) {
					player.releaseAllKeys();
					hasMouse = false;
					if (isLevelLoaded) {
						try {
							Mouse.setNativeCursor((Cursor) null);
						} catch (LWJGLException var4) {
							var4.printStackTrace();
						}
					} else {
						Mouse.setGrabbed(false);
					}
				}

				int var2 = width * 240 / height;
				int var3 = height * 240 / height;
				var1.open(this, var2, var3);
				isOnline = false;
				return;
			}
			grabMouse();
		}
	}

	private void setDisplayMode() throws LWJGLException {
		if (displayModes.size() == 0) {
			displayModes.add(new DisplayMode(2560, 1600));
			displayModes.add(new DisplayMode(2880, 1800));
		}
		HashSet<DisplayMode> var1 = new HashSet<DisplayMode>();
		Collections.addAll(var1, Display.getAvailableDisplayModes());
		DisplayMode var2 = Display.getDesktopDisplayMode();

		if (!var1.contains(var2) && getOs() == Minecraft$OS.macos) {
			Iterator<DisplayMode> var3 = displayModes.iterator();

			while (var3.hasNext()) {
				DisplayMode var4 = var3.next();
				boolean var5 = true;
				Iterator<DisplayMode> var6 = var1.iterator();
				DisplayMode var7;

				while (var6.hasNext()) {
					var7 = var6.next();

					if (var7.getBitsPerPixel() == 32
							&& var7.getWidth() == var4.getWidth()
							&& var7.getHeight() == var4.getHeight()) {
						var5 = false;
						break;
					}
				}

				if (!var5) {
					var6 = var1.iterator();

					while (var6.hasNext()) {
						var7 = var6.next();

						if (var7.getBitsPerPixel() == 32
								&& var7.getWidth() == var4.getWidth() / 2
								&& var7.getHeight() == var4.getHeight() / 2) {
							var2 = var7;
							break;
						}
					}
				}
			}
		}

		Display.setDisplayMode(var2);
		width = var2.getWidth();
		height = var2.getHeight();
	}

	public final void setLevel(Level theLevel) {
		if (applet == null
				|| !applet.getDocumentBase().getHost()
						.equalsIgnoreCase("minecraft.net")
				&& !applet.getDocumentBase().getHost()
						.equalsIgnoreCase("www.minecraft.net")
				|| !applet.getCodeBase().getHost()
						.equalsIgnoreCase("minecraft.net")
				&& !applet.getCodeBase().getHost()
						.equalsIgnoreCase("www.minecraft.net")) {
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
			theLevel.rendererContext$5cd64a7f = this;
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
				if (theLevel != null) {
					theLevel.player = player;
					theLevel.addEntity(player);
				}
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
		} catch (Exception var3) {
			;
		}

		if (!isLevelLoaded) {
			try {
				if (level != null && isSinglePlayer) {
					if (level.creativeMode) {
						new LevelSerializer(level).saveMap("levelc");
						// LevelIO.save(level, (new FileOutputStream(new
						// File(mcDir, "levelc.dat"))));
					} else {
						new LevelSerializer(level).saveMap("levels");
						// LevelIO.save(level, (new FileOutputStream(new
						// File(mcDir, "levels.dat"))));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Mouse.destroy();
		Keyboard.destroy();
		if (!isSystemShuttingDown()) {
			Display.destroy();
		}
	}

	public void takeAndSaveScreenshot(int width, int height) {
		try {
			int i = 6400;
			int j = height;
			int size = i * j * 3;

			GL11.glReadBuffer(1028);
			ByteBuffer buffer = ByteBuffer.allocateDirect(size);
			GL11.glReadPixels(0, 0, i, j, 6407, 5121, buffer);

			byte[] pixels = new byte[size];
			buffer.get(pixels);
			pixels = flipPixels(pixels, i, height);

			ColorSpace colorSpace = ColorSpace.getInstance(1000);
			int[] a = { 8, 8, 8 };
			int[] b = { 0, 1, 2 };

			ComponentColorModel colorComp = new ComponentColorModel(colorSpace,
					a, false, false, 3, 0);

			WritableRaster raster = Raster.createInterleavedRaster(
					new DataBufferByte(pixels, pixels.length), width, height,
					i * 3, 3, b, null);

			BufferedImage image = new BufferedImage(colorComp, raster, false,
					null);

			String str = String.format(
					"screenshot_%1$tY%1$tm%1$td%1$tH%1$tM%1$tS.png",
					new Object[] { Calendar.getInstance() });
			Calendar cal = Calendar.getInstance();
			String month = new SimpleDateFormat("MMM").format(cal.getTime());
			String serverName = ProgressBarDisplay.title.toLowerCase()
					.contains("connecting..") ? "" : ProgressBarDisplay.title;
			if (serverName == "Loading level" || serverName == "Connecting.."
					|| serverName == "") {
				serverName = "Singleplayer";
			}
			serverName = FontRenderer.stripColor(serverName);
			serverName = serverName.replaceAll("[^A-Za-z0-9\\._-]+", "_");
			File logDir = new File(Minecraft.getMinecraftDirectory(),
					"/Screenshots/");
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
			if (ImageIO.write(image, "png", new File(monthDir, str))) {
				hud.addChat("&2Screenshot saved into the Screenshots folder");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void tick() {
		if (soundPlayer != null) {
			SoundPlayer var1 = soundPlayer;
			SoundManager var2 = sound;
			if (System.currentTimeMillis() > var2.lastMusic
					&& var2.playMusic(var1, "calm")) {
				var2.lastMusic = System.currentTimeMillis()
						+ var2.random.nextInt(900000) + 300000L;
			}
		}

		gamemode.spawnMob();
		HUDScreen var17 = hud;
		int var16;
		if (canRenderGUI) {
			++hud.ticks;

			for (var16 = 0; var16 < var17.chat.size(); ++var16) {
				++var17.chat.get(var16).time;
			}
		}

		GL11.glBindTexture(3553, textureManager.load("/terrain.png"));
		TextureManager texManager = textureManager;

		for (var16 = 0; var16 < texManager.animations.size(); ++var16) {
			TextureFX texFX;
			(texFX = texManager.animations.get(var16)).anaglyph = texManager.settings.anaglyph;
			texFX.animate();
			if (texManager.textureBuffer.capacity() != texFX.textureData.length) {
				texManager.textureBuffer = BufferUtils
						.createByteBuffer(texFX.textureData.length);
			} else {
				texManager.textureBuffer.clear();
			}
			texManager.textureBuffer.put(texFX.textureData);
			texManager.textureBuffer.position(0)
					.limit(texFX.textureData.length);
			GL11.glTexSubImage2D(3553, 0, texFX.textureId % 16 << 4,
					texFX.textureId / 16 << 4, 16, 16, 6408, 5121,
					texManager.textureBuffer);
		}

		int var4;
		int i;
		int var40;
		int var46;
		int var45;
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
							var4 = 0;
							while (networkHandler.in.position() > 0
									&& var4++ != 100) {
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
										System.out
												.println("Connecting to AppName: "
														+ AppName
														+ " with extension count: "
														+ ExtensionCount);
										recievedExtensionLength = ExtensionCount;
									} else if (packetType == PacketType.EXT_ENTRY) {
										String ExtName = (String) packetParams[0];
										Integer Version = ((Integer) packetParams[1])
												.intValue();
										com.oyasunadev.mcraft.client.util.Constants.ServerSupportedExtensions
												.add(new ExtData(ExtName,
														Version));

										if (ExtName.toLowerCase().contains(
												"heldblock")) {
											canSendHeldBlock = true;
										}
										if (ExtName.toLowerCase().contains(
												"messagetypes")) {
											serverSupportsMessages = true;
										}

										if (recievedExtensionLength == com.oyasunadev.mcraft.client.util.Constants.ServerSupportedExtensions
												.size()) {
											System.out
													.println("Sending client's supported Exts");
											List<ExtData> temp = new ArrayList<ExtData>();
											for (int j = 0; j < PacketType.packets.length - 1; j++) {
												if (PacketType.packets[j] != null
														&& PacketType.packets[j].extName != "") {
													temp.add(new ExtData(
															PacketType.packets[j].extName,
															PacketType.packets[j].Version));
												}
											}
											String AppName = "ClassiCube Client";
											Object[] toSendParams = new Object[] {
													AppName,
													(short) temp.size() };
											networkManager.netHandler.send(
													PacketType.EXT_INFO,
													toSendParams);
											for (int k = 0; k < temp.size(); k++) {
												System.out
														.println("Sending ext: "
																+ temp.get(k).Name
																+ " with version: "
																+ temp.get(k).Version);
												toSendParams = new Object[] {
														temp.get(k).Name,
														temp.get(k).Version };
												networkManager.netHandler.send(
														PacketType.EXT_ENTRY,
														toSendParams);
											}
										}
									} else if (packetType == PacketType.SELECTION_CUBOID) {
										byte ID = ((Byte) packetParams[0])
												.byteValue();
										String Name = (String) packetParams[1];
										Short X1 = (Short) packetParams[2];
										Short Y1 = (Short) packetParams[3];
										Short Z1 = (Short) packetParams[4];
										Short X2 = (Short) packetParams[5];
										Short Y2 = (Short) packetParams[6];
										Short Z2 = (Short) packetParams[7];
										Short r = ((Short) packetParams[8])
												.shortValue();
										Short g = ((Short) packetParams[9])
												.shortValue();
										Short b = ((Short) packetParams[10])
												.shortValue();
										Short a = ((Short) packetParams[11])
												.shortValue();

										// System.out.println(ID + " " + Name +
										// " " + X1 + " " + Y1
										// + " " + Z1 + " " + X2 + " " + Y2 +
										// " " + Z2);
										SelectionBoxData data = new SelectionBoxData(
												ID, Name,
												new ColorCache(r / 255.0F,
														g / 255.0F, b / 255.0F,
														a / 255.0F),
												new CustomAABB(X1, Y1, Z1, X2,
														Y2, Z2));
										selectionBoxes.add(data);
									} else if (packetType == PacketType.REMOVE_SELECTION_CUBOID) {
										byte ID = ((Byte) packetParams[0])
												.byteValue();
										List<SelectionBoxData> cache = selectionBoxes;
										for (int q = 0; q < selectionBoxes
												.size(); q++) {
											if (selectionBoxes.get(q).ID == ID) {
												cache.remove(q);
											}
										}
										selectionBoxes = cache;
									} else if (packetType == PacketType.ENV_SET_COLOR) {
										byte Variable = ((Byte) packetParams[0])
												.byteValue();
										Short r = ((Short) packetParams[1])
												.shortValue();
										Short g = ((Short) packetParams[2])
												.shortValue();
										Short b = ((Short) packetParams[3])
												.shortValue();
										int dec = (r & 0x0ff) << 16
												| (g & 0x0ff) << 8 | b & 0x0ff;
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
											level.customShadowColour = new ColorCache(
													r / 255.0F, g / 255.0F,
													b / 255.0F);
                                                                                        levelRenderer.refresh();
											break;
										case 4: // diffuse color
											level.customLightColour = new ColorCache(
													r / 255.0F, g / 255.0F,
													b / 255.0F);
                                                                                        levelRenderer.refresh();
											break;
										}
									} else if (packetType == PacketType.ENV_SET_MAP_APPEARANCE) {
										String textureUrl = (String) packetParams[0];
										byte sideBlock = ((Byte) packetParams[1])
												.byteValue();
										byte edgeBlock = ((Byte) packetParams[2])
												.byteValue();
										short sideLevel = ((Short) packetParams[3])
												.byteValue();

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
												File path = new File(
														getMinecraftDirectory(),
														"/skins/terrain");
												if (!path.exists()) {
													path.mkdirs();
												}
												String hash = getHash(textureUrl);
												if (hash != null) {
													File file = new File(path,
															hash + ".png");
													BufferedImage image;
													if (!file.exists()) {
														downloadImage(
																textureUrl,
																file.getAbsolutePath());
													}
													image = ImageIO.read(file);
													if (image.getWidth() % 16 == 0
															&& image.getHeight() % 16 == 0) {
														textureManager.animations
																.clear();
														textureManager.currentTerrainPng = image;
													}
												}
											} else {
												textureManager.animations
														.clear();
												try {
													textureManager.currentTerrainPng = ImageIO
															.read(TextureManager.class
																	.getResourceAsStream("/terrain.png"));
												} catch (IOException e1) {
													e1.printStackTrace();
												}
											}
											textureManager.textures.clear();
											level.waterLevel = sideLevel;
											levelRenderer.refresh();
										}
									} else if (packetType == PacketType.CLICK_DISTANCE) {
										short Distance = (Short) packetParams[0];
										gamemode.reachDistance = Distance / 32;
									} else if (packetType == PacketType.HOLDTHIS) {
										byte BlockToHold = ((Byte) packetParams[0])
												.byteValue();
										byte PreventChange = ((Byte) packetParams[1])
												.byteValue();
										boolean CanPreventChange = PreventChange > 0;

										if (CanPreventChange == true) {
											GameSettings.CanReplaceSlot = false;
										}

										player.inventory.selected = 0;
										player.inventory
												.replaceSlot(Block.blocks[BlockToHold]);

										if (CanPreventChange == false) {
											GameSettings.CanReplaceSlot = true;
										}
									} else if (packetType == PacketType.SET_TEXT_HOTKEY) {
										String Label = (String) packetParams[0];
										String Action = (String) packetParams[1];
										int keyCode = (Integer) packetParams[2];
										byte KeyMods = ((Byte) packetParams[3])
												.byteValue();
										HotKeyData data = new HotKeyData(Label,
												Action, keyCode, KeyMods);
										hotKeys.add(data);

									} else if (packetType == PacketType.EXT_ADD_PLAYER_NAME) {
										Short NameId = (Short) packetParams[0];
										String playerName = (String) packetParams[1];
										String listName = (String) packetParams[2];
										String groupName = (String) packetParams[3];
										byte unusedRank = ((Byte) packetParams[4])
												.byteValue();

										int playerIndex = -1;

										for (PlayerListNameData b : playerListNameData) {
											if (b.nameID == NameId) { // --
																		// Already
																		// exists,
																		// update
																		// the
																		// entry.
												playerIndex = playerListNameData
														.indexOf(b);
												break;
											}
										}

										if (playerIndex == -1)
											playerListNameData
													.add(new PlayerListNameData(
															NameId, playerName,
															listName,
															groupName,
															unusedRank));
										else
											playerListNameData.set(playerIndex,
													new PlayerListNameData(
															NameId, playerName,
															listName,
															groupName,
															unusedRank));

										Collections.sort(playerListNameData,
												new PlayerListComparator());
									} else if (packetType == PacketType.EXT_ADD_ENTITY) {
										byte playerID = ((Byte) packetParams[0])
												.byteValue();
										String skinName = (String) packetParams[2];

										NetworkPlayer player = networkManager.players
												.get(playerID);
										if (player != null) {
											player.SkinName = skinName;
											player.downloadSkin();
										}
									} else if (packetType == PacketType.EXT_REMOVE_PLAYER_NAME) {
										Short NameId = (Short) packetParams[0];
										List<PlayerListNameData> cache = playerListNameData;
										for (int q = 0; q < playerListNameData
												.size(); q++) {
											if (playerListNameData.get(q).nameID == NameId) {
												cache.remove(q);
											}
										}
										playerListNameData = cache;
									} else if (packetType == PacketType.CUSTOM_BLOCK_SUPPORT_LEVEL) {
										System.out
												.println("Custom blocks packet recieved");
										byte SupportLevel = ((Byte) packetParams[0])
												.byteValue();
										networkManager.netHandler
												.send(PacketType.CUSTOM_BLOCK_SUPPORT_LEVEL,
														com.oyasunadev.mcraft.client.util.Constants.SupportLevel);
										SessionData
												.SetAllowedBlocks(SupportLevel);
									} else if (packetType == PacketType.SET_BLOCK_PERMISSIONS) {
										byte BlockType = ((Byte) packetParams[0])
												.byteValue();
										byte AllowPlacement = ((Byte) packetParams[1])
												.byteValue();
										byte AllowDeletion = ((Byte) packetParams[2])
												.byteValue();
										Block block = Block.blocks[BlockType];
										if (block == null) {
											return;
										}
										if (AllowPlacement == 0) {
											if (!disallowedPlacementBlocks
													.contains(block)) {
												disallowedPlacementBlocks
														.add(block);
												System.out
														.println("DisallowingPlacement block: "
																+ block);
											}
										} else {
											if (disallowedPlacementBlocks
													.contains(block)) {
												disallowedPlacementBlocks
														.remove(block);
												System.out
														.println("AllowingPlacement block: "
																+ block);
											}
										}
										if (AllowDeletion == 0) {
											if (!DisallowedBreakingBlocks
													.contains(block)) {
												DisallowedBreakingBlocks
														.add(block);
												System.out
														.println("DisallowingDeletion block: "
																+ block);
											}
										} else {
											if (DisallowedBreakingBlocks
													.contains(block)) {
												DisallowedBreakingBlocks
														.remove(block);
												System.out
														.println("AllowingDeletion block: "
																+ block);
											}
										}
									} else if (packetType == PacketType.CHANGE_MODEL) {
										byte PlayerID = ((Byte) packetParams[0])
												.byteValue();
										String ModelName = (String) packetParams[1];
										if (PlayerID >= 0) {
											NetworkPlayer netPlayer;
											if ((netPlayer = networkManager.players
														.get(Byte.valueOf(PlayerID))) != null) {
													ModelManager m = new ModelManager();
													if (m.getModel(ModelName.toLowerCase()) == null) {
															netPlayer.modelName = "humanoid";
													} else netPlayer.modelName = ModelName.toLowerCase();
													netPlayer.bindTexture(textureManager);
											}
										} else if (PlayerID == -1){
												Player thisPlayer = player;											
												ModelManager m = new ModelManager();
												if (m.getModel(ModelName.toLowerCase()) == null) {
														thisPlayer.modelName = "humanoid";
												} else thisPlayer.modelName = ModelName.toLowerCase();
												thisPlayer.bindTexture(textureManager);
										}                                                                                
									}

									else if (packetType == PacketType.ENV_SET_WEATHER_TYPE) {
										byte Weather = ((Byte) packetParams[0])
												.byteValue();
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
									}

									else if (packetType == PacketType.IDENTIFICATION) {
										networkManager.minecraft.progressBar
												.setTitle(packetParams[1]
														.toString());
										networkManager.minecraft.player.userType = ((Byte) packetParams[3])
												.byteValue();
										networkManager.minecraft.progressBar
												.setText(packetParams[2]
														.toString());
									} else if (packetType == PacketType.LEVEL_INIT) {
										networkManager.minecraft
												.setLevel((Level) null);
										networkManager.levelData = new ByteArrayOutputStream();
									} else if (packetType == PacketType.LEVEL_DATA) {
										short chunkLength = ((Short) packetParams[0])
												.shortValue();
										byte[] chunkData = (byte[]) packetParams[1];
										byte percentComplete = ((Byte) packetParams[2])
												.byteValue();
										networkManager.minecraft.progressBar
												.setProgress(percentComplete);
										isLoadingMap = false;
										networkManager.levelData.write(
												chunkData, 0, chunkLength);
									} else if (packetType == PacketType.LEVEL_FINALIZE) {
										try {
											networkManager.levelData.close();
										} catch (IOException e) {
											e.printStackTrace();
										}

										byte[] decompressedStream = LevelLoader
												.decompress(new ByteArrayInputStream(
														networkManager.levelData
																.toByteArray()));
										networkManager.levelData = null;
										short xSize = ((Short) packetParams[0])
												.shortValue();
										short ySize = ((Short) packetParams[1])
												.shortValue();
										short zSize = ((Short) packetParams[2])
												.shortValue();
										Level level;
										(level = new Level())
												.setNetworkMode(true);
										level.setData(xSize, ySize, zSize,
												decompressedStream);
										networkManager.minecraft
												.setLevel(level);
										networkManager.minecraft.isOnline = false;
										networkManager.levelLoaded = true;
										// ProgressBarDisplay.InitEnv(this);
										// this.levelRenderer.refresh();
									} else if (packetType == PacketType.BLOCK_CHANGE) {
										if (networkManager.minecraft.level != null) {
											networkManager.minecraft.level
													.netSetTile(
															((Short) packetParams[0])
																	.shortValue(),
															((Short) packetParams[1])
																	.shortValue(),
															((Short) packetParams[2])
																	.shortValue(),
															((Byte) packetParams[3])
																	.byteValue());
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
											var10001 = ((Byte) packetParams[0])
													.byteValue();
											String var10002 = (String) packetParams[1];
											var10003 = ((Short) packetParams[2])
													.shortValue();
											var10004 = ((Short) packetParams[3])
													.shortValue();
											short var10005 = ((Short) packetParams[4])
													.shortValue();
											byte var10006 = ((Byte) packetParams[5])
													.byteValue();
											byte var58 = ((Byte) packetParams[6])
													.byteValue();
											var9 = var10006;
											short var10 = var10005;
											var47 = var10004;
											var36 = var10003;
											var34 = var10002;
											byte var5 = var10001;
											if (var5 >= 0) {
												var9 = (byte) (var9 + 128);
												var47 = (short) (var47 - 22);
												var33 = new NetworkPlayer(
														networkManager.minecraft,
														var34, var36, var47,
														var10,
														var9 * 360 / 256.0F,
														var58 * 360 / 256.0F);
												networkManager.players.put(
														Byte.valueOf(var5),
														var33);
												networkManager.minecraft.level
														.addEntity(var33);
											} else {
												networkManager.minecraft.level
														.setSpawnPos(
																var36 / 32,
																var47 / 32,
																var10 / 32,
																var9 * 320 / 256);
												networkManager.minecraft.player
														.moveTo(var36 / 32.0F,
																var47 / 32.0F,
																var10 / 32.0F,
																var9 * 360 / 256.0F,
																var58 * 360 / 256.0F);
											}
										} else {
											byte var53;
											NetworkPlayer networkPlayer;
											byte var69;
											if (packetType == PacketType.POSITION_ROTATION) {
												var10001 = ((Byte) packetParams[0])
														.byteValue();
												short var66 = ((Short) packetParams[1])
														.shortValue();
												var10003 = ((Short) packetParams[2])
														.shortValue();
												var10004 = ((Short) packetParams[3])
														.shortValue();
												var69 = ((Byte) packetParams[4])
														.byteValue();
												var9 = ((Byte) packetParams[5])
														.byteValue();
												var53 = var69;
												var47 = var10004;
												var36 = var10003;
												short var38 = var66;
												byte var5 = var10001;
												if (var5 < 0) {
													networkManager.minecraft.player
															.moveTo(var38 / 32.0F,
																	var36 / 32.0F,
																	var47 / 32.0F,
																	var53 * 360 / 256.0F,
																	var9 * 360 / 256.0F);
												} else {
													var53 = (byte) (var53 + 128);
													var36 = (short) (var36 - 22);
													if ((networkPlayer = networkManager.players
															.get(Byte
																	.valueOf(var5))) != null) {
														networkPlayer
																.teleport(
																		var38,
																		var36,
																		var47,
																		var53 * 360 / 256.0F,
																		var9 * 360 / 256.0F);
													}
												}
											} else {
												byte var37;
												byte var44;
												byte var49;
												byte var65;
												byte var67;
												if (packetType == PacketType.POSITION_ROTATION_UPDATE) {
													var10001 = ((Byte) packetParams[0])
															.byteValue();
													var67 = ((Byte) packetParams[1])
															.byteValue();
													var65 = ((Byte) packetParams[2])
															.byteValue();
													byte var64 = ((Byte) packetParams[3])
															.byteValue();
													var69 = ((Byte) packetParams[4])
															.byteValue();
													var9 = ((Byte) packetParams[5])
															.byteValue();
													var53 = var69;
													var49 = var64;
													var44 = var65;
													var37 = var67;
													byte playerID = var10001;
													if (playerID >= 0) {
														var53 = (byte) (var53 + 128);
														if ((networkPlayer = networkManager.players
																.get(Byte
																		.valueOf(playerID))) != null) {
															networkPlayer
																	.queue(var37,
																			var44,
																			var49,
																			var53 * 360 / 256.0F,
																			var9 * 360 / 256.0F);
														}
													}
												} else if (packetType == PacketType.ROTATION_UPDATE) {
													var10001 = ((Byte) packetParams[0])
															.byteValue();
													var67 = ((Byte) packetParams[1])
															.byteValue();
													var44 = ((Byte) packetParams[2])
															.byteValue();
													var37 = var67;
													byte var5 = var10001;
													if (var5 >= 0) {
														var37 = (byte) (var37 + 128);
														NetworkPlayer var54;
														if ((var54 = networkManager.players
																.get(Byte
																		.valueOf(var5))) != null) {
															var54.queue(
																	var37 * 360 / 256.0F,
																	var44 * 360 / 256.0F);
														}
													}
												} else if (packetType == PacketType.POSITION_UPDATE) {
													var10001 = ((Byte) packetParams[0])
															.byteValue();
													var67 = ((Byte) packetParams[1])
															.byteValue();
													var65 = ((Byte) packetParams[2])
															.byteValue();
													var49 = ((Byte) packetParams[3])
															.byteValue();
													var44 = var65;
													var37 = var67;
													byte var5 = var10001;
													NetworkPlayer var59;
													if (var5 >= 0
															&& (var59 = networkManager.players
																	.get(Byte
																			.valueOf(var5))) != null) {
														var59.queue(var37,
																var44, var49);
													}
												} else if (packetType == PacketType.DESPAWN_PLAYER) {
													byte var5 = ((Byte) packetParams[0])
															.byteValue();
													if (var5 >= 0
															&& (var33 = networkManager.players
																	.remove(Byte
																			.valueOf(var5))) != null) {
														var33.clear();
														networkManager.minecraft.level
																.removeEntity(var33);
													}
												} else if (packetType == PacketType.CHAT_MESSAGE) {
													var10001 = ((Byte) packetParams[0])
															.byteValue();
													var34 = (String) packetParams[1];
													byte var5 = var10001;
													if (var5 < 0) {
														networkManager.minecraft.hud
																.addChat("&e"
																		+ var34);
													} else if (var5 > 0
															&& serverSupportsMessages) {
														switch (var5) {
														case 1:
															HUDScreen.ServerName = var34;
															break;
														case 2:
															HUDScreen.Compass = var34;
															break;
														case 3:
															HUDScreen.UserDetail = var34;
															break;
														case 11:
															HUDScreen.BottomRight1 = var34;
															break;
														case 12:
															HUDScreen.BottomRight2 = var34;
															break;
														case 13:
															HUDScreen.BottomRight3 = var34;
															break;
														case 21:
															break;
														case 100:
															HUDScreen.Announcement = var34;
															break;
														default:
															networkManager.players
																	.get(Byte
																			.valueOf(var5));
															networkManager.minecraft.hud
																	.addChat(var34);
															break;
														}
													} else {
														networkManager.players
																.get(Byte
																		.valueOf(var5));
														networkManager.minecraft.hud
																.addChat(var34);
													}
												} else if (packetType == PacketType.DISCONNECT) {
													networkManager.netHandler
															.close();
													networkManager.minecraft
															.setCurrentScreen(new ErrorScreen(
																	"Connection lost",
																	(String) packetParams[0]));
												} else if (packetType == PacketType.UPDATE_PLAYER_TYPE) {
													networkManager.minecraft.player.userType = ((Byte) packetParams[0])
															.byteValue();
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
								networkHandler.channel
										.write(networkHandler.out);
								networkHandler.out.compact();
							}
						} catch (Exception var15) {
							var20.minecraft.setCurrentScreen(new ErrorScreen(
									"Disconnected!",
									"You\'ve lost connection to the server"));
							var20.minecraft.isOnline = false;
							var15.printStackTrace();
							var20.netHandler.close();
							var20.minecraft.networkManager = null;
						}
					}
				}

				Player player = this.player;
				var20 = networkManager;
				if (networkManager.levelLoaded) {
					int var24 = (int) (player.x * 32.0F);
					var4 = (int) (player.y * 32.0F);
					var40 = (int) (player.z * 32.0F);
					var46 = (int) (player.yRot * 256.0F / 360.0F) & 255;
					var45 = (int) (player.xRot * 256.0F / 360.0F) & 255;
					var20.netHandler.send(
							PacketType.POSITION_ROTATION,
							new Object[] {
									canSendHeldBlock ? player.inventory
											.getSelected() : Integer
											.valueOf(-1),
									Integer.valueOf(var24),
									Integer.valueOf(var4),
									Integer.valueOf(var40),
									Integer.valueOf(var46),
									Integer.valueOf(var45) });
				}
			}
		}

		if (isLoadingMap) {
			while (Keyboard.next()) {
				if (Keyboard.getEventKeyState()) {

					if (Keyboard.getEventKey() == 1) {
						pause();
					}
				}
			}
			return;
		}

		if (currentScreen == null && player != null && player.health <= 0) {
			setCurrentScreen((GuiScreen) null);
		}

		int var25;
		if (currentScreen instanceof BlockSelectScreen) {
			while (Mouse.next()) {
				if ((var25 = Mouse.getEventDWheel()) != 0) {
					player.inventory.swapPaint(var25);
					break;
				}
                                currentScreen.mouseEvent();
			}
                        while (Keyboard.next()) {
				if (Keyboard.getEventKey() > 1 && Keyboard.getEventKey() < 11) {
                                    if (GameSettings.CanReplaceSlot) {
                                        player.inventory.selected = Keyboard.getEventKey() - 2;
                                        break;
                                    }
                                }
                                currentScreen.keyboardEvent();
			}
                        
		}

		else if (currentScreen == null) {
			while (Mouse.next()) {
				if ((var25 = Mouse.getEventDWheel()) != 0) {
					player.inventory.swapPaint(var25);
				}

				if (currentScreen == null) {
					if (!hasMouse && Mouse.getEventButtonState()) {
						grabMouse();
					} else {
						if (Mouse.getEventButton() == 0
								&& Mouse.getEventButtonState()) {
							onMouseClick(0);
							lastClick = ticks;
						}

						if (Mouse.getEventButton() == 1
								&& Mouse.getEventButtonState()) {
							onMouseClick(1);
							lastClick = ticks;
						}

						if (Mouse.getEventButton() == 2
								&& Mouse.getEventButtonState()
								&& selected != null) {
							var16 = level.getTile(selected.x, selected.y,
									selected.z);
							player.inventory.grabTexture(var16,
									gamemode instanceof CreativeGameMode);
						}
					}
				}

				if (currentScreen != null) {
					currentScreen.mouseEvent();
				}
			}

			if (blockHitTime > 0) {
				--blockHitTime;
			}
                        
                        if (flyToggleTimer > 0)
                        {
                            --flyToggleTimer;
                        }  

			while (Keyboard.next()) {
				player.setKey(Keyboard.getEventKey(),
						Keyboard.getEventKeyState());
				if (Keyboard.getEventKeyState()) {
					if (currentScreen != null) {
						currentScreen.keyboardEvent();
					}

					if (currentScreen == null) {
						if (Keyboard.getEventKey() == 1) {
							pause();
						}

						if (gamemode instanceof CreativeGameMode) {
							if (HackState.Respawn) {
								if (Keyboard.getEventKey() == settings.loadLocationKey.key) {
									if (!(currentScreen instanceof ChatInputScreen)) {
										player.resetPos();
									}
								}

								if (Keyboard.getEventKey() == settings.saveLocationKey.key) {
									level.setSpawnPos((int) player.x,
											(int) player.y, (int) player.z,
											player.yRot);
									player.resetPos();
								}
							}
						}

						Keyboard.getEventKey();
						if (Keyboard.getEventKey() == 63) {
							isRaining = !isRaining;
							isSnowing = false;
						}
						if (Keyboard.getEventKey() == 62) {
							isSnowing = !isSnowing;
							isRaining = false;
						}
						if (Keyboard.getEventKey() == 53) {
							player.releaseAllKeys();
							ChatInputScreenExtension s = new ChatInputScreenExtension();
							setCurrentScreen(s);
							s.inputLine = "/";
							s.caretPos++;
						}

						if (Keyboard.getEventKey() == Keyboard.KEY_F11) {
							toggleFullscreen();
						}
						if (Keyboard.getEventKey() == Keyboard.KEY_F1) {
							canRenderGUI = !canRenderGUI;
						}

						if (Keyboard.getEventKey() == Keyboard.KEY_F6) {
							if (HackState.Noclip || HackState.Fly
									|| HackState.Speed) {
								if (cameraDistance == -0.1F) {
									cameraDistance = -5.1f;
									settings.thirdPersonMode = true;
								} else {
									cameraDistance = -0.1F;
									settings.thirdPersonMode = false;
								}
							} else {
								cameraDistance = -0.1F;
								settings.thirdPersonMode = false;
							}
						}

						if (Keyboard.getEventKey() == Keyboard.KEY_F2) {
							takeAndSaveScreenshot(width, height);
						}
                                                
                                                if (Keyboard.getEventKey() == Keyboard.KEY_F3) {
							settings.showDebug = !settings.showDebug;
						}

						if (settings.HacksEnabled) {
							if (settings.HackType == 0) {
								if (Keyboard.getEventKey() == settings.noClip.key) {
									if (HackState.Noclip || HackState.Noclip
											&& player.userType >= 100) {
										player.noPhysics = !player.noPhysics;
										player.hovered = !player.hovered;
									}
								}
                                                                if (HackState.Fly && Keyboard.getEventKey() == settings.jumpKey.key)
                                                                {
                                                                    if (flyToggleTimer == 0)
                                                                    {
                                                                        this.flyToggleTimer = 7;
                                                                    }
                                                                    else
                                                                    {
                                                                        player.flyingMode = !player.flyingMode;
                                                                        this.flyToggleTimer = 0;
                                                                    }
                                                                }

								if (Keyboard.getEventKey() == Keyboard.KEY_Z) {
									if (HackState.Fly) {
										player.flyingMode = !player.flyingMode;
									}
								}
							}
						} else {
							player.flyingMode = false;
							player.noPhysics = false;
							player.hovered = false;
						}

						if (Keyboard.getEventKey() == 15
								&& gamemode instanceof SurvivalGameMode
								&& player.arrows > 0) {
							level.addEntity(new Arrow(level, player, player.x,
									player.y, player.z, player.yRot,
									player.xRot, 1.2F));
							--player.arrows;
						}

						if (Keyboard.getEventKey() == settings.inventoryKey.key) {
							gamemode.openInventory();
						}

						if (Keyboard.getEventKey() == settings.chatKey.key) {
							player.releaseAllKeys();
							setCurrentScreen(new ChatInputScreenExtension());
						}
					}

					for (var25 = 0; var25 < 9; ++var25) {
						if (Keyboard.getEventKey() == var25 + 2) {
							if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
								// tabbing
								// player
								// list
								return;
							} else if (GameSettings.CanReplaceSlot) {
								player.inventory.selected = var25;
							}
						}
					}

					if (Keyboard.getEventKey() == settings.toggleFogKey.key) {
						settings.toggleSetting(4, !Keyboard.isKeyDown(42)
								&& !Keyboard.isKeyDown(54) ? 1 : -1);
					}
				}
			}

			if (currentScreen == null) {
				if (Mouse.isButtonDown(0)
						&& ticks - lastClick >= timer.tps / 4.0F && hasMouse) {
					onMouseClick(0);
					lastClick = ticks;
				}

				if (Mouse.isButtonDown(1)
						&& ticks - lastClick >= timer.tps / 4.0F && hasMouse) {
					onMouseClick(1);
					lastClick = ticks;
				}
			}

			boolean var26 = currentScreen == null && Mouse.isButtonDown(0)
					&& hasMouse;
			if (!gamemode.instantBreak && blockHitTime <= 0) {
				if (var26 && selected != null && selected.entityPos == 0) {
					var4 = selected.x;
					var40 = selected.y;
					var46 = selected.z;
					gamemode.hitBlock(var4, var40, var46, selected.face);
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

			Player var27 = player;
			var4 = var41.minecraft.player.inventory.getSelected();
			Block var43 = null;
			if (var4 > 0) {
				var43 = Block.blocks[var4];
			}

			float var48 = 0.4F;
			float var50;
			if ((var50 = (var43 == var41.block ? 1.0F : 0.0F) - var41.pos) < -var48) {
				var50 = -var48;
			}

			if (var50 > var48) {
				var50 = var48;
			}

			var41.pos += var50;
			if (var41.pos < 0.1F) {
				var41.block = var43;
			}

			if (renderer.minecraft.isRaining) {
				var27 = renderer.minecraft.player;
				Level var32 = renderer.minecraft.level;
				var40 = (int) var27.x;
				var46 = (int) var27.y;
				var45 = (int) var27.z;

				for (i = 0; i < 50; ++i) {
					int var60 = var40 + renderer.random.nextInt(9) - 4;
					int var52 = var45 + renderer.random.nextInt(9) - 4;
					int var57;
					if ((var57 = var32.getHighestTile(var60, var52)) <= var46 + 4
							&& var57 >= var46 - 4) {
						float var56 = renderer.random.nextFloat();
						float var62 = renderer.random.nextFloat();
						renderer.minecraft.particleManager
								.spawnParticle(new WaterDropParticle(var32,
										var60 + var56, var57 + 0.1F, var52
												+ var62));
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
				Display.setDisplayMode(new DisplayMode(tempDisplayWidth,
						tempDisplayHeight));
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
