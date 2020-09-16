package zermia.properties;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ZermiaCodeClient {

	public String getClientCode() throws IOException{
		Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
		String propPath = path + "/ConfigurationProperties/ClientCodeStart.txt";
		
		FileInputStream fstream = new FileInputStream(propPath);
		DataInputStream inp = new DataInputStream(fstream);	
		BufferedReader buffRead = new BufferedReader(new InputStreamReader(inp));
		String strLine;
		String clientCode = null;
		
		while ((strLine = buffRead.readLine()) != null)   {	
		clientCode = strLine;
		}
		buffRead.close();
		
		boolean hasZermia = false;
		for (String word : clientCode.split("\\s+")) {
		  if (word.toLowerCase().equals("zermia")) {
			Logger.getLogger(ZermiaCodeClient.class.getName()).log(Level.INFO, "Client Code Obtained with success");  
			hasZermia= true;
		    break;
		  } 
		}
		
		if(hasZermia == false) {
			Logger.getLogger(ZermiaCodeClient.class.getName()).log(Level.INFO, "Client Code without Zermia(Client number parameter) in it, Shutting down");
			System.exit(1);
		}
	
	return clientCode;
	}
}
