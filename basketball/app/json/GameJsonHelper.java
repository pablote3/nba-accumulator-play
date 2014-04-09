package json;

import java.util.ArrayList;
import java.util.List;

import models.BoxScore;
import models.BoxScorePlayer;
import models.Game.ProcessingType;
import models.GameOfficial;
import models.Official;
import models.PeriodScore;
import models.RosterPlayer;

public class GameJsonHelper {
   
	public static List<PeriodScore> getPeriodScores(int[] scores) {
    	List<PeriodScore> periodScores = new ArrayList<PeriodScore>();	    
	    PeriodScore periodScore;

        for (int i = 0; i < scores.length; i++) {
        	periodScore = new PeriodScore();
      		periodScore.setQuarter((short)(i+1));
      		periodScore.setScore((short)scores[i]);
			periodScores.add(periodScore);
        }
	    return periodScores;
    }
    
	public static BoxScore getBoxScoreStats(BoxScore boxScore, BoxScore stats) {
        boxScore.setPoints(stats.getPoints());
        boxScore.setAssists(stats.getAssists());
        boxScore.setTurnovers(stats.getTurnovers());
        boxScore.setSteals(stats.getSteals());
        boxScore.setBlocks(stats.getBlocks());
        boxScore.setFieldGoalAttempts(stats.getFieldGoalAttempts());
        boxScore.setFieldGoalMade(stats.getFieldGoalMade());
        boxScore.setFieldGoalPercent(stats.getFieldGoalPercent());
        boxScore.setThreePointAttempts(stats.getThreePointAttempts());
        boxScore.setThreePointMade(stats.getThreePointMade());
        boxScore.setThreePointPercent(stats.getThreePointPercent());
        boxScore.setFreeThrowAttempts(stats.getFreeThrowAttempts());
        boxScore.setFreeThrowMade(stats.getFreeThrowMade());
        boxScore.setFreeThrowPercent(stats.getFreeThrowPercent());
        boxScore.setReboundsOffense(stats.getReboundsOffense());
        boxScore.setReboundsDefense(stats.getReboundsDefense());
        boxScore.setPersonalFouls(stats.getPersonalFouls());
    	return boxScore;
    }
	
	public static List<GameOfficial> getGameOfficials(Official[] officials, ProcessingType processingType) {
    	List<GameOfficial> gameOfficials = new ArrayList<GameOfficial>();
	    GameOfficial gameOfficial;
	    Official official;
	    
        for (int i = 0; i < officials.length; i++) {
        	official = Official.findByName(officials[i].getLastName(), officials[i].getFirstName(), processingType);
        	gameOfficial = new GameOfficial();
        	gameOfficial.setOfficial(official);
        	gameOfficials.add(gameOfficial);
        }
    	return gameOfficials;
    }
	
	public static List<BoxScorePlayer> getBoxScorePlayers(BoxScorePlayer[] players, String date, ProcessingType processingType) {
    	List<BoxScorePlayer> boxScorePlayers = new ArrayList<BoxScorePlayer>();
    	BoxScorePlayer statsBoxScorePlayer;
	    BoxScorePlayer boxScorePlayer;
	    RosterPlayer rosterPlayer;
	    
        for (int i = 0; i < players.length; i++) {
        	statsBoxScorePlayer = players[i];
        	String lastName = statsBoxScorePlayer.getRosterPlayer().getPlayer().getLastName();
        	String firstName = statsBoxScorePlayer.getRosterPlayer().getPlayer().getFirstName();
        	rosterPlayer = RosterPlayer.findByDatePlayerName(date, lastName, firstName, processingType);
        	if (rosterPlayer != null) {       		
        		boxScorePlayer = new BoxScorePlayer();
        		boxScorePlayer.setRosterPlayer(rosterPlayer);
        		boxScorePlayer.setPosition(statsBoxScorePlayer.getPosition());
        		boxScorePlayer.setMinutes(statsBoxScorePlayer.getMinutes());
        		boxScorePlayer.setStarter(statsBoxScorePlayer.getStarter());
                boxScorePlayer.setPoints(statsBoxScorePlayer.getPoints());
                boxScorePlayer.setAssists(statsBoxScorePlayer.getAssists());
                boxScorePlayer.setTurnovers(statsBoxScorePlayer.getTurnovers());
                boxScorePlayer.setSteals(statsBoxScorePlayer.getSteals());
                boxScorePlayer.setBlocks(statsBoxScorePlayer.getBlocks());
                boxScorePlayer.setFieldGoalAttempts(statsBoxScorePlayer.getFieldGoalAttempts());
                boxScorePlayer.setFieldGoalMade(statsBoxScorePlayer.getFieldGoalMade());
                boxScorePlayer.setFieldGoalPercent(statsBoxScorePlayer.getFieldGoalPercent());
                boxScorePlayer.setThreePointAttempts(statsBoxScorePlayer.getThreePointAttempts());
                boxScorePlayer.setThreePointMade(statsBoxScorePlayer.getThreePointMade());
                boxScorePlayer.setThreePointPercent(statsBoxScorePlayer.getThreePointPercent());
                boxScorePlayer.setFreeThrowAttempts(statsBoxScorePlayer.getFreeThrowAttempts());
                boxScorePlayer.setFreeThrowMade(statsBoxScorePlayer.getFreeThrowMade());
                boxScorePlayer.setFreeThrowPercent(statsBoxScorePlayer.getFreeThrowPercent());
                boxScorePlayer.setReboundsOffense(statsBoxScorePlayer.getReboundsOffense());
                boxScorePlayer.setReboundsDefense(statsBoxScorePlayer.getReboundsDefense());
                boxScorePlayer.setPersonalFouls(statsBoxScorePlayer.getPersonalFouls());       		
        		boxScorePlayers.add(boxScorePlayer);
        	}
        }
    	return boxScorePlayers;
    }
}
