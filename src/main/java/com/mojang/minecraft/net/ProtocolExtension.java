package com.mojang.minecraft.net;

import com.mojang.util.LogUtil;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ProtocolExtension {

    public final String name;
    public final int version;

    public ProtocolExtension(String name, int version) {
        this.name = name;
        this.version = version;
    }
    
    public static boolean isSupported(ProtocolExtension other){
        return supportedExtensions.contains(other);
    }
    
    private static final Set<ProtocolExtension> supportedExtensions = new HashSet<>();
    
    public static final ProtocolExtension CLICK_DISTANCE = new ProtocolExtension("ClickDistance", 1);
    public static final ProtocolExtension CUSTOM_BLOCKS = new ProtocolExtension("CustomBlocks", 1);
    public static final ProtocolExtension HELD_BLOCK = new ProtocolExtension("HeldBlock", 1);
    //public static final ProtocolExtension EXT_PLAYER_LIST_2 = new ProtocolExtension("ExtPlayerList", 2);
    public static final ProtocolExtension ENV_COLORS = new ProtocolExtension("EnvColors", 1);
    public static final ProtocolExtension SELECTION_CUBOID = new ProtocolExtension("SelectionCuboid", 1);
    public static final ProtocolExtension BLOCK_PERMISSIONS = new ProtocolExtension("BlockPermissions", 1);
    public static final ProtocolExtension CHANGE_MODEL = new ProtocolExtension("ChangeModel", 1);
    public static final ProtocolExtension ENV_MAP_APPEARANCE = new ProtocolExtension("EnvMapAppearance", 1);
    public static final ProtocolExtension ENV_WEATHER_TYPE = new ProtocolExtension("EnvWeatherType", 1);
    //public static final ProtocolExtension HACK_CONTROL = new ProtocolExtension("HackControl", 1);
    public static final ProtocolExtension MESSAGE_TYPES = new ProtocolExtension("MessageTypes", 1);

    static {
        supportedExtensions.add(CLICK_DISTANCE);
        supportedExtensions.add(CUSTOM_BLOCKS);
        supportedExtensions.add(HELD_BLOCK);
        supportedExtensions.add(ENV_COLORS);
        supportedExtensions.add(SELECTION_CUBOID);
        supportedExtensions.add(BLOCK_PERMISSIONS);
        supportedExtensions.add(CHANGE_MODEL);
        supportedExtensions.add(ENV_MAP_APPEARANCE);
        supportedExtensions.add(ENV_WEATHER_TYPE);
        supportedExtensions.add(MESSAGE_TYPES);
    }
    
    @Override
    public boolean equals(Object other) {
        if(other instanceof ProtocolExtension){
            ProtocolExtension otherExt = (ProtocolExtension)other;
            return name.equalsIgnoreCase(otherExt.name) && (version == otherExt.version);
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.name);
        hash = 61 * hash + this.version;
        return hash;
    }
}
