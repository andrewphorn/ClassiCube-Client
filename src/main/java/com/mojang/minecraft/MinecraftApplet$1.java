package com.mojang.minecraft;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.imageio.ImageIO;

// MinecraftCanvas
public class MinecraftApplet$1 extends Canvas {
	private BufferedImage image;
	private BufferedImage image2;

	void SetImage() throws IOException {
		image = ImageIO.read(getClass().getResourceAsStream("/resources" + "/rsbg.jpg"));

	}

	void SetImage2() throws IOException {
		image2 = ImageIO.read(getClass().getResourceAsStream("/resources" + "/bg.jpg"));
	}

	public void download(String address, String localFileName) {
        OutputStream out = null;
        URLConnection connection = null;
        InputStream in = null;

        try {
            URL url = new URL(address);
            out = new BufferedOutputStream(new FileOutputStream(localFileName));
            connection = url.openConnection();
            //I HAVE to send this or the server responds with 403
            connection.setRequestProperty("Content-Type", 
                       "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Language", "en-US"); 
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            in = connection.getInputStream();
            byte[] buffer = new byte[1024];

            int numRead;
            long numWritten = 0;

            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
                numWritten += numRead;
            }

            System.out.println(localFileName + "\t" + numWritten);
        } 
        catch (Exception exception) { 
            exception.printStackTrace();
        } 
        finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } 
            catch (IOException ioe) {
            }
        }
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
			g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
			font = new Font("Purisa", Font.BOLD, 48);
			g2.setFont(font);
			g.setColor(Color.black);
			g2.drawString("ClassiCube", 12, 50); // shadow
			g.setColor(Color.white);
			g2.drawString("ClassiCube", 10, 48); // normal
			font = new Font("Serif", Font.BOLD, 18);
			g2.setFont(font);
			g.setColor(Color.black);
			g2.drawString(GameSettings.PercentString, 12, 100); // shadow
			g2.drawString(GameSettings.StatusString, 12, 80);
			g.setColor(Color.white);
			g2.drawString(GameSettings.PercentString, 10, 98); // normal
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
			g.drawImage(image2, 0, 0, this.getWidth(), this.getHeight(), null);
		}
	}

	public MinecraftApplet$1(MinecraftApplet minecraftApplet) {
		this.applet = minecraftApplet;
	}

	@Override
	public synchronized void addNotify() {
		super.addNotify();

		applet.startGameThread();
	}

	@Override
	public synchronized void removeNotify() {
		applet.stopGameThread();

		super.removeNotify();
	}

	private static final long serialVersionUID = 1L;

	private MinecraftApplet applet;
}
