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

	public NetworkHandler(String var1, int var2, Minecraft m) {
		try {
			channel = SocketChannel.open();
			channel.connect(new InetSocketAddress(var1, var2));
			channel.configureBlocking(false);
			System.currentTimeMillis();
			sock = channel.socket();
			connected = true;
			in.clear();
			out.clear();
			sock.setTcpNoDelay(true);
			sock.setTrafficClass(24);
			sock.setKeepAlive(false);
			sock.setReuseAddress(false);
			sock.setSoTimeout(100);
			sock.getInetAddress().toString();
		} catch (Exception e) {
			e.printStackTrace();
			m.setCurrentScreen(new ErrorScreen("Failed to connect",
					"You failed to connect to the server. It\'s probably down!"));
			m.online = false;

			m.networkManager = null;
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
		} catch (Exception var2) {
			;
		}

		connected = false;

		try {
			channel.close();
		} catch (Exception var1) {
			;
		}

		sock = null;
		channel = null;
	}

	@SuppressWarnings("rawtypes")
	public Object readObject(Class var1) {
		if (!connected) {
			return null;
		} else {
			try {
				if (var1 == Long.TYPE) {
					return Long.valueOf(in.getLong());
				} else if (var1 == Integer.TYPE) {
					return Integer.valueOf(in.getInt());
				} else if (var1 == Short.TYPE) {
					return Short.valueOf(in.getShort());
				} else if (var1 == Byte.TYPE) {
					return Byte.valueOf(in.get());
				} else if (var1 == Double.TYPE) {
					return Double.valueOf(in.getDouble());
				} else if (var1 == Float.TYPE) {
					return Float.valueOf(in.getFloat());
				} else if (var1 == String.class) {
					in.get(stringBytes);
					return new String(stringBytes, "UTF-8").trim();
				} else if (var1 == byte[].class) {
					byte[] var3 = new byte[1024];
					in.get(var3);
					return var3;
				} else {
					return null;
				}
			} catch (Exception var2) {
				netManager.error(var2);
				return null;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public final void send(PacketType packetType, Object... object) {
		if (connected) {
			out.put(packetType.opcode);

			for (int i = 0; i < object.length; ++i) {
				Class packetClass = packetType.params[i];
				Object packetObject = object[i];
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
							byte[] var9;
							if (packetClass != String.class) {
								if (packetClass == byte[].class) {
									if ((var9 = (byte[]) packetObject).length < 1024) {
										var9 = Arrays.copyOf(var9, 1024);
									}

									out.put(var9);
								}
							} else {
								var9 = ((String) packetObject).getBytes("UTF-8");
								Arrays.fill(stringBytes, (byte) 32);

								int var8;
								for (var8 = 0; var8 < 64 && var8 < var9.length; ++var8) {
									stringBytes[var8] = var9[var8];
								}

								for (var8 = var9.length; var8 < 64; ++var8) {
									stringBytes[var8] = 32;
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
