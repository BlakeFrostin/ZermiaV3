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
import zermia.proto.ProtoRuntime.clientFaultActivationReply;
import zermia.proto.ProtoRuntime.clientFaultActivationRequest;
import zermia.proto.ProtoRuntime.clientInitialReply;
import zermia.proto.ProtoRuntime.clientInitialRequest;
import zermia.proto.ZermiaServicesGrpc;

public class ZermiaRuntimeStub {
	static ZermiaRuntimeChannel z_channel = new ZermiaRuntimeChannel();
	static {
		z_channel.ChannelCreation();
	}
	static ManagedChannel z_managedChannel = z_channel.getChannel();
	ZermiaServicesGrpc.ZermiaServicesBlockingStub runtimeBlockStub;
	
//----------------------------------------------------------------------------------//	
	
	public ArrayList<ArrayList<String>> runtimeFirstConnection(String replicaID) {	
		ArrayList<ArrayList<String>> replyArray = new ArrayList<ArrayList<String>>();
		runtimeBlockStub = ZermiaServicesGrpc.newBlockingStub(z_managedChannel);
		
		ConnectionRequest req = ConnectionRequest.newBuilder()
				.setReplicaID(replicaID)
				.build();
		
		ConnectionReply rep = runtimeBlockStub.firstConnection(req);
	
		
		ArrayList<String> f1 = new ArrayList<String>();
		ArrayList<String> f2 = new ArrayList<String>();
		ArrayList<String> f3 = new ArrayList<String>();
		ArrayList<String> f4 = new ArrayList<String>();
		
		f1.add(String.valueOf(rep.getReplicaStatus()));
		f2.add(String.valueOf(rep.getFaultScheduleSize()));
		f3.addAll(rep.getFaultRepListList());
		f4.add(String.valueOf(rep.getGroupSize()));
		
		replyArray.add(f1);
		replyArray.add(f2);
		replyArray.add(f3);
		replyArray.add(f4);
		
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
	
	
//----------------------------------------------------------------------------------//	
	//client initial request
	public ArrayList<ArrayList<String>> runtimeClientFirstRequest() {
		ArrayList<ArrayList<String>> replyArray = new ArrayList<ArrayList<String>>();
		runtimeBlockStub = ZermiaServicesGrpc.newBlockingStub(z_managedChannel);
		
		clientInitialRequest req = clientInitialRequest.newBuilder()
				.setT(true)
				.build();
		
		clientInitialReply rep = runtimeBlockStub.clientFirstRequest(req);

		ArrayList<String> f1 = new ArrayList<String>();
		ArrayList<String> f2 = new ArrayList<String>();
		ArrayList<String> f3 = new ArrayList<String>();
		ArrayList<String> f4 = new ArrayList<String>();
		ArrayList<String> f5 = new ArrayList<String>();
		
		f1.add(String.valueOf(rep.getFaultyClientGroupSize()));
		f2.add(String.valueOf(rep.getFaultScheduleSize()));
		f3.addAll(rep.getFaultRepListList());
		f4.add(String.valueOf(rep.getGroupSizeReplicas()));
		f5.addAll(rep.getRepListList());
		
		replyArray.add(f1);
		replyArray.add(f2);
		replyArray.add(f3);
		replyArray.add(f4);
		replyArray.add(f5);
		
		return replyArray;	
	}
	
//----------------------------------------------------------------------------------//	
	//Client fault request 
	public ArrayList<ArrayList<String>> runtimeClientFaultRequest(Integer fScheduleIterator) {
		ArrayList<ArrayList<String>> replyArray = new ArrayList<ArrayList<String>>();
		ArrayList<String> faultsPam = new ArrayList<String>();
		ArrayList<String> runTriggers = new ArrayList<String>();
		
		runtimeBlockStub = ZermiaServicesGrpc.newBlockingStub(z_managedChannel);
		
		clientFaultActivationRequest req = clientFaultActivationRequest.newBuilder()
				.setFaultScheduleIterator(fScheduleIterator)
				.build();
		
		clientFaultActivationReply rep = runtimeBlockStub.clientFaultService(req);
		
		faultsPam.addAll(rep.getFaultPamList());
		runTriggers.addAll(rep.getRunTriggerList());
		
		replyArray.add(0,faultsPam);
		replyArray.add(1,runTriggers);
		
		return replyArray;
	}
	
}
