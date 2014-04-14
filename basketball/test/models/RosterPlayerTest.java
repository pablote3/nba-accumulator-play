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
    public void findAll() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findAll();
        	  assertThat(rosterPlayers.size()).isEqualTo(3);
          }
        });
    }
    
    @Test
    public void findDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {  	  
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findByDate("2014-03-02");
        	  assertThat(rosterPlayers.size()).isEqualTo(1);
          }
        });
    }
    
	@Test
    public void findTeam() {
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
    public void findPlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {  	  
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findByPlayer("Webber", "Chris");
        	  assertThat(rosterPlayers.size()).isEqualTo(3);
          }
        });
    }
    
    @Test
    public void findDatePlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {  	  
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findByDatePlayer("2014-03-02", "Webber", "Chris", ProcessingType.online);
        	  assertThat(rosterPlayers.size()).isEqualTo(1);
          }
        });
    }    

	
	@Test
    public void findDateTeamPlayer() {
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
    public void createPlayerExistsActivate() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player player = TestMockHelper.getPlayer(false);
        	  Player.create(player);
        	  Long playerId = player.getId();
        	  
        	  player.setActive(true);
        	  Player.update(player, ProcessingType.online);

        	  RosterPlayer rosterPlayer = TestMockHelper.getRosterPlayer("2014-04-04", "9999-12-31");
        	  rosterPlayer.setPlayer(player);
        	  rosterPlayer.setTeam(Team.findByAbbr("GS"));
        	  RosterPlayer.create(rosterPlayer);
        	  
        	  RosterPlayer createRosterPlayer = RosterPlayer.findByDateTeamPlayer("2014-04-05", "GS", player.getLastName(), player.getFirstName(), ProcessingType.batch);
              assertThat(createRosterPlayer.getNumber()).isEqualTo("10");
              assertThat(createRosterPlayer.getPlayer().getActive()).isTrue();
              assertThat(createRosterPlayer.getPlayer().getBirthPlace()).isEqualTo("Brooklyn, New York, USA");
              assertThat(rosterPlayer.getTeam().getKey()).isEqualTo("golden-state-warriors");
              
              RosterPlayer.delete(createRosterPlayer.getId());
              Player.delete(playerId);
          }
        });
    }
    
//    @Test
//    public void updateRosterPlayer() {
//        running(fakeApplication(), new Runnable() {
//          public void run() {
//        	  RosterPlayer rosterPlayer = TestMockHelper.getRosterPlayer();
//        	  rosterPlayer.setPlayer(Player.findByName("Webber", "Chris"));
//        	  rosterPlayer.setTeam(Team.findByKey("key", "sacramento-kings"));
//        	  
//        	  RosterPlayer.create(rosterPlayer);
//        	  Long rosterPlayerId = rosterPlayer.getId();
//        	  
//        	  RosterPlayer createRosterPlayer = RosterPlayer.findById(rosterPlayerId);
//        	  createRosterPlayer.setPosition(Position.center);
//        	  //createRosterPlayer.getPlayer().setActive(true);
//        	  createRosterPlayer.update();
//              
//        	  RosterPlayer updateRosterPlayer = RosterPlayer.findById(rosterPlayerId);
//        	  assertThat(updateRosterPlayer.getPosition()).isEqualTo(Position.center);
//              //assertThat(updateRosterPlayer.getPlayer().getActive()).isTrue();
//              RosterPlayer.delete(updateRosterPlayer.getId());
//          }
//        });
//    }
    
    @Test
    public void updateRosterPlayerValidation() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Long rosterPlayerId = null;
       		  try {
       			  RosterPlayer rosterPlayer = TestMockHelper.getRosterPlayer("2014-04-04", "9999-12-31");
       			  rosterPlayer.setPlayer(Player.findByName("Webber", "Chris", ProcessingType.online));
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
