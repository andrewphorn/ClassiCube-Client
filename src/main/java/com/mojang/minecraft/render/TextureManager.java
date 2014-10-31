package com.mojang.minecraft.render;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.net.NetworkPlayer;
import com.mojang.minecraft.render.texture.TextureFX;
import com.mojang.minecraft.render.texture.TextureFireFX;
import com.mojang.minecraft.render.texture.TextureLavaFX;
import com.mojang.minecraft.render.texture.TextureWaterFX;
import com.mojang.minecraft.render.texture.Textures;
import com.mojang.util.LogUtil;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

public class TextureManager {

    public boolean applet;
    private final HashMap<String, Integer> textures = new HashMap<>();
    public HashMap<Integer, BufferedImage> textureImages = new HashMap<>();
    public IntBuffer idBuffer = BufferUtils.createIntBuffer(1);
    public ByteBuffer textureBuffer = BufferUtils.createByteBuffer(262144);
    public List<TextureFX> animations = new ArrayList<>();
    public GameSettings settings;

    // Stores block IDs of side/edge blocks. "-1" means "use default".
    private int sideBlockId = -1;
    private int edgeBlockId = -1;

    // If a corresponding *BlockId field is set to non-default value, these fields will
    // store a bitmap from the texture atlas corresponding to that block's texture.
    // These fields need to be updated (by calling setSideBlock/setEdgeBlock) when
    // texture pack is changed.
    public BufferedImage customSideBlock = null;
    public BufferedImage customEdgeBlock = null;

    public List<BufferedImage> textureAtlas = new ArrayList<>();
    public BufferedImage currentTerrainPng = null;
    public BufferedImage customDirtPng = null;
    public BufferedImage customRainPng = null;
    public BufferedImage customGUI = null;
    public BufferedImage customIcons = null;
    public BufferedImage customFont = null;
    public BufferedImage customClouds = null;
    public BufferedImage customSnow = null;
    public BufferedImage customChicken = null;
    public BufferedImage customCreeper = null;
    public BufferedImage customCrocodile = null;
    public BufferedImage customHumanoid = null;
    public BufferedImage customPig = null;
    public BufferedImage customPrinter = null;
    public BufferedImage customSheep = null;
    public BufferedImage customSkeleton = null;
    public BufferedImage customSpider = null;
    public BufferedImage customZombie = null;
    public File minecraftFolder;
    public File texturesFolder;
    public int previousMipmapMode;

    public TextureManager(GameSettings settings, boolean Applet) {
        this.applet = Applet;
        this.settings = settings;

        minecraftFolder = Minecraft.mcDir;
        texturesFolder = new File(minecraftFolder, "texturepacks");

        if (!texturesFolder.exists()) {
            texturesFolder.mkdir();
        }
        ImageIO.setUseCache(false);
    }

    public static BufferedImage crop(BufferedImage src, int width, int height, int x, int y)
            throws IOException {

        // LogUtil.logInfo("---" + src.getWidth() + " - " + src.getHeight() +
        // " - " + x + " - " + y);
        BufferedImage clipping = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);// src.getType());
        Graphics2D area = (Graphics2D) clipping.getGraphics().create();
        area.drawImage(src, 0, 0, clipping.getWidth(), clipping.getHeight(), x, y,
                x + clipping.getWidth(), y + clipping.getHeight(), null);
        area.dispose();

