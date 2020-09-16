package zermia.local.boot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import zermia.properties.ZermiaLibsLocation;
import zermia.properties.ZermiaProperties;

public class ZermiaReplicaLocalBoot {
	
	public boolean bootStrapReplica(String replicaCode, String replicaN) throws IOException {
		ZermiaProperties props = new ZermiaProperties();
		props.loadProperties();
		String libs = ZermiaLibsLocation.getLibs();
		
		Logger.getLogger(ZermiaReplicaLocalBoot .class.getName()).log(Level.INFO, "Starting bootStrap for replica number " + replicaN );
		String command1="cmd.exe";
		String command2="/c start java -cp " + libs + " " + replicaCode;
		//String command2="/c  java -cp " + libs + " " + replicaCode;
		ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList(new String[] {command1,command2}));
			
		processBuilder.redirectErrorStream(true);
		processBuilder.directory(new File(props.getWorkingDir()));
						
		try {
			final Process proc = processBuilder.start();
			 new Thread() {
	                @Override
	                public void run() {
	                    String s;
	                    BufferedReader stdout = new BufferedReader(
	                            new InputStreamReader(proc.getInputStream()));
	                    try {
	                        while ((s = stdout.readLine()) != null) {
	                            System.out.println(s);
	                        }
	                    } catch (IOException ex) {
	                        Logger.getLogger(ZermiaReplicaLocalBoot.class.getName()).log(Level.SEVERE,"BootStrap error");
	                    }
	                }
	            }.start();
	            try {
	                int exitValue = proc.exitValue();
	                if (exitValue != 0) {
	                    Logger.getLogger(ZermiaReplicaLocalBoot.class.getName()).log(Level.SEVERE,"BootStrap terminated");

	                    return false;
	                }
	            } catch (IllegalThreadStateException pEx) {
	            }
				
		} catch(Exception e) {
			e.printStackTrace();
		}		
		return true;
	}	
}
