package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.entity.BoxScore;
import models.entity.BoxScore.Location;
import models.entity.Game;
import models.entity.Game.SeasonType;
import models.entity.Game.Status;
import models.entity.Team;
import models.partial.GameKey;

import org.junit.Test;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

public class GameTest {

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
    public void findGameKeysDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<GameKey> games = Game.findKeyByDate("2012-10-31");     
              assertThat(games.size()).isEqualTo(9);
          }
        });
    }
    
    @Test
    public void findGameDateTeam() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Game game = Game.findByDateTeam("2012-10-31", "sacramento-kings");
        	  assertThat(game.getSeasonType()).isEqualTo(SeasonType.regular);
        	  for (int i = 0; i < game.getBoxScores().size(); i++) {
        		  BoxScore boxScore = game.getBoxScores().get(i);
        		  if (boxScore.getLocation().equals(Location.away)) {
        			  assertThat(game.getBoxScores().get(0).getTeam().getAbbr()).isEqualTo("SAC");
        		  }
        		  else {
        			  assertThat(game.getBoxScores().get(0).getTeam().getAbbr()).isEqualTo("CHI");
        		  }
        	  }
          }
        });
    }
    
    public void findGameKeyByDateTeam() {
        running(fakeApplication(), new Runnable() {
            public void run() {
          	  GameKey game = Game.findKeyByDateTeam("2012-10-31", "sacramento-kings");
          	  assertThat(game.getHomeTeamKey()).isEqualTo("chicago-bulls");
            }
        });
    }
    
    @Test
    public void createGameScheduled() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	Game game = TestMockHelper.getGameScheduled();
		    
		    BoxScore homeBoxScore = TestMockHelper.getBoxScoreHomeScheduled();
		    homeBoxScore.setTeam(Team.find.where().eq("key", "new-orleans-pelicans").findUnique());
		    game.addBoxScore(homeBoxScore);
		    
		    BoxScore awayBoxScore = TestMockHelper.getBoxScoreAwayScheduled();
		    awayBoxScore.setTeam(Team.find.where().eq("key", "sacramento-kings").findUnique());
		    game.addBoxScore(awayBoxScore);
		    
		    Game.create(game);
		    
		    Game createGame = Game.findByDateTeam("2013-07-04", "sacramento-kings");
		    assertThat(createGame.getSeasonType()).isEqualTo(SeasonType.pre);
      	  	for (int i = 0; i < createGame.getBoxScores().size(); i++) {
      	  		BoxScore boxScore = createGame.getBoxScores().get(i);
      	  		if (boxScore.getLocation().equals(Location.away)) {
      	  			assertThat(boxScore.getTeam().getAbbr()).isEqualTo("SAC");
      	  		}
      	  		else {
      	  			assertThat(boxScore.getTeam().getAbbr()).isEqualTo("NOP");
      	  		}
      	  	}
            Game.delete(createGame.getId());		    
		  }
		});
	}
    
    @Test
    public void createGameCompleted() {
        running(fakeApplication(), new Runnable() {
          public void run() {  
        	Game game = TestMockHelper.getGameCompleted();
        	game.setGameOfficials(TestMockHelper.getGameOfficials());
		    
		    BoxScore homeBoxScore = TestMockHelper.getBoxScoreHomeCompleted(TestMockHelper.getBoxScoreHomeScheduled());
		    homeBoxScore.setTeam(Team.find.where().eq("key", "toronto-raptors").findUnique());
		    homeBoxScore.setPeriodScores(TestMockHelper.getPeriodScoresHome());
		    game.addBoxScore(homeBoxScore);
		    
		    BoxScore awayBoxScore = TestMockHelper.getBoxScoreAwayCompleted(TestMockHelper.getBoxScoreAwayScheduled());
		    awayBoxScore.setTeam(Team.find.where().eq("key", "detroit-pistons").findUnique());
		    awayBoxScore.setPeriodScores(TestMockHelper.getPeriodScoresAway());
		    game.addBoxScore(awayBoxScore);
		    
		    Game.create(game);
		    
		    Game createGame = Game.findByDateTeam("2013-07-05", "toronto-raptors");
            assertThat(createGame.getSeasonType()).isEqualTo(SeasonType.pre);
            if (createGame.getGameOfficials().size() > 0)
            	assertThat(createGame.getGameOfficials().get(0).getOfficial().getLastName()).endsWith("Brown");
            for (int i = 0; i < createGame.getBoxScores().size(); i++) {
            	BoxScore boxScore = createGame.getBoxScores().get(i);
            	if (boxScore.getLocation().equals(Location.away)) {
            		assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)29);
            		if (boxScore.getPeriodScores().size() > 0)
            			assertThat(boxScore.getPeriodScores().get(0).getScore()).isEqualTo((short)25);
            		assertThat(boxScore.getTeam().getAbbr()).isEqualTo("DET");           		
            	}
            	else {
            		assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)30);
            		if (boxScore.getPeriodScores().size() > 0)
            			assertThat(boxScore.getPeriodScores().get(0).getScore()).isEqualTo((short)25);
            		assertThat(boxScore.getTeam().getAbbr()).isEqualTo("TOR");
            	}
            }
            Game.delete(createGame.getId());	
		  }
		});
	}
    
    @Test
    public void updateGameScheduled() {
        running(fakeApplication(), new Runnable() {
          public void run() {  
          	Game scheduleGame = TestMockHelper.getGameScheduled();
		    
  		    BoxScore homeBoxScore = TestMockHelper.getBoxScoreHomeScheduled();
  		    homeBoxScore.setTeam(Team.find.where().eq("key", "new-orleans-pelicans").findUnique());
  		    scheduleGame.addBoxScore(homeBoxScore);
  		    
  		    BoxScore awayBoxScore = TestMockHelper.getBoxScoreAwayScheduled();
  		    awayBoxScore.setTeam(Team.find.where().eq("key", "sacramento-kings").findUnique());
  		    scheduleGame.addBoxScore(awayBoxScore);
  		    
  		    Game.create(scheduleGame);

  		    Game completeGame = Game.findByDateTeam("2013-07-04", "sacramento-kings");
  		    
  		    completeGame.setStatus(Status.completed);
  		    completeGame.setGameOfficials(TestMockHelper.getGameOfficials());
  		    
  		    for (int i = 0; i < completeGame.getBoxScores().size(); i++) {
				BoxScore boxScore = completeGame.getBoxScores().get(i);
				if (boxScore.getLocation().equals(Location.away)) {
					TestMockHelper.getBoxScoreAwayCompleted(boxScore);
					boxScore.setPeriodScores(TestMockHelper.getPeriodScoresAway());
				} 
				else {
					TestMockHelper.getBoxScoreHomeCompleted(boxScore);
					boxScore.setPeriodScores(TestMockHelper.getPeriodScoresHome());
				}
			}

  		    completeGame.update();
  		    
  		    Game updateGame = Game.findByDateTeam("2013-07-04", "sacramento-kings");
            assertThat(updateGame.getSeasonType()).isEqualTo(SeasonType.pre);
            if (updateGame.getGameOfficials().size() > 0)
            	assertThat(updateGame.getGameOfficials().get(0).getOfficial().getLastName()).endsWith("Brown");
            for (int i = 0; i < updateGame.getBoxScores().size(); i++) {
            	BoxScore boxScore = updateGame.getBoxScores().get(i);
            	if (boxScore.getLocation().equals(Location.away)) {
                    assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)29);
                    assertThat(boxScore.getTeam().getAbbr()).isEqualTo("SAC");
                    if (boxScore.getPeriodScores().size() > 0)
                    	assertThat(boxScore.getPeriodScores().get(0).getScore()).isEqualTo((short)25);
            	}
            	else {
                    assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)29);
                    assertThat(boxScore.getTeam().getAbbr()).isEqualTo("NOP");
                    if (boxScore.getPeriodScores().size() > 0)
                    	assertThat(boxScore.getPeriodScores().get(0).getScore()).isEqualTo((short)25);
            	}
            }
            Game.delete(updateGame.getId());	
		  }
		});
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
}
