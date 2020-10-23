package zermia.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ZermiaLogger {
	public final static Level LEVEL = Level.ALL; //
	
	public static void createLogFile() throws SecurityException, IOException {
		Logger logger = Logger.getLogger("");
		FileHandler handler = new FileHandler("Zermia.log",true);
		SimpleFormatter formatter = new SimpleFormatter();
		handler.setFormatter(formatter);
		handler.setLevel(LEVEL);
		logger.addHandler(handler);	
	}	
}
