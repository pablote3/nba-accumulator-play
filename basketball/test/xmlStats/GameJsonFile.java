package xmlStats;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import json.xmlStats.JsonHelper;
import json.xmlStats.NBABoxScore;
import models.BoxScore;
import models.BoxScorePlayer;
import models.Game;
import models.Player;
import models.RosterPlayer;
import models.Team;
import models.BoxScore.Location;
import models.BoxScore.Result;
import models.Game.ProcessingType;
import models.Game.SeasonType;
import models.Game.Status;

import org.junit.Ignore;
import org.junit.Test;

import util.DateTime;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameJsonFile {

    @Test
    public void createGame() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  InputStream baseJson; 
        	  try {
        		  Path path =  Paths.get(System.getProperty("config.test")).resolve("GameJson.txt");
        		  File file = path.toFile();
	              baseJson = new FileInputStream(file);
		        
	              ObjectMapper mapper = new ObjectMapper();
	              mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	            
	              NBABoxScore xmlStats = mapper.readValue(baseJson, NBABoxScore.class);
              
	              Game game = new Game();
	              game.setDate(xmlStats.event_information.getDate());
	              game.setStatus(Status.completed);
	              game.setSeasonType(xmlStats.event_information.getSeasonType());	              
	              game.setGameOfficials(JsonHelper.getGameOfficials(xmlStats.officials, ProcessingType.online));
	              
	              BoxScore homeBoxScore = new BoxScore();
	              homeBoxScore.setLocation(Location.home);
	              homeBoxScore.setTeam(Team.findByKey("key", xmlStats.home_team.getKey()));
	              homeBoxScore.setPeriodScores(JsonHelper.getPeriodScores(xmlStats.home_period_scores));
	              JsonHelper.getBoxScoreStats(homeBoxScore, xmlStats.home_totals);
	              homeBoxScore.setBoxScorePlayers(JsonHelper.getBoxScorePlayers(xmlStats.home_stats, DateTime.getFindDateShort(game.getDate()), ProcessingType.online));
	              
	              BoxScore awayBoxScore = new BoxScore();
	              awayBoxScore.setLocation(Location.away);
	              awayBoxScore.setTeam(Team.findByKey("key", xmlStats.away_team.getKey()));
	              awayBoxScore.setPeriodScores(JsonHelper.getPeriodScores(xmlStats.away_period_scores));
	              JsonHelper.getBoxScoreStats(awayBoxScore, xmlStats.away_totals);
	              awayBoxScore.setBoxScorePlayers(JsonHelper.getBoxScorePlayers(xmlStats.away_stats, DateTime.getFindDateShort(game.getDate()), ProcessingType.online));

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
	              			  assertThat(boxScore.getBoxScorePlayers().get(0).getRosterPlayer().getPlayer().getLastName()).isEqualTo("Durant");
	              			  assertThat(boxScore.getBoxScorePlayers().get(0).getPoints()).isEqualTo((short)32);
	              		  }
	              		  else {
	              			  assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)40);
	              			  assertThat(boxScore.getPeriodScores().get(0).getScore()).isEqualTo((short)31);
	              			  assertThat(boxScore.getTeam().getAbbr()).isEqualTo("MIA");
	              			  assertThat(boxScore.getBoxScorePlayers().get(1).getRosterPlayer().getPlayer().getLastName()).isEqualTo("Wade");
	              			  assertThat(boxScore.getBoxScorePlayers().get(1).getPoints()).isEqualTo((short)20);
	              		  }
	              	  }
        	  	  }

	              ArrayList<Long> playerIds = new ArrayList<Long>();
	              ArrayList<Long> rosterPlayerIds = new ArrayList<Long>();
	              
	              for (int i = 0; i < createGame.getBoxScores().size(); i++) {
	            	  BoxScore bs = createGame.getBoxScores().get(i);
	            	  for (int j = 0; j < bs.getBoxScorePlayers().size(); j++) {
	            		  BoxScorePlayer bsp = bs.getBoxScorePlayers().get(j);
	            		  playerIds.add(bsp.getRosterPlayer().getPlayer().getId());
	            		  rosterPlayerIds.add(bsp.getRosterPlayer().getId());
	            	  }
	              }
	              
	              Game.delete(createGame.getId(), ProcessingType.online);
	              
	              for (int i = 0; i < rosterPlayerIds.size(); i++) {
	            	  RosterPlayer.delete(rosterPlayerIds.get(i));
	              }
	              
	              for (int i = 0; i < playerIds.size(); i++) {
	            	  Player.delete(playerIds.get(i));
	              }

        	  } catch (FileNotFoundException e) {
      	          e.printStackTrace();
      	      } catch (IOException e) {
      	          e.printStackTrace();
      	      }
          }
        });
    }
    
    @Ignore
    @Test
    public void updateGame() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  InputStream baseJson; 
        	  try {
        		  Path path =  Paths.get(System.getProperty("config.test")).resolve("GameJson.txt");
        		  File file = path.toFile();
	              baseJson = new FileInputStream(file);
		        
	              ObjectMapper mapper = new ObjectMapper();
	              mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	            
	              NBABoxScore xmlStats = mapper.readValue(baseJson, NBABoxScore.class);
              
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
	              completeGame.setGameOfficials(JsonHelper.getGameOfficials(xmlStats.officials, ProcessingType.online));
	              
	              awayBoxScore = completeGame.getBoxScores().get(0);
	              awayBoxScore.setPeriodScores(JsonHelper.getPeriodScores(xmlStats.away_period_scores));
	              JsonHelper.getBoxScoreStats(awayBoxScore, xmlStats.away_totals);   
	              
	              homeBoxScore = completeGame.getBoxScores().get(1);
	              homeBoxScore.setPeriodScores(JsonHelper.getPeriodScores(xmlStats.home_period_scores));
	              JsonHelper.getBoxScoreStats(homeBoxScore, xmlStats.home_totals);     

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