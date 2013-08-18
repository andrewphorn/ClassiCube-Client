package com.mojang.minecraft.render;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.render.texture.TextureFX;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipFile;

public class TextureManager
{
	public TextureManager(GameSettings settings)
	{
		this.settings = settings;

		minecraftFolder = Minecraft.mcDir;
		texturesFolder = new File(minecraftFolder, "texturepacks");

		if(!texturesFolder.exists())
		{
			texturesFolder.mkdir();
		}
	}

	public HashMap<String, Integer> textures = new HashMap<String, Integer>();
	public HashMap<Integer, BufferedImage> textureImages = new HashMap<Integer, BufferedImage>();
	public IntBuffer idBuffer = BufferUtils.createIntBuffer(1);
	public ByteBuffer textureBuffer = BufferUtils.createByteBuffer(262144);
	public List<TextureFX> animations = new ArrayList<TextureFX>();
	public GameSettings settings;

	public HashMap<String, Integer> externalTexturePacks = new HashMap<String, Integer>();

	public File minecraftFolder;
	public File texturesFolder;

	public int previousMipmapMode;

	public int load(String file)
	{
		if(textures.get(file) != null)
		{
			return textures.get(file);
		} else if(externalTexturePacks.get(file) != null) {
			return externalTexturePacks.get(file);
		} else {
			try {
				idBuffer.clear();

				GL11.glGenTextures(idBuffer);

				int textureID = idBuffer.get(0);

				if(file.endsWith(".png"))
				{
					if(file.startsWith("##"))
					{
						load(load1(ImageIO.read(TextureManager.class.getResourceAsStream(file.substring(2)))), textureID);
					} else {
						load(ImageIO.read(TextureManager.class.getResourceAsStream(file)), textureID);
					}

					textures.put(file, textureID);
				} else if(file.endsWith(".zip")) {
					ZipFile zip = new ZipFile(new File(minecraftFolder, "texturepacks/" + file));

					String terrainPNG = "terrain.png";

					if(zip.getEntry(terrainPNG.startsWith("/") ? terrainPNG.substring(1, terrainPNG.length()) : terrainPNG) != null)
					{
						load(ImageIO.read(zip.getInputStream(zip.getEntry(terrainPNG.startsWith("/") ? terrainPNG.substring(1, terrainPNG.length()) : terrainPNG))), textureID);
					} else {
						load(ImageIO.read(TextureManager.class.getResourceAsStream(terrainPNG)), textureID);
					}

					zip.close();

					externalTexturePacks.put(file, textureID);
				}

				return textureID;
			} catch (IOException e) {
				throw new RuntimeException("!!", e);
			}
		}
	}

	public static BufferedImage load1(BufferedImage image)
	{
		int charWidth = image.getWidth() / 16;
		BufferedImage image1 = new BufferedImage(16, image.getHeight() * charWidth, 2);
		Graphics graphics = image1.getGraphics();

		for(int i = 0; i < charWidth; i++)
		{
			graphics.drawImage(image, -i << 4, i * image.getHeight(), null);
		}

		graphics.dispose();

		return image1;
	}

	public int load(BufferedImage image)
	{
		idBuffer.clear();

		GL11.glGenTextures(idBuffer);

		int textureID = idBuffer.get(0);

		load(image, textureID);

		textureImages.put(textureID, image);

		return textureID;
	}

	public void load(BufferedImage image, int textureID)
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		if(settings.smoothing > 0)
		{
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 4);

