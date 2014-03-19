package com.mojang.minecraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.mojang.minecraft.render.TextureManager;

public final class GameSettings {

    public static String StatusString = "";
    public static String PercentString = "";

    public static boolean CanReplaceSlot = true;

    public static List<String> typingLog = new ArrayList<>();
    public static int typingLogPos = 0;

    public boolean showClouds = true;
    public byte thirdPersonMode = 0;
    public boolean CanSpeed = true;

    public transient Minecraft minecraft;
    private final File settingsFile;
    public int settingCount;

    // ==== CONSTANTS =============================================================================
    public static String[] smoothingOptions = new String[]{"OFF", "Automatic", "Universal"};
    public static String[] showNamesOptions
            = new String[]{"Hover", "Hover (No Scaling)", "Always", "Always (No Scaling)"};
    private static final String[] viewDistanceOptions
            = new String[]{"FAR", "NORMAL", "SHORT", "TINY"};

    // showNames values
    public static final int SHOWNAMES_HOVER = 0,
            SHOWNAMES_HOVER_UNSCALED = 1,
            SHOWNAMES_ALWAYS = 2,
            SHOWNAMES_ALWAYS_UNSCALED = 3;

    // thirdPersonMode values
    public static final int FIRST_PERSON = 0,
            THIRD_PERSON_BACK = 1,
            THIRD_PERSON_FRONT = 2;

    // hackType values
    public static final int HACKTYPE_NORMAL = 0,
            HACKTYPE_ADVANCED = 1;

    // valid range of values for viewDistance
    public static final int VIEWDISTANCE_MIN = 0,
            VIEWDISTANCE_MAX = viewDistanceOptions.length - 1;

    // smoothing values
    public static final int SMOOTHING_OFF = 0,
            SMOOTHING_AUTO = 1,
            SMOOTHING_UNIVERSAL = 2;

    public static final float SCALE_MIN = 0.6f,
            SCALE_MAX = 1.2f;

    // min valid value for anisotropy. Max is set by TextureManager.
    public static final int ANISOTROPY_OFF = 0;

    // ==== BINDINGS ==============================================================================
    public KeyBinding forwardKey = new KeyBinding("Forward", Keyboard.KEY_W);
    public KeyBinding leftKey = new KeyBinding("Left", Keyboard.KEY_A);
    public KeyBinding backKey = new KeyBinding("Back", Keyboard.KEY_S);
    public KeyBinding rightKey = new KeyBinding("Right", Keyboard.KEY_D);
    public KeyBinding jumpKey = new KeyBinding("Jump", Keyboard.KEY_SPACE);
    public KeyBinding inventoryKey = new KeyBinding("Block List", Keyboard.KEY_B);
    public KeyBinding chatKey = new KeyBinding("Chat", Keyboard.KEY_T);
    public KeyBinding toggleFogKey = new KeyBinding("Toggle fog", Keyboard.KEY_F);
    public KeyBinding saveLocationKey = new KeyBinding("Save location", Keyboard.KEY_RETURN);
    public KeyBinding loadLocationKey = new KeyBinding("Load location", Keyboard.KEY_R);
    public KeyBinding runKey = new KeyBinding("Run", Keyboard.KEY_LSHIFT);
    public KeyBinding flyKey = new KeyBinding("Fly", Keyboard.KEY_Z);
    public KeyBinding flyUp = new KeyBinding("Fly Up", Keyboard.KEY_Q);
    public KeyBinding flyDown = new KeyBinding("Fly Down", Keyboard.KEY_E);
    public KeyBinding noClip = new KeyBinding("NoClip", Keyboard.KEY_X);
    public KeyBinding[] bindings;
    public KeyBinding[] bindingsmore;

    // ==== SETTINGS ==============================================================================
    public int HackType = 0;
    public int ShowNames = 0;
    public String lastUsedTexturePack;
    public boolean HacksEnabled = true;
    public int smoothing = 0;
    public boolean limitFramerate = true;
    public boolean viewBobbing = true;
    public int viewDistance;

