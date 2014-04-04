package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.List;

import javax.persistence.PersistenceException;

import org.junit.Test;

import com.avaje.ebean.Page;

public class PlayerTest {    
    @Test
    public void findPlayersAll() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Player> players = Player.findAll();
        	  assertThat(players.size()).isGreaterThanOrEqualTo(0);
          }
        });
    }
    
	@Test
    public void findPlayersActive() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Player> players = Player.findActive(true);
        	  assertThat(players.size()).isGreaterThanOrEqualTo(0);
          }
        });
    }

//    @Test
//    public void findPlayerByNameBatch() {
//  	  Player player = Player.findByName("Webber", "Chris", ProcessingType.batch);
//        assertThat(player.getActive()).isFalse();
//        assertThat(player.getWeight()).isEqualTo((short)245);
//    }
    
    @Test
    public void createPlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player.create(TestMockHelper.getPlayer());
              
        	  Player player = Player.findByName("Webber", "Chris");
              assertThat(player.getActive()).isFalse();
              assertThat(player.getWeight()).isEqualTo((short)245);
              Player.delete(player.getId());
          }
        });
    }
    
    @Test
    public void updatePlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player.create(TestMockHelper.getPlayer());
              
        	  Player createPlayer = Player.findByName("Webber", "Chris");
        	  createPlayer.setActive(true);
        	  createPlayer.update();
        	  
        	  Player updatePlayer = Player.findByName("Webber", "Chris");
              assertThat(updatePlayer.getActive()).isTrue();
              assertThat(updatePlayer.getWeight()).isEqualTo((short)245);
              Player.delete(updatePlayer.getId());
          }
        });
    }
    
    @Test
    public void updatePlayerValidation() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Long id = null;
       		  try {
            	  Player.create(TestMockHelper.getPlayer());
                  
            	  Player player = Player.findByName("Webber", "Chris");
            	  id = player.getId();
				  player.setFirstName(null);
				  player.update();
       		  } catch (PersistenceException e) {
       			  assertThat(e.getCause().getMessage().equalsIgnoreCase("Column 'first_name' cannot be null"));
       		  } finally {
       			  Player.delete(id);
       		  }
          }
        });
    }

    @Test
    public void paginationPlayers() {
        running(fakeApplication(), new Runnable() {
           public void run() {
               Page<Player> players = Player.page(0, 15, "firstName", "ASC", "");
               assertThat(players.getTotalRowCount()).isEqualTo(63);
               assertThat(players.getList().size()).isEqualTo(15);
           }
        });
    }
    
    @Test
    public void pagnationPlayersFilter() {
        running(fakeApplication(), new Runnable() {
           public void run() {
               Page<Player> players = Player.page(0, 15, "lastName", "ASC", "Crawford");
               assertThat(players.getTotalRowCount()).isEqualTo(2);
               assertThat(players.getList().size()).isEqualTo(2);
           }
        });
    }
}
