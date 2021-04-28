package zermia.runtime;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import zermia.properties.ZermiaRuntimeProperties;
import zermia.runtime.proto.ZermiaRuntimeStub;

public class ZermiaRuntime {
	public static ZermiaRuntime zermiaRuntime = new ZermiaRuntime();
	protected ReentrantLock z_lock = new ReentrantLock();
	ZermiaRuntimeStub replicaStub = new ZermiaRuntimeStub();
	
	static ZermiaRuntimeProperties props = new ZermiaRuntimeProperties();
	
	static ArrayList<ArrayList<String>> replyArray = new ArrayList<ArrayList<String>>();
	static ArrayList<ArrayList<String>> replyArray2 = new ArrayList<ArrayList<String>>();
	
	static ArrayList<ArrayList<String>> faultPamList = new ArrayList<ArrayList<String>>();
	static ArrayList<ArrayList<String>> runTriggerList = new ArrayList<ArrayList<String>>();
	
	String replicaID;
	Boolean replicaFaultState = false;
	Boolean listIncreaseOnce = true;
	Integer currentPrimary;
	Integer regencyN; //Next reg
	Integer regencyO;
	
	Integer runTrigSum = 0;
	Integer runStart = 0;
	Integer runTriggers = 0;
	volatile Integer runListIterator = 0;
	
	Integer faultScheduleSize = 0;
	volatile Integer numberOfMessagesSent = 0;
	volatile Integer numberOfMessages1secSent = 0;
	Integer numberConsensusRounds = 0;
	Integer faultGroupSize;
	
	double timeFinish;
	Integer timeSec=0;
	
//----Messages to attack-----//
	Boolean Vc = false;	//view change
	Boolean Ck = false;	//checkpoint
	//Boolean Cs = false;	//All consensus type of messages
	Boolean Fw = false;	//forwareded messages to primary
	Boolean Creply = false; // messages sent to client, reply
	
	Boolean CsPrP = false; // PrePrepare
	Boolean CsPr = false; // Prepare
	Boolean CsCm = false; // Commit
	
//---Focus------//
	Boolean Fprimary = false; //attacks focused on the primary, target = primary
	
//--FaultyReplicasList--//
	static ArrayList<String> fRepList = new ArrayList<String>();
	
	
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
	
//----------------------------------------------------------------------------------//
	
	public void setReplicaFaultState(Boolean replicaFState) {
		replicaFaultState = replicaFState;
	}
	
	public Boolean getReplicaFaultState() {
		return replicaFaultState;
	}
	
//----------------------------------------------------------------------------------//	
	
	public Integer getRunTrigSum() {
		return	runTrigSum;		
	}
	
	public void setRunTrigSum(Integer runt) {
		runTrigSum = runt;
	}
	
//----------------------------------------------------------------------------------//	
	public Integer getRunStart() {
		return runStart;
	}
	
	public void setRunStart(Integer run) {
		runStart=run;
	}
	
//----------------------------------------------------------------------------------//	
	
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
	
	public ArrayList<ArrayList<String>> getRunTriggerList(){
		return runTriggerList;
	}

//----------------------------------------------------------------------------------//	
	
	public void setFaultScheduleSize(Integer faultIterator) {
		faultScheduleSize = faultIterator;
	}
	
	public Integer getFaultScheduleSize() {
		return faultScheduleSize;
	}
	
//----------------------------------------------------------------------------------//	
	
	public void increaseNumberOfMessagesSent() {
		numberOfMessagesSent = numberOfMessagesSent + 1;
	}
	
	public Integer getNumberOfMessagesSent() {
		return numberOfMessagesSent;
	}
	
	public void increaseNumberOfMessageSentFlood(Integer floodtimes) {
		numberOfMessagesSent = numberOfMessagesSent + floodtimes;
	}
	
//----------------------------------------------------------------------------------//		
	
	public void increaseNumberOfMessages1secSent() {
		numberOfMessages1secSent = numberOfMessages1secSent + 1;
	}
	
	public Integer getNumberOfMessages1secSent() {
		return numberOfMessages1secSent;
	}
	
	public void setNumberOfMessages1secSent() {
		numberOfMessages1secSent = 0;
	}
	
	public void increaseNumberOfMessage1secSentFlood(Integer floodtimes) {
		numberOfMessages1secSent = numberOfMessages1secSent + floodtimes;
	}
	
//----------------------------------------------------------------------------------//	
	
	public void setTimeFinished(double time) {
		timeFinish = time;
	}
	
