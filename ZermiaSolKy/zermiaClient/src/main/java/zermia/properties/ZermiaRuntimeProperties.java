package zermia.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

public class ZermiaRuntimeProperties {
	Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
	String propPath = path + "/zermiaRuntime.properties"; 
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
	
	//Get Coordinator IP address
	public String getCoordinatorIP() {
		return spaceRemoval(props.getProperty("zermia.coordinator.ip"));
	}	
	//Get Coordinator Port
	public Integer getCoordinatorPort() {
		return Integer.parseInt(spaceRemoval(props.getProperty("zermia.coordinator.port")));
	}	
	
	//Get how much runs the test will take in consensus rounds
	public Integer getEndTestRounds() {
		return Integer.parseInt(spaceRemoval(props.getProperty("zermia.test.run.client")));
	}
	
	//Get how much time the test will take in secs, not done, because there is no need for now
	public Integer getEndTestTime() {
		return Integer.parseInt(spaceRemoval(props.getProperty("zermia.test.time.client")));
	}
	
}
