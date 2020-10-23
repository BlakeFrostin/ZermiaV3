package zermia.faults;


import zermia.runtime.ZermiaFault;

public class RandomPacketFault extends ZermiaFault{
	protected Integer testR;

	public byte[] executeFault(Integer faultChance, byte[] data) {
		Integer randVal = randomGen.nextInt(100);
	   	if(randVal > faultChance) {
    	} else if (randVal <= faultChance) {
	   		for (int i = 0; i < data.length; i++) {
 		    	testR = randomGen.nextInt(126);
                data[i] = testR.byteValue();
             }
    	}
	  return data;
    }
}
