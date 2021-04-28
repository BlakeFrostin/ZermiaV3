package zermia.replica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.function.Function;
import java.util.stream.Collectors;

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
	
	public ArrayList<String> returnFRepList(){
		ArrayList<String> tt = new ArrayList<String>();
		for(ZermiaReplica k : replicasList.values()) {
			if(k.getFaultness()) {
				tt.add(k.getID());
			}
		}
		return tt;
	}
	
	public ArrayList<String> returnAllRepList(){
		ArrayList<String> tt = new ArrayList<String>();
		for(ZermiaReplica k : replicasList.values()) {
			if(Integer.valueOf(k.getID())<100) {
				tt.add(k.getID());	
			}
		}
		return tt;
	}
	
	public Integer getLenght() {
		return replicasList.size();
	}	
	
	
	public Set<String> getKeySet() {
		return replicasList.keySet();
	}
	
	public Integer highestNumber() {
		List<String> l = new ArrayList<String>(replicasList.keySet());
		
		List<Integer> lInt = convertStringListToIntList( l, Integer::parseInt);
		
		return Collections.max(lInt);
		
	}
	
    public static <T, U> List<U> convertStringListToIntList(List<T> listOfString, Function<T, U> function) 
    { 
        return listOfString.stream() 
            .map(function) 
            .collect(Collectors.toList()); 
    } 
  
	
}
