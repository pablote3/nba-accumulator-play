package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.List;

import javax.persistence.PersistenceException;

import models.RosterPlayer.Position;

import org.junit.Test;

import com.avaje.ebean.Page;

public class RosterPlayerTest {
    @Test
    public void findRosterPlayersAll() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findAll();
        	  assertThat(rosterPlayers.size()).isEqualTo(1);
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
    public void findRosterPlayersPlayerName() {
        running(fakeApplication(), new Runnable() {
          public void run() {  	  
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findByPlayerName("Webber", "Chris");
        	  assertThat(rosterPlayers.size()).isEqualTo(1);
        	  assertThat(rosterPlayers.get(0).getNumber()).isEqualTo("4");
        	  assertThat(rosterPlayers.get(0).getTeam().getKey()).isEqualTo("sacramento-kings");
          }
        });
    }
    
    @Test
    public void findRosterPlayerDatePlayerName() {
        running(fakeApplication(), new Runnable() {
          public void run() {  	  
        	  RosterPlayer rosterPlayer = RosterPlayer.findByDatePlayerName("2014-03-02", "Webber", "Chris");
        	  assertThat(rosterPlayer.getNumber()).isEqualTo("4");
              assertThat(rosterPlayer.getPlayer().getFirstName()).isEqualTo("Chris");
              assertThat(rosterPlayer.getPlayer().getLastName()).isEqualTo("Webber");
              assertThat(rosterPlayer.getPlayer().getActive()).isFalse();
              assertThat(rosterPlayer.getTeam().getKey()).isEqualTo("sacramento-kings");
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
    public void findRosterPlayersTeamDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findByDateTeam("2014-03-02", "sacramento-kings");
        	  assertThat(rosterPlayers.size()).isEqualTo(1);
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
    
//    @Test
//    public void updateRosterPlayer() {
//        running(fakeApplication(), new Runnable() {
//          public void run() {
//        	  RosterPlayer rosterPlayer = TestMockHelper.getRosterPlayer();
//        	  rosterPlayer.setPlayer(TestMockHelper.getPlayer());
//        	  rosterPlayer.setTeam(TestMockHelper.getTeam());
//        	  
//        	  RosterPlayer.create(rosterPlayer);
//        	  Long rosterPlayerId = rosterPlayer.getId();
//        	  
//        	  RosterPlayer createRosterPlayer = RosterPlayer.findById(rosterPlayerId);
//        	  createRosterPlayer.setPosition(Position.center);
//        	  createRosterPlayer.getPlayer().setActive(true);
//        	  createRosterPlayer.update();
//              
//        	  RosterPlayer updateRosterPlayer = RosterPlayer.findById(rosterPlayerId);
//        	  assertThat(updateRosterPlayer.getPosition()).isEqualTo(Position.center);
//              assertThat(updateRosterPlayer.getPlayer().getActive()).isTrue();
//              RosterPlayer.delete(updateRosterPlayer.getId());
//          }
//        });
//    }
//    
//    @Test
//    public void updateRosterPlayerValidation() {
//        running(fakeApplication(), new Runnable() {
//          public void run() {
//       		  try {
//            	  RosterPlayer rosterPlayer = TestMockHelper.getRosterPlayer();
//            	  rosterPlayer.setPlayer(TestMockHelper.getPlayer());
//            	  rosterPlayer.setTeam(TestMockHelper.getTeam());
//            	  
//            	  RosterPlayer.create(rosterPlayer);
//            	  Long rosterPlayerId = rosterPlayer.getId();
//            	  
//            	  RosterPlayer createRosterPlayer = RosterPlayer.findById(rosterPlayerId);
//       			  createRosterPlayer.getPlayer().setFirstName(null);
//       			  createRosterPlayer.update();
//       		  } catch (PersistenceException e) {
//       			  assertThat(e.getCause().getMessage().equalsIgnoreCase("Column 'first_name' cannot be null"));
//       		  }
//          }
//        });
//    }
//
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
