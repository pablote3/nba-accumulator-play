package models;

import java.util.ArrayList;
import java.util.List;

import models.BoxScore.Location;
import models.BoxScore.Result;
import models.Game.ProcessingType;
import models.Game.SeasonType;
import models.Game.Status;
import models.RosterPlayer.Position;
import models.Standing.StreakType;
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
    
	protected static BoxScore getBoxScoreHomeCompleted(BoxScore boxScore) {
    	boxScore.setResult(Result.win);
    	boxScore.setMinutes((short)180);
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
    	boxScore.setMinutes((short)190);
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
	
	protected static Standing getStandingHomeCompleted(Team team) {
		Standing standing = new Standing();
		standing.setTeamKey(team.getKey());
		standing.setGameDate(new DateTime(2013, 7, 5, 0, 0, 0));
    	standing.setRank((short)1);
    	standing.setOrdinalRank("1st");
	    standing.setGamesWon((short)99);
	    standing.setGamesLost((short)100);
	    standing.setStreak("L3");
	    standing.setStreakType(StreakType.loss);
	    standing.setStreakTotal((short)3);
	    standing.setGamesBack((float)0);
	    standing.setPointsFor((short)2998);
	    standing.setPointsAgainst((short)3501);
	    standing.setHomeWins((short)50);
	    standing.setHomeLosses((short)50);
	    standing.setAwayWins((short)49);
	    standing.setAwayLosses((short)50);
	    standing.setConferenceWins((short)15);
	    standing.setConferenceLosses((short)11);
	    standing.setLastFive("3-2");
	    standing.setLastTen("5-10");
	    standing.setGamesPlayed((short)199);
	    standing.setPointsScoredPerGame((float)102.1);
	    standing.setPointsAllowedPerGame((float)105.1);
	    standing.setWinPercentage((float)0.505);
	    standing.setPointDifferential((short)-2);
	    standing.setPointDifferentialPerGame((float)-0.4);
	    standing.setOpptOpptWins(2);
	    standing.setOpptOpptGamesPlayed(3);
	    standing.setAvgOpptOpptWinPercentage((float)0.4);
    	return standing;
    }
	
	protected static Standing getStandingAwayCompleted(Team team) {
		Standing standing = new Standing();
		standing.setTeamKey(team.getKey());
		standing.setGameDate(new DateTime(2013, 7, 5, 0, 0, 0));
    	standing.setRank((short)2);
    	standing.setOrdinalRank("2nd");
	    standing.setGamesWon((short)95);
	    standing.setGamesLost((short)102);
	    standing.setStreak("W1");
	    standing.setStreakType(StreakType.win);
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
	    standing.setOpptOpptWins(1);
	    standing.setOpptOpptGamesPlayed(2);
	    standing.setAvgOpptOpptWinPercentage((float)0.5);
    	return standing;
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
