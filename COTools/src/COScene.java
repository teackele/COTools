
import processing.core.*;

 abstract public class COScene extends PApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected CODirector _director;

	public void handleDraw() {
		this._director.sceneCallback();
		super.handleDraw();
	}

	
}
