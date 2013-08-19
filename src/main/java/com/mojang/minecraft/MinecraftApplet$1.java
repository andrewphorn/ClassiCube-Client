package com.mojang.minecraft;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

// MinecraftCanvas
public class MinecraftApplet$1 extends Canvas
{
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		Font font = new Font("Serif", Font.BOLD, 18);
		g2.setFont(font);
		if (!ResourceDownloadThread.Done) {
			g.setColor(new Color(159,121,238));
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			g.setColor(Color.white);
			font = new Font("Purisa", Font.BOLD, 48);
			g2.setFont(font);
			g2.drawString("ClassicCube", 10,
					48);
			font = new Font("Serif", Font.BOLD, 18);
			g2.setFont(font);
				g2.drawString(ResourceDownloadThread.PercentString, 10,
						98);
				g2.drawString(ResourceDownloadThread.StatusString, 10,
						78);
			
		}
		else {
			g.setColor(new Color(159,121,238));
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
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
