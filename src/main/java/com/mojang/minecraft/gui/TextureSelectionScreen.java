package com.mojang.minecraft.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import com.mojang.minecraft.Minecraft;

public class TextureSelectionScreen extends GuiScreen implements Runnable {

    protected GuiScreen parent;
    private boolean finished = false;
    private boolean loaded = false;
    private ArrayList<TexturePackData> textures = new ArrayList<TexturePackData>();
    private String status = "";
    protected String title = "Load texture";
    boolean frozen = false;
    JFileChooser chooser;
    protected boolean saving = false;
    protected File selectedFile;

    public TextureSelectionScreen(GuiScreen var1) {
	this.parent = var1;
    }

    protected final void onButtonClick(Button var1) {
	if (!this.frozen && var1.active) {
	    if (this.loaded && var1.id < 5) {
		this.openTexture(textures.get(var1.id));
	    }

	    if (this.loaded && var1.id == 6) {
		this.frozen = true;
		TextureDialog var2;
		(var2 = new TextureDialog(this, minecraft)).setDaemon(true);
		SwingUtilities.invokeLater(var2);
	    }

	    if (this.finished || this.loaded && var1.id == 7) {
		this.minecraft.setCurrentScreen(this.parent);
	    }
	    if (var1.id == 8) {
		this.minecraft.textureManager.currentTerrainPng = null;
		this.minecraft.textureManager.load("/terrain.png");
		this.minecraft.setCurrentScreen((GuiScreen) null);
		this.minecraft.grabMouse();
		this.minecraft.textureManager.textures.clear();
		this.minecraft.levelRenderer.refresh();
	    }
	}
    }

    public final void onClose() {
	super.onClose();
	if (this.chooser != null) {
	    this.chooser.cancelSelection();
	}

    }

    public void onOpen() {
	(new Thread(this)).start();

	for (int var1 = 0; var1 < 5; ++var1) {
	    this.buttons.add(new Button(var1, this.width / 2 - 100, this.height
		    / 6 + var1 * 24, "---"));
	    ((Button) this.buttons.get(var1)).visible = false;
	    ((Button) this.buttons.get(var1)).active = false;
	}

	this.buttons.add(new Button(6, this.width / 2 - 100,
		this.height / 6 + 120 + 12, "Load file..."));
	this.buttons.add(new Button(7, this.width / 2 - 100,
		this.height / 6 + 154 + 22, "Cancel"));
	this.buttons.add(new Button(8, this.width / 2 - 100,
		this.height / 6 + 154, "Default Texture"));
    }

    protected void openTexture(String file) {
	try {
	    this.minecraft.textureManager.loadTexturePack(file);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	this.minecraft.setCurrentScreen((GuiScreen) null);
	this.minecraft.grabMouse();
    }

    protected void openTexture(TexturePackData var1) {
	this.selectedFile = new File(var1.location);
	openTexture(var1.location);
	this.minecraft.setCurrentScreen(this.parent);
    }

    public void render(int var1, int var2) {
	drawFadingBox(0, 0, this.width, this.height, 1610941696, -1607454624);
	drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20,
		16777215);
	if (this.frozen) {
	    drawCenteredString(this.fontRenderer, "Selecting file..",
		    this.width / 2, this.height / 2 - 4, 16777215);

	    try {
		Thread.sleep(20L);
	    } catch (InterruptedException var3) {
		var3.printStackTrace();
	    }
	} else {
	    if (!this.loaded) {
		drawCenteredString(this.fontRenderer, this.status,
			this.width / 2, this.height / 2 - 4, 16777215);
	    }

	    super.render(var1, var2);
	}
    }

    public void run() {
	try {
	    if (this.frozen) {
		try {
		    Thread.sleep(100L);
		} catch (InterruptedException var2) {
		    var2.printStackTrace();
		}
	    }

	    this.status = "Getting texture list..";
	    TexturePackData data;
	    for (String file : (new File(Minecraft.getMinecraftDirectory()
		    + "/texturepacks").list())) {
		if (!file.endsWith(".zip"))
		    continue;
		data = new TexturePackData(file, file.substring(0,
			file.indexOf(".")));
		textures.add(data);
	    }
	    if (this.textures.size() >= 1) {
		this.setTextures(this.textures);
		this.loaded = true;
		return;
	    }

	    this.status = "Finished loading textures";
	    this.finished = true;
	} catch (Exception var3) {
	    var3.printStackTrace();
	    this.status = "Failed to load textures";
	    this.finished = true;
	}

    }

    protected void setTextures(ArrayList<TexturePackData> var1) {
	for (int var2 = 0; var2 < Math.min(var1.size(), 5); ++var2) {

	    this.buttons.get(var2).active = !var1.get(var2).equals("-");
	    this.buttons.get(var2).text = var1.get(var2).name;
	    this.buttons.get(var2).visible = true;
	}
    }

    public final void tick() {
	super.tick();
	if (this.selectedFile != null) {
	    this.selectedFile = null;
	}
    }
}
