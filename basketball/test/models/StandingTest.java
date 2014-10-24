package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import models.Game.ProcessingType;

import org.junit.Ignore;
import org.junit.Test;

public class StandingTest {    
    @Test
    @Ignore
    public void findOpponentOppenentWinPercentageSeason() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Float winPercentage = Standing.findOpponentOppenentWinPercentageSeason("2012-11-09", "atlanta-hawks", ProcessingType.online);
              assertThat(winPercentage).isEqualTo((float)0.462);
          }
        });
    }

}
