
import java.lang.reflect.Method;
/*
 * Misschien stage manager ertussen voor het beheer van de scenes?
 * 
 */
public class CODirector extends  COBase {
	 
	// net stuff
	COCmdServer _cmdServer;
	
	// scene management stuff
	protected COStageManager stageManager;

	// process controll
	
	volatile boolean klaar = false;
	volatile String status;
	Object eventMonitor = new Object();
	boolean gotEvent = false;
	//	Thread thread;
	
	
	/*************** Object glue *******************/
	CODirector() { 	
		System.out.println("con");
		this.stageManager = new COStageManager();
		this._cmdServer = new COCmdServer(this,6789);
		
		//this.thread = new Thread(this);
	}
	
	public void dispose() {
		this._cmdServer.dispose();
		this.stageManager.dispose();
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
		dispose();
		System.exit(0);
	}
	
	public void run() {
		this.stageManager.addScene("MyScene");
		this.stageManager.addScene("MyScene2");
		
		this.status = "Wachten";
		
		while(!klaar) {
			
			if (!this.stageManager.playing) {
				this.stageManager.cueNextScene();
			}
		    
		    try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// stop niet nodig, gaat terug naar main van waaruit ie is aangeroepen
		// stop();
		
		
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
			  cmdMethod.invoke(this,  (Object) tokens, (Object) aclient);
		  }
		  catch(Exception e) {
			  // invalid command
			  aclient.write("Ongeldig commando\n");
		  }
		  // find class method

		  
	  }
	  
	  /*
	   * Stopt de director vanaf extern
	   */
	  public void netCommandstop(String payload[], COCmdClient aclient) {
		  log("stop");
		  //aclient.stop();
		  //System.exit(0);
		  aclient.write("stopping\n");
		  this.klaar = true;
		  //this.wakeup();
	  }

	  public void netCommandstopscene(String payload[], COCmdClient aclient) {
		  log("stopscene");
		  //aclient.stop();
		  //System.exit(0);
		  //aclient.write("stopping\n");
		  this.stageManager.stopScene();
		  aclient.write("stopped\n");
		  
	  }

	  public void netCommandstatus(String payload[], COCmdClient aclient) {
		  log("status");
		  aclient.write(this.status + "\n");
		  //aclient.stop();
	  }

	  public void netCommandnext(String payload[], COCmdClient aclient) {
		  log("next");
		  this.stageManager.cueNextScene();
		  aclient.write(this.status + "\n");
		  //aclient.stop();
	  }
	
}
