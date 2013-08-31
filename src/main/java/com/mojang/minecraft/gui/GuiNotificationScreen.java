package com.mojang.minecraft.gui;

import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.opengl.GL11;

public final class GuiNotificationScreen extends GuiScreen {

    String title;
    String message;
    public boolean fadeOut = false;

    public GuiNotificationScreen(String Title, String Message, int Duration) {
	this.title = Title;
	this.message = Message;
	miliseconds = Duration;
    }

    public final void onOpen() {
	this.buttons.clear();
	this.grabsMouse = true;
	start();
    }
    
    private final Timer timer = new Timer();
    private final int miliseconds;

    public void start() {
        timer.schedule(new TimerTask() {
            public void run() {
                Stop();
                timer.cancel();
            }
        }, miliseconds);
    }
    
    void Stop()
    {
	this.minecraft.notifyScreen = null;
    }

    int widthOffset = -200;

    public final void render(int var1, int var2) {
	if (!fadeOut) {
	    if (widthOffset < 0) {
		widthOffset += 13;
		if (widthOffset > 0)
		    widthOffset = 0;
	    }
	}
	GL11.glPushMatrix();
	GL11.glTranslatef(this.width * 0.7F - 2 + widthOffset,
		this.height * 0.7F - 21, 0.0F);
	GL11.glScalef(0.3F, 0.3F, 0.3F);
	drawBox(0, 0, this.width - 2, this.height - (this.height / 4), Integer.MIN_VALUE);

	GL11.glScalef(2F, 2F, 2F);
	drawCenteredString(this.fontRenderer, this.title, (this.width / 2 / 2), 20,
		16777215);

	int lastSubstring = 0;
	int lastWidth = 0;
	for (int i = 0; i <= 41 * 6; i += 41) {
	    try {
		drawCenteredString(
			this.fontRenderer,
			message.substring(lastSubstring,
				Math.min(i, message.length())),
			(this.width / 2 / 2), 22 + lastWidth, 7992295);
		lastSubstring = i;
		lastWidth += 12;
	    } catch (Exception e) {
	    }
	}

	GL11.glPopMatrix();
    }
}
