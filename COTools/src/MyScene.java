import COTools.COScene;

import oklaGeneral.*;

public class MyScene extends COScene {

	
	  public void setup() {
	    size(200,200);
	    // Initialize all "stripes"
	    this.maxRunTime = 1000;
//	    OklaAudioPlayer x = (OklaAudioPlayer) components.get("oklaAudio");
	    oklaAudio.play("data/audio/telefoonbel16b.wav", "fiets");
	    oklaTTS.speak("Teackele");
	  }

	  public void draw() {
		    stroke(255);
		    if (mousePressed) {
		    	 this.finished = true;
		    }
	  }
	
	  public void mousePressed() {
		  switch(mouseButton) {
		  case LEFT:
			  System.out.println("links\n");
			  break;
		  case RIGHT:
			  System.out.println("rechts\n");
			 
			  break;
		  case CENTER:
			  System.out.println("midden\n");
			  break;
		  
		  }
		  System.out.println(mouseButton);
		  //		  this.finished = true;
		  
	}
	  
}
