package zermia.faults;

import zermia.runtime.ZermiaFault;

public class ThreadDelayFault extends ZermiaFault{

	public void executeFault(Integer duration) throws InterruptedException {
		Thread.sleep(duration);
	}

}
