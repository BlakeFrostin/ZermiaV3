package zermia.faults;

public class CrashFault {

	public void executeFault() {
		System.out.println("Crash");
		System.exit(-1);
		}	
}