	public Integer getConsensusRoundsFinish() {
		return numberConsensusRounds;
	}

//----------------------------------------------------------------------------------//	
	
	public Integer getGroupSize() {
		return faultGroupSize;
	}
	
	
//----------------------------------------------------------------------------------//	
	
	public Boolean getVc() {
		return Vc;
	}
	
	public Boolean getCk() {
		return Ck;
	}	
	
//	public Boolean getCs() {
//		return Cs;
//	}
	
	public Boolean getFw() {
		return Fw;
	}
	
	public Boolean getFprimary() {
		return Fprimary;
	}
	
	public Boolean getCreply() {
		return Creply;
	}
	
	public Boolean getCsPrP() {
		return CsPrP;
	}
	
	public Boolean getCsPr() {
		return CsPr;
	}
	
	public Boolean getCsCm() {
		return CsCm;
	}
	
//----------------------------------------------------------------------------------//	
	
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
		numberConsensusRounds=props.getEndTestRounds();
		
		z_lock.lock();
		replyArray = (ArrayList<ArrayList<String>>) replicaStub.runtimeFirstConnection(replicaID).clone();
		replicaFaultState = Boolean.valueOf(replyArray.get(0).get(0));
		faultScheduleSize = Integer.valueOf(replyArray.get(1).get(0));
		fRepList.addAll(replyArray.get(2));
		faultGroupSize = Integer.parseInt(replyArray.get(3).get(0));
		z_lock.unlock();
		
		if(replicaFaultState.equals(true)){
			Logger.getLogger(ZermiaRuntime.class.getName()).log(Level.INFO, "Replica " + replicaID + " is FAULTY");		
		} else {
			Logger.getLogger(ZermiaRuntime.class.getName()).log(Level.INFO, "Replica " + replicaID + " is CORRECT");		
		}
		
		if(replicaFaultState) {
			z_lock.lock();
			messageInfo();
			z_lock.unlock();
		}
		
	}
//----------------------------------------------------------------------------------//
	//sending message to server to get which messages to inject faults
	@SuppressWarnings("unchecked")
	public void messageInfo() {
		ArrayList<Boolean> messageInfoMsg = new ArrayList<Boolean>();
		
		messageInfoMsg = (ArrayList<Boolean>) replicaStub.runtimeSecondConnection(replicaID).clone();
		//System.out.println("teste " + messageInfoMsg);
		
		Vc = messageInfoMsg.get(0);	//Viewchange
		Ck = messageInfoMsg.get(1);	//Checkpoint
		Fw = messageInfoMsg.get(2); //forwarded
		
		Fprimary = messageInfoMsg.get(3); //primary focused attack
		Creply = messageInfoMsg.get(4); //Client reply
		
		CsPrP = messageInfoMsg.get(5);
		CsPr = messageInfoMsg.get(6);
		CsCm = messageInfoMsg.get(7);
		
		
		//System.out.println("teste " + messageInfoMsg.size());
		
		Logger.getLogger(ZermiaRuntime.class.getName()).log(Level.INFO, "Message information passed");		
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
	
//----------------------------------------------------------------------------------//	
	
	public void statsSend(){
		z_lock.lock();
		replicaStub.runtimeStatsService(replicaID, timeFinish, numberOfMessagesSent);
		z_lock.unlock();
	}

//----------------------------------------------------------------------------------//	
	
	public void statSend1sec() {
		z_lock.lock();
		timeSec = timeSec + 1;
		replicaStub.runtimeStat1SecService(replicaID, timeSec ,numberOfMessages1secSent);
		numberOfMessages1secSent = 0;
		z_lock.unlock();
	}
		
//----------------------------------------------------------------------------------//	
	//For focused attacks on primary target = primary
	public void setCurrentPrimary(Integer primary) {
		currentPrimary = primary;
	}
	
	public Integer getCurrentPrimary() {
		return currentPrimary;
	}
	
	public Boolean amIthePrimary() {
		if(Integer.parseInt(replicaID) == currentPrimary) {
			return true;
		} else return false;
	}
	
	public void setNextRegency(Integer regency) {
		regencyN=regency; 
	}
	
	public Integer getNextRegency() {
		return regencyN;
	}
	
	public void setOldRegency(Integer regency) {
		regencyO=regency;
	}
	
	public Integer getOldRegency() {
		return regencyO;
	}
	
//----------------------------------------------------------------------------------//
	
	public Boolean checkFaultyReplicas(String rep) {
		if(fRepList.contains(rep)) {
			return true;
		} else return false;
	}
	
	
}
