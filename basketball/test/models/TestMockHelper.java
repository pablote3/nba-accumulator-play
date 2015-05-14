package models;

import java.util.ArrayList;
import java.util.List;

import models.BoxScore.Location;
import models.BoxScore.Result;
import models.Game.ProcessingType;
import models.Game.SeasonType;
import models.Game.Status;
import models.RosterPlayer.Position;
import models.Team.Conference;
import models.Team.Division;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class TestMockHelper {

	protected static Game getGameCompleted() {
	    Game game = new Game();
		game.setDate(new DateTime(2013, 7, 5, 0, 0, 0));
	    game.setStatus(Status.completed);
	    game.setSeasonType(SeasonType.pre);
	    return game;
    }
    
	protected static Game getGameScheduled() {
	    Game game = new Game();
		game.setDate(new DateTime(2013, 7, 4, 0, 0, 0));
	    game.setStatus(Status.scheduled);
	    game.setSeasonType(SeasonType.pre);
	    return game;
    }
    
	protected static List<GameOfficial> getGameOfficials() {
    	List<GameOfficial> gameOfficials = new ArrayList<GameOfficial>();    
	    GameOfficial gameOfficial;
	    Official official;
	    
	    official = Official.findByName("Brown", "Tony", ProcessingType.online);
	  	gameOfficial = new GameOfficial();
	  	gameOfficial.setOfficial(official);
	  	gameOfficial.setCounter((short)1);
	  	gameOfficials.add(gameOfficial);
	  	
	  	official = Official.findByName("Davis", "Marc", ProcessingType.online);
	  	gameOfficial = new GameOfficial();
	  	gameOfficial.setOfficial(official);
	  	gameOfficial.setCounter((short)2);
	  	gameOfficials.add(gameOfficial);
    	
	    official = Official.findByName("Palmer", "Violet", ProcessingType.online);
	  	gameOfficial = new GameOfficial();
	  	gameOfficial.setOfficial(official);
	  	gameOfficial.setCounter((short)3);
	  	gameOfficials.add(gameOfficial);
    	return gameOfficials;
    }
	
	protected static Official getOfficial() {
  	  Official official = new Official();
  	  official.setNumber("99");
  	  official.setLastName("Hansen");
  	  official.setFirstName("Chris");
      LocalDate date = new LocalDate(2012, 11, 5);
	  official.setFirstGame(date);
	  official.setActive(false);
  	  return official;
	}
	
	protected static Player getPlayer(String birthDate) {
	  Player player = new Player();
	  player.setLastName("Jones");
	  player.setFirstName("Tim");
	  player.setDisplayName("Tim Jones");
	  player.setHeight((short)79);
	  player.setWeight((short)215);
	  LocalDate date = null;
	  try {
		  date = LocalDate.parse(birthDate, DateTimeFormat.forPattern("yyyy-MM-dd"));
	  } catch (Exception e) {
	  	  e.printStackTrace();
	  }
	  
	  player.setBirthDate(date);
	  player.setBirthPlace("Brooklyn, New York, USA");
	  return player;
	}
	
	protected static RosterPlayer getRosterPlayer(Player player, Team team, String startDate, String endDate) {
	  RosterPlayer rosterPlayer = new RosterPlayer();
	  rosterPlayer.setPlayer(player);
	  rosterPlayer.setTeam(team);
	  rosterPlayer.setNumber("10");
	  rosterPlayer.setPosition(Position.PG);
	  LocalDate fromDate = null;
	  LocalDate toDate = null;
	  try {
	  	  fromDate = LocalDate.parse(startDate, DateTimeFormat.forPattern("yyyy-MM-dd"));
	  	  toDate = LocalDate.parse(endDate, DateTimeFormat.forPattern("yyyy-MM-dd"));
	  } catch (Exception e) {
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
    
	protected static BoxScore getBoxScoreHomeCompleted(BoxScore boxScore, short[] periodScores) {
    	boxScore.setResult(Result.win);
    	boxScore.setMinutes((short)230);
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
	    boxScore.setPointsPeriod1(periodScores[0]);
	    boxScore.setPointsPeriod2(periodScores[1]);
	    boxScore.setPointsPeriod3(periodScores[2]);
	    boxScore.setPointsPeriod4(periodScores[3]);
	    if (periodScores.length > 4)
	    	boxScore.setPointsPeriod5(periodScores[4]);
    	return boxScore;
    }
    
	protected static BoxScore getBoxScoreAwayCompleted(BoxScore boxScore, short[] periodScores) {
    	boxScore.setResult(Result.loss);
    	boxScore.setMinutes((short)230);
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
	    boxScore.setPointsPeriod1(periodScores[0]);
	    boxScore.setPointsPeriod2(periodScores[1]);
	    boxScore.setPointsPeriod3(periodScores[2]);
	    boxScore.setPointsPeriod4(periodScores[3]);
	    if (periodScores.length > 4)
	    	boxScore.setPointsPeriod5(periodScores[4]);
    	return boxScore;
    }
	
	protected static Standing getStanding(String date, Team team) {
		Standing standing = new Standing();
		standing.setTeam(team);
		standing.setDate(LocalDate.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd")));
    	standing.setRank((short)2);
    	standing.setOrdinalRank("2nd");
	    standing.setGamesWon((short)95);
	    standing.setGamesLost((short)102);
	    standing.setStreak("W1");
	    standing.setStreakType("Win");
	    standing.setStreakTotal((short)1);
	    standing.setGamesBack((float)1.5);
	    standing.setPointsFor((short)2902);
	    standing.setPointsAgainst((short)3505);
	    standing.setHomeWins((short)40);
	    standing.setHomeLosses((short)60);
	    standing.setAwayWins((short)49);
	    standing.setAwayLosses((short)50);
	    standing.setConferenceWins((short)15);
	    standing.setConferenceLosses((short)11);
	    standing.setLastFive("2-3");
	    standing.setLastTen("5-10");
	    standing.setGamesPlayed((short)197);
	    standing.setPointsScoredPerGame((float)102.1);
	    standing.setPointsAllowedPerGame((float)105.1);
	    standing.setWinPercentage((float)0.505);
	    standing.setPointDifferential((short)-2);
	    standing.setPointDifferentialPerGame((float)-0.4);
    	return standing;
    }
    
	protected static short[] getPeriodScoresHome() {
		short[] periodScores = new short[5];		
		periodScores[0] = (short)25;
		periodScores[1] = (short)25;
		periodScores[2] = (short)25;
		periodScores[3] = (short)25;
		periodScores[4] = (short)20;
	    return periodScores;
    }
    
	protected static short[] getPeriodScoresAway() {
		short[] periodScores = new short[5];
		periodScores[0] = (short)25;
		periodScores[1] = (short)25;
		periodScores[2] = (short)25;
		periodScores[3] = (short)25;
		periodScores[4] = (short)21;
	    return periodScores;
    }
}
