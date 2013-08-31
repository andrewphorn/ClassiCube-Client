package com.mojang.minecraft;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.net.MalformedURLException;
import java.net.URL;

public class MinecraftApplet extends Applet {
    private static final long serialVersionUID = 1L;

    private Canvas canvas;
    private Minecraft minecraft;

    private Thread thread = null;

    public URL getDocumentBase() {
	try {
	    return new URL("http://minecraft.net:80/play.jsp");
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	}

	return null;
    }

    public URL getCodeBase() {
	try {
	    return new URL("http://minecraft.net:80/");
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public void init() {
	canvas = new MinecraftApplet$1(this);

	boolean fullscreen = false;

	if (getParameter("fullscreen") != null) {
	    fullscreen = getParameter("fullscreen").equalsIgnoreCase("true");
	}

	minecraft = new Minecraft(canvas, this, getWidth(), getHeight(),
		fullscreen, true);

	minecraft.host = getDocumentBase().getHost();

	if (getDocumentBase().getPort() > 0) {
	    minecraft.host = minecraft.host + ":" + getDocumentBase().getPort();
	}

	if (getParameter("username") != null
		&& getParameter("sessionid") != null) {
	    minecraft.session = new SessionData(getParameter("username"),
		    getParameter("sessionid"));

	    if (getParameter("mppass") != null) {
		minecraft.session.mppass = getParameter("mppass");
	    }

	    // TODO: Not tested.
	    minecraft.session.haspaid = getParameter("haspaid")
		    .equalsIgnoreCase("true");
	}

	if (getParameter("loadmap_user") != null
		&& getParameter("loadmap_id") != null) {
	    minecraft.levelName = getParameter("loadmap_user");
	    minecraft.levelId = Integer.parseInt(getParameter("loadmap_id"));
	} else if (getParameter("server") != null
		&& getParameter("port") != null) {
	    String server = getParameter("server");
	    int port = Integer.parseInt(getParameter("port"));

	    minecraft.server = server;
	    minecraft.port = port;
	}

	minecraft.levelLoaded = true;

	setLayout(new BorderLayout());

	add(canvas, "Center");

	canvas.setFocusable(true);

	validate();
    }

    public void startGameThread() {
	if (thread == null) {
	    thread = new Thread(minecraft);

	    thread.start();
	}
    }

    @Override
    public void start() {
	minecraft.waiting = false;
    }

    @Override
    public void stop() {
	minecraft.waiting = true;
    }

    @Override
    public void destroy() {
	stopGameThread();
    }

    public void stopGameThread() {
	if (thread != null) {
	    minecraft.running = false;

	    try {
		thread.join(1000L);
	    } catch (InterruptedException var3) {
		minecraft.shutdown();
	    }

	    thread = null;
	}
    }
}
