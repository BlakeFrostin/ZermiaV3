package zermia.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZermiaServerReplicaList {
	static ConcurrentHashMap<String,ZermiaServerReplica> replicasList = new ConcurrentHashMap<String,ZermiaServerReplica>();
	
//----------------------------------------------------------------------------------//	
	
	public void addReplica(String idRep, ZermiaServerReplica replica) {
		replicasList.put(idRep, replica);
	}
	
	public ZermiaServerReplica getReplica(String idRep) {
		return replicasList.get(idRep);
	}

//----------------------------------------------------------------------------------//		
	
	public boolean checkReplicaExistence(String idRep) {
		if(replicasList.containsKey(idRep)) {
			return true;
		} else return false;	
	}
		
//----------------------------------------------------------------------------------//	
	
	public Integer getNumberOfFaultyReplicas() {
		int n=0;
		for(Map.Entry<String, ZermiaServerReplica> rep : replicasList.entrySet()) {
			if(rep.getValue().getFaultness() == true) {
				n++;
			}
		}
		return n;
	}
	
//----------------------------------------------------------------------------------//	
}
