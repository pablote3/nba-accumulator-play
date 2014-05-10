package json.xmlStats;

import java.util.ArrayList;
import java.util.List;

import models.BoxScore;
import models.BoxScorePlayer;
import models.BoxScorePlayer.Position;
import models.Game.ProcessingType;
import models.GameOfficial;
import models.Official;
import models.PeriodScore;
import models.Player;
import models.RosterPlayer;
import util.DateTime;

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
	
	public static List<BoxScorePlayer> getBoxScorePlayers(BoxScorePlayerDTO[] boxScorePlayerDTOs, String gameDate, ProcessingType processingType) {
    	List<BoxScorePlayer> boxScorePlayers = new ArrayList<BoxScorePlayer>();
    	BoxScorePlayerDTO boxScorePlayerDTO;
	    BoxScorePlayer boxScorePlayer;
	    RosterPlayer rosterPlayer;
	    
        for (int i = 0; i < boxScorePlayerDTOs.length; i++) {
        	boxScorePlayerDTO = boxScorePlayerDTOs[i];
        	String lastName = boxScorePlayerDTO.getLast_name();
        	String firstName = boxScorePlayerDTO.getFirst_name();
        	String teamAbbr = boxScorePlayerDTO.getTeam_abbreviation();
        	rosterPlayer = RosterPlayer.findByDatePlayerNameTeam(gameDate, lastName, firstName, teamAbbr, processingType);
        	if (rosterPlayer == null) {
        		return null;
        	}        	
        	else {
	        	boxScorePlayer = new BoxScorePlayer();
	        	boxScorePlayer.setRosterPlayer(rosterPlayer);
	        	boxScorePlayer.setPosition(Position.valueOf(boxScorePlayerDTO.getPosition()));
	        	boxScorePlayer.setMinutes(boxScorePlayerDTO.getMinutes());
	        	boxScorePlayer.setStarter(boxScorePlayerDTO.getIs_starter());
	            boxScorePlayer.setPoints(boxScorePlayerDTO.getPoints());
	            boxScorePlayer.setAssists(boxScorePlayerDTO.getAssists());
	            boxScorePlayer.setTurnovers(boxScorePlayerDTO.getTurnovers());
	            boxScorePlayer.setSteals(boxScorePlayerDTO.getSteals());
	            boxScorePlayer.setBlocks(boxScorePlayerDTO.getBlocks());
	            boxScorePlayer.setFieldGoalAttempts(boxScorePlayerDTO.getFieldGoalAttempts());
	            boxScorePlayer.setFieldGoalMade(boxScorePlayerDTO.getFieldGoalMade());
	            boxScorePlayer.setFieldGoalPercent(boxScorePlayerDTO.getFieldGoalPercent());
	            boxScorePlayer.setThreePointAttempts(boxScorePlayerDTO.getThreePointAttempts());
	            boxScorePlayer.setThreePointMade(boxScorePlayerDTO.getThreePointMade());
	            boxScorePlayer.setThreePointPercent(boxScorePlayerDTO.getThreePointPercent());
	            boxScorePlayer.setFreeThrowAttempts(boxScorePlayerDTO.getFreeThrowAttempts());
	            boxScorePlayer.setFreeThrowMade(boxScorePlayerDTO.getFreeThrowMade());
	            boxScorePlayer.setFreeThrowPercent(boxScorePlayerDTO.getFreeThrowPercent());
	            boxScorePlayer.setReboundsOffense(boxScorePlayerDTO.getReboundsOffense());
	            boxScorePlayer.setReboundsDefense(boxScorePlayerDTO.getReboundsDefense());
	            boxScorePlayer.setPersonalFouls(boxScorePlayerDTO.getPersonalFouls());       		
	        	boxScorePlayers.add(boxScorePlayer);
        	}
        }
    	return boxScorePlayers;
    }
	
	public static List<Player> getPlayers(Player[] playerDTOs) {
    	List<Player> players = new ArrayList<Player>();	    
	    Player player;

        for (int i = 0; i < playerDTOs.length; i++) {
        	player = new Player();
        	player.setLastName(playerDTOs[i].getLastName());
        	player.setFirstName(playerDTOs[i].getFirstName());
        	player.setDisplayName(playerDTOs[i].getDisplayName());
        	player.setHeight(playerDTOs[i].getHeight());
        	player.setWeight(playerDTOs[i].getWeight());
        	player.setBirthDate(DateTime.createDateMinTime(playerDTOs[i].getBirthDate()));
        	player.setBirthPlace(playerDTOs[i].getBirthPlace());
        	players.add(player);
        }
	    return players;
    }
}
