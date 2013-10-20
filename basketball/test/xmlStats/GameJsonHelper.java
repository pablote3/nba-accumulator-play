package xmlStats;

import java.util.ArrayList;
import java.util.List;

import models.entity.BoxScore;
import models.entity.GameOfficial;
import models.entity.Official;
import models.entity.PeriodScore;

public class GameJsonHelper {
   
	protected static List<GameOfficial> getGameOfficials(Official[] officials) {
    	List<GameOfficial> gameOfficials = new ArrayList<GameOfficial>();
	    GameOfficial gameOfficial;
	    Official official;
	    
        for (int i = 0; i < officials.length; i++) {
      	  official = Official.findByName(officials[i].getLastName(), officials[i].getFirstName());
      	  gameOfficial = new GameOfficial();
      	  gameOfficial.setOfficial(official);
      	  gameOfficials.add(gameOfficial);
        }
    	return gameOfficials;
    }
	
	protected static List<PeriodScore> getPeriodScores(int[] scores) {
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
    
	protected static BoxScore getBoxScore(BoxScore stats) {
		BoxScore boxScore = new BoxScore();
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
    
}