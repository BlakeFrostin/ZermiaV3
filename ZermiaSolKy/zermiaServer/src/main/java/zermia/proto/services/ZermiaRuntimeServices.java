package zermia.proto.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.stub.StreamObserver;
import zermia.properties.ZermiaProperties;
import zermia.proto.ProtoRuntime.ConnectionReply;
import zermia.proto.ProtoRuntime.ConnectionRequest;
import zermia.proto.ProtoRuntime.ConnectionSecondReply;
import zermia.proto.ProtoRuntime.ConnectionSecondRequest;
import zermia.proto.ProtoRuntime.FaultActivationReply;
import zermia.proto.ProtoRuntime.FaultActivationRequest;
import zermia.proto.ProtoRuntime.Stat1SecReply;
import zermia.proto.ProtoRuntime.Stat1SecRequest;
import zermia.proto.ProtoRuntime.StatsReply;
import zermia.proto.ProtoRuntime.StatsRequest;
import zermia.proto.ZermiaServicesGrpc.ZermiaServicesImplBase;
import zermia.replica.ZermiaReplicaList;
import zermia.server.ZermiaServerMain;
import zermia.stats.ZermiaStats;

public class ZermiaRuntimeServices extends ZermiaServicesImplBase{
	ZermiaReplicaList replicaList = new ZermiaReplicaList();
	ZermiaProperties props = new ZermiaProperties();
	static ZermiaProperties props2 = new ZermiaProperties();
	ZermiaStats zStats = new ZermiaStats();
	protected ReentrantLock z_lock = new ReentrantLock();
	static TreeMap<Integer, ArrayList<String>> replicaStats = new TreeMap<Integer, ArrayList<String>>();
	static Integer repListSize;
	static TreeMap<Integer,Boolean> jumperVClist = new TreeMap<Integer,Boolean>();
	

