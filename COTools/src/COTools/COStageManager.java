package COTools;
import java.awt.BorderLayout;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;

import processing.core.PApplet;

import oklaGeneral.*;

import java.util.Hashtable;

public class COStageManager extends COBase {
	// scene management stuff
	protected ArrayList<String>  _scenes;
	
	// state management
	public boolean playing = false;
	protected COScene currentScene;
	protected int currentSceneId = -1;
	protected Thread sceneThread;
	
	protected long sceneStarted;
	
	protected Hashtable<String, Object> components;
	

	// who's the boss
	protected CODirector director;

	public CODataStore data;
	
	//stage stuff
    static public int platform;
    static {
        String osname = System.getProperty("os.name");

        if (osname.indexOf("Mac") != -1) {
          platform = processing.core.PConstants.MACOSX;

        } else if (osname.indexOf("Windows") != -1) {
          platform = processing.core.PConstants.WINDOWS;

        } else if (osname.equals("Linux")) {  // true for the ibm vm
          platform = processing.core.PConstants.LINUX;

        } else {
          platform = processing.core.PConstants.OTHER;
        }
      }

	Frame stage;

	// Host PApplet for processing coomponents that need PApplet functionality
	COHost host;
	
	// componenten
	//OklaAudioPlayer oklaAudio;
	
	/*
	 * Constructor
	 * zet stage op (fullscreen awt frame)
	 * 
	 */
	COStageManager() {
		this._scenes = new ArrayList<String>();
		this.data = new CODataStore();
		
		// gekpieerde uit PApplet embed voorbeeld en runsketch
		if (platform == processing.core.PConstants.MACOSX) {
	        // Only run this on OS X otherwise it can cause a permissions error.
	        // http://dev.processing.org/bugs/show_bug.cgi?id=976
	        System.setProperty("apple.awt.graphics.UseQuartz",
	                           String.valueOf(1));
	    }
        GraphicsEnvironment environment =
	    	        GraphicsEnvironment.getLocalGraphicsEnvironment();
	     
		GraphicsDevice displayDevice = environment.getDefaultScreenDevice();
	    this.stage = new Frame(displayDevice.getDefaultConfiguration());
	    this.stage.setLayout(new BorderLayout());
	    this.stage.setUndecorated(true);
	    //this.setBackground(backgroundColor);
//        displayDevice.setFullScreenWindow(this.stage);
	    Rectangle fullScreenRect = this.stage.getBounds();
	    
        DisplayMode mode = displayDevice.getDisplayMode();
        fullScreenRect = new Rectangle(0, 0, mode.getWidth(), mode.getHeight());
        this.stage.setBounds(fullScreenRect);
        this.stage.setVisible(true);
        
	    this.stage.setLayout(null); 
	    this.stage.invalidate();
	    
//	    host = new COScene();
		//Class<?> c = Thread.currentThread().getContextClassLoader().loadClass("COScene");
		host =  new COHost();
		host.init();
		
		components = new Hashtable<String, Object>();
		
		OklaAudioPlayer oklaAudio = new OklaAudioPlayer(host);
		components.put("oklaAudio", oklaAudio);
		
		OklaTTS oklaTTS = new OklaTTS(0);
		components.put("oklaTTS", oklaTTS);
		
		//fixme, cam wants to capture to papplet... host is the wrong applet
		OklaCam oklaCam = new OklaCam(host,0);
		components.put("oklaCam", oklaCam);



	}

	public void dispose() {
		host.dispose();
//		OklaAudioPlayer oklaAudio = (OklaAudioPlayer) components.get("oklaAudio");
//		components.put("oklaAudio", oklaAudio);
		
		// TODO Auto-generated method stub
		
		
	}
	
	/************** Scene list management *******************/
	public void addScene(String name) {
		this._scenes.add(name);
		
	}
	
	
	/******************* Scene control *******************/
	public void cueNextScene() {
		this.stopScene();

		this.currentSceneId++;
		if (this.currentSceneId >= this._scenes.size()) {
			this.currentSceneId = 0;
		}
		
		String nextScene = this._scenes.get(this.currentSceneId);
		
		this.currentScene = this.loadScene(nextScene);
		
		this.runScene();
		
		// blocking!!
		while (!playing) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void stopScene() {
		if (this.playing) {
			this.currentScene.finished = true;
			try {
				this.sceneThread.join();
			} catch (Exception E) {};
			

			this.stage.remove(this.currentScene);
			this.currentScene.dispose();
			
			this.playing = false;
		}
		
	}
	
	/***************** ***************/
	
	public void sceneCallback() {
		if (!this.playing) {
			// aquire animation thread
			this.sceneThread = Thread.currentThread();
			this.playing = true;
			this.setSceneStopEvent();
			log("Scene started");
			
		}
		
		// TODO implement timoeut base on COScene maxtime
		if (this.currentScene.maxRunTime != 0) {
			if (System.currentTimeMillis() > this.sceneStarted + this.currentScene.maxRunTime ) {
				this.currentScene.finished = true;
			}
		}
		// log("cb");
		
	}
	
	protected void setSceneStopEvent() {
		stopEventWatcher watch = new stopEventWatcher();
		watch.threadToWaitFor = this.sceneThread;
		watch.stagemanager = this;
		watch.start();
	}
	
	protected COScene loadScene(String name) {
		COScene newScene;
		
		try {
			Class<?> c = Thread.currentThread().getContextClassLoader().loadClass(name);
			newScene = (COScene) c.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
	    }
		// TODO fetch data items as set in scene
		
		newScene.stageManager = this;
		newScene.frame = this.stage;	
		
		newScene.oklaAudio = (OklaAudioPlayer) components.get("oklaAudio");
		newScene.oklaTTS = (OklaTTS) components.get("oklaTTS");
		newScene.oklaCam = (OklaCam) components.get("oklaCam");
		host.currentScene = newScene;
		
		return newScene;		
		
				
	}
	
	protected void runScene() {
		//maybe better to create own frame and rund init
		// see embed example
//		PApplet.runSketch(new String [] {"--present"}, this.currentScene);
		this.stage.add(this.currentScene, BorderLayout.CENTER);
		this.sceneStarted = System.currentTimeMillis();
		this.currentScene.init();

	}

	class stopEventWatcher extends Thread {
		public Thread threadToWaitFor;
		public COStageManager stagemanager;
		public void run() {
			while (!(this.threadToWaitFor == null) && !this.stagemanager.currentScene.finished) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.stagemanager.stopScene();
	    }
	}

	
}

