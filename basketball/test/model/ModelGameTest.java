package model;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.entity.*;
import models.entity.BoxScore.Location;
import models.entity.BoxScore.Result;
import models.entity.Game.SeasonType;
import models.entity.Game.Status;

import org.junit.Test;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

public class ModelGameTest {

    @Test
    public void createGame() {
        running(fakeApplication(), new Runnable() {
          public void run() {  
        	Game game = getMockGame();
        	game.setGameOfficial(getMockOfficials());
		    
		    BoxScore homeBoxScore = getMockBoxScoreHome();
		    BoxScore awayBoxScore = getMockBoxScoreAway();
		    
		    homeBoxScore.setTeam(Team.find.where().eq("key", "new-orleans-pelicans").findUnique());
		    awayBoxScore.setTeam(Team.find.where().eq("key", "sacramento-kings").findUnique());
		    
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
		    
		//    Game.create(game);
		  }
		});
	}
    
    private Game getMockGame() {
	    Game game = new Game();
	    game.setDate(xmlStats.event_information.getDate());
	    game.setStatus(Status.completed);
	    game.setSeasonType(SeasonType.regular);
	    return game;
    }
    
    private List<GameOfficial> getMockOfficials() {
    	List<GameOfficial> gameOfficials = new ArrayList<GameOfficial>();
	    
	    GameOfficial gameOfficial;
	    Official official;
	    official = Official.findByName("Brown", "Tony");
	  	gameOfficial = new GameOfficial();
	  	gameOfficial.setOfficial(official);
	  	gameOfficials.add(gameOfficial);
	  	
	    official = Official.findByName("Fehr", "Kevin");
	  	gameOfficial = new GameOfficial();
	  	gameOfficial.setOfficial(official);
	  	gameOfficials.add(gameOfficial);
	  	
	    official = Official.findByName("Davis", "Marc");
	  	gameOfficial = new GameOfficial();
	  	gameOfficial.setOfficial(official);
	  	gameOfficials.add(gameOfficial);
    	return gameOfficials;
    }
    
    private BoxScore getMockBoxScoreHome() {
    	BoxScore boxScore = new BoxScore();
	    boxScore.setPoints((short)99);
	    boxScore.setAssists((short)25);
	    boxScore.setTurnovers((short)12);
	    boxScore.setSteals((short)5);
	    boxScore.setBlocks((short)7);
	    boxScore.setFieldGoalAttempts((short)39);
	    boxScore.setFieldGoalMade((short)29);
	    boxScore.setFieldGoalPercent(new Float(0.7435));
	    boxScore.setThreePointAttempts((short)17);
	    boxScore.setThreePointMade((short)10);
	    boxScore.setThreePointPercent(new Float(0.5882));
	    boxScore.setFreeThrowAttempts((short)15);
	    boxScore.setFreeThrowMade((short)11);
	    boxScore.setFreeThrowPercent(new Float(0.7333));
	    boxScore.setReboundsOffense((short)15);
	    boxScore.setReboundsDefense((short)12);
	    boxScore.setPersonalFouls((short)21);
    	return boxScore;
    }
    
    private BoxScore getMockBoxScoreAway() {
    	BoxScore boxScore = new BoxScore();
	    boxScore.setPoints((short)99);
	    boxScore.setAssists((short)25);
	    boxScore.setTurnovers((short)12);
	    boxScore.setSteals((short)5);
	    boxScore.setBlocks((short)7);
	    boxScore.setFieldGoalAttempts((short)39);
	    boxScore.setFieldGoalMade((short)29);
	    boxScore.setFieldGoalPercent(new Float(0.7435));
	    boxScore.setThreePointAttempts((short)17);
	    boxScore.setThreePointMade((short)10);
	    boxScore.setThreePointPercent(new Float(0.5882));
	    boxScore.setFreeThrowAttempts((short)15);
	    boxScore.setFreeThrowMade((short)11);
	    boxScore.setFreeThrowPercent(new Float(0.7333));
	    boxScore.setReboundsOffense((short)15);
	    boxScore.setReboundsDefense((short)12);
	    boxScore.setPersonalFouls((short)21);
    	return boxScore;
    }

    @Test
    public void aggregateScores() {
        running(fakeApplication(), new Runnable() {
          public void run() {                      	  
        	  Date startDate = null;
        	  Date endDate = null;
        	  
        	  try {
        		  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        		  startDate = simpleDateFormat.parse("2012-10-29");
        		  endDate = simpleDateFormat.parse("2013-04-18");
        	  } catch (ParseException e) {
        		  e.printStackTrace();
        	  }
        	  
        	  String sql 
        	  		= "select g.id, g.date, g.status " +
            		  "from Game g " +
            		  "inner join Box_Score bs1 on bs1.game_id = g.id " +
            		  "inner join Team t1 on t1.id = bs1.team_id " + 
            		  "inner join Box_Score bs2 on bs2.game_id = g.id and bs2.id <> bs1.id " +
            		  "inner join Team t2 on t2.id = bs2.team_id ";
        	  
        	  RawSql rawSql =
        			RawSqlBuilder
        			  .parse(sql)
        			  .columnMapping("g.id", "id")
        			  .columnMapping("g.date", "date")
        			  .columnMapping("g.status", "status")
        			  .create();
        	  
              Query<Game> query = Ebean.find(Game.class);
              query.setRawSql(rawSql);
            		  
              query.where().between("g.date", startDate, endDate);
              query.where().eq("t1.abbr", "SAC");

              List<Game> games = query.findList();
              assertThat(games.size() == 82);
          }
        });
    }

    @Test
    public void findGamesDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Game> games = Game.findByDate("2012-10-31");        
              assertThat(games.size()).isEqualTo(9);
          }
        });
    }
    
    @Test
    public void findGamesFinderDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {                      	  
        	  String gameDate = "2012-10-31";
        	  
        	  Query<Game> query = Ebean.find(Game.class);
        	  query.fetch("boxScores");
        	  query.fetch("boxScores.team");
              query.where().ilike("date", gameDate + "%");

              List<Game> games = query.findList();
              assertThat(games.size()).isEqualTo(9);
//              Game game = null;
//              java.util.Iterator<Game> iter = games.iterator();
//              while (iter.hasNext()) {
//            	  game = iter.next();
//            	  System.out.println(game.toString());
//              }
          }
        });
    }
}
