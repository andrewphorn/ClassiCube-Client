package com.mojang.net;

import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.PacketType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public final class NetworkHandler {

	public volatile boolean connected;
	public SocketChannel channel;
	public ByteBuffer in = ByteBuffer.allocate(1048576);
	public ByteBuffer out = ByteBuffer.allocate(1048576);
	public NetworkManager netManager;
	private Socket sock;
	private byte[] stringBytes = new byte[64];

	public NetworkHandler(String var1, int var2) {
		try {
			channel = SocketChannel.open();
			this.channel.connect(new InetSocketAddress(var1, var2));
			this.channel.configureBlocking(false);
			System.currentTimeMillis();
			this.sock = this.channel.socket();
			this.connected = true;
			this.in.clear();
			this.out.clear();
			this.sock.setTcpNoDelay(true);
			this.sock.setTrafficClass(24);
			this.sock.setKeepAlive(false);
			this.sock.setReuseAddress(false);
			this.sock.setSoTimeout(100);
			this.sock.getInetAddress().toString();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final void close() {
		try {
			if (this.out.position() > 0) {
				this.out.flip();
				this.channel.write(this.out);
				this.out.compact();
			}
		} catch (Exception var2) {
			;
		}

		this.connected = false;

		try {
			this.channel.close();
		} catch (Exception var1) {
			;
		}

		this.sock = null;
		this.channel = null;
	}

	@SuppressWarnings("rawtypes")
	public Object readObject(Class var1) {
		if (!this.connected) {
			return null;
		} else {
			try {
				if (var1 == Long.TYPE) {
					return Long.valueOf(this.in.getLong());
				} else if (var1 == Integer.TYPE) {
					return Integer.valueOf(this.in.getInt());
				} else if (var1 == Short.TYPE) {
					return Short.valueOf(this.in.getShort());
				} else if (var1 == Byte.TYPE) {
					return Byte.valueOf(this.in.get());
				} else if (var1 == Double.TYPE) {
					return Double.valueOf(this.in.getDouble());
				} else if (var1 == Float.TYPE) {
					return Float.valueOf(this.in.getFloat());
				} else if (var1 == String.class) {
					this.in.get(this.stringBytes);
					return (new String(this.stringBytes, "UTF-8")).trim();
				} else if (var1 == byte[].class) {
					byte[] var3 = new byte[1024];
					this.in.get(var3);
					return var3;
				} else {
					return null;
				}
			} catch (Exception var2) {
				this.netManager.error(var2);
				return null;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public final void send(PacketType packetType, Object... object) {
		if (this.connected) {
			this.out.put(packetType.opcode);

			for (int i = 0; i < object.length; ++i) {
				Class packetClass = packetType.params[i];
				Object packetObject = object[i];
				if (this.connected) {
					try {
						if (packetClass == Long.TYPE) {
							this.out.putLong(((Long) packetObject).longValue());
						} else if (packetClass == Integer.TYPE) {
							this.out.putInt(((Number) packetObject).intValue());
						} else if (packetClass == Short.TYPE) {
							this.out.putShort(((Number) packetObject).shortValue());
						} else if (packetClass == Byte.TYPE) {
							this.out.put(((Number) packetObject).byteValue());
						} else if (packetClass == Double.TYPE) {
							this.out.putDouble(((Double) packetObject).doubleValue());
						} else if (packetClass == Float.TYPE) {
							this.out.putFloat(((Float) packetObject).floatValue());
						} else {
							byte[] var9;
							if (packetClass != String.class) {
								if (packetClass == byte[].class) {
									if ((var9 = ((byte[]) packetObject)).length < 1024) {
										var9 = Arrays.copyOf(var9, 1024);
									}

									this.out.put(var9);
								}
							} else {
								var9 = ((String) packetObject).getBytes("UTF-8");
								Arrays.fill(this.stringBytes, (byte) 32);

								int var8;
								for (var8 = 0; var8 < 64 && var8 < var9.length; ++var8) {
									this.stringBytes[var8] = var9[var8];
								}

								for (var8 = var9.length; var8 < 64; ++var8) {
									this.stringBytes[var8] = 32;
								}

								this.out.put(this.stringBytes);
							}
						}
					} catch (Exception e) {
						this.netManager.error(e);
					}
				}
			}

		}
	}
}
