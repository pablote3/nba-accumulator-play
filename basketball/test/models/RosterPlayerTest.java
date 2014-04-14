package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.List;

import javax.persistence.PersistenceException;

import models.Game.ProcessingType;
import models.RosterPlayer.Position;

import org.junit.Test;

import com.avaje.ebean.Page;

public class RosterPlayerTest {
    @Test
    public void findRosterPlayersAll() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findAll();
        	  assertThat(rosterPlayers.size()).isEqualTo(2);
          }
        });
    }
    
    @Test
    public void findRosterPlayersDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {  	  
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findByDate("2014-03-02");
        	  assertThat(rosterPlayers.size()).isEqualTo(1);
          }
        });
    }
    
	@Test
    public void findRosterPlayersTeam() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findByTeam("sacramento-kings");
        	  assertThat(rosterPlayers.size()).isEqualTo(1);
              assertThat(rosterPlayers.get(0).getPlayer().getFirstName()).isEqualTo("Chris");
              assertThat(rosterPlayers.get(0).getPlayer().getLastName()).isEqualTo("Webber");
              assertThat(rosterPlayers.get(0).getPlayer().getActive()).isFalse();
              assertThat(rosterPlayers.get(0).getTeam().getKey()).isEqualTo("sacramento-kings");
          }
        });
    }
    
    @Test
    public void findRosterPlayerPlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {  	  
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findByPlayer("Webber", "Chris");
        	  assertThat(rosterPlayers.size()).isEqualTo(2);
          }
        });
    }
    
    @Test
    public void findRosterPlayerDatePlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {  	  
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findByDatePlayer("2014-03-02", "Webber", "Chris", ProcessingType.online);
        	  assertThat(rosterPlayers.size()).isEqualTo(1);
          }
        });
    }    

	
	@Test
    public void findRosterDateTeamPlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  RosterPlayer rosterPlayer = RosterPlayer.findByDateTeamPlayer("2014-03-02", "SAC", "Webber", "Chris", ProcessingType.online);
        	  assertThat(rosterPlayer.getNumber()).isEqualTo("4");
              assertThat(rosterPlayer.getPlayer().getFirstName()).isEqualTo("Chris");
              assertThat(rosterPlayer.getPlayer().getLastName()).isEqualTo("Webber");
              assertThat(rosterPlayer.getPlayer().getActive()).isFalse();
              assertThat(rosterPlayer.getTeam().getKey()).isEqualTo("sacramento-kings");
          }
        });
    }
    
    @Test
    public void createRosterPlayerExistingPlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  RosterPlayer rosterPlayer = TestMockHelper.getRosterPlayer();
        	  rosterPlayer.setPlayer(Player.findByName("Webber", "Chris"));
        	  rosterPlayer.setTeam(Team.findByKey("key", "sacramento-kings"));
        	  
        	  RosterPlayer.create(rosterPlayer);
        	  Long rosterPlayerId = rosterPlayer.getId();
        	  
        	  RosterPlayer createRosterPlayer = RosterPlayer.findById(rosterPlayerId);
              assertThat(createRosterPlayer.getNumber()).isEqualTo("4");
              assertThat(createRosterPlayer.getPlayer().getActive()).isFalse();
              assertThat(createRosterPlayer.getPlayer().getFirstName()).isEqualTo("Chris");
              assertThat(rosterPlayer.getTeam().getKey()).isEqualTo("sacramento-kings");
              RosterPlayer.delete(createRosterPlayer.getId());
          }
        });
    }
    
    @Test
    public void updateRosterPlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  RosterPlayer rosterPlayer = TestMockHelper.getRosterPlayer();
        	  rosterPlayer.setPlayer(Player.findByName("Webber", "Chris"));
        	  rosterPlayer.setTeam(Team.findByKey("key", "sacramento-kings"));
        	  
        	  RosterPlayer.create(rosterPlayer);
        	  Long rosterPlayerId = rosterPlayer.getId();
        	  
        	  RosterPlayer createRosterPlayer = RosterPlayer.findById(rosterPlayerId);
        	  createRosterPlayer.setPosition(Position.center);
        	  //createRosterPlayer.getPlayer().setActive(true);
        	  createRosterPlayer.update();
              
        	  RosterPlayer updateRosterPlayer = RosterPlayer.findById(rosterPlayerId);
        	  assertThat(updateRosterPlayer.getPosition()).isEqualTo(Position.center);
              //assertThat(updateRosterPlayer.getPlayer().getActive()).isTrue();
              RosterPlayer.delete(updateRosterPlayer.getId());
          }
        });
    }
    
    @Test
    public void updateRosterPlayerValidation() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Long rosterPlayerId = null;
       		  try {
       			  RosterPlayer rosterPlayer = TestMockHelper.getRosterPlayer();
       			  rosterPlayer.setPlayer(Player.findByName("Webber", "Chris"));
       			  rosterPlayer.setTeam(Team.findByKey("key", "sacramento-kings"));
            	  
            	  RosterPlayer.create(rosterPlayer);
            	  rosterPlayerId = rosterPlayer.getId();
            	  
            	  RosterPlayer createRosterPlayer = RosterPlayer.findById(rosterPlayerId);
       			  createRosterPlayer.getPlayer().setFirstName(null);
       			  createRosterPlayer.update();
       		  } catch (PersistenceException e) {
       			  assertThat(e.getCause().getMessage().equalsIgnoreCase("Column 'first_name' cannot be null"));
       		  } finally {
       			  RosterPlayer.delete(rosterPlayerId);
       		  }
          }
        });
    }

//    @Test
//    public void paginationRosterPlayers() {
//        running(fakeApplication(), new Runnable() {
//           public void run() {
//               Page<RosterPlayer> rosterPlayers = RosterPlayer.page(0, 15, "firstName", "ASC", "");
//               assertThat(rosterPlayers.getTotalRowCount()).isEqualTo(63);
//               assertThat(rosterPlayers.getList().size()).isEqualTo(15);
//           }
//        });
//    }
//    
//    @Test
//    public void pagnationRosterPlayersFilter() {
//        running(fakeApplication(), new Runnable() {
//           public void run() {
//               Page<RosterPlayer> rosterPlayers = RosterPlayer.page(0, 15, "lastName", "ASC", "Webber");
//               assertThat(rosterPlayers.getTotalRowCount()).isEqualTo(2);
//               assertThat(rosterPlayers.getList().size()).isEqualTo(2);
//           }
//        });
//    }
}
