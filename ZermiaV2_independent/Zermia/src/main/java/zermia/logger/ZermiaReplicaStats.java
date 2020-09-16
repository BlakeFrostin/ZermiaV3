package zermia.logger;

import java.time.Duration;
import java.time.Instant;

public class ZermiaReplicaStats {
	static Instant start;
	static Instant end;
	
	public void startTimer() {
		start = Instant.now();
	}

	public void endTimer() {
		end = Instant.now();
		System.out.println("Test end at " + Duration.between(start, end).toMillis() + " milliseconds");
	}	
}
