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
	
	//Get single Replica ID
	public  String getReplicasID(int replicaN) {
		return spaceRemoval(props.getProperty("zermia.replica.ID." + replicaN));
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
	
	//Get single Client ID
	public String getClientsId(int clientN) {
		return spaceRemoval(props.getProperty("zermia.client.ID." + clientN));
	}
	
//-------------------------------------------------------------------------//	
	
	//Get Coordinator IP address
	public String getOrchestratorIP() {
		return spaceRemoval(props.getProperty("zermia.orchestrator.ip"));
	}	
	
	//Get Coordinator Port
	public int getOrchestratorPort() {
		return Integer.parseInt(spaceRemoval(props.getProperty("zermia.orchestrator.port")));
	}
	
//-------------------------------------------------------------------------//
	
	public Boolean getPrioritySort() {
		return Boolean.valueOf(props.getProperty("zermia.prioritySort"));
	}
	
//-------------------------------------------------------------------------//	
	//viewchange
	public Boolean getVC() {
		return Boolean.valueOf(props.getProperty("zermia.message.ViewChange"));
	}
	//checkpoint
	public Boolean getCk() {
		return Boolean.valueOf(props.getProperty("zermia.message.ViewChange"));
	}
	//consensus?
	public Boolean getCs() {
		return Boolean.valueOf(props.getProperty("zermia.message.ConsensusAll"));
	}
	//forwarded messages to Primary
	public Boolean getFw() {
		return Boolean.valueOf(props.getProperty("zermia.message.Forwarded"));
	}
	
//-------------------------------------------------------------------------//	
	//focus attacks solely on primary
	public Boolean getFprimary() {
		return Boolean.valueOf(props.getProperty("zermia.message.focus.primary"));
	}
	
	public Boolean getCreply() {
		return Boolean.valueOf(props.getProperty("zermia.message.ClientReply"));
	}
	
//-------------------------------------------------------------------------//	
	//consensus type messages
	public Boolean getCsPrP() {
		return Boolean.valueOf(props.getProperty("zermia.message.PrePrepare"));
	}
	
	public Boolean getCsPr() {
		return Boolean.valueOf(props.getProperty("zermia.message.Prepare"));
	}
	
	public Boolean getCsCm() {
		return Boolean.valueOf(props.getProperty("zermia.message.Commit"));
	}
}
