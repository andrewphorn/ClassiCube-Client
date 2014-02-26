package com.mojang.minecraft;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.mojang.minecraft.render.TextureManager;

public final class GameSettings implements Serializable {

	private static final long serialVersionUID = 1L;
	public static String StatusString = "";
	public static String PercentString = "";

	public static boolean CanReplaceSlot = true;

	public static List<String> typinglog = new ArrayList<String>();
	public static int typinglogpos = 0;

	private static final String[] renderDistances = new String[] { "FAR", "NORMAL", "SHORT", "TINY" };

	public boolean music = true;
	public boolean sound = true;
	public boolean invertMouse = false;
	public boolean canServerChangeTextures = true;
	public boolean showDebug = false;
	public int viewDistance = 0;
	public boolean viewBobbing = true;
	public boolean showClouds = true;
	public boolean anaglyph = false;
	public boolean limitFramerate = true;
	public boolean thirdPersonMode = false;
	public KeyBinding forwardKey = new KeyBinding("Forward", 17);
	public KeyBinding leftKey = new KeyBinding("Left", 30);
	public KeyBinding backKey = new KeyBinding("Back", 31);
	public KeyBinding rightKey = new KeyBinding("Right", 32);
	public KeyBinding jumpKey = new KeyBinding("Jump", 57);
	public KeyBinding inventoryKey = new KeyBinding("Block List", 48);
	public KeyBinding chatKey = new KeyBinding("Chat", 20);
	public KeyBinding toggleFogKey = new KeyBinding("Toggle fog", 33);
	public KeyBinding saveLocationKey = new KeyBinding("Save location", 28);
	public KeyBinding loadLocationKey = new KeyBinding("Load location", 19);
	public KeyBinding runKey = new KeyBinding("Run", 42);
	public KeyBinding[] bindings;
        public KeyBinding[] bindingsmore;
	public transient Minecraft minecraft;
	private File settingsFile;
	public int settingCount;
	public boolean CanSpeed = true;
	public int HackType = 0;
	public int ShowNames = 0;

	public String lastUsedTexturePack;

	public boolean HacksEnabled = true;

	public int smoothing = 0;

	public String[] smoothingOptions = new String[] { "OFF", "Automatic", "Universal" };
	public int anisotropic = 0;

	public float scale = 1.0f;

	public String[] anisotropicOptions = new String[] { "OFF", "ON" };
	public KeyBinding flyKey = new KeyBinding("Fly", Keyboard.KEY_Z);

	public KeyBinding flyUp = new KeyBinding("Fly Up", Keyboard.KEY_Q);
	public KeyBinding flyDown = new KeyBinding("Fly Down", Keyboard.KEY_E);        
	public KeyBinding noClip = new KeyBinding("NoClip", Keyboard.KEY_X);

	public GameSettings(Minecraft minecraft, File minecraftFolder) {
		bindings = new KeyBinding[] { forwardKey, leftKey, backKey, rightKey, jumpKey,
				inventoryKey, chatKey, toggleFogKey, saveLocationKey, loadLocationKey };
                bindingsmore = new KeyBinding[] { runKey, flyKey, flyUp, flyDown, noClip};
                
		settingCount = 15;

		this.minecraft = minecraft;

		settingsFile = new File(minecraftFolder, "options.txt");

		load();
	}

	public String getBinding(int key) {
		return bindings[key].name + ": " + Keyboard.getKeyName(bindings[key].key);
	}
        public String getBindingMore(int key) {
		return bindingsmore[key].name + ": " + Keyboard.getKeyName(bindingsmore[key].key);
	}

