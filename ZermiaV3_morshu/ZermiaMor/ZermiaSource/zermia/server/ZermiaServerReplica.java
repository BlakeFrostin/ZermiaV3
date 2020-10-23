package zermia.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.TreeSet;

public class ZermiaServerReplica {
	String replicaID;
	String replicaIP;
	Boolean faultness = false;
	
	LinkedList<ArrayList <String>> faultPamList = new LinkedList<ArrayList <String>>();
	LinkedList<ArrayList <String>> runTriggerList = new LinkedList<ArrayList <String>>();
	ArrayList <Integer> iteratorList = new ArrayList <Integer>();
	
	Integer messagesSentTotal = 0;
	double timeFinish = 0; 
	
//----------------------------------------------------------------------------------//	
	//New lists
	public LinkedList<ArrayList <String>> getFaultPamList() {
		return faultPamList;
	}
	public LinkedList<ArrayList <String>> getRunsTriggerList() {
		return runTriggerList;
	}
	
	public ArrayList <Integer> getIteratorList(){
		return iteratorList;
	}
	
//----------------------------------------------------------------------------------//
	
	public ZermiaServerReplica (String rep_id, String rep_ip) {
		replicaID = rep_id;
		replicaIP = rep_ip;
	}

//----------------------------------------------------------------------------------//
	
	public String getID() {
		return replicaID;
	}
	
	public String getIP() {
		return replicaIP;
	}
	
	public Boolean getFaultness() {
		return faultness;
	}
	
	public void setFaultness(Boolean faultyOrNot) {
		faultness = faultyOrNot;
	}
	
	public void setMessagesSentTotal(Integer messagesTotal) {
		messagesSentTotal = messagesTotal;
	}

	public Integer getMessagesSentTotal() {
		return messagesSentTotal;
	}
	
	public void setTimeFinish(double timeEnd) {
		timeFinish = timeEnd;
	}
	
	public double getTimeFinish() {
		return timeFinish;
	}
//----------------------------------------------------------------------------------//

	public Integer currentRunDiff(Integer currentRun) {
		NavigableSet<Integer> values = new TreeSet<Integer>();
		for (int ka = 0; ka<runTriggerList.size(); ka++) {
			values.add(Integer.parseInt(runTriggerList.get(ka).get(0)));
		}
		//Integer lower = values.floor(currentRun);
		Integer higher = values.ceiling(currentRun);

		return higher;
	}
}
