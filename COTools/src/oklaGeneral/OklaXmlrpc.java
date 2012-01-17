package oklaGeneral;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import processing.core.PApplet;
import processing.core.PImage;

public class OklaXmlrpc {
	PApplet pApplet;
	boolean workOffline = false;
	XmlRpcClient xmlrpcClient;
	XmlRpcClientConfigImpl config;
	int sesId=-1;

	public OklaXmlrpc(PApplet pApplet, boolean workOffline) {
		super();
		this.pApplet = pApplet;
		this.workOffline = workOffline;
		xmlrpcClient = new XmlRpcClient();
		config= new XmlRpcClientConfigImpl();
	}
	
	public void startClient() {
		xmlrpcClient.setConfig(config);
	}
	
	public boolean setServer(String s) {
		try {
			config.setServerURL(new URL(s));
		} 
		catch (MalformedURLException e) {
			return false;
	}
		return true;
	}
	
	public void setUsername(String u) {
		config.setBasicUserName(u);
	}
	
	public void setPassword(String pwd) {
		config.setBasicPassword(pwd);
	}

	public int getSessionId(String rfidStr) {
		if (workOffline)
			return (1);
		else {
			Object answer = null;
			String[] comArray = new String[1];
			comArray[0] = rfidStr;
			println("command send:" + comArray[0]);
			try {
				answer = xmlrpcClient.execute("load_session", comArray);
			} catch (Exception ex) {
				println("xmlrpcGetSessionId: Exception:" + ex);
				String[] m1 = PApplet.match(ex.toString(), "token not found");
				if (m1 != null)
					return (0);
				return (-1);
			}

			if (answer == null) {
				println("xmlrpcGetSessionId:No answer");
				return (-1);
			}

			if (answer instanceof org.apache.xmlrpc.XmlRpcException) {
				String[] m1 = PApplet.match(answer.toString(),
						"token not found");
				if (m1 != null)
					return (0);
				else
					return (-1);
			} else if (answer instanceof Integer) {
				// tag already in system
				sesId = (Integer) answer;
				println("Known session: tag=" + rfidStr + " Sessionid= "
						+ sesId);
				
				return (sesId);
			}
			return (-1);
		}
	}

	public boolean setKey(String theKey, Object theValue) {
		if (workOffline)
			return (true);
		else {
			Object[] request = new Object[3];
			request[0] = PApplet.str(sesId);
			request[1] = theKey;
			request[2] = theValue;
			println("xmlrpcSetKey: send request :" + request[0] + " "
					+ request[1] + " " + request[2]);

			Object answer = null;

			try {
				answer = xmlrpcClient.execute("set_key", request);
			}
			/*
			 * catch (org.apache.xmlrpc.XmlRpcException e) {
			 * println("xmlrpcSetKey: xmlrpcexception server may not exist: " +
			 * e); return(false); }
			 */

			catch (Exception e) {
				println("xmlrpcSetKey: exception: " + e);
				return (false);
			}

			if (answer instanceof org.apache.xmlrpc.XmlRpcException) {
				println("xmlrpcSetkey: error from request:" + request);
				return (false);
			}

			println("xmlrpcSetKey: Answer:" + answer);
			return (true);
		}
	}

	public boolean setImage(String theKey, PImage img, String imgType) {
		if (workOffline)
			return (true);
		else {
			println("xmlrpcSetImage:" + theKey);
			String fName = "data/xmlrpcimage/" + pApplet.str(sesId) + "set"
					+ theKey + "." + imgType;
			img.save(fName);
			byte[] b = pApplet.loadBytes(fName);
			Object[] request = new Object[4];
			request[0] = pApplet.str(sesId);
			request[1] = theKey;
			request[2] = b;
			request[3] = "image/" + imgType;
			println("xmlrpcSetKey: send request :" + request);

			Object answer = null;

			try {
				answer = xmlrpcClient.execute("set_key", request);
			} catch (Exception e) {
				println("xmlrpcSetKey: exception: " + e);
				return (false);
			}

			if (answer instanceof org.apache.xmlrpc.XmlRpcException) {
				println("xmlrpcSetkey: error from request:" + request);
				return (false);
			}

			println("xmlrpcSetKey: Answer:" + answer);
			return (true);
		}
	}

	public String value(String theKey) {
		if (workOffline)
			return ("testValue");
		else {
			String[] request = new String[2];
			request[0] = PApplet.str(sesId);
			request[1] = theKey;
			println("xmlrpcValue: send request :" + request[0] + " "
					+ request[1]);

			Object answer = null;

			try {
				answer = xmlrpcClient.execute("value", request);
			} catch (Exception e) {
				println("xmlrpcSetKey exception: " + e);
				return ("error");
			}

			if (answer instanceof org.apache.xmlrpc.XmlRpcException) {
				println("xmlrpcValue: error from request:" + request);
				return ("error");
			}

			println("xmlrpcValue: Answer=" + answer);
			String answerStr = (String) answer;
			return (answerStr);
		}
	}

	public PImage xmlrpcGetImage(String theKey, String imgType) {
		if (workOffline)
			return (null);
		else {
			println("xmlrpcGetImage:" + theKey);
			String[] request = new String[2];
			request[0] = PApplet.str(sesId);
			request[1] = theKey;
			println("xmlrpcValue: send request :" + request[0] + " "
					+ request[1]);

			Object answer = null;
			byte[] b = null;

			try {
				answer = xmlrpcClient.execute("value", request);
			} catch (Exception e) {
				println("xmlrpcSetKey exception: " + e);
				return (null);
			}

			if (answer instanceof org.apache.xmlrpc.XmlRpcException) {
				println("xmlrpcValue: error from request:" + request);
				return (null);
			}
			String fName = "data/xmlrpcimage/" + PApplet.str(sesId) + "get"
					+ theKey + "." + imgType;
			pApplet.saveBytes(fName, b);
			PImage img;
			img = pApplet.loadImage("fName");
			return (img);
		}
	}

	private void println(String s) {
		PApplet.println(s);
	}

	public boolean isWorkOffline() {
		return workOffline;
	}

	public void setWorkOffline(boolean workOffline) {
		this.workOffline = workOffline;
	}

	public int getSesId() {
		return sesId;
	}

	public void setSesId(int sesId) {
		this.sesId = sesId;
	}
	

}
