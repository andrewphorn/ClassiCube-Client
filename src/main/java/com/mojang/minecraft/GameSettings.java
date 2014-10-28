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
import com.mojang.util.LogUtil;

public final class GameSettings {

    // ==== CONSTANTS =============================================================================
    public static String[] smoothingOptions = new String[]{"OFF", "Automatic", "Universal"};
    public static String[] showNamesOptions = new String[]{
        "Hover", "Hover (No Scaling)", "Always", "Always (No Scaling)"
    };
    // showNames values
    public static final int SHOWNAMES_HOVER = 0,
            SHOWNAMES_HOVER_UNSCALED = 1,
            SHOWNAMES_ALWAYS = 2,
            SHOWNAMES_ALWAYS_UNSCALED = 3;
    // common framerate limits
    public static int MAX_SUPPORTED_FRAMERATE = 60;
    public static final int[] FRAMERATE_LIMITS = {20, 30, 40, 60, 75, 85, 120, 144};
    // thirdPersonMode values
    public static final int FIRST_PERSON = 0,
            THIRD_PERSON_BACK = 1,
            THIRD_PERSON_FRONT = 2;
    // hackType values
    public static final int HACKTYPE_NORMAL = 0,
            HACKTYPE_ADVANCED = 1;
    private static final String[] viewDistanceOptions = {
        "TINY (8)", "TINY (16)", "SHORT (32)", "SHORT (64)",
        "NORMAL (128)", "NORMAL (256)", "FAR (512)", "FAR (1024)"
    };
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
    public static String StatusString = "";
    public static String PercentString = "";
    public static boolean CanReplaceSlot = true;
    // TODO Below two never used
    public static List<String> typingLog = new ArrayList<>();
    public static int typingLogPos = 0;

    private final File settingsFile;
    public boolean showClouds = true;
    public ThirdPersonMode thirdPersonMode = ThirdPersonMode.NONE;
    public boolean CanSpeed = true;
    public transient Minecraft minecraft;
    public int settingCount; // TODO Never used

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
    public int hackType = 0;
    public int showNames = 0;
    public String lastUsedTexturePack;
    public boolean hacksEnabled = true;
    public int smoothing = 0;
    public int framerateLimit = 60;
    public boolean viewBobbing = true;
    public int viewDistance = 4; // default to "normal (128)"

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

    private static String toOnOff(boolean value) {
        return (value ? "ON" : "OFF");
    }

    public String getBinding(int key) {
        return bindings[key].name + ": " + Keyboard.getKeyName(bindings[key].key);
    }

