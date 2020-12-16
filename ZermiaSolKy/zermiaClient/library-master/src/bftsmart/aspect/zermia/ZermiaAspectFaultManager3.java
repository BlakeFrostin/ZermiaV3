package bftsmart.aspect.zermia;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import bftsmart.communication.SystemMessage;
import bftsmart.consensus.messages.ConsensusMessage;
import bftsmart.reconfiguration.VMMessage;
import bftsmart.statemanagement.SMMessage;
import bftsmart.tom.core.messages.ForwardedMessage;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.leaderchange.LCMessage;
import zermia.faults.BigPacketFault;
import zermia.faults.CrashFault;
import zermia.faults.Leakv2Fault;
import zermia.faults.PacketDropperFault;
import zermia.faults.RandomPacketFault;
import zermia.faults.StackLeakFault;
import zermia.faults.ThreadDelayFault;
import zermia.faults.ZeroPacketFault;
import zermia.runtime.ZermiaRuntime;
import zermia.stats.Zermia1sec;
import zermia.stats.ZermiaStatsTime;

@Aspect
public class ZermiaAspectFaultManager3 {
	static volatile Integer run;
	static volatile Integer formerRun=0;
	volatile Boolean sendStatsOnce = true;
	volatile boolean loadOnce = true; //test
	byte[] msgData;
	
	static ZermiaStatsTime timeStat = new ZermiaStatsTime();
	static Zermia1sec timeStat1sec = new Zermia1sec();
	
	volatile boolean kekw = true; //LoadLoop for cpu load
	
	static volatile boolean FaultOnOff = false;
	static ReentrantLock z_lock = new ReentrantLock();
	
	class threadLoad extends Thread{  //needed for fault load, just throw anything to make some cpu waste
		public void run(){  
		while(kekw) {
			Integer wasting = 2;
			Integer twaste = 1024*1024*1024;
			while(wasting<twaste) {
				wasting = wasting * 2;
			}
		}}}

//----------------------------------------------------------------------------------//	
	
	@Before("execution (* bftsmart.communication.ServerCommunicationSystem.send*(..))")
	public void advice1(JoinPoint joinPoint) throws Throwable {
		ZermiaRuntime zR = ZermiaRuntime.getInstance();
		
		if (zR.getReplicaFaultState().equals(true)) {
			SystemMessage Smessage = (SystemMessage) joinPoint.getArgs()[1];
			
			 if(Smessage instanceof TOMMessage) {
		        	
	        	if(ZermiaRuntime.getInstance().getCreply()) { //client reply
	        		//System.out.println("Mensagem de Reply para cliente");
	        		//FaultOnOff = true;
	        	}
		     }
		}
	}
	
//----------------------------------------------------------------------------------//	
	
