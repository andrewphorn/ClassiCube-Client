package com.mojang.minecraft.net;

import java.net.ConnectException;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.ErrorScreen;
import com.oyasunadev.mcraft.client.util.Constants;

public class ServerConnectThread extends Thread {

    private final String server;
    private final int port;
    private final String username;
    private final String key;

    private final Minecraft minecraft;
    private final NetworkManager netManager;

    public ServerConnectThread(NetworkManager networkManager, String server, int port,
            String username, String key, Minecraft minecraft) {
        this.netManager = networkManager;
        this.server = server;
        this.port = port;
        this.username = username;
        this.key = key;
        this.minecraft = minecraft;
    }

    @Override
    public void run() {
        try {
            netManager.connect(server, port);
            netManager.send(
                    PacketType.IDENTIFICATION,
                    Constants.PROTOCOL_VERSION, username, key,
                    (int) Constants.CLIENT_TYPE);
        } catch (Exception e) {
            // TODO: test for race conditions
            minecraft.setCurrentScreen(new ErrorScreen("Failed to connect",
                    "You failed to connect to the server. It\'s probably down!"));
            minecraft.isConnecting = false;
            minecraft.networkManager = null;
        }
    }
}