    public String getBindingMore(int key) {
        return bindingsmore[key].name + ": " + Keyboard.getKeyName(bindingsmore[key].key);
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
            case VIEW_DISTANCE:
                return "View distance: " + viewDistanceOptions[viewDistance];
            case VIEW_BOBBING:
                return "View bobbing: " + toOnOff(viewBobbing);
            case FRAMERATE_LIMIT:
                return "Framerate limit: " + (framerateLimit == 0 ? "OFF" : framerateLimit + " FPS");
            case SMOOTHING:
                return "Smoothing: " + smoothingOptions[smoothing];
            case ANISOTROPIC:
                return "Anisotropic: " + (anisotropy == 0 ? "OFF" : (1 << anisotropy) + "x");
            case ALLOW_SERVER_TEXTURES:
                return "Allow server textures: " + (canServerChangeTextures ? "Yes" : "No");
            case SPEEDHACK_TYPE:
                return "SpeedHack type: " + (hackType == 0 ? "Normal" : "Adv");
            case FONT_SCALE:
                return "Font Scale: " + Math.round(scale * 100) + "%";
            case ENABLE_HACKS:
                return "Enable Hacks: " + (hacksEnabled ? "Yes" : "No");
            case SHOW_NAMES:
                return "Show Names: " + showNamesOptions[showNames];
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
                // Not used any more. Replaced by framerateLimit.
                // Left here for legacy/compatibility reasons.
                if (isTrue) {
                    framerateLimit = 60;
                } else {
                    framerateLimit = 0;
                }
                break;
            case "frameratelimit":
                framerateLimit = Integer.parseInt(value);
                if (framerateLimit != 0) {
                    framerateLimit = Math.min(framerateLimit, MAX_SUPPORTED_FRAMERATE);
                    framerateLimit = closestTo(FRAMERATE_LIMITS, framerateLimit);
                }
                if (Display.isCreated()) {
                    Display.setVSyncEnabled(framerateLimit != 0);
                }
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
                hackType = Math.min(Math.max(Byte.parseByte(value),
                        HACKTYPE_NORMAL), HACKTYPE_ADVANCED);
                break;
            case "scale":
                // Round scale to nearest 10% step (0.1)
                float roundedVal = Math.round(Float.parseFloat(value) * 10) / 10f;
                scale = Math.min(Math.max(roundedVal, SCALE_MIN), SCALE_MAX);
                break;
            case "hacksenabled":
                hacksEnabled = isTrue;
                break;
            case "shownames":
                showNames = Math.min(Math.max(Byte.parseByte(value),
                        SHOWNAMES_HOVER), SHOWNAMES_ALWAYS_UNSCALED);
                break;
            case "texturepack":
                lastUsedTexturePack = value;
                break;
            default:
                for (KeyBinding binding : bindings) {
                    if (("key_" + binding.name.toLowerCase()).equals(key)) {
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
                writer.println("framerateLimit:" + framerateLimit);
                writer.println("smoothing:" + smoothing);
                writer.println("anisotropic:" + anisotropy);
                writer.println("canServerChangeTextures:" + canServerChangeTextures);
                writer.println("hackType:" + hackType);
                writer.println("scale:" + scale);
                writer.println("hacksEnabled:" + hacksEnabled);
                writer.println("showNames:" + showNames);
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
            case VIEW_DISTANCE:
                int newViewDist = viewDistance + fogValue;
                if (newViewDist < VIEWDISTANCE_MIN) {
                    newViewDist = VIEWDISTANCE_MAX;
                } else if (newViewDist > VIEWDISTANCE_MAX) {
                    newViewDist = VIEWDISTANCE_MIN;
                }
                viewDistance = newViewDist;
                break;
            case VIEW_BOBBING:
                viewBobbing = !viewBobbing;
                break;
            case FRAMERATE_LIMIT:
                if (framerateLimit == 0) {
                    // From "Off" to lowest limit
                    framerateLimit = FRAMERATE_LIMITS[0];
                } else if (framerateLimit == MAX_SUPPORTED_FRAMERATE) {
                    // From highest limit to "Off"
                    framerateLimit = 0;
                } else {
                    // Go to the next higher framerate
                    for (int i = 0; i < FRAMERATE_LIMITS.length; i++) {
                        if (framerateLimit == FRAMERATE_LIMITS[i]) {
                            if (FRAMERATE_LIMITS[i + 1] > MAX_SUPPORTED_FRAMERATE) {
                                if (FRAMERATE_LIMITS[i] < MAX_SUPPORTED_FRAMERATE) {
                                    // Special case: go up to screen refresh rate that's not on our list
                                    framerateLimit = MAX_SUPPORTED_FRAMERATE;
                                } else {
                                    // Wrap around to "Off"
                                    framerateLimit = 0;
                                }
                            }else{
                                // Go up to the next higher limit
                                framerateLimit = FRAMERATE_LIMITS[i + 1];
                            }
                            break;
                        }
                    }
                }
                if (Display.isCreated()) {
                    // TODO: decouple vsync from framerate limit
                    Display.setVSyncEnabled(framerateLimit != 0);
                }
                break;
            case SMOOTHING:
                smoothing++;
                if (smoothing > SMOOTHING_UNIVERSAL) {
                    smoothing = SMOOTHING_OFF;
                }
                minecraft.textureManager.textures.clear();
                break;
            case ANISOTROPIC:
                anisotropy++;
                if (anisotropy > TextureManager.getMaxAnisotropySetting()) {
                    anisotropy = ANISOTROPY_OFF;
                }
                minecraft.textureManager.textures.clear();
                break;
            case ALLOW_SERVER_TEXTURES:
                canServerChangeTextures = !canServerChangeTextures;
                break;
            case SPEEDHACK_TYPE:
                hackType++;
                if (hackType > HACKTYPE_ADVANCED) {
                    hackType = HACKTYPE_NORMAL;
                }
                break;
            case FONT_SCALE:
                scale += 0.1;
                if (scale > SCALE_MAX) {
                    scale = SCALE_MIN;
                }
                break;
            case ENABLE_HACKS:
                hacksEnabled = !hacksEnabled;
                break;
            case SHOW_NAMES:
                showNames++;
                if (showNames > SHOWNAMES_ALWAYS_UNSCALED) {
                    showNames = SHOWNAMES_HOVER;
                }
                break;
        }
        save();
    }

    private static int closestTo(int[] options, int target) {
        if (options == null) {
            throw new NullPointerException("options");
        }
        int closest = Integer.MAX_VALUE;
        long minDifference = Integer.MAX_VALUE;
        for (int i = 0; i < options.length; i++) {
            long difference = Math.abs((long) options[i] - target);
            if (minDifference > difference) {
                minDifference = difference;
                closest = options[i];
            }
        }
        return closest;
    }

    public void capRefreshRate(int maxRefreshRate) {
        MAX_SUPPORTED_FRAMERATE = maxRefreshRate;
        if (framerateLimit > maxRefreshRate) {
            framerateLimit = maxRefreshRate;
        }
    }
}
