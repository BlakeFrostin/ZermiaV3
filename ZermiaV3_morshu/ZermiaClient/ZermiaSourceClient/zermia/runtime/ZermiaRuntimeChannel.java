package zermia.runtime;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import zermia.properties.ZermiaRuntimeProperties;

public class ZermiaRuntimeChannel {
	ZermiaRuntimeProperties props = new ZermiaRuntimeProperties();
	ManagedChannel mgChannel;
	
	public void ChannelCreation() {
		props.loadProperties();
		 mgChannel = ManagedChannelBuilder.forAddress(props.getOrchestratorIP(), props.getOrchestratorPort())
				 .usePlaintext()
				 .build(); 		
	}

	public void ChannelClose(ManagedChannel channelShutDown) {
		mgChannel.shutdown();
	}
	
	public ManagedChannel getChannel() {
		return mgChannel;
	}
		
}
