package com.mojang.minecraft.net;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.ErrorScreen;
import com.mojang.util.LogUtil;

public class ServerConnectThread extends Thread {

    private final String server;
    private final int port;
    private final Minecraft minecraft;
    private final NetworkManager netManager;

    public ServerConnectThread(NetworkManager networkManager, String server, int port, Minecraft minecraft) {
        this.netManager = networkManager;
        this.server = server;
        this.port = port;
        this.minecraft = minecraft;
    }

    @Override
    public void run() {
        try {
            netManager.connect(server, port);
        } catch (Exception ex) {
            LogUtil.logError("Failed to connect", ex);
            minecraft.setCurrentScreen(new ErrorScreen("Failed to connect",
                    "You failed to connect to the server. It\'s probably down!"));
            minecraft.isConnecting = false;
            minecraft.networkManager = null;
        }
    }
}
