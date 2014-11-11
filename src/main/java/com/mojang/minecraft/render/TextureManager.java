package com.mojang.minecraft.render;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.FontRenderer;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.level.tile.TextureSide;
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
import java.util.Map.Entry;
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
            image = loadImageFast(TextureManager.class.getResourceAsStream(Textures.TERRAIN));
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
        if (settings.smoothing > GameSettings.SMOOTHING_OFF) {
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

        if (settings.smoothing > GameSettings.SMOOTHING_OFF) {
            if (settings.smoothing == GameSettings.SMOOTHING_AUTO) {
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
            } else if (settings.smoothing == GameSettings.SMOOTHING_UNIVERSAL) {
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

    public int load(final String file) {
        Integer val = textures.get(file);
        if (val != null) {
            return (int) val;
        }
        switch (file) {
            case Textures.CLOUDS:
                return loadCustom(file, customClouds);

            case Textures.FONT:
                // Note: FontRenderer needs to be re-created whenever font texture changes.
                return loadCustom(file, customFont);

            case Textures.GUI:
                return loadCustom(file, customGUI);

            case Textures.ICONS:
                return loadCustom(file, customIcons);

            case Textures.LOADING_BACKGROUND:
                return loadCustom(file, customDirtPng);

            case Textures.MAP_EDGE:
                if (customEdgeBlock == null && currentTerrainPng != null) {
                    // This will fill in MAP_EDGE and MAP_SIDE
                    load(Textures.TERRAIN);
                } else {
                    return loadCustom(file, customEdgeBlock);
                }

            case Textures.MAP_SIDE:
                if (customSideBlock == null && currentTerrainPng != null) {
                    // This will fill in MAP_EDGE and MAP_SIDE
                    load(Textures.TERRAIN);
                } else {
                    return loadCustom(file, customSideBlock);
                }

            case Textures.MOB_CHICKEN:
                return loadCustom(file, customChicken);

            case Textures.MOB_CREEPER:
                return loadCustom(file, customCreeper);

            case Textures.MOB_CROC:
                return loadCustom(file, customCrocodile);

            case Textures.MOB_HUMANOID:
                return loadCustom(file, customHumanoid);

            case Textures.MOB_PIG:
                return loadCustom(file, customPig);

            case Textures.MOB_PRINTER:
                return loadCustom(file, customPrinter);

            case Textures.MOB_SHEEP:
                return loadCustom(file, customSheep);

            case Textures.MOB_SKELETON:
                return loadCustom(file, customSkeleton);

            case Textures.MOB_SPIDER:
                return loadCustom(file, customSpider);

            case Textures.MOB_ZOMBIE:
                return loadCustom(file, customZombie);

            case Textures.RAIN:
                return loadCustom(file, customRainPng);

            case Textures.SNOW:
                return loadCustom(file, customSnow);

            case Textures.TERRAIN:
                int id = loadCustom(file, currentTerrainPng);
                try {
                    initAtlas();
                    if (currentTerrainPng != null) {
                        // Disable animations for non-standard texture packs
                        animations.clear();
                    } else {
                        registerAnimations();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException("Failed to load texture atlas!", ex);
                }
                if (currentTerrainPng != null) {
                    // We can use non-standard "terrain.png" to fill in
                    // missing custom "rock.png", "water.png", and "dirt.png" textures
                    // (textures used for map sides, edges, and loading backgrounds)
                    if (customSideBlock == null) {
                        customSideBlock = textureAtlas.get(Block.BEDROCK.textureId);
                        loadCustom(Textures.MAP_SIDE, customSideBlock);
                    }
                    if (customEdgeBlock == null) {
                        customEdgeBlock = textureAtlas.get(Block.WATER.textureId);
                        loadCustom(Textures.MAP_EDGE, customEdgeBlock);
                    }
                    if (customDirtPng == null) {
                        customDirtPng = textureAtlas.get(Block.DIRT.textureId);
                        loadCustom(Textures.LOADING_BACKGROUND, customDirtPng);
                    }
                }
                return id;

            default:
                return loadDefault(file);
        }
    }

    int loadCustom(String file, BufferedImage img) {
        int id;
        if (img != null) {
            id = load(img);
        } else {
            id = loadDefault(file);
        }
        textures.put(file, id);
        return id;
    }

    int loadDefault(String file) {
        try {
            idBuffer.clear();
            GL11.glGenTextures(idBuffer);
            int textureID = idBuffer.get(0);
            if (file.endsWith(".png")) {
                if (file.startsWith("##")) {
                    load(load1(loadImageFast(TextureManager.class.getResourceAsStream(file.substring(2)))), textureID);
                } else {
                    load(loadImageFast(TextureManager.class.getResourceAsStream(file)), textureID);
                }

                textures.put(file, textureID);
            } else {
                throw new RuntimeException("Cannot load texture from " + file + ": unsupported format.");
            }

            return textureID;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load texture", ex);
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

    public void reloadTextures() throws IOException {
        if (settings.minecraft.networkManager != null) {
            for (NetworkPlayer p : settings.minecraft.networkManager.getPlayers()) {
                p.forceTextureReload();
            }
            settings.minecraft.player.forceTextureReload();
        }
        settings.minecraft.fontRenderer = new FontRenderer(settings, this);

        // Force to reload custom side/edge textures from the atlas, while keeping block IDs same.
        load(Textures.TERRAIN);
        setSideBlock(sideBlockId);
        setEdgeBlock(edgeBlockId);
    }

    public void loadTexturePack(final String file) throws IOException {
        if (file.endsWith(".zip")) {
            useDefaultTextures();
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
        reloadTextures();
    }

    public void registerAnimations() {
        animations.clear();
        animations.add(new TextureWaterFX());
        animations.add(new TextureLavaFX());
        animations.add(new TextureFireFX());
    }

    public void unloadTexture(String textureName) {
        if (textures.containsKey(textureName)) {
            //LogUtil.logInfo("Unloaded texture: " + textureName);
            GL11.glDeleteTextures(textures.remove(textureName));
        }
    }

    public void unloadTexture(int textureId) {
        while (textures.values().remove(textureId)) {
        }
        GL11.glDeleteTextures(textureId);
    }

    // Resets all custom textures to their defaults.
    // Frees all previosly-loaded textures (including the font).
    // Does *not* affect block types for map edges/sides.
    // Use resetSideBlock/resetEdgeBlock for that.
    public void useDefaultTextures() {
        currentTerrainPng = null;
        customEdgeBlock = null;
        customSideBlock = null;
        customDirtPng = null;
        customRainPng = null;
        customGUI = null;
        customIcons = null;
        customFont = null;
        customClouds = null;

        unloadTexture(Textures.TERRAIN);
        unloadTexture(Textures.MAP_EDGE);
        unloadTexture(Textures.MAP_SIDE);
        unloadTexture(Textures.LOADING_BACKGROUND);
        unloadTexture(Textures.RAIN);
        unloadTexture(Textures.GUI);
        unloadTexture(Textures.ICONS);
        unloadTexture(Textures.FONT);
        unloadTexture(Textures.CLOUDS);

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

        unloadTexture(Textures.MOB_CHICKEN);
        unloadTexture(Textures.MOB_CREEPER);
        unloadTexture(Textures.MOB_CROC);
        unloadTexture(Textures.MOB_HUMANOID);
        unloadTexture(Textures.MOB_PIG);
        unloadTexture(Textures.MOB_PRINTER);
        unloadTexture(Textures.MOB_SHEEP);
        unloadTexture(Textures.MOB_SKELETON);
        unloadTexture(Textures.MOB_SPIDER);
        unloadTexture(Textures.MOB_ZOMBIE);
    }

    public int getSideBlock() {
        return sideBlockId;
    }

    public void setSideBlock(int blockId) {
        sideBlockId = blockId;
        if (blockId < 0 || blockId > Block.blocks.length) {
            resetSideBlock();
        } else {
            int texId = Block.blocks[blockId].getTextureId(TextureSide.Top);
            unloadTexture(Textures.MAP_SIDE);
            customSideBlock = textureAtlas.get(texId);
        }
    }

    public void resetSideBlock() {
        sideBlockId = -1;
        customSideBlock = null;
        unloadTexture(Textures.MAP_SIDE);
    }

    public int getEdgeBlock() {
        return edgeBlockId;
    }

    public void setEdgeBlock(int blockId) {
        edgeBlockId = blockId;
        if (blockId < 0 || blockId > Block.blocks.length) {
            resetEdgeBlock();
        } else {
            int texId = Block.blocks[blockId].getTextureId(TextureSide.Top);
            unloadTexture(Textures.MAP_EDGE);
            customEdgeBlock = textureAtlas.get(texId);
        }
    }

    public void resetEdgeBlock() {
        edgeBlockId = -1;
        customEdgeBlock = null;
        unloadTexture(Textures.MAP_EDGE);
    }

    public void setTerrainTexture(BufferedImage newImage) {
        currentTerrainPng = newImage;
        unloadTexture(Textures.TERRAIN);
        unloadTexture(Textures.MAP_EDGE);
        unloadTexture(Textures.MAP_SIDE);
        load(Textures.TERRAIN);
        setSideBlock(sideBlockId);
        setEdgeBlock(edgeBlockId);
    }
}
