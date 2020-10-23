package zermia.runtime;

import java.util.ArrayList;

import io.grpc.ManagedChannel;
import zermia.proto.ProtoRuntime.ConnectionReply;
import zermia.proto.ProtoRuntime.ConnectionRequest;
import zermia.proto.ProtoRuntime.FaultActivationReply;
import zermia.proto.ProtoRuntime.FaultActivationRequest;
import zermia.proto.ProtoRuntime.StatsReply;
import zermia.proto.ProtoRuntime.StatsRequest;
import zermia.proto.ZermiaServicesGrpc;


public class ZermiaRuntimeStub {
	static ZermiaRuntimeChannel z_channel = new ZermiaRuntimeChannel();
	static {
		z_channel.ChannelCreation();
	}
	static ManagedChannel z_managedChannel = z_channel.getChannel();
	
	ZermiaServicesGrpc.ZermiaServicesBlockingStub runtimeBlockStub;
	
//----------------------------------------------------------------------------------//
	public ArrayList<String> runtimeFirstConnection(String replicaID) {	
		ArrayList<String> replyArray = new ArrayList<String>();
		runtimeBlockStub = ZermiaServicesGrpc.newBlockingStub(z_managedChannel);
		
		ConnectionRequest req = ConnectionRequest.newBuilder()
				.setReplicaID(replicaID)
				.build();
		
		ConnectionReply rep = runtimeBlockStub.firstConnection(req);
		
		replyArray.add(String.valueOf(rep.getConnectionStatus()));
		replyArray.add(String.valueOf(rep.getReplicaStatus()));
		replyArray.add(String.valueOf(rep.getFaultScheduleSize()));
		
		return replyArray;
	}	
//----------------------------------------------------------------------------------//
 
	public ArrayList<ArrayList<String>> runtimeFaultActivation(String replicaID, Integer fScheduleIterator) {
		ArrayList<ArrayList<String>> replyArray = new ArrayList<ArrayList<String>>();
		ArrayList<String> faultsPam = new ArrayList<String>();
		ArrayList<String> runTriggers = new ArrayList<String>();
		
		runtimeBlockStub = ZermiaServicesGrpc.newBlockingStub(z_managedChannel);
		
		FaultActivationRequest req = FaultActivationRequest.newBuilder()
				.setReplicaID(replicaID)
				.setFaultScheduleIterator(fScheduleIterator)
				.build();
		
		FaultActivationReply rep = runtimeBlockStub.faultService(req);
		
		faultsPam.addAll(rep.getFaultPamList());
		runTriggers.addAll(rep.getRunTriggerList());
		
		replyArray.add(0,faultsPam);
		replyArray.add(1,runTriggers);
		
		return replyArray;
	}
//----------------------------------------------------------------------------------//	
	
	public void runtimeStatsService(String replicaID, double timeFinish, Integer messagesSent) {
		
		runtimeBlockStub = ZermiaServicesGrpc.newBlockingStub(z_managedChannel);
		
		StatsRequest req = StatsRequest.newBuilder()
				.setReplicaID(replicaID)
				.setTimeFinal(timeFinish)
				.setMessageTotal(messagesSent)
				.build();
		
		StatsReply rep = runtimeBlockStub.statsService(req);	
	}
	
}
