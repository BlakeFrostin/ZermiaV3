package zermia.server;

import java.util.concurrent.ConcurrentHashMap;

public class ZermiaServerClientList {
	static ConcurrentHashMap<String,ZermiaServerClient> clientList = new ConcurrentHashMap<String,ZermiaServerClient>();
	
//----------------------------------------------------------------------------------//	
	
	public void addReplica(String c_id, ZermiaServerClient client) {
		clientList.put(c_id, client);
	}
		
	public ZermiaServerClient getReplica(String c_id) {
		return clientList.get(c_id);
	}

//----------------------------------------------------------------------------------//
	
}
