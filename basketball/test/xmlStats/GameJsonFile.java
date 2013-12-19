package xmlStats;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import json.GameJsonHelper;
import json.XmlStat;

import models.BoxScore;
import models.Game;
import models.Team;
import models.BoxScore.Location;
import models.BoxScore.Result;
import models.Game.ProcessingType;
import models.Game.SeasonType;
import models.Game.Status;

import org.junit.Test;

import util.FileIO;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameJsonFile {

    @Test
    public void createGame() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  InputStream baseJson; 
        	  try {
	              String path = FileIO.getPropertyPath("config.basketball");
	              baseJson = new FileInputStream(path + "//test//GameJson.txt");
		        
	              ObjectMapper mapper = new ObjectMapper();
	              mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	            
	              XmlStat xmlStats = mapper.readValue(baseJson, XmlStat.class);
              
	              Game game = new Game();
	              game.setDate(xmlStats.event_information.getDate());
	              game.setStatus(Status.completed);
	              game.setSeasonType(xmlStats.event_information.getSeasonType());	              
	              game.setGameOfficials(GameJsonHelper.getGameOfficials(xmlStats.officials, ProcessingType.online));
	              
	              BoxScore homeBoxScore = new BoxScore();
	              homeBoxScore.setLocation(Location.home);
	              homeBoxScore.setTeam(Team.findByKey("key", xmlStats.home_team.getKey()));
	              homeBoxScore.setPeriodScores(GameJsonHelper.getPeriodScores(xmlStats.home_period_scores));
	              GameJsonHelper.getBoxScoreStats(homeBoxScore, xmlStats.home_totals);
	              
	              BoxScore awayBoxScore = new BoxScore();
	              awayBoxScore.setLocation(Location.away);
	              awayBoxScore.setTeam(Team.findByKey("key", xmlStats.away_team.getKey()));
	              awayBoxScore.setPeriodScores(GameJsonHelper.getPeriodScores(xmlStats.away_period_scores));
	              GameJsonHelper.getBoxScoreStats(awayBoxScore, xmlStats.away_totals);

	              if (xmlStats.away_totals.getPoints() > xmlStats.home_totals.getPoints()) {
	            	  homeBoxScore.setResult(Result.loss);
	            	  awayBoxScore.setResult(Result.win);
	              }
	              else {
	            	  homeBoxScore.setResult(Result.win);
	            	  awayBoxScore.setResult(Result.loss);
	              }	  
	              
	              game.addBoxScore(homeBoxScore);
	              game.addBoxScore(awayBoxScore);
	              
	              Game.create(game);
	              Long gameId = game.getId();

	              Game createGame = Game.findById(gameId, ProcessingType.online);
			    
	              assertThat(createGame.getSeasonType()).isEqualTo(SeasonType.post);
	              if (createGame.getGameOfficials().size() > 0) {
	            	  assertThat(createGame.getGameOfficials().get(1).getOfficial().getLastName()).endsWith("Crawford");
	              	  for (int i = 0; i < createGame.getBoxScores().size(); i++) {
	              		  BoxScore boxScore = createGame.getBoxScores().get(i);
	              		  if (boxScore.getLocation().equals(Location.away)) {
	              			  assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)36);
	              			  assertThat(boxScore.getPeriodScores().get(0).getScore()).isEqualTo((short)26);
	              			  assertThat(boxScore.getTeam().getAbbr()).isEqualTo("OKC");	              			  
	              		  }
	              		  else {
	              			  assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)40);
	              			  assertThat(boxScore.getPeriodScores().get(0).getScore()).isEqualTo((short)31);
	              			  assertThat(boxScore.getTeam().getAbbr()).isEqualTo("MIA");
	              		  }
	              	  }
        	  	  }
	              Game.delete(createGame.getId(), ProcessingType.online);
      	      } catch (FileNotFoundException e) {
      	          e.printStackTrace();
      	      } catch (IOException e) {
      	          e.printStackTrace();
      	      }
          }
        });
    }
    
    @Test
    public void updateGame() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  InputStream baseJson; 
        	  try {
	              String path = FileIO.getPropertyPath("config.basketball");
	              baseJson = new FileInputStream(path + "//test//GameJson.txt");
		        
	              ObjectMapper mapper = new ObjectMapper();
	              mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	            
	              XmlStat xmlStats = mapper.readValue(baseJson, XmlStat.class);
              
	              Game scheduleGame = new Game();
	              scheduleGame.setDate(xmlStats.event_information.getDate());
	              scheduleGame.setStatus(Status.scheduled);
	              scheduleGame.setSeasonType(xmlStats.event_information.getSeasonType());
	              
	              BoxScore awayBoxScore = new BoxScore();
	              awayBoxScore.setLocation(Location.away);
	              awayBoxScore.setTeam(Team.findByKey("key", xmlStats.away_team.getKey()));
	              scheduleGame.addBoxScore(awayBoxScore);
	              
	              BoxScore homeBoxScore = new BoxScore();
	              homeBoxScore.setLocation(Location.home);
	              homeBoxScore.setTeam(Team.findByKey("key", xmlStats.home_team.getKey()));
	              scheduleGame.addBoxScore(homeBoxScore);
	                
	              Game.create(scheduleGame);
	              Long gameId = scheduleGame.getId();
	              
	    		  Game completeGame = Game.findById(gameId, ProcessingType.online);
	      		  completeGame.setStatus(Status.completed);	              
	              completeGame.setGameOfficials(GameJsonHelper.getGameOfficials(xmlStats.officials, ProcessingType.online));
	              
	              awayBoxScore = completeGame.getBoxScores().get(0);
	              awayBoxScore.setPeriodScores(GameJsonHelper.getPeriodScores(xmlStats.away_period_scores));
	              GameJsonHelper.getBoxScoreStats(awayBoxScore, xmlStats.away_totals);   
	              
	              homeBoxScore = completeGame.getBoxScores().get(1);
	              homeBoxScore.setPeriodScores(GameJsonHelper.getPeriodScores(xmlStats.home_period_scores));
	              GameJsonHelper.getBoxScoreStats(homeBoxScore, xmlStats.home_totals);     

	              if (awayBoxScore.getPoints() > homeBoxScore.getPoints()) {
	            	  homeBoxScore.setResult(Result.loss);
	            	  awayBoxScore.setResult(Result.win);
	              }
	              else {
	            	  homeBoxScore.setResult(Result.win);
	            	  awayBoxScore.setResult(Result.loss);
	              }	  
	              
	              completeGame.update();
	              
	              Game createGame = Game.findById(gameId, ProcessingType.online);

	              assertThat(createGame.getSeasonType()).isEqualTo(SeasonType.post);
	              assertThat(createGame.getGameOfficials().size()).isEqualTo(3);
	              if (createGame.getGameOfficials().size() > 0) {
	            	  assertThat(createGame.getGameOfficials().get(1).getOfficial().getLastName()).endsWith("Crawford");
	            	  assertThat(createGame.getBoxScores().size()).isEqualTo(2);
	              	  for (int i = 0; i < createGame.getBoxScores().size(); i++) {
	              		  BoxScore boxScore = createGame.getBoxScores().get(i);
	              		  if (boxScore.getLocation().equals(Location.away)) {
	              			  assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)36);
	              			  assertThat(boxScore.getPeriodScores().get(0).getScore()).isEqualTo((short)26);
	              			  assertThat(boxScore.getTeam().getAbbr()).isEqualTo("OKC");	              			  
	              		  }
	              		  else {
	              			  assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)40);
	              			  assertThat(boxScore.getPeriodScores().get(0).getScore()).isEqualTo((short)31);
	              			  assertThat(boxScore.getTeam().getAbbr()).isEqualTo("MIA");
	              		  }
	              	  }
        	  	  }
	              Game.delete(createGame.getId(), ProcessingType.online);
      	      } catch (FileNotFoundException e) {
      	          e.printStackTrace();
      	      } catch (IOException e) {
      	          e.printStackTrace();
      	      }
          }
        });
    }
}