	public String getSetting(int id) {
		return id == 0 ? "Music: " + (music ? "ON" : "OFF") : id == 1 ? "Sound: "
				+ (sound ? "ON" : "OFF") : id == 2 ? "Invert mouse: "
				+ (invertMouse ? "ON" : "OFF") : id == 3 ? "Show Debug: "
				+ (showDebug ? "ON" : "OFF") : id == 4 ? "Render distance: "
				+ renderDistances[viewDistance] : id == 5 ? "View bobbing: "
				+ (viewBobbing ? "ON" : "OFF") : id == 6 ? "3d anaglyph: "
				+ (anaglyph ? "ON" : "OFF") : id == 7 ? "Limit framerate: "
				+ (limitFramerate ? "ON" : "OFF") : id == 8 ? "Smoothing: "
				+ smoothingOptions[smoothing] : id == 9 ? "Anisotropic: "
				+ anisotropicOptions[anisotropic] : id == 10 ? "Allow server textures: "
				+ (canServerChangeTextures ? "Yes" : "No") : id == 11 ? "SpeedHack Type: "
				+ (HackType == 0 ? "Normal" : "Adv") : id == 12 ? "Font Scale: "
				+ new DecimalFormat("#.#").format(scale) : id == 13 ? "Enable Hacks: "
				+ (HacksEnabled ? "Yes" : "No") : id == 14 ? "Show Names: "
				+ (ShowNames == 0 ? "Hover" : "Always") : "";
	}

	private void load() {
		try {
			if (settingsFile.exists()) {
				FileReader fileReader = new FileReader(settingsFile);
				BufferedReader reader = new BufferedReader(fileReader);

				String line = null;

				while ((line = reader.readLine()) != null) {
					String[] setting = line.split(":");

					if (setting[0].equals("music")) {
						music = setting[1].equals("true");
					}

					if (setting[0].equals("sound")) {
						sound = setting[1].equals("true");
					}

					if (setting[0].equals("invertYMouse")) {
						invertMouse = setting[1].equals("true");
					}

					if (setting[0].equals("showDebug")) {
						showDebug = setting[1].equals("true");
					}

					if (setting[0].equals("viewDistance")) {
						viewDistance = Integer.parseInt(setting[1]);
					}

					if (setting[0].equals("bobView")) {
						viewBobbing = setting[1].equals("true");
					}

					if (setting[0].equals("anaglyph3d")) {
						anaglyph = setting[1].equals("true");
					}

					if (setting[0].equals("limitFramerate")) {
						limitFramerate = setting[1].equals("true");
						Display.setVSyncEnabled(limitFramerate);
					}

					if (setting[0].equals("smoothing")) {
						smoothing = Integer.parseInt(setting[1]);
					}

					if (setting[0].equals("anisotropic")) {
						anisotropic = Integer.parseInt(setting[1]);
					}
					if (setting[0].equals("canServerChangeTextures")) {
						canServerChangeTextures = setting[1].equals("true");
					}
					if (setting[0].equals("HackType")) {
						HackType = Integer.parseInt(setting[1]);
					}
					if (setting[0].equals("Scale")) {
						scale = Float.parseFloat(setting[1]);
					}
					if (setting[0].equals("HacksEnabled")) {
						HacksEnabled = setting[1].equals("true");
					}
					if (setting[0].equals("ShowNames")) {
						ShowNames = Integer.parseInt(setting[1]);
					}
					if (setting[0].equals("texturepack")) {
						lastUsedTexturePack = setting[1];
					}

					for (int index = 0; index < bindings.length; index++) {
						if (setting[0].equals("key_" + bindings[index].name)) {
							bindings[index].key = Integer.parseInt(setting[1]);
						}
					}
				}

				reader.close();
			}
		} catch (Exception e) {
			System.out.println("Failed to load options");

			e.printStackTrace();
		}
	}

