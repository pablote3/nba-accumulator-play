package loadTables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

import models.BoxScore;
import models.BoxScore.Location;
import models.Game;
import models.Game.ProcessingType;
import models.Game.SeasonType;
import models.Game.Status;
import models.Team;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Ignore;
import org.junit.Test;

public class ScheduleCsvFile {
	@Ignore
	@Test
	public void createSchedule() {
	    //http://www.basketball-reference.com/leagues/NBA_2015_games.html
		//http://www.nba.com/schedules/national_tv_schedule/
	    Path path =  Paths.get(System.getProperty("config.load")).resolve("Schedule_2014-2015.csv");
		File file = path.toFile();

		BufferedReader bufRdr = null;
		try {
			bufRdr = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line;
		Game game;
		BoxScore boxScoreHome;
		BoxScore boxScoreAway;
		Team teamHome;
		Team teamAway;
		DateTime preseasonEnd = new DateTime(2014, 9, 28, 0, 0, 0);		//10/28/13
		StringBuffer sbDate;
		DateTime date;
		int i = 0;
				 
		//read each line of text file
		try {
			bufRdr.readLine();								//jump over header line
			while((line = bufRdr.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line,",");
				sbDate = new StringBuffer();
			            
				sbDate.append(st.nextToken());				//start date                  
	            sbDate.append(" " + st.nextToken());		//start time (ET)
	            String awayTeam = st.nextToken().trim();
	            String homeTeam = st.nextToken().trim();

	            try {
	            	date = DateTimeFormat.forPattern("MM/dd/yyyy kk:mm").parseDateTime(sbDate.toString());
	            } catch (Exception e) {
	            	e.printStackTrace();
	            	break;
	            }
			            
	            if(date.isAfter(preseasonEnd)) {
		            teamHome = Team.findByKey("shortName", homeTeam, ProcessingType.batch);
		            teamAway = Team.findByKey("shortName", awayTeam, ProcessingType.batch);				            
				            
					game = new Game();
		            game.setDate(date);
		            game.setStatus(Status.scheduled);
		            game.setSeasonType(SeasonType.regular);
				            
					boxScoreAway = new BoxScore();			            
		            boxScoreAway.setLocation(Location.away);
		            boxScoreAway.setTeam(teamAway);
		            game.addBoxScore(boxScoreAway);
				            
					boxScoreHome = new BoxScore();
		            boxScoreHome.setLocation(Location.home);
		            boxScoreHome.setTeam(teamHome);
		            game.addBoxScore(boxScoreHome);			
			            
					Game.create(game, ProcessingType.batch);
					System.out.println("i = " + i++ + " " + teamAway.getFullName() + " " + teamHome.getFullName());
	            }
	            else
					System.out.println("b = " + i++ +  " " + awayTeam + " " + homeTeam);
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
