
import processing.core.*;

 abstract public class COScene extends PApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected COStageManager stageManager;
	public long maxRunTime = 0;

	/*
	 * 
	 * String with key values automatically fetched if we have a sessionId
	 */
	public String[] dataNeeded = {"a","b"};
	
	/*
	 * resource needed
	 */
	public String[] resourcesNeeded = {};

	public void handleDraw() {
		try {
			this.stageManager.sceneCallback();
			
		} catch (Exception E) {};
		
		super.handleDraw();
	}

	
}
