package model;

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

import org.junit.Test;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

public class ModelGameTest {

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
    public void findGameDateTeam() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Game game = Game.findByDateTeamKey("2012-10-31", "sacramento-kings");        
              assertThat(game.getSeasonType()).isEqualTo(SeasonType.regular);
              assertThat(game.getBoxScores().get(0).getLocation()).isEqualTo(Location.away);
              assertThat(game.getBoxScores().get(0).getTeam().getAbbr()).isEqualTo("SAC");
          }
        });
    }
    
    @Test
    public void createGameScheduled() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	Game game = MockTestHelper.getGameScheduled();
		    
		    BoxScore homeBoxScore = MockTestHelper.getBoxScoreHomeScheduled();
		    homeBoxScore.setTeam(Team.find.where().eq("key", "new-orleans-pelicans").findUnique());
		    game.addBoxScore(homeBoxScore);
		    
		    BoxScore awayBoxScore = MockTestHelper.getBoxScoreAwayScheduled();
		    awayBoxScore.setTeam(Team.find.where().eq("key", "sacramento-kings").findUnique());
		    game.addBoxScore(awayBoxScore);
		    
		    Game.create(game);
		    
		    Game createGame = Game.findByDateTeamKey("2013-07-04", "sacramento-kings");
            assertThat(createGame.getSeasonType()).isEqualTo(SeasonType.pre);
            assertThat(createGame.getBoxScores().get(0).getLocation()).isEqualTo(Location.away);
            assertThat(createGame.getBoxScores().get(0).getTeam().getAbbr()).isEqualTo("SAC");
            Game.delete(createGame.getId());		    
		  }
		});
	}
    
    @Test
    public void createGameCompleted() {
        running(fakeApplication(), new Runnable() {
          public void run() {  
        	Game game = MockTestHelper.getGameCompleted();
        	game.setGameOfficials(MockTestHelper.getGameOfficials());
		    
		    BoxScore homeBoxScore = MockTestHelper.getBoxScoreHomeCompleted(MockTestHelper.getBoxScoreHomeScheduled());
		    homeBoxScore.setTeam(Team.find.where().eq("key", "toronto-raptors").findUnique());
		    homeBoxScore.setPeriodScores(MockTestHelper.getPeriodScoresHome());
		    game.addBoxScore(homeBoxScore);
		    
		    BoxScore awayBoxScore = MockTestHelper.getBoxScoreAwayCompleted(MockTestHelper.getBoxScoreAwayScheduled());
		    awayBoxScore.setTeam(Team.find.where().eq("key", "detroit-pistons").findUnique());
		    awayBoxScore.setPeriodScores(MockTestHelper.getPeriodScoresAway());
		    game.addBoxScore(awayBoxScore);
		    
		    Game.create(game);
		    
		    Game createGame = Game.findByDateTeamKey("2013-07-05", "toronto-raptors");
            assertThat(createGame.getSeasonType()).isEqualTo(SeasonType.pre);
            assertThat(createGame.getGameOfficials().get(0).getOfficial().getLastName()).endsWith("Brown");
            assertThat(createGame.getBoxScores().get(0).getLocation()).isEqualTo(Location.home);
            assertThat(createGame.getBoxScores().get(0).getFieldGoalMade()).isEqualTo((short)30);
            assertThat(createGame.getBoxScores().get(0).getPeriodScores().get(0).getScore()).isEqualTo((short)25);
            assertThat(createGame.getBoxScores().get(0).getTeam().getAbbr()).isEqualTo("TOR");
            Game.delete(createGame.getId());	
		  }
		});
	}
    
    @Test
    public void updateGameScheduled() {
        running(fakeApplication(), new Runnable() {
          public void run() {  
          	Game scheduleGame = MockTestHelper.getGameScheduled();
		    
  		    BoxScore homeBoxScore = MockTestHelper.getBoxScoreHomeScheduled();
  		    homeBoxScore.setTeam(Team.find.where().eq("key", "new-orleans-pelicans").findUnique());
  		    scheduleGame.addBoxScore(homeBoxScore);
  		    
  		    BoxScore awayBoxScore = MockTestHelper.getBoxScoreAwayScheduled();
  		    awayBoxScore.setTeam(Team.find.where().eq("key", "sacramento-kings").findUnique());
  		    scheduleGame.addBoxScore(awayBoxScore);
  		    
  		    Game.create(scheduleGame);
  		    
  		    Game completeGame = Game.findByDateTeamKey("2013-07-04", "sacramento-kings");
  		    completeGame.setStatus(Status.completed);
  		    completeGame.setGameOfficials(MockTestHelper.getGameOfficials());
  		    
  		    for (int i = 0; i < completeGame.getBoxScores().size(); i++) {
				BoxScore boxScore = completeGame.getBoxScores().get(i);
				if (boxScore.getLocation().equals(Location.away)) {
					MockTestHelper.getBoxScoreAwayCompleted(boxScore);
					boxScore.setPeriodScores(MockTestHelper.getPeriodScoresAway());
				} 
				else {
					MockTestHelper.getBoxScoreHomeCompleted(boxScore);
					boxScore.setPeriodScores(MockTestHelper.getPeriodScoresHome());
				}
			}

  		    completeGame.update();
  		    
  		    Game updateGame = Game.findByDateTeamKey("2013-07-04", "sacramento-kings");
            assertThat(updateGame.getSeasonType()).isEqualTo(SeasonType.pre);
            assertThat(updateGame.getGameOfficials().get(0).getOfficial().getLastName()).endsWith("Brown");
            assertThat(updateGame.getBoxScores().get(0).getLocation()).isEqualTo(Location.away);
            assertThat(updateGame.getBoxScores().get(0).getFieldGoalMade()).isEqualTo((short)29);
            assertThat(updateGame.getBoxScores().get(0).getPeriodScores().get(0).getScore()).isEqualTo((short)25);
            assertThat(updateGame.getBoxScores().get(0).getTeam().getAbbr()).isEqualTo("SAC");
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
