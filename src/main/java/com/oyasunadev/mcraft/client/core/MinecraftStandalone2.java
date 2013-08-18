//package com.oyasunadev.mcraft.client.core;
//
///**
//* Created with IntelliJ IDEA.
//* User: Oliver
//* Date: 4/19/13
//* Time: 2:34 PM
//*/
//
//import com.mojang.minecraft.Minecraft;
//import com.mojang.minecraft.MinecraftApplet;
//
//import javax.swing.*;
//import java.awt.*;
//
///**
//* Run Minecraft Classic standalone version.
//*/
//public class MinecraftStandalone2 extends JFrame
//{
//	/**
//	 * Default constructor.
//	 */
//	public MinecraftStandalone2()
//	{
//	}
//
//	/**
//	 * Reference to the MCraftApplet2 object.
//	 */
//	private MCraftApplet2 mcApplet;
//
//	/**
//	 * Custom applet in order to bypass URL check as well as add functionality.
//	 */
//	private class MCraftApplet2 extends MinecraftApplet
//	{
//		/**
//		 * Default constructor.
//		 */
//		public MCraftApplet2()
//		{
//		}
//
//		/**
//		 * Reference to the applet canvas.
//		 */
//		private Canvas mcCanvas;
//
//		/**
//		 * Reference to the Minecraft object.
//		 */
//		private Minecraft mc;
//
//		/**
//		 * Reference to the Minecraft main thread.
//		 */
//		private Thread mcThread = null;
//
//		/**
//		 * Setup the applet.
//		 */
//		@Override
//		public void init()
//		{
//			mcCanvas = new MCraftCanvas2(this);
//
//			boolean fullscreen = getParameter("fullscreen").equalsIgnoreCase("true");
//
//			mc = new Minecraft(mcCanvas, this, 854, 480, fullscreen);
//		}
//	}
//
//	/**
//	 * Custom canvas.
//	 */
//	private class MCraftCanvas2 extends Canvas
//	{
//		/**
//		 * Set the applet object.
//		 *
//		 * @param mcApplet
//		 */
//		public MCraftCanvas2(MCraftApplet2 mcApplet)
//		{
//			this.mcApplet = mcApplet;
//		}
//
//		/**
//		 * Start the thread.
//		 */
//		@Override
//		public synchronized void addNotify()
//		{
//			super.addNotify();
//
//			mcApplet.startMainThread();
//		}
//
//		/**
//		 * Stop the thread.
//		 */
//		@Override
//		public synchronized void removeNotify()
//		{
//			mcApplet.shutdown();
//
//			super.removeNotify();
//		}
//
//		/**
//		 * Reference to the MCraftApplet2 object.
//		 */
//		private MCraftApplet2 mcApplet;
//	}
//}
