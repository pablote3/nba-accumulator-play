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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;

import models.BoxScore;
import models.BoxScore.Location;
import models.Game;
import models.Game.SeasonType;
import models.Game.Status;
import models.Team;

import org.junit.Ignore;
import org.junit.Test;

public class ScheduleCsvFile {
	@Ignore
	@Test
	public void createSchedule() {
	    running(fakeApplication(), new Runnable() {
	        public void run() {
	        	//entire nba schedule from www.LiveFanChat.com
	        	//need to replace ,, with , , prior to execution for parsing to work correctly
	        	//found a few accuracy problems with games, needed to compare with www.basketball-reference.com/leagues/NBA_2013_games.html
	        	//like that it has game start times though
	        	Path path =  Paths.get(System.getProperty("config.load")).resolve("Schedule_2012-2013.csv");
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
				final Date preseasonEnd = new GregorianCalendar(2012, 9, 28).getTime();		//10/28/13
				StringBuffer sbDate;
				Date date;
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
			            	date = new SimpleDateFormat("MM/dd/yyyy kk:mm", Locale.ENGLISH).parse(sbDate.toString());
			            } catch (ParseException e) {
			            	e.printStackTrace();
			            	break;
			            }
			            
			            if(date.after(preseasonEnd)) {
				            teamHome = Team.findByKey("shortName", homeTeam);
				            teamAway = Team.findByKey("shortName", awayTeam);				            
				            
							game = new Game();
				            game.setDate(date);
				            game.setStatus(Status.finished);
				            game.setSeasonType(SeasonType.regular);
				            
							boxScoreAway = new BoxScore();			            
				            boxScoreAway.setLocation(Location.away);
				            boxScoreAway.setTeam(teamAway);
				            game.addBoxScore(boxScoreAway);
				            
							boxScoreHome = new BoxScore();
				            boxScoreHome.setLocation(Location.home);
				            boxScoreHome.setTeam(teamHome);
				            game.addBoxScore(boxScoreHome);			
			            
							Game.create(game);
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
	    });
	}
}
