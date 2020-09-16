package zermia.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

public class ZermiaRuntimeProperties {
	Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
	String propPath = path + "/zermiaRuntime.properties";  //verificar isto se posso usar a mesma pasta
	static Properties props = new Properties();	
	
	//Simple property file loader
	public void loadProperties() {
		try {
			props.load(new FileInputStream(propPath));
		} catch (FileNotFoundException e) {
			System.out.println("Property File not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error File Properties");
			e.printStackTrace();
		}
	}
	
	public String spaceRemoval(String st) {
		return st.replaceAll("\\s+","");
	}
	
	
//-------------------------------------------------------------------------//	

	//Get Orchestrator IP address
	public String getOrchestratorIP() {
		return spaceRemoval(props.getProperty("zermia.orchestrator.ip"));
	}	
		
	//Get Orchestrator Port
	public int getOrchestratorPort() {
		return Integer.parseInt(spaceRemoval(props.getProperty("zermia.orchestrator.port")));
	}
	
}