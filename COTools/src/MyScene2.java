
public class MyScene2 extends COScene {

	  public void setup() {
	  //  size(200,200);
	    // Initialize all "stripes"
	  }

	  public void draw() {
		    stroke(255);
		    if (mousePressed) {
		    	 this.finished = true;
		    }	
		    
	  }
	
	  public void mousePressed() {
		  this.finished = true;
		  
	}
	  
}
