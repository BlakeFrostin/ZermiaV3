package zermia.proto.services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.stub.StreamObserver;
import zermia.properties.ZermiaProperties;
import zermia.proto.ProtoRuntime.ConnectionReply;
import zermia.proto.ProtoRuntime.ConnectionRequest;
import zermia.proto.ProtoRuntime.FaultActivationReply;
import zermia.proto.ProtoRuntime.FaultActivationRequest;
import zermia.proto.ProtoRuntime.StatsReply;
import zermia.proto.ProtoRuntime.StatsRequest;
import zermia.proto.ZermiaServicesGrpc.ZermiaServicesImplBase;
import zermia.server.ZermiaServerMain;
import zermia.server.ZermiaServerReplicaList;
import zermia.stats.ZermiaStats;

public class ZermiaRuntimeServices extends ZermiaServicesImplBase {
	ZermiaServerReplicaList replicaList = new ZermiaServerReplicaList();
	ZermiaStats zStats = new ZermiaStats();
	protected ReentrantLock z_lock = new ReentrantLock();
	static TreeMap<Integer, ArrayList<String>> replicaStats = new TreeMap<Integer, ArrayList<String>>();
	static Integer repListSize;
	static {
		ZermiaProperties props = new ZermiaProperties();
		props.loadProperties();
		repListSize = props.getNumberOfReplicas();
	}
	
	
	@Override
	public void firstConnection(ConnectionRequest request, StreamObserver<ConnectionReply> responseObserver) {
		Logger.getLogger(ZermiaRuntimeServices.class.getName()).log(Level.INFO, "Connection Request from Replica number " + request.getReplicaID());
			
		if(replicaList.checkReplicaExistence(request.getReplicaID())){
			if(replicaList.getReplica(request.getReplicaID()).getFaultness()) {
				ConnectionReply rep = ConnectionReply.newBuilder()
						.setConnectionStatus(true)
						.setReplicaStatus(true) //faulty replica
						.setFaultScheduleSize(replicaList.getReplica(request.getReplicaID()).getFaultPamList().size())
						.build();
				
				responseObserver.onNext(rep);
				responseObserver.onCompleted();
				Logger.getLogger(ZermiaRuntimeServices.class.getName()).log(Level.INFO, "Connection successful to Faulty Replica number " + request.getReplicaID());	
			} else {
				ConnectionReply rep = ConnectionReply.newBuilder()
						.setConnectionStatus(true)
						.setReplicaStatus(false) //correct replica
						.setFaultScheduleSize(0)
						.build();
				
				responseObserver.onNext(rep);
				responseObserver.onCompleted();
				Logger.getLogger(ZermiaRuntimeServices.class.getName()).log(Level.INFO, "Connection successful to non-faulty Replica number " + request.getReplicaID());	
			}
			
		}else {
			ConnectionReply rep = ConnectionReply.newBuilder()
					.setConnectionStatus(false)
					.setReplicaStatus(false)
					.setFaultScheduleSize(0)
					.build();
			
			responseObserver.onNext(rep);
			responseObserver.onCompleted();
			//This happens when it is not added to the properties file and i promptly shutdown that node
			Logger.getLogger(ZermiaRuntimeServices.class.getName()).log(Level.INFO, "Connection refused to Replica number " + request.getReplicaID());	
		}	
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
//	@Override
//	public void statsService(StatsRequest request, StreamObserver<StatsReply> responseObserver) {
//		
//	//	Logger.getLogger(ZermiaRuntimeServices.class.getName()).log(Level.INFO, "Stats Request from runtime Replica : " + request.getReplicaID());			
//		
//		replicaList.getReplica(request.getReplicaID()).setMessagesSentTotal(request.getMessageTotal());
//		replicaList.getReplica(request.getReplicaID()).setTimeFinish(request.getTimeFinal());
//		
//		StatsReply sReply = StatsReply.newBuilder()
//				.build();
//		
//		responseObserver.onNext(sReply);
//		responseObserver.onCompleted();
//		z_lock.lock();
//		zStats = new ZermiaStats();
//		zStats.calculateAll(request.getReplicaID()); //final calculations of throughput and stuff
//		z_lock.unlock();
//	}
	
	@Override
	public void statsService(StatsRequest request, StreamObserver<StatsReply> responseObserver) {
			
		replicaList.getReplica(request.getReplicaID()).setMessagesSentTotal(request.getMessageTotal());
		replicaList.getReplica(request.getReplicaID()).setTimeFinish(request.getTimeFinal());
		
		StatsReply sReply = StatsReply.newBuilder()
				.build();
		
		responseObserver.onNext(sReply);
		responseObserver.onCompleted();
		
		z_lock.lock();	
		zStats = new ZermiaStats();
		
		replicaStats.put(Integer.parseInt(request.getReplicaID()), zStats.calculateAll2(request.getReplicaID()));
		
		if(repListSize <= replicaStats.size() ) {
			for(int k = 0; k<replicaStats.size();k++) {
				Logger.getLogger(ZermiaServerMain.class.getName()).log(Level.INFO, "------------- Replica " + k + " END OF TEST STATS --------------");
				
				System.out.println("Replica " + k + " END TIME : " + replicaStats.get(k).get(0) + " seconds");
				System.out.println("Replica " + k + " THROUGHPUT : " + replicaStats.get(k).get(1) + " messages per second");
				System.out.println("Replica " + k + " total messages : " + replicaStats.get(k).get(2) + " messages");
				System.out.println("Replica " + k + " average latency: " + replicaStats.get(k).get(3) + " millisenconds");
			}
			replicaStats.clear();
		}
		z_lock.unlock();
	}
	
	
}
