package bftsmart.aspect.zermia;
//
//import java.io.ByteArrayInputStream;
//import java.io.ObjectInputStream;
//import java.nio.ByteBuffer;
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
//import zermia.logger.ZermiaReplicaStats;
//
//@Aspect
public class ZermiaAspectFaultManager {
//	static Integer run = 0;
//	protected boolean tdelayOnce = false ; // helper for thread delay to only execute only once instead of several times each time sendbytes is called
//	protected boolean leakOnce = false; // non-crash
//	protected boolean loadOnce = false; //test
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
//    	if(run == null || run == 0) { //
//    	timeStat.startTimer();
//    	}
//    	else if(run >= 4999) {
//			timeStat.endTimer();
//		}
//		
//		
//		byte[] messageData = (byte[]) joinPoint.getArgs()[0];
//		//System.out.println("testNormal " + messageData.length);
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
//        if(formerRun!=run) { //helper to make sure these faults only run once per round
//        	tdelayOnce = true;
//        	leakOnce = true;	
//        }
//        
//        //giving time for the system be fully operation and working 
//        if (ZermiaRuntime.getInstance().getReplicaFaultState().equals(false) || run == null || run < 50 ) {
//            joinPoint.proceed();
//            return;
//        }
//        
//        System.out.println("**************************Run number : " + run);
//        if(run == 50 || ZermiaRuntime.getInstance().getRunStart().equals(0) && ZermiaRuntime.getInstance().getRunTriggers().equals(0)) { //first request sent to the zermia server to receive the faults
//            	ZermiaRuntime.getInstance().faultScheduler(run);   	
//            	loadOnce = true;
//        	}
//        else if(ZermiaRuntime.getInstance().getRunTrigSum().equals(run)){
//        		ZermiaRuntime.getInstance().faultScheduler(run);
//        		kekw=false; //setting all threads to a stop for load fault
//        		loadOnce = true;
//            	joinPoint.proceed();
//        	}
//        else if(ZermiaRuntime.getInstance().getRunStart() <= run &&  ZermiaRuntime.getInstance().getRunTrigSum()> run){
//        	for (int i = 0; i < ZermiaRuntime.getInstance().getFaultPamList().size(); i = i + 2) {
//        		Integer faultArg = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(i+1));
//        		switch (ZermiaRuntime.getInstance().getFaultPamList().get(i)) {
//	        		case "TdelayOnce" : {
//	        			if(tdelayOnce) { //only executes once per run
//		        			ThreadDelayFault f = new ThreadDelayFault();
//		        			f.executeFault(faultArg);
//		        			tdelayOnce = false;
//	        			}
//	        		break;}
//	        		case "TdelayAll" : {
//		        			ThreadDelayFault f = new ThreadDelayFault();
//		        			f.executeFault(faultArg);
//	        		break;}
//	        		case "Leak" : {
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
//		        		   }   
//	        			}
//	        		break;}
//	        		case "Dropper" :{
//	        			PacketDropperFault f = new PacketDropperFault();
//	        			if(f.executeFault(faultArg)){
//	        				return;
//	        			}
//	        		break;}
//	        		case "Flood" :{
//	        			ServerConnection obj = (ServerConnection) joinPoint.getTarget();
//	        			boolean b = (boolean) joinPoint.getArgs()[1];
//	        			
//	        			 if(ZermiaRuntime.getInstance().getFaultPamList().contains("RandPacket")) {
//	        				faultArgSub = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getFaultPamList().indexOf("RandPacket")+1));
//	        				RandomPacketFault f1 = new RandomPacketFault();
//	 	        			messageData = f1.executeFault(faultArgSub, messageData);
//	        			 } else if(ZermiaRuntime.getInstance().getFaultPamList().contains("0Packet")) {
//	        				faultArgSub = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getFaultPamList().indexOf("0Packet")+1));
//	        				RandomPacketFault f1 = new RandomPacketFault();
//		 	        		messageData = f1.executeFault(faultArgSub, messageData);	
//	        			 }
//	        			 
//	        			 if(ZermiaRuntime.getInstance().getFaultPamList().contains("BigPacket")) {
//	        				 //System.out.println("test1 " + messageData.length);
//	        				faultArgSub2 = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getFaultPamList().indexOf("BigPacket")+1));
//	     	        		BigPacketFault f2 = new BigPacketFault();
//	     	        		messageData = f2.executeFault(messageData, faultArgSub2);
//	     	        		//System.out.println("test2 " + messageData.length);
//	        			 }
//	        			 obj.ZermiaSendBytes(messageData, b, faultArg);
//	        		break;}
//	        		case "BigPacket" :{
//	        			Object[] arg = joinPoint.getArgs();
//	        			if(ZermiaRuntime.getInstance().getFaultPamList().contains("RandPacket")) {
//	        				faultArgSub = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getFaultPamList().indexOf("RandPacket")+1));
//	        				RandomPacketFault f1 = new RandomPacketFault();
//	 	        			messageData = f1.executeFault(faultArgSub, messageData);
//	        			 } else if(ZermiaRuntime.getInstance().getFaultPamList().contains("0Packet")) {
//	        				faultArgSub = Integer.parseInt(ZermiaRuntime.getInstance().getFaultPamList().get(ZermiaRuntime.getInstance().getFaultPamList().indexOf("0Packet")+1));
//	        				RandomPacketFault f1 = new RandomPacketFault();
//		 	        		messageData = f1.executeFault(faultArgSub, messageData);	
//	        			 }
//	        			BigPacketFault f = new BigPacketFault();
//	        			messageData = f.executeFault(messageData, faultArg);
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
//	        		case "RandPacket" :{
//	        			Object[] arg = joinPoint.getArgs();
//	        			//System.out.println("normal data" + ByteBuffer.wrap(messageData).getInt());
//	        			RandomPacketFault f = new RandomPacketFault();
//	        			messageData = f.executeFault(faultArg, messageData);
//	        			//System.out.println("Data scramble" + ByteBuffer.wrap(messageData).getInt());
//	        			arg[0] = messageData;
//	        			joinPoint.proceed(arg);
//	        		break;}
//	        		case "Crash" :{
//	        			CrashFault f = new CrashFault();
//	        			f.executeFault();
//	        		break;}
//        		}joinPoint.proceed();
//        	}joinPoint.proceed();
//        }joinPoint.proceed();
//	}
//		
}
