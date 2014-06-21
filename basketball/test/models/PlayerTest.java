package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import models.Game.ProcessingType;

import org.junit.Test;

import util.DateTimeUtil;

public class PlayerTest {    
    @Test
    public void findAll() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Player> createPlayers = createPlayers();
        	  
        	  List<Player> players = Player.findAll();
        	  assertThat(players.size()).isGreaterThanOrEqualTo(2);
        	  
        	  for (int i = 0; i < createPlayers.size(); i++) {
        		  Player.delete(createPlayers.get(i), ProcessingType.online);
        	  }
          }
        });
    }

    @Test
    public void findByName() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Player> createPlayers = createPlayers();
        	  
        	  List<Player> players = Player.findByName("Jones", "Tim", ProcessingType.online);
        	  assertThat(players.size()).isEqualTo(2);
        	  
        	  for (int i = 0; i < createPlayers.size(); i++) {
        		  Player.delete(createPlayers.get(i), ProcessingType.online);
        	  }
          }
        });
    }
    
    @Test
    public void findByNameBirthDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {        	  
        	  List<Player> createPlayers = createPlayers();
        	  
        	  Player createPlayer = Player.findByNameBirthDate("Jones", "Tim", "1975-01-01", ProcessingType.online);
        	  assertThat(DateTimeUtil.getFindDateShort(createPlayer.getBirthDate())).isEqualTo("1975-01-01");
        	  assertThat(createPlayer.getWeight()).isEqualTo((short)215);
        	  
        	  for (int i = 0; i < createPlayers.size(); i++) {
        		  Player.delete(createPlayers.get(i), ProcessingType.online);
        	  }        	  
          }
        });
    }
    
    @Test
    public void createPlayerOnline() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player player = TestMockHelper.getPlayer("1975-01-01");
        	  Player.create(player, ProcessingType.online);
              
        	  Player createPlayer = Player.findByNameBirthDate("Jones", "Tim", "1975-01-01", ProcessingType.online);
        	  assertThat(DateTimeUtil.getFindDateShort(player.getBirthDate())).isEqualTo("1975-01-01");
              assertThat(createPlayer.getWeight()).isEqualTo((short)215);
              Player.delete(createPlayer, ProcessingType.online);
          }
        });
    }
    
    @Test
    public void createPlayerBatch() {
    	Player player = TestMockHelper.getPlayer("1975-01-01");
        Player.create(player, ProcessingType.batch);
              
        Player createPlayer = Player.findByNameBirthDate("Jones", "Tim", "1975-01-01", ProcessingType.batch);
        assertThat(DateTimeUtil.getFindDateShort(player.getBirthDate())).isEqualTo("1975-01-01");
        assertThat(createPlayer.getWeight()).isEqualTo((short)215);
        Player.delete(createPlayer, ProcessingType.batch);
    }
    
    @Test
    public void updatePlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player player = TestMockHelper.getPlayer("1975-01-01");
        	  Player.create(player, ProcessingType.online);
        	  
        	  Player createPlayer = Player.findByNameBirthDate("Jones", "Tim", "1975-01-01", ProcessingType.online);
        	  createPlayer.setWeight((short)345);
        	  createPlayer.update();
        	  
        	  Player updatePlayer = Player.findByNameBirthDate("Jones", "Tim", "1975-01-01", ProcessingType.online);
        	  assertThat(DateTimeUtil.getFindDateShort(player.getBirthDate())).isEqualTo("1975-01-01");
              assertThat(updatePlayer.getWeight()).isEqualTo((short)345);
              Player.delete(createPlayer, ProcessingType.online);
          }
        });
    }
    
    @Test
    public void updatePlayerValidation() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player createPlayer = null;
       		  try {
            	  Player player = TestMockHelper.getPlayer("1975-01-01");
            	  Player.create(player, ProcessingType.online);
            	  
            	  createPlayer = Player.findByNameBirthDate("Jones", "Tim", "1975-01-01", ProcessingType.online);
				  createPlayer.setFirstName(null);
				  createPlayer.update();
       		  } catch (PersistenceException e) {
       			  assertThat(e.getCause().getMessage().equalsIgnoreCase("Column 'first_name' cannot be null"));
       		  } finally {
       			  Player.delete(createPlayer, ProcessingType.online);
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
    
    public List<Player> createPlayers() {
    	List<Player> players = new ArrayList<Player>();
    			
  	  	Player player1 = TestMockHelper.getPlayer("1975-01-01");
  	  	Player.create(player1, ProcessingType.online);
  	  	players.add(player1);
  	  
  	  	Player player2 = TestMockHelper.getPlayer("1972-05-01");
  	  	Player.create(player2, ProcessingType.online);
  	  	players.add(player2);
  	  	
  	  	return players;
    }
    
}
