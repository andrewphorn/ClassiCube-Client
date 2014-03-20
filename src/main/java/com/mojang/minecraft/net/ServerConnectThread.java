package com.mojang.minecraft.net;

import java.net.ConnectException;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.ErrorScreen;
import com.mojang.net.NetworkHandler;
import com.oyasunadev.mcraft.client.util.Constants;

public class ServerConnectThread extends Thread {
    private String server;

    private int port;

    private String username;
    private String key;

    private Minecraft minecraft;
    private NetworkManager netManager;

    public ServerConnectThread(NetworkManager networkManager, String server, int port,
                               String username, String key, Minecraft minecraft) {
        super();

        netManager = networkManager;

        this.server = server;
        this.port = port;

        this.username = username;
        this.key = key;

        this.minecraft = minecraft;
    }

    @Override
    public void run() {
        try {
            tryConnect();
        } catch (ConnectException e) {
            minecraft.setCurrentScreen(new ErrorScreen("Failed to connect",
                    "You failed to connect to the server. It\'s probably down!"));
            minecraft.isOnline = false;

            minecraft.networkManager = null;
            netManager.successful = false;
        }
    }

    public void tryConnect() throws ConnectException {
        netManager.netHandler = new NetworkHandler(server, port, minecraft);
        netManager.netHandler.netManager = netManager;

        netManager.netHandler.send(
                PacketType.IDENTIFICATION,
                Constants.PROTOCOL_VERSION, username, key,
                (int) Constants.CLIENT_TYPE);

        netManager.successful = true;
    }
}
