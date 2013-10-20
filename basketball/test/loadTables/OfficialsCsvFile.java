package loadTables;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import models.entity.Official;

import org.junit.Ignore;
import org.junit.Test;

import util.FileIO;

public class OfficialsCsvFile {
	@Ignore
	@Test
	public void createOfficial() {
	    running(fakeApplication(), new Runnable() {
	        public void run() {
			   	String path = FileIO.getPropertyPath("config.basketball");
				File file = new File(path + "//load//Officials.csv");
				 
				BufferedReader bufRdr = null;
				try {
					bufRdr = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				String line = null;
				Official official;
				Date date;
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
			            	date = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(st.nextToken());
			            } catch (ParseException e) {
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