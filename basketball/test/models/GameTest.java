package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.BoxScore.Location;
import models.Game.ProcessingType;
import models.Game.SeasonType;
import models.Game.Status;

import org.junit.Ignore;
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
    public void findGameIdsDateSizeOnline_Season() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Long> games = Game.findIdsByDateSize("2012-10-30", "0", ProcessingType.online);
              assertThat(games.size()).isEqualTo(1230);
          }
        });
    }
    
    @Test
    public void findGameIdsDateSizeOnline_Game() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Long> games = Game.findIdsByDateSize("2012-10-31", "1", ProcessingType.online);
              assertThat(games.size()).isEqualTo(1);
          }
        });
    }
    
    @Test
    public void findGameIdsDateSizeBatch_Season() {
    	List<Long> games = Game.findIdsByDateSize("2012-10-30", "0", ProcessingType.batch);
        assertThat(games.size()).isEqualTo(1230);
    }
    
    @Test
    public void findGameIdsDateSizeBatch_Game() {
    	List<Long> games = Game.findIdsByDateSize("2012-10-30", "1", ProcessingType.batch);
        assertThat(games.size()).isEqualTo(1);
    }
    
    @Test
    public void findGameDateTeam() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Game game = Game.findByDateTeam("2012-10-31", "sacramento-kings");
        	  assertThat(game.getSeasonType()).isEqualTo(SeasonType.regular);
        	  assertThat(game.getBoxScores().size()).isEqualTo(1);
       		  BoxScore boxScore = game.getBoxScores().get(0);
       		  assertThat(boxScore.getLocation()).isEqualTo(Location.away);
        	  assertThat(boxScore.getTeam().getAbbr()).isEqualTo("SAC");
          }
        });
    }

    @Test
    @Ignore
    public void findPreviousGameDateTeam() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Game game = Game.findPreviousByDateTeam("2012-12-01", "sacramento-kings");
        	  Game previousGame = Game.findById(game.getId(), ProcessingType.online);
        	  
        	  assertThat(previousGame.getSeasonType()).isEqualTo(SeasonType.regular);
        	  assertThat(previousGame.getBoxScores().size()).isEqualTo(2);
       		  BoxScore boxScore = previousGame.getBoxScores().get(0);
       		  assertThat(boxScore.getLocation()).isEqualTo(Location.away);
        	  assertThat(boxScore.getTeam().getAbbr()).isEqualTo("IND");
          }
        });
    }
    
    @Test
    @Ignore
    public void findPreviousGameDateTeamSeason_Valid() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Game> games = Game.findCompletedByDateTeamSeason("2012-12-01", "sacramento-kings", ProcessingType.online);
        	  Game previousGame = Game.findById(games.get(0).getId(), ProcessingType.online);
        	  
        	  assertThat(previousGame.getSeasonType()).isEqualTo(SeasonType.regular);
        	  assertThat(previousGame.getBoxScores().size()).isEqualTo(2);
       		  BoxScore boxScore = previousGame.getBoxScores().get(0);
       		  assertThat(boxScore.getLocation()).isEqualTo(Location.away);
        	  assertThat(boxScore.getTeam().getAbbr()).isEqualTo("IND");
          }
        });
    }
    
    @Test
    public void findPreviousGameDateTeamSeason_Invalid() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Game> game = Game.findCompletedByDateTeamSeason("2012-09-30", "washington-wizards", ProcessingType.online);
        	  assertThat(game.size()).isEqualTo(0);
          }
        });
    }
    
    @Test
    public void findGameIdByDateTeamOnline_Game() {
        running(fakeApplication(), new Runnable() {
            public void run() {
          	  List<Long> games = Game.findIdsByDateTeamSize("2012-10-31", "sacramento-kings", "1", ProcessingType.batch);
          	  assertThat(games.size()).isEqualTo(1);
          	  Game game = Game.findById(games.get(0), ProcessingType.online);
          	  
          	  assertThat(game.getBoxScores().size()).isEqualTo(2);
          	  for (int i = 0; i < game.getBoxScores().size(); i++) {
          		  BoxScore boxScore = game.getBoxScores().get(i);
          		  if (boxScore.getLocation().equals(Location.away)) {
          			  assertThat(boxScore.getTeam().getAbbr()).isEqualTo("SAC");
          		  }
          		  else if (boxScore.getLocation().equals(Location.home)) {
          			  assertThat(boxScore.getTeam().getAbbr()).isEqualTo("CHI");
          		  }
          	  }
            }
        });
    }
    
    @Test
    public void findGameIdByDateTeamOnline_Season() {
        running(fakeApplication(), new Runnable() {
            public void run() {
          	  List<Long> games = Game.findIdsByDateTeamSize("2012-10-30", "sacramento-kings", "0", ProcessingType.online);
          	  assertThat(games.size()).isEqualTo(82);
            }
        });
    }
    
    @Test
    public void findGameIdByDateTeamBatch_Game() {
        running(fakeApplication(), new Runnable() {
            public void run() {
          	  List<Long> games = Game.findIdsByDateTeamSize("2012-10-31", "sacramento-kings", "1", ProcessingType.batch);
          	  assertThat(games.size()).isEqualTo(1);
          	  Game game = Game.findById(games.get(0), ProcessingType.batch);
          	  
          	  assertThat(game.getBoxScores().size()).isEqualTo(2);
          	  for (int i = 0; i < game.getBoxScores().size(); i++) {
          		  BoxScore boxScore = game.getBoxScores().get(i);
          		  if (boxScore.getLocation().equals(Location.away)) {
          			  assertThat(boxScore.getTeam().getAbbr()).isEqualTo("SAC");
          		  }
          		  else if (boxScore.getLocation().equals(Location.home)) {
          			  assertThat(boxScore.getTeam().getAbbr()).isEqualTo("CHI");
          		  }
          	  }
            }
        });
    }
    
    @Test
    public void findGameIdByDateTeamBatch_Season() {
        running(fakeApplication(), new Runnable() {
            public void run() {
          	  List<Long> games = Game.findIdsByDateTeamSize("2012-10-30", "sacramento-kings", "0", ProcessingType.batch);
          	  assertThat(games.size()).isEqualTo(82);
            }
        });
    }
    
    @Test
    public void createGameScheduled() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	Game game = TestMockHelper.getGameScheduled();
		    
		    BoxScore homeBoxScore = TestMockHelper.getBoxScoreHomeScheduled();
		    homeBoxScore.setTeam(Team.findByKey("key", "new-orleans-pelicans", ProcessingType.online));
		    game.addBoxScore(homeBoxScore);
		    
		    BoxScore awayBoxScore = TestMockHelper.getBoxScoreAwayScheduled();
		    awayBoxScore.setTeam(Team.findByKey("key", "sacramento-kings", ProcessingType.online));
		    game.addBoxScore(awayBoxScore);
		    
		    Game.create(game, ProcessingType.online);
		    Long gameId = game.getId();
		    
		    Game createGame = Game.findById(gameId, ProcessingType.online);
		    assertThat(createGame.getSeasonType()).isEqualTo(SeasonType.pre);
		    assertThat(createGame.getBoxScores().size()).isEqualTo(2);
      	  	for (int i = 0; i < createGame.getBoxScores().size(); i++) {
      	  		BoxScore boxScore = createGame.getBoxScores().get(i);
      	  		if (boxScore.getLocation().equals(Location.away)) {
      	  			assertThat(boxScore.getTeam().getAbbr()).isEqualTo("SAC");
      	  		}
      	  		else {
      	  			assertThat(boxScore.getTeam().getAbbr()).isEqualTo("NO");
      	  		}
      	  	}
            Game.delete(gameId, ProcessingType.online);
		  }
		});
	}
    
    @Test
    public void createGameCompleted() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	Game game = TestMockHelper.getGameCompleted();
        	game.setGameOfficials(TestMockHelper.getGameOfficials());
		    
        	Team homeTeam = Team.findByKey("key", "toronto-raptors", ProcessingType.online);
		    BoxScore homeBoxScore = TestMockHelper.getBoxScoreHomeCompleted(TestMockHelper.getBoxScoreHomeScheduled(), TestMockHelper.getPeriodScoresHome());
		    homeBoxScore.setTeam(homeTeam);
		    game.addBoxScore(homeBoxScore);
		    
		    Team awayTeam = Team.findByKey("key", "detroit-pistons", ProcessingType.online);
		    BoxScore awayBoxScore = TestMockHelper.getBoxScoreAwayCompleted(TestMockHelper.getBoxScoreAwayScheduled(), TestMockHelper.getPeriodScoresAway());
		    awayBoxScore.setTeam(awayTeam);
		    game.addBoxScore(awayBoxScore);
		    
		    Game.create(game, ProcessingType.online);
		    Long gameId = game.getId();
		    
		    Game createGame = Game.findById(gameId, ProcessingType.online);
            assertThat(createGame.getSeasonType()).isEqualTo(SeasonType.pre);
            assertThat(createGame.getGameOfficials().size()).isEqualTo(3);
            if (createGame.getGameOfficials().size() > 0) {
            	assertThat(createGame.getGameOfficials().get(0).getOfficial().getLastName()).isEqualTo("Brown");
            	assertThat(createGame.getGameOfficials().get(0).getCounter()).isEqualTo((short)1);
            }
            assertThat(createGame.getBoxScores().size()).isEqualTo(2);
            for (int i = 0; i < createGame.getBoxScores().size(); i++) {
            	BoxScore boxScore = createGame.getBoxScores().get(i);
            	if (boxScore.getLocation().equals(Location.away)) {
            		assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)29);
           			assertThat(boxScore.getPointsPeriod5()).isEqualTo((short)21);
            		assertThat(boxScore.getTeam().getAbbr()).isEqualTo("DET");
            	}
            	else {
            		assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)30);
            		assertThat(boxScore.getPointsPeriod5()).isEqualTo((short)20);
            		assertThat(boxScore.getTeam().getAbbr()).isEqualTo("TOR");
            	}
            }
            Game.delete(gameId, ProcessingType.online);	
		  }
		});
	}
 
   	@Ignore
    @Test
    public void updateGameScheduled() {
        running(fakeApplication(), new Runnable() {
          public void run() {
          	Game scheduleGame = TestMockHelper.getGameScheduled();
		    
          	Team homeTeam = Team.findByKey("key", "new-orleans-pelicans", ProcessingType.online);
  		    BoxScore homeBoxScore = TestMockHelper.getBoxScoreHomeScheduled();
  		    homeBoxScore.setTeam(homeTeam);
  		    scheduleGame.addBoxScore(homeBoxScore);
  		    
  		    Team awayTeam = Team.findByKey("key", "sacramento-kings", ProcessingType.online);
  		    BoxScore awayBoxScore = TestMockHelper.getBoxScoreAwayScheduled();
  		    awayBoxScore.setTeam(awayTeam);
  		    scheduleGame.addBoxScore(awayBoxScore);
  		    
  		    Game.create(scheduleGame, ProcessingType.online);
  		    Long gameId = scheduleGame.getId();

  		    Game completeGame = Game.findById(gameId, ProcessingType.online);
  		    completeGame.setStatus(Status.completed);
  		    completeGame.setGameOfficials(TestMockHelper.getGameOfficials());
  		    
  		    for (int i = 0; i < completeGame.getBoxScores().size(); i++) {
				BoxScore boxScore = completeGame.getBoxScores().get(i);
				if (boxScore.getLocation().equals(Location.away))
					TestMockHelper.getBoxScoreAwayCompleted(boxScore, TestMockHelper.getPeriodScoresAway());
				else
					TestMockHelper.getBoxScoreHomeCompleted(boxScore, TestMockHelper.getPeriodScoresHome());
  		    }

  		    Game.update(completeGame, ProcessingType.online);
  		    
  		    Game updateGame = Game.findById(gameId, ProcessingType.online);
            assertThat(updateGame.getSeasonType()).isEqualTo(SeasonType.pre);
            assertThat(updateGame.getBoxScores().size()).isEqualTo(2);
            if (updateGame.getGameOfficials().size() > 0) {
            	assertThat(updateGame.getGameOfficials().get(0).getOfficial().getLastName()).endsWith("Brown");
            	assertThat(updateGame.getGameOfficials().get(0).getCounter()).isEqualTo((short)1);
            }
            assertThat(updateGame.getBoxScores().size()).isEqualTo(2);
            for (int i = 0; i < updateGame.getBoxScores().size(); i++) {
            	BoxScore boxScore = updateGame.getBoxScores().get(i);
            	if (boxScore.getLocation().equals(Location.away)) {
                    assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)29);
                    assertThat(boxScore.getTeam().getAbbr()).isEqualTo("SAC");
                    assertThat(boxScore.getPointsPeriod5()).isEqualTo((short)21);
            	}
            	else {
                    assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)30);
                    assertThat(boxScore.getTeam().getAbbr()).isEqualTo("NO");
                    assertThat(boxScore.getPointsPeriod5()).isEqualTo((short)20);
            	}
            }
            Game.delete(updateGame.getId(), ProcessingType.online);	
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
            		  "from game g " +
            		  "inner join box_score bs1 on bs1.game_id = g.id " +
            		  "inner join team t1 on t1.id = bs1.team_id " + 
            		  "inner join box_score bs2 on bs2.game_id = g.id and bs2.id <> bs1.id " +
            		  "inner join team t2 on t2.id = bs2.team_id ";
        	  
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
