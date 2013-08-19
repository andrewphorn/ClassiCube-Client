package com.mojang.minecraft;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;

import javax.imageio.ImageIO;

// MinecraftCanvas
public class MinecraftApplet$1 extends Canvas
{
	private Image image;
	private Image image2;

	void SetImage() throws IOException {
		image = (ImageIO.read(getClass().getResourceAsStream(
				"/resources" + "/rsbg.jpg")));

	}

	void SetImage2() throws IOException {
		image2 = (ImageIO.read(getClass().getResourceAsStream(
				"/resources" + "/bg.jpg")));
	}

	@Override
	public void paint(Graphics g) {
		if (image == null) {
			try {
				SetImage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		Font font = new Font("Serif", Font.BOLD, 18);
		g2.setFont(font);
		if (!ResourceDownloadThread.Done) {
			g.drawImage(image, 0, 0, this.getWidth(),
					this.getHeight(), null);
			g.setColor(Color.white);
			font = new Font("Purisa", Font.BOLD, 48);
			g2.setFont(font);
			g2.drawString("ClassiCube", 10, 48);
			font = new Font("Serif", Font.BOLD, 18);
			g2.setFont(font);
			g2.drawString(GameSettings.PercentString, 10, 98);
			g2.drawString(GameSettings.StatusString, 10, 78);

		} else {
			if (image2 == null) {
				try {
					SetImage2();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			g.drawImage(image2, 0, 0, this.getWidth(),
					this.getHeight(), null);
		}
	}
	public MinecraftApplet$1(MinecraftApplet minecraftApplet)
	{
		this.applet = minecraftApplet;
	}

	@Override
	public synchronized void addNotify()
	{
		super.addNotify();

		applet.startGameThread();
	}

	@Override
	public synchronized void removeNotify()
	{
		applet.stopGameThread();

		super.removeNotify();
	}

	private static final long serialVersionUID = 1L;

	private MinecraftApplet applet;
}
