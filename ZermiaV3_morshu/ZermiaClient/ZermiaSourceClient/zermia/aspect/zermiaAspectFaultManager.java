package zermia.aspect;

public class zermiaAspectFaultManager {

}
//package bftsmart.aspect.zermia;
//
//import java.io.ByteArrayInputStream;
//import java.io.ObjectInputStream;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//
//import bftsmart.communication.SystemMessage;
//import bftsmart.communication.server.ServerConnection;
//import bftsmart.consensus.messages.ConsensusMessage;
//import zermia.faults.BigPacketFault;
//import zermia.faults.CrashFault;
//import zermia.faults.PacketDropperFault;
//import zermia.faults.RandomPacketFault;
//import zermia.faults.StackLeakFault;
//import zermia.faults.ThreadDelayFault;
//import zermia.faults.ZeroPacketFault;
//import zermia.runtime.ZermiaRuntime;
//import zermia.stats.ZermiaReplicaStats;
//
//@Aspect
//public class ZermiaAspectFaultManager {
//	static Integer run = 0;
//	protected boolean tdelayOnce = false ; // helper for thread delay to only execute only once instead of several times each time sendbytes is called
//	protected boolean leakOnce = false; // non-crash
//	protected boolean loadOnce = true; //test
//	protected volatile boolean sendStatsOnce = true;
//	protected int formerRun=0;
//	protected Integer faultArgSub; //for more specific flood attacks , 0pack or randpack
//	protected Integer faultArgSub2; //extra for flood attacks, adding big packets
//	volatile boolean kekw = true; //LoadLoop for cpu load
//	static ZermiaReplicaStats timeStat = new ZermiaReplicaStats();
//	
//	class threadLoad extends Thread{  //needed for fault load, through this max load
//		public void run(){  
//		while(kekw) {
//		}}}
//		
//	@Around("execution (* bftsmart.communication.server.ServerConnection.sendBytes*(..))")
//	public void advice(ProceedingJoinPoint joinPoint) throws Throwable {
//		
// 
//		byte[] messageData = (byte[]) joinPoint.getArgs()[0];
//        try {
//            SystemMessage sm = (SystemMessage) (new ObjectInputStream(new ByteArrayInputStream(messageData)).readObject());
//            if (sm instanceof ConsensusMessage) {
//            	ConsensusMessage pm = (ConsensusMessage) sm;
//            	formerRun=run; //
//                run = pm.getNumber(); 
//                if (pm.getNumber() > run) {
//                    run = pm.getNumber();
//                }
//            }
//        } catch (Exception ex) {
//       }
//        
//		statsStuff();
//		
//        if (ZermiaRuntime.getInstance().getReplicaFaultState().equals(false)) {
//            joinPoint.proceed();
//            return;
//        }
//        
// 
//        if(formerRun!=run) { //helper to make sure these faults only run once per consensus round
//        	tdelayOnce = true;
//        	leakOnce = true;	
//        }
//        
//       if(ZermiaRuntime.getInstance().getRunTrigSum().equals(run)) {
//    	   kekw = false;
//    	   loadOnce = true;
//    	   if(ZermiaRuntime.getInstance().getFaultScheduleSize()>ZermiaRuntime.getInstance().getListIterator()) {
//    		   ZermiaRuntime.getInstance().increaseListIterator();
//    		   ZermiaRuntime.getInstance().setRunTrigger(Integer.parseInt(ZermiaRuntime.getInstance().getRunTriggerList().get(ZermiaRuntime.getInstance().getListIterator()).get(1)));
//    		   ZermiaRuntime.getInstance().setRunStart(Integer.parseInt(ZermiaRuntime.getInstance().getRunTriggerList().get(ZermiaRuntime.getInstance().getListIterator()).get(0)));
//    		   Integer RunSum = Integer.parseInt(ZermiaRuntime.getInstance().getRunTriggerList().get(ZermiaRuntime.getInstance().getListIterator()).get(0)) +
//    				   Integer.parseInt(ZermiaRuntime.getInstance().getRunTriggerList().get(ZermiaRuntime.getInstance().getListIterator()).get(1));
//    		   ZermiaRuntime.getInstance().setRunTrigSum(RunSum);
//    	
//    	   } else {
//    		   ZermiaRuntime.getInstance().setReplicaFaultState(false);
//    	   }
//    	   
//    	   joinPoint.proceed();
//       }
//       else if(ZermiaRuntime.getInstance().getRunStart() <= run && ZermiaRuntime.getInstance().getRunTrigSum()> run) {
//    	   for (int i = 0; i < ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).size(); i = i + 2) {
//    		   Integer faultArg = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(i+1)); 
//    		   String faultN = ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(i);
//    		   switch(faultN) {
//		       		case "TdelayOnce" : {
//		    			if(tdelayOnce) { //only executes once per run
//		        			ThreadDelayFault f = new ThreadDelayFault();
//		        			f.executeFault(faultArg);
//		        			tdelayOnce = false;
//		    			}
//		    		break;}
//		       		case "TdelayAll" : {
//	        			ThreadDelayFault f = new ThreadDelayFault();
//	        			f.executeFault(faultArg);
//	        		break;}
//		       		case "Leak" : {
//	        			if(leakOnce) { //only executes once per run
//	        				StackLeakFault f = new StackLeakFault();
//	        				f.executeFault(faultArg);
//	        				leakOnce=false;
//	        			}
//	        		break;}
//	        		case "Load" :{ //only executes once per run
//	        			if(loadOnce) {
//		        		   loadOnce=false;
//		        		   kekw=true; //setting this flag to true for the next load fault
//		        		   for(int kj = 0; kj<faultArg; kj++) {
//		        			   threadLoad t1 = new threadLoad();
//			        		   t1.start();
//			        		   System.out.println("thread : " + kj);
//		        		   }   
//	        			}
//	        		break;}
//	        		case "Dropper" :{
//	        			PacketDropperFault f = new PacketDropperFault();
//	        			if(f.executeFault(faultArg)){
//	        				ZermiaRuntime.getInstance().increaseNumberOfMessageSentFlood(-1);
//	        				return;
//	        			}
//	        		break;}
//	        		case "Crash" :{
//	        			CrashFault f = new CrashFault();
//	        			f.executeFault();
//	        		break;}
//	        		case "RandPacket" :{
//	        			Object[] arg = joinPoint.getArgs();
//	        			//System.out.println("normal data" + ByteBuffer.wrap(messageData).getInt());
//	        			RandomPacketFault f = new RandomPacketFault();
//	        			messageData = f.executeFault(faultArg, messageData);
//	        			//System.out.println("Data scramble" + ByteBuffer.wrap(messageData).getInt());
//	        			arg[0] = messageData;
//	        			joinPoint.proceed(arg);
//	        		break;}
//	        		case "0Packet" :{
//	        			Object[] arg = joinPoint.getArgs();
//	        			//System.out.println("normal data" + ByteBuffer.wrap(messageData).getInt());
//	        			ZeroPacketFault f = new ZeroPacketFault();
//	        			messageData = f.executeFault(faultArg, messageData);
//	        			//System.out.println("Data scramble" + ByteBuffer.wrap(messageData).getInt());
//	        			arg[0] = messageData;
//	        			joinPoint.proceed(arg);
//	        		break;}
//	        		case "BigPacket" :{
//	        			Object[] arg = joinPoint.getArgs();
//	        			if(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).contains("RandPacket")) {
//	        				faultArgSub = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).indexOf("RandPacket")+1));
//	        				RandomPacketFault f1 = new RandomPacketFault();
//	 	        			messageData = f1.executeFault(faultArgSub, messageData);
//	        			 } else if(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).contains("0Packet")) {
//	        				faultArgSub = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).indexOf("0Packet")+1));
//	        				RandomPacketFault f1 = new RandomPacketFault();
//		 	        		messageData = f1.executeFault(faultArgSub, messageData);	
//	        			 }
//	        			BigPacketFault f = new BigPacketFault();
//	        			messageData = f.executeFault(messageData, faultArg);
//	        			arg[0] = messageData;
//	        			joinPoint.proceed(arg);
//	        		break;}
//	        		case "Flood" :{		
//	        			ServerConnection obj = (ServerConnection) joinPoint.getTarget();
//	        			boolean b = (boolean) joinPoint.getArgs()[1];
//	        			
//	        			 if(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).contains("RandPacket")) {
//	        				faultArgSub = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).indexOf("RandPacket")+1));
//	        				RandomPacketFault f1 = new RandomPacketFault();
//	 	        			messageData = f1.executeFault(faultArgSub, messageData);
//	        			 } else if(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).contains("0Packet")) {
//	        				faultArgSub = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).indexOf("0Packet")+1));
//	        				RandomPacketFault f1 = new RandomPacketFault();
//		 	        		messageData = f1.executeFault(faultArgSub, messageData);	
//	        			 }
//	        			 
//	        			 if(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).contains("BigPacket")) {
//	        				 //System.out.println("test1 " + messageData.length);
//	        				faultArgSub2 = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).get(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getListIterator()).indexOf("BigPacket")+1));
//	     	        		BigPacketFault f2 = new BigPacketFault();
//	     	        		messageData = f2.executeFault(messageData, faultArgSub2);
//	     	        		//System.out.println("test2 " + messageData.length);
//	        			 }
//
//	        			 obj.ZermiaSendBytes(messageData, b, faultArg);
//	        			 ZermiaRuntime.getInstance().increaseNumberOfMessageSentFlood(faultArg);
//	        		break;}
//    		   }
//    	   }
//       	}joinPoint.proceed();
// 
//	}
//
////----------------------------------------------------------------------------------//
//	//sending stats to server when finished
//	public void statsStuff() {
//		ZermiaRuntime.getInstance().increaseNumberOfMessagesSent();
//		if(run == null || run == 0) { //
//    	timeStat.startTimer();
//    	}
//    	else if(run+1 >= ZermiaRuntime.getInstance().getConsensusRoundsFinish()) {
//    		if(sendStatsOnce) {
//    			sendStatsOnce = false;
//    			ZermiaRuntime.getInstance().setTimeFinished(timeStat.endTimer());
//    			ZermiaRuntime.getInstance().statsSend();		
//    		}
//		}
//	}
//		
//}
