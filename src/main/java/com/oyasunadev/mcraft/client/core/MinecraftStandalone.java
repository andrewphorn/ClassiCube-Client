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
	String Player = null;
	String Server = null;
	int Port = 0;
	String Mppass = null;
	if (args != null) {
	    if (args.length > 3) {
		Server = args[0];
		Port = Integer.parseInt(args[1]);
		Player = args[2];
		Mppass = args[3];
	    }
	}
	MinecraftStandalone minecraftStandalone = new MinecraftStandalone();
	if (Player == null || Server == null || Mppass == null || Port <= 0) {
	    minecraftStandalone.startMinecraft(null, null, null, 0);
	} else {
	    minecraftStandalone.startMinecraft(Player, Server, Mppass, Port);
	}
    }

    public void startMinecraft(String Player, String Server, String Mppass,
	    int Port) {
	MinecraftFrame minecraftFrame = new MinecraftFrame();

	minecraftFrame.startMinecraft(Player, Server, Mppass, Port);
    }

    public void startMinecraft() {
	MinecraftFrame minecraftFrame = new MinecraftFrame();

	minecraftFrame.startMinecraft(null, null, null, 0);
    }

    /**
     * Default constructor.
     */
    public MinecraftStandalone() {

    }

    /**
     * A class representing the Minecraft Classic game.
     */
    private class MinecraftFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public MinecraftFrame() {
	    setSize(1024, 512);
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
	public void startMinecraft(String Player, String Server, String Mppass,
		int Port) {

	    MCraftApplet applet = new MCraftApplet();
	    final MinecraftCanvas canvas = new MinecraftCanvas();
	    minecraft = new Minecraft(canvas, applet, getWidth(), getHeight(),
		    false, false);

	    minecraft.session = new SessionData(Player, "noidea");
	    minecraft.session.mppass = Mppass;
	    minecraft.session.haspaid = true;
	    minecraft.server = Server;
	    minecraft.port = Port;
	    
	    boolean RunFakeNetwork = true;

	    if (RunFakeNetwork) {
		minecraft.host = "127.0.0.1";
		minecraft.host = minecraft.host + ":" + "25566";
		minecraft.session = new SessionData("Scoot1", "noidea");
		minecraft.session.mppass = "c0dd4746a88c5785952cd0190e8214a6";
		minecraft.session.haspaid = true;
		minecraft.server = "127.0.0.1";
		minecraft.port = 25566;
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
	     * 
	     */
	    private static final long serialVersionUID = 1L;

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
		image = ImageIO.read(getClass().getResourceAsStream(
			"/resources" + "/rsbg.jpg"));

	    }

	    void SetImage2() throws IOException {
		image2 = ImageIO.read(getClass().getResourceAsStream(
			"/resources" + "/bg.jpg"));
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

	    public Image getImage2() {
		return image2;
	    }
	}
    }
}
