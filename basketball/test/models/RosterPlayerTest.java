package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.PersistenceException;

import models.Game.ProcessingType;

import org.junit.Test;

public class RosterPlayerTest {
    @Test
    public void findAll() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findAll();
        	  assertThat(rosterPlayers.size()).isEqualTo(6);
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
        	  assertThat(rosterPlayers.size()).isEqualTo(3);
              assertThat(rosterPlayers.get(0).getPlayer().getFirstName()).isEqualTo("Tim");
              assertThat(rosterPlayers.get(0).getPlayer().getLastName()).isEqualTo("Jones");
              assertThat(rosterPlayers.get(0).getPlayer().getBirthDateShort()).isEqualTo("1975-01-01");
              assertThat(rosterPlayers.get(0).getTeam().getKey()).isEqualTo("sacramento-kings");
          }
        });
    }
    
    @Test
    public void findDatePlayerName() {
        running(fakeApplication(), new Runnable() {
          public void run() {  	  
        	  List<RosterPlayer> rosterPlayersMin = RosterPlayer.findByDatePlayerName("2014-03-01", "Jones", "Tim", ProcessingType.online);
        	  assertThat(rosterPlayersMin.size()).isEqualTo(1);
        	  
        	  List<RosterPlayer> rosterPlayersMax = RosterPlayer.findByDatePlayerName("2014-06-30", "Jones", "Tim", ProcessingType.online);
        	  assertThat(rosterPlayersMax.size()).isEqualTo(1);
          }
        });
    }    

	
	@Test
    public void findDateTeamPlayerName_NotNull() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  RosterPlayer rosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2014-02-02", "Jones", "Tim", "SAC", ProcessingType.online);
              assertThat(rosterPlayer.getPlayer().getFirstName()).isEqualTo("Tim");
              assertThat(rosterPlayer.getPlayer().getLastName()).isEqualTo("Jones");
              assertThat(rosterPlayer.getPlayer().getBirthDateShort()).isEqualTo("1975-01-01");
              assertThat(rosterPlayer.getTeam().getKey()).isEqualTo("sacramento-kings");
          }
        });
    }
	
	@Test
    public void findDateTeamPlayerName_Null() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  RosterPlayer rosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2014-02-02", "Jones", "Tim", "GS", ProcessingType.online);
              assertThat(rosterPlayer).isNull();
          }
        });
    }
	
//    @Test
//    public void findLatestPlayerNameBirthDateBySeason() {
//        running(fakeApplication(), new Runnable() {
//          public void run() {  	  
//        	  RosterPlayer rosterPlayer = RosterPlayer.findLatestPlayerNameBirthDateBySeason("Jones", "Tim");
//        	  assertThat(rosterPlayer.getFromDate()).isEqualTo("2014-01-01");
//          }
//        });
//    }
    
    @Test
    public void createRosterPlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player player = TestMockHelper.getPlayer(false);
        	  Player.create(player, ProcessingType.online);

        	  RosterPlayer rosterPlayer = TestMockHelper.getRosterPlayer("2014-04-04", "9999-12-31");
        	  rosterPlayer.setPlayer(player);
        	  rosterPlayer.setTeam(Team.findByAbbr("GS", ProcessingType.online));
        	  RosterPlayer.create(rosterPlayer, ProcessingType.online);
        	  
        	  RosterPlayer createRosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2014-04-04", player.getLastName(), player.getFirstName(), "GS", ProcessingType.online);
              assertThat(createRosterPlayer.getNumber()).isEqualTo("10");
              assertThat(createRosterPlayer.getPlayer().getBirthPlace()).isEqualTo("Brooklyn, New York, USA");
              assertThat(createRosterPlayer.getTeam().getKey()).isEqualTo("golden-state-warriors");
              
              RosterPlayer.delete(createRosterPlayer.getId());
              Player.delete(player.getId());
          }
        });
    }
    
    @Test
    public void updateRosterPlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player player = TestMockHelper.getPlayer(true);
        	  Player.create(player, ProcessingType.online);
        	  
        	  RosterPlayer rosterPlayer = TestMockHelper.getRosterPlayer("2014-04-04", "9999-12-31");
        	  rosterPlayer.setPlayer(player);
        	  rosterPlayer.setTeam(Team.findByAbbr("GS", ProcessingType.online));
        	  RosterPlayer.create(rosterPlayer, ProcessingType.online);
        	  
        	  RosterPlayer createRosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2014-04-04", player.getLastName(), player.getFirstName(), "GS", ProcessingType.online);
        	  
        	  Date toDate = null;
        	  try {
        	  	  toDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2014-04-15");
        	  } catch (ParseException e) {
        	  	  e.printStackTrace();
        	  }
        	  createRosterPlayer.setToDate(toDate);
        	  RosterPlayer.update(createRosterPlayer, ProcessingType.online);
        	  
        	  RosterPlayer expiredRosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2014-04-16", player.getLastName(), player.getFirstName(), "GS", ProcessingType.online);
        	  assertThat(expiredRosterPlayer).isNull();
        	  
        	  RosterPlayer updateRosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2014-04-15", player.getLastName(), player.getFirstName(), "GS", ProcessingType.online);
        	  
              assertThat(updateRosterPlayer.getNumber()).isEqualTo("10");
              assertThat(updateRosterPlayer.getPlayer().getBirthPlace()).isEqualTo("Brooklyn, New York, USA");
              assertThat(updateRosterPlayer.getTeam().getKey()).isEqualTo("golden-state-warriors");
              
              RosterPlayer.delete(updateRosterPlayer.getId());
              Player.delete(player.getId());
          }
        });
    }
    
    @Test
    public void updateRosterPlayerValidation() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Long playerId = null;
        	  Long rosterPlayerId = null;
       		  try {
            	  Player player = TestMockHelper.getPlayer(true);
            	  Player.create(player, ProcessingType.online);
            	  playerId = player.getId();
            	  
            	  RosterPlayer rosterPlayer = TestMockHelper.getRosterPlayer("2014-04-04", "9999-12-31");
            	  rosterPlayer.setPlayer(player);
            	  rosterPlayer.setTeam(Team.findByAbbr("GS", ProcessingType.online));
            	  RosterPlayer.create(rosterPlayer, ProcessingType.online);
            	  rosterPlayerId = rosterPlayer.getId();
            	  
            	  RosterPlayer createRosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2014-04-04", player.getLastName(), player.getFirstName(), "GS", ProcessingType.online);

            	  createRosterPlayer.getPlayer().setFirstName(null);
       			  createRosterPlayer.update();
       		  } catch (PersistenceException e) {
       			  assertThat(e.getCause().getMessage().equalsIgnoreCase("Column 'first_name' cannot be null"));
       		  } finally {
       			  RosterPlayer.delete(rosterPlayerId);
       			  Player.delete(playerId);
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
