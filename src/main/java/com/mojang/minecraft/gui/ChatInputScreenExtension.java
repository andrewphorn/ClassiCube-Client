package com.mojang.minecraft.gui;

import com.mojang.minecraft.net.NetworkManager;
import com.mojang.minecraft.net.PacketType;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Vector;
import org.lwjgl.input.Keyboard;

public class ChatInputScreenExtension extends GuiScreen
{
	  public String inputLine = "";
	  private int tickCount = 0;
	  private int caretPos = 0;
	  private int historyPos = 0;

	public final void onOpen() {
		Keyboard.enableRepeatEvents(true);
	}

	public final void onClose() {
		Keyboard.enableRepeatEvents(false);
	}

	public final void tick() {
		++this.tickCount;
	}

	public static Vector<String> history = new Vector();
	 
	int j;
	@Override
	protected final void onKeyPress(char paramChar, int paramInt) {
	    if (paramInt == 1) {
	    	this.minecraft.setCurrentScreen((GuiScreen) null);
	      return;
	    }
	    if(Keyboard.isKeyDown(Keyboard.KEY_TAB))
		return;

	    if (paramInt == 28) {
	      String str1 = this.inputLine.trim();
	      if (str1.length() > 0) {
	    	  NetworkManager var10000 = this.minecraft.networkManager;
	    	  NetworkManager var3 = var10000;
				if ((str1 = str1.trim()).length() > 0) {
					var3.netHandler.send(PacketType.CHAT_MESSAGE, new Object[] {
							Integer.valueOf(-1), str1 });
	        history.add(str1);
	      }
				
	    }
	      this.minecraft.setCurrentScreen((GuiScreen) null);
	      return;
	    }

	    int i = this.inputLine.length();
	    if ((paramInt == 14) && (i > 0) && (this.caretPos > 0)) {
	      this.inputLine = (this.inputLine.substring(0, this.caretPos - 1) + this.inputLine.substring(this.caretPos));
	      this.caretPos -= 1;
	    }

	    if ((paramInt == 203) && (this.caretPos > 0)) {
	      this.caretPos -= 1;
	    }

	    if ((paramInt == 205) && (this.caretPos < i)) {
	      this.caretPos += 1;
	    }

	    if (paramInt == 199) {
	      this.caretPos = 0;
	    }

	    if (paramInt == 207) {
	      this.caretPos = i;
	    }

	    if ((Keyboard.isKeyDown(219)) || (Keyboard.isKeyDown(220)) || (Keyboard.isKeyDown(29)) || (Keyboard.isKeyDown(157)))
	    {
	      if (paramInt == 47) {
	        paramChar = '\000';
	        String str2 = getClipboard();
	        insertTextAtCaret(str2);
	      }
	      else if (paramInt == 46) {
	        paramChar = '\000';
	        setClipboard(this.inputLine);
	      }
	    }

	    if (paramInt == 200) {
	      j = history.size();
	      if (this.historyPos < j) {
	        this.historyPos += 1;
	        this.inputLine = ((String)history.get(j - this.historyPos));
	        this.caretPos = this.inputLine.length();
	      }
	    }

	    if (paramInt == 208) {
	      j = history.size();
	      if (this.historyPos > 0) {
	        this.historyPos -= 1;

	        if (this.historyPos > 0) {
	          this.inputLine = ((String)history.get(j - this.historyPos));
	        }
	        else {
	          this.inputLine = "";
	        }
	        this.caretPos = this.inputLine.length();
	      }
	    }

	    int j = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ,.:-_'*!\\\"#%/()=+?[]{}<>@|$;~`^".indexOf(paramChar) >= 0 ? 1 : 0;

	    if (j != 0)
	      insertTextAtCaret(String.valueOf(paramChar));
	}
	    
	  private void insertTextAtCaret(String paramString)
	  {
	    int i = 64 - this.minecraft.session.username.length() - 2;

	    int j = paramString.length();
	    this.inputLine = (this.inputLine.substring(0, this.caretPos) + paramString + this.inputLine.substring(this.caretPos));
	    this.caretPos += j;
	    if (this.inputLine.length() > i) {
	      this.inputLine = this.inputLine.substring(0, i);
	    }
	    if (this.caretPos > this.inputLine.length())
	      this.caretPos = this.inputLine.length();
	  }

	  public void render(int paramInt1, int paramInt2)
	  {
	    drawBox(2, this.height - 14, this.width - 2, this.height - 2, -2147483648);
	  //  System.out.println(""+temp.length);
	    char[] temp = new char[128]; 
	    for(int a = 0; a< this.inputLine.length(); a++)
	    {
	    	temp[a]= this.inputLine.toCharArray()[a];
	    }
	    
	    if(temp.length == 0)
	    	temp[temp.length] = (this.tickCount / 6 % 2 == 0 ? '_' : ' ');
	    else 
	    	temp[this.caretPos] = (this.tickCount / 6 % 2 == 0 ? '_' : temp[this.caretPos]);
	    
	    String string = "";
	    for(int i = 0; i< temp.length; i++){
	    	string += temp[i];
	    }
	    drawString(this.fontRenderer, "> " + string, 4, this.height - 12, 14737632);
	   // drawString(this.fontRenderer, "> " + this.inputLine + (this.tickCount / 6 % 2 == 0 ? this.caretPos + 1 : -1), 4, this.height - 12, -3092272);
	    //drawString(this.fontRenderer, "> " + this.inputLine + (this.tickCount / 6 % 2 == 0 ? "" : "_"), 4, this.height - 12, -3092272);
	  }
	  
	  protected final void onMouseClick(int paramInt1, int paramInt2, int paramInt3) {
	    if ((paramInt3 == 0) && (this.minecraft.hud.hoveredPlayer != null))
	      insertTextAtCaret(this.minecraft.hud.hoveredPlayer + " ");
	  }

	  private String getClipboard()
	  {
	    Transferable localTransferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
	    try
	    {
	      if ((localTransferable != null) && (localTransferable.isDataFlavorSupported(DataFlavor.stringFlavor)))
	        return (String)localTransferable.getTransferData(DataFlavor.stringFlavor);
	    }
	    catch (UnsupportedFlavorException localUnsupportedFlavorException) {
	    }
	    catch (IOException localIOException) {
	    }
	    return null;
	  }

	  private void setClipboard(String paramString) {
	    StringSelection localStringSelection = new StringSelection(paramString);
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(localStringSelection, null);
	  }
}