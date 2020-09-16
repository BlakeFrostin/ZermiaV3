package zermia.proto.services;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.stub.StreamObserver;
import zermia.proto.ProtoRuntime.ConnectionReply;
import zermia.proto.ProtoRuntime.ConnectionRequest;
import zermia.proto.ProtoRuntime.FaultActivationReply;
import zermia.proto.ProtoRuntime.FaultActivationRequest;
import zermia.proto.ZermiaServicesGrpc.ZermiaServicesImplBase;
import zermia.server.ZermiaServerReplicaList;

public class ZermiaRuntimeServices extends ZermiaServicesImplBase {
	ZermiaServerReplicaList replicaList = new ZermiaServerReplicaList();
	
	@Override
	public void firstConnection(ConnectionRequest request, StreamObserver<ConnectionReply> responseObserver) {
		Logger.getLogger(ZermiaRuntimeServices.class.getName()).log(Level.INFO, "Connection Request from Faulty Replica number " + request.getReplicaID());
			
		if(replicaList.checkReplicaExistence(request.getReplicaID())){
			if(replicaList.getReplica(request.getReplicaID()).getFaultness()) {
				ConnectionReply rep = ConnectionReply.newBuilder()
						.setConnectionStatus(true)
						.setReplicaStatus(true) //faulty replica
						.build();
				
				responseObserver.onNext(rep);
				responseObserver.onCompleted();
				Logger.getLogger(ZermiaRuntimeServices.class.getName()).log(Level.INFO, "Connection successful to Correct Replica number " + request.getReplicaID());	
			} else {
				ConnectionReply rep = ConnectionReply.newBuilder()
						.setConnectionStatus(true)
						.setReplicaStatus(false) //correct replica
						.build();
				
				responseObserver.onNext(rep);
				responseObserver.onCompleted();
				Logger.getLogger(ZermiaRuntimeServices.class.getName()).log(Level.INFO, "Connection successful to non-faulty Replica number " + request.getReplicaID());	
			}
			
		}else {
			ConnectionReply rep = ConnectionReply.newBuilder()
					.setConnectionStatus(false)
					.setReplicaStatus(false)
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
		if(request.getCurrentRun() == 50) {
			fault_PamList = replicaList.getReplica(request.getReplicaID()).getFaultPamList().get(0);
			run_TriggerList = replicaList.getReplica(request.getReplicaID()).getRunsTriggerList().get(0);
			
			FaultActivationReply runFaultAct = FaultActivationReply.newBuilder()
					.addAllFaultPam(fault_PamList)
					.addAllRunTrigger(run_TriggerList)
					.build();
			
			responseObserver.onNext(runFaultAct);
			responseObserver.onCompleted();	
		}	    																																				
		else if (replicaList.getReplica(request.getReplicaID()).getIteratorList().contains(request.getCurrentRun())){
			if(replicaList.getReplica(request.getReplicaID()).getIteratorList().size()-1 == replicaList.getReplica(request.getReplicaID()).getIteratorList().indexOf(request.getCurrentRun())) {
				fault_PamList = new ArrayList<String>();
				run_TriggerList = new ArrayList<String>();
				fault_PamList.add(0,"false");
				run_TriggerList.add(0,"0");
				run_TriggerList.add(1,"0");
				FaultActivationReply runFaultAct = FaultActivationReply.newBuilder()
						.addAllFaultPam(fault_PamList)
						.addAllRunTrigger(run_TriggerList)
						.build();
				responseObserver.onNext(runFaultAct);
				responseObserver.onCompleted();	
			} else {
				Integer indexIterator = replicaList.getReplica(request.getReplicaID()).getIteratorList().indexOf(request.getCurrentRun()) + 1;
				fault_PamList = new ArrayList<String>();
				run_TriggerList = new ArrayList<String>();
				fault_PamList = replicaList.getReplica(request.getReplicaID()).getFaultPamList().get(indexIterator);
				run_TriggerList = replicaList.getReplica(request.getReplicaID()).getRunsTriggerList().get(indexIterator);
					
				FaultActivationReply runFaultAct = FaultActivationReply.newBuilder()
						.addAllFaultPam(fault_PamList)
						.addAllRunTrigger(run_TriggerList)
						.build();
				
				responseObserver.onNext(runFaultAct);
				responseObserver.onCompleted();	
			}
		} else { // case for when a replica is added midway and does not know the faults
			Integer diff = replicaList.getReplica(request.getReplicaID()).currentRunDiff(request.getCurrentRun());
			// case where it has started when there is no faults available to be triggered
			if(diff == null) {
				fault_PamList = new ArrayList<String>();
				run_TriggerList = new ArrayList<String>();
				fault_PamList.add(0,"false");
				run_TriggerList.add(0,"0");
				run_TriggerList.add(1,"0");
				FaultActivationReply runFaultAct = FaultActivationReply.newBuilder()
						.addAllFaultPam(fault_PamList)
						.addAllRunTrigger(run_TriggerList)
						.build();
				responseObserver.onNext(runFaultAct);
				responseObserver.onCompleted();	
			} else { // case where there are faults yet to be triggered
				Integer indexIterator = replicaList.getReplica(request.getReplicaID()).getIteratorList().indexOf(diff);
				fault_PamList = new ArrayList<String>();
				run_TriggerList = new ArrayList<String>();
				fault_PamList = replicaList.getReplica(request.getReplicaID()).getFaultPamList().get(indexIterator);
				run_TriggerList = replicaList.getReplica(request.getReplicaID()).getRunsTriggerList().get(indexIterator);
					
				FaultActivationReply runFaultAct = FaultActivationReply.newBuilder()
						.addAllFaultPam(fault_PamList)
						.addAllRunTrigger(run_TriggerList)
						.build();
				
				responseObserver.onNext(runFaultAct);
				responseObserver.onCompleted();				
			}
		}	
	}

//----------------------------------------------------------------------------------//
}