    // 0 = off, higher values mean nth-powers-of-2 (e.g. 1 => 2x, 2 => 4x, 3 => 8x, 4 => 16x)
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
            forwardKey, leftKey, backKey, rightKey, jumpKey, inventoryKey,
            chatKey, toggleFogKey, saveLocationKey, loadLocationKey};
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
                return "Anisotropic: " + (anisotropy == 0 ? "OFF" : (1 << anisotropy) + "x");
            case ALLOW_SERVER_TEXTURES:
                return "Allow server textures: " + (canServerChangeTextures ? "Yes" : "No");
            case SPEEDHACK_TYPE:
                return "SpeedHack type: " + (HackType == 0 ? "Normal" : "Adv");
            case FONT_SCALE:
                return "Font Scale: " + Math.round(scale * 100) + "%";
            case ENABLE_HACKS:
                return "Enable Hacks: " + (HacksEnabled ? "Yes" : "No");
            case SHOW_NAMES:
                return "Show Names: " + showNamesOptions[ShowNames];
            default:
                throw new IllegalArgumentException();
        }
    }

    private void load() {
        try {
            if (settingsFile.exists()) {
                try (FileReader fileReader = new FileReader(settingsFile);
                        BufferedReader reader = new BufferedReader(fileReader)) {
                    // Read the raw settings keys/values
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] setting = line.split(":");
                        String key = setting[0].toLowerCase();
                        String value = setting[1];
                        try {
                            parseOneSetting(key, value);
                        } catch (Exception ex) {
                            String errorMsg = String.format("Error parsing a setting: %s=%s", key, value);
                            LogUtil.logWarning(errorMsg, ex);
                        }
                    }
                }
            } else {
                LogUtil.logWarning("Options file not found at " + settingsFile + ", using defaults.");
            }
        } catch (Exception ex) {
            LogUtil.logError("Failed to load options from " + settingsFile, ex);
        }
    }

    private void parseOneSetting(String key, String value) {
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
                viewDistance = Math.min(Math.max(Byte.parseByte(value),
                        VIEWDISTANCE_MIN), VIEWDISTANCE_MAX);
                break;
            case "bobview":
                viewBobbing = isTrue;
                break;
            case "limitframerate":
                limitFramerate = isTrue;
                Display.setVSyncEnabled(limitFramerate);
                break;
            case "smoothing":
                smoothing = Math.min(Math.max(Byte.parseByte(value),
                        SMOOTHING_OFF), SMOOTHING_UNIVERSAL);
                break;
            case "anisotropic":
                anisotropy = Byte.parseByte(value);
                break;
            case "canserverchangetextures":
                canServerChangeTextures = isTrue;
                break;
            case "hacktype":
                HackType = Math.min(Math.max(Byte.parseByte(value),
                        HACKTYPE_NORMAL), HACKTYPE_ADVANCED);
                break;
            case "scale":
                // Round scale to nearest 10% step (0.1)
                float roundedVal = Math.round(Float.parseFloat(value) * 10) / 10f;
                scale = Math.min(Math.max(roundedVal, SCALE_MIN), SCALE_MAX);
                break;
            case "hacksenabled":
                HacksEnabled = isTrue;
                break;
            case "shownames":
                ShowNames = Math.min(Math.max(Byte.parseByte(value),
                        SHOWNAMES_HOVER), SHOWNAMES_ALWAYS_UNSCALED);
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

    public void save() {
        try {
            try (FileWriter fileWriter = new FileWriter(settingsFile);
                    PrintWriter writer = new PrintWriter(fileWriter)) {
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
                if (newViewDist < VIEWDISTANCE_MIN) {
                    newViewDist = VIEWDISTANCE_MAX;
                } else if(newViewDist > VIEWDISTANCE_MAX){
                    newViewDist = VIEWDISTANCE_MIN;
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
                smoothing++;
                if(smoothing > SMOOTHING_UNIVERSAL){
                    smoothing = SMOOTHING_OFF;
                }
                minecraft.textureManager.textures.clear();
                break;
            case ANISOTROPIC:
                anisotropy++;
                if(anisotropy > TextureManager.getMaxAnisotropySetting()){
                    anisotropy = ANISOTROPY_OFF;
                }
                minecraft.textureManager.textures.clear();
                break;
            case ALLOW_SERVER_TEXTURES:
                canServerChangeTextures = !canServerChangeTextures;
                break;
            case SPEEDHACK_TYPE:
                HackType++;
                if (HackType > HACKTYPE_ADVANCED) {
                    HackType = HACKTYPE_NORMAL;
                }
                break;
            case FONT_SCALE:
                scale += 0.1;
                if (scale > SCALE_MAX) {
                    scale = SCALE_MIN;
                }
                break;
            case ENABLE_HACKS:
                HacksEnabled = !HacksEnabled;
                break;
            case SHOW_NAMES:
                ShowNames++;
                if (ShowNames > SHOWNAMES_ALWAYS_UNSCALED) {
                    ShowNames = SHOWNAMES_HOVER;
                }
                break;
        }
        save();
    }
}
