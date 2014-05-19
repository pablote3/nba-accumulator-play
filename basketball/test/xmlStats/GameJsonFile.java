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
import models.BoxScore.Location;
import models.BoxScore.Result;
import models.BoxScorePlayer;
import models.Game;
import models.Game.ProcessingType;
import models.Game.SeasonType;
import models.Game.Status;
import models.Player;
import models.RosterPlayer;
import models.Team;

import org.junit.Ignore;
import org.junit.Test;

import util.DateTime;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameJsonFile {
	@Ignore
    @Test
    public void createGame() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  InputStream baseJson; 
        	  try {
        		  Path path =  Paths.get(System.getProperty("config.nbaBoxScore")).resolve("20120621-oklahoma-city-thunder-at-miami-heat.json");
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

	              ArrayList<Player> players = new ArrayList<Player>();
	              ArrayList<RosterPlayer> rosterPlayers = new ArrayList<RosterPlayer>();
	              
	              for (int i = 0; i < createGame.getBoxScores().size(); i++) {
	            	  BoxScore bs = createGame.getBoxScores().get(i);
	            	  for (int j = 0; j < bs.getBoxScorePlayers().size(); j++) {
	            		  BoxScorePlayer bsp = bs.getBoxScorePlayers().get(j);
	            		  players.add(bsp.getRosterPlayer().getPlayer());
	            		  rosterPlayers.add(bsp.getRosterPlayer());
	            	  }
	              }
	              
	              Game.delete(createGame.getId(), ProcessingType.online);
	              
	              for (int i = 0; i < rosterPlayers.size(); i++) {
	            	  RosterPlayer.delete(rosterPlayers.get(i), ProcessingType.online);
	              }
	              
	              for (int i = 0; i < players.size(); i++) {
	            	  Player.delete(players.get(i), ProcessingType.online);
	              }

        	  } catch (FileNotFoundException e) {
      	          e.printStackTrace();
      	      } catch (IOException e) {
      	          e.printStackTrace();
      	      }
          }
        });
    }
}