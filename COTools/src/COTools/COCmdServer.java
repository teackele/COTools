package COTools;
/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Server - basic network server implementation
  Part of the Processing project - http://processing.org

  Copyright (c) 2004-2007 Ben Fry and Casey Reas
  The previous version of this code was developed by Hernando Barragan

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

import processing.core.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;

/**
 * A server sends and receives data to and from its associated COCmdClients (other programs connected to it).
 * When a server is started, it begins listening for connections on the port specified by the <b>port</b> parameter.
 * Computers have many ports for transferring data and some are commonly used so be sure to not select one of these.
 * For example, web servers usually use port 80 and POP mail uses port 110.
 * 
 * @webref
 * @brief The server class is used to create server objects which send and receives data to and from its associated COCmdClients (other programs connected to it). 
 * @instanceName server  	any variable of type Server
 * @usage  	Application
 */
public class COCmdServer implements Runnable {

  COBase parent;
  Method serverEventMethod;

  Thread thread;
  ServerSocket server;
  int port;
  
  /** Number of COCmdClients currently connected. */
  public int COCmdClientCount;
  /** Array of COCmdClient objects, useful length is determined by COCmdClientCount. */
  public COCmdClient[] COCmdClients;

  /**
   * 
   * @param parent typically use "this"
   * @param port port used to transfer data
   */
  public COCmdServer(COBase parent, int port) {
    this.parent = parent;
    this.port = port;

    try {
      server = new ServerSocket(this.port);
      //COCmdClients = new Vector();
      COCmdClients = new COCmdClient[10];

      thread = new Thread(this);
      thread.start();

     // parent.registerDispose(this);

      // reflection to check whether host applet has a call for
      // public void serverEvent(Server s, COCmdClient c);
      // which is called when a new guy connects
      try {
        serverEventMethod =
          parent.getClass().getMethod("serverEvent",
                                      new Class[] { COCmdServer.class,
                                                    COCmdClient.class });
      } catch (Exception e) {
        // no such method, or an error.. which is fine, just ignore
      }

    } catch (IOException e) {
      e.printStackTrace();
      thread = null;
      //errorMessage("<init>", e);
    }
  }


  /**
   * Disconnect a particular COCmdClient.
   * @webref
   * @param COCmdClient the COCmdClient to disconnect
   */
  public void disconnect(COCmdClient COCmdClient) {
    //COCmdClient.stop();
    COCmdClient.dispose();
    int index = COCmdClientIndex(COCmdClient);
    if (index != -1) {
      removeIndex(index);
    }
  }
  
  
  protected void removeIndex(int index) {
    COCmdClientCount--;
    // shift down the remaining COCmdClients
    for (int i = index; i < COCmdClientCount; i++) {
      COCmdClients[i] = COCmdClients[i+1];
    }
    // mark last empty var for garbage collection
    COCmdClients[COCmdClientCount] = null;
  }
  
  
  protected void addCOCmdClient(COCmdClient COCmdClient) {
    if (COCmdClientCount == COCmdClients.length) {
      COCmdClients = (COCmdClient[]) PApplet.expand(COCmdClients);
    }
    COCmdClients[COCmdClientCount++] = COCmdClient;
  }
  
  
  protected int COCmdClientIndex(COCmdClient COCmdClient) {
    for (int i = 0; i < COCmdClientCount; i++) {
      if (COCmdClients[i] == COCmdClient) {
        return i;
      }
    }
    return -1;
  }


  // the last index used for available. can't just cycle through
  // the COCmdClients in order from 0 each time, because if COCmdClient 0 won't
  // shut up, then the rest of the COCmdClients will never be heard from.
  int lastAvailable = -1;

  /**
   * Returns the next COCmdClient in line with a new message
   * @webref
   */
  public COCmdClient available() {
    synchronized (COCmdClients) {
      int index = lastAvailable + 1;
      if (index >= COCmdClientCount) index = 0;

      for (int i = 0; i < COCmdClientCount; i++) {
        int which = (index + i) % COCmdClientCount;
        COCmdClient COCmdClient = COCmdClients[which];
        if (COCmdClient.available() > 0) {
          lastAvailable = which;
          return COCmdClient;
        }
      }
    }
    return null;
  }


  /**
   * Disconnects all COCmdClients and stops the server
   * =advanced
   * <p/>
   * Use this to shut down the server if you finish using it while your applet 
   * is still running. Otherwise, it will be automatically be shut down by the 
   * host PApplet using dispose(), which is identical. 
   * @webref
   */
  public void stop() {
    dispose();
  }


  /**
   * Disconnect all COCmdClients and stop the server: internal use only.
   */
  public void dispose() {
    try {
      thread = null;

      if (COCmdClients != null) {
        for (int i = 0; i < COCmdClientCount; i++) {
          disconnect(COCmdClients[i]);
        }
        COCmdClientCount = 0;
        COCmdClients = null;
      }

      if (server != null) {
        server.close();
        server = null;
      }

    } catch (IOException e) {
      e.printStackTrace();
      //errorMessage("stop", e);
    }
  }


  public void run() {
    while (Thread.currentThread() == thread) {
      try {
        Socket socket = server.accept();
        COCmdClient COCmdClient = new COCmdClient(parent, socket);
        synchronized (COCmdClients) {
          addCOCmdClient(COCmdClient);
          if (serverEventMethod != null) {
            try {
              serverEventMethod.invoke(parent, new Object[] { this, COCmdClient });
            } catch (Exception e) {
              System.err.println("Disabling serverEvent() for port " + port);
              e.printStackTrace();
              serverEventMethod = null;
            }
          }
        }
      } catch (IOException e) {
        //errorMessage("run", e);
        e.printStackTrace();
        thread = null;
      }
      try {
        Thread.sleep(8);
      } catch (InterruptedException ex) { }
    }
  }



  /**
   * Write a value to all the connected COCmdClients.
   * See COCmdClient.write() for operational details.
   * 
   * @webref
   * @brief Writes data to all connected COCmdClients
   * @param data data to write
   */
  public void write(int data) {  // will also cover char
    int index = 0;
    while (index < COCmdClientCount) {
      COCmdClients[index].write(data);
      if (COCmdClients[index].active()) {
        index++;
      } else {
        removeIndex(index);
      }
    }
  }


  /**
   * Write a byte array to all the connected COCmdClients.
   * See COCmdClient.write() for operational details.
   */
  public void write(byte data[]) {
    int index = 0;
    while (index < COCmdClientCount) {
      COCmdClients[index].write(data);
      if (COCmdClients[index].active()) {
        index++;
      } else {
        removeIndex(index);
      }
    }
  }


  /**
   * Write a String to all the connected COCmdClients.
   * See COCmdClient.write() for operational details.
   */
  public void write(String data) {
    int index = 0;
    while (index < COCmdClientCount) {
      COCmdClients[index].write(data);
      if (COCmdClients[index].active()) {
        index++;
      } else {
        removeIndex(index);
      }
    }
  }

	


  /**
   * General error reporting, all corraled here just in case
   * I think of something slightly more intelligent to do.
   */
//  public void errorMessage(String where, Exception e) {
//    parent.die("Error inside Server." + where + "()", e);
//    //System.err.println("Error inside Server." + where + "()");
//    //e.printStackTrace(System.err);
//  }
}

/*
import processing.net.Server;
import processing.net.COCmdClient;

public class COCmdServer implements Runnable {

	protected Server _server;
	protected int _port;
	protected Thread _thread;
	
	volatile boolean klaar = false;
*/	
	/*
	 * Zet poort en start main worker thread
	 */

/*
	COCmdServer(int port) {
		this._port = port;
		this._thread = new Thread(this);
		this._thread.start();
	}
	
	public void run() {
		this._server = new Server(null, this._port);
		
		while (!klaar) {
			 COCmdClient thisCOCmdClient = this._server.available();		
			 if (thisCOCmdClient !=null) {
				    String whatCOCmdClientSaid = thisCOCmdClient.readString();
				    if (whatCOCmdClientSaid != null) {
				      System.out.println(thisCOCmdClient.ip() + "t" + whatCOCmdClientSaid);
				    } 
				  } 			
		}
		// Set up server
		// Wait for COCmdClient
		// Handl cient
		// Disconnect COCmdClient
		
	}
}

*/