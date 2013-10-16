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
import models.entity.Game.Status;
import models.entity.GameOfficial;
import models.entity.Official;
import models.entity.PeriodScore;
import models.entity.Team;
import models.partial.XmlStats;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig;

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
	              
	              GameOfficial gameOfficial;
	              for (int i = 0; i < xmlStats.officials.length; i++) {
	            	  Official official = Official.findByName(xmlStats.officials[i].getLastName(), xmlStats.officials[i].getFirstName());
	            	  gameOfficial = new GameOfficial();
	            	  gameOfficial.setOfficial(official);
	            	  game.addGameOfficial(gameOfficial);
	              }
	              
	              BoxScore homeBoxScore = new BoxScore();
	              BoxScore awayBoxScore = new BoxScore();
	              
	              homeBoxScore.setTeam(Team.find.where().eq("key", xmlStats.home_team.getKey()).findUnique());
	              awayBoxScore.setTeam(Team.find.where().eq("key", xmlStats.away_team.getKey()).findUnique());
	              
	              PeriodScore periodScore;
	              for (int i = 0; i < xmlStats.home_period_scores.length; i++) {
	            	periodScore = new PeriodScore();
	            	periodScore.setQuarter((short)(i+1));
	            	periodScore.setScore((short)xmlStats.home_period_scores[i]);
					homeBoxScore.addPeriodScore(periodScore);
	              }
	              for (int i = 0; i < xmlStats.away_period_scores.length; i++) {
	            	periodScore = new PeriodScore();
	            	periodScore.setQuarter((short)(i+1));
	            	periodScore.setScore((short)xmlStats.away_period_scores[i]);
					awayBoxScore.addPeriodScore(periodScore);
	              }
	              
	              homeBoxScore.setLocation(Location.home);
	              awayBoxScore.setLocation(Location.away);
	              
	              if (xmlStats.away_totals.getPoints() > xmlStats.home_totals.getPoints()) {
	            	  homeBoxScore.setResult(Result.loss);
	            	  awayBoxScore.setResult(Result.win);
	              }
	              else {
	            	  homeBoxScore.setResult(Result.win);
	            	  awayBoxScore.setResult(Result.loss);
	              }	  
	              
	              homeBoxScore.setPoints(xmlStats.home_totals.getPoints());
	              homeBoxScore.setAssists(xmlStats.home_totals.getAssists());
	              homeBoxScore.setTurnovers(xmlStats.home_totals.getTurnovers());
	              homeBoxScore.setSteals(xmlStats.home_totals.getSteals());
	              homeBoxScore.setBlocks(xmlStats.home_totals.getBlocks());
	              homeBoxScore.setFieldGoalAttempts(xmlStats.home_totals.getFieldGoalAttempts());
	              homeBoxScore.setFieldGoalMade(xmlStats.home_totals.getFieldGoalMade());
	              homeBoxScore.setFieldGoalPercent(xmlStats.home_totals.getFieldGoalPercent());
	              homeBoxScore.setThreePointAttempts(xmlStats.home_totals.getThreePointAttempts());
	              homeBoxScore.setThreePointMade(xmlStats.home_totals.getThreePointMade());
	              homeBoxScore.setThreePointPercent(xmlStats.home_totals.getThreePointPercent());
	              homeBoxScore.setFreeThrowAttempts(xmlStats.home_totals.getFreeThrowAttempts());
	              homeBoxScore.setFreeThrowMade(xmlStats.home_totals.getFreeThrowMade());
	              homeBoxScore.setFreeThrowPercent(xmlStats.home_totals.getFreeThrowPercent());
	              homeBoxScore.setReboundsOffense(xmlStats.home_totals.getReboundsOffense());
	              homeBoxScore.setReboundsDefense(xmlStats.home_totals.getReboundsDefense());
	              homeBoxScore.setPersonalFouls(xmlStats.home_totals.getPersonalFouls());

	              awayBoxScore.setPoints(xmlStats.away_totals.getPoints());
	              awayBoxScore.setAssists(xmlStats.away_totals.getAssists());
	              awayBoxScore.setTurnovers(xmlStats.away_totals.getTurnovers());
	              awayBoxScore.setSteals(xmlStats.away_totals.getSteals());
	              awayBoxScore.setBlocks(xmlStats.away_totals.getBlocks());
	              awayBoxScore.setFieldGoalAttempts(xmlStats.away_totals.getFieldGoalAttempts());
	              awayBoxScore.setFieldGoalMade(xmlStats.away_totals.getFieldGoalMade());
	              awayBoxScore.setFieldGoalPercent(xmlStats.away_totals.getFieldGoalPercent());
	              awayBoxScore.setThreePointAttempts(xmlStats.away_totals.getThreePointAttempts());
	              awayBoxScore.setThreePointMade(xmlStats.away_totals.getThreePointMade());
	              awayBoxScore.setThreePointPercent(xmlStats.away_totals.getThreePointPercent());
	              awayBoxScore.setFreeThrowAttempts(xmlStats.away_totals.getFreeThrowAttempts());
	              awayBoxScore.setFreeThrowMade(xmlStats.away_totals.getFreeThrowMade());
	              awayBoxScore.setFreeThrowPercent(xmlStats.away_totals.getFreeThrowPercent());
	              awayBoxScore.setReboundsOffense(xmlStats.away_totals.getReboundsOffense());
	              awayBoxScore.setReboundsDefense(xmlStats.away_totals.getReboundsDefense());
	              awayBoxScore.setPersonalFouls(xmlStats.away_totals.getPersonalFouls());
	              
	              game.addBoxScore(homeBoxScore);
	              game.addBoxScore(awayBoxScore);
              
	              System.out.println(game.toString());
	              
//	              Game.create(game);
	              
      	      } catch (FileNotFoundException e) {
      	          e.printStackTrace();
      	      } catch (IOException e) {
      	          e.printStackTrace();
      	      }
              
//              Team createTeam = Team.find.where().eq("key", "seattle-supersonics").findUnique();
//              assertThat(createTeam.getFullName()).isEqualTo("Seattle Supersonics");
//              assertThat(createTeam.getAbbr()).isEqualTo("SEA");
//              Team.delete(createTeam.getId());
          }
        });
    }
}