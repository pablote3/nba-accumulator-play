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
    public void findByName() {
        running(fakeApplication(), new Runnable() {
          public void run() {        	  
        	  Player player = TestMockHelper.getPlayer(true);
        	  Player.create(player, ProcessingType.online);
        	  Long playerId = player.getId();
        	  
        	  Player createPlayer = Player.findByName("Mullin", "Chris", ProcessingType.online);
        	  assertThat(createPlayer.getBirthDateShort()).isEqualTo("1963-07-30");
        	  assertThat(createPlayer.getWeight()).isEqualTo((short)215);
        	  Player.delete(playerId);
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
        	  assertThat(player.getBirthDateShort()).isEqualTo("1963-07-30");
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
        	  assertThat(player.getBirthDateShort()).isEqualTo("1963-07-30");
              assertThat(createPlayer.getWeight()).isEqualTo((short)215);
              Player.delete(playerId);
          }
        });
    }
    
    @Test
    public void updatePlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player player = TestMockHelper.getPlayer(true);
        	  Player.create(player, ProcessingType.online);
        	  Long playerId = player.getId();
        	  
        	  Player createPlayer = Player.findByName("Mullin", "Chris", ProcessingType.online);
        	  createPlayer.setWeight((short)345);
        	  createPlayer.update();
        	  
        	  Player updatePlayer = Player.findByName("Mullin", "Chris", ProcessingType.online);
        	  assertThat(player.getBirthDateShort()).isEqualTo("1963-07-30");
              assertThat(updatePlayer.getWeight()).isEqualTo((short)345);
              Player.delete(playerId);
          }
        });
    }
    
    @Test
    public void updatePlayerValidation() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Long playerId = null;
       		  try {
            	  Player player = TestMockHelper.getPlayer(true);
            	  Player.create(player, ProcessingType.online);
            	  playerId = player.getId();
            	  
            	  Player createPlayer = Player.findByName("Mullin", "Chris", ProcessingType.online);
				  createPlayer.setFirstName(null);
				  createPlayer.update();
       		  } catch (PersistenceException e) {
       			  assertThat(e.getCause().getMessage().equalsIgnoreCase("Column 'first_name' cannot be null"));
       		  } finally {
       			Player.delete(playerId);
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
