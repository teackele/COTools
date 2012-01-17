package oklaGeneral;

import processing.core.PApplet;
import processing.core.PImage;

abstract public class OklaBasicScene {

	PImage bgImage;
	PImage bgImageFile;
	PApplet pApplet;
	boolean isInit = false;
	String name="default";
	boolean isFinished = false;
	boolean doSceneReset = false;
	OklaData data;
	
	public OklaBasicScene(PApplet pApplet) {
		data=new OklaData(); 
		this.pApplet=pApplet;
	}
	
	public void draw() {
	}
	
	public void init() {
		isInit=false;
		isFinished=false;
		doSceneReset=false;
	}
	
	public void close() {
		isInit=false;
		isFinished=false;
		doSceneReset=false;
	}
	
}
