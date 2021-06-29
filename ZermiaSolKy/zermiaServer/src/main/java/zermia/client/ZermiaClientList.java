package zermia.client;

import java.util.concurrent.ConcurrentHashMap;

public class ZermiaClientList {
	static ConcurrentHashMap<String,ZermiaClient> clientList = new ConcurrentHashMap<String,ZermiaClient>();
	
//----------------------------------------------------------------------------------//	
	
	public void addClient(String c_id, ZermiaClient client) {
		clientList.put(c_id, client);
	}
	
	public ZermiaClient getClient(String c_id) {
		return clientList.get(c_id);
	}

//----------------------------------------------------------------------------------//	
	
	
}
