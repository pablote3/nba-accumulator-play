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
		Standing.create(TestMockHelper.getStanding("chicago-bulls", "2010-10-31"), ProcessingType.online);
		Standing.create(TestMockHelper.getStanding("san-antonio-spurs", "2010-10-31"), ProcessingType.online);
		Standing.create(TestMockHelper.getStanding("utah-jazz", "2010-10-31"), ProcessingType.online);
		Standing.create(TestMockHelper.getStanding("san-antonio-spurs", "2010-11-03"), ProcessingType.online);
		Standing.create(TestMockHelper.getStanding("san-antonio-spurs", "2010-11-05"), ProcessingType.online);
	}
	
	@Test
    public void findByTeam() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Standing> standings = Standing.findByTeam("san-antonio-spurs", ProcessingType.online);
        	  assertThat(standings.size()).isEqualTo(3);
          }
        });
    }
	
	@Test
    public void findByDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Standing> standings = Standing.findByDate("2010-10-31", ProcessingType.batch);
        	  assertThat(standings.size()).isEqualTo(3);
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
              Standing.create(TestMockHelper.getStanding("seattle-supersonics", "2010-12-31"), ProcessingType.online);
              Standing createStanding = Standing.findByDateTeam("2010-12-31", "seattle-supersonics", ProcessingType.online);
              assertThat(createStanding.getGamesBack()).isEqualTo((float)1.5);
              assertThat(createStanding.getGamesWon()).isEqualTo((short)95);
              Standing.delete(createStanding.getId(), ProcessingType.online);
          }
        });
    }
    
    @Test
    public void updateStanding() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Standing standing = Standing.findByDateTeam("chicago-bulls", "2010-10-31", ProcessingType.online);
              standing.setGamesLost((short)15);
              standing.update();
              
              Standing updateStanding = Standing.findByDateTeam("chicago-bulls", "2010-10-31", ProcessingType.online);
              assertThat(updateStanding.getGamesLost()).isEqualTo((short)15);
              updateStanding.setGamesLost((short)95);
              updateStanding.update();
          }
        });
    }

	@After public void cleanup() {
		Standing.delete(Standing.findByDateTeam("chicago-bulls", "2010-10-31", ProcessingType.online).getId(), ProcessingType.online);
		Standing.delete(Standing.findByDateTeam("san-antonio-spurs", "2010-10-31", ProcessingType.online).getId(), ProcessingType.online);
		Standing.delete(Standing.findByDateTeam("utah-jazz", "2010-10-31", ProcessingType.online).getId(), ProcessingType.online);
		Standing.delete(Standing.findByDateTeam("san-antonio-spurs", "2010-11-03", ProcessingType.online).getId(), ProcessingType.online);
		Standing.delete(Standing.findByDateTeam("san-antonio-spurs", "2010-11-05", ProcessingType.online).getId(), ProcessingType.online);
	}
}
