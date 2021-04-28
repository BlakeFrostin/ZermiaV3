package bftsmart.aspect.zermia;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.yahoo.ycsb.ByteIterator;

import bftsmart.demo.ycsb.YCSBMessage;
import bftsmart.tom.core.messages.TOMMessage;
import zermia.faults.ThreadDelayFault;
import zermia.runtime.clients.ZermiaClientRuntime;

@Aspect
public class ZermiaAspectClientStartYCSB {
	static ReentrantLock z_lock = new ReentrantLock(); 
	static boolean firstTime = true;
	private final  AtomicBoolean stopFaults = new AtomicBoolean(false);
	static volatile Integer currentPrimary = 0;
	private final AtomicBoolean firstime = new AtomicBoolean(true);
	private final AtomicInteger helperT = new AtomicInteger(0); 
	private final AtomicBoolean msgFirst = new AtomicBoolean(true);
	private final AtomicBoolean oneMsgCraft = new AtomicBoolean(true);
	
	volatile static int[] newTgtSingle = new int[1];
	
	protected static SecureRandom randomGen = null;
	
	static String tblNameCraft;
	static String keyNameCraft;
	static HashMap<String, ByteIterator> mapCraft;
	static byte[] msgBytes;
	
    static {
        randomGen = new SecureRandom();
    }
	
	@Around("execution (* bftsmart.communication.client.CommunicationSystemClientSide.send(*,*,*,*,*,*))")
	public void injectFunctionClient(ProceedingJoinPoint joinPoint) throws Throwable {
		Object[] arg = joinPoint.getArgs();
		
		if(!stopFaults.get()) {			
		TOMMessage Smessage = (TOMMessage) joinPoint.getArgs()[2];

		z_lock.lock();
		if(firstime.get()) {
			
			firstime.set(false);
	        try {
	            ZermiaClientRuntime.getInstance().InstanceStart();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } 
	        craftFaultyMsg();
	        
		}
		z_lock.unlock();
		
		ZermiaClientRuntime zR = ZermiaClientRuntime.getInstance();
		
		if(Smessage.getSender() >= zR.getBaseClientID()  && Smessage.getSender() <= zR.getLimitClientFaultyID()) {
			//System.out.println("sequencia" + Smessage.getSequence());
			if(zR.getRunTrigSum().equals(Smessage.getSequence())) {
					
				if(zR.getFaultScheduleSize()>zR.getListIterator()) {
					zR.increaseListIterator();
					zR.setRunTrigger(Integer.parseInt(zR.getRunTriggerList().get(zR.getListIterator()).get(1)));
					zR.setRunStart(Integer.parseInt(zR.getRunTriggerList().get(zR.getListIterator()).get(0)));
		    		Integer RunSum = Integer.parseInt(zR.getRunTriggerList().get(zR.getListIterator()).get(0)) +
		    				   Integer.parseInt(zR.getRunTriggerList().get(zR.getListIterator()).get(1)); 
		    		zR.setRunTrigSum(RunSum); 
				} else {
		    		   stopFaults.set(true);
		    	   	}
			} else if (zR.getRunStart() <= Smessage.getSequence() && zR.getRunTrigSum()> Smessage.getSequence()) {
				zR.setBoolIterator(true);
				//System.out.println("Passou na parte de entre limites de runs");
				for (int i = 0; i < zR.getFaultPamList().get(zR.getListIterator()).size(); i = i + 2) {
					Integer faultArg = Integer.parseInt(zR.getFaultPamList().get(zR.getListIterator()).get(i+1));
					String faultN = zR.getFaultPamList().get(zR.getListIterator()).get(i);
					//System.out.println("Passou na parte das falhas " + faultN);
					switch(faultN) {
		       		case "TdelayAll" : {
	        			ThreadDelayFault f = new ThreadDelayFault();
	        			f.executeFault(faultArg);
	        		break;}
		       		case "TargetNon1":{
		       			newTgtSingle[0] = faultArg;
		       			arg[1] = newTgtSingle;
		       		break;}
		       		case "TargetNonPick":{
		       			int[] newTgt = new int[faultArg];
		       			for(int st=0; st<faultArg; st++) {
		       				newTgt[st] = randomGen.nextInt(zR.getReplicaGroupSize());
		       				while(newTgt[st]==(Smessage.getViewID()%zR.getReplicaGroupSize())) {
		       					newTgt[st] = randomGen.nextInt(zR.getReplicaGroupSize());
		       				}
		       			}
		       			arg[1] = newTgt;
		       		break;}
		       		case "TargetNonAll":{
		       			int[] newTgt = new int[zR.getReplicaGroupSize()];
		       			for(int kt=0;kt<zR.getReplicaGroupSize();kt++) {
		       				if(Integer.valueOf(zR.getRepList().get(kt))==(Smessage.getViewID()%zR.getReplicaGroupSize())) {
		       					helperT.set(1);
		       				} else {
		       					newTgt[kt-helperT.get()]=Integer.valueOf(zR.getRepList().get(kt));
		       					if(kt==zR.getReplicaGroupSize()-1) {
		       						helperT.set(0);
		       					}
		       				}
		       			}
		       			arg[1] = newTgt;
		       		break;}
		       		case "TargetPrimary":{
		       			z_lock.lock();
		       			newTgtSingle[0] = Smessage.getViewID()%zR.getReplicaGroupSize();
		          		arg[1] = newTgtSingle;
		          		z_lock.unlock();
		       		break;}
		       		case "PNRS":{
		       			arg[3] = zR.getReplicaGroupSize();
		       			arg[5] = "PNRS";
		       		break;}
		       		case "DMTA":{
		       			
		       			arg[3] = zR.getReplicaGroupSize();
		       			arg[4] = msgBytes;
		       			arg[5] = "DMTA";
		       		break;}
		       		case "DMTAEP":{
		       			arg[3] = zR.getReplicaGroupSize();
		       			arg[4] = msgBytes;
		       			arg[5] = "DMTAEP";
		       		break;}
					}
				}
			}
			
		}
		
	  }
		joinPoint.proceed(arg);
	}
	
//----------------------------------------------------------------//
	@SuppressWarnings("unchecked")
	@Before("execution (* bftsmart.demo.ycsb.YCSBClient.update*(..))")
	public void getPayloadContents(JoinPoint joinPoint) {
		if(msgFirst.get()) {
			msgFirst.set(false);
			tblNameCraft = (String) joinPoint.getArgs()[0];
			keyNameCraft = (String) joinPoint.getArgs()[1];
			mapCraft = (HashMap<String, ByteIterator>) joinPoint.getArgs()[2];
			//Iterator<String> keys = mapCraft.keySet().iterator();
		}
	}
	
	// This is used to copy one of the earlier payloads
	public void craftFaultyMsg() {
		if(oneMsgCraft.get()) {
			msgFirst.set(false);
			
			Iterator<String> keys = mapCraft.keySet().iterator();
	        HashMap<String, byte[]> map = new HashMap<>();
	        while (keys.hasNext()) {
	            String field = keys.next();
	            map.put(field, mapCraft.get(field).toArray());
	        }
			
			YCSBMessage yMsg = YCSBMessage.newUpdateRequest(tblNameCraft, keyNameCraft, map);
			msgBytes = yMsg.getBytes();
		}
	}
	
}
