package com.mojang.minecraft;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourceDownloadThread extends Thread {
    public static boolean Done = false;

    public ResourceDownloadThread(File minecraftFolder, Minecraft minecraft)
	    throws IOException {
	this.minecraft = minecraft;

	this.setName("Resource download thread");
	this.setDaemon(true);

	dir = new File(minecraftFolder, "resources/");

	if (!dir.exists() && !dir.mkdirs()) {
	    throw new RuntimeException(
		    "The working directory could not be created: " + dir);
	}
    }

    public static FileOutputStream fos;

    @Override
    public void run() {
	String[] files = new String[] { "music/calm1.ogg", "music/calm2.ogg",
		"music/calm3.ogg", "newmusic/hal1.ogg", "newmusic/hal2.ogg",
		"newmusic/hal3.ogg", "newmusic/hal4.ogg",
		"sound/step/grass1.ogg", "sound/step/grass2.ogg",
		"sound/step/grass3.ogg", "sound/step/grass4.ogg",
		"sound/step/gravel1.ogg", "sound/step/gravel2.ogg",
		"sound/step/gravel3.ogg", "sound/step/gravel4.ogg",
		"sound/step/stone1.ogg", "sound/step/stone2.ogg",
		"sound/step/stone3.ogg", "sound/step/stone4.ogg",
		"sound/step/wood1.ogg", "sound/step/wood2.ogg",
		"sound/step/wood3.ogg", "sound/step/wood4.ogg" };

	URL url;
	ReadableByteChannel rbc;
	File file;

	File folder = new File(dir, "music");
	folder.mkdir();
	folder = new File(dir, "newmusic");
	folder.mkdir();
	folder = new File(dir, "sound");
	folder.mkdir();
	folder = new File(folder, "step");
	folder.mkdir();

	try {
	    GameSettings.PercentString = "5%";
	    GameSettings.StatusString = "Downloading music and sounds...";
	    System.out.println("Downloading music and sounds...");

	    int Percent = 5;
	    for (int i = 0; i < files.length; i++) {
		if (Percent >= 80)
		    Percent = 80;
		Percent += 3;
		file = new File(dir, files[i]);

		if (!file.exists()) {
		    GameSettings.PercentString = Percent + "%";
		    GameSettings.StatusString = "Downloading http://s3.amazonaws.com/MinecraftResources/"
			    + files[i] + "...";
		    System.out
			    .println("Downloading http://s3.amazonaws.com/MinecraftResources/"
				    + files[i] + "...");

		    url = new URL("http://s3.amazonaws.com/MinecraftResources/"
			    + files[i]);
		    rbc = Channels.newChannel(url.openStream());
		    fos = new FileOutputStream(file);
		    fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		    GameSettings.StatusString = "Downloaded http://s3.amazonaws.com/MinecraftResources/"
			    + files[i] + "!";
		    System.out
			    .println("Downloaded http://s3.amazonaws.com/MinecraftResources/"
				    + files[i] + "!");
		}
	    }
	    GameSettings.PercentString = "85%";
	    GameSettings.StatusString = "Downloaded music and sounds!";
	    System.out.println("Downloaded music and sounds!");
	    GameSettings.StatusString = "";
	    GameSettings.PercentString = "";
	    Done = true;
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	File musicFolder = new File(dir, "music");

	for (int i = 1; i <= 3; i++) {
	    minecraft.sound.registerMusic("calm" + i + ".ogg", new File(
		    musicFolder, "calm" + i + ".ogg"));
	}

	File newMusicFolder = new File(dir, "newmusic");

	for (int i = 1; i <= 4; i++) {
	    minecraft.sound.registerMusic("calm" + i + ".ogg", new File(
		    newMusicFolder, "hal" + i + ".ogg"));
	}

	File stepsFolder = new File(dir, "sound/step");

	for (int i = 1; i <= 4; i++) {
	    minecraft.sound.registerSound(new File(stepsFolder, "grass" + i
		    + ".ogg"), "step/grass" + i + ".ogg");
	    minecraft.sound.registerSound(new File(stepsFolder, "gravel" + i
		    + ".ogg"), "step/gravel" + i + ".ogg");
	    minecraft.sound.registerSound(new File(stepsFolder, "stone" + i
		    + ".ogg"), "step/stone" + i + ".ogg");
	    minecraft.sound.registerSound(new File(stepsFolder, "wood" + i
		    + ".ogg"), "step/wood" + i + ".ogg");
	}

	finished = true;
    }

    private File dir;
    private Minecraft minecraft;
    boolean running = false;

    private boolean finished = false;

    public boolean deleteDir(File dir) {
	if (dir.isDirectory()) {
	    String[] children = dir.list();

	    for (int i = 0; i < children.length; i++) {
		boolean success = deleteDir(new File(dir, children[i]));

		if (!success) {
		    return false;
		}
	    }
	}

	return dir.delete();
    }

    public void unpack(String filename1) {
	String filename = filename1;

	File srcFile = new File(filename);

	String zipPath = filename.substring(0, filename.length() - 4);
	File temp = new File(zipPath);
	temp.mkdir();

	ZipFile zipFile = null;

	try {

	    zipFile = new ZipFile(srcFile);

	    Enumeration<? extends ZipEntry> e = zipFile.entries();

	    while (e.hasMoreElements()) {

		ZipEntry entry = e.nextElement();

		File destinationPath = new File(zipPath, entry.getName());

		destinationPath.getParentFile().mkdirs();

		if (entry.isDirectory()) {
		    continue;
		} else {
		    System.out.println("Extracting file: " + destinationPath);

		    BufferedInputStream bis = new BufferedInputStream(
			    zipFile.getInputStream(entry));

		    int b;
		    byte buffer[] = new byte[1024];

		    FileOutputStream fos = new FileOutputStream(destinationPath);

		    BufferedOutputStream bos = new BufferedOutputStream(fos,
			    1024);

		    while ((b = bis.read(buffer, 0, 1024)) != -1) {
			bos.write(buffer, 0, b);
		    }

		    bos.close();
		    bis.close();
		}
	    }
	} catch (IOException e1) {
	    System.out.println("Error opening zip file" + e1);
	} finally {
	    try {
		if (zipFile != null) {
		    zipFile.close();
		}
	    } catch (IOException e2) {
		System.out.println("Error while closing zip file" + e2);
	    }
	}
    }

    public static void copyFolder(File src, File dest) {
	try {
	    if (src.isDirectory()) {

		if (!dest.exists()) {
		    dest.mkdir();

		    System.out.println("Directory copied from " + src + "  to "
			    + dest);
		}

		String files[] = src.list();

		for (String file : files) {
		    File srcFile = new File(src, file);
		    File destFile = new File(dest, file);

		    copyFolder(srcFile, destFile);
		}
	    } else {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dest);

		byte[] buffer = new byte[1024];

		int length;

		while ((length = in.read(buffer)) > 0) {
		    out.write(buffer, 0, length);
		}

		in.close();
		out.close();

		System.out.println("File copied from " + src + " to " + dest);
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public boolean isFinished() {
	return finished;
    }
}