	@Around("execution (* bftsmart.communication.server.ServersCommunicationLayer.send*(..))")
	public void advice2(ProceedingJoinPoint joinPoint) throws Throwable {
		ZermiaRuntime zR = ZermiaRuntime.getInstance();
		Object[] arg = joinPoint.getArgs();
		SystemMessage Smessage = (SystemMessage) joinPoint.getArgs()[1];
		
		if (Smessage instanceof ConsensusMessage) {
			
			ConsensusMessage cMessage = (ConsensusMessage) Smessage;
			run = cMessage.getNumber();
            formerRun=run; //
            //System.out.println("RUN" + run);
            if (cMessage.getNumber() > run) {
                run = cMessage.getNumber();
            	}
		}
		
		if (zR.getReplicaFaultState().equals(true)) {
				
			if (Smessage instanceof ConsensusMessage) {
				
				ConsensusMessage cMessage = (ConsensusMessage) Smessage;	

	            switch(cMessage.getPaxosVerboseType()){
	            	case "PROPOSE": {
		                if(ZermiaRuntime.getInstance().getCsPrP()) {
		                	//System.out.println("Mensagem de pre-prepare");
		                	FaultOnOff = true;
		                }
	                break;}
	                case "WRITE": {
		                if(ZermiaRuntime.getInstance().getCsCm()) {
		                	//System.out.println("Mensagem de prepare");
		                	FaultOnOff = true;	
		                }
	                break;}
	                case "ACCEPT": {
		                if(ZermiaRuntime.getInstance().getCsPr()) {
		                  	//System.out.println("Mensagem de commit");
		                	FaultOnOff = true;
		                }
	                break;}
	            }
	            
	            
			} else if(Smessage instanceof ForwardedMessage) { //message forwarding to Leader
	        	
		       	 if(ZermiaRuntime.getInstance().getFw()) {
		       		 //System.out.println("Mensagem de forwarded");
		       		 FaultOnOff = true; 	 
		       	 }
			} else if(Smessage instanceof LCMessage) { //leaderchange
	        	
	        	if(ZermiaRuntime.getInstance().getVc()) {
	        		//System.out.println("Mensagem de ViewChange");
	        		FaultOnOff = true;
	        	}	
	        } else if(Smessage instanceof SMMessage) { //checkpoint stuff
	        	
	        	if(ZermiaRuntime.getInstance().getCk()) {
	        		//System.out.println("Mensagem de Checkpoint");
	        		FaultOnOff = true;	
	        	}	
	        } else if(Smessage instanceof VMMessage) { //reconfigure stuff
	        	
	        	if(ZermiaRuntime.getInstance().getCk()) {
	        		//System.out.println("Mensagem de VMM");
	        		FaultOnOff = true;	
	        	}	
	        }
		}
		
		
		if(run!=null) {
			if (zR.getReplicaFaultState().equals(true) && ZermiaRuntime.getInstance().getCurrentPrimary()!=null && zR.getRunStart() <= run && zR.getRunTrigSum()> run && FaultOnOff) {
				if(zR.getFprimary()) {
					arg[0] = (int[]) setPrimaryTarget();
					}	
			}
		}
		joinPoint.proceed(arg);
	}
	

//----------------------------------------------------------------------------------//	
	@Around("execution (* bftsmart.communication.server.ServerConnection.sendBytes*(..))")
	public void advice3(ProceedingJoinPoint joinPoint) throws Throwable {
		ZermiaRuntime zR = ZermiaRuntime.getInstance();
		Object[] arg = joinPoint.getArgs();
		msgData =  (byte[]) joinPoint.getArgs()[0];
		 statsStuff();
		 statsStuff1sec();
		
		if (zR.getReplicaFaultState().equals(false) || run == null) {
			//if not faulty do nothing
		} else {
			 if(zR.getRunTrigSum().equals(run)) {
				 kekw = false;
				 loadOnce = true;
				 if(zR.getFaultScheduleSize()>zR.getListIterator()) {
					 zR.increaseListIterator();
					 zR.setRunTrigger(Integer.parseInt(zR.getRunTriggerList().get(zR.getListIterator()).get(1)));
					 zR.setRunStart(Integer.parseInt(zR.getRunTriggerList().get(zR.getListIterator()).get(0)));
		    		 Integer RunSum = Integer.parseInt(zR.getRunTriggerList().get(zR.getListIterator()).get(0)) +
		    				   Integer.parseInt(zR.getRunTriggerList().get(zR.getListIterator()).get(1)); 
		    		 zR.setRunTrigSum(RunSum); 
				 	} else {
		    		   zR.setReplicaFaultState(false);
		    	   	}
				 }
			 else if (zR.getRunStart() <= run && zR.getRunTrigSum()> run && FaultOnOff) {
				 zR.setBoolIterator(true);
				 for (int i = 0; i < zR.getFaultPamList().get(zR.getListIterator()).size(); i = i + 2) {
					 Integer faultArg = Integer.parseInt(zR.getFaultPamList().get(zR.getListIterator()).get(i+1));
					 String faultN = zR.getFaultPamList().get(zR.getListIterator()).get(i);
					 switch(faultN) {
			       		case "TdelayAll" : {
		        			ThreadDelayFault f = new ThreadDelayFault();
		        			f.executeFault(faultArg);
		        		break;}
			       		case "Leak" : {
		        				StackLeakFault f = new StackLeakFault();
		        				f.executeFault(faultArg);
		        		break;}
			       		case "LeakCrash" : {
		        				sendForcedStats();
		        				StackLeakFault f = new StackLeakFault();
		        				f.executeFault(faultArg);
		        		break;}
			       		case "LeakV2" : {
			       			Leakv2Fault f = new Leakv2Fault();
			       			f.executeFault(faultArg);
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
		        			PacketDropperFault f = new PacketDropperFault();
		        			if(f.executeFault(faultArg)){
		        				ZermiaRuntime.getInstance().increaseNumberOfMessageSentFlood(-1);
		        				return;}
		        		break;}
		        		case "Crash" :{
		        			sendForcedStats();
		        			CrashFault f = new CrashFault();
		        			f.executeFault();
		        		break;}
		        		case "RandPacket" :{
		        			sendForcedStats();
		        			RandomPacketFault f = new RandomPacketFault();
		        			msgData = f.executeFault(faultArg, msgData);
		        			//System.out.println("Data scramble" + ByteBuffer.wrap(messageData).getInt());
		        			
		        			arg[0] = msgData;
		        		break;}
		        		case "0Packet" :{
		        			sendForcedStats();
		        			//System.out.println("normal data" + ByteBuffer.wrap(messageData).getInt());
		        			ZeroPacketFault f = new ZeroPacketFault();
		        			msgData = f.executeFault(faultArg, msgData);
		        			//System.out.println("Data scramble" + ByteBuffer.wrap(messageData).getInt());
		        			
		        			arg[0] = msgData;       			
		        		break;}
		        		case "BigPacket" :{
		        			BigPacketFault f = new BigPacketFault();
		        			msgData = f.executeFault(msgData, faultArg);
		        			arg[0] = msgData;
		        					        			
		        			joinPoint.proceed(arg);
		        		break;}
		        		case "Flood" :{		
		        			arg[0] = msgData;
		        			for(int jk=0;jk<faultArg;jk++) {
		        				joinPoint.proceed(arg);
		        				statsStuff1sec();
		        			}	
		        			ZermiaRuntime.getInstance().increaseNumberOfMessageSentFlood(faultArg);	 
		        		break;}
		        		case "Flood0Packet" :{
		        			sendForcedStats();
		        			ZeroPacketFault f = new ZeroPacketFault();
		        			msgData = f.executeFault(faultArg, msgData);
		        			arg[0] = msgData;
		        			for(int jk=0;jk<faultArg;jk++) {
		        				joinPoint.proceed(arg);
		        				statsStuff1sec();
		        			}	
		        			ZermiaRuntime.getInstance().increaseNumberOfMessageSentFlood(faultArg);	 
		        		break;}
		        		case "FloodRandPacket" :{
		        			sendForcedStats();
		        			ZeroPacketFault f = new ZeroPacketFault();
		        			msgData = f.executeFault(faultArg, msgData);
		        			arg[0] = msgData;
		        			for(int jk=0;jk<faultArg;jk++) {
		        				joinPoint.proceed(arg);
		        				statsStuff1sec();
		        			}	
		        			ZermiaRuntime.getInstance().increaseNumberOfMessageSentFlood(faultArg);	
		        		break;}
		        		case "FloodBigPacket" :{
		        			BigPacketFault f = new BigPacketFault();
		        			msgData = f.executeFault(msgData, faultArg);
		        			arg[0] = msgData;
		        			for(int jk=0;jk<faultArg;jk++) {
		        				joinPoint.proceed(arg);
		        				statsStuff1sec();
		        			}	
		        			ZermiaRuntime.getInstance().increaseNumberOfMessageSentFlood(faultArg);	
		        		break;}
					 }
				 }
			 }	
			 FaultOnOff = false;
		}
		joinPoint.proceed(arg);
	}
	
//----------------------------------------------------------------------------------//
	//sending stats to server when finished
	public void statsStuff() {
		ZermiaRuntime.getInstance().increaseNumberOfMessagesSent();
		z_lock.lock();
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
		z_lock.unlock();
	}
	
//----------------------------------------------------------------------------------//
		
