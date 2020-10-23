package zermia.faults;

import zermia.runtime.ZermiaFault;

public class CrashFault extends ZermiaFault{

	public void executeFault() {
		System.out.println("Crash");
		System.exit(-1);
		}	
}
