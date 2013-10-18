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
import models.entity.Game;
import models.entity.Team;

import org.junit.Test;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

public class ModelGameTest {

    @Test
    public void createGameScheduled() {
        running(fakeApplication(), new Runnable() {
          public void run() {  
        	Game game = MockTestHelper.getGameScheduled();
        	game.setGameOfficials(MockTestHelper.getGameOfficials());
		    
		    BoxScore homeBoxScore = MockTestHelper.getBoxScoreHomeScheduled();
		    homeBoxScore.setTeam(Team.find.where().eq("key", "new-orleans-pelicans").findUnique());
		    game.addBoxScore(homeBoxScore);
		    
		    BoxScore awayBoxScore = MockTestHelper.getBoxScoreAwayScheduled();
		    awayBoxScore.setTeam(Team.find.where().eq("key", "sacramento-kings").findUnique());
		    game.addBoxScore(awayBoxScore);
		
		    System.out.println(game.toString());
		    
		//    Game.create(game);
		  }
		});
	}
    
    @Test
    public void createGameCompleted() {
        running(fakeApplication(), new Runnable() {
          public void run() {  
        	Game game = MockTestHelper.getGameCompleted();
        	game.setGameOfficials(MockTestHelper.getGameOfficials());
		    
		    BoxScore homeBoxScore = MockTestHelper.getBoxScoreHomeCompleted();
		    homeBoxScore.setTeam(Team.find.where().eq("key", "new-orleans-pelicans").findUnique());
		    homeBoxScore.setPeriodScores(MockTestHelper.getPeriodScoresHome());
		    game.addBoxScore(homeBoxScore);
		    
		    BoxScore awayBoxScore = MockTestHelper.getBoxScoreAwayCompleted();
		    awayBoxScore.setTeam(Team.find.where().eq("key", "sacramento-kings").findUnique());
		    awayBoxScore.setPeriodScores(MockTestHelper.getPeriodScoresAway());
		    game.addBoxScore(awayBoxScore);
		
		    System.out.println(game.toString());
		    
		//    Game.create(game);
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
    public void findGamesFinderDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {                      	  
        	  String gameDate = "2012-10-31";
        	  
        	  Query<Game> query = Ebean.find(Game.class);
        	  query.fetch("boxScores");
        	  query.fetch("boxScores.team");
              query.where().ilike("date", gameDate + "%");

              List<Game> games = query.findList();
              assertThat(games.size()).isEqualTo(9);
//              Game game = null;
//              java.util.Iterator<Game> iter = games.iterator();
//              while (iter.hasNext()) {
//            	  game = iter.next();
//            	  System.out.println(game.toString());
//              }
          }
        });
    }
}
