package com.mojang.minecraft.net;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.gui.FontRenderer;
import com.mojang.minecraft.mob.HumanoidMob;
import com.mojang.minecraft.render.TextureManager;

public class NetworkPlayer extends HumanoidMob {

	public static final long serialVersionUID = 77479605454997290L;

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public transient List<PositionUpdate> moveQueue = new LinkedList<PositionUpdate>();
	private transient Minecraft minecraft;
	private int xp;
	private int yp;
	private int zp;
	private transient int a = -1;
	public transient BufferedImage newTexture = null;
	public String name;
	public String displayName;
	int tickCount = 0;
	private transient TextureManager textures;

	public String SkinName = null;

	public NetworkPlayer(Minecraft var1, String var3, int var4, int var5, int var6, float var7,
			float var8) {
		super(var1.level, var4, var5, var6);
		minecraft = var1;
		displayName = var3;
		var3 = FontRenderer.stripColor(var3);
		name = var3;
		xp = var4;
		yp = var5;
		zp = var6;
		heightOffset = 0.0F;
		pushthrough = 0.8F;
		this.setPos(var4 / 32.0F, var5 / 32.0F, var6 / 32.0F);
		xRot = var8;
		yRot = var7;
		armor = helmet = false;
		renderOffset = 0.6875F;
		allowAlpha = false;
		if (name.equalsIgnoreCase("Jonty800") || name.equalsIgnoreCase("Jonty800+")
				|| name.equalsIgnoreCase("Jonty800@")) {
			modelName = "sheep";
		}
		if (modelName.equals("humanoid")) {
			downloadSkin();
		} else if (isInteger(modelName)) {
			GL11.glBindTexture(3553, var1.textureManager.load("/terrain.png"));
		}
	}

	@Override
	public void aiStep() {
		int var1 = 5;
		if (moveQueue != null) {
			do {

				if (moveQueue.size() > 0) {
					this.setPos(moveQueue.remove(0));
				}
			} while (var1-- > 0 && moveQueue.size() > 10);
		}

		onGround = true;
	}

	@Override
	public void bindTexture(TextureManager var1) {
		textures = var1;
		if (newTexture != null) {
			BufferedImage var2 = newTexture;
			int[] var3 = new int[512];
			var2.getRGB(32, 0, 32, 16, var3, 0, 32);
			int var5 = 0;

			boolean var10001;
			while (true) {
				if (var5 >= var3.length) {
					var10001 = false;
					break;
				}

				if (var3[var5] >>> 24 < 128) {
					var10001 = true;
					break;
				}

				++var5;
			}

			hasHair = var10001;
			if (modelName.equals("humanoid")) {
				a = var1.load(newTexture);
			}
			newTexture = null;
		}
		if (isInteger(modelName)) {
			GL11.glBindTexture(3553, var1.load("/terrain.png"));
			return;
		} else if (!modelName.startsWith("humanoid")) {
			GL11.glBindTexture(3553, var1.load("/mob/" + modelName.replace('.', '_') + ".png"));
			return;
		}
		if (a < 0) {
			GL11.glBindTexture(3553, var1.load("/char.png"));
		} else {
			GL11.glBindTexture(3553, a);
		}
	}

	public void clear() {
		if (a >= 0 && textures != null) {
			TextureManager var10000 = textures;
			int var1 = a;
			TextureManager var2 = textures;
			var10000.textureImages.remove(Integer.valueOf(var1));
			var2.idBuffer.clear();
			var2.idBuffer.put(var1);
			var2.idBuffer.flip();
			GL11.glDeleteTextures(var2.idBuffer);
		}

	}

	public void downloadSkin() {
		new SkinDownloadThread(this, minecraft.skinServer).start();
	}

	public void queue(byte var1, byte var2, byte var3) {
		moveQueue.add(new PositionUpdate((xp + var1 / 2.0F) / 32.0F, (yp + var2 / 2.0F) / 32.0F,
				(zp + var3 / 2.0F) / 32.0F));
		xp += var1;
		yp += var2;
		zp += var3;
		moveQueue.add(new PositionUpdate(xp / 32.0F, yp / 32.0F, zp / 32.0F));
	}

