package com.mojang.minecraft.net;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.HotKeyData;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.PlayerListComparator;
import com.mojang.minecraft.PlayerListNameData;
import com.mojang.minecraft.SelectionBoxData;
import com.mojang.minecraft.SessionData;
import com.mojang.minecraft.gui.ErrorScreen;
import com.mojang.minecraft.gui.HUDScreen;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelLoader;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.level.tile.TextureSide;
import com.mojang.minecraft.model.ModelManager;
import com.mojang.minecraft.physics.CustomAABB;
import com.mojang.minecraft.render.TextureManager;
import com.mojang.net.NetworkHandler;
import com.mojang.util.ColorCache;
import com.mojang.util.LogUtil;
import com.mojang.util.MathHelper;
import com.oyasunadev.mcraft.client.util.Constants;

public class PacketHandler {

    private static final List<ProtocolExtension> supportedExtensions = new ArrayList<>();
    private int extEntriesExpected, extEntriesReceived;
    private boolean receivedExtInfo;

    private final Minecraft minecraft;
    public boolean canSendHeldBlock = false;
    private boolean serverSupportsMessages = false;

    public boolean isLoadingLevel = false;

    public PacketHandler(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public void setLoadingLevel(boolean value) {
        isLoadingLevel = value;
    }

    // return true if more packets should be read; return false if that's it
    public boolean handlePacket(NetworkHandler networkHandler) throws IOException, Exception {
        networkHandler.in.flip();
        byte packetId = networkHandler.in.get(0);
        if (packetId < 0 || packetId > PacketType.packets.length - 1) {
            throw new IOException("Unknown packet ID received: " + packetId);
        }

        PacketType packetType = PacketType.packets[packetId];
        if (networkHandler.in.remaining() < packetType.length + 1) {
            networkHandler.in.compact();
            return false;
        }
        networkHandler.in.get();
        Object[] packetParams = new Object[packetType.params.length];

        for (int i = 0; i < packetParams.length; ++i) {
            packetParams[i] = networkHandler.readObject(packetType.params[i]);
        }

        NetworkManager networkManager = networkHandler.netManager;
        if (networkHandler.netManager.successful) {
            if (packetType == PacketType.EXT_INFO) {
                if (receivedExtInfo) {
                    LogUtil.logWarning("Received multiple ExtInfo packets! Only one was expected.");
                }
                receivedExtInfo = true;
                String appName = (String) packetParams[0];
                short extensionCount = (Short) packetParams[1];
                LogUtil.logInfo(String.format("Connecting to AppName \"%s\" with ExtensionCount %d",
                        appName, extensionCount));
                extEntriesExpected = extensionCount;
                supportedExtensions.clear();

            } else if (packetType == PacketType.EXT_ENTRY) {
                extEntriesReceived++;
                String extName = (String) packetParams[0];
                Integer version = (Integer) packetParams[1];

                if (extEntriesReceived > extEntriesExpected) {
                    LogUtil.logWarning(String.format(
                            "Expected %d ExtEntries but received too many (%d)! "
                            + "This ext will be ignored: %s with version %d",
                            extEntriesReceived, extEntriesExpected, extName, version));
                } else {
                    ProtocolExtension serverExt = new ProtocolExtension(extName, version);
                    LogUtil.logInfo(String.format("Receiving ext: %s with version: %d",
                            serverExt.name, serverExt.version));
                    if (ProtocolExtension.isSupported(serverExt)) {
                        supportedExtensions.add(serverExt);
                        if (extName.equalsIgnoreCase(ProtocolExtension.HELD_BLOCK.name)) {
                            canSendHeldBlock = true;
                        } else if (extName.equalsIgnoreCase(ProtocolExtension.MESSAGE_TYPES.name)) {
                            serverSupportsMessages = true;
                        }
                    }

                    if (extEntriesExpected == extEntriesReceived) {
                        LogUtil.logInfo(String.format(
                                "Sending list of mutually-supported CPE extensions (%d)",
                                supportedExtensions.size()));
                        Object[] toSendParams = new Object[]{
                            Constants.CLIENT_NAME, (short) supportedExtensions.size()};
                        networkManager.netHandler.send(PacketType.EXT_INFO, toSendParams);
                        for (ProtocolExtension ext : supportedExtensions) {
                            LogUtil.logInfo(String.format("Sending ext: %s with version: %d",
                                    ext.name, ext.version));
                            toSendParams = new Object[]{ext.name, ext.version};
                            networkManager.netHandler.send(PacketType.EXT_ENTRY, toSendParams);
                        }
                    }
                }

            } else if (packetType == PacketType.SELECTION_CUBOID) {
                if (!ProtocolExtension.isSupported(ProtocolExtension.SELECTION_CUBOID)) {
                    LogUtil.logWarning("Server attempted to use unsupported extension: SelectionCuboid");
                }
                Level level = minecraft.level;
                byte selectionId = (byte) packetParams[0];
                String selectionName = (String) packetParams[1];
                // Selection coordinates must be clamped to map boundaries.
                int x1 = MathHelper.clamp((short) packetParams[2], 0, level.width);
                int y1 = MathHelper.clamp((short) packetParams[3], 0, level.height);
                int z1 = MathHelper.clamp((short) packetParams[4], 0, level.length);
                // Max values for coordinates may not exceed map dimensions.
                // They also cannot be lower than min values.
                int x2 = MathHelper.clamp((short) packetParams[5], x1, level.width);
                int y2 = MathHelper.clamp((short) packetParams[6], y1, level.height);
                int z2 = MathHelper.clamp((short) packetParams[7], z1, level.length);
                // Color components must be clamped to valid range (0-255)
                int r = MathHelper.clamp((short) packetParams[8], 0, 255);
                int g = MathHelper.clamp((short) packetParams[9], 0, 255);
                int b = MathHelper.clamp((short) packetParams[10], 0, 255);
                int a = MathHelper.clamp((short) packetParams[11], 0, 255);

                SelectionBoxData data = new SelectionBoxData(selectionId, selectionName,
                        new ColorCache(r / 255F, g / 255F, b / 255F, a / 255F),
                        new CustomAABB(x1, y1, z1, x2, y2, z2)
                );
                // If a cuboid with the same ID already exists, it will be replaced.
                minecraft.selectionBoxes.put(selectionId, data);

            } else if (packetType == PacketType.REMOVE_SELECTION_CUBOID) {
                if (!ProtocolExtension.isSupported(ProtocolExtension.SELECTION_CUBOID)) {
                    LogUtil.logWarning("Server attempted to use unsupported extension: SelectionCuboid");
                }
                byte selectionId = (Byte) packetParams[0];
                if (minecraft.selectionBoxes.remove(selectionId) == null) {
                    LogUtil.logWarning("Attempting to remove selection with unknown id " + selectionId);
                }

            } else if (packetType == PacketType.ENV_SET_COLOR) {
                if (!ProtocolExtension.isSupported(ProtocolExtension.ENV_COLORS)) {
                    LogUtil.logWarning("Server attempted to use unsupported extension: EnvColors");
                }
                byte envVariable = (Byte) packetParams[0];
                int r = (Short) packetParams[1];
                int g = (Short) packetParams[2];
                int b = (Short) packetParams[3];
                // If R, G, or B is out-of-range, we should reset the color to default.
                boolean doReset = (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255);
                int dec = (r & 0x0ff) << 16 | (g & 0x0ff) << 8 | b & 0x0ff;
                switch (envVariable) {
                    case 0: // sky
                        if (doReset) {
                            minecraft.level.skyColor = Level.DEFAULT_SKY_COLOR;
                        } else {
                            minecraft.level.skyColor = dec;
                        }
                        break;
                    case 1: // cloud
                        if (doReset) {
                            minecraft.level.cloudColor = Level.DEFAULT_CLOUD_COLOR;
                        } else {
                            minecraft.level.cloudColor = dec;
                        }
                        break;
                    case 2: // fog
                        if (doReset) {
                            minecraft.level.fogColor = Level.DEFAULT_FOG_COLOR;
                        } else {
                            minecraft.level.fogColor = dec;
                        }
                        break;
                    case 3: // ambient light
                        if (doReset) {
                            minecraft.level.customShadowColour = null;
                        } else {
                            minecraft.level.customShadowColour = new ColorCache(r / 255F, g / 255F, b / 255F);
                        }
                        minecraft.levelRenderer.refresh();
                        break;
                    case 4: // diffuse color
                        if (doReset) {
                            minecraft.level.customLightColour = null;
                        } else {
                            minecraft.level.customLightColour = new ColorCache(r / 255F, g / 255F, b / 255F);
                        }
                        minecraft.levelRenderer.refresh();
                        break;
                }

            } else if (packetType == PacketType.ENV_SET_MAP_APPEARANCE) {
                if (!ProtocolExtension.isSupported(ProtocolExtension.ENV_MAP_APPEARANCE)) {
                    LogUtil.logWarning("Server attempted to use unsupported extension: EnvMapAppearance");
                }
                String textureUrl = (String) packetParams[0];
                byte sideBlock = (Byte) packetParams[1];
                byte edgeBlock = (Byte) packetParams[2];
                short sideLevel = (Short) packetParams[3];

                if (minecraft.settings.canServerChangeTextures) {
                    if (sideBlock == -1) {
                        minecraft.textureManager.customSideBlock = null;
                    } else if (sideBlock < Block.blocks.length) {
                        int ID = Block.blocks[sideBlock].textureId;
                        minecraft.textureManager.customSideBlock = minecraft.textureManager.textureAtlas.get(ID);
                    }
                    if (edgeBlock == -1) {
                        minecraft.textureManager.customEdgeBlock = null;
                    } else if (edgeBlock < Block.blocks.length) {
                        Block block = Block.blocks[edgeBlock];
                        int ID = block.getTextureId(TextureSide.Top);
                        minecraft.textureManager.customEdgeBlock = minecraft.textureManager.textureAtlas.get(ID);
                    }
                    if (textureUrl.length() > 0) {
                        File path = new File(Minecraft.getMinecraftDirectory(), "/skins/terrain");
                        if (!path.exists()) {
                            path.mkdirs();
                        }
                        String hash = minecraft.getHash(textureUrl);
                        if (hash != null) {
                            File file = new File(path, hash + ".png");
                            BufferedImage image;
                            if (!file.exists()) {
                                minecraft.downloadImage(new URL(textureUrl), file);
                            }
                            image = ImageIO.read(file);
                            if (image.getWidth() % 16 == 0 && image.getHeight() % 16 == 0) {
                                minecraft.textureManager.animations.clear();
                                minecraft.textureManager.currentTerrainPng = image;
                            }
                        }
                    } else {
                        try {
                            minecraft.textureManager.currentTerrainPng = ImageIO.read(
                                    TextureManager.class.getResourceAsStream("/terrain.png"));
                        } catch (IOException ex2) {
                            LogUtil.logError("Error reading default terrain texture.", ex2);
                        }
                    }
                    minecraft.level.waterLevel = sideLevel;
                    minecraft.levelRenderer.refresh();
                }

            } else if (packetType == PacketType.CLICK_DISTANCE) {
                if (!ProtocolExtension.isSupported(ProtocolExtension.CLICK_DISTANCE)) {
                    LogUtil.logWarning("Server attempted to use unsupported extension: ClickDistance");
                }
                short clickDistance = (Short) packetParams[0];
                minecraft.gamemode.reachDistance = clickDistance / 32;

            } else if (packetType == PacketType.HOLD_THIS) {
                if (!ProtocolExtension.isSupported(ProtocolExtension.HELD_BLOCK)) {
                    LogUtil.logWarning("Server attempted to use unsupported extension: HeldBlock");
                }
                byte blockToHold = (Byte) packetParams[0];
                byte preventChange = (Byte) packetParams[1];
                boolean canPreventChange = preventChange > 0;

                if (canPreventChange) {
                    GameSettings.CanReplaceSlot = false;
                }

                minecraft.player.inventory.selected = 0;
                minecraft.player.inventory.replaceSlot(Block.blocks[blockToHold]);

                if (!canPreventChange) {
                    GameSettings.CanReplaceSlot = true;
                }

            } else if (packetType == PacketType.SET_TEXT_HOTKEY) {
                LogUtil.logWarning("Server attempted to use unsupported extension: TextHotKey");
                String label = (String) packetParams[0];
                String action = (String) packetParams[1];
                int keyCode = (Integer) packetParams[2];
                byte keyMods = (Byte) packetParams[3];
                HotKeyData data = new HotKeyData(label, action, keyCode, keyMods);
                //minecraft.hotKeys.add(data);

            } else if (packetType == PacketType.EXT_ADD_PLAYER_NAME) {
                LogUtil.logWarning("Server attempted to use unsupported extension: ExtPlayerList");
                short nameId = (short) packetParams[0];
                String playerName = (String) packetParams[1];
                String listName = (String) packetParams[2];
                String groupName = (String) packetParams[3];
                byte unusedRank = (Byte) packetParams[4];

                int playerIndex = -1;

                for (PlayerListNameData b : minecraft.playerListNameData) {
                    if (b.nameID == nameId) {
                        // Already exists, update the entry.
                        playerIndex = minecraft.playerListNameData.indexOf(b);
                        break;
                    }
                }

                if (playerIndex == -1) {
                    minecraft.playerListNameData.add(new PlayerListNameData(nameId,
                            playerName, listName, groupName, unusedRank));
                } else {
                    minecraft.playerListNameData.set(playerIndex,
                            new PlayerListNameData(nameId, playerName,
                                    listName, groupName, unusedRank));
                }

                Collections.sort(minecraft.playerListNameData, new PlayerListComparator());

            } else if (packetType == PacketType.EXT_ADD_ENTITY) {
                LogUtil.logWarning("Server attempted to use unsupported extension: ExtPlayerList version 1");
                byte playerID = (Byte) packetParams[0];
                String InGameName = (String) packetParams[1];
                String skinName = (String) packetParams[2];
                if (skinName != null) {
                    if (playerID >= 0) {
                        NetworkPlayer tmp = networkManager.players.get(playerID);
                        if (tmp != null) {
                            tmp.defaultTexture = false;
                            if ("default".equals(skinName)) {
                                tmp.defaultTexture = true;
                            }
                            tmp.SkinName = skinName;
                            tmp.downloadSkin(tmp.SkinName);
                            tmp.bindTexture(minecraft.textureManager);
                            tmp.displayName = InGameName;
                            tmp.renderHover(minecraft.textureManager);
                        }
                    } else if (playerID == -1) {
                        minecraft.player.textureName = skinName;
                        new SkinDownloadThread(minecraft.player, skinName).start();
                        minecraft.player.bindTexture(minecraft.textureManager);
                        //No need to set the display name for yourself
                    }
                }
            } else if (packetType == PacketType.EXT_REMOVE_PLAYER_NAME) {
                LogUtil.logWarning("Server attempted to use unsupported extension: ExtPlayerList");
                short nameID = (short) packetParams[0];
                List<PlayerListNameData> cache = minecraft.playerListNameData;
                for (int q = 0; q < minecraft.playerListNameData.size(); q++) {
                    if (minecraft.playerListNameData.get(q).nameID == nameID) {
                        cache.remove(q);
                    }
                }
                minecraft.playerListNameData = cache;

            } else if (packetType == PacketType.CUSTOM_BLOCK_SUPPORT_LEVEL) {
                if (!ProtocolExtension.isSupported(ProtocolExtension.CUSTOM_BLOCKS)) {
                    LogUtil.logWarning("Server attempted to use unsupported extension: CustomBlocks");
                }
                byte supportLevel = (Byte) packetParams[0];
                LogUtil.logInfo("Using CustomBlocks level " + supportLevel);
                networkManager.netHandler.send(
                        PacketType.CUSTOM_BLOCK_SUPPORT_LEVEL,
                        Constants.CUSTOM_BLOCK_SUPPORT_LEVEL);
                SessionData.setAllowedBlocks(supportLevel);

            } else if (packetType == PacketType.SET_BLOCK_PERMISSIONS) {
                if (!ProtocolExtension.isSupported(ProtocolExtension.BLOCK_PERMISSIONS)) {
                    LogUtil.logWarning("Server attempted to use unsupported extension: BlockPermissions");
                }
                byte blockType = (byte) packetParams[0];
                byte allowPlacement = (byte) packetParams[1];
                byte allowDeletion = (byte) packetParams[2];
                Block block = Block.blocks[blockType];
                if (block == null) {
                    LogUtil.logWarning("Unknown block ID given for SetBlockPermission packet: " + blockType);
                    return true;
                }
                if (allowPlacement == 0) {
                    if (minecraft.disallowedPlacementBlocks.add(block)) {
                        LogUtil.logInfo("DisallowingPlacement block: " + block);
                    }
                } else if (minecraft.disallowedPlacementBlocks.remove(block)) {
                    LogUtil.logInfo("AllowingPlacement block: " + block);
                }
                if (allowDeletion == 0) {
                    if (minecraft.disallowedBreakingBlocks.add(block)) {
                        LogUtil.logInfo("DisallowingDeletion block: " + block);
                    }
                } else if (minecraft.disallowedBreakingBlocks.remove(block)) {
                    LogUtil.logInfo("AllowingDeletion block: " + block);
                }

            } else if (packetType == PacketType.CHANGE_MODEL) {
                if (!ProtocolExtension.isSupported(ProtocolExtension.CHANGE_MODEL)) {
                    LogUtil.logWarning("Server attempted to use unsupported extension: ChangeModel");
                }
                byte playerId = (byte) packetParams[0];
                String modelName = ((String) packetParams[1]).toLowerCase();
                if (playerId >= 0) {
                    // Set another player's model
                    NetworkPlayer netPlayer = networkManager.players.get(playerId);
                    if (netPlayer != null) {
                        ModelManager m = new ModelManager();
                        if (m.getModel(modelName) == null) {
                            netPlayer.modelName = "humanoid";
                        } else {
                            netPlayer.modelName = modelName;
                        }
                        netPlayer.bindTexture(minecraft.textureManager);
                    }
                } else if (playerId == -1) {
                    // Set own model
                    ModelManager modelManager = new ModelManager();
                    if (modelManager.getModel(modelName) == null) {
                        minecraft.player.modelName = "humanoid";
                    } else {
                        minecraft.player.modelName = modelName;
                    }
                    minecraft.player.bindTexture(minecraft.textureManager);
                }

            } else if (packetType == PacketType.ENV_SET_WEATHER_TYPE) {
                if (!ProtocolExtension.isSupported(ProtocolExtension.ENV_WEATHER_TYPE)) {
                    LogUtil.logWarning("Server attempted to use unsupported extension: EnvWeatherType");
                }
                byte weatherType = (byte) packetParams[0];
                if (weatherType == 0) {
                    minecraft.isRaining = false;
                    minecraft.isSnowing = false;
                } else if (weatherType == 1) {
                    minecraft.isRaining = !minecraft.isRaining;
                    minecraft.isSnowing = false;
                } else if (weatherType == 2) {
                    minecraft.isSnowing = !minecraft.isSnowing;
                    minecraft.isRaining = false;
                }

            } else if (packetType == PacketType.IDENTIFICATION) {
                minecraft.progressBar.setTitle(packetParams[1].toString());
                minecraft.progressBar.setText(packetParams[2].toString());
                minecraft.player.userType = (Byte) packetParams[3];
                setLoadingLevel(true);

            } else if (packetType == PacketType.LEVEL_INIT) {
                minecraft.selectionBoxes.clear();
                minecraft.setLevel(null);
                networkManager.levelData = new ByteArrayOutputStream();
                setLoadingLevel(true);

            } else if (packetType == PacketType.LEVEL_DATA) {
                short chunkLength = (short) packetParams[0];
                byte[] chunkData = (byte[]) packetParams[1];
                byte percentComplete = (byte) packetParams[2];
                networkManager.minecraft.progressBar.setProgress(percentComplete);
                networkManager.levelData.write(chunkData, 0, chunkLength);

            } else if (packetType == PacketType.LEVEL_FINALIZE) {
                networkManager.minecraft.progressBar.setProgress(100);
                try {
                    networkManager.levelData.close();
                } catch (IOException ex) {
                    LogUtil.logError("Error receiving level data.");
                    throw ex; // We are in an inconsistent state; abort!
                }

                byte[] decompressedStream = LevelLoader.decompress(
                        new ByteArrayInputStream(networkManager.levelData.toByteArray()));
                networkManager.levelData = null;
                short xSize = (short) packetParams[0];
                short ySize = (short) packetParams[1];
                short zSize = (short) packetParams[2];
                Level newLevel = new Level();
                newLevel.setNetworkMode(true);
                newLevel.setData(xSize, ySize, zSize, decompressedStream);
                networkManager.minecraft.setLevel(newLevel);
                networkManager.minecraft.isConnecting = false;
                networkManager.levelLoaded = true;
                // ProgressBarDisplay.InitEnv(this);
                // this.levelRenderer.refresh();
                setLoadingLevel(false);

            } else if (packetType == PacketType.BLOCK_CHANGE) {
                if (networkManager.minecraft.level != null) {
                    networkManager.minecraft.level.netSetTile(
                            (short) packetParams[0], (short) packetParams[1],
                            (short) packetParams[2], (byte) packetParams[3]);
                } // else: no level is loaded, ignore block change

            } else if (packetType == PacketType.SPAWN_PLAYER) {
                byte newPlayerId = (Byte) packetParams[0];
                String newPlayerName = (String) packetParams[1];
                short newPlayerX = (Short) packetParams[2];
                short newPlayerY = (Short) packetParams[3];
                short newPlayerZ = (Short) packetParams[4];
                byte newPlayerXRot = (Byte) packetParams[5];
                byte newPlayerYRot = (Byte) packetParams[6];
                if (newPlayerId >= 0) {
                    // Spawn a new player
                    newPlayerXRot = (byte) (newPlayerXRot + 128);
                    newPlayerY = (short) (newPlayerY - 22);
                    NetworkPlayer newPlayer = new NetworkPlayer(networkManager.minecraft,
                            newPlayerName, newPlayerX, newPlayerY, newPlayerZ,
                            newPlayerYRot * 360 / 256F, newPlayerXRot * 360 / 256F);
                    networkManager.players.put(newPlayerId, newPlayer);
                    minecraft.level.addEntity(newPlayer);
                } else {
                    // Set own spawnpoint
                    minecraft.level.setSpawnPos(
                            newPlayerX / 32, newPlayerY / 32, newPlayerZ / 32,
                            newPlayerXRot * 320 / 256);
                    minecraft.player.moveTo(newPlayerX / 32F,
                            newPlayerY / 32F, newPlayerZ / 32F,
                            newPlayerXRot * 360 / 256F, newPlayerYRot * 360 / 256F);
                }

            } else if (packetType == PacketType.POSITION_ROTATION) {
                byte playerId = (Byte) packetParams[0];
                short newX = (Short) packetParams[1];
                short newY = (Short) packetParams[2];
                short newZ = (Short) packetParams[3];
                byte newXRot = (Byte) packetParams[4];
                byte newYRot = (Byte) packetParams[5];
                if (playerId < 0) {
                    // Move self
                    minecraft.player.moveTo(newX / 32F, newY / 32F, newZ / 32F,
                            newXRot * 360 / 256F, newYRot * 360 / 256F);
                } else {
                    // Move another player
                    newXRot = (byte) (newXRot + 128);
                    newY = (short) (newY - 22);
                    NetworkPlayer networkPlayer = networkManager.players.get(playerId);
                    if (networkPlayer != null) {
                        networkPlayer.teleport(newX, newY, newZ,
                                newYRot * 360 / 256F, newXRot * 360 / 256F);
                    } // else: unknown player ID given, ignore it.
                }

            } else if (packetType == PacketType.POSITION_ROTATION_UPDATE) {
                byte playerId = (byte) packetParams[0];
                byte deltaX = (byte) packetParams[1];
                byte deltaY = (byte) packetParams[2];
                byte deltaZ = (byte) packetParams[3];
                byte newXRot = (byte) packetParams[4];
                byte newYRot = (byte) packetParams[5];
                if (playerId >= 0) {
                    newXRot = (byte) (newXRot + 128);
                    NetworkPlayer networkPlayerInstance = networkManager.players.get(playerId);
                    if (networkPlayerInstance != null) {
                        networkPlayerInstance.queue(deltaX, deltaY,
                                deltaZ, newYRot * 360 / 256F, newXRot * 360 / 256F);
                    }
                } // else: This packet cannot be applied to self, and is ignored if playerId<0

            } else if (packetType == PacketType.ROTATION_UPDATE) {
                byte playerID = (Byte) packetParams[0];
                byte newXRot = (Byte) packetParams[1];
                byte newYRot = (Byte) packetParams[2];
                if (playerID >= 0) {
                    newXRot = (byte) (newXRot + 128);
                    NetworkPlayer networkPlayerInstance = networkManager.players.get(playerID);
                    if (networkPlayerInstance != null) {
                        networkPlayerInstance.queue(newYRot * 360 / 256F, newXRot * 360 / 256F);
                    }
                } // else: This packet cannot be applied to self, and is ignored if playerId<0

            } else if (packetType == PacketType.POSITION_UPDATE) {
                byte playerID = (Byte) packetParams[0];
                NetworkPlayer networkPlayerInstance = networkManager.players.get(playerID);
                if (playerID >= 0 && networkPlayerInstance != null) {
                    networkPlayerInstance.queue((Byte) packetParams[1],
                            (Byte) packetParams[2], (Byte) packetParams[3]);
                } // else: This packet cannot be applied to self, and is ignored if playerId<0

            } else if (packetType == PacketType.DESPAWN_PLAYER) {
                byte playerID = (Byte) packetParams[0];
                NetworkPlayer targetPlayer = networkManager.players.remove(playerID);
                if (playerID >= 0 && targetPlayer != null) {
                    targetPlayer.clear();
                    minecraft.level.removeEntity(targetPlayer);
                } // else: This packet cannot be applied to self, and is ignored if playerId<0

            } else if (packetType == PacketType.CHAT_MESSAGE) {
                byte messageType = (Byte) packetParams[0];
                String message = (String) packetParams[1];
                if (messageType > 0 && serverSupportsMessages) {
                    // MESSAGE_TYPES CPE
                    switch (messageType) {
                        case 1:
                            HUDScreen.ServerName = message;
                            break;
                        case 2:
                            HUDScreen.Compass = message;
                            break;
                        case 3:
                            HUDScreen.UserDetail = message;
                            break;
                        case 11:
                            HUDScreen.BottomRight1 = message;
                            break;
                        case 12:
                            HUDScreen.BottomRight2 = message;
                            break;
                        case 13:
                            HUDScreen.BottomRight3 = message;
                            break;
                        case 100:
                            HUDScreen.AnnouncementTimer = System.currentTimeMillis();
                            HUDScreen.Announcement = message;
                            break;
                        default:
                            // unknown MessageType: stick it into regular chat box
                            minecraft.hud.addChat(message);
                            break;
                    }
                } else if (messageType < 0 && !serverSupportsMessages) {
                    // For compatibility with vanilla Minecraft: negative ID colors a message yellow
                    minecraft.hud.addChat("&e" + message);
                } else {
                    // Regular chat
                    minecraft.hud.addChat(message);
                }

            } else if (packetType == PacketType.DISCONNECT) {
                networkManager.netHandler.close();
                minecraft.setCurrentScreen(new ErrorScreen("Connection lost", (String) packetParams[0]));

            } else if (packetType == PacketType.UPDATE_PLAYER_TYPE) {
                minecraft.player.userType = (byte) packetParams[0];
            }
        }

        if (!networkHandler.connected) {
            return false;
        }

        networkHandler.in.compact();
        return true;
    }
}
