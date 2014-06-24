package loadTables;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

import models.Official;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Ignore;
import org.junit.Test;

public class OfficialsCsvFile {
	@Ignore
	@Test
	public void createOfficial() {
	    running(fakeApplication(), new Runnable() {
	        public void run() {
	        	//http://www.basketball-reference.com/referees/
	        	Path path =  Paths.get(System.getProperty("config.load")).resolve("Officials.csv");
				File file = path.toFile();
				 
				BufferedReader bufRdr = null;
				try {
					bufRdr = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				String line = null;
				Official official;
				LocalDate date;
				int i = 0;
				 
				//read each line of text file
				try {
					while((line = bufRdr.readLine()) != null) {
						StringTokenizer st = new StringTokenizer(line,",");
						official = new Official();
						official.setNumber(st.nextToken());
						official.setFirstName(st.nextToken());
						official.setLastName(st.nextToken());
						
			            try {
			            	date = LocalDate.parse(st.nextToken(), DateTimeFormat.forPattern("MM/dd/yyyy"));
			            } catch (Exception e) {
			            	e.printStackTrace();
			            	break;
			            }

						official.setFirstGame(date);
						official.setActive((Boolean.valueOf(st.nextToken()).booleanValue()));
						
			            Official.create(official);
			            System.out.println("i = " + i++ + " " + official.getFirstName() + " " + official.getLastName());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				//close the file
				try {
					bufRdr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    });
	}
}