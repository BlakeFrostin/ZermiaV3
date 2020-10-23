package zermia.faults;

import zermia.runtime.ZermiaFault;

public class PacketDropperFault extends ZermiaFault{

	public boolean executeFault(Integer faultChance) {
		Integer randVal = randomGen.nextInt(100);
	   	if(randVal > faultChance) {
    		return false;
    	} else if (randVal <= faultChance) {
    		return true;
    	} else { 
    		return false; 
    	}
    }
}