	public void sendForcedStats() { //for when a replica is about to crash
		ZermiaRuntime.getInstance().setTimeFinished(timeStat.endTimer());
		ZermiaRuntime.getInstance().statsSend();
	}
	
//----------------------------------------------------------------------------------//
	
	public void statsStuff1sec() {
		
		ZermiaRuntime.getInstance().increaseNumberOfMessages1secSent();
		z_lock.lock();
		if(timeStat1sec.time1Sec()) {
			ZermiaRuntime.getInstance().statSend1sec();
		}
		z_lock.unlock();
	}
	
//----------------------------------------------------------------------------------//	
	public byte[] messageVert(SystemMessage sm) {
		ByteArrayOutputStream bo = new ByteArrayOutputStream(); //248
		try {
			new ObjectOutputStream(bo).writeObject(sm);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] smk = bo.toByteArray();
		return smk;
	}
	
	public SystemMessage messageVert2(byte[] sm, SystemMessage msk) {
		SystemMessage smk = msk;
		try {
		smk = (SystemMessage) (new ObjectInputStream(new ByteArrayInputStream(sm)).readObject());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return smk;
	}
	
//----------------------------------------------------------------------------------//	
	
	public int[] setPrimaryTarget() {
		 int[] newTarget = new int[1];
		 if(ZermiaRuntime.getInstance().getFprimary()) {			 
			 newTarget[0] = ZermiaRuntime.getInstance().getCurrentPrimary();
		 }
		return newTarget;
	}
	
//---------------------------------------------------------------------------------//	
	
	@Before("execution (* bftsmart.tom.leaderchange.LCManager.setNewLeader*(..))")
	public void advice4(JoinPoint jpoint) {
		Integer primaryN = (Integer) jpoint.getArgs()[0];
		ZermiaRuntime.getInstance().setCurrentPrimary(primaryN);
	}

//---------------------------------------------------------------------------------//	
	@AfterReturning(pointcut="execution (* bftsmart.tom.core.ExecutionManager.getCurrentLeader*(..))", returning ="currentLeader")
	public void advice5(Object currentLeader) {
		ZermiaRuntime zR = ZermiaRuntime.getInstance();
		if (zR.getReplicaFaultState().equals(true)){
			zR.setCurrentPrimary((Integer) currentLeader);	
		}
		
	}	
	
}
