package oklaGeneral;

import processing.core.PApplet;
import processing.core.PImage;

public class OklaKeyboard {
	PApplet pApplet;
	public PImage keyboard;
	public PImage keyboardCap;
	public PImage pressedMask;
	public int leftUpPixelX;
	public int leftUpPixelY;
	public float pixelWidth;
	public float pixelHeight;
	public int nRows = 11;
	public int nCols = 4;
	private int activeRow = -1;
	private int activeCol = -1;
	public int rowReturn = 2;
	public int colReturn = 11;
	private int buttonPixelWidth;
	private int buttonPixelHeight;
	public boolean on = false;
	public boolean capsLock = false;

	private String[][] keys = {
			{ "@", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" },
			{ "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "\n" },
			{ "shift", "a", "s", "d", "f", "g", "h", "j", "k", "l", "\n" },
			{ "#", "z", "x", "c", "v", "b", "n", "m", ".", "/", "-" } };

	public OklaKeyboard (PApplet pApp, int px, int py, int pw, int ph, PImage keyb, PImage keybCap, PImage pMask) {
    pApplet=pApp;
	keyboard = keyb;
    keyboardCap = keybCap;
    pressedMask = pMask;
    leftUpPixelX = px;
    leftUpPixelY = py;
    pixelWidth = PApplet.parseFloat(pw);
    pixelHeight = PApplet.parseFloat(ph);
    buttonPixelWidth = pw/nRows + 1;
    buttonPixelHeight = ph/nCols + 1;
  }

	public String keyPress(int mousePosX, int mousePosY) {
    if (on){
      float posOnKeyboardX = mousePosX-leftUpPixelX;
      float posOnKeyboardY = mousePosY-leftUpPixelY;
      int col = PApplet.parseInt(posOnKeyboardX/pixelWidth*PApplet.parseFloat(nRows));
      int row = PApplet.parseInt(posOnKeyboardY/pixelHeight*PApplet.parseFloat(nCols));
      String returnKey = "";
      if ((col >= 0) && (row >= 0) && (row < keys.length) && (col < keys[0].length)) {
        returnKey = keys[row][col];
        if (returnKey.equals("shift")) {
          capsLock = !capsLock;
          returnKey = "";
        }
        activeRow = row;
        activeCol = col;
      }
      return(returnKey);  
    }
    return("");
  }		public void keyRelease() {
		activeRow = -1;
		activeCol = -1;
	}

	void draw() {
		if (on) {
			pApplet.noTint();
			if (capsLock)
				pApplet.image(keyboardCap, leftUpPixelX, leftUpPixelY); // capital
																// keyboard
			else
				pApplet.image(keyboard, leftUpPixelX, leftUpPixelY); // normal
																// keyboard
			if ((activeCol >= 0) && (activeRow >= 0)
					&& (activeRow < keys.length)
					&& (activeCol < keys[0].length)) {
				pApplet.tint(255, 50);
				pApplet.image(pressedMask, leftUpPixelX + activeCol
						* buttonPixelWidth, leftUpPixelY + activeRow
						* buttonPixelHeight);
				pApplet.noTint();
			}
		}
	}
}