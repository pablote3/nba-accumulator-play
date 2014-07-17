package loadTables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

import models.Game.ProcessingType;
import models.Team;
import models.Team.Conference;
import models.Team.Division;

import org.junit.Ignore;
import org.junit.Test;

public class TeamsCsvFile {
	@Ignore
	@Test
	public void createTeam() {
       	Path path =  Paths.get(System.getProperty("config.load")).resolve("Teams.csv");
		File file = path.toFile();
				 
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
			            
				Team.create(team, ProcessingType.batch);
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
}