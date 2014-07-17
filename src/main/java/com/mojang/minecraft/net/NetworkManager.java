package com.mojang.minecraft.net;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mojang.util.LogUtil;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.ErrorScreen;
import com.mojang.net.NetworkHandler;

public class NetworkManager {
    // max number of packets to receive per tick
    public static final int MAX_PACKETS_PER_TICK = 100;
    
    public ByteArrayOutputStream levelData;

    public NetworkHandler netHandler;

    public Minecraft minecraft;

    public boolean successful = false;

    public boolean levelLoaded = false;
    public HashMap<Byte, NetworkPlayer> players = new HashMap<>();

    public NetworkManager(Minecraft minecraft, String server, int port, String username, String key) {
        minecraft.isOnline = true;
        this.minecraft = minecraft;

        new ServerConnectThread(this, server, port, username, key, minecraft).start();
    }

    public void error(Exception ex) {
        LogUtil.logWarning("Network communication error", ex);
        netHandler.close();
        ErrorScreen errorScreen = new ErrorScreen("Disconnected!", ex.getMessage());
        minecraft.setCurrentScreen(errorScreen);
    }

    public List<String> getPlayers() {
        ArrayList<String> list = new ArrayList<>();
        list.add(minecraft.session.username);
        for (NetworkPlayer networkPlayer : players.values()) {
            list.add(networkPlayer.name);
        }
        return list;
    }

    public boolean isConnected() {
        return netHandler != null && netHandler.connected;
    }

    public void sendBlockChange(int x, int y, int z, int mode, int block) {
        netHandler.send(PacketType.PLAYER_SET_BLOCK, x, y, z, mode, block);
    }
}
