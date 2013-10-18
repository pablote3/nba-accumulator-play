package model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.entity.BoxScore;
import models.entity.BoxScore.Location;
import models.entity.BoxScore.Result;
import models.entity.Game;
import models.entity.Game.SeasonType;
import models.entity.Game.Status;
import models.entity.GameOfficial;
import models.entity.Official;
import models.entity.PeriodScore;

public class MockTestHelper {

	protected static Game getGameCompleted() {
	    Game game = new Game();
	    try {
			game.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2012-11-05"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    game.setStatus(Status.completed);
	    game.setSeasonType(SeasonType.regular);
	    return game;
    }
    
	protected static Game getGameScheduled() {
	    Game game = new Game();
	    try {
			game.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2013-11-04"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    game.setStatus(Status.scheduled);
	    game.setSeasonType(SeasonType.regular);
	    return game;
    }
    
	protected static List<GameOfficial> getGameOfficials() {
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
	
	protected static Official getOfficial() {
  	  Official official = new Official();
  	  official.setNumber("99");
  	  official.setLastName("Hansen");
  	  official.setFirstName("Chris");
      Date date = null;
      try {
    	  date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2012-11-05");
      } catch (ParseException e) {
    	  e.printStackTrace();
      }
	  official.setFirstGame(date);
	  official.setActive(false);
  	  return official;
	}
    
	protected static BoxScore getBoxScoreHomeScheduled() {
    	BoxScore boxScore = new BoxScore();
    	boxScore.setLocation(Location.home);
    	return boxScore;
    }
    
	protected static BoxScore getBoxScoreAwayScheduled() {
    	BoxScore boxScore = new BoxScore();
    	boxScore.setLocation(Location.away);
    	return boxScore;
    }
    
	protected static BoxScore getBoxScoreHomeCompleted() {
    	BoxScore boxScore = new BoxScore();
    	boxScore.setLocation(Location.home);
    	boxScore.setResult(Result.win);
	    boxScore.setPoints((short)100);
	    boxScore.setAssists((short)25);
	    boxScore.setTurnovers((short)12);
	    boxScore.setSteals((short)5);
	    boxScore.setBlocks((short)7);
	    boxScore.setFieldGoalAttempts((short)39);
	    boxScore.setFieldGoalMade((short)30);
	    boxScore.setFieldGoalPercent(new Float(0.7692));
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
    
	protected static BoxScore getBoxScoreAwayCompleted() {
    	BoxScore boxScore = new BoxScore();
    	boxScore.setLocation(Location.away);
    	boxScore.setResult(Result.loss);
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
    
	protected static List<PeriodScore> getPeriodScoresHome() {
    	List<PeriodScore> periodScores = new ArrayList<PeriodScore>();	    
	    PeriodScore periodScore;
	    
	    periodScore = new PeriodScore();
	    periodScore.setQuarter((short)1);
	    periodScore.setScore((short)25);
	    periodScores.add(periodScore);

	    periodScore = new PeriodScore();
	    periodScore.setQuarter((short)2);
	    periodScore.setScore((short)25);
	    periodScores.add(periodScore);
	    
	    periodScore = new PeriodScore();
	    periodScore.setQuarter((short)3);
	    periodScore.setScore((short)25);
	    periodScores.add(periodScore);
	    
	    periodScore = new PeriodScore();
	    periodScore.setQuarter((short)4);
	    periodScore.setScore((short)25);
	    periodScores.add(periodScore);
	    
	    return periodScores;
    }
    
	protected static List<PeriodScore> getPeriodScoresAway() {
    	List<PeriodScore> periodScores = new ArrayList<PeriodScore>();	    
	    PeriodScore periodScore;
	    
	    periodScore = new PeriodScore();
	    periodScore.setQuarter((short)1);
	    periodScore.setScore((short)25);
	    periodScores.add(periodScore);

	    periodScore = new PeriodScore();
	    periodScore.setQuarter((short)2);
	    periodScore.setScore((short)25);
	    periodScores.add(periodScore);
	    
	    periodScore = new PeriodScore();
	    periodScore.setQuarter((short)3);
	    periodScore.setScore((short)25);
	    periodScores.add(periodScore);
	    
	    periodScore = new PeriodScore();
	    periodScore.setQuarter((short)4);
	    periodScore.setScore((short)24);
	    periodScores.add(periodScore);
	    
	    return periodScores;
    }
}
