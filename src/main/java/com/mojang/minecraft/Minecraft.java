package com.mojang.minecraft;

import com.mojang.minecraft.gamemode.CreativeGameMode;
import com.mojang.minecraft.gamemode.GameMode;
import com.mojang.minecraft.gamemode.SurvivalGameMode;
import com.mojang.minecraft.gui.*;
import com.mojang.minecraft.item.Arrow;
import com.mojang.minecraft.item.Item;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelIO;
import com.mojang.minecraft.level.generator.LevelGenerator;
import com.mojang.minecraft.level.liquid.LiquidType;
import com.mojang.minecraft.level.tile.Block;
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
import com.mojang.minecraft.render.*;
import com.mojang.minecraft.render.texture.TextureFX;
import com.mojang.minecraft.render.texture.TextureLavaFX;
import com.mojang.minecraft.render.texture.TextureFireFX;
import com.mojang.minecraft.render.texture.TextureWaterFX;
import com.mojang.minecraft.sound.SoundManager;
import com.mojang.minecraft.sound.SoundPlayer;
import com.mojang.net.NetworkHandler;
import com.mojang.util.MathHelper;
import com.oyasunadev.mcraft.client.util.ExtData;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Minecraft implements Runnable {

	public GameMode gamemode = new CreativeGameMode(this);
	private boolean fullscreen = false;
	public int width;
	public int height;
	private Timer timer = new Timer(20.0F);
	public Level level;
	public LevelRenderer levelRenderer;
	public Player player;
	public ParticleManager particleManager;
	public SessionData session = null;
	public String host;
	public Canvas canvas;
	public boolean levelLoaded = false;
	public volatile boolean waiting = false;
	private Cursor cursor;
	public TextureManager textureManager;
	public FontRenderer fontRenderer;
	public GuiScreen currentScreen = null;
	public GuiScreen notifyScreen = null;
	public ProgressBarDisplay progressBar = new ProgressBarDisplay(this);
	public com.mojang.minecraft.render.Renderer renderer = new com.mojang.minecraft.render.Renderer(
			this);
	public LevelIO levelIo;
	public SoundManager sound;
	private ResourceDownloadThread resourceThread;
	private int ticks;
	private int blockHitTime;
	public String levelName;
	public int levelId;
	public Robot robot;
	public HUDScreen hud;
	public boolean online;
	public NetworkManager networkManager;
	public SoundPlayer soundPlayer;
	public MovingObjectPosition selected;
	public GameSettings settings;
	public boolean isApplet;
	public String server;
	public int port;
	public volatile boolean running;
	public String debug;
	public boolean hasMouse;
	private int lastClick;
	public boolean raining;
	public MinecraftApplet applet;
	public ClientHacksState HackState;
	public static boolean PlayerIsRunning = false;
	public List<SelectionBoxData> selectionBoxes = new ArrayList<SelectionBoxData>();

	public static File mcDir;

	public Minecraft(Canvas var1, MinecraftApplet var2, int var3, int var4,
			boolean var5, boolean IsApplet) {
		this.isApplet = IsApplet;
		this.levelIo = new LevelIO(this.progressBar);
		this.sound = new SoundManager();
		this.ticks = 0;
		this.blockHitTime = 0;
		this.levelName = null;
		this.levelId = 0;
		this.online = false;
		new HumanoidModel(0.0F);
		this.selected = null;
		this.server = null;
		this.port = 0;
		this.running = false;
		this.debug = "";
		this.hasMouse = false;
		this.lastClick = 0;
		this.raining = false;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception var7) {
			var7.printStackTrace();
		}

		this.applet = var2;
		new SleepForeverThread(this);
		this.canvas = var1;
		this.width = var3;
		this.height = var4;
		this.fullscreen = var5;
		if (var1 != null) {
			try {
				this.robot = new Robot();
				return;
			} catch (AWTException var8) {
				var8.printStackTrace();
			}
		}

	}

	public final void setCurrentScreen(GuiScreen var1) {
		if (!(this.currentScreen instanceof ErrorScreen)) {
			if (this.currentScreen != null) {
				this.currentScreen.onClose();
			}

			if (var1 == null && this.player.health <= 0) {
				var1 = new GameOverScreen();
			}

			this.currentScreen = (GuiScreen) var1;
			if (var1 != null) {
				if (this.hasMouse) {
					this.player.releaseAllKeys();
					this.hasMouse = false;
					if (this.levelLoaded) {
						try {
							Mouse.setNativeCursor((Cursor) null);
						} catch (LWJGLException var4) {
							var4.printStackTrace();
						}
					} else {
						Mouse.setGrabbed(false);
					}
				}

				int var2 = this.width * 240 / this.height;
				int var3 = this.height * 240 / this.height;
				((GuiScreen) var1).open(this, var2, var3);
				this.online = false;
			} else {
				this.grabMouse();
			}
		}
	}

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

	public final void shutdown() {
		try {
			if (this.soundPlayer != null) {
				this.soundPlayer.running = false;
			}

			if (this.resourceThread != null) {
				this.resourceThread.running = true;
			}
		} catch (Exception var3) {
			;
		}

		Minecraft var5 = this;
		if (!this.levelLoaded) {
			try {
				if (var5 != null) {
					if (var5.level != null) {
						if (var5.level.creativeMode) {
							LevelIO.save(var5.level,
									(OutputStream) (new FileOutputStream(
											new File(mcDir, "levelc.dat"))));
						} else {
							LevelIO.save(var5.level,
									(OutputStream) (new FileOutputStream(
											new File(mcDir, "levels.dat"))));
						}
					}
				}
			} catch (Exception var2) {
				var2.printStackTrace();
			}
		}

		Mouse.destroy();
		Keyboard.destroy();
		if (!isSystemShuttingDown()) {
			Display.destroy();
		}
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

	public static File GetMinecraftDirectory() {
		String folder = "mcraft/client";
		String home = System.getProperty("user.home", ".");
		File minecraftFolder;
		Minecraft$OS os = getOs();
		switch (os.id) {
		case 0:
			minecraftFolder = new File(home, folder + '/');
			break;
		case 1:
			minecraftFolder = new File(home, '.' + folder + '/');
			break;
		case 2:
			String appData = System.getenv("APPDATA");

			if (appData != null) {
				minecraftFolder = new File(appData, "." + folder + '/');
			} else {
				minecraftFolder = new File(home, '.' + folder + '/');
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

	public final void run() {
		this.running = true;

		mcDir = GetMinecraftDirectory();

		try {
			Minecraft var1 = this;

			var1.resourceThread = new ResourceDownloadThread(mcDir, var1);
			var1.resourceThread.run();

			if (!isApplet) {
				System.setProperty("org.lwjgl.librarypath", mcDir
						+ "/native/windows");
				System.setProperty("net.java.games.input.librarypath", mcDir
						+ "/native/windows");
			}
			if (this.canvas != null) {
				Display.setParent(this.canvas);
			} else if (this.fullscreen) {
				Display.setFullscreen(true);
				this.width = Display.getDisplayMode().getWidth();
				this.height = Display.getDisplayMode().getHeight();
			} else {
				Display.setDisplayMode(new DisplayMode(this.width, this.height));
			}

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

			try {
				Controllers.create();
			} catch (Exception var55) {
				var55.printStackTrace();
			}

			checkGLError("Pre startup");
			GL11.glEnable(3553);
			GL11.glShadeModel(7425);
			GL11.glClearDepth(1.0D);
			GL11.glEnable(2929);
			GL11.glDepthFunc(515);
			GL11.glEnable(3008);
			GL11.glAlphaFunc(516, 0.0F);
			GL11.glCullFace(1029);
			GL11.glMatrixMode(5889);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(5888);
			checkGLError("Startup");
			//
			this.settings = new GameSettings(this, mcDir);
			this.textureManager = new TextureManager(this.settings, isApplet);
			this.textureManager.registerAnimation(new TextureFireFX());
			this.textureManager.registerAnimation(new TextureLavaFX());
			this.textureManager.registerAnimation(new TextureWaterFX());
			this.fontRenderer = new FontRenderer(this.settings, "/default.png",
					this.textureManager);
			IntBuffer var9;
			(var9 = BufferUtils.createIntBuffer(256)).clear().limit(256);
			this.levelRenderer = new LevelRenderer(this, this.textureManager);
			Item.initModels();
			Mob.modelCache = new ModelManager();
			GL11.glViewport(0, 0, this.width, this.height);
			if (this.server != null && this.session != null) {
				Level var85;
				(var85 = new Level()).setData(8, 8, 8, new byte[512]);
				this.setLevel(var85);
			} else {
				boolean var10 = false;

				try {
					if (var1.levelName != null) {
						var1.loadOnlineLevel(var1.levelName, var1.levelId);
					} else if (!var1.levelLoaded) {
						Level var11 = null;
						if (gamemode instanceof CreativeGameMode) {
							if ((var11 = var1.levelIo
									.load((InputStream) (new FileInputStream(
											new File(mcDir, "levelc.dat"))))) != null) {
								var1.setLevel(var11);
							}
						} else if (gamemode instanceof SurvivalGameMode) {
							if ((var11 = var1.levelIo
									.load((InputStream) (new FileInputStream(
											new File(mcDir, "levels.dat"))))) != null) {
								var1.setLevel(var11);
							}
						}
					}
				} catch (Exception var54) {
					var54.printStackTrace();
				}

				if (this.level == null) {
					this.generateLevel(1);
				}
			}

			this.particleManager = new ParticleManager(this.level,
					this.textureManager);
			if (this.levelLoaded) {
				try {
					var1.cursor = new Cursor(16, 16, 0, 0, 1, var9,
							(IntBuffer) null);
				} catch (LWJGLException var53) {
					var53.printStackTrace();
				}
			}

			try {
				var1.soundPlayer = new SoundPlayer(var1.settings);
				SoundPlayer var4 = var1.soundPlayer;

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
			this.hud = new HUDScreen(this, this.width, this.height);
			(new SkinDownloadThread(this)).start();
			if (this.server != null && this.session != null) {
				this.networkManager = new NetworkManager(this, this.server,
						this.port, this.session.username, this.session.mppass);
			}
		} catch (Exception var62) {
			var62.printStackTrace();
			JOptionPane.showMessageDialog((Component) null, var62.toString(),
					"Failed to start Minecraft", 0);
			return;
		}

		long var13 = System.currentTimeMillis();
		int var15 = 0;

		try {
			while (this.running) {
				if (this.waiting) {
					Thread.sleep(100L);
				} else {
					if (this.canvas == null && Display.isCloseRequested()) {
						this.running = false;
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
						Timer var63 = this.timer;
						long var16;
						long var18 = (var16 = System.currentTimeMillis())
								- var63.lastSysClock;
						long var20 = System.nanoTime() / 1000000L;
						double var24;
						if (var18 > 1000L) {
							long var22 = var20 - var63.lastHRClock;
							var24 = (double) var18 / (double) var22;
							var63.adjustment += (var24 - var63.adjustment) * 0.20000000298023224D;
							var63.lastSysClock = var16;
							var63.lastHRClock = var20;
						}

						if (var18 < 0L) {
							var63.lastSysClock = var16;
							var63.lastHRClock = var20;
						}

						double var95;
						var24 = ((var95 = (double) var20 / 1000.0D) - var63.lastHR)
								* var63.adjustment;
						var63.lastHR = var95;
						if (var24 < 0.0D) {
							var24 = 0.0D;
						}

						if (var24 > 1.0D) {
							var24 = 1.0D;
						}

						var63.elapsedDelta = (float) ((double) var63.elapsedDelta + var24
								* (double) var63.speed * (double) var63.tps);
						var63.elapsedTicks = (int) var63.elapsedDelta;
						if (var63.elapsedTicks > 100) {
							var63.elapsedTicks = 100;
						}

						var63.elapsedDelta -= (float) var63.elapsedTicks;
						var63.delta = var63.elapsedDelta;

						for (int var64 = 0; var64 < this.timer.elapsedTicks; ++var64) {
							++this.ticks;
							this.tick();
						}

						checkGLError("Pre render");
						GL11.glEnable(3553);
						if (!this.online) {
							this.gamemode.applyCracks(this.timer.delta);
							float var65 = this.timer.delta;
							com.mojang.minecraft.render.Renderer renderer = this.renderer;
							if (this.renderer.displayActive
									&& !Display.isActive()) {
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
								if (renderer.minecraft.levelLoaded) {
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

								renderer.minecraft.player.turn((float) var81,
										(float) (var86 * var91));
							}

							if (!renderer.minecraft.online) {
								var81 = renderer.minecraft.width * 240
										/ renderer.minecraft.height;
								var86 = renderer.minecraft.height * 240
										/ renderer.minecraft.height;
								int var94 = Mouse.getX() * var81
										/ renderer.minecraft.width;
								var70 = var86 - Mouse.getY() * var86
										/ renderer.minecraft.height - 1;
								if (renderer.minecraft.level != null) {
									float var80 = var65;
									com.mojang.minecraft.render.Renderer var82 = renderer;
									com.mojang.minecraft.render.Renderer var27 = renderer;
									Player var28;
									float var29 = (var28 = renderer.minecraft.player).xRotO
											+ (var28.xRot - var28.xRotO)
											* var65;
									float var30 = var28.yRotO
											+ (var28.yRot - var28.yRotO)
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
									List var37 = renderer.minecraft.level.blockMap
											.getEntities(
													var28,
													var28.bb.expand(
															var34 * reachDistance,
															var33 * reachDistance,
															var87 * reachDistance));
									float var35 = 0.0F;

									for (var81 = 0; var81 < var37.size(); ++var81) {
										Entity var88;
										if ((var88 = (Entity) var37.get(var81))
												.isPickable()) {
											var74 = 0.1F;
											MovingObjectPosition var78;
											if ((var78 = var88.bb.grow(var74,
													var74, var74).clip(var31,
													vec3D)) != null
													&& ((var74 = var31
															.distance(var78.vec)) < var35 || var35 == 0.0F)) {
												var27.entity = var88;
												var35 = var74;
											}
										}
									}

									if (var27.entity != null
											&& !(var27.minecraft.gamemode instanceof CreativeGameMode)) {
										var27.minecraft.selected = new MovingObjectPosition(
												var27.entity);
									}

									int var77 = 0;

									while (true) {
										if (var77 >= 2) {
											GL11.glColorMask(true, true, true,
													false);
											break;
										}

										if (var82.minecraft.settings.anaglyph) {
											if (var77 == 0) {
												GL11.glColorMask(false, true,
														true, false);
											} else {
												GL11.glColorMask(true, false,
														false, false);
											}
										}

										Player var126 = var82.minecraft.player;
										Level var119 = var82.minecraft.level;
										LevelRenderer var89 = var82.minecraft.levelRenderer;
										ParticleManager var93 = var82.minecraft.particleManager;
										GL11.glViewport(0, 0,
												var82.minecraft.width,
												var82.minecraft.height);
										Level var26 = var82.minecraft.level;
										var28 = var82.minecraft.player;
										var29 = 1.0F / (float) (4 - var82.minecraft.settings.viewDistance);
										var29 = 1.0F - (float) Math.pow(
												(double) var29, 0.25D);
										var30 = (float) (var26.skyColor >> 16 & 255) / 255.0F;
										float var117 = (float) (var26.skyColor >> 8 & 255) / 255.0F;
										var32 = (float) (var26.skyColor & 255) / 255.0F;
										var82.fogRed = (float) (var26.fogColor >> 16 & 255) / 255.0F;
										var82.fogBlue = (float) (var26.fogColor >> 8 & 255) / 255.0F;
										var82.fogGreen = (float) (var26.fogColor & 255) / 255.0F;
										var82.fogRed += (var30 - var82.fogRed)
												* var29;
										var82.fogBlue += (var117 - var82.fogBlue)
												* var29;
										var82.fogGreen += (var32 - var82.fogGreen)
												* var29;
										var82.fogRed *= var82.fogColorMultiplier;
										var82.fogBlue *= var82.fogColorMultiplier;
										var82.fogGreen *= var82.fogColorMultiplier;
										Block var73;
										if ((var73 = Block.blocks[var26
												.getTile(
														(int) var28.x,
														(int) (var28.y + 0.12F),
														(int) var28.z)]) != null
												&& var73.getLiquidType() != LiquidType.NOT_LIQUID) {
											LiquidType var79;
											if ((var79 = var73.getLiquidType()) == LiquidType.WATER) {
												var82.fogRed = 0.02F;
												var82.fogBlue = 0.02F;
												var82.fogGreen = 0.2F;
											} else if (var79 == LiquidType.LAVA) {
												var82.fogRed = 0.6F;
												var82.fogBlue = 0.1F;
												var82.fogGreen = 0.0F;
											}
										}

										if (var82.minecraft.settings.anaglyph) {
											var74 = (var82.fogRed * 30.0F
													+ var82.fogBlue * 59.0F + var82.fogGreen * 11.0F) / 100.0F;
											var33 = (var82.fogRed * 30.0F + var82.fogBlue * 70.0F) / 100.0F;
											var34 = (var82.fogRed * 30.0F + var82.fogGreen * 70.0F) / 100.0F;
											var82.fogRed = var74;
											var82.fogBlue = var33;
											var82.fogGreen = var34;
										}

										GL11.glClearColor(var82.fogRed,
												var82.fogBlue, var82.fogGreen,
												0.0F);
										GL11.glClear(16640);
										var82.fogColorMultiplier = 1.0F;
										GL11.glEnable(2884);
										var82.fogEnd = (float) (512 >> (var82.minecraft.settings.viewDistance << 1));
										GL11.glMatrixMode(5889);
										GL11.glLoadIdentity();
										var29 = 0.07F;
										if (var82.minecraft.settings.anaglyph) {
											GL11.glTranslatef(
													(float) (-((var77 << 1) - 1))
															* var29, 0.0F, 0.0F);
										}

										Player var116 = var82.minecraft.player;
										var69 = 70.0F;
										if (var116.health <= 0) {
											var74 = (float) var116.deathTime
													+ var80;
											var69 /= (1.0F - 500.0F / (var74 + 500.0F)) * 2.0F + 1.0F;
										}

										GLU.gluPerspective(
												var69,
												(float) var82.minecraft.width
														/ (float) var82.minecraft.height,
												0.05F, var82.fogEnd);
										GL11.glMatrixMode(5888);
										GL11.glLoadIdentity();
										if (var82.minecraft.settings.anaglyph) {
											GL11.glTranslatef(
													(float) ((var77 << 1) - 1) * 0.1F,
													0.0F, 0.0F);
										}

										var82.hurtEffect(var80);
										if (var82.minecraft.settings.viewBobbing) {
											var82.applyBobbing(var80);
										}

										var116 = var82.minecraft.player;
										GL11.glTranslatef(0.0F, 0.0F, -0.1F);
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
										Frustrum var76 = FrustrumImpl.update();
										Frustrum var100 = var76;
										LevelRenderer var101 = var82.minecraft.levelRenderer;

										int var98;
										for (var98 = 0; var98 < var101.chunkCache.length; ++var98) {
											var101.chunkCache[var98]
													.clip(var100);
										}

										var101 = var82.minecraft.levelRenderer;
										Collections
												.sort(var82.minecraft.levelRenderer.chunks,
														new ChunkDirtyDistanceComparator(
																var126));
										var98 = var101.chunks.size() - 1;
										int var105;
										if ((var105 = var101.chunks.size()) > 3) {
											var105 = 3;
										}

										int var104;
										for (var104 = 0; var104 < var105; ++var104) {
											Chunk var118;
											(var118 = (Chunk) var101.chunks
													.remove(var98 - var104))
													.update();
											var118.loaded = false;
										}

										var82.updateFog();
										GL11.glEnable(2912);
										var89.sortChunks(var126, 0);
										int var83;
										int var110;
										ShapeRenderer var115;
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
															var115 = ShapeRenderer.instance;
															ShapeRenderer.instance
																	.begin();

															for (var114 = 0; var114 < 6; ++var114) {
																Block.blocks[var104]
																		.renderInside(
																				var115,
																				var99,
																				var98,
																				var105,
																				var114);
															}

															var115.end();
															GL11.glCullFace(1028);
															var115.begin();

															for (var114 = 0; var114 < 6; ++var114) {
																Block.blocks[var104]
																		.renderInside(
																				var115,
																				var99,
																				var98,
																				var105,
																				var114);
															}

															var115.end();
															GL11.glCullFace(1029);
															GL11.glDepthFunc(515);
														}
													}
												}
											}
										}

										var82.setLighting(true);
										Vec3D var103 = var82
												.getPlayerVector(var80);
										var89.level.blockMap.render(var103,
												var76, var89.textureManager,
												var80);
										var82.setLighting(false);
										var82.updateFog();
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
												ShapeRenderer var121 = ShapeRenderer.instance;
												ShapeRenderer.instance.begin();

												for (var120 = 0; var120 < var96.particles[var83]
														.size(); ++var120) {
													((Particle) var96.particles[var83]
															.get(var120))
															.render(var121,
																	var107,
																	var29,
																	var69,
																	var30,
																	var117,
																	var32);
												}

												var121.end();
											}
										}

										GL11.glBindTexture(3553,
												var89.textureManager
														.load("/rock.png"));
										GL11.glEnable(3553);
										GL11.glCallList(var89.listId); // rock
																		// edges
										var82.updateFog();
										var101 = var89;
										GL11.glBindTexture(3553,
												var89.textureManager
														.load("/clouds.png"));
										GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
										var107 = (float) (var89.level.cloudColor >> 16 & 255) / 255.0F;
										var29 = (float) (var89.level.cloudColor >> 8 & 255) / 255.0F;
										var30 = (float) (var89.level.cloudColor & 255) / 255.0F;
										if (var89.minecraft.settings.anaglyph) {
											var117 = (var107 * 30.0F + var29
													* 59.0F + var30 * 11.0F) / 100.0F;
											var32 = (var107 * 30.0F + var29 * 70.0F) / 100.0F;
											var69 = (var107 * 30.0F + var30 * 70.0F) / 100.0F;
											var107 = var117;
											var29 = var32;
											var30 = var69;
										}

										var115 = ShapeRenderer.instance;
										var74 = 0.0F;
										var33 = 4.8828125E-4F;
										var74 = (float) (var89.level.depth + 2);
										var34 = ((float) var89.ticks + var80)
												* var33 * 0.03F;
										var35 = 0.0F;
										var115.begin();
										var115.color(var107, var29, var30);

										for (var86 = -2048; var86 < var101.level.width + 2048; var86 += 512) {
											for (var125 = -2048; var125 < var101.level.height + 2048; var125 += 512) {
												var115.vertexUV((float) var86,
														var74,
														(float) (var125 + 512),
														(float) var86 * var33
																+ var34,
														(float) (var125 + 512)
																* var33);
												var115.vertexUV(
														(float) (var86 + 512),
														var74,
														(float) (var125 + 512),
														(float) (var86 + 512)
																* var33 + var34,
														(float) (var125 + 512)
																* var33);
												var115.vertexUV(
														(float) (var86 + 512),
														var74,
														(float) var125,
														(float) (var86 + 512)
																* var33 + var34,
														(float) var125 * var33);
												var115.vertexUV((float) var86,
														var74, (float) var125,
														(float) var86 * var33
																+ var34,
														(float) var125 * var33);
												var115.vertexUV((float) var86,
														var74, (float) var125,
														(float) var86 * var33
																+ var34,
														(float) var125 * var33);
												var115.vertexUV(
														(float) (var86 + 512),
														var74,
														(float) var125,
														(float) (var86 + 512)
																* var33 + var34,
														(float) var125 * var33);
												var115.vertexUV(
														(float) (var86 + 512),
														var74,
														(float) (var125 + 512),
														(float) (var86 + 512)
																* var33 + var34,
														(float) (var125 + 512)
																* var33);
												var115.vertexUV((float) var86,
														var74,
														(float) (var125 + 512),
														(float) var86 * var33
																+ var34,
														(float) (var125 + 512)
																* var33);
											}
										}

										var115.end();
										GL11.glDisable(3553);
										var115.begin();
										var34 = (float) (var101.level.skyColor >> 16 & 255) / 255.0F;
										var35 = (float) (var101.level.skyColor >> 8 & 255) / 255.0F;
										var87 = (float) (var101.level.skyColor & 255) / 255.0F;
										if (var101.minecraft.settings.anaglyph) {
											reachDistance = (var34 * 30.0F
													+ var35 * 59.0F + var87 * 11.0F) / 100.0F;
											var69 = (var34 * 30.0F + var35 * 70.0F) / 100.0F;
											var74 = (var34 * 30.0F + var87 * 70.0F) / 100.0F;
											var34 = reachDistance;
											var35 = var69;
											var87 = var74;
										}

										var115.color(var34, var35, var87);
										var74 = (float) (var101.level.depth + 10);

										for (var125 = -2048; var125 < var101.level.width + 2048; var125 += 512) {
											for (var68 = -2048; var68 < var101.level.height + 2048; var68 += 512) {
												var115.vertex((float) var125,
														var74, (float) var68);
												var115.vertex(
														(float) (var125 + 512),
														var74, (float) var68);
												var115.vertex(
														(float) (var125 + 512),
														var74,
														(float) (var68 + 512));
												var115.vertex((float) var125,
														var74,
														(float) (var68 + 512));
											}
										}

										var115.end();
										GL11.glEnable(3553);
										var82.updateFog();
										int var108;
										if (var82.minecraft.selected != null) {
											GL11.glDisable(3008);
											MovingObjectPosition var10001 = var82.minecraft.selected;
											var105 = var126.inventory
													.getSelected();
											boolean var106 = false;
											MovingObjectPosition var102 = var10001;
											var101 = var89;
											ShapeRenderer var113 = ShapeRenderer.instance;
											GL11.glEnable(3042);
											GL11.glEnable(3008);
											GL11.glBlendFunc(770, 1);
											GL11.glColor4f(
													1.0F,
													1.0F,
													1.0F,
													(MathHelper.sin((float) System
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
												GL11.glTranslatef(
														(float) var102.x
																+ var74,
														(float) var102.y
																+ var33,
														(float) var102.z
																+ var34);
												var35 = 1.01F;
												GL11.glScalef(1.01F, var35,
														var35);
												GL11.glTranslatef(
														-((float) var102.x + var74),
														-((float) var102.y + var33),
														-((float) var102.z + var34));
												var113.begin();
												var113.noColor();
												GL11.glDepthMask(false);
												if (var73 == null) {
													var73 = Block.STONE;
												}

												for (var86 = 0; var86 < 6; ++var86) {
													var73.renderSide(
															var113,
															var102.x,
															var102.y,
															var102.z,
															var86,
															240 + (int) (var101.cracks * 10.0F));
												}

												var113.end();
												GL11.glDepthMask(true);
												GL11.glPopMatrix();
											}
											
											

											GL11.glDisable(3042);
											GL11.glDisable(3008);
											var10001 = var82.minecraft.selected;
											var126.inventory.getSelected();
											var106 = false;
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
										var82.updateFog();
										GL11.glEnable(3553);
										GL11.glEnable(3042);
										GL11.glBindTexture(3553,
												var89.textureManager
														.load("/water.png"));
										GL11.glCallList(var89.listId + 1); // outside
																			// of
																			// map
										GL11.glDisable(3042);
										GL11.glEnable(3042);
										GL11.glColorMask(false, false, false,
												false);

										GL11.glColorMask(true, true, true, true);
										if (var82.minecraft.settings.anaglyph) {
											if (var77 == 0) {
												GL11.glColorMask(false, true,
														true, false);
											} else {
												GL11.glColorMask(true, false,
														false, false);
											}
										}
										var120 = var89.sortChunks(var126, 1); // draws
																				// the
																				// blocks
										if (var120 > 0) {
											GL11.glBindTexture(
													3553,
													var89.textureManager
															.load("/terrain.png"));
											GL11.glCallLists(var89.buffer); // draws
																			// the
																			// transparent
																			// blocks
										}
										GL11.glCallList(var89.listId + 2); // outside
																			// of
																			// map
										//-------------------
										
										for(int i = 0; i< this.selectionBoxes.size(); i++)
										{
										CustomAABB bounds =  this.selectionBoxes.get(i).Bounds;
										ColorCache color = this.selectionBoxes.get(i).Color;
										GL11.glLineWidth(2);
										
										GL11.glDisable(3042);
										GL11.glDisable(3008);
										GL11.glEnable(3042);
										GL11.glBlendFunc(770, 771);
										GL11.glColor4f(color.R, color.G, color.B,
												color.A);
										GL11.glDisable(3553);
										GL11.glDepthMask(false);
										GL11.glDisable(GL11.GL_CULL_FACE);
										//GL11.glBegin(GL11.GL_QUADS);

									    // Front Face 
										
									    //  Bottom Left
										ShapeRenderer sr = ShapeRenderer.instance;
									    sr.begin();
										sr.vertex(bounds.x0, bounds.y0,  bounds.z1);
									    // Bottom Right  
										sr.vertex( bounds.x1, bounds.y0,  bounds.z1);
									    // Top Right  
										sr.vertex( bounds.x1,  bounds.y1,  bounds.z1);
									    // Top Left  
										sr.vertex(bounds.x0,  bounds.y1,  bounds.z1);
									    
									    // Back Face 
										
									    // Bottom Right  
										sr.vertex(bounds.x0, bounds.y0, bounds.z0);
									    // Top Right  
										sr.vertex(bounds.x0,  bounds.y1, bounds.z0);
									    // Top Left  
										sr.vertex( bounds.x1,  bounds.y1, bounds.z0);
									    // Bottom Left  
										sr.vertex( bounds.x1, bounds.y0, bounds.z0);
									    
									    // Top Face 
									    // Top Left  
										
									    // Bottom Left  
										sr.vertex(bounds.x0,  bounds.y1,  bounds.z0);
										 sr.vertex(bounds.x0,  bounds.y1,  bounds.z1);
									    // Bottom Right  
										sr.vertex( bounds.x1,  bounds.y1,  bounds.z1);
									    // Top Right  
										sr.vertex( bounds.x1,  bounds.y1, bounds.z0);
									    
									    // Bottom Face 
										
									    // Top Right  
										sr.vertex(bounds.x0, bounds.y0, bounds.z0);
									    // Top Left  
										sr.vertex( bounds.x1, bounds.y0, bounds.z0);
									    // Bottom Left  
										sr.vertex( bounds.x1, bounds.y0,  bounds.z1);
									    // Bottom Right  
										sr.vertex(bounds.x0, bounds.y0,  bounds.z1);
									    
									    // Right face 
										
									    // Bottom Right  
										sr.vertex( bounds.x1, bounds.y0, bounds.z0);
									    // Top Right  
										sr.vertex( bounds.x1,  bounds.y1, bounds.z0);
									    // Top Left  
										sr.vertex( bounds.x1,  bounds.y1,  bounds.z1);
									    // Bottom Left  
										sr.vertex( bounds.x1, bounds.y0,  bounds.z1);
									    
									    // Left Face 
										
									    // Bottom Left  
									    sr.vertex(bounds.x0, bounds.y0, bounds.z0);
									    // Bottom Right  
									    sr.vertex(bounds.x0, bounds.y0,  bounds.z1);
									    // Top Right  
									    sr.vertex(bounds.x0, bounds.y1,  bounds.z1);
									    // Top Left  
									    sr.vertex(bounds.x0, bounds.y1, bounds.z0);
									    sr.end();
									    
									    GL11.glColor4f(color.R, color.G, color.B, color.A + 0.2F);
									    
									    
									    GL11.glBegin(GL11.GL_LINE_STRIP);

									    GL11.glVertex3f(bounds.x0, bounds.y0, bounds.z1);
									    GL11.glVertex3f(bounds.x0, bounds.y1, bounds.z1);
									    GL11.glVertex3f(bounds.x1, bounds.y1, bounds.z1);
									    GL11.glVertex3f(bounds.x1, bounds.y0, bounds.z1);
									    GL11.glVertex3f(bounds.x0, bounds.y0, bounds.z1);

									    GL11.glVertex3f(bounds.x0, bounds.y0, bounds.z0);
									    GL11.glVertex3f(bounds.x0, bounds.y1, bounds.z0);
									    GL11.glVertex3f(bounds.x1, bounds.y1, bounds.z0);
									    GL11.glVertex3f(bounds.x1, bounds.y0, bounds.z0);
									    GL11.glVertex3f(bounds.x0, bounds.y0, bounds.z0);

									    GL11.glEnd();
									    
									    GL11.glBegin(GL11.GL_LINES);

									    GL11.glVertex3f(bounds.x0, bounds.y0, bounds.z1);
									    GL11.glVertex3f(bounds.x0, bounds.y0, bounds.z0);

									    GL11.glVertex3f(bounds.x0, bounds.y1, bounds.z1);
									    GL11.glVertex3f(bounds.x0, bounds.y1, bounds.z0);

									    GL11.glVertex3f(bounds.x1, bounds.y1, bounds.z1);
									    GL11.glVertex3f(bounds.x1, bounds.y1, bounds.z0);

									    GL11.glVertex3f(bounds.x1, bounds.y0, bounds.z1);
									    GL11.glVertex3f(bounds.x1, bounds.y0, bounds.z0);

									    GL11.glEnd();
									    
										 GL11.glDepthMask(true);
											GL11.glEnable(3553);
											GL11.glDisable(3042);
											GL11.glEnable(3008);
										
										GL11.glEnable(GL11.GL_CULL_FACE);
										
										//------------------
										}

										
										GL11.glDisable(3042);
										GL11.glDisable(2912);
										if (var82.minecraft.raining) {
											float var97 = var80;
											var27 = var82;
											var28 = var82.minecraft.player;
											Level var109 = var82.minecraft.level;
											var104 = (int) var28.x;
											var108 = (int) var28.y;
											var114 = (int) var28.z;
											ShapeRenderer var84 = ShapeRenderer.instance;
											GL11.glDisable(2884);
											GL11.glNormal3f(0.0F, 1.0F, 0.0F);
											GL11.glEnable(3042);
											GL11.glBlendFunc(770, 771);
											GL11.glBindTexture(
													3553,
													var82.minecraft.textureManager
															.load("/rain.png"));

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
														var74 = ((float) ((var27.levelTicks
																+ var110 * 3121 + var122 * 418711) % 32) + var97) / 32.0F;
														float var124 = (float) var110
																+ 0.5F
																- var28.x;
														var35 = (float) var122
																+ 0.5F
																- var28.z;
														float var92 = MathHelper
																.sqrt(var124
																		* var124
																		+ var35
																		* var35)
																/ (float) 5;
														GL11.glColor4f(
																1.0F,
																1.0F,
																1.0F,
																(1.0F - var92
																		* var92) * 0.7F);
														var84.begin();
														var84.vertexUV(
																(float) var110,
																(float) var86,
																(float) var122,
																0.0F,
																(float) var86
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														var84.vertexUV(
																(float) (var110 + 1),
																(float) var86,
																(float) (var122 + 1),
																2.0F,
																(float) var86
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														var84.vertexUV(
																(float) (var110 + 1),
																(float) var125,
																(float) (var122 + 1),
																2.0F,
																(float) var125
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														var84.vertexUV(
																(float) var110,
																(float) var125,
																(float) var122,
																0.0F,
																(float) var125
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														var84.vertexUV(
																(float) var110,
																(float) var86,
																(float) (var122 + 1),
																0.0F,
																(float) var86
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														var84.vertexUV(
																(float) (var110 + 1),
																(float) var86,
																(float) var122,
																2.0F,
																(float) var86
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														var84.vertexUV(
																(float) (var110 + 1),
																(float) var125,
																(float) var122,
																2.0F,
																(float) var125
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														var84.vertexUV(
																(float) var110,
																(float) var125,
																(float) (var122 + 1),
																0.0F,
																(float) var125
																		* 2.0F
																		/ 8.0F
																		+ var74
																		* 2.0F);
														var84.end();
													}
												}
											}

											GL11.glEnable(2884);
											GL11.glDisable(3042);
										}

										if (var82.entity != null) {
											var82.entity
													.renderHover(
															var82.minecraft.textureManager,
															var80);
										}

										GL11.glClear(256);
										GL11.glLoadIdentity();
										if (var82.minecraft.settings.anaglyph) {
											GL11.glTranslatef(
													(float) ((var77 << 1) - 1) * 0.1F,
													0.0F, 0.0F);
										}

										var82.hurtEffect(var80);
										if (var82.minecraft.settings.viewBobbing) {
											var82.applyBobbing(var80);
										}

										HeldBlock var112 = var82.heldBlock;
										var117 = var82.heldBlock.lastPos
												+ (var112.pos - var112.lastPos)
												* var80;
										var116 = var112.minecraft.player;
										GL11.glPushMatrix();
										GL11.glRotatef(var116.xRotO
												+ (var116.xRot - var116.xRotO)
												* var80, 1.0F, 0.0F, 0.0F);
										GL11.glRotatef(var116.yRotO
												+ (var116.yRot - var116.yRotO)
												* var80, 0.0F, 1.0F, 0.0F);
										var112.minecraft.renderer
												.setLighting(true);
										GL11.glPopMatrix();
										GL11.glPushMatrix();
										var69 = 0.8F;
										if (var112.moving) {
											var33 = MathHelper
													.sin((var74 = ((float) var112.offset + var80) / 7.0F) * 3.1415927F);
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
										if (var112.moving) {
											var33 = MathHelper
													.sin((var74 = ((float) var112.offset + var80) / 7.0F)
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

										ColorCache color = var112.minecraft.level
												.getBrightnessColor(
														(int) var116.x,
														(int) var116.y,
														(int) var116.z);
										GL11.glColor4f(color.R, color.G,
												color.B, 1.0F);

										ShapeRenderer var123 = ShapeRenderer.instance;
										if (var112.block != null) {
											var34 = 0.4F;
											GL11.glScalef(0.4F, var34, var34);
											GL11.glTranslatef(-0.5F, -0.5F,
													-0.5F);
											GL11.glBindTexture(
													3553,
													var112.minecraft.textureManager
															.load("/terrain.png"));
											var112.block.renderPreview(var123);
										} else {
											var116.bindTexture(var112.minecraft.textureManager);
											GL11.glScalef(1.0F, -1.0F, -1.0F);
											GL11.glTranslatef(0.0F, 0.2F, 0.0F);
											GL11.glRotatef(-120.0F, 0.0F, 0.0F,
													1.0F);
											GL11.glScalef(1.0F, 1.0F, 1.0F);
											var34 = 0.0625F;
											ModelPart var127;
											if (!(var127 = var112.minecraft.player
													.getModel().leftArm).hasList) {
												var127.generateList(var34);
											}

											GL11.glCallList(var127.list);
										}

										GL11.glDisable(2977);
										GL11.glPopMatrix();
										var112.minecraft.renderer
												.setLighting(false);
										if (!var82.minecraft.settings.anaglyph) {
											break;
										}

										++var77;
									}

									renderer.minecraft.hud
											.render(var65,
													renderer.minecraft.currentScreen != null,
													var94, var70);
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
								if (renderer.minecraft.notifyScreen != null) {
									renderer.minecraft.notifyScreen.render(
											var94, var70);
								}

								if (renderer.minecraft.currentScreen != null) {
									renderer.minecraft.currentScreen.render(
											var94, var70);
								}

								Thread.yield();
								Display.update();
							}
						}

						if (this.settings.limitFramerate) {
							Thread.sleep(5L);
						}

						checkGLError("Post render");
						++var15;
					} catch (Exception var58) {
						this.setCurrentScreen(new ErrorScreen("Client error",
								"The game broke! [" + var58 + "]"));
						var58.printStackTrace();
					}

					while (System.currentTimeMillis() >= var13 + 1000L) {
						this.debug = var15 + " fps, " + Chunk.chunkUpdates
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
			this.shutdown();
		}

	}

	public final void grabMouse() {
		if (!this.hasMouse) {
			this.hasMouse = true;
			if (this.levelLoaded) {
				try {
					Mouse.setNativeCursor(this.cursor);
					Mouse.setCursorPosition(this.width / 2, this.height / 2);
				} catch (LWJGLException var2) {
					var2.printStackTrace();
				}

				if (this.canvas == null) {
					this.canvas.requestFocus();
				}
			} else {
				Mouse.setGrabbed(true);
			}

			this.setCurrentScreen((GuiScreen) null);
			this.lastClick = this.ticks + 10000;
		}
	}

	public final void pause() {
		if (this.currentScreen == null) {
			this.setCurrentScreen(new PauseScreen());
		}
	}

	private void onMouseClick(int var1) {
		if (var1 != 0 || this.blockHitTime <= 0) {
			HeldBlock var2;
			if (var1 == 0) {
				var2 = this.renderer.heldBlock;
				this.renderer.heldBlock.offset = -1;
				var2.moving = true;
			}

			int x;
			if (var1 == 1 && (x = this.player.inventory.getSelected()) > 0
					&& this.gamemode.useItem(this.player, x)) {
				var2 = this.renderer.heldBlock;
				this.renderer.heldBlock.pos = 0.0F;
			} else if (this.selected == null) {
				if (var1 == 0 && !(this.gamemode instanceof CreativeGameMode)) {
					this.blockHitTime = 10;
				}

			} else {
				if (this.selected.entityPos == 1) {
					if (var1 == 0) {
						this.selected.entity.hurt(this.player, 4);
						return;
					}
				} else if (this.selected.entityPos == 0) {
					x = this.selected.x;
					int y = this.selected.y;
					int z = this.selected.z;
					if (var1 != 0) {
						if (this.selected.face == 0) {
							--y;
						}

						if (this.selected.face == 1) {
							++y;
						}

						if (this.selected.face == 2) {
							--z;
						}

						if (this.selected.face == 3) {
							++z;
						}

						if (this.selected.face == 4) {
							--x;
						}

						if (this.selected.face == 5) {
							++x;
						}
					}

					Block block = Block.blocks[this.level.getTile(x, y, z)];
					// if mouse click left
					if (var1 == 0) {
						if (block != Block.BEDROCK
								|| this.player.userType >= 100) {
							this.gamemode.hitBlock(x, y, z);
							return;
						}
						// else if its right click
					} else {
						int blockID = this.player.inventory.getSelected();
						if (blockID <= 0) {
							return; // if air return
						}
						AABB aabb = Block.blocks[blockID].getCollisionBox(x, y,
								z);
						if ((block == null || block == Block.WATER
								|| block == Block.STATIONARY_WATER
								|| block == Block.LAVA || block == Block.STATIONARY_LAVA)
								&& (aabb == null || (this.player.bb
										.intersects(aabb) ? false : this.level
										.isFree(aabb)))) {
							if (!this.gamemode.canPlace(blockID)) {
								return;
							}
							Block toCheck = Block.blocks[this.level.getTile(x,
									y - 1, z)];
							if (toCheck != null) {
								if (toCheck.id > 0) {
									if (toCheck == Block.SNOW) {
										if (this.selected.face == 1) {
											if (block == Block.SNOW)
												return;
											else
												y -= 1;
										}
									}
								}
							}

							if (this.isOnline()) {
								this.networkManager.sendBlockChange(x, y, z,
										var1, blockID);
							}

							this.level.netSetTile(x, y, z, blockID);
							var2 = this.renderer.heldBlock;
							this.renderer.heldBlock.pos = 0.0F;
							Block.blocks[blockID].onPlace(this.level, x, y, z);
						}
					}
				}

			}
		}
	}

	int recievedExtensionLength;

	private void tick() {
		if (this.soundPlayer != null) {
			SoundPlayer var1 = this.soundPlayer;
			SoundManager var2 = this.sound;
			if (System.currentTimeMillis() > var2.lastMusic
					&& var2.playMusic(var1, "calm")) {
				var2.lastMusic = System.currentTimeMillis()
						+ (long) var2.random.nextInt(900000) + 300000L;
			}
		}

		this.gamemode.spawnMob();
		HUDScreen var17 = this.hud;
		++this.hud.ticks;

		int var16;
		for (var16 = 0; var16 < var17.chat.size(); ++var16) {
			++((ChatLine) var17.chat.get(var16)).time;
		}

		GL11.glBindTexture(3553, this.textureManager.load("/terrain.png"));
		TextureManager var19 = this.textureManager;

		for (var16 = 0; var16 < var19.animations.size(); ++var16) {
			TextureFX var3;
			(var3 = var19.animations.get(var16)).anaglyph = var19.settings.anaglyph;
			var3.animate();
			var19.textureBuffer.clear();
			var19.textureBuffer.put(var3.textureData);
			var19.textureBuffer.position(0).limit(var3.textureData.length);
			GL11.glTexSubImage2D(3553, 0, var3.textureId % 16 << 4,
					var3.textureId / 16 << 4, 16, 16, 6408, 5121,
					var19.textureBuffer);
		}

		int var4;
		int i;
		int var40;
		int var46;
		int var45;
		if (this.networkManager != null
				&& !(this.currentScreen instanceof ErrorScreen)) {
			if (!this.networkManager.isConnected()) {
				this.progressBar.setTitle("Connecting..");
				this.progressBar.setProgress(0);
			} else {
				NetworkManager var20 = this.networkManager;
				if (this.networkManager.successful) {
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
								if(packetType.opcode != 8 && packetType.opcode != 2){
									System.out.println("Reading Packet: " + packetType.opcode);
								}
								networkHandler.in.get();
								Object[] packetParams = new Object[packetType.params.length];

								for (i = 0; i < packetParams.length; ++i) {
									packetParams[i] = networkHandler
											.readObject(packetType.params[i]);
									if(packetType.opcode != 8 && packetType.opcode != 2){
										System.out.println("Reading object: " + packetParams[i]);
									}
								}

								NetworkManager networkManager = networkHandler.netManager;
								if (networkHandler.netManager.successful) {
									if (packetType == PacketType.EXT_INFO) {
										String AppName = (String) packetParams[0];
										short ExtensionCount = (Short) packetParams[1];
										System.out.println("Connecting to AppName: "
														+ AppName
														+ " with extension count: "
														+ ExtensionCount);
										recievedExtensionLength = ExtensionCount;
									} else if (packetType == PacketType.EXT_ENTRY) {
										System.out.println("EXT_ENTRY packet");
										String ExtName = ((String) packetParams[0]);
										Integer Version = ((Integer) packetParams[1]).intValue();
										System.out.println("Adding ExtEntry to list");
										com.oyasunadev.mcraft.client.util.Constants.ServerSupportedExtensions
												.add(new ExtData(ExtName, Version));

										if (recievedExtensionLength == com.oyasunadev.mcraft.client.util.Constants.ServerSupportedExtensions
												.size()) {
											System.out.println("ExtEntry has reached total length");
											System.out.println("Making temp array data");
											List<ExtData> temp = new ArrayList<ExtData>();
											for (int j = 0; j < PacketType.packets.length - 1; j++) {
												if(PacketType.packets[j] != null){
												if (PacketType.packets[j].extName != "") {
													temp.add(new ExtData(
															PacketType.packets[j].extName,
															PacketType.packets[j].Version));
													System.out.println("Adding ExtEntry to packet to send:" + PacketType.packets[j].extName);
												}
											}
											}
											String AppName = "ClassiCube";
											Object[] toSendParams = new Object[] {
													AppName, temp.size() };
											System.out.println("Sending my ExtList inside a loop, one by one");
											networkManager.netHandler.send(
													PacketType.EXT_INFO,
													toSendParams);
											for (int k = 0; k < temp.size(); k++) {
												toSendParams = new Object[] {
														temp.get(k).Name,
														temp.get(k).Version };
												networkManager.netHandler.send(
														PacketType.EXT_ENTRY,
														toSendParams);
												System.out.println("Sent: " + temp.get(k).Name);
											}
											System.out.println("Done");
										}
									}
									else if(packetType == PacketType.SELECTION_CUBOID){
										byte ID = ((Byte) packetParams[0])
												.byteValue();
										String Name = ((String) packetParams[1]);
										Short X1 = ((Short) packetParams[2]);
										Short Y1 = ((Short) packetParams[3]);
										Short Z1 = ((Short) packetParams[4]);
										Short X2 = ((Short) packetParams[5]);
										Short Y2 = ((Short) packetParams[6]);
										Short Z2 = ((Short) packetParams[7]);
										byte r = ((Byte) packetParams[8])
												.byteValue();
										byte g = ((Byte) packetParams[9])
												.byteValue();
										byte b = ((Byte) packetParams[10])
												.byteValue();
										byte a = ((Byte) packetParams[11])
												.byteValue();
										SelectionBoxData data = new SelectionBoxData(
												ID, Name,
												new ColorCache(r/255.0F, g/255.0F, b/255.0F, a/255.0F),
												new CustomAABB(X1, Y1, Z1, X2, Y2, Z2));
										this.selectionBoxes.add(data);
									}else if (packetType == PacketType.REMOVE_SELECTION_CUBOID){
										byte ID = ((Byte) packetParams[0])
												.byteValue();
										if(this.selectionBoxes.size() >= ID)
											this.selectionBoxes.remove(ID);
									}
									else if (packetType == PacketType.ENV_SET_COLOR) {
										byte Variable = ((Byte) packetParams[0])
												.byteValue();
										byte r = ((Byte) packetParams[1])
												.byteValue();
										byte g = ((Byte) packetParams[2])
												.byteValue();
										byte b = ((Byte) packetParams[3])
												.byteValue();
										int dec = 256 * 256 * r + 256 * g + b;
										switch (Variable) {
										case 0: // sky
											this.level.skyColor = dec;
											break;
										case 1: // cloud
											this.level.cloudColor = dec;
											break;
										case 2: // fog
											this.level.fogColor = dec;
											break;
										case 3: // ambient light
												// (TODO)
											this.level.customShadowColour = new ColorCache(
													r / 255.0F, g / 255.0F,
													b / 255.0F);
											break;
										case 4: // diffuse color
												// (TODO)
											this.level.customLightColour = new ColorCache(
													r / 255.0F, g / 255.0F,
													b / 255.0F);
											break;
										}
										this.levelRenderer.refresh();
									} else if (packetType == PacketType.CLICK_DISTANCE) {
										short Distance = (Short) packetParams[0];
										this.gamemode.reachDistance = Distance / 32;
									} else if (packetType == PacketType.HOLDTHIS) {
										byte BlockToHold = ((Byte) packetParams[0])
												.byteValue();
										byte PreventChange = ((Byte) packetParams[1])
												.byteValue();
										boolean CanPreventChange = PreventChange > 0;

										if (CanPreventChange == true)
											GameSettings.CanReplaceSlot = false;

										this.player.inventory.selected = 0;
										this.player.inventory
												.replaceSlot(Block.blocks[BlockToHold]);

										if (CanPreventChange == false)
											GameSettings.CanReplaceSlot = true;
									} else if (packetType == PacketType.SET_TEXT_HOTKEY) {
										String Label = (String) packetParams[0];
										String Action = (String) packetParams[1];
										int keyCode = (Integer) packetParams[2];
										byte KeyMods = ((Byte) packetParams[3])
												.byteValue();
									} else if (packetType == PacketType.EXT_ADD_ENTITY) {
										byte playerID = ((Byte) packetParams[0])
												.byteValue();
										String playerName = (String) packetParams[1];
										String listName = (String) packetParams[2];
										String groupName = (String) packetParams[3];
										byte unusedRank = ((Byte) packetParams[4])
												.byteValue();
									} else if (packetType == PacketType.EXT_ADD_ENTITY) {
										byte playerID = ((Byte) packetParams[0])
												.byteValue();
										String playerName = (String) packetParams[1];
										String skinName = (String) packetParams[2];

										NetworkPlayer player = networkManager.players
												.get(playerID);
										if (player != null) {
											player.SkinName = skinName;
											((NetworkPlayer) player)
													.downloadSkin();
										}
									} else if (packetType == PacketType.EXT_REMOVE_PLAYER_NAME) {
										String playerName = (String) packetParams[0];
									}
									else if (packetType == PacketType.CUSTOM_BLOCK_SUPPORT_LEVEL) {
										System.out.println("Custom block packet");
										byte SupportLevel = ((Byte) packetParams[0])
												.byteValue();
										//byte[] toSendParams = new byte[] { com.oyasunadev.mcraft.client.util.Constants.SupportLevel };
										networkManager.netHandler
												.send(PacketType.CUSTOM_BLOCK_SUPPORT_LEVEL,
														com.oyasunadev.mcraft.client.util.Constants.SupportLevel);
										SessionData.SetAllowedBlocks(SupportLevel);
									}
									
										else if (packetType == PacketType.IDENTIFICATION) {
										networkManager.minecraft.progressBar
												.setTitle(packetParams[1]
														.toString());
										networkManager.minecraft.progressBar
												.setText(packetParams[2]
														.toString());
										networkManager.minecraft.player.userType = ((Byte) packetParams[3])
												.byteValue();
									} else if (packetType == PacketType.LEVEL_INIT) {
										networkManager.minecraft
												.setLevel((Level) null);
										networkManager.levelData = new ByteArrayOutputStream();
									} else if (packetType == PacketType.LEVEL_DATA) {
										short chunkLength = ((Short) packetParams[0])
												.shortValue();
										byte[] chunkData = (byte[]) ((byte[]) packetParams[1]);
										byte percentComplete = ((Byte) packetParams[2])
												.byteValue();
										networkManager.minecraft.progressBar
												.setProgress(percentComplete);
										networkManager.levelData.write(
												chunkData, 0, chunkLength);
									} else if (packetType == PacketType.LEVEL_FINALIZE) {
										try {
											networkManager.levelData.close();
										} catch (IOException e) {
											e.printStackTrace();
										}

										byte[] decompressedStream = LevelIO
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
										networkManager.minecraft.online = false;
										networkManager.levelLoaded = true;
										ProgressBarDisplay.InitEnv(this);
										this.levelRenderer.refresh();
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
														var5,
														var34,
														var36,
														var47,
														var10,
														(float) (var9 * 360) / 256.0F,
														(float) (var58 * 360) / 256.0F);
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
																(float) (var9 * 320 / 256));
												networkManager.minecraft.player
														.moveTo((float) var36 / 32.0F,
																(float) var47 / 32.0F,
																(float) var10 / 32.0F,
																(float) (var9 * 360) / 256.0F,
																(float) (var58 * 360) / 256.0F);
											}
										} else {
											byte var53;
											NetworkPlayer var61;
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
															.moveTo((float) var38 / 32.0F,
																	(float) var36 / 32.0F,
																	(float) var47 / 32.0F,
																	(float) (var53 * 360) / 256.0F,
																	(float) (var9 * 360) / 256.0F);
												} else {
													var53 = (byte) (var53 + 128);
													var36 = (short) (var36 - 22);
													if ((var61 = (NetworkPlayer) networkManager.players
															.get(Byte
																	.valueOf(var5))) != null) {
														var61.teleport(
																var38,
																var36,
																var47,
																(float) (var53 * 360) / 256.0F,
																(float) (var9 * 360) / 256.0F);
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
													byte var5 = var10001;
													if (var5 >= 0) {
														var53 = (byte) (var53 + 128);
														if ((var61 = (NetworkPlayer) networkManager.players
																.get(Byte
																		.valueOf(var5))) != null) {
															var61.queue(
																	var37,
																	var44,
																	var49,
																	(float) (var53 * 360) / 256.0F,
																	(float) (var9 * 360) / 256.0F);
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
														if ((var54 = (NetworkPlayer) networkManager.players
																.get(Byte
																		.valueOf(var5))) != null) {
															var54.queue(
																	(float) (var37 * 360) / 256.0F,
																	(float) (var44 * 360) / 256.0F);
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
															&& (var59 = (NetworkPlayer) networkManager.players
																	.get(Byte
																			.valueOf(var5))) != null) {
														var59.queue(var37,
																var44, var49);
													}
												} else if (packetType == PacketType.DESPAWN_PLAYER) {
													byte var5 = ((Byte) packetParams[0])
															.byteValue();
													if (var5 >= 0
															&& (var33 = (NetworkPlayer) networkManager.players
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
												// ===============KICK if packetType is not mutual================
												
														//if(com.oyasunadev.mcraft.client.util.Constants.ServerSupportedExtensions
																//.contains(new ExtData(
																	//	packetType.extName,
																		//packetType.Version))) 
																{
															/* else if (packetType == PacketType.ENV_SET_COLOR) {
														byte Variable = ((Byte) packetParams[0])
																.byteValue();
														byte r = ((Byte) packetParams[1])
																.byteValue();
														byte g = ((Byte) packetParams[2])
																.byteValue();
														byte b = ((Byte) packetParams[3])
																.byteValue();
														int dec = 256 * 256 * r + 256 * g + b;
														switch (Variable) {
														case 0: // sky
															this.level.skyColor = dec;
															break;
														case 1: // cloud
															this.level.cloudColor = dec;
															break;
														case 2: // fog
															this.level.fogColor = dec;
															break;
														case 3: // ambient light
																// (TODO)
															this.level.customShadowColour = new ColorCache(
																	r / 255.0F, g / 255.0F,
																	b / 255.0F);
															break;
														case 4: // diffuse color
																// (TODO)
															this.level.customLightColour = new ColorCache(
																	r / 255.0F, g / 255.0F,
																	b / 255.0F);
															break;
														}
														this.levelRenderer.refresh();
													} else if (packetType == PacketType.CLICK_DISTANCE) {
														short Distance = (Short) packetParams[0];
														this.gamemode.reachDistance = Distance / 32;
													} else if (packetType == PacketType.HOLDTHIS) {
														byte BlockToHold = ((Byte) packetParams[0])
																.byteValue();
														byte PreventChange = ((Byte) packetParams[1])
																.byteValue();
														boolean CanPreventChange = PreventChange > 0;

														if (CanPreventChange == true)
															GameSettings.CanReplaceSlot = false;

														this.player.inventory.selected = 0;
														this.player.inventory
																.replaceSlot(Block.blocks[BlockToHold]);

														if (CanPreventChange == false)
															GameSettings.CanReplaceSlot = true;
													} else if (packetType == PacketType.SET_TEXT_HOTKEY) {
														String Label = (String) packetParams[0];
														String Action = (String) packetParams[1];
														int keyCode = (Integer) packetParams[2];
														byte KeyMods = ((Byte) packetParams[3])
																.byteValue();
													} else if (packetType == PacketType.EXT_ADD_ENTITY) {
														byte playerID = ((Byte) packetParams[0])
																.byteValue();
														String playerName = (String) packetParams[1];
														String listName = (String) packetParams[2];
														String groupName = (String) packetParams[3];
														byte unusedRank = ((Byte) packetParams[4])
																.byteValue();
													} else if (packetType == PacketType.EXT_ADD_ENTITY) {
														byte playerID = ((Byte) packetParams[0])
																.byteValue();
														String playerName = (String) packetParams[1];
														String skinName = (String) packetParams[2];

														NetworkPlayer player = networkManager.players
																.get(playerID);
														if (player != null) {
															player.SkinName = skinName;
															((NetworkPlayer) player)
																	.downloadSkin();
														}
													} else if (packetType == PacketType.EXT_REMOVE_PLAYER_NAME) {
														String playerName = (String) packetParams[0];
													}*/
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
							var20.minecraft.online = false;
							var15.printStackTrace();
							var20.netHandler.close();
							var20.minecraft.networkManager = null;
						}
					}
				}

				Player var28 = this.player;
				var20 = this.networkManager;
				if (this.networkManager.levelLoaded) {
					int var24 = (int) (var28.x * 32.0F);
					var4 = (int) (var28.y * 32.0F);
					var40 = (int) (var28.z * 32.0F);
					var46 = (int) (var28.yRot * 256.0F / 360.0F) & 255;
					var45 = (int) (var28.xRot * 256.0F / 360.0F) & 255;
					var20.netHandler.send(
							PacketType.POSITION_ROTATION,
							new Object[] { Integer.valueOf(-1),
									Integer.valueOf(var24),
									Integer.valueOf(var4),
									Integer.valueOf(var40),
									Integer.valueOf(var46),
									Integer.valueOf(var45) });
				}
			}
		}

		if (this.currentScreen == null && this.player != null
				&& this.player.health <= 0) {
			this.setCurrentScreen((GuiScreen) null);
		}

		if (this.currentScreen == null || this.currentScreen.grabsMouse) {
			int var25;
			while (Mouse.next()) {
				if ((var25 = Mouse.getEventDWheel()) != 0) {
					this.player.inventory.swapPaint(var25);
				}

				if (this.currentScreen == null) {
					if (!this.hasMouse && Mouse.getEventButtonState()) {
						this.grabMouse();
					} else {
						if (Mouse.getEventButton() == 0
								&& Mouse.getEventButtonState()) {
							this.onMouseClick(0);
							this.lastClick = this.ticks;
						}

						if (Mouse.getEventButton() == 1
								&& Mouse.getEventButtonState()) {
							this.onMouseClick(1);
							this.lastClick = this.ticks;
						}

						if (Mouse.getEventButton() == 2
								&& Mouse.getEventButtonState()
								&& this.selected != null) {
							var16 = this.level.getTile(this.selected.x,
									this.selected.y, this.selected.z);
							this.player.inventory.grabTexture(var16,
									this.gamemode instanceof CreativeGameMode);
						}
					}
				}

				if (this.currentScreen != null) {
					this.currentScreen.mouseEvent();
				}
			}

			if (this.blockHitTime > 0) {
				--this.blockHitTime;
			}

			while (Keyboard.next()) {
				this.player.setKey(Keyboard.getEventKey(),
						Keyboard.getEventKeyState());
				if (Keyboard.getEventKeyState()) {
					if (this.currentScreen != null) {
						this.currentScreen.keyboardEvent();
					}

					if (this.currentScreen == null) {
						if (Keyboard.getEventKey() == 1) {
							this.pause();
						}

						if (Keyboard.getEventKey() == Keyboard.KEY_X) {
							if (HackState == ClientHacksState.HacksTagEnabled
									|| HackState == ClientHacksState.OpHacks
									&& this.player.userType >= 100) {
								this.player.noPhysics = !this.player.noPhysics;
								this.player.hovered = !this.player.hovered;
							}
						}

						if (Keyboard.getEventKey() == Keyboard.KEY_Z) {
							if (HackState == ClientHacksState.HacksTagEnabled
									|| HackState == ClientHacksState.NoHacksTagShown
									|| HackState == ClientHacksState.OpHacks
									&& this.player.userType >= 100) {
								this.player.flyingMode = !this.player.flyingMode;
							}
						}

						if (this.gamemode instanceof CreativeGameMode) {
							if (Keyboard.getEventKey() == this.settings.loadLocationKey.key) {
								this.player.resetPos();
							}

							if (Keyboard.getEventKey() == this.settings.saveLocationKey.key) {
								this.level.setSpawnPos((int) this.player.x,
										(int) this.player.y,
										(int) this.player.z, this.player.yRot);
								this.player.resetPos();
							}
						}

						Keyboard.getEventKey();
						if (Keyboard.getEventKey() == 63) {
							this.raining = !this.raining;
						}
						if (Keyboard.getEventKey() == 53
								&& this.networkManager != null
								&& this.networkManager.isConnected()) {
							this.player.releaseAllKeys();
							ChatInputScreenExtension s = new ChatInputScreenExtension();
							this.setCurrentScreen(s);
							s.inputLine = "/";
						}

						if (Keyboard.getEventKey() == 15
								&& this.gamemode instanceof SurvivalGameMode
								&& this.player.arrows > 0) {
							this.level.addEntity(new Arrow(this.level,
									this.player, this.player.x, this.player.y,
									this.player.z, this.player.yRot,
									this.player.xRot, 1.2F));
							--this.player.arrows;
						}

						if (Keyboard.getEventKey() == this.settings.inventoryKey.key) {
							// this.player.inventory.selected = 0;
							// this.player.inventory.replaceSlot(Block.blocks[6]);
							// GameSettings.CanReplaceSlot = false;
							this.gamemode.openInventory();
							//this.selectionBoxes.add(new SelectionBoxData((byte) 1,"",new ColorCache(0F,0F,0F,0.6F), new CustomAABB(12,45,30, 20, 30, 40))); 
						}

						if (Keyboard.getEventKey() == this.settings.chatKey.key
								&& this.networkManager != null
								&& this.networkManager.isConnected()) {
							this.player.releaseAllKeys();
							this.setCurrentScreen(new ChatInputScreenExtension());
						}
					}

					for (var25 = 0; var25 < 9; ++var25) {
						if (Keyboard.getEventKey() == var25 + 2) {
							if (GameSettings.CanReplaceSlot)
								this.player.inventory.selected = var25;
						}
					}

					if (Keyboard.getEventKey() == this.settings.toggleFogKey.key) {
						this.settings.toggleSetting(4, !Keyboard.isKeyDown(42)
								&& !Keyboard.isKeyDown(54) ? 1 : -1);
					}
				}
			}

			if (this.currentScreen == null) {
				if (Mouse.isButtonDown(0)
						&& (float) (this.ticks - this.lastClick) >= this.timer.tps / 4.0F
						&& this.hasMouse) {
					this.onMouseClick(0);
					this.lastClick = this.ticks;
				}

				if (Mouse.isButtonDown(1)
						&& (float) (this.ticks - this.lastClick) >= this.timer.tps / 4.0F
						&& this.hasMouse) {
					this.onMouseClick(1);
					this.lastClick = this.ticks;
				}
			}

			boolean var26 = this.currentScreen == null && Mouse.isButtonDown(0)
					&& this.hasMouse;
			boolean var35 = false;
			if (!this.gamemode.instantBreak && this.blockHitTime <= 0) {
				if (var26 && this.selected != null
						&& this.selected.entityPos == 0) {
					var4 = this.selected.x;
					var40 = this.selected.y;
					var46 = this.selected.z;
					this.gamemode.hitBlock(var4, var40, var46,
							this.selected.face);
				} else {
					this.gamemode.resetHits();
				}
			}
		}

		if (this.currentScreen != null) {
			this.lastClick = this.ticks + 10000;
		}

		if (this.currentScreen != null) {
			this.currentScreen.doInput();
			if (this.currentScreen != null) {
				this.currentScreen.tick();
			}
		}

		if (this.level != null) {
			com.mojang.minecraft.render.Renderer var29 = this.renderer;
			++this.renderer.levelTicks;
			HeldBlock var41 = var29.heldBlock;
			var29.heldBlock.lastPos = var41.pos;
			if (var41.moving) {
				++var41.offset;
				if (var41.offset == 7) {
					var41.offset = 0;
					var41.moving = false;
				}
			}

			Player var27 = var41.minecraft.player;
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

			if (var29.minecraft.raining) {
				com.mojang.minecraft.render.Renderer var39 = var29;
				var27 = var29.minecraft.player;
				Level var32 = var29.minecraft.level;
				var40 = (int) var27.x;
				var46 = (int) var27.y;
				var45 = (int) var27.z;

				for (i = 0; i < 50; ++i) {
					int var60 = var40 + var39.random.nextInt(9) - 4;
					int var52 = var45 + var39.random.nextInt(9) - 4;
					int var57;
					if ((var57 = var32.getHighestTile(var60, var52)) <= var46 + 4
							&& var57 >= var46 - 4) {
						float var56 = var39.random.nextFloat();
						float var62 = var39.random.nextFloat();
						var39.minecraft.particleManager
								.spawnParticle(new WaterDropParticle(var32,
										(float) var60 + var56,
										(float) var57 + 0.1F, (float) var52
												+ var62));
					}
				}
			}

			++this.levelRenderer.ticks;
			this.level.tickEntities();
			if (!this.isOnline()) {
				this.level.tick();
			}

			this.particleManager.tick();
		}

	}

	public final boolean isOnline() {
		return this.networkManager != null;
	}

	public final void generateLevel(int var1) {
		String var2 = this.session != null ? this.session.username
				: "anonymous";
		Level var4 = (new LevelGenerator(this.progressBar)).generate(var2,
				128 << var1, 128 << var1, 64);
		this.gamemode.prepareLevel(var4);
		this.setLevel(var4);
	}

	public final boolean loadOnlineLevel(String var1, int var2) {
		Level var3;
		if ((var3 = this.levelIo.loadOnline(this.host, var1, var2)) == null) {
			return false;
		} else {
			this.setLevel(var3);
			return true;
		}
	}

	public final void setLevel(Level var1) {
		if (this.applet == null
				|| !this.applet.getDocumentBase().getHost()
						.equalsIgnoreCase("minecraft.net")
				&& !this.applet.getDocumentBase().getHost()
						.equalsIgnoreCase("www.minecraft.net")
				|| !this.applet.getCodeBase().getHost()
						.equalsIgnoreCase("minecraft.net")
				&& !this.applet.getCodeBase().getHost()
						.equalsIgnoreCase("www.minecraft.net")) {
			var1 = null;
		}

		this.level = var1;
		if (var1 != null) {
			var1.initTransient();
			this.gamemode.apply(var1);
			var1.font = this.fontRenderer;
			var1.rendererContext$5cd64a7f = this;
			if (!this.isOnline()) {
				this.player = (Player) var1.findSubclassOf(Player.class);
			} else if (this.player != null) {
				this.player.resetPos();
				this.gamemode.preparePlayer(this.player);
				if (var1 != null) {
					var1.player = this.player;
					var1.addEntity(this.player);
				}
			}
		}

		if (this.player == null) {
			this.player = new Player(var1);
			this.player.resetPos();
			this.gamemode.preparePlayer(this.player);
			if (var1 != null) {
				var1.player = this.player;
			}
		}

		if (this.player != null) {
			this.player.input = new InputHandlerImpl(this.settings);
			this.gamemode.apply(this.player);
		}

		if (this.levelRenderer != null) {
			LevelRenderer var3 = this.levelRenderer;
			if (this.levelRenderer.level != null) {
				var3.level.removeListener(var3);
			}

			var3.level = var1;
			if (var1 != null) {
				var1.addListener(var3);
				var3.refresh();
			}
		}

		if (this.particleManager != null) {
			ParticleManager var5 = this.particleManager;
			if (var1 != null) {
				var1.particleEngine = var5;
			}

			for (int var4 = 0; var4 < 2; ++var4) {
				var5.particles[var4].clear();
			}
		}

		System.gc();
	}

	public void resize() {
		width = Display.getDisplayMode().getWidth();
		height = Display.getDisplayMode().getHeight();

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
}
