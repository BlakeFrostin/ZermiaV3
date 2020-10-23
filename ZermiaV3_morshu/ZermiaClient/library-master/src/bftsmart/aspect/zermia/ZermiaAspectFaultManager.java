package bftsmart.aspect.zermia;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import bftsmart.communication.SystemMessage;
import bftsmart.communication.server.ServerConnection;
import bftsmart.consensus.messages.ConsensusMessage;
import zermia.faults.BigPacketFault;
import zermia.faults.CrashFault;
import zermia.faults.PacketDropperFault;
import zermia.faults.RandomPacketFault;
import zermia.faults.StackLeakFault;
import zermia.faults.ThreadDelayFault;
import zermia.faults.ZeroPacketFault;
import zermia.runtime.ZermiaRuntime;
import zermia.stats.ZermiaReplicaStats;

@Aspect
public class ZermiaAspectFaultManager {
	static volatile Integer run = 0;
	volatile boolean tdelayOnce = false ; // helper for thread delay to only execute only once instead of several times each time sendbytes is called
	volatile boolean leakOnce = false; // non-crash
	volatile boolean loadOnce = true; //test
	volatile boolean sendStatsOnce = true;
	volatile int formerRun=0;
	Integer faultArgSub; //for more specific flood attacks , 0pack or randpack
	Integer faultArgSub2; //extra for flood attacks, adding big packets
	volatile boolean kekw = true; //LoadLoop for cpu load
	
	static ZermiaReplicaStats timeStat = new ZermiaReplicaStats();
			
	class threadLoad extends Thread{  //needed for fault load, just throw anything to make some cpu waste
		public void run(){  
		while(kekw) {
			Integer wasting = 2;
			Integer twaste = 1024*1024*1024;
			while(wasting<twaste) {
				wasting = wasting * 2;
			}
		}}}
		
