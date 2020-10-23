package zermia.runtime;

import java.security.SecureRandom;

public abstract class ZermiaFault {

	protected static SecureRandom randomGen = null;
	
    static {
        randomGen = new SecureRandom();
    }

	
}
