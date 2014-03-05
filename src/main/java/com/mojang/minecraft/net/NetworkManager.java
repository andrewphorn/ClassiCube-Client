package com.mojang.minecraft.net;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.ErrorScreen;
import com.mojang.net.NetworkHandler;

public class NetworkManager {
    public ByteArrayOutputStream levelData;

    public NetworkHandler netHandler;

    public Minecraft minecraft;

    public boolean successful = false;

    public boolean levelLoaded = false;
    public HashMap<Byte, NetworkPlayer> players;

    public NetworkManager(Minecraft minecraft, String server, int port, String username, String key) {
        minecraft.isOnline = true;

        this.minecraft = minecraft;

        players = new HashMap<Byte, NetworkPlayer>();

        new ServerConnectThread(this, server, port, username, key, minecraft).start();
    }

    public void error(Exception e) {
        netHandler.close();

        ErrorScreen errorScreen = new ErrorScreen("Disconnected!", e.getMessage());

        minecraft.setCurrentScreen(errorScreen);

        e.printStackTrace();
    }

    public List<String> getPlayers() {
        ArrayList<String> list = new ArrayList<String>();

        list.add(minecraft.session.username);

        Iterator<NetworkPlayer> playerIterator = players.values().iterator();

        while (playerIterator.hasNext()) {
            NetworkPlayer networkPlayer = playerIterator.next();

            list.add(networkPlayer.name);
        }

        return list;
    }

    public boolean isConnected() {
        return netHandler != null && netHandler.connected;
    }

    public void sendBlockChange(int x, int y, int z, int mode, int block) {
        netHandler.send(PacketType.PLAYER_SET_BLOCK, new Object[] { x, y, z, mode, block });
    }
}
