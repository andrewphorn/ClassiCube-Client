package com.oyasunadev.mcraft.client.core;

import com.mojang.minecraft.GameSettings;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.MinecraftApplet;
import com.mojang.minecraft.ResourceDownloadThread;
import com.mojang.minecraft.SessionData;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Oliver Yasuna
 * Date: 9/30/12
 * Time: 7:16 PM
 */

/**
 * Run Minecraft Classic standalone version.
 */
public class MinecraftStandalone {
	public static void main(String[] args) {
		MinecraftStandalone minecraftStandalone = new MinecraftStandalone();

		minecraftStandalone.startMinecraft();
	}

	/**
	 * Default constructor.
	 */
	public MinecraftStandalone() {
	}

	/**
	 * Start Minecraft Classic.
	 */
	public void startMinecraft() {
		MinecraftFrame minecraftFrame = new MinecraftFrame();

		minecraftFrame.startMinecraft();
	}

	/**
	 * A class representing the Minecraft Classic game.
	 */
	private class MinecraftFrame extends JFrame {
		/**
		 * Default constructor.
		 */
		public MinecraftFrame() {
			setSize(1020, 510);
			// setResizable(false);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(new BorderLayout());

			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					minecraft.running = false;
				}
			});
		}

		/**
		 * Minecraft reference.
		 */
		private Minecraft minecraft;

		/**
		 * Start Minecraft Classic.
		 */
		public void startMinecraft() {
			boolean RunFakeNetwork = false;
			MCraftApplet applet = new MCraftApplet();
			final MinecraftCanvas canvas = new MinecraftCanvas();
			minecraft = new Minecraft(canvas, applet, getWidth(), getHeight(),
					false, false);

			if (RunFakeNetwork) {
				minecraft.host = "127.0.0.1";
				minecraft.host = minecraft.host + ":" + "25565";
				minecraft.session = new SessionData("Jonty800", "noidea");
				minecraft.session.mppass = "c0dd4746a88c5785952cd0190e8214a6";
				minecraft.session.haspaid = true;
				minecraft.server = "127.0.0.1";
				minecraft.port = 25565;
			}

			canvas.setMinecraft(minecraft);
			canvas.setSize(getSize());

			add(canvas, "Center");

			canvas.setFocusable(true);

			pack();
			setLocation(
					(Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
					(Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
			setVisible(true);

			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (!minecraft.running) {
							minecraft.shutdown();
							dispose();
						}

						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();

			boolean pass = false;

			while (!pass) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (minecraft.running) {
					pass = true;
				}
			}

			// DO SHIT...?
		}

		/**
		 * Override the MinecraftApplet class because we need to fake the
		 * Document Base and Code Base.
		 */
		private class MCraftApplet extends MinecraftApplet {
			/**
			 * Default constructor.
			 */
			public MCraftApplet() {
				parameters = new HashMap<String, String>();
			}

			/**
			 * Fake the Document Base.
			 * 
			 * @return new URL("http://minecraft.net:80/play.jsp")
			 */
			@Override
			public URL getDocumentBase() {
				try {
					return new URL("http://minecraft.net:80/play.jsp");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				return null;
			}

			/**
			 * Fake the Code Base.
			 * 
			 * @return new URL("http://minecraft.net:80/")
			 */
			@Override
			public URL getCodeBase() {
				try {
					return new URL("http://minecraft.net:80/");
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				return null;
			}

			/**
			 * Return our own parameters variable.
			 * 
			 * @param name
			 * @return
			 */
			@Override
			public String getParameter(String name) {
				return parameters.get(name);
			}

			/**
			 * Use our own parameters map.
			 */
			private Map<String, String> parameters;
		}

		/**
		 * A canvas for the Minecraft thread.
		 */
		private class MinecraftCanvas extends Canvas {
			private Image image;
			private Image image2;

			void SetImage() throws IOException {
				setImage(ImageIO.read(getClass().getResourceAsStream(
						"/resources" + "/rsbg.jpg")));

			}

			void SetImage2() throws IOException {
				setImage2(ImageIO.read(getClass().getResourceAsStream(
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
					g.drawImage(getImage(), 0, 0, this.getWidth(),
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
					g.drawImage(getImage2(), 0, 0, this.getWidth(),
							this.getHeight(), null);
				}
			}

			/**
			 * Default constructor.
			 */
			public MinecraftCanvas() {
			}

			/**
			 * Start the thread.
			 */
			@Override
			public synchronized void addNotify() {
				super.addNotify();

				startThread();
			}

			/**
			 * Stop the thread.
			 */
			@Override
			public synchronized void removeNotify() {
				stopThread();

				super.removeNotify();
			}

			private static final long serialVersionUID = 1L;

			/**
			 * The Minecraft variable.
			 */
			private Minecraft minecraft;
			/**
			 * The Minecraft thread.
			 */
			private Thread thread;

			/**
			 * Set the "minecraft" variable.
			 * 
			 * @param minecraft
			 *            The new Minecraft variable.
			 */
			public void setMinecraft(Minecraft minecraft) {
				this.minecraft = minecraft;
			}

			/**
			 * Start the Minecraft client thread.
			 */
			private synchronized void startThread() {
				if (thread == null) {
					thread = new Thread(minecraft, "Client");

					thread.start();
				}
			}

			/**
			 * Stop the Minecraft client.
			 */
			private synchronized void stopThread() {
				if (thread != null) {
					minecraft.running = false;

					try {
						thread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();

						minecraft.shutdown();
					}

					thread = null;
				}
			}

			public Image getImage() {
				return image;
			}

			public void setImage(Image image) {
				this.image = image;
			}

			public Image getImage2() {
				return image2;
			}

			public void setImage2(Image image2) {
				this.image2 = image2;
			}
		}
	}
}
