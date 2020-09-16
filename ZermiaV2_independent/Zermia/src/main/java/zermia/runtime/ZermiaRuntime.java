package zermia.runtime;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ZermiaRuntime {
	public static ZermiaRuntime zermiaRuntime = new ZermiaRuntime();
	protected ReentrantLock z_lock = new ReentrantLock();
	ZermiaRuntimeStub replicaStub = new ZermiaRuntimeStub();
	
	String replicaID;
	Boolean replicaFaultState = false;
	Boolean connectionStatus = false;
	static ArrayList<Boolean> replyArray = new ArrayList<Boolean>();
	static ArrayList<ArrayList<String>> replyArray2 = new ArrayList<ArrayList<String>>();
	
	 static ArrayList<String> faultPamList = new ArrayList<String>();
	 static ArrayList<String> runTriggerList = new ArrayList<String>();
	
	 Integer runTrigSum = 0;
	 Integer runStart = 0;
	 Integer runTriggers = 0;
	
	
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
	
	public Integer getRunStart() {
		return runStart;
	}
	
	public Integer getRunTriggers() {
		return runTriggers;
	}
//----------------------------------------------------------------------------------//
	
	public ArrayList<String> getFaultPamList(){
		return faultPamList;
	}
	
	
//----------------------------------------------------------------------------------//	

	@SuppressWarnings("unchecked")
	public void InstanceStart() {
		Logger.getLogger(ZermiaRuntime.class.getName()).log(Level.INFO, "Starting Runtime on replica : " + replicaID);
		
		z_lock.lock();
		replyArray = (ArrayList<Boolean>) replicaStub.runtimeFirstConnection(replicaID).clone();
		connectionStatus = replyArray.get(0);
		replicaFaultState = replyArray.get(1);
		z_lock.unlock();
		if(replicaFaultState.equals(true)){
			Logger.getLogger(ZermiaRuntime.class.getName()).log(Level.INFO, "Replica " + replicaID + " is FAULTY");		
		} else {
			Logger.getLogger(ZermiaRuntime.class.getName()).log(Level.INFO, "Replica " + replicaID + " is CORRECT");		
		}
		
		if(connectionStatus.equals(false)) {
			Logger.getLogger(ZermiaRuntime.class.getName()).log(Level.INFO, "Replica " + replicaID + " is not in the replica List of ZermiaServer \\n" + " ************ Shutting down in 10 seconds ************");
			try {
				Thread.sleep(10000);
				System.exit(-1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
//----------------------------------------------------------------------------------//	
	@SuppressWarnings("unchecked")
	public void faultScheduler(Integer run) {
		 faultPamList.clear();
		 runTriggerList.clear();
		 replyArray2.clear();
		
		z_lock.lock();
		replyArray2 = (ArrayList<ArrayList<String>>) replicaStub.runtimeFaultActivation(replicaID,run).clone();
		faultPamList.addAll(replyArray2.get(0));
		runTriggerList.addAll(replyArray2.get(1));
		runStart = Integer.parseInt(runTriggerList.get(0));
		runTriggers = Integer.parseInt(runTriggerList.get(1));
		runTrigSum = runStart + runTriggers;
		z_lock.unlock();
		
		if(replyArray2.get(0).get(0).equals("false")) {
			replicaFaultState = false;
		}	
	}	
}