	@Override
	public void firstConnection(ConnectionRequest request, StreamObserver<ConnectionReply> responseObserver) {
		props.loadProperties();
		Logger.getLogger(ZermiaRuntimeServices.class.getName()).log(Level.INFO, "Connection Request from Replica number " + request.getReplicaID());
		
		replicaList.getReplica(request.getReplicaID()).setNewFileTest();
		
		if(replicaList.checkReplicaExistence(request.getReplicaID())){
			if(replicaList.getReplica(request.getReplicaID()).getFaultness()) {
				ConnectionReply rep = ConnectionReply.newBuilder()
						.setReplicaStatus(true)
						.setFaultScheduleSize(replicaList.getReplica(request.getReplicaID()).getFaultPamList().size())
						.build();
				
				responseObserver.onNext(rep);
				responseObserver.onCompleted();
				Logger.getLogger(ZermiaRuntimeServices.class.getName()).log(Level.INFO, "Connection successful to Faulty Replica number " + request.getReplicaID());	
			} else {
				ConnectionReply rep = ConnectionReply.newBuilder()
						.setReplicaStatus(false) //correct replica
						.setFaultScheduleSize(0)
						.build();
				
				responseObserver.onNext(rep);
				responseObserver.onCompleted();
				Logger.getLogger(ZermiaRuntimeServices.class.getName()).log(Level.INFO, "Connection successful to non-faulty Replica number " + request.getReplicaID());	
			}
		}
		try {
			replicaList.getReplica(request.getReplicaID()).checkForExcelFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//----------------------------------------------------------------------------------//	

	@Override
	public void secondConnection(ConnectionSecondRequest request, StreamObserver<ConnectionSecondReply> responseObserver) {
	props.loadProperties();
	ArrayList<Boolean> arrayReply = new ArrayList<Boolean>();
	arrayReply.add(replicaList.getReplica(request.getReplicaID()).getVc());	//viewchange
	arrayReply.add(replicaList.getReplica(request.getReplicaID()).getCk());	//checkpoint
	//arrayReply.add(props.getCs());	//consensus
	
	arrayReply.add(replicaList.getReplica(request.getReplicaID()).getFw());	//forwarded
	
	arrayReply.add(replicaList.getReplica(request.getReplicaID()).getFocusPrimary()); //primary focused attack
	
	arrayReply.add(replicaList.getReplica(request.getReplicaID()).getClientReply()); //Client Reply
	
	arrayReply.add(replicaList.getReplica(request.getReplicaID()).getCsPrP()); //consensus pre-prepare
	arrayReply.add(replicaList.getReplica(request.getReplicaID()).getCsPr()); //consensus prepare
	arrayReply.add(replicaList.getReplica(request.getReplicaID()).getCsCm()); //consensus commit
	
	System.out.println(arrayReply);
	
	ConnectionSecondReply rep = ConnectionSecondReply.newBuilder()
			.addAllMessageTypes(arrayReply)
			.build();
	
	responseObserver.onNext(rep);
	responseObserver.onCompleted();
	}
	
	
//----------------------------------------------------------------------------------//
	
	@Override
	public void faultService(FaultActivationRequest request, StreamObserver<FaultActivationReply> responseObserver) {
		Logger.getLogger(ZermiaRuntimeServices.class.getName()).log(Level.INFO, "Fault ACTIVATION Request from runtime Replica : " + request.getReplicaID());			

		ArrayList<String> fault_PamList = new ArrayList<String>();
		ArrayList<String> run_TriggerList = new ArrayList<String>();
		
		fault_PamList = replicaList.getReplica(request.getReplicaID()).getFaultPamList().get(request.getFaultScheduleIterator());
		run_TriggerList = replicaList.getReplica(request.getReplicaID()).getRunsTriggerList().get(request.getFaultScheduleIterator());
				
		FaultActivationReply runFaultAct = FaultActivationReply.newBuilder()
				.addAllFaultPam(fault_PamList)
				.addAllRunTrigger(run_TriggerList)
				.build();
				
		responseObserver.onNext(runFaultAct);
		responseObserver.onCompleted();	
		}
	
//----------------------------------------------------------------------------------//
	
	@Override
	public void statsService(StatsRequest request, StreamObserver<StatsReply> responseObserver) {
		props.loadProperties();
		repListSize = props.getNumberOfReplicas();
		replicaList.getReplica(request.getReplicaID()).setMessagesSentTotal(request.getMessageTotal());
		replicaList.getReplica(request.getReplicaID()).setTimeFinish(request.getTimeFinal());
		
		StatsReply sReply = StatsReply.newBuilder()
				.build();
		
		responseObserver.onNext(sReply);
		responseObserver.onCompleted();
		
		z_lock.lock();	
		zStats = new ZermiaStats();
		
		try {
			zStats.checkForExcelFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		replicaStats.put(Integer.parseInt(request.getReplicaID()), zStats.calculateAll2(request.getReplicaID()));
		
//		try {
//			replicaList.getReplica(request.getReplicaID()).fillExcelFile();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		if(repListSize <= replicaStats.size() ) {
			for(int k = 0; k<replicaStats.size();k++) {
				Logger.getLogger(ZermiaServerMain.class.getName()).log(Level.INFO, "------------- Replica " + k + " END OF TEST STATS --------------");
				
				System.out.println("Replica " + k + " END TIME : " + replicaStats.get(k).get(0) + " seconds");
				System.out.println("Replica " + k + " THROUGHPUT : " + replicaStats.get(k).get(1) + " messages per second");
				System.out.println("Replica " + k + " total messages : " + replicaStats.get(k).get(2) + " messages");
				System.out.println("Replica " + k + " average latency: " + replicaStats.get(k).get(3) + " millisenconds");
				
				try {
					zStats.fillExcelFile(k,replicaStats.get(k).get(0), replicaStats.get(k).get(2), replicaStats.get(k).get(1),replicaStats.get(k).get(3));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				zStats.fillExcelTestSeparator();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			replicaStats.clear();
		}
		z_lock.unlock();
	}
	
	
//----------------------------------------------------------------------------------//	
	@Override
	public void stat1SecService(Stat1SecRequest request, StreamObserver<Stat1SecReply> responseObserver) {
		
		replicaList.getReplica(request.getReplicaID()).getTP1secList().put(request.getMessage1Sec(), request.getMessage1Throughput());

		try {
			replicaList.getReplica(request.getReplicaID()).fillExcelFile2(request.getMessage1Sec(), request.getMessage1Throughput());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Stat1SecReply rep = Stat1SecReply.newBuilder()
				.setTeste("0")
				.build();
		
		responseObserver.onNext(rep);
		responseObserver.onCompleted();	
	}
		
//----------------------------------------------------------------------------------//
	
}
	

