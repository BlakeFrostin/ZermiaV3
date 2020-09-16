package zermia.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Ordering;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import zermia.local.boot.ZermiaClientLocalBoot;
import zermia.local.boot.ZermiaReplicaLocalBoot;
import zermia.logger.ZermiaLogger;
import zermia.properties.ZermiaCodeClient;
import zermia.properties.ZermiaCodeReplica;
import zermia.properties.ZermiaProperties;
import zermia.proto.services.ZermiaRuntimeServices;

public class ZermiaServerMain {
	static ZermiaProperties props = new ZermiaProperties();
	static ZermiaCodeClient codClient = new ZermiaCodeClient();
	static ZermiaCodeReplica codReplica = new ZermiaCodeReplica();
	ZermiaServerReplicaList repList = new ZermiaServerReplicaList();
	ZermiaServerClientList clientList = new ZermiaServerClientList();
	static int closeServer; 
	static BiMap<Integer,String> faultList1 = HashBiMap.create();//trying something with priorities in faults wih bimap
	//adicionar logger
	
//----------------------------------------------------------------------------------//
	//logger stuff
	static {
		try {
			ZermiaLogger.createLogFile();
			Logger.getLogger(ZermiaServerMain.class.getName()).log(Level.INFO, "--------------------//--------------------//--------------------//--------------------//--------------------//--------------------");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//----------------------------------------------------------------------------------//		
	//services and server start
	public void ZermiaServer() {
		Server zermiaServer = ServerBuilder.forPort(props.getOrchestratorPort())
				.addService(new ZermiaRuntimeServices())
				.build();
				
      try {
          zermiaServer.start();
          Logger.getLogger(ZermiaServerMain.class.getName()).log(Level.INFO, "Server starting in port : " + zermiaServer.getPort());
          zermiaServer.awaitTermination(closeServer ,TimeUnit.SECONDS);
          Logger.getLogger(ZermiaServerMain.class.getName()).log(Level.SEVERE, "Server Shutdown after the established " + closeServer + " Seconds");
      } catch (IOException e) {
          e.printStackTrace();
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
  }
	
//----------------------------------------------------------------------------------//	
	//client local boot
	public void BootClientStart() throws Exception {
		String clientCode;
		
		for(int i = 0; i<props.getNumberOfClients(); i++ ) {
			clientCode = codClient.getClientCode();
			ZermiaClientLocalBoot clientBoot = new ZermiaClientLocalBoot();
			clientCode = clientCode.replaceAll("(?i)Zermia", props.getClientsId(i));
			clientBoot.bootStrapClient(clientCode);
		}
	}
	
//----------------------------------------------------------------------------------//	
	//Replica local boot
	public void BootReplicaStart() throws Exception {
		String replicaCode;
		
		for(int i = 0; i<props.getNumberOfReplicas(); i++ ) {
			replicaCode = codReplica.getReplicaCode();
			ZermiaReplicaLocalBoot replicaBoot = new ZermiaReplicaLocalBoot();
			replicaCode = replicaCode.replaceAll("(?i)Zermia", props.getReplicasID(i));
			replicaBoot.bootStrapReplica(replicaCode,props.getReplicasID(i));
		}
	}
	
//----------------------------------------------------------------------------------//
	//populating replica and cliest list with ids, ips
	public void populatingLists() {
		for(int i = 0; i<props.getNumberOfAvailableReplicas(); i++ ) {
			ZermiaServerReplica replica = new ZermiaServerReplica(props.getReplicasID(i), props.getReplicasIp(i));
			repList.addReplica(replica.getID(), replica);
	    }
		
		for(int j = 0; j<props.getNumberOfAvailableClients(); j++) {
			ZermiaServerClient client = new ZermiaServerClient(props.getClientsId(j), props.getClientsIp(j));
			clientList.addReplica(client.getID(), client);
		}	 
	}	
	
//----------------------------------------------------------------------------------//
	//error message is displayed when typed wrongly in the command line
    public void	errorMessage(Integer errorType, Integer repNumber, String fault) {
    	switch(errorType) {
    	case 1 : {
    		System.out.println("**** Badly formed sentence for Replica " + repNumber + " ****");
    		System.out.println("**** Fault " + fault +  " unrecognized ****");
    		System.out.println("**** Available Faults : Crash, Tdelay, Leak, LeakCrash, Dropper, Load, 0Packet, RandPacket, Flood, BigPacket ****");
    		System.out.println("**** Faults should be numbered according to the timeline they are going to be active at, going from the earliest round start to the latest one  ****");
    		System.out.println("**** Correct Sentence Example : Replica 2 Tdelay 1000 Crash 0 100 10 Tdelay 5000 200 100 Replica 3 Tdelay 2000 100 10 Load 550 100 ****");
    		System.exit(0);
    		break;}
    	case 2 : {
    		System.out.println("**** Badly formed sentence for Replica " + repNumber + " ****");
    		System.out.println("**** Faults should be numbered according to the timeline they are going to be active at, going from the earliest round start to the latest one  ****");
    		System.out.println("**** Correct Sentence Example :  Replica 2 Tdelay 1000 Crash 0 100 10 Tdelay 5000 200 100 Replica 3 Tdelay 2000 100 10 Load 550 100****");
    		System.exit(0);
    		break;}
    	}
    }	

//----------------------------------------------------------------------------------//
   //gets faults from the cmd and populates lists corresponding to the faults, parameters, runs, trigger events from the specified replicas
    public void setReplicasProperties2(String[] args) {
    	String replicaID = null;
    	int j = 0; 
    	
    	ArrayList<String> faultPam = new ArrayList<String>();
    	ArrayList<String> runTrigger = new ArrayList<String>();
    	LinkedList<Integer> runOrdering= new LinkedList<Integer>();  
    	
    	for(int i = 0; i<args.length; i=i+2) {
    		if(args[i].matches("(?i)Replica")){
 
				if(!Ordering.natural().isOrdered(runOrdering)) { //checking if runs are ordered from smallest to the biggest
					errorMessage(2,Integer.valueOf(replicaID), "Nulo");
				}
    			runOrdering= new LinkedList<Integer>();
    			j=0;
    			
    			replicaID = args[i+1];
    			repList.getReplica(replicaID).setFaultness(true);
    			
    		} else if(args[i].matches("\\d+") && args[i+1].matches("\\d+")) {
    			if(repList.getReplica(replicaID).getRunsTriggerList().size()<j+1 || repList.getReplica(replicaID).getRunsTriggerList().isEmpty()) {
        			runTrigger.add(args[i]);
        			runTrigger.add(args[i+1]);//if not int then system exit
        			repList.getReplica(replicaID).getRunsTriggerList().add(j, runTrigger);
        			runTrigger = new ArrayList<String>();
        			runOrdering.add(Integer.valueOf(args[i]));
    			} else {
    				repList.getReplica(replicaID).getRunsTriggerList().get(j).add(args[i]);
    				repList.getReplica(replicaID).getRunsTriggerList().get(j).add(args[i+1]);//if not int then system exit
    				runOrdering.add(Integer.valueOf(args[i]));
    			}
    			
    			j=j+1;
    		} else {
    			if(repList.getReplica(replicaID).getFaultPamList().size()<j+1 || repList.getReplica(replicaID).getFaultPamList().isEmpty()) {
    				if(!checkFaultName(args[i])) {
    					errorMessage(1,Integer.valueOf(replicaID), args[i]);
    				}
    				
    				faultPam.add(args[i]);
    				faultPam.add(args[i+1]);//if not int then system exit
    				repList.getReplica(replicaID).getFaultPamList().add(j, faultPam);
    				faultPam = new ArrayList<String>();
    			} else {
    				if(!checkFaultName(args[i])) {
    					errorMessage(1,Integer.valueOf(replicaID), args[i]);
    				}
    				
    				repList.getReplica(replicaID).getFaultPamList().get(j).add(args[i]);
    				repList.getReplica(replicaID).getFaultPamList().get(j).add(args[i+1]);//if not int then system exit
    			}
    		}	
    	}
    	
    }
    
//----------------------------------------------------------------------------------//
    //checking if a typed fault exists
	public Boolean checkFaultName(String faultName) {
		if(faultList1.containsValue(faultName)) {
			return true;
		} else {
			return false;
		}	
	}
  
//----------------------------------------------------------------------------------//	
	//setting priorities for the existing faults
	public void populatingAvailableFaults() { //available faults and priority
		faultList1.put(1,"TdelayOnce");
		faultList1.put(2,"TdelayAll");
		faultList1.put(3,"Leak");
		faultList1.put(4,"Load");
		faultList1.put(5,"Dropper");
		faultList1.put(6,"Flood");
		faultList1.put(7,"BigPacket");
		faultList1.put(8,"0Packet");
		faultList1.put(9,"RandPacket");
		faultList1.put(10,"Crash");
	}
	
//----------------------------------------------------------------------------------//		
	//Setting fault priorities straight through slective sorting
	public ArrayList<String> sortingFaultPriority(ArrayList<String> faultList){
		Integer n = faultList.size();
		@SuppressWarnings("unchecked")
		ArrayList<String> fpList = faultToPrio((ArrayList<String>) faultList.clone());
			
		for(int i = 0; i<n-2; i=i+2) {
			int min = i;
			for(int j= i+2; j<n; j=j+2) {
				if(Integer.valueOf(fpList.get(j)) < Integer.valueOf(fpList.get(min))) {
					min = j;
				}	
			}
		String temp1 = fpList.get(min);
		String temp2 = fpList.get(min+1);
		fpList.set(min, fpList.get(i));	
		fpList.set(min+1, fpList.get(i+1));
		fpList.set(i, temp1);
		fpList.set(i+1, temp2);
		}
		
		return prioToFault(fpList);
	}
//----------------------------------------------------------------------------------//
	//helping method that changes fault to priority
	public ArrayList<String> faultToPrio (ArrayList<String> faultList){
		for(int i=0; i<faultList.size(); i=i+2) {
			if(faultList1.containsValue(faultList.get(i))) {
				faultList.set(i, String.valueOf(faultList1.inverse().get(faultList.get(i))));
			}
		}
		return faultList;
	}

//----------------------------------------------------------------------------------//
	//helping method that changes priority to fault
	public ArrayList<String> prioToFault (ArrayList<String> faultList){
		for(int i=0; i<faultList.size(); i=i+2) {
			if(faultList1.containsKey(Integer.valueOf(faultList.get(i)))) {
				faultList.set(i, faultList1.get(Integer.valueOf(faultList.get(i))));
			}
		}
		return faultList;
	}
	
//----------------------------------------------------------------------------------//	
	//full sorter
	public String[] sorterList(String[] listArg) {
		String[] sortedList = new String[listArg.length];		
		sortedList=listArg;
		ArrayList<String> argus = new ArrayList<String>();
		Integer st=0;
		Integer ed=0;
		Integer testt=0;
		Integer kk=0;
		
		for(int i=0;i<listArg.length;i=i+2) {
			if(listArg[i].matches("(?i)Replica")){
				st=i+2;
				}
			 else if(listArg[i].matches("\\d+") && listArg[i+1].matches("\\d+")) {
				ed=i;
				argus=sortingFaultPriority(argus);
				for(int k=st;k<ed;k++) {
					sortedList[k] = argus.get(testt);
					testt=testt+1;
				}
				testt=0;
				kk=0;
				argus.clear();
			} else if(listArg[i-2].matches("\\d+") && listArg[i-1].matches("\\d+")){
				st=i;
				argus.add(kk,listArg[i]);
				argus.add(kk+1,listArg[i+1]);
			} else {
				argus.add(kk,listArg[i]);
				argus.add(kk+1,listArg[i+1]);
			}
		
		}
		for(int omega=0;omega<sortedList.length;omega++) {
			System.out.println(sortedList[omega]);
		}
		return sortedList;
	}
//----------------------------------------------------------------------------------//	
	//Iterator stuff
	public void settingIterators() {
		for (int a = 0; a < props.getNumberOfReplicas(); a++) {
			if(repList.getReplica(String.valueOf(a)).getFaultness().equals(true)) {
				for(int b = 0; b <repList.getReplica(String.valueOf(a)).getRunsTriggerList().size(); b++) {
					repList.getReplica(String.valueOf(a)).getIteratorList().add(Integer.valueOf(repList.getReplica(String.valueOf(a)).getRunsTriggerList().get(b).get(0)) + Integer.valueOf(repList.getReplica(String.valueOf(a)).getRunsTriggerList().get(b).get(1)));
				}		
			}
		}
	}
	
	
//----------------------------------------------------------------------------------//	
	//main
	public static void main(String[] args) throws Exception {
		props.loadProperties();						//properties file loader
		closeServer =  props.getServerUptime();		//server uptime in secs
		ZermiaServerMain ServerZ = new ZermiaServerMain();
		ServerZ.populatingAvailableFaults(); 		//fault list available
		ServerZ.populatingLists();					//replica and client Lists
		
		if(props.getPrioritySort()) {				//if priority method is true in config
			ServerZ.setReplicasProperties2(ServerZ.sorterList(args)); 	//priority rearrange	
		} else {
			ServerZ.setReplicasProperties2(args);	//if false, there is no priority rearrange
		}
		
		ServerZ.settingIterators();  
		
		//ServerStart
		new Thread() {
			public void run(){
				ServerZ.ZermiaServer();				//zermia server start
			}
		}.start();
		
		try {
			ServerZ.BootReplicaStart();				//local boot of replicas, disable this if not running on localhost, needs to be recompiled again and put on Zermia main folder
			Thread.sleep(props.getClientBootTime());
			ServerZ.BootClientStart();				//local boot of clients
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
