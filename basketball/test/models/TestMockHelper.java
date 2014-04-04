package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.BoxScore.Location;
import models.BoxScore.Result;
import models.Game.SeasonType;
import models.Game.Status;
import models.RosterPlayer.Position;
import models.Team.Conference;
import models.Team.Division;

public class TestMockHelper {

	protected static Game getGameCompleted() {
	    Game game = new Game();
	    try {
			game.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2013-07-05"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    game.setStatus(Status.completed);
	    game.setSeasonType(SeasonType.pre);
	    return game;
    }
    
	protected static Game getGameScheduled() {
	    Game game = new Game();
	    try {
			game.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2013-07-04"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    game.setStatus(Status.scheduled);
	    game.setSeasonType(SeasonType.pre);
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
	  	
	  	official = Official.findByName("Davis", "Marc");
	  	gameOfficial = new GameOfficial();
	  	gameOfficial.setOfficial(official);
	  	gameOfficials.add(gameOfficial);
    	
	    official = Official.findByName("Palmer", "Violet");
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
	
	protected static Player getPlayer() {
	  Player player = new Player();
	  player.setLastName("Webber");
	  player.setFirstName("Chris");
	  player.setDisplayName("Chris Webber");
	  player.setActive(false);
	  player.setHeight((short)82);
	  player.setWeight((short)245);
	  Date date = null;
	  try {
	  	  date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("1973-03-01");
	  } catch (ParseException e) {
	  	  e.printStackTrace();
	  }
	  player.setBirthDate(date);
	  player.setBirthPlace("Detroit, Michigan, USA");
	  return player;
	}
	
	protected static RosterPlayer getRosterPlayer() {
		  RosterPlayer rosterPlayer = new RosterPlayer();
		  rosterPlayer.setNumber("4");
		  rosterPlayer.setPosition(Position.powerForward);
		  Date fromDate = null;
		  Date toDate = null;
		  try {
		  	  fromDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2014-03-01");
		  	  toDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2014-03-10");
		  } catch (ParseException e) {
		  	  e.printStackTrace();
		  }
		  rosterPlayer.setFromDate(fromDate);
		  rosterPlayer.setToDate(toDate);
		  return rosterPlayer;
		}
	
	protected static Team getTeam() {
        Team team = new Team();
        team.setKey("seattle-supersonics");
        team.setFullName("Seattle Supersonics");
        team.setShortName("Supersonics");
        team.setAbbr("SEA");
        team.setConference(Conference.West);
        team.setDivision(Division.Pacific);
        team.setSiteName("Key Arena");
        team.setCity("Seattle");
        team.setState("WA");
        team.setActive(false);
        return team;
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
    
	protected static BoxScore getBoxScoreHomeCompleted(BoxScore boxScore) {
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
    
	protected static BoxScore getBoxScoreAwayCompleted(BoxScore boxScore) {
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
