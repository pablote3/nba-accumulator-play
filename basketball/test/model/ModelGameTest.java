package model;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import models.entity.*;
import models.entity.BoxScore.Location;
import models.entity.Game.SeasonType;
import models.entity.Game.Status;

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
              Game game = new Game();
              Date date = null;
              try {
            	  date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2012-07-05");
              } catch (ParseException e) {
            	  e.printStackTrace();
              }
              game.setDate(date);
              game.setStatus(Status.scheduled);
              game.setSeasonType(SeasonType.regular);
              
              BoxScore boxScoreHome = new BoxScore();
              boxScoreHome.setLocation(Location.home);
              boxScoreHome.setTeam(Team.find.where().eq("fullName", "Sacramento Kings").findUnique());
              game.addBoxScore(boxScoreHome);
              
              BoxScore boxScoreAway = new BoxScore();
              boxScoreAway.setLocation(Location.away);
              boxScoreAway.setTeam(Team.find.where().eq("fullName", "Golden State Warriors").findUnique());              
              game.addBoxScore(boxScoreAway);
              
              Game.create(game);
          
              //does finder need unique value or list returned
              Game createGame = Game.find.where().eq("date", date).findUnique();
              assertThat(createGame.getBoxScores().size()).isEqualTo(2);
              assertThat(createGame.getStatus()).isEqualTo(Status.scheduled);
              Game.delete(createGame.getId());
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
    
    //	http://scores.espn.go.com/nba/scoreboard?date=20121031

    @Test
    public void findGamesByDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {                      	  
        	  String gameDate = "2012-10-31%";
        	  
        	  Query<Game> query = Ebean.find(Game.class);
        	  query.fetch("boxScores");
        	  query.fetch("boxScores.team");
              query.where().ilike("date", gameDate);

              List<Game> games = query.findList();
              Game game = null;
              Iterator<Game> iter = games.iterator();
              while (iter.hasNext()) {
            	  game = iter.next();
            	  System.out.println(game.toString());
              }
              
              assertThat(games.size()).isEqualTo(9);
          }
        });
    }
    
}
