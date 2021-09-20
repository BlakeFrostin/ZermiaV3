package zermia.replica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;


import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ZermiaReplica {
	String replicaID;
	Boolean faultness = false;
	
	LinkedList<ArrayList <String>> faultPamList = new LinkedList<ArrayList <String>>();
	LinkedList<ArrayList <String>> runTriggerList = new LinkedList<ArrayList <String>>();
	
	TreeMap<Integer,Integer> throughput1secList = new TreeMap<Integer,Integer>();
	
	Integer messagesSentTotal = 0;
	double timeFinish = 0;
	
	Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
	String propPath = path + "/replicaStats/";
	
	boolean fileNewTest = true;
	
	Boolean consensusPrePrepare = false;
	Boolean consensusPrepare = false;
	Boolean consensusCommit = false;
	Boolean clientReply = false;
	
	Boolean viewChange = false;
	Boolean checkPoint = false;
	Boolean forwarded = false;
	
	Boolean focusPrimary = false;
	
	Boolean viewStop = false;
	Boolean viewStopD = false;
	Boolean viewSync= false;
	
//----------------------------------------------------------------------------------//		
	//New lists
	public LinkedList<ArrayList <String>> getFaultPamList() {
		return faultPamList;
	}
	public LinkedList<ArrayList <String>> getRunsTriggerList() {
		return runTriggerList;
	}	
	
	public TreeMap<Integer,Integer> getTP1secList(){
		return throughput1secList;
	}
//----------------------------------------------------------------------------------//	
	
	public ZermiaReplica (String rep_id) {
		replicaID = rep_id;
	}
	
//----------------------------------------------------------------------------------//	
	
	public String getID() {
		return replicaID;
	}
	
	public Boolean getFaultness() {
		return faultness;
	}
	
	public void setFaultness(Boolean faultyOrNot) {
		faultness = faultyOrNot;
	}

	public void setMessagesSentTotal(Integer messagesTotal) {
		messagesSentTotal = messagesTotal;
	}
	
	public Integer getMessagesSentTotal() {
		return messagesSentTotal;
	}
	
	public void setTimeFinish(double timeEnd) {
		timeFinish = timeEnd;
	}
	
	public double getTimeFinish() {
		return timeFinish;
	}
	
	public void setNewFileTest() {
		fileNewTest = true;
	}
	
//----------------------------------------------------------------------------------//	
	//Message Stuff pre-prepare
	public void setCsPrP(Boolean msgType) {
		consensusPrePrepare = msgType;
	}
	
	public Boolean getCsPrP() {
		return consensusPrePrepare;
	}
	
	//Message Stuff prepare
	public void setCsPr(Boolean msgType) {
		consensusPrepare = msgType;
	}
	
	public Boolean getCsPr() {
		return consensusPrepare;
	}
	
	//Message Stuff commit
	public void setCsCm(Boolean msgType) {
		consensusCommit = msgType;
	}
	
	public Boolean getCsCm() {
		return consensusCommit;
	}
	
	//Message Stuff viewchange
	public void setVc(Boolean msgType) {
		viewChange= msgType;
	}
	
	public Boolean getVc() {
		return viewChange;
	}
	
	//Message Stuff checkpoint
	public void setCk(Boolean msgType) {
		checkPoint = msgType;
	}
	
	public Boolean getCk() {
		return checkPoint;
	}
	
	//Message Stuff forwarded messages to primary
	public void setFw(Boolean msgType) {
		forwarded = msgType;
	}
	
	public Boolean getFw() {
		return forwarded;
	}
	
	//Message Stuff focus primary
	public void setFocusPrimary(Boolean msgType) {
		focusPrimary = msgType;
	}
	
	public Boolean getFocusPrimary() {
		return focusPrimary;
	}
	
	//Message Stuff focus primary
	public void setClientReply(Boolean msgType) {
		clientReply = msgType;
	}
	
	public Boolean getClientReply() {
		return clientReply;
	}
	
	
	//first part view change
	public void setVcStop(Boolean msgType) {
		viewStop = msgType;
	}
	
	public Boolean getVcStop() {
		return viewStop;
	}
	
	//second part view change
	public void setVcStopD(Boolean msgType) {
		viewStopD = msgType;
	}
	
	public Boolean getVcStopD() {
		return viewStopD;
	}
	
	//third part view change
	public void setVcSync(Boolean msgType) {
		viewSync = msgType;
	}
	
	public Boolean getVcSync() {
		return viewSync;
	}
	
	
//----------------------------------------------------------------------------------//		
	
	public void checkForExcelFile() throws IOException {
		String propPath2 = propPath + "Replica" + replicaID + ".xlsx";
		File f = new File(propPath2);
		if(f.exists() && !f.isDirectory()) {
			
		} else {
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("Replica");
			
			//header
			XSSFRow headRow = sheet.createRow(0);
			headRow.createCell(0).setCellValue("Seconds");
			headRow.createCell(1).setCellValue("Throughput");
			
			try {
				FileOutputStream out = new FileOutputStream(new File(propPath2));
				wb.write(out);
				out.close();
				System.out.println("excel creation sucess");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}

//----------------------------------------------------------------------------------//		

//----------------------------------------------------------------------------------//
	public void fillExcelFile2(Integer msgSec, Integer TPC) throws FileNotFoundException, IOException{
		String propPath2 = propPath + "Replica" + replicaID + ".xlsx";
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(propPath2));
		XSSFSheet sh = wb.getSheet("Replica");
		XSSFRow row;
		Integer rowN = sh.getPhysicalNumberOfRows();
		
		if(fileNewTest) {
			rowN = rowN +1;
			row = sh.createRow(rowN);
			row.createCell(0).setCellValue("NewTest");
			fileNewTest = false;
		}
		rowN = rowN +1;
		row = sh.createRow(rowN);
		row.createCell(0).setCellValue(msgSec);
		row.createCell(1).setCellValue(TPC);
		
		wb.write(new FileOutputStream(propPath2));
		wb.close();
	}
}
