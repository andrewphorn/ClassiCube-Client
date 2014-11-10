package com.mojang.minecraft.net;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mojang.util.LogUtil;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.ErrorScreen;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Collection;

public class NetworkManager {

    public static final int MAX_PACKETS_PER_TICK = 100;
    private static final int BUFFER_SIZE = 1048576;

    public Minecraft minecraft;

    private volatile boolean connected;
    public boolean handshakeSent = false;
    public SocketChannel channel;
    public final ByteBuffer in = ByteBuffer.allocate(BUFFER_SIZE);
    public final ByteBuffer out = ByteBuffer.allocate(BUFFER_SIZE);
    private final byte[] stringBytes = new byte[64];

    public ByteArrayOutputStream levelData;
    public boolean levelLoaded = false;
    private final HashMap<Byte, NetworkPlayer> players = new HashMap<>();

    public NetworkManager(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void beginConnect(String server, int port) {
        minecraft.isConnecting = true;
        new ServerConnectThread(this, server, port, minecraft).start();
    }

    // Called from ServerConnectThread
    public void connect(String ip, int port) throws IOException {
        channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(ip, port));
        channel.configureBlocking(false);

        /*
         * sock = channel.socket(); sock.setTcpNoDelay(true);
         * sock.setTrafficClass(soTrafficClass); sock.setKeepAlive(false);
         * sock.setReuseAddress(false); sock.setSoTimeout(100);
         * sock.getInetAddress().toString();
         */

        in.clear();
        out.clear();
        connected = true;
    }

    public boolean isConnected() {
        return connected;
    }
    
    public void setConnected(boolean value){
        connected = value;
    }

    @SuppressWarnings("rawtypes")
    public Object readObject(Class obj) {
        if (!connected) {
            return null;
        } else {
            try {
                if (obj == Long.TYPE) {
                    return in.getLong();
                } else if (obj == Integer.TYPE) {
                    return in.getInt();
                } else if (obj == Short.TYPE) {
                    return in.getShort();
                } else if (obj == Byte.TYPE) {
                    return in.get();
                } else if (obj == Double.TYPE) {
                    return in.getDouble();
                } else if (obj == Float.TYPE) {
                    return in.getFloat();
                } else if (obj == String.class) {
                    in.get(stringBytes);
                    return new String(stringBytes, "UTF-8").trim();
                } else if (obj == byte[].class) {
                    byte[] theBytes = new byte[1024];
                    in.get(theBytes);
                    return theBytes;
                } else {
                    return null;
                }
            } catch (Exception e) {
                error(e);
                return null;
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public final void send(PacketType packetType, Object... obj) {
        if (connected) {
            out.put(packetType.opcode);

            for (int i = 0; i < obj.length; ++i) {
                Class packetClass = packetType.params[i];
                Object packetObject = obj[i];
                if (connected) {
                    try {
                        if (packetClass == Long.TYPE) {
                            out.putLong((Long) packetObject);
                        } else if (packetClass == Integer.TYPE) {
                            out.putInt((Integer) packetObject);
                        } else if (packetClass == Short.TYPE) {
                            out.putShort(((Number) packetObject).shortValue());
                        } else if (packetClass == Byte.TYPE) {
                            out.put(((Number) packetObject).byteValue());
                        } else if (packetClass == Double.TYPE) {
                            out.putDouble((Double) packetObject);
                        } else if (packetClass == Float.TYPE) {
                            out.putFloat((Float) packetObject);
                        } else {
                            byte[] bytesToSend;
                            if (packetClass != String.class) {
                                if (packetClass == byte[].class) {
                                    if ((bytesToSend = (byte[]) packetObject).length < 1024) {
                                        bytesToSend = Arrays.copyOf(bytesToSend, 1024);
                                    }
                                    out.put(bytesToSend);
                                }
                            } else {
                                bytesToSend = ((String) packetObject).getBytes("UTF-8");
                                Arrays.fill(stringBytes, (byte) 32);

                                int j;
                                for (j = 0; j < 64 && j < bytesToSend.length; ++j) {
                                    stringBytes[j] = bytesToSend[j];
                                }

                                for (j = bytesToSend.length; j < 64; ++j) {
                                    stringBytes[j] = 32;
                                }

                                out.put(stringBytes);
                            }
                        }
                    } catch (Exception e) {
                        error(e);
                    }
                }
            }
        }
    }

    // Flips the buffer and writes out all pending data.
    public void writeOut() throws IOException {
        if (out.position() > 0) {
            out.flip();
            channel.write(out);
            out.compact();
        }
    }

    public void sendBlockChange(int x, int y, int z, int mode, int block) {
        send(PacketType.PLAYER_SET_BLOCK, x, y, z, mode, block);
    }

    public void addPlayer(byte playerId, NetworkPlayer newPlayer) {
        if (newPlayer == null) {
            throw new IllegalArgumentException("newPlayer is null");
        }
        players.put(playerId, newPlayer);
    }

    // TODO: move player list management into a separate class?
    public boolean hasPlayers() {
        return !players.isEmpty();
    }

    public NetworkPlayer getPlayer(byte playerId) {
        return players.get(playerId);
    }

    public NetworkPlayer removePlayer(byte playerId) {
        return players.remove(playerId);
    }

    public Collection<NetworkPlayer> getPlayers() {
        return players.values();
    }

    public List<String> getPlayerNames() {
        ArrayList<String> list = new ArrayList<>();
        list.add(minecraft.session.username);
        for (NetworkPlayer networkPlayer : players.values()) {
            list.add(networkPlayer.name);
        }
        return list;
    }

    private void error(Exception ex) {
        LogUtil.logWarning("Network communication error", ex);
        close();
        ErrorScreen errorScreen = new ErrorScreen("Disconnected!", ex.getMessage());
        minecraft.setCurrentScreen(errorScreen);
    }

    public final void close() {
        try {
            if (out.position() > 0) {
                out.flip();
                channel.write(out);
                out.compact();
            }
        } catch (Exception e) {
        }

        connected = false;

        try {
            channel.close();
        } catch (Exception e) {
        }

        channel = null;
    }
}