	@Around("execution (* bftsmart.communication.server.ServerConnection.sendBytes*(..))")
	public void advice(ProceedingJoinPoint joinPoint) throws Throwable {
		
 
		byte[] messageData = (byte[]) joinPoint.getArgs()[0];
        try {
            SystemMessage sm = (SystemMessage) (new ObjectInputStream(new ByteArrayInputStream(messageData)).readObject());
            if (sm instanceof ConsensusMessage) {
            	ConsensusMessage pm = (ConsensusMessage) sm;
            	formerRun=run; //
                run = pm.getNumber(); 
                if (pm.getNumber() > run) {
                    run = pm.getNumber();
                }
            }
        } catch (Exception ex) {
       }
        
		statsStuff();
		
        if (ZermiaRuntime.getInstance().getReplicaFaultState().equals(false)) {
            joinPoint.proceed();
            return;
        }
        
 
        if(formerRun!=run) { //helper to make sure these faults only run once per consensus round
        	tdelayOnce = true;
        	leakOnce = true;
        }
        
       if(ZermiaRuntime.getInstance().getRunTrigSum().equals(run)) {
    	   kekw = false;
    	   loadOnce = true;
	    	   if(ZermiaRuntime.getInstance().getFaultScheduleSize()>ZermiaRuntime.getInstance().getListIterator()) {
	    		   ZermiaRuntime.getInstance().increaseListIterator();
	    		   ZermiaRuntime.getInstance().setRunTrigger(Integer.parseInt(ZermiaRuntime.getInstance().getRunTriggerList().get(ZermiaRuntime.getInstance().getListIterator()).get(1)));
	    		   ZermiaRuntime.getInstance().setRunStart(Integer.parseInt(ZermiaRuntime.getInstance().getRunTriggerList().get(ZermiaRuntime.getInstance().getListIterator()).get(0)));
	    		   Integer RunSum = Integer.parseInt(ZermiaRuntime.getInstance().getRunTriggerList().get(ZermiaRuntime.getInstance().getListIterator()).get(0)) +
	    				   Integer.parseInt(ZermiaRuntime.getInstance().getRunTriggerList().get(ZermiaRuntime.getInstance().getListIterator()).get(1));
	    		   ZermiaRuntime.getInstance().setRunTrigSum(RunSum);
	    	
	    	   } else {
	    		   ZermiaRuntime.getInstance().setReplicaFaultState(false);
	    	   	}
    	   System.out.println("Iterador " + ZermiaRuntime.getInstance().getListIterator());
    	   joinPoint.proceed();
       }
       else if(ZermiaRuntime.getInstance().getRunStart() <= run && ZermiaRuntime.getInstance().getRunTrigSum()> run) {
    	   ZermiaRuntime.getInstance().setBoolIterator(true);
    	   for (int i = 0; i < ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).size(); i = i + 2) {
    		   Integer faultArg = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(i+1)); 
    		   String faultN = ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(i);
    		   switch(faultN) {
		       		case "TdelayOnce" : {
		    			if(tdelayOnce) { //only executes once per run
		        			ThreadDelayFault f = new ThreadDelayFault();
		        			f.executeFault(faultArg);
		        			tdelayOnce = false;
		    			}
		    		break;}
		       		case "TdelayAll" : {
	        			ThreadDelayFault f = new ThreadDelayFault();
	        			f.executeFault(faultArg);
	        		break;}
		       		case "Leak" : {
	        			if(leakOnce) { //only executes once per run
	        				StackLeakFault f = new StackLeakFault();
	        				f.executeFault(faultArg);
	        				leakOnce=false;
	        			}
	        		break;}
		       		case "LeakCrash" : {
	        			if(leakOnce) { //only executes once per run
	        				sendForcedStats();
	        				StackLeakFault f = new StackLeakFault();
	        				f.executeFault(faultArg);
	        				leakOnce=false;
	        			}
	        		break;}
	        		case "Load" :{ //only executes once per run
	        			if(loadOnce) {
		        		   loadOnce=false;
		        		   kekw=true; //setting this flag to true for the next load fault
		        		   for(int kj = 0; kj<faultArg; kj++) {
		        			   threadLoad t1 = new threadLoad();
			        		   t1.start();
			        		   System.out.println("thread : " + kj);
		        		   }   
	        			}
	        		break;}
	        		case "Dropper" :{
	        			//sendForcedStats();
	        			PacketDropperFault f = new PacketDropperFault();
	        			if(f.executeFault(faultArg)){
	        				ZermiaRuntime.getInstance().increaseNumberOfMessageSentFlood(-1);
	        				return;
	        			}
	        		break;}
	        		case "Crash" :{
	        			sendForcedStats();
	        			CrashFault f = new CrashFault();
	        			f.executeFault();
	        		break;}
	        		case "RandPacket" :{
	        			sendForcedStats();
	        			Object[] arg = joinPoint.getArgs();
	        			//System.out.println("normal data" + ByteBuffer.wrap(messageData).getInt());
	        			RandomPacketFault f = new RandomPacketFault();
	        			messageData = f.executeFault(faultArg, messageData);
	        			//System.out.println("Data scramble" + ByteBuffer.wrap(messageData).getInt());
	        			arg[0] = messageData;
	        			joinPoint.proceed(arg);
	        		break;}
	        		case "0Packet" :{
	        			sendForcedStats();
	        			Object[] arg = joinPoint.getArgs();
	        			//System.out.println("normal data" + ByteBuffer.wrap(messageData).getInt());
	        			ZeroPacketFault f = new ZeroPacketFault();
	        			messageData = f.executeFault(faultArg, messageData);
	        			//System.out.println("Data scramble" + ByteBuffer.wrap(messageData).getInt());
	        			arg[0] = messageData;
	        			joinPoint.proceed(arg);
	        		break;}
	        		case "BigPacket" :{
	        			Object[] arg = joinPoint.getArgs();
	        			if(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).contains("RandPacket")) {
	        				faultArgSub = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).indexOf("RandPacket")+1));
	        				RandomPacketFault f1 = new RandomPacketFault();
	 	        			messageData = f1.executeFault(faultArgSub, messageData);
	        			 } else if(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).contains("0Packet")) {
	        				faultArgSub = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).indexOf("0Packet")+1));
	        				RandomPacketFault f1 = new RandomPacketFault();
		 	        		messageData = f1.executeFault(faultArgSub, messageData);	
	        			 }
	        			BigPacketFault f = new BigPacketFault();
	        			messageData = f.executeFault(messageData, faultArg);
	        			arg[0] = messageData;
	        			joinPoint.proceed(arg);
	        		break;}
	        		case "Flood" :{		
	        			ServerConnection obj = (ServerConnection) joinPoint.getTarget();
	        			
	        			 if(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).contains("RandPacket")) {
	        				 sendForcedStats();
	        				 faultArgSub = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).indexOf("RandPacket")+1));
	        				RandomPacketFault f1 = new RandomPacketFault();
	 	        			messageData = f1.executeFault(faultArgSub, messageData);
	        			 } else if(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).contains("0Packet")) {
	        				 sendForcedStats();
	        				 faultArgSub = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).indexOf("0Packet")+1));
	        				RandomPacketFault f1 = new RandomPacketFault();
		 	        		messageData = f1.executeFault(faultArgSub, messageData);	
	        			 }
	        			 
	        			 if(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).contains("BigPacket")) {
	        				 //System.out.println("test1 " + messageData.length);
	        				faultArgSub2 = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).indexOf("BigPacket")+1));
	     	        		BigPacketFault f2 = new BigPacketFault();
	     	        		messageData = f2.executeFault(messageData, faultArgSub2);
	     	        		//System.out.println("test2 " + messageData.length);
	        			 }

	        			 obj.ZermiaSendBytes(messageData, faultArg);
	        			 ZermiaRuntime.getInstance().increaseNumberOfMessageSentFlood(faultArg);
	        		break;}
    		   }
    	   }
       	}joinPoint.proceed();
 
	}

//----------------------------------------------------------------------------------//
	//sending stats to server when finished
	public void statsStuff() {
		ZermiaRuntime.getInstance().increaseNumberOfMessagesSent();
		if(run == null || run == 0) { //
    	timeStat.startTimer();
    	}
    	else if(run+1 >= ZermiaRuntime.getInstance().getConsensusRoundsFinish()) {
    		if(sendStatsOnce) {
    			sendStatsOnce = false;
    			ZermiaRuntime.getInstance().setTimeFinished(timeStat.endTimer());
    			ZermiaRuntime.getInstance().statsSend();		
    		}
		}
	}
	
	public void sendForcedStats() { //for when a replica is about to crash
		ZermiaRuntime.getInstance().setTimeFinished(timeStat.endTimer());
		ZermiaRuntime.getInstance().statsSend();
	}
		
}
