package com.mojang.minecraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.mojang.minecraft.render.TextureManager;

public final class GameSettings implements Serializable {
    private static final long serialVersionUID = 2L;
    public static String StatusString = "";
    public static String PercentString = "";

    public static String[] smoothingOptions
            = new String[]{"OFF", "Automatic", "Universal"};
    private static final String[] viewDistanceOptions
            = new String[]{"FAR", "NORMAL", "SHORT", "TINY"};
    

    public static boolean CanReplaceSlot = true;

    public static List<String> typinglog = new ArrayList<>();
    public static int typinglogpos = 0;

    public boolean showClouds = true;
    public byte thirdPersonMode = 0;
    public boolean CanSpeed = true;
    
    public transient Minecraft minecraft;
    private final File settingsFile;
    public int settingCount;
    
    //==== BINDINGS ===============================================================================
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
    public KeyBinding flyKey = new KeyBinding("Fly", Keyboard.KEY_Z);
    public KeyBinding flyUp = new KeyBinding("Fly Up", Keyboard.KEY_Q);
    public KeyBinding flyDown = new KeyBinding("Fly Down", Keyboard.KEY_E);
    public KeyBinding noClip = new KeyBinding("NoClip", Keyboard.KEY_X);
    public KeyBinding[] bindings;
    public KeyBinding[] bindingsmore;
    
    //==== SETTINGS ===============================================================================
    public int HackType = 0;
    public int ShowNames = 0;
    public String lastUsedTexturePack;
    public boolean HacksEnabled = true;
    public int smoothing = 0;
    public boolean limitFramerate = true;
    public boolean viewBobbing = true;
    public int viewDistance;
    
    // 0 = off, higher values mean powers-of-2 (e.g. 1=>2x, 2=>4x, 3=>8x, 4=>16x)
    public int anisotropy;
    
    // Interface font scale, as a ratio of default font (1.0 => 100%)
    public float scale = 1;
    public boolean music = true;
    public boolean sound = true;
    public boolean invertMouse = false;
    public boolean canServerChangeTextures = true;
    public boolean showDebug = false;