        return clipping;
    }

    public static BufferedImage load1(BufferedImage image) {
        int charWidth = image.getWidth() / 16;
        BufferedImage image1 = new BufferedImage(16, image.getHeight() * charWidth,
                BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = image1.getGraphics();

        for (int i = 0; i < charWidth; i++) {
            graphics.drawImage(image, -i << 4, i * image.getHeight(), null);
        }

        graphics.dispose();

        return image1;
    }

    public static int getMaxAnisotropySetting() {
        float maxLevel = GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
        return (int) Math.round(Math.log(maxLevel) / Math.log(2));
    }

    public List<BufferedImage> Atlas2dInto1d(BufferedImage atlas2d, int tiles, int atlasSizeLimit) {
        int tileSize = atlas2d.getWidth() / tiles;

        int atlasesCount = Math.max(1, tiles * tiles * tileSize / atlasSizeLimit);
        List<BufferedImage> atlases = new ArrayList<>();

        // 256 x 1
        BufferedImage atlas1d = null;

        for (int i = 0; i < tiles * tiles; i++) {
            int x = i % tiles;
            int y = i / tiles;
            int tilesInAtlas = tiles * tiles / atlasesCount;
            if (i % tilesInAtlas == 0) {
                if (atlas1d != null) {
                    atlases.add(atlas1d);
                }
                atlas1d = new BufferedImage(tileSize, atlasSizeLimit, BufferedImage.TYPE_INT_ARGB);
            }
            try {
                atlas1d = crop(atlas2d, tileSize, tileSize, x * tileSize, y * tileSize);
            } catch (IOException ex) {
                LogUtil.logWarning("Error extracting texture from an atlas.", ex);
            }
        }
        atlases.add(atlas1d);
        return atlases;
    }

    private int blend(int c1, int c2) {
        int a1 = (c1 & 0xFF000000) >> 24 & 0xFF;
        int a2 = (c2 & 0xFF000000) >> 24 & 0xFF;

        int ax = (a1 + a2) / 2;
        if (ax > 255) {
            ax = 255;
        }
        if (a1 + a2 <= 0) {
            a1 = 1;
            a2 = 1;
            ax = 0;
        }

        int r1 = (c1 >> 16 & 0xFF) * a1;
        int g1 = (c1 >> 8 & 0xFF) * a1;
        int b1 = (c1 & 0xFF) * a1;

        int r2 = (c2 >> 16 & 0xFF) * a2;
        int g2 = (c2 >> 8 & 0xFF) * a2;
        int b2 = (c2 & 0xFF) * a2;

        int rx = (r1 + r2) / (a1 + a2);
        int gx = (g1 + g2) / (a1 + a2);
        int bx = (b1 + b2) / (a1 + a2);

        return ax << 24 | rx << 16 | gx << 8 | bx;
    }

    public void generateMipMaps(ByteBuffer data, int width, int height, boolean test) {
        ByteBuffer mipData = data;

        for (int level = test ? 0 : 1; level <= 4; level++) {
            int parWidth = width >> level - 1;
            int mipWidth = width >> level;
            int mipHeight = height >> level;

            if (mipWidth <= 0 || mipHeight <= 0) {
                break;
            }

            ByteBuffer mipData1 = BufferUtils.createByteBuffer(data.capacity());

            mipData1.clear();

            for (int mipX = 0; mipX < mipWidth; mipX++) {
                for (int mipY = 0; mipY < mipHeight; mipY++) {
                    int p1 = mipData.getInt((mipX * 2 + 0 + (mipY * 2 + 0) * parWidth) * 4);
                    int p2 = mipData.getInt((mipX * 2 + 1 + (mipY * 2 + 0) * parWidth) * 4);
                    int p3 = mipData.getInt((mipX * 2 + 1 + (mipY * 2 + 1) * parWidth) * 4);
                    int p4 = mipData.getInt((mipX * 2 + 0 + (mipY * 2 + 1) * parWidth) * 4);

                    int pixel = blend(blend(p1, p2), blend(p3, p4));

                    mipData1.putInt((mipX + mipY * mipWidth) * 4, pixel);
                }
            }

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, level, GL11.GL_RGBA, mipWidth, mipHeight, 0,
                    GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, mipData1);
            // GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.1F * level); // Create transparency for each level.
            mipData = mipData1;
        }
    }

    public void initAtlas() throws IOException {
        BufferedImage image;
        if (currentTerrainPng != null) {
            image = currentTerrainPng;
        } else {
            image = loadImageFast(TextureManager.class.getResourceAsStream("/terrain.png"));
        }
        textureAtlas.clear();
        textureAtlas = Atlas2dInto1d(image, 16, image.getWidth() / 16);
    }

    public int load(BufferedImage image) {
        idBuffer.clear();
        GL11.glGenTextures(idBuffer);
        int textureID = idBuffer.get(0);
        load(image, textureID);
        textureImages.put(textureID, image);
        return textureID;
    }

    public void load(BufferedImage image, int textureID) {
        if (image == null) {
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        if (settings.smoothing > 0) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                    GL11.GL_NEAREST_MIPMAP_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, GL11.GL_POINTS);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, GL11.GL_TRIANGLES);
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        }

        // GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        int[] pixels = new int[width * height];
        byte[] color = new byte[width * height << 2];

        image.getRGB(0, 0, width, height, pixels, 0, width);

        for (int pixel = 0; pixel < pixels.length; pixel++) {
            int alpha = pixels[pixel] >>> 24;
            int red = pixels[pixel] >> 16 & 0xFF;
            int green = pixels[pixel] >> 8 & 0xFF;
            int blue = pixels[pixel] & 0xFF;

            int i = pixel << 2;
            color[i] = (byte) red;
            color[i + 1] = (byte) green;
            color[i + 2] = (byte) blue;
            color[i + 3] = (byte) alpha;
        }

        if (textureBuffer.capacity() != color.length) {
            textureBuffer = BufferUtils.createByteBuffer(color.length);
        } else {
            textureBuffer.clear();
        }
        textureBuffer.put(color);
        textureBuffer.position(0).limit(color.length);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE, textureBuffer);

        if (settings.smoothing > 0) {
            if (settings.smoothing == 1) {
                ContextCapabilities capabilities = GLContext.getCapabilities();
                if (capabilities.OpenGL30) {
                    if (previousMipmapMode != settings.smoothing) {
                        LogUtil.logInfo("Using OpenGL 3.0 for mipmap generation.");
                    }

                    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
                } else if (capabilities.GL_EXT_framebuffer_object) {
                    if (previousMipmapMode != settings.smoothing) {
                        LogUtil.logInfo("Using GL_EXT_framebuffer_object extension for mipmap generation.");
                    }

                    EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);
                } else if (capabilities.OpenGL14) {
                    if (previousMipmapMode != settings.smoothing) {
                        LogUtil.logInfo("Using OpenGL 1.4 for mipmap generation.");
                    }

                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
                }
            } else if (settings.smoothing == 2) {
                if (previousMipmapMode != settings.smoothing) {
                    LogUtil.logInfo("Using custom system for mipmap generation.");
                }

                generateMipMaps(textureBuffer, width, height, false);
            }
            if (settings.anisotropy > 0) {
                float desiredLevel = 1 << settings.anisotropy;
                float maxLevel = GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
                float actualLevel = Math.min(desiredLevel, maxLevel);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, actualLevel);
            }
        }

        previousMipmapMode = settings.smoothing;
    }

    public int load(String file) {
        if (this.currentTerrainPng == null && animations.isEmpty()) {
            registerAnimations();
        }
        if (file.startsWith("/dirt") && textures.containsKey("customDirt")) {
            return textures.get("customDirt");
        }

        if (file.startsWith("/mob")) {
            String mobName = file.replace("/mob/", "").replace(".png", "").trim();
            if (textures.containsKey("custom" + mobName)) {
                return textures.get("custom" + mobName);
            } else if (!textures.containsKey("custom" + mobName)) {
                if (mobName.equalsIgnoreCase("creeper") && customCreeper != null) {
                    int id = load(customCreeper);
                    textures.put("custom" + mobName, id);
                    return id;
                } else if (mobName.equalsIgnoreCase("zombie") && customZombie != null) {
                    int id = load(customZombie);
                    textures.put("custom" + mobName, id);
                    return id;
                } else if (mobName.equalsIgnoreCase("sheep") && customSheep != null) {
                    int id = load(customSheep);
                    textures.put("custom" + mobName, id);
                    return id;
                } else if (mobName.equalsIgnoreCase("skeleton") && customSkeleton != null) {
                    int id = load(customSkeleton);
                    textures.put("custom" + mobName, id);
                    return id;
                } else if (mobName.equalsIgnoreCase("spider") && customSpider != null) {
                    int id = load(customSpider);
                    textures.put("custom" + mobName, id);
                    return id;
                } else if (mobName.equalsIgnoreCase("printer") && customPrinter != null) {
                    int id = load(customPrinter);
                    textures.put("custom" + mobName, id);
                    return id;
                } else if (mobName.equalsIgnoreCase("pig") && customPig != null) {
                    int id = load(customPig);
                    textures.put("custom" + mobName, id);
                    return id;
                } else if (mobName.equalsIgnoreCase("chicken") && customChicken != null) {
                    int id = load(customChicken);
                    textures.put("custom" + mobName, id);
                    return id;
                } else if (mobName.equalsIgnoreCase("croc") && customCrocodile != null) {
                    int id = load(customCrocodile);
                    textures.put("custom" + mobName, id);
                    return id;
                }
            }
        }
        if (file.startsWith("/clouds") && textures.containsKey("customClouds")) {
            return textures.get("customClouds");
        }
        if (file.startsWith("/clouds") && !textures.containsKey("customClouds")
                && customClouds != null) {
            int id = load(customClouds);
            textures.put("customClouds", id);
            return id;
        }

        if (file.startsWith("/snow") && textures.containsKey("customSnow")) {
            return textures.get("customSnow");
        }
        if (file.startsWith("/snow") && !textures.containsKey("customSnow") && customSnow != null) {
            int id = load(customSnow);
            textures.put("customSnow", id);
            return id;
        }

        if (file.startsWith("/char") && textures.containsKey("customHumanoid")) {
            return textures.get("customHumanoid");
        }
        if (file.startsWith("/char") && !textures.containsKey("customHumanoid")
                && customHumanoid != null) {
            int id = load(customHumanoid);
            textures.put("customHumanoid", id);
            return id;
        }

        if (file.startsWith("/gui/gui") && textures.containsKey("customGUI")) {
            return textures.get("customGUI");
        }
        if (file.startsWith("/gui/gui") && !textures.containsKey("customGUI") && customGUI != null) {
            int id = load(customGUI);
            textures.put("customGUI", id);
            return id;
        }

        if (file.startsWith("/gui/icons") && textures.containsKey("customIcons")) {
            return textures.get("customIcons");
        }
        if (file.startsWith("/gui/icons") && !textures.containsKey("customIcons")
                && customIcons != null) {
            int id = load(customIcons);
            textures.put("customIcons", id);
            return id;
        }

        if (file.startsWith("/default") && textures.containsKey("customFont")) {
            return textures.get("customGUI");
        }
        if (file.startsWith("/default") && !textures.containsKey("customFont")
                && customFont != null) {
            int id = load(customFont);
            textures.put("customFont", id);
            return id;
        }

        if (file.startsWith("/rain") && textures.containsKey("customRain")) {
            return textures.get("customRain");
        }
        if (file.startsWith("/rain") && !textures.containsKey("customRain")
                && customRainPng != null) {
            int id = load(customRainPng);
            textures.put("customRain", id);
            return id;
        }

        if (file.startsWith("/terrain") && textures.containsKey("customTerrain")) {
            return textures.get("customTerrain");
        }
        if (file.startsWith("/terrain") && !textures.containsKey("customTerrain")
                && currentTerrainPng != null) {
            int id = load(currentTerrainPng);
            textures.put("customTerrain", id);
            if (customSideBlock == null) {
                customSideBlock = textureAtlas.get(Block.BEDROCK.textureId);
                textures.put("customSide", load(customSideBlock));
            }
            if (customEdgeBlock == null) {
                customEdgeBlock = textureAtlas.get(Block.WATER.textureId);
                textures.put("customEdge", load(customEdgeBlock));
            }
            if (customDirtPng == null) {
                customDirtPng = textureAtlas.get(Block.DIRT.textureId);
                textures.put("customDirt", load(customDirtPng));
            }
            return id;
        }

        if (file.startsWith("/dirt") && textures.containsKey("customDirt")) {
            return textures.get("customDirt");
        }

        if (Textures.MAP_SIDE.equals(file) && customSideBlock != null) {
            if (!textures.containsKey("customSide")) {
                int id = load(customSideBlock);
                textures.put("customSide", id);
                return id;
            } else {
                return textures.get("customSide");
            }
        }

        if (Textures.MAP_EDGE.equals(file) && customEdgeBlock != null) {
            if (!textures.containsKey("customEdge")) {
                int id = load(customEdgeBlock);
                textures.put("customEdge", id);
                return id;
            } else {
                return textures.get("customEdge");
            }
        }

        if (textures.get(file) != null) {
            return textures.get(file);
        } else {
            try {
                idBuffer.clear();
                GL11.glGenTextures(idBuffer);
                int textureID = idBuffer.get(0);
                if (file.endsWith(".png")) {
                    if (file.startsWith("##")) {
                        load(load1(loadImageFast(TextureManager.class.getResourceAsStream(file
                                .substring(2)))), textureID);
                    } else {
                        load(loadImageFast(TextureManager.class.getResourceAsStream(file)),
                                textureID);
                    }

                    textures.put(file, textureID);
                } else if (file.endsWith(".zip")) {
                    try (ZipFile zip = new ZipFile(
                            new File(minecraftFolder, "texturepacks/" + file))) {
                        String terrainPNG = "terrain.png";
                        if (zip.getEntry(terrainPNG) != null) {
                            try (InputStream is = zip.getInputStream(zip.getEntry(terrainPNG))) {
                                load(loadImageFast(is), textureID);
                            }
                        } else {
                            load(loadImageFast(TextureManager.class.getResourceAsStream("/"
                                    + terrainPNG)), textureID);
                        }
                    }
                }

                return textureID;
            } catch (IOException ex) {
                throw new RuntimeException("Failed to load texture", ex);
            }
        }
    }

    public BufferedImage loadImageFast(InputStream inputStream) throws IOException {
        return ImageIO.read(inputStream);
    }

    private BufferedImage loadImageFromZip(ZipFile zip, String fileName) throws IOException {
        String properName = fileName.startsWith("/") ? fileName.substring(1, fileName.length()) : fileName;
        ZipEntry entry = zip.getEntry(properName);
        if (entry != null) {
            return loadImageFast(zip.getInputStream(entry));
        } else {
            return null;
        }
    }

    public void loadTexturePack(final String file) throws IOException {
        if (file.endsWith(".zip")) {
            resetCustomTextures();
            try (ZipFile zip = new ZipFile(new File(minecraftFolder, "texturepacks/" + file))) {
                currentTerrainPng = loadImageFromZip(zip, "terrain.png");
                customRainPng = loadImageFromZip(zip, "rain.png");
                customGUI = loadImageFromZip(zip, "gui.png");
                customIcons = loadImageFromZip(zip, "icons.png");
                customFont = loadImageFromZip(zip, "default.png");
                customSnow = loadImageFromZip(zip, "snow.png");
                customChicken = loadImageFromZip(zip, "chicken.png");
                customCreeper = loadImageFromZip(zip, "creeper.png");
                customCrocodile = loadImageFromZip(zip, "croc.png");
                customHumanoid = loadImageFromZip(zip, "char.png");
                customPig = loadImageFromZip(zip, "pig.png");
                customPrinter = loadImageFromZip(zip, "printer.png");
                customSheep = loadImageFromZip(zip, "sheep.png");
                customSkeleton = loadImageFromZip(zip, "skeleton.png");
                customSpider = loadImageFromZip(zip, "spider.png");
                customZombie = loadImageFromZip(zip, "zombie.png");
                customClouds = loadImageFromZip(zip, "clouds.png");
            }
        }
        initAtlas();
        if (settings.minecraft.networkManager != null) {
            for (NetworkPlayer p : settings.minecraft.networkManager.getPlayers()) {
                p.bindTexture(this);
            }
            settings.minecraft.player.bindTexture(this);
        }
        animations.clear();

        // Force to reload custom side/edge textures from the atlas, while keeping block IDs same.
        setSideBlock(sideBlockId);
        setEdgeBlock(edgeBlockId);
    }

    public void registerAnimations() {
        animations.clear();
        animations.add(new TextureWaterFX());
        animations.add(new TextureLavaFX());
        animations.add(new TextureFireFX());
    }

    public void forceTextureReload() {
        for (int id : textures.values()) {
            GL11.glDeleteTextures(id);
        }
        LogUtil.logInfo("Reloaded all " + textures.size() + " textures.");
        textures.clear();
    }

    public void forceTextureReload(String textureName) {
        if (textures.containsKey(textureName)) {
            LogUtil.logInfo("Reloaded texture: " + textureName);
            GL11.glDeleteTextures(textures.remove(textureName));
        }
    }

    // Resets all custom textures to their defaults.
    // Frees all previosly-loaded textures (including the font).
    // Does *not* affect block types for map edges/sides.
    // Use resetSideBlock/resetEdgeBlock for that.
    public void resetCustomTextures() {
        forceTextureReload();

        currentTerrainPng = null;
        customEdgeBlock = null;
        customSideBlock = null;
        customDirtPng = null;
        customRainPng = null;
        customGUI = null;
        customIcons = null;
        customFont = null;
        customClouds = null;

        customChicken = null;
        customCreeper = null;
        customCrocodile = null;
        customHumanoid = null;
        customPig = null;
        customPrinter = null;
        customSheep = null;
        customSkeleton = null;
        customSpider = null;
        customZombie = null;
    }

    public int getSideBlock() {
        return sideBlockId;
    }

    public void setSideBlock(int blockId) {
        sideBlockId = blockId;
        if (blockId < 0 || blockId > Block.blocks.length) {
            resetSideBlock();
        } else {
            int texId = Block.blocks[blockId].textureId;
            forceTextureReload("customSide");
            customSideBlock = textureAtlas.get(texId);
        }
    }

    public void resetSideBlock() {
        sideBlockId = -1;
        customSideBlock = null;
        forceTextureReload("customSide");
    }

    public int getEdgeBlock() {
        return edgeBlockId;
    }

    public void setEdgeBlock(int blockId) {
        edgeBlockId = blockId;
        if (blockId < 0 || blockId > Block.blocks.length) {
            resetEdgeBlock();
        } else {
            int texId = Block.blocks[blockId].textureId;
            forceTextureReload("customEdge");
            customEdgeBlock = textureAtlas.get(texId);
        }
    }

    public void resetEdgeBlock() {
        edgeBlockId = -1;
        customEdgeBlock = null;
        forceTextureReload("customEdge");
    }
}
