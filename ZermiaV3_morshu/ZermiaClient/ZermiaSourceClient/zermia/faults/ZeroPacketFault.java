package zermia.faults;

import zermia.runtime.ZermiaFault;

public class ZeroPacketFault extends ZermiaFault{

	public byte[] executeFault(Integer faultChance, byte[] data ) {
		Integer randVal = randomGen.nextInt(100);
	   	if(randVal > faultChance) {
    	} else if(randVal <= faultChance){
	   		for (int i = 0; i < data.length; i++) {
                data[i] = 0;
             }
    	}
	   	return data; 
    }
}
