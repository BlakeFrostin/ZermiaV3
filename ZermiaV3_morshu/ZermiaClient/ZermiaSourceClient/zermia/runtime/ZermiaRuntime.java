package zermia.runtime;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import zermia.properties.ZermiaRuntimeProperties;

public class ZermiaRuntime {
	public static ZermiaRuntime zermiaRuntime = new ZermiaRuntime();
	protected ReentrantLock z_lock = new ReentrantLock();
	ZermiaRuntimeStub replicaStub = new ZermiaRuntimeStub();
	
	static ZermiaRuntimeProperties props = new ZermiaRuntimeProperties();
	
	static ArrayList<String> replyArray = new ArrayList<String>();
	static ArrayList<ArrayList<String>> replyArray2 = new ArrayList<ArrayList<String>>();
	
	static ArrayList<ArrayList<String>> faultPamList = new ArrayList<ArrayList<String>>();
	static ArrayList<ArrayList<String>> runTriggerList = new ArrayList<ArrayList<String>>();
	
	
	String replicaID;
	Boolean replicaFaultState = false;
	Boolean connectionStatus = false;
	Boolean listIncreaseOnce = true;
	
	Integer runTrigSum = 0;
	Integer runStart = 0;
	Integer runTriggers = 0;
	volatile Integer runListIterator = 0;
	
	Integer faultScheduleSize = 0;
	volatile Integer numberOfMessagesSent = 0;
	Integer numberConsensusRounds = 0;
	
	double timeFinish;
//----------------------------------------------------------------------------------//
	
	public ZermiaRuntime(String repID) {
		replicaID = repID;
	}
	
	public ZermiaRuntime() {
	}
	
	public static ZermiaRuntime getInstance() {
		return zermiaRuntime;
	}
	
//----------------------------------------------------------------------------------//

	public void setID(String repID) {
		replicaID= repID;
	}
	
	public String getID() {
		return replicaID;
	}
	
	public void setReplicaFaultState(Boolean replicaFState) {
		replicaFaultState = replicaFState;
	}
	
	public Boolean getReplicaFaultState() {
		return replicaFaultState;
	}
	
	public void setConnectionStatus(Boolean conStatus) {
		connectionStatus = conStatus;
	}
	
	public Boolean getConnectionStatus() {
		return connectionStatus;
	}
	
	public Integer getRunTrigSum() {
		return	runTrigSum;		
	}
	
	public void setRunTrigSum(Integer runt) {
		runTrigSum = runt;
	}
	
	public Integer getRunStart() {
		return runStart;
	}
	
	public void setRunStart(Integer run) {
		runStart=run;
	}
	
	public Integer getRunTriggers() {
		return runTriggers;
	}
	
	public void setRunTrigger(Integer runTr) {
		runTriggers = runTr;
	}
//----------------------------------------------------------------------------------//
	
	public ArrayList<ArrayList<String>> getFaultPamList(){
		return faultPamList;
	}
	
	public void setFaultScheduleSize(Integer faultIterator) {
		faultScheduleSize = faultIterator;
	}
	
	public Integer getFaultScheduleSize() {
		return faultScheduleSize;
	}

	public ArrayList<ArrayList<String>> getRunTriggerList(){
		return runTriggerList;
	}
	
	
//----------------------------------------------------------------------------------//		
	public void increaseNumberOfMessagesSent() {
		numberOfMessagesSent = numberOfMessagesSent + 1;
	}
	
	public void increaseNumberOfMessageSentFlood(Integer floodtimes) {
		numberOfMessagesSent = numberOfMessagesSent + floodtimes;
	}
	
	public Integer getNumberOfMessagesSent() {
		return numberOfMessagesSent;
	}
	
	public void setTimeFinished(double time) {
		timeFinish = time;
	}
	
	public Integer getConsensusRoundsFinish() {
		return numberConsensusRounds;
	}
	
	public void increaseListIterator() {
		z_lock.lock();
		if(listIncreaseOnce) {
			runListIterator = runListIterator + 1;
			listIncreaseOnce = false;
		}
		z_lock.unlock();
	}
	
	public Integer getListIterator() {
		return runListIterator;
	}
	
	public void setBoolIterator(Boolean bo) {
		listIncreaseOnce = bo;
	}
	
//----------------------------------------------------------------------------------//	

	@SuppressWarnings("unchecked")
	public void InstanceStart() {
		Logger.getLogger(ZermiaRuntime.class.getName()).log(Level.INFO, "Starting Runtime on replica : " + replicaID);
		props.loadProperties();
		numberConsensusRounds=props.getEndTestRun();
		
		z_lock.lock();
		replyArray = (ArrayList<String>) replicaStub.runtimeFirstConnection(replicaID).clone();
		connectionStatus = Boolean.valueOf(replyArray.get(0));
		replicaFaultState = Boolean.valueOf(replyArray.get(1));
		faultScheduleSize = Integer.valueOf(replyArray.get(2));
		z_lock.unlock();
		if(replicaFaultState.equals(true)){
			Logger.getLogger(ZermiaRuntime.class.getName()).log(Level.INFO, "Replica " + replicaID + " is FAULTY");		
		} else {
			Logger.getLogger(ZermiaRuntime.class.getName()).log(Level.INFO, "Replica " + replicaID + " is CORRECT");		
		}
	}
	
//----------------------------------------------------------------------------------//	
	
	@SuppressWarnings("unchecked")
	public void faultScheduler() {
		if(replicaFaultState.equals(true)) {
			ArrayList<String> replyArrayFP = new ArrayList<String>();
			ArrayList<String> replyArrayRT = new ArrayList<String>();
			
			z_lock.lock();
			for(int i=0;i<faultScheduleSize;i++) {
				replyArray2 = (ArrayList<ArrayList<String>>) replicaStub.runtimeFaultActivation(replicaID,i).clone();
				replyArrayFP.addAll(replyArray2.get(0));
				faultPamList.add(i,replyArrayFP);
				replyArrayRT.addAll(replyArray2.get(1));
				runTriggerList.add(i,replyArrayRT);

				replyArrayFP = new ArrayList<String>();
				replyArrayRT = new ArrayList<String>();
			}
			z_lock.unlock();
			
			//for the first and unique time
			runStart = Integer.valueOf(runTriggerList.get(0).get(0));
			runTriggers = Integer.valueOf(runTriggerList.get(0).get(1));
			runTrigSum = runStart + runTriggers;
			faultScheduleSize=faultScheduleSize-1;
		}
	}
	
	public void statsSend(){
		z_lock.lock();
		replicaStub.runtimeStatsService(replicaID, timeFinish, numberOfMessagesSent);
		z_lock.unlock();
	}
	
}

