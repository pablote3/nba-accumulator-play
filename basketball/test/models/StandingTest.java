package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.List;

import models.Game.ProcessingType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StandingTest {
	@Before public void initialize() {
      running(fakeApplication(), new Runnable() {
    	  public void run() {
    		  Standing.create(TestMockHelper.getStanding("2010-10-31", Team.findByTeamKey("utah-jazz", ProcessingType.online)), ProcessingType.online);
    		  Standing.create(TestMockHelper.getStanding("2010-10-31", Team.findByTeamKey("san-antonio-spurs", ProcessingType.online)), ProcessingType.online);
    		  Standing.create(TestMockHelper.getStanding("2010-11-01", Team.findByTeamKey("utah-jazz", ProcessingType.online)), ProcessingType.online);
    		  Standing.create(TestMockHelper.getStanding("2010-11-03", Team.findByTeamKey("san-antonio-spurs", ProcessingType.online)), ProcessingType.online);
    		  Standing.create(TestMockHelper.getStanding("2010-11-05", Team.findByTeamKey("san-antonio-spurs", ProcessingType.online)), ProcessingType.online);
          }
       });
	}

	@Test
    public void findByDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Standing> standings = Standing.findByDate("2010-10-31", ProcessingType.batch);
        	  assertThat(standings.size()).isEqualTo(2);
          }
        });
    }
	
	@Test
    public void findByDate_Null() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Standing> standings = Standing.findByDate("2010-10-20", ProcessingType.batch);
        	  assertThat(standings.isEmpty());
          }
        });
    }

	@Test
    public void findByDateTeam() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Standing standing = Standing.findByDateTeam("2010-10-31", "san-antonio-spurs", ProcessingType.batch);
        	  assertThat(standing.getOrdinalRank()).isEqualTo("2nd");
          }
        });
    }
	
    @Test
    public void createStanding() {
        running(fakeApplication(), new Runnable() {
          public void run() {
              Standing.create(TestMockHelper.getStanding("2010-12-31", Team.findByTeamKey("portland-trail-blazers", ProcessingType.online)), ProcessingType.online);
              Standing createStanding = Standing.findByDateTeam("2010-12-31", "portland-trail-blazers", ProcessingType.online);
              assertThat(createStanding.getGamesBack()).isEqualTo((float)1.5);
              assertThat(createStanding.getGamesWon()).isEqualTo((short)95);
              Standing.delete(createStanding, ProcessingType.online);
          }
        });
    }
    
    @Test
    public void updateStanding() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Standing standing = Standing.findByDateTeam("2010-10-31", "utah-jazz", ProcessingType.online);
        	  assertThat(standing.getGamesLost()).isEqualTo((short)102);
              standing.setGamesLost((short)15);
              standing.update();
              
              Standing updateStanding = Standing.findByDateTeam("2010-10-31", "utah-jazz", ProcessingType.online);
              assertThat(updateStanding.getGamesLost()).isEqualTo((short)15);
              updateStanding.setGamesLost((short)102);
              updateStanding.update();
          }
        });
    }

	@After public void cleanup() {
	  running(fakeApplication(), new Runnable() {
	      public void run() {
	    	  Standing.delete(Standing.findByDateTeam("2010-10-31", "utah-jazz", ProcessingType.online), ProcessingType.online);
	    	  Standing.delete(Standing.findByDateTeam("2010-10-31", "san-antonio-spurs", ProcessingType.online), ProcessingType.online);
	    	  Standing.delete(Standing.findByDateTeam("2010-11-01", "utah-jazz", ProcessingType.online), ProcessingType.online);
	    	  Standing.delete(Standing.findByDateTeam("2010-11-03", "san-antonio-spurs", ProcessingType.online), ProcessingType.online);
	    	  Standing.delete(Standing.findByDateTeam("2010-11-05", "san-antonio-spurs", ProcessingType.online), ProcessingType.online);
          }
       });
	}
}
