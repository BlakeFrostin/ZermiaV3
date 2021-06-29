package zermia.faults;

public class ThreadDelayFault{

	public void executeFault(Integer duration) throws InterruptedException {
		Thread.sleep(duration);
	}

}
