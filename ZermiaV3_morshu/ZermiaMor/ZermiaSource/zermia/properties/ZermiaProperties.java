package zermia.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;

public class ZermiaProperties {
	Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
	String propPath = path + "/ConfigurationProperties/zermia.properties";
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
	
	// Get Zermia Server Uptime	
	public  int getServerUptime() {
		return Integer.parseInt(spaceRemoval(props.getProperty("zermia.serverUptime")));
	}	
//-------------------------------------------------------------------------//
	
	// Get number of replicas	
	public int getNumberOfReplicas() {
		return Integer.parseInt(spaceRemoval(props.getProperty("zermia.numberOfReplicas")));
	}	
	
	// Get replicas ID 
	public ArrayList<String> getReplicasID() {
		int numReplicas = getNumberOfReplicas();
		ArrayList<String> replicasID = new ArrayList<String>();
		for(int i = 0; i < numReplicas; i++) {
			replicasID.add(spaceRemoval(props.getProperty("zermia.replica.ID." + i)));
		}
		return replicasID;
	}
	
	// Get replicas IP address
	public ArrayList<String> getReplicasIP() {
		int numReplicas = getNumberOfReplicas();
		ArrayList<String> replicasIP = new ArrayList<String>();
		
		for(int i = 0; i < numReplicas; i++) {
			replicasIP.add(spaceRemoval(props.getProperty("zermia.replica.IP." + i)));
		}
		return replicasIP;
	}
	
	// Get single Replica Ip
	public  String getReplicasIp(int replicaN) {
		return spaceRemoval(props.getProperty("zermia.replica.IP." + replicaN));
	}
	
	//Get single Replica ID
	public  String getReplicasID(int replicaN) {
		return spaceRemoval(props.getProperty("zermia.replica.ID." + replicaN));
	}
	
//-------------------------------------------------------------------------//
	
	public Integer getNumberOfAvailableReplicas() {
		return Integer.parseInt(spaceRemoval(props.getProperty("zermia.numberOfAvailableReplicas")));
	}
	
	public Integer getNumberOfAvailableClients() {
		return Integer.parseInt(spaceRemoval(props.getProperty("zermia.numberOfAvailableClients")));
	}
	
//-------------------------------------------------------------------------//
	
	// Get number of Clients	
	public  int getNumberOfClients() {
		return Integer.parseInt(spaceRemoval(props.getProperty("zermia.numberOfClients")));
	}		
	
	// Get Clients ID 
	public ArrayList<String> getClientsID() {
		int numReplicas = getNumberOfReplicas();
		ArrayList<String> ClientsID = new ArrayList<String>();
		for(int i = 0; i < numReplicas; i++) {
			ClientsID.add(spaceRemoval(props.getProperty("zermia.client.ID." + i)));
		}
		return ClientsID;
	}	
	
	// Get Clients IP address
	public ArrayList<String> getClientsIP() {
		int numReplicas = getNumberOfReplicas();
		ArrayList<String> ClientsIP = new ArrayList<String>();
		for(int i = 0; i < numReplicas; i++) {
			ClientsIP.add(spaceRemoval(props.getProperty("zermia.client.ID." + i)));
		}
		return ClientsIP;
	}
	
	//Get single Client ID
	public String getClientsId(int clientN) {
		return spaceRemoval(props.getProperty("zermia.client.ID." + clientN));
	}
	
	//Get single Client IP
	public String getClientsIp(int clientN) {
		return spaceRemoval(props.getProperty("zermia.client.IP." + clientN));
	}	
	
//-------------------------------------------------------------------------//		
	//Time for client boots after server starts
	public Integer getClientBootTime() {
		return Integer.parseInt(spaceRemoval(props.getProperty("zermia.clientBooting")));
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
	
	public String getWorkingDir() {
		return props.getProperty("zermia.workingdir");
	}
	
//-------------------------------------------------------------------------//	
	
	public Boolean getPrioritySort() {
		return Boolean.valueOf(props.getProperty("zermia.prioritySort"));
	}
}
