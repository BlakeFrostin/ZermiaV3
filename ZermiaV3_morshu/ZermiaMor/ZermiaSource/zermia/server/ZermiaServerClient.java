package zermia.server;

public class ZermiaServerClient {
	String clientID;
	String clientIP;
	
	public ZermiaServerClient(String c_id, String c_ip) {
		clientID = c_id;
		clientIP = c_ip;
	}
	
	public String getID() {
		return clientID;
	}
	
	public String getIP() {
		return clientIP;
	}
	
}
