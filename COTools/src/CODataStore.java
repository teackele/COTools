import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class CODataStore extends Hashtable {
	public String _serverUrl;
	public String sessionId;
//	final ObjectMapper mapper = new ObjectMapper();

	
	CODataStore() {
		this._serverUrl = "http://oklahoma/new/";
		
		this.sessionId = "Teackele";
		this.fetch();
		
		COData x = new COData();
		x.name = "fiets";
		x.value = "raleigh";
		x.type = "img";
		
		this.put("fiets", x);
		
		
		this.write();

		
		
	}
	
	public void setSessionId(String aSessionId) {
		if (aSessionId != this.sessionId) {
			this.sessionId = aSessionId;
			// reset data??
		}
	}
		
	public Object get(String key){
		// see if we have it
		Object found = super.get(key);

		if (found != null) {
			return found;
		} 

		//try to fetch it if we don't have it
		
		this.fetch((String)key);
		return super.get(key);
	}
	/*
	public Object put(Object key, Object value) {
		Object found = super.get(key);
		
		if (found != null){
			System.out.println("fgsdfg");
			
		}
		
		

		return super.put(key, value);
		
		
		
	}
	*/
	/********************* reading ***************/
	
	public void fetch() {
		this.fetch("");
	}
	
	public void fetch(String key) {
		String url = this._serverUrl + "/COKey/get/" + this.sessionId + "/" + key;
//		InputStream src = this.createInput("http://oklahoma/new/COKey/get/Teackele");
		InputStream src = this.createInput(url);

		JsonReader reader;
		try {
			reader = new JsonReader(new InputStreamReader(src, "UTF-8"));
	    	Gson gson = new Gson();
	        COData data[] = gson.fromJson(reader, COData[].class);
	        
	        for (int i=0; i<data.length; i++) {
	        	this.put(data[i].name, data[i]);
	        }
	        System.out.println(data.length);
	        
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
		
	public InputStream createInput(String filename) {
		InputStream stream;
		
	    try {
	          URL url = new URL(filename);
	          stream = url.openStream();
	          return stream;

	    } catch (MalformedURLException mfue) {
	          // not a url, that's fine

        } catch (FileNotFoundException fnfe) {
	          // Java 1.5 likes to throw this when URL not available. (fix for 0119)
	          // http://dev.processing.org/bugs/show_bug.cgi?id=403

        } catch (IOException e) {
	          // changed for 0117, shouldn't be throwing exception
	          e.printStackTrace();
	          //System.err.println("Error downloading from URL " + filename);
	          return null;
	          //throw new RuntimeException("Error downloading from URL " + filename);
        }
	    return null;
	}
	
	
	
	/**************** writing   **************/
	
	public void write() {
		String url = this._serverUrl + "/COKey/put/" + this.sessionId ;
		
		// Hashtable to array
		COData[] data = new COData[this.size()];
		
		data = (COData[]) this.values().toArray(data);
		
		// array to json
		Gson gson = new Gson();
		String json = gson.toJson(data);  

		System.out.println(json);
		
		// post to server
		String stat =  CODataStore.excutePost(url,  "data=" + json);
		System.out.println(stat);
		
		
	}
	
	public static String excutePost(String targetURL, String urlParameters)
	{
		URL url;
		HttpURLConnection connection = null;  
		try {
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", 
					"application/x-www-form-urlencoded");
	
			connection.setRequestProperty("Content-Length", "" + 
					Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");  
	
			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
	
			//Send request
			DataOutputStream wr = new DataOutputStream (
					connection.getOutputStream ());
			wr.writeBytes (urlParameters);
			wr.flush ();
			wr.close ();
	
			//Get Response	
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
	
		} catch (Exception e) {
	
			e.printStackTrace();
			return null;
	
		} finally {
	
			if(connection != null) {
				connection.disconnect(); 
			}
		}
	}

	public class COData {
		String name;
		String type;
		String value;
		
		public boolean equals(Object o) {
			COData x = (COData) o;
			
			if (x.name == this.name) {
				return true;
			}
			return false;
		}
		

		
	}

}

