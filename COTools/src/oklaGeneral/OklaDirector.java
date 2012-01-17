package oklaGeneral;

import java.util.ArrayList;

public class OklaDirector {
	ArrayList<OklaBasicScene> scenes;
	private int curSceneIndex = -1;

	public OklaDirector() {
		scenes = new ArrayList<OklaBasicScene>();
		resetScenes();

	}
	
	public void draw() {
		curScene().draw();
	}

	public void control() {

		if (!scenes.isEmpty()) {
			if (scenes.get(curSceneIndex).isFinished) {
				if (curScene().doSceneReset) {
					curScene().close();
					resetScenes();
				}
				if (curSceneIndex >= scenes.size()-1) {
					curScene().close();
					resetScenes();
				} else {
					goToScene(curSceneIndex+1);
				}
			}
		}
	}

	public void resetScenes() {
		curSceneIndex = 0;
	}
	
	public void goToScene(int newSceneIndex) {
		scenes.get(newSceneIndex).data=curScene().data;
		curScene().close();
		curSceneIndex=newSceneIndex;
		curScene().init();
	}
	
	public OklaBasicScene curScene() {
		if (scenes.isEmpty()) return null;
		return scenes.get(curSceneIndex);
	}
}
