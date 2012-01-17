package COTools;

import java.io.*;
import java.util.Hashtable;

public class COConfig extends Hashtable {

	COConfig() {
	}

	public void read(String filename) {
		{
			try {
				// Open the file that is the first
				// command line parameter
				FileInputStream fstream = new FileInputStream("textfile.txt");
				// Get the object of DataInputStream
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				String strLine;
				// Read File Line By Line
				while ((strLine = br.readLine()) != null) {
					// Print the content on the console
					System.out.println(strLine);
				}
				// Close the input stream
				in.close();
			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
	}

}
