
import java.lang.reflect.Method;
import java.util.ArrayList;

import processing.core.PApplet;
/*
 * Misschien stage manager ertussen voor het beheer van de scenes?
 * 
 */
public class CODirector extends  COBase {
	 
	COCmdServer _cmdServer;
	protected ArrayList<String>  _scenes;
	volatile protected COScene currentScene;
	volatile boolean klaar = false;
	volatile String status;
	
	Object eventMonitor = new Object();
	boolean gotEvent = false;
//	Thread thread;
	
	
	/*************** Object glue *******************/
	CODirector() { 	
		System.out.println("con");
		this._cmdServer = new COCmdServer(this,6789);
		this._scenes = new ArrayList<String>();
		
		//this.thread = new Thread(this);
	}
	
	public void dispose() {
		this._cmdServer.dispose();
	}
	
	/*************** Scene management *******************/

	public void addScene(String newScene) {
		this._scenes.add(newScene);
		
	}
	
	// get called from animation thread
	public void sceneCallback() {
		// set finished to true to stop applet
		//System.out.println(Thread.currentThread());
		//System.out.println("cb");
	}
	
	public synchronized void sceneStop() {
		this.currentScene.finished = true;
	}
	
	void startVoorstelling() {
//		PApplet.main(new String[] { "--present", this._scenes[0] });
		
		String name = this._scenes.get(0);
	    
		// final Scene applet;
		try {
			Class<?> c = Thread.currentThread().getContextClassLoader().loadClass(name);
			this.currentScene = (COScene) c.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
	    }
		
		this.currentScene._director = this;
		
	      
//		PApplet.main(new String[] { "--present",applet);
		PApplet.runSketch(new String [] {"--present"}, this.currentScene);
		System.out.println(Thread.currentThread());
		
		while (!this.currentScene.finished) {
//			this.sleep();
		}
		
		this.currentScene.dispose();
		//System.exit(0);
		// hier wachten tot skectch klaar is?
		
	}

	/**************** Thread and process management *******************/
	/*
	 * Start de Director
	 * 
	 */
	/*
	public void go() {
		this.thread.start();
	}
	*/
	
	public void stop() {
		
		System.exit(0);
	}
	
	public void run() {
		this.status = "Wachten";
		
		while(!klaar) {
		    synchronized(this.eventMonitor){
		        while(!this.gotEvent){
		          try{
		        	 eventMonitor.wait();
		           } catch(InterruptedException e){}
		        }
		        // handle stuff
		        
		        
		        //clear signal and continue running.
		        this.gotEvent = false;
		      }
		    /*
		    try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		}
		
		
	}

	public void wakeup() {
		synchronized(this.eventMonitor){
			this.gotEvent = true;
			eventMonitor.notify();
	    }
	}
	
	
	
	
	
	/*************** Net interface  *******************/
	public void serverEvent (COCmdServer someServer, COCmdClient someClient) {
		someClient.write("welkom bij Club Oklahoma zuil (0.1)\n");
		
	}
	/*
	 * Handles event van de server bij het binnekomen van nieuwe connectie
	 * wacht tot er een [CR] #10 binnenkomt en voert dan procesCommand uut
	 */
	  public void clientEvent(COCmdClient aclient) {
		  //statements
		  if (aclient.available() > 0) {
			  String dataIn = aclient.readStringUntil(10);
			  if (dataIn != null) {
				  log(dataIn);
				  this.processCommand(aclient, dataIn);
				  
			  }
		  }
	  }
	  
	  /*
	   * Zoekt op basis van het commando de passende methode uit en voer deze uiut
	   * methodes zien er als vlogt uit
	   *  public void netCommand<commandonaam>(String payload[], COCmdClient aclient) {
	   *  payload is de commandstring gesplit op de spaties, inclusief het commando zelf
	   */
	  public void processCommand(COCmdClient aclient, String command) {
		  
		  // Remove whitespaces and CR
		  String stripped = command.trim();
		  
		  // Split at space
		  String tokens[] = stripped.split(" ");
		  String cmd = tokens[0].toLowerCase();
		 
		  log(cmd);

		  Method cmdMethod;
		  try {
			  cmdMethod = this.getClass().getMethod("netCommand" + cmd, String[].class,  COCmdClient.class);
			  this.wakeup();
			  cmdMethod.invoke(this,  (Object) tokens, (Object) aclient);
		  }
		  catch(Exception e) {
			  // invalid command
			  aclient.write("Ongeldig commando");
			  aclient.write(10);
		  }
		  // find class method

		  
	  }
	  
	  /*
	   * Stopt de director vanaf extern
	   */
	  public void netCommandstop(String payload[], COCmdClient aclient) {
		  log("stop");
		  //aclient.stop();
		  System.exit(0);
	  }
	  public void netCommandstatus(String payload[], COCmdClient aclient) {
		  log("status");
		  aclient.write(this.status);
		  aclient.write(10);
		  //aclient.stop();
	  }

	
}
