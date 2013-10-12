package loadTables;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import models.entity.*;
import models.entity.Team.Conference;
import models.entity.Team.Division;

import org.junit.Test;

import util.FileIO;

public class TeamsCsvFile {
	@Test
	public void createTeam() {
	    running(fakeApplication(), new Runnable() {
	        public void run() {
			   	String path = FileIO.getPropertyPath("config.basketball");
				File file = new File(path + "//load//Teams.csv");
				 
				BufferedReader bufRdr = null;
				try {
					bufRdr = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				String line;
				Team team;
				int i = 0;
				 
				//read each line of text file
				try {
					while((line = bufRdr.readLine()) != null) {
						StringTokenizer st = new StringTokenizer(line,",");
						team = new Team();
						team.setKey(st.nextToken());
			            team.setFullName(st.nextToken());
			            team.setShortName(st.nextToken());
			            team.setAbbr(st.nextToken());
			            team.setActive((Boolean.valueOf(st.nextToken()).booleanValue()));
			            team.setConference(Conference.valueOf(st.nextToken()));
			            team.setDivision(Division.valueOf(st.nextToken()));
			            team.setSiteName(st.nextToken());
			            team.setCity(st.nextToken());
			            team.setState(st.nextToken());
			            
						Team.create(team);
						System.out.println("i = " + i++ + " " + team.getFullName());
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