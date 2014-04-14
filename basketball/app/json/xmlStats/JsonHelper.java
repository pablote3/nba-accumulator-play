package json.xmlStats;

import java.util.ArrayList;
import java.util.List;

import models.BoxScore;
import models.BoxScorePlayer;
import models.Game.ProcessingType;
import models.GameOfficial;
import models.Official;
import models.PeriodScore;
import models.RosterPlayer;
import models.BoxScorePlayer.Position;

public class JsonHelper {
   
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
	
	public static List<BoxScorePlayer> getBoxScorePlayers(BoxScorePlayerDTO[] players, String date, ProcessingType processingType) {
    	List<BoxScorePlayer> boxScorePlayers = new ArrayList<BoxScorePlayer>();
    	BoxScorePlayerDTO statsBoxScorePlayerDTO;
	    BoxScorePlayer boxScorePlayer;
	    RosterPlayer rosterPlayer;
	    
        for (int i = 0; i < players.length; i++) {
        	statsBoxScorePlayerDTO = players[i];
        	String lastName = statsBoxScorePlayerDTO.getLast_name();
        	String firstName = statsBoxScorePlayerDTO.getFirst_name();
        	String teamAbbr = statsBoxScorePlayerDTO.getTeam_abbreviation();
        	rosterPlayer = RosterPlayer.findByDateTeamPlayer(date, teamAbbr, lastName, firstName, processingType);
        	if (rosterPlayer == null) {
        		
        	}
        	boxScorePlayer = new BoxScorePlayer();
        	boxScorePlayer.setRosterPlayer(rosterPlayer);
        	boxScorePlayer.setPosition(Position.valueOf(statsBoxScorePlayerDTO.getPosition()));
        	boxScorePlayer.setMinutes(statsBoxScorePlayerDTO.getMinutes());
        	boxScorePlayer.setStarter(statsBoxScorePlayerDTO.getStarter());
            boxScorePlayer.setPoints(statsBoxScorePlayerDTO.getPoints());
            boxScorePlayer.setAssists(statsBoxScorePlayerDTO.getAssists());
            boxScorePlayer.setTurnovers(statsBoxScorePlayerDTO.getTurnovers());
            boxScorePlayer.setSteals(statsBoxScorePlayerDTO.getSteals());
            boxScorePlayer.setBlocks(statsBoxScorePlayerDTO.getBlocks());
            boxScorePlayer.setFieldGoalAttempts(statsBoxScorePlayerDTO.getFieldGoalAttempts());
            boxScorePlayer.setFieldGoalMade(statsBoxScorePlayerDTO.getFieldGoalMade());
            boxScorePlayer.setFieldGoalPercent(statsBoxScorePlayerDTO.getFieldGoalPercent());
            boxScorePlayer.setThreePointAttempts(statsBoxScorePlayerDTO.getThreePointAttempts());
            boxScorePlayer.setThreePointMade(statsBoxScorePlayerDTO.getThreePointMade());
            boxScorePlayer.setThreePointPercent(statsBoxScorePlayerDTO.getThreePointPercent());
            boxScorePlayer.setFreeThrowAttempts(statsBoxScorePlayerDTO.getFreeThrowAttempts());
            boxScorePlayer.setFreeThrowMade(statsBoxScorePlayerDTO.getFreeThrowMade());
            boxScorePlayer.setFreeThrowPercent(statsBoxScorePlayerDTO.getFreeThrowPercent());
            boxScorePlayer.setReboundsOffense(statsBoxScorePlayerDTO.getReboundsOffense());
            boxScorePlayer.setReboundsDefense(statsBoxScorePlayerDTO.getReboundsDefense());
            boxScorePlayer.setPersonalFouls(statsBoxScorePlayerDTO.getPersonalFouls());       		
        	boxScorePlayers.add(boxScorePlayer);
        }
    	return boxScorePlayers;
    }
}
