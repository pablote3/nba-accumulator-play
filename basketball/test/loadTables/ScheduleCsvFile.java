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

import models.BoxScore;
import models.BoxScore.Location;
import models.Game;
import models.Game.SeasonType;
import models.Game.Status;
import models.Team;

import org.junit.Test;

import utils.Utilities;

public class ScheduleCsvFile {
	@Test
	public void createSchedule() {
	    running(fakeApplication(), new Runnable() {
	        public void run() {
			   	String path = Utilities.getPropertyPath("config.basketball");
				File file = new File(path + "//load//Schedule12-13.csv");
				 
				BufferedReader bufRdr = null;
				try {
					bufRdr = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				String line = null;
				Game game = null;
				BoxScore boxScoreHome = null;
				BoxScore boxScoreAway = null;
				Date date = null;
				 
				//read each line of text file
				try {
					while((line = bufRdr.readLine()) != null) {
						StringTokenizer st = new StringTokenizer(line,",");
						game = new Game();
						
			            try {
			            	date = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH).parse(st.nextToken());
			            } catch (ParseException e) {
			            	e.printStackTrace();
			            }
			            game.setDate(date);
			            game.setStatus(Status.completed);
			            game.setSeasonType(SeasonType.regular);
			            
						boxScoreAway = new BoxScore();			            
			            boxScoreAway.setLocation(Location.away);
			            boxScoreAway.setTeam(Team.find.where().eq("fullName", st.nextToken()).findUnique());  
			            game.addBoxScore(boxScoreAway);
			            
						boxScoreHome = new BoxScore();
			            boxScoreHome.setLocation(Location.home);
			            boxScoreHome.setTeam(Team.find.where().eq("fullName", st.nextToken()).findUnique());
			            game.addBoxScore(boxScoreHome);			
		            
						Game.create(game);
						game = null;
						boxScoreHome = null;
						boxScoreAway = null;
						date = null;
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