    public GameSettings(Minecraft minecraft, File minecraftFolder) {
        bindings = new KeyBinding[]{
            forwardKey, leftKey, backKey, rightKey, jumpKey,
            inventoryKey, chatKey, toggleFogKey, saveLocationKey, loadLocationKey};
        bindingsmore = new KeyBinding[]{runKey, flyKey, flyUp, flyDown, noClip};

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

    private static String toOnOff(boolean value) {
        return (value ? "ON" : "OFF");
    }

    public String getSetting(Setting id) {
        switch (id) {
            case MUSIC:
                return "Music: " + toOnOff(music);
            case SOUND:
                return "Sound: " + toOnOff(sound);
            case INVERT_MOUSE:
                return "Invert mouse: " + toOnOff(invertMouse);
            case SHOW_DEBUG:
                return "Show Debug: " + toOnOff(showDebug);
            case RENDER_DISTANCE:
                return "Render distance: " + viewDistanceOptions[viewDistance];
            case VIEW_BOBBING:
                return "View bobbing: " + toOnOff(viewBobbing);
            case LIMIT_FRAMERATE:
                return "Limit framerate: " + toOnOff(limitFramerate);
            case SMOOTHING:
                return "Smoothing: " + smoothingOptions[smoothing];
            case ANISOTROPIC:
                return "Anisotropic: " + (anisotropy == 0 ? "OFF" : (1<<anisotropy) + "x");
            case ALLOW_SERVER_TEXTURES:
                return "Allow server textures: " + (canServerChangeTextures ? "Yes" : "No");
            case SPEEDHACK_TYPE:
                return "SpeedHack type: " + (HackType == 0 ? "Normal" : "Adv");
            case FONT_SCALE:
                return "Font Scale: " + new DecimalFormat("#.#").format(scale);
            case ENABLE_HACKS:
                return "Enable Hacks: " + (HacksEnabled ? "Yes" : "No");
            case SHOW_NAMES:
                return "Show Names: " + (ShowNames == 0 ? "Hover" : "Always");
            default:
                throw new IllegalArgumentException();
        }
    }

    private void load() {
        try {
            if (settingsFile.exists()) {
                try (FileReader fileReader = new FileReader(settingsFile)) {
                    BufferedReader reader = new BufferedReader(fileReader);
                    HashMap<String, String> rawSettings = new HashMap<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] setting = line.split(":");
                        rawSettings.put(setting[0].toLowerCase(), setting[1]);
                    }
                    parseLoadedSettings(rawSettings);
                }
            }
        } catch (Exception ex) {
            LogUtil.logError("Failed to load options.", ex);
        }
    }

    private void parseLoadedSettings(HashMap<String, String> settings) {
        for (String key : settings.keySet()) {
            String value = settings.get(key);
            boolean isTrue = "true".equalsIgnoreCase(value) || "1".equals(value);
            switch (key) {
                case "music":
                    music = isTrue;
                    break;
                case "sound":
                    sound = isTrue;
                    break;
                case "invertymouse":
                    invertMouse = isTrue;
                    break;
                case "showdebug":
                    showDebug = isTrue;
                    break;
                case "viewdistance":
                    viewDistance = Math.min( Math.max(Integer.parseInt(value), 0),
                            viewDistanceOptions.length - 1);
                    break;
                case "bobview":
                    viewBobbing = isTrue;
                    break;
                case "limitframerate":
                    limitFramerate = isTrue;
                    Display.setVSyncEnabled(limitFramerate);
                    break;
                case "smoothing":
                    smoothing = Integer.parseInt(value);
                    break;
                case "anisotropic":
                    smoothing = Integer.parseInt(value);
                    break;
                case "canserverchangetextures":
                    canServerChangeTextures = isTrue;
                    break;
                case "hacktype":
                    HackType = Integer.parseInt(value);
                    break;
                case "scale":
                    scale = Float.parseFloat(value);
                    break;
                case "hacksenabled":
                    HacksEnabled = isTrue;
                    break;
                case "shownames":
                    ShowNames = Integer.parseInt(value);
                    break;
                case "texturepack":
                    lastUsedTexturePack = value;
                    break;
                default:
                    for (KeyBinding binding : bindings) {
                        if (("key_" + binding.name.toLowerCase()).equals(value)) {
                            binding.key = Integer.parseInt(value);
                            break;
                        }
                    }
                    break;
            }
        }
    }

    public void save() {
        try {
            try (FileWriter fileWriter = new FileWriter(settingsFile)) {
                PrintWriter writer = new PrintWriter(fileWriter);

                writer.println("music:" + music);
                writer.println("sound:" + sound);
                writer.println("invertYMouse:" + invertMouse);
                writer.println("showDebug:" + showDebug);
                writer.println("viewDistance:" + viewDistance);
                writer.println("bobView:" + viewBobbing);
                writer.println("limitFramerate:" + limitFramerate);
                writer.println("smoothing:" + smoothing);
                writer.println("anisotropic:" + anisotropy);
                writer.println("canServerChangeTextures:" + canServerChangeTextures);
                writer.println("HackType:" + HackType);
                writer.println("Scale:" + scale);
                writer.println("HacksEnabled:" + HacksEnabled);
                writer.println("ShowNames:" + ShowNames);
                writer.println("texturepack:" + lastUsedTexturePack);
                for (KeyBinding binding : bindings) {
                    writer.println("key_" + binding.name + ":" + binding.key);
                }
            }
        } catch (Exception ex) {
            LogUtil.logError("Failed to save options.", ex);
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

    public void toggleSetting(Setting setting, int fogValue) {
        switch (setting) {
            case MUSIC:
                music = !music;
                break;
            case SOUND:
                sound = !sound;
                break;
            case INVERT_MOUSE:
                invertMouse = !invertMouse;
                break;
            case SHOW_DEBUG:
                showDebug = !showDebug;
                break;
            case RENDER_DISTANCE:
                int newViewDist = viewDistance + fogValue;
                if (newViewDist < 0) {
                    newViewDist = viewDistanceOptions.length-1;
                }else{
                    newViewDist = newViewDist % viewDistanceOptions.length;
                }
                viewDistance = newViewDist;
                break;
            case VIEW_BOBBING:
                viewBobbing = !viewBobbing;
                break;
            case LIMIT_FRAMERATE:
                limitFramerate = !limitFramerate;
                if (Display.isCreated()) {
                    Display.setVSyncEnabled(limitFramerate);
                }
                break;
            case SMOOTHING:
                smoothing = (smoothing + 1) % smoothingOptions.length;
                minecraft.textureManager.textures.clear();
                // minecraft.levelRenderer.refresh(); // (?)
                break;
            case ANISOTROPIC:
                anisotropy = (anisotropy + 1) % TextureManager.getMaxAnisotropySetting();
                minecraft.textureManager.textures.clear();
                break;
            case ALLOW_SERVER_TEXTURES:
                canServerChangeTextures = !canServerChangeTextures;
                break;
            case SPEEDHACK_TYPE:
                if (HackType == 1) {
                    HackType = 0;
                } else {
                    HackType++;
                }
                break;
            case FONT_SCALE:
                scale += 0.1;
                if (scale > 1.2f) {
                    scale = 0.6f;
                }
                break;
            case ENABLE_HACKS:
                HacksEnabled = !HacksEnabled;
                break;
            case SHOW_NAMES:
                if (ShowNames == 0) {
                    ShowNames = 1;
                } else {
                    ShowNames = 0;
                }
                break;
        }
        save();
    }
}