	public void save() {
		try {
			FileWriter fileWriter = new FileWriter(settingsFile);
			PrintWriter writer = new PrintWriter(fileWriter);

			writer.println("music:" + music);
			writer.println("sound:" + sound);
			writer.println("invertYMouse:" + invertMouse);
			writer.println("showDebug:" + showDebug);
			writer.println("viewDistance:" + viewDistance);
			writer.println("bobView:" + viewBobbing);
			writer.println("anaglyph3d:" + anaglyph);
			writer.println("limitFramerate:" + limitFramerate);
			writer.println("smoothing:" + smoothing);
			writer.println("anisotropic:" + anisotropic);
			writer.println("canServerChangeTextures:" + canServerChangeTextures);
			writer.println("HackType:" + HackType);
			writer.println("Scale:" + scale);
			writer.println("HacksEnabled:" + HacksEnabled);
			writer.println("ShowNames:" + ShowNames);
			writer.println("texturepack:" + lastUsedTexturePack);
			for (int binding = 0; binding < bindings.length; binding++) {
				writer.println("key_" + bindings[binding].name + ":" + bindings[binding].key);
			}

			writer.close();
		} catch (Exception e) {
			System.out.println("Failed to save options");

			e.printStackTrace();
		}
	}

	public void setBinding(int key, int keyID) {
		bindings[key].key = keyID;

		save();
	}
        
        public void setBindingMore(int key, int keyID) {
		bindingsmore[key].key = keyID;

		save();
	}

	public void toggleSetting(int setting, int fogValue) {
		if (setting == 0) {
			music = !music;
		}

		if (setting == 1) {
			sound = !sound;
		}

		if (setting == 2) {
			invertMouse = !invertMouse;
		}

		if (setting == 3) {
			showDebug = !showDebug;
		}

		if (setting == 4) {
			viewDistance = viewDistance + fogValue & 3;
		}

		if (setting == 5) {
			viewBobbing = !viewBobbing;
		}

		if (setting == 6) {
			anaglyph = !anaglyph;

			TextureManager textureManager = minecraft.textureManager;
			Iterator<?> iterator = minecraft.textureManager.textureImages.keySet().iterator();

			int i;
			BufferedImage image;

			while (iterator.hasNext()) {
				i = (Integer) iterator.next();
				image = textureManager.textureImages.get(Integer.valueOf(i));

				textureManager.load(image, i);
			}

			iterator = textureManager.textures.keySet().iterator();

			while (iterator.hasNext()) {
				String s = (String) iterator.next();

				try {
					if (s.startsWith("##")) {
						image = TextureManager.load1(ImageIO.read(TextureManager.class
								.getResourceAsStream(s.substring(2))));
					} else {
						image = ImageIO.read(TextureManager.class.getResourceAsStream(s));
					}

					i = textureManager.textures.get(s);

					textureManager.load(image, i);
				} catch (IOException var6) {
					var6.printStackTrace();
				}
			}
		}

		if (setting == 7) {
			limitFramerate = !limitFramerate;
			if (Display.isCreated()) {
				Display.setVSyncEnabled(limitFramerate);
			}
		}

		if (setting == 8) {
			if (smoothing == smoothingOptions.length - 1) {
				smoothing = 0;
			} else {
				smoothing++;
			}

			minecraft.textureManager.textures.clear();

			// minecraft.levelRenderer.refresh();
		}

		if (setting == 9) {
			if (anisotropic == anisotropicOptions.length - 1) {
				anisotropic = 0;
			} else {
				anisotropic++;
			}

			minecraft.textureManager.textures.clear();

			// minecraft.levelRenderer.refresh();
		}
		if (setting == 10) {
			canServerChangeTextures = !canServerChangeTextures;
		}
		if (setting == 11) {
			if (HackType == 1) {
				HackType = 0;
			} else {
				HackType++;
			}
		}
		if (setting == 12) {
			scale += 0.1;
			if (scale > 1.2f) {
				scale = 0.6f;
			}
		}
		if (setting == 13) {
			HacksEnabled = !HacksEnabled;
		}
		if (setting == 14) {
			if (ShowNames == 0) {
				ShowNames = 1;
			} else {
				ShowNames = 0;
			}
		}

		save();
	}

}