	public void queue(byte var1, byte var2, byte var3, float var4, float var5) {
		float var6 = var4 - yRot;

		float var7;
		for (var7 = var5 - xRot; var6 >= 180.0F; var6 -= 360.0F) {
			;
		}

		while (var6 < -180.0F) {
			var6 += 360.0F;
		}

		while (var7 >= 180.0F) {
			var7 -= 360.0F;
		}

		while (var7 < -180.0F) {
			var7 += 360.0F;
		}

		var6 = yRot + var6 * 0.5F;
		var7 = xRot + var7 * 0.5F;
		moveQueue.add(new PositionUpdate((xp + var1 / 2.0F) / 32.0F, (yp + var2 / 2.0F) / 32.0F,
				(zp + var3 / 2.0F) / 32.0F, var6, var7));
		xp += var1;
		yp += var2;
		zp += var3;
		moveQueue.add(new PositionUpdate(xp / 32.0F, yp / 32.0F, zp / 32.0F, var4, var5));
	}

	public void queue(float var1, float var2) {
		float var3 = var1 - yRot;

		float var4;
		for (var4 = var2 - xRot; var3 >= 180.0F; var3 -= 360.0F) {
			;
		}

		while (var3 < -180.0F) {
			var3 += 360.0F;
		}

		while (var4 >= 180.0F) {
			var4 -= 360.0F;
		}

		while (var4 < -180.0F) {
			var4 += 360.0F;
		}

		var3 = yRot + var3 * 0.5F;
		var4 = xRot + var4 * 0.5F;
		moveQueue.add(new PositionUpdate(var3, var4));
		moveQueue.add(new PositionUpdate(var1, var2));
	}

	@Override
	public void renderHover(TextureManager var1, float var2) {
		FontRenderer var3 = minecraft.fontRenderer;
		GL11.glPushMatrix();
		GL11.glTranslatef(xo + (x - xo) * var2, yo + (y - yo) * var2 + 0.8F + renderOffset, zo
				+ (z - zo) * var2);
		GL11.glRotatef(-minecraft.player.yRot, 0.0F, 1.0F, 0.0F);
		var2 = 0.05F;
		GL11.glScalef(0.05F, -var2, var2);
		GL11.glTranslatef(-var3.getWidth(displayName) / 2.0F, 0.0F, 0.0F);
		GL11.glNormal3f(1.0F, -1.0F, 1.0F);
		GL11.glDisable(2896);
		GL11.glDisable(16384);

		if (name.equalsIgnoreCase("Notch")) {
			var3.renderNoShadow(displayName, 0, 0, 16776960);
		} else {
			var3.renderNoShadow(displayName, 0, 0, 16777215);
		}

		GL11.glDepthFunc(516);
		GL11.glDepthMask(false);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		var3.renderNoShadow(displayName, 0, 0, 16777215);
		GL11.glDisable(3042);
		GL11.glDepthMask(true);
		GL11.glDepthFunc(515);
		GL11.glTranslatef(1.0F, 1.0F, -0.05F);
		var3.renderNoShadow(name, 0, 0, 5263440);
		GL11.glEnable(16384);
		GL11.glEnable(2896);
		GL11.glPopMatrix();
	}

	public void teleport(short var1, short var2, short var3, float var4, float var5) {
		float var6 = var4 - yRot;

		float var7;
		for (var7 = var5 - xRot; var6 >= 180.0F; var6 -= 360.0F) {
			;
		}

		while (var6 < -180.0F) {
			var6 += 360.0F;
		}

		while (var7 >= 180.0F) {
			var7 -= 360.0F;
		}

		while (var7 < -180.0F) {
			var7 += 360.0F;
		}

		var6 = yRot + var6 * 0.5F;
		var7 = xRot + var7 * 0.5F;
		moveQueue.add(new PositionUpdate((xp + var1) / 64.0F, (yp + var2) / 64.0F,
				(zp + var3) / 64.0F, var6, var7));
		xp = var1;
		yp = var2;
		zp = var3;
		moveQueue.add(new PositionUpdate(xp / 32.0F, yp / 32.0F, zp / 32.0F, var4, var5));
	}
}
