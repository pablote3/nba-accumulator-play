package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.List;

import javax.persistence.PersistenceException;

import models.Game.ProcessingType;

import org.junit.Test;

import com.avaje.ebean.Page;

public class PlayerTest {    
    @Test
    public void findAll() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Player> players = Player.findAll();
        	  assertThat(players.size()).isGreaterThanOrEqualTo(0);
          }
        });
    }
    
	@Test
    public void findActive() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Player> players = Player.findActive(true);
        	  assertThat(players.size()).isGreaterThanOrEqualTo(0);
          }
        });
    }

    @Test
    public void findByName() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player player = Player.findByName("Webber", "Chris", ProcessingType.online);
        	  assertThat(player.getActive()).isFalse();
        	  assertThat(player.getWeight()).isEqualTo((short)245);
          }
        });
    }
    
    @Test
    public void createPlayerOnline() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player player = TestMockHelper.getPlayer(true);
        	  Player.create(player, ProcessingType.online);
        	  Long playerId = player.getId();
              
        	  Player createPlayer = Player.findById(playerId);
              assertThat(createPlayer.getActive()).isTrue();
              assertThat(createPlayer.getWeight()).isEqualTo((short)215);
              Player.delete(playerId);
          }
        });
    }
    
    @Test
    public void createPlayerBatch() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player player = TestMockHelper.getPlayer(true);
        	  Player.create(player, ProcessingType.batch);
        	  Long playerId = player.getId();
              
        	  Player createPlayer = Player.findById(playerId);
              assertThat(createPlayer.getActive()).isTrue();
              assertThat(createPlayer.getWeight()).isEqualTo((short)215);
              Player.delete(playerId);
          }
        });
    }
    
    @Test
    public void updatePlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player player = Player.findByName("Webber", "Chris", ProcessingType.online);
        	  player.setActive(true);
        	  player.update();
        	  
        	  Player updatePlayer = Player.findByName("Webber", "Chris", ProcessingType.online);
              assertThat(updatePlayer.getActive()).isTrue();
              assertThat(updatePlayer.getWeight()).isEqualTo((short)245);

        	  player.setActive(false);
        	  player.update();
          }
        });
    }
    
    @Test
    public void updatePlayerValidation() {
        running(fakeApplication(), new Runnable() {
          public void run() {
       		  try {
            	  Player player = Player.findByName("Webber", "Chris", ProcessingType.online);
				  player.setFirstName(null);
				  player.update();
       		  } catch (PersistenceException e) {
       			  assertThat(e.getCause().getMessage().equalsIgnoreCase("Column 'first_name' cannot be null"));
       		  }
          }
        });
    }

//    @Test
//    public void paginationPlayers() {
//        running(fakeApplication(), new Runnable() {
//           public void run() {
//               Page<Player> players = Player.page(0, 15, "firstName", "ASC", "");
//               assertThat(players.getTotalRowCount()).isEqualTo(63);
//               assertThat(players.getList().size()).isEqualTo(15);
//           }
//        });
//    }
//    
//    @Test
//    public void pagnationPlayersFilter() {
//        running(fakeApplication(), new Runnable() {
//           public void run() {
//               Page<Player> players = Player.page(0, 15, "lastName", "ASC", "Crawford");
//               assertThat(players.getTotalRowCount()).isEqualTo(2);
//               assertThat(players.getList().size()).isEqualTo(2);
//           }
//        });
//    }
}
