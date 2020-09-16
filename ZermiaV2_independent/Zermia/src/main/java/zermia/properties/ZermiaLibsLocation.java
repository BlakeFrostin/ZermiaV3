package zermia.properties;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class ZermiaLibsLocation {
	
	public static String getLibs() throws IOException{
		Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
		String propPath = path + "/ConfigurationProperties/NecessaryLibs.txt";
		
		FileInputStream fstream = new FileInputStream(propPath);
		DataInputStream inp = new DataInputStream(fstream);	
		BufferedReader buffRead = new BufferedReader(new InputStreamReader(inp));
		String strLine;
		String clientCode = null;
		
		while ((strLine = buffRead.readLine()) != null)   {	
		clientCode = strLine;
		}
		buffRead.close();
		
	return clientCode;
	}
}
