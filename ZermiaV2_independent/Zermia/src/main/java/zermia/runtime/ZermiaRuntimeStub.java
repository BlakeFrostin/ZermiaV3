package zermia.runtime;

import java.util.ArrayList;

import io.grpc.ManagedChannel;
import zermia.proto.ProtoRuntime.ConnectionReply;
import zermia.proto.ProtoRuntime.ConnectionRequest;
import zermia.proto.ProtoRuntime.FaultActivationReply;
import zermia.proto.ProtoRuntime.FaultActivationRequest;
import zermia.proto.ZermiaServicesGrpc;


public class ZermiaRuntimeStub {
	static ZermiaRuntimeChannel z_channel = new ZermiaRuntimeChannel();
	static {
		z_channel.ChannelCreation();
	}
	static ManagedChannel z_managedChannel = z_channel.getChannel();
	
	ZermiaServicesGrpc.ZermiaServicesBlockingStub runtimeBlockStub;
	
//----------------------------------------------------------------------------------//
	public ArrayList<Boolean> runtimeFirstConnection(String replicaID) {	
		ArrayList<Boolean> replyArray = new ArrayList<Boolean>();
		runtimeBlockStub = ZermiaServicesGrpc.newBlockingStub(z_managedChannel);
		
		ConnectionRequest req = ConnectionRequest.newBuilder()
				.setReplicaID(replicaID)
				.build();
		
		ConnectionReply rep = runtimeBlockStub.firstConnection(req);
		
		replyArray.add(rep.getConnectionStatus());
		replyArray.add(rep.getReplicaStatus());
		
		return replyArray;
	}	
//----------------------------------------------------------------------------------//
 
	public ArrayList<ArrayList<String>> runtimeFaultActivation(String replicaID, Integer currentRun) {
		ArrayList<ArrayList<String>> replyArray = new ArrayList<ArrayList<String>>();
		ArrayList<String> faultsPam = new ArrayList<String>();
		ArrayList<String> runTriggers = new ArrayList<String>();
		runtimeBlockStub = ZermiaServicesGrpc.newBlockingStub(z_managedChannel);
		
		FaultActivationRequest req = FaultActivationRequest.newBuilder()
				.setReplicaID(replicaID)
				.setCurrentRun(currentRun)		//
				.build();
		
		FaultActivationReply rep = runtimeBlockStub.faultService(req);
		
		faultsPam.addAll(rep.getFaultPamList());
		runTriggers.addAll(rep.getRunTriggerList());
		
		replyArray.add(0,faultsPam);
		replyArray.add(1,runTriggers);
		
		return replyArray;
	}
//----------------------------------------------------------------------------------//	
	
}
