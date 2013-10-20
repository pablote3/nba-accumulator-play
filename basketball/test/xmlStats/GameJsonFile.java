package xmlStats;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import models.entity.BoxScore;
import models.entity.BoxScore.Location;
import models.entity.BoxScore.Result;
import models.entity.Game;
import models.entity.Game.SeasonType;
import models.entity.Game.Status;
import models.entity.Team;
import models.partial.XmlStats;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import util.FileIO;

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
	              mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	            
	              XmlStats xmlStats = mapper.readValue(baseJson, XmlStats.class);
              
	              Game game = new Game();
	              game.setDate(xmlStats.event_information.getDate());
	              game.setStatus(Status.completed);
	              game.setSeasonType(xmlStats.event_information.getSeasonType());	              
	              game.setGameOfficials(GameJsonHelper.getGameOfficials(xmlStats.officials));
	              
	              BoxScore homeBoxScore = new BoxScore();
	              homeBoxScore.setLocation(Location.home);
	              homeBoxScore.setTeam(Team.find.where().eq("key", xmlStats.home_team.getKey()).findUnique());
	              homeBoxScore.setPeriodScores(GameJsonHelper.getPeriodScores(xmlStats.home_period_scores));
	              GameJsonHelper.getBoxScoreStats(homeBoxScore, xmlStats.home_totals);
	              
	              BoxScore awayBoxScore = new BoxScore();
	              awayBoxScore.setLocation(Location.away);
	              awayBoxScore.setTeam(Team.find.where().eq("key", xmlStats.away_team.getKey()).findUnique());
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
			    
	              Game createGame = Game.findByDateTeamKey("2012-06-21", "miami-heat");
	              assertThat(createGame.getSeasonType()).isEqualTo(SeasonType.post);
	              assertThat(createGame.getGameOfficials().get(1).getOfficial().getLastName()).endsWith("Crawford");
	              assertThat(createGame.getBoxScores().get(0).getLocation()).isEqualTo(Location.home);
	              assertThat(createGame.getBoxScores().get(0).getFieldGoalMade()).isEqualTo((short)40);
	              assertThat(createGame.getBoxScores().get(0).getPeriodScores().get(0).getScore()).isEqualTo((short)31);
	              assertThat(createGame.getBoxScores().get(0).getTeam().getAbbr()).isEqualTo("MIA");
	              Game.delete(createGame.getId());
      	      } catch (FileNotFoundException e) {
      	          e.printStackTrace();
      	      } catch (IOException e) {
      	          e.printStackTrace();
      	      }
          }
        });
    }
}