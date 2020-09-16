package zermia.faults;

public class ThreadDelayFault {
	protected Integer faultDuration;
	
	public void executeFault(Integer duration) throws InterruptedException {
		Thread.sleep(duration);
	}	
}
