package zermia.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Properties;

public class ZermiaMessageProperties {
	Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
	String propPath = path + "/ConfigurationProperties/faultyReplicas.properties";
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
	//consensus type messages
	public String getFaultyReplicaMessagesType(Integer replicaID) {
		return props.getProperty("Zermia.faulty.replica." + replicaID);
	}
	
	public Boolean getFocusPrimary(Integer replicaID) {
		return Boolean.valueOf(props.getProperty("Zermia.faulty.replica.focusPrimary." + replicaID));
	}
	
}
