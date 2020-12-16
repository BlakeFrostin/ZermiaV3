package zermia.stats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import zermia.replica.ZermiaReplicaList;


public class ZermiaStats {

	ZermiaReplicaList replicaList = new ZermiaReplicaList();
	double total;
	double total2;
	double timeTotal;
	Integer messagesSent;
	
	Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
	String propPath = path + "/replicaStats/EndTestReplicaStats.xlsx";	
	
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
	
//----------------------------------------------------------------------------------//	

	public void checkForExcelFile() throws IOException {
		File f = new File(propPath);
		if(f.exists() && !f.isDirectory()) {
			
		} else {
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("FullStats");
			
			//header
			XSSFRow headRow = sheet.createRow(0);
			headRow.createCell(1).setCellValue("End Time");
			headRow.createCell(2).setCellValue("Total Messages");
			headRow.createCell(3).setCellValue("Average Trhoughput");
			headRow.createCell(4).setCellValue("Average Latency");
			
			try {
				FileOutputStream out = new FileOutputStream(new File(propPath));
				wb.write(out);
				out.close();
				System.out.println("excel creation sucess");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}
	
//----------------------------------------------------------------------------------//		
	public void fillExcelFile(Integer repId, String timeEnd, String totalMessages, String avgThroughput, String averageLatency ) throws FileNotFoundException, IOException {
		
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(propPath));
		XSSFSheet sh = wb.getSheet("FullStats");
		XSSFRow row;
		Integer rowN = sh.getPhysicalNumberOfRows();
		rowN = rowN +1;
		row = sh.createRow(rowN);
		row.createCell(0).setCellValue("Replica " + repId);
		row.createCell(1).setCellValue(timeEnd);
		row.createCell(2).setCellValue(totalMessages);
		row.createCell(3).setCellValue(avgThroughput);
		row.createCell(4).setCellValue(averageLatency);
			
		wb.write(new FileOutputStream(propPath));
		wb.close();
	}
	
//----------------------------------------------------------------------------------//		
	public void fillExcelTestSeparator() throws FileNotFoundException, IOException {
		
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(propPath));
		XSSFSheet sh = wb.getSheet("FullStats");
		XSSFRow row;
		Integer rowN = sh.getPhysicalNumberOfRows();
		rowN = rowN +1;
		row = sh.createRow(rowN);
		row.createCell(0).setCellValue("New Test");
		
		wb.write(new FileOutputStream(propPath));
		wb.close();
	}
	
}
