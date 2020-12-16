package zermia.replica;

import java.util.concurrent.ConcurrentHashMap;

public class ZermiaReplicaList {
	static ConcurrentHashMap<String,ZermiaReplica> replicasList = new ConcurrentHashMap<String,ZermiaReplica>();
	
//----------------------------------------------------------------------------------//	
	
	public void addReplica(String idRep, ZermiaReplica replica) {
		replicasList.put(idRep, replica);
	}	
	
	public ZermiaReplica getReplica(String idRep) {
		return replicasList.get(idRep);
	}

//----------------------------------------------------------------------------------//	
	
	public boolean checkReplicaExistence(String idRep) {
		if(replicasList.containsKey(idRep)) {
			return true;
		} else return false;	
	}	

//----------------------------------------------------------------------------------//	
	
	
}
