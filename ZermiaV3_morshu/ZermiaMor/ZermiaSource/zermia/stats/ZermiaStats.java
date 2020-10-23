package zermia.stats;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import zermia.server.ZermiaServerMain;
import zermia.server.ZermiaServerReplicaList;

public class ZermiaStats {

	ZermiaServerReplicaList replicaList = new ZermiaServerReplicaList();
	
	double total;
	double total2;
	double timeTotal;
	Integer messagesSent;
	
	public void calculateAll(String replicaID) {
		
		Logger.getLogger(ZermiaServerMain.class.getName()).log(Level.INFO, "------------- Replica " + replicaID + " END OF TEST STATS --------------");
		
		messagesSent = replicaList.getReplica(replicaID).getMessagesSentTotal();
		timeTotal = replicaList.getReplica(replicaID).getTimeFinish()/1000;
		timeTotal = (double) Math.round(timeTotal*100)/100;
		total = (int) Math.round(messagesSent/timeTotal);
		
		System.out.println("Replica " + replicaID + " END TIME : " + timeTotal + " seconds");
		System.out.println("Replica " + replicaID + " THROUGHPUT : " + total + " messages per second");
		System.out.println("Replica " + replicaID + " total messages : " + messagesSent + " messages");
		
		
		total2 = (1/total);
		total2 = (double) Math.round(total2*10000)/10;
		System.out.println("Replica " + replicaID + " average latency: " + total2 + " millisenconds");
		}
	
	public ArrayList<String> calculateAll2(String replicaID) {
		ArrayList<String> repStatsCalc = new ArrayList<String>();
		
		messagesSent = replicaList.getReplica(replicaID).getMessagesSentTotal();
		timeTotal = replicaList.getReplica(replicaID).getTimeFinish()/1000;
		timeTotal = (double) Math.round(timeTotal*100)/100;
		total = (int) Math.round(messagesSent/timeTotal);
		
		total2 = (1/total);
		total2 = (double) Math.round(total2*10000)/10;
		
		repStatsCalc.add(0, String.valueOf(timeTotal));
		repStatsCalc.add(1, String.valueOf(total));
		repStatsCalc.add(2, String.valueOf(messagesSent));
		repStatsCalc.add(3, String.valueOf(total2));
		return repStatsCalc;
		}
	
}