			if(settings.anisotropic > 0)
			{
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, 16);
			}
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		}

		//GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);

		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixels = new int[width * height];
		byte[] color = new byte[width * height << 2];

		image.getRGB(0, 0, width, height, pixels, 0, width);

		for(int pixel = 0; pixel < pixels.length; pixel++)
		{
			int alpha = pixels[pixel] >>> 24;
			int red = pixels[pixel] >> 16 & 0xFF;
			int green = pixels[pixel] >> 8 & 0xFF;
			int blue = pixels[pixel] & 0xFF;

			if(settings.anaglyph)
			{
				int rgba3D = (red * 30 + green * 59 + blue * 11) / 100;

				green = (red * 30 + green * 70) / 100;
				blue = (red * 30 + blue * 70) / 100;
				red = rgba3D;
			}

			color[pixel << 2] = (byte)red;
			color[(pixel << 2) + 1] = (byte)green;
			color[(pixel << 2) + 2] = (byte)blue;
			color[(pixel << 2) + 3] = (byte)alpha;
		}

		textureBuffer.clear();
		textureBuffer.put(color);
		textureBuffer.position(0).limit(color.length);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureBuffer);

		if(settings.smoothing > 0)
		{
			if(settings.smoothing == 1)
			{
				ContextCapabilities capabilities = GLContext.getCapabilities();

				if(capabilities.OpenGL30)
				{
					if(previousMipmapMode != settings.smoothing)
					{
						System.out.println("Using OpenGL 3.0 for mipmap generation.");
					}

					GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
				} else if(capabilities.GL_EXT_framebuffer_object) {
					if(previousMipmapMode != settings.smoothing)
					{
						System.out.println("Using GL_EXT_framebuffer_object extension for mipmap generation.");
					}

					EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);
				} else if(capabilities.OpenGL14) {
					if(previousMipmapMode != settings.smoothing)
					{
						System.out.println("Using OpenGL 1.4 for mipmap generation.");
					}

					GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
				}
			} else if(settings.smoothing == 2) {
				if(previousMipmapMode != settings.smoothing)
				{
					System.out.println("Using custom system for mipmap generation.");
				}

				generateMipMaps(textureBuffer, width, height, false);
			}
		}

		previousMipmapMode = settings.smoothing;
	}

	public void generateMipMaps(ByteBuffer data, int width, int height, boolean test)
	{
		ByteBuffer mipData = data;

		for (int level = test ? 0 : 1; level <= 4; level++)
		{
			int parWidth = width >> level - 1;
			int mipWidth = width >> level;
			int mipHeight = height >> level;

			if(mipWidth <= 0 || mipHeight <= 0)
			{
				break;
			}

			ByteBuffer mipData1 = BufferUtils.createByteBuffer(data.capacity());

			mipData1.clear();

			for (int mipX = 0; mipX < mipWidth; mipX++)
			{
				for (int mipY = 0; mipY < mipHeight; mipY++)
				{
					int p1 = mipData.getInt((mipX * 2 + 0 + (mipY * 2 + 0) * parWidth) * 4);
					int p2 = mipData.getInt((mipX * 2 + 1 + (mipY * 2 + 0) * parWidth) * 4);
					int p3 = mipData.getInt((mipX * 2 + 1 + (mipY * 2 + 1) * parWidth) * 4);
					int p4 = mipData.getInt((mipX * 2 + 0 + (mipY * 2 + 1) * parWidth) * 4);

					int pixel = b(b(p1, p2), b(p3, p4));

					mipData1.putInt((mipX + mipY * mipWidth) * 4, pixel);
				}
			}

			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, level, GL11.GL_RGBA, mipWidth, mipHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, mipData1);
			GL11.glAlphaFunc(GL11.GL_GEQUAL, 0.1F * level); // Create transparency for each level.

			mipData = mipData1;
		}
	}

	private int b(int c1, int c2)
	{
		int a1 = (c1 & 0xFF000000) >> 24 & 0xFF;
		int a2 = (c2 & 0xFF000000) >> 24 & 0xFF;

		int ax = (a1 + a2) / 2;
		if (ax > 255) {
			ax = 255;
		}
		if (a1 + a2 <= 0)
		{
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

	public void registerAnimation(TextureFX FX)
	{
		animations.add(FX);

		FX.animate();
	}
}
