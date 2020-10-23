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

public class ZermiaCodeReplica {
	
	public String getReplicaCode() throws IOException{
		Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
		String propPath = path + "/ConfigurationProperties/ReplicaCodeStart.txt";
		
		FileInputStream fstream = new FileInputStream(propPath);
		DataInputStream inp = new DataInputStream(fstream);	
		BufferedReader buffRead = new BufferedReader(new InputStreamReader(inp));
		String strLine;
		String replicaCode = null;
		
		while ((strLine = buffRead.readLine()) != null)   {	
			replicaCode = strLine;
			}
			buffRead.close();
			
			boolean hasZermia = false;
			for (String word : replicaCode.split("\\s+")) {
			  if (word.toLowerCase().equals("zermia")) {
				Logger.getLogger(ZermiaCodeClient.class.getName()).log(Level.INFO, "Replica Code Obtained with success");  
				hasZermia= true;
			    break;
			  } 
			}
			
			if(hasZermia == false) {
				Logger.getLogger(ZermiaCodeClient.class.getName()).log(Level.INFO, "Replica Code without Zermia(Client number parameter) in it, Shutting down");
				System.exit(1);
			}
		
		return replicaCode;
	}
}
