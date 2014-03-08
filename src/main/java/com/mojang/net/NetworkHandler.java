package com.mojang.net;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.ErrorScreen;
import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.PacketType;

public final class NetworkHandler {

    public volatile boolean connected;
    public SocketChannel channel;
    public ByteBuffer in = ByteBuffer.allocate(1048576);
    public ByteBuffer out = ByteBuffer.allocate(1048576);
    public NetworkManager netManager;
    private Socket sock;
    private byte[] stringBytes = new byte[64];
    protected int soTrafficClass = 0x04 | 0x08 | 0x010;

    public NetworkHandler(String ip, int port, Minecraft mc) {
        try {
            channel = SocketChannel.open();
            channel.connect(new InetSocketAddress(ip, port));
            channel.configureBlocking(false);

            System.currentTimeMillis();
            /*sock = channel.socket();
            sock.setTcpNoDelay(true);
            sock.setTrafficClass(soTrafficClass);
            sock.setKeepAlive(false);
            sock.setReuseAddress(false);
            sock.setSoTimeout(100);
            sock.getInetAddress().toString();*/

            connected = true;
            in.clear();
            out.clear();

        } catch (Exception e) {
            e.printStackTrace();
            mc.setCurrentScreen(new ErrorScreen("Failed to connect", "You failed to connect to the server. It\'s probably down!"));
            mc.isOnline = false;

            mc.networkManager = null;
            netManager.successful = false;
        }
    }

    public final void close() {
        try {
            if (out.position() > 0) {
                out.flip();
                channel.write(out);
                out.compact();
            }
        } catch (Exception e) {}

        connected = false;

        try {
            channel.close();
        } catch (Exception e) {}

        sock = null;
        channel = null;
    }

    @SuppressWarnings("rawtypes")
    public Object readObject(Class obj) {
        if (!connected) {
            return null;
        } else {
            try {
                if (obj == Long.TYPE) {
                    return Long.valueOf(in.getLong());
                } else if (obj == Integer.TYPE) {
                    return Integer.valueOf(in.getInt());
                } else if (obj == Short.TYPE) {
                    return Short.valueOf(in.getShort());
                } else if (obj == Byte.TYPE) {
                    return Byte.valueOf(in.get());
                } else if (obj == Double.TYPE) {
                    return Double.valueOf(in.getDouble());
                } else if (obj == Float.TYPE) {
                    return Float.valueOf(in.getFloat());
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
                netManager.error(e);
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
                            out.putLong(((Long) packetObject).longValue());
                        } else if (packetClass == Integer.TYPE) {
                            out.putInt(((Number) packetObject).intValue());
                        } else if (packetClass == Short.TYPE) {
                            out.putShort(((Number) packetObject).shortValue());
                        } else if (packetClass == Byte.TYPE) {
                            out.put(((Number) packetObject).byteValue());
                        } else if (packetClass == Double.TYPE) {
                            out.putDouble(((Double) packetObject).doubleValue());
                        } else if (packetClass == Float.TYPE) {
                            out.putFloat(((Float) packetObject).floatValue());
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
                        netManager.error(e);
                    }
                }
            }

        }
    }
}
