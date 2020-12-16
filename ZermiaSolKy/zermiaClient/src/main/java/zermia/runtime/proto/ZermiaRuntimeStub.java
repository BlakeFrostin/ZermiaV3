package zermia.runtime.proto;

import java.util.ArrayList;

import io.grpc.ManagedChannel;
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
		
		replyArray.add(String.valueOf(rep.getReplicaStatus()));
		replyArray.add(String.valueOf(rep.getFaultScheduleSize()));
		
		return replyArray;
	}
	
//----------------------------------------------------------------------------------//		
	
	public ArrayList<Boolean> runtimeSecondConnection(String replicaID){
		ArrayList<Boolean> replyArray = new ArrayList<Boolean>();
		runtimeBlockStub = ZermiaServicesGrpc.newBlockingStub(z_managedChannel);
		
		ConnectionSecondRequest req = ConnectionSecondRequest.newBuilder()
				.setReplicaID(replicaID)
				.build();
		
		ConnectionSecondReply rep = runtimeBlockStub.secondConnection(req);
		
		replyArray.addAll(rep.getMessageTypesList());
		
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
	
//----------------------------------------------------------------------------------//
	
	public void runtimeStat1SecService(String replicaID, Integer messageSec, Integer messagesSent1Sec) {
		runtimeBlockStub = ZermiaServicesGrpc.newBlockingStub(z_managedChannel);
		
		Stat1SecRequest req = Stat1SecRequest.newBuilder()
				.setReplicaID(replicaID)
				.setMessage1Sec(messageSec)
				.setMessage1Throughput(messagesSent1Sec)
				.build();
		
		Stat1SecReply rep = runtimeBlockStub.stat1SecService(req);
	}
	
	
}
