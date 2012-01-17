package oklaGeneral;

import processing.core.PApplet;
import processing.core.PImage;
import processing.video.*;
import codeanticode.gsvideo.GSCapture;

public class OklaCam {
	
	PApplet pApplet;
	Capture qtCam;
	GSCapture gsCam;
	int camType=0; //0=quicktime, 1=gsVideo
	
	public OklaCam(PApplet pApplet, int camType) {
		super();
		this.pApplet = pApplet;
		this.camType = camType;
	}

	public void initCam(int w, int h) {
		if (camType == 0) {
			qtCam = new Capture(pApplet, w, h);
		} else if (camType == 1) {
			gsCam = new GSCapture(pApplet, w, h);
			while (!gsCam.available()) {
				pApplet.delay(50);
			}
		} else if (camType == 2) {
			// jMyronCam = new JMyron();// make a new instance of the object
			// jMyronCam.start(width, height);// start a capture at 320x240

			// jMyronCam.findGlobs(0);// disable the intelligence to speed up
			// frame
			// rate
			// println("Myron " + jMyronCam.version());
		}
	}

	public PImage getCamImage() {
		if (camType == 0) {
			if (qtCam.available() == true) {
				qtCam.read();
			}
			return qtCam.get();
		} else if (camType == 1) {
			if (gsCam.available() == true) {
				gsCam.read();
			}
			return gsCam.get();
		} else if (camType == 2) {
			// if (jMyronCam.available() == true) {
			// gsCam.jMyronCam.read();
			// }
			return null; // jMyronCam.get();
		} else
			return null;

	}


}
