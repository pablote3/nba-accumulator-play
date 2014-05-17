package json.xmlStats;

import java.util.ArrayList;
import java.util.List;

import models.BoxScore;
import models.BoxScorePlayer;
import models.RosterPlayer.Position;
import models.Game.ProcessingType;
import models.GameOfficial;
import models.Official;
import models.PeriodScore;
import models.Player;
import models.RosterPlayer;
import models.Team;
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
        	String teamKey = Team.findByAbbr(boxScorePlayerDTO.getTeam_abbreviation(), processingType).getKey();
        	rosterPlayer = RosterPlayer.findByDatePlayerNameTeam(gameDate, lastName, firstName, teamKey, processingType);
        	if (rosterPlayer == null) {
        		return null;
        	}        	
        	else {
	        	boxScorePlayer = new BoxScorePlayer();
	        	boxScorePlayer.setRosterPlayer(rosterPlayer);
	        	boxScorePlayer.setPosition(BoxScorePlayer.Position.valueOf(boxScorePlayerDTO.getPosition()));
	        	boxScorePlayer.setMinutes(boxScorePlayerDTO.getMinutes());
	        	boxScorePlayer.setStarter(boxScorePlayerDTO.getIs_starter());
	            boxScorePlayer.setPoints(boxScorePlayerDTO.getPoints());
	            boxScorePlayer.setAssists(boxScorePlayerDTO.getAssists());
	            boxScorePlayer.setTurnovers(boxScorePlayerDTO.getTurnovers());
	            boxScorePlayer.setSteals(boxScorePlayerDTO.getSteals());
	            boxScorePlayer.setBlocks(boxScorePlayerDTO.getBlocks());
	            boxScorePlayer.setFieldGoalAttempts(boxScorePlayerDTO.getField_goals_attempted());
	            boxScorePlayer.setFieldGoalMade(boxScorePlayerDTO.getField_goals_made());
	            boxScorePlayer.setFieldGoalPercent(boxScorePlayerDTO.getField_goal_percentage());
	            boxScorePlayer.setThreePointAttempts(boxScorePlayerDTO.getThree_point_field_goals_attempted());
	            boxScorePlayer.setThreePointMade(boxScorePlayerDTO.getThree_point_field_goals_made());
	            boxScorePlayer.setThreePointPercent(boxScorePlayerDTO.getThree_point_percentage());
	            boxScorePlayer.setFreeThrowAttempts(boxScorePlayerDTO.getFree_throws_attempted());
	            boxScorePlayer.setFreeThrowMade(boxScorePlayerDTO.getFree_throws_made());
	            boxScorePlayer.setFreeThrowPercent(boxScorePlayerDTO.getFree_throw_percentage());
	            boxScorePlayer.setReboundsOffense(boxScorePlayerDTO.getOffensive_rebounds());
	            boxScorePlayer.setReboundsDefense(boxScorePlayerDTO.getDefensive_rebounds());
	            boxScorePlayer.setPersonalFouls(boxScorePlayerDTO.getPersonal_fouls());       		
	        	boxScorePlayers.add(boxScorePlayer);
        	}
        }
    	return boxScorePlayers;
    }
	
	public static List<RosterPlayer> getRosterPlayers(Roster xmlStatsRoster, ProcessingType processingType) {
		RosterPlayerDTO[] rosterPlayerDTOs = xmlStatsRoster.players;
		Team team = Team.findByTeamKey(xmlStatsRoster.team.getKey(), processingType);
    	List<RosterPlayer> rosterPlayers = new ArrayList<RosterPlayer>();	    
	    Player player;
	    RosterPlayer rosterPlayer;
        for (int i = 0; i < rosterPlayerDTOs.length; i++) {
        	player = new Player();
        	player.setLastName(rosterPlayerDTOs[i].getLast_name());
        	player.setFirstName(rosterPlayerDTOs[i].getFirst_name());
        	player.setDisplayName(rosterPlayerDTOs[i].getDisplay_name());
        	player.setHeight(rosterPlayerDTOs[i].getHeight_in());
        	player.setWeight(rosterPlayerDTOs[i].getWeight_lb());
        	player.setBirthDate(DateTime.createDateMinTime(rosterPlayerDTOs[i].getBirthdate()));
        	player.setBirthPlace(rosterPlayerDTOs[i].getBirthplace());
        	rosterPlayer = new RosterPlayer();
        	rosterPlayer.setPlayer(player);
        	rosterPlayer.setTeam(team);
        	rosterPlayer.setNumber(rosterPlayerDTOs[i].getUniform_number());
        	rosterPlayer.setPosition(Position.valueOf(rosterPlayerDTOs[i].getPosition()));
        	rosterPlayers.add(rosterPlayer);
        }
	    return rosterPlayers;
    }
}
