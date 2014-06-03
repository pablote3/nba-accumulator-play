package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.PersistenceException;

import models.Game.ProcessingType;

import org.junit.Test;

import util.DateTime;

public class RosterPlayerTest {
    @Test
    public void findAll() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> createRosterPlayers = createRosterPlayers();
        	  Set<Player> createPlayers = new HashSet<Player>();
        	  Player deletePlayer;
        	  
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findAll();
        	  assertThat(rosterPlayers.size()).isGreaterThanOrEqualTo(6);
        	  
        	  for (int i = 0; i < createRosterPlayers.size(); i++) {
        		  createPlayers.add(createRosterPlayers.get(i).getPlayer());
        		  RosterPlayer.delete(createRosterPlayers.get(i), ProcessingType.online);
        	  } 
        	  for (Iterator<Player> it = createPlayers.iterator(); it.hasNext(); ) {
        		  deletePlayer = it.next();
        		  Player.delete(deletePlayer, ProcessingType.online);
        	  }
          }
        });
    }
    
    @Test
    public void findDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> createRosterPlayers = createRosterPlayers();
        	  Set<Player> createPlayers = new HashSet<Player>();
        	  Player deletePlayer;
        	  
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findByDate("2000-03-02");
        	  assertThat(rosterPlayers.size()).isEqualTo(1);
        	  
        	  for (int i = 0; i < createRosterPlayers.size(); i++) {
        		  createPlayers.add(createRosterPlayers.get(i).getPlayer());
        		  RosterPlayer.delete(createRosterPlayers.get(i), ProcessingType.online);
        	  } 
        	  for (Iterator<Player> it = createPlayers.iterator(); it.hasNext(); ) {
        		  deletePlayer = it.next();
        		  Player.delete(deletePlayer, ProcessingType.online);
        	  }
          }
        });
    }
	
	@Test
    public void findDateTeam() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> createRosterPlayers = createRosterPlayers();
        	  Set<Player> createPlayers = new HashSet<Player>();
        	  Player deletePlayer;        	  
        	  
        	  List<RosterPlayer> rosterPlayers = RosterPlayer.findByDateTeam("2000-02-15", "sacramento-kings", ProcessingType.online);
        	  assertThat(rosterPlayers.size()).isEqualTo(1);
              assertThat(rosterPlayers.get(0).getPlayer().getFirstName()).isEqualTo("Tim");
              assertThat(rosterPlayers.get(0).getPlayer().getLastName()).isEqualTo("Jones");
              assertThat(DateTime.getFindDateShort(rosterPlayers.get(0).getPlayer().getBirthDate())).isEqualTo("1975-01-01");
              assertThat(rosterPlayers.get(0).getTeam().getKey()).isEqualTo("sacramento-kings");
              
        	  for (int i = 0; i < createRosterPlayers.size(); i++) {
        		  createPlayers.add(createRosterPlayers.get(i).getPlayer());
        		  RosterPlayer.delete(createRosterPlayers.get(i), ProcessingType.online);
        	  } 
        	  for (Iterator<Player> it = createPlayers.iterator(); it.hasNext(); ) {
        		  deletePlayer = it.next();
        		  Player.delete(deletePlayer, ProcessingType.online);
        	  }
          }
        });
    }
    
    @Test
    public void findDatePlayerName() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> createRosterPlayers = createRosterPlayers();
        	  Set<Player> createPlayers = new HashSet<Player>();
        	  Player deletePlayer;      
        	  
        	  List<RosterPlayer> rosterPlayersMin = RosterPlayer.findByDatePlayerName("2000-03-01", "Jones", "Tim", ProcessingType.online);
        	  assertThat(rosterPlayersMin.size()).isEqualTo(1);
        	  
        	  List<RosterPlayer> rosterPlayersMax = RosterPlayer.findByDatePlayerName("2000-06-30", "Jones", "Tim", ProcessingType.online);
        	  assertThat(rosterPlayersMax.size()).isEqualTo(1);
        	  
        	  for (int i = 0; i < createRosterPlayers.size(); i++) {
        		  createPlayers.add(createRosterPlayers.get(i).getPlayer());
        		  RosterPlayer.delete(createRosterPlayers.get(i), ProcessingType.online);
        	  } 
        	  for (Iterator<Player> it = createPlayers.iterator(); it.hasNext(); ) {
        		  deletePlayer = it.next();
        		  Player.delete(deletePlayer, ProcessingType.online);
        	  }
          }
        });
    }    

	@Test
    public void findDateTeamPlayerName_NotNull() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> createRosterPlayers = createRosterPlayers();
        	  Set<Player> createPlayers = new HashSet<Player>();
        	  Player deletePlayer;    
        	  
        	  RosterPlayer rosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2000-02-02", "Jones", "Tim", "sacramento-kings", ProcessingType.online);
              assertThat(rosterPlayer.getPlayer().getFirstName()).isEqualTo("Tim");
              assertThat(rosterPlayer.getPlayer().getLastName()).isEqualTo("Jones");
              assertThat(DateTime.getFindDateShort(rosterPlayer.getPlayer().getBirthDate())).isEqualTo("1975-01-01");
              assertThat(rosterPlayer.getTeam().getKey()).isEqualTo("sacramento-kings");
              
        	  for (int i = 0; i < createRosterPlayers.size(); i++) {
        		  createPlayers.add(createRosterPlayers.get(i).getPlayer());
        		  RosterPlayer.delete(createRosterPlayers.get(i), ProcessingType.online);
        	  } 
        	  for (Iterator<Player> it = createPlayers.iterator(); it.hasNext(); ) {
        		  deletePlayer = it.next();
        		  Player.delete(deletePlayer, ProcessingType.online);
        	  }
          }
        });
    }
	
	@Test
    public void findDateTeamPlayerName_Null() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> createRosterPlayers = createRosterPlayers();
        	  Set<Player> createPlayers = new HashSet<Player>();
        	  Player deletePlayer;  
        	  
        	  RosterPlayer rosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2000-02-02", "Jones", "Tim", "golden-state-warriors", ProcessingType.online);
              assertThat(rosterPlayer).isNull();
              
        	  for (int i = 0; i < createRosterPlayers.size(); i++) {
        		  createPlayers.add(createRosterPlayers.get(i).getPlayer());
        		  RosterPlayer.delete(createRosterPlayers.get(i), ProcessingType.online);
        	  } 
        	  for (Iterator<Player> it = createPlayers.iterator(); it.hasNext(); ) {
        		  deletePlayer = it.next();
        		  Player.delete(deletePlayer, ProcessingType.online);
        	  }
          }
        });
    }
	
    @Test
    public void findLatestPlayerNameBirthDateSeason_BirthDate1() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> createRosterPlayers = createRosterPlayers();
        	  Set<Player> createPlayers = new HashSet<Player>();
        	  Player deletePlayer;  
        	  
        	  RosterPlayer rosterPlayer = RosterPlayer.findLatestByPlayerNameBirthDateSeason("2000-02-02", "Jones", "Tim", "1975-01-01", ProcessingType.online);
        	  assertThat(DateTime.getFindDateShort(rosterPlayer.getFromDate())).isEqualTo("2000-02-01");
        	  assertThat(rosterPlayer.getTeam().getAbbr()).isEqualTo("SAC");
        	  
        	  for (int i = 0; i < createRosterPlayers.size(); i++) {
        		  createPlayers.add(createRosterPlayers.get(i).getPlayer());
        		  RosterPlayer.delete(createRosterPlayers.get(i), ProcessingType.online);
        	  } 
        	  for (Iterator<Player> it = createPlayers.iterator(); it.hasNext(); ) {
        		  deletePlayer = it.next();
        		  Player.delete(deletePlayer, ProcessingType.online);
        	  }
          }
        });
    }
    
    @Test
    public void findLatestPlayerNameBirthDateSeason_BirthDate2() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> createRosterPlayers = createRosterPlayers();
        	  Set<Player> createPlayers = new HashSet<Player>();
        	  Player deletePlayer;  
        	  
        	  RosterPlayer rosterPlayer = RosterPlayer.findLatestByPlayerNameBirthDateSeason("2000-02-02", "Jones", "Tim", "1972-05-01", ProcessingType.online);
        	  assertThat(DateTime.getFindDateShort(rosterPlayer.getFromDate())).isEqualTo("1999-11-01");
        	  assertThat(rosterPlayer.getTeam().getAbbr()).isEqualTo("LAL");
        	  
        	  for (int i = 0; i < createRosterPlayers.size(); i++) {
        		  createPlayers.add(createRosterPlayers.get(i).getPlayer());
        		  RosterPlayer.delete(createRosterPlayers.get(i), ProcessingType.online);
        	  } 
        	  for (Iterator<Player> it = createPlayers.iterator(); it.hasNext(); ) {
        		  deletePlayer = it.next();
        		  Player.delete(deletePlayer, ProcessingType.online);
        	  }
          }
        });
    }
    
    @Test
    public void findLatestPlayerNameBirthDateSeason_PreviousSeason() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> createRosterPlayers = createRosterPlayers();
        	  Set<Player> createPlayers = new HashSet<Player>();
        	  Player deletePlayer;  
        	  
        	  RosterPlayer rosterPlayer = RosterPlayer.findLatestByPlayerNameBirthDateSeason("1998-11-12", "Jones", "Tim", "1972-05-01", ProcessingType.online);
        	  assertThat(DateTime.getFindDateShort(rosterPlayer.getFromDate())).isEqualTo("1998-10-30");
        	  assertThat(rosterPlayer.getTeam().getAbbr()).isEqualTo("SAC");
        	  
        	  for (int i = 0; i < createRosterPlayers.size(); i++) {
        		  createPlayers.add(createRosterPlayers.get(i).getPlayer());
        		  RosterPlayer.delete(createRosterPlayers.get(i), ProcessingType.online);
        	  } 
        	  for (Iterator<Player> it = createPlayers.iterator(); it.hasNext(); ) {
        		  deletePlayer = it.next();
        		  Player.delete(deletePlayer, ProcessingType.online);
        	  }
          }
        });
    }
    
    @Test
    public void findLatestPlayerNameBirthDateSeason_NullPointer_GameDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> createRosterPlayers = createRosterPlayers();
        	  Set<Player> createPlayers = new HashSet<Player>();
        	  Player deletePlayer;
        	  
        	  RosterPlayer rosterPlayer = RosterPlayer.findLatestByPlayerNameBirthDateSeason("2000-12-31", "Jones", "Tim", "1975-01-01", ProcessingType.online);
        	  assertThat(rosterPlayer).isNull();
        	  
        	  for (int i = 0; i < createRosterPlayers.size(); i++) {
        		  createPlayers.add(createRosterPlayers.get(i).getPlayer());
        		  RosterPlayer.delete(createRosterPlayers.get(i), ProcessingType.online);
        	  } 
        	  for (Iterator<Player> it = createPlayers.iterator(); it.hasNext(); ) {
        		  deletePlayer = it.next();
        		  Player.delete(deletePlayer, ProcessingType.online);
        	  }
          }
        });
    }
    
    @Test
    public void findLatestPlayerNameBirthDateSeason_NullPointer_LastName() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> createRosterPlayers = createRosterPlayers();
        	  Set<Player> createPlayers = new HashSet<Player>();
        	  Player deletePlayer;
        	  
        	  RosterPlayer rosterPlayer = RosterPlayer.findLatestByPlayerNameBirthDateSeason("2000-02-02", "Bones", "Tim", "1975-01-01", ProcessingType.online);
        	  assertThat(rosterPlayer).isNull();
        	  
        	  for (int i = 0; i < createRosterPlayers.size(); i++) {
        		  createPlayers.add(createRosterPlayers.get(i).getPlayer());
        		  RosterPlayer.delete(createRosterPlayers.get(i), ProcessingType.online);
        	  } 
        	  for (Iterator<Player> it = createPlayers.iterator(); it.hasNext(); ) {
        		  deletePlayer = it.next();
        		  Player.delete(deletePlayer, ProcessingType.online);
        	  }
          }
        });
    }
    
    @Test
    public void findLatestPlayerNameBirthDateSeason_NullPointer_FirstName() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> createRosterPlayers = createRosterPlayers();
        	  Set<Player> createPlayers = new HashSet<Player>();
        	  Player deletePlayer;
        	  
        	  RosterPlayer rosterPlayer = RosterPlayer.findLatestByPlayerNameBirthDateSeason("2000-02-02", "Jones", "Slim", "1975-01-01", ProcessingType.online);
        	  assertThat(rosterPlayer).isNull();
        	  
        	  for (int i = 0; i < createRosterPlayers.size(); i++) {
        		  createPlayers.add(createRosterPlayers.get(i).getPlayer());
        		  RosterPlayer.delete(createRosterPlayers.get(i), ProcessingType.online);
        	  } 
        	  for (Iterator<Player> it = createPlayers.iterator(); it.hasNext(); ) {
        		  deletePlayer = it.next();
        		  Player.delete(deletePlayer, ProcessingType.online);
        	  }
          }
        });
    }
    
    @Test
    public void findLatestPlayerNameBirthDateSeason_NullPointer_BirthDate() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<RosterPlayer> createRosterPlayers = createRosterPlayers();
        	  Set<Player> createPlayers = new HashSet<Player>();
        	  Player deletePlayer;
        	  
        	  RosterPlayer rosterPlayer = RosterPlayer.findLatestByPlayerNameBirthDateSeason("2000-02-02", "Jones", "Tim", "1975-05-29", ProcessingType.online);
        	  assertThat(rosterPlayer).isNull();
        	  
        	  for (int i = 0; i < createRosterPlayers.size(); i++) {
        		  createPlayers.add(createRosterPlayers.get(i).getPlayer());
        		  RosterPlayer.delete(createRosterPlayers.get(i), ProcessingType.online);
        	  } 
        	  for (Iterator<Player> it = createPlayers.iterator(); it.hasNext(); ) {
        		  deletePlayer = it.next();
        		  Player.delete(deletePlayer, ProcessingType.online);
        	  }
          }
        });
    }
    
    @Test
    public void createRosterPlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player player = TestMockHelper.getPlayer("1975-01-01");
        	  Player.create(player, ProcessingType.online);

        	  Team team = Team.findByAbbr("GS", ProcessingType.online);
        	  RosterPlayer rosterPlayer = TestMockHelper.getRosterPlayer(player, team, "2000-04-04", "9999-12-31");
        	  RosterPlayer.create(rosterPlayer, ProcessingType.online);
        	  
        	  RosterPlayer createRosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2000-04-04", player.getLastName(), player.getFirstName(), "golden-state-warriors", ProcessingType.online);
              assertThat(createRosterPlayer.getNumber()).isEqualTo("10");
              assertThat(createRosterPlayer.getPlayer().getBirthPlace()).isEqualTo("Brooklyn, New York, USA");
              assertThat(createRosterPlayer.getTeam().getKey()).isEqualTo("golden-state-warriors");
              
              RosterPlayer.delete(createRosterPlayer, ProcessingType.online);
              Player.delete(player, ProcessingType.online);
          }
        });
    }
    
    @Test
    public void updateRosterPlayer() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player player = TestMockHelper.getPlayer("1975-01-01");
        	  Player.create(player, ProcessingType.online);
        	  
        	  Team team = Team.findByAbbr("GS", ProcessingType.online);
        	  RosterPlayer rosterPlayer = TestMockHelper.getRosterPlayer(player, team, "2000-04-04", "9999-12-31");
        	  RosterPlayer.create(rosterPlayer, ProcessingType.online);
        	  
        	  RosterPlayer createRosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2000-04-04", player.getLastName(), player.getFirstName(), "golden-state-warriors", ProcessingType.online);
        	  
        	  Date toDate = null;
        	  try {
        	  	  toDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2000-04-15");
        	  } catch (ParseException e) {
        	  	  e.printStackTrace();
        	  }
        	  createRosterPlayer.setToDate(toDate);
        	  RosterPlayer.update(createRosterPlayer, ProcessingType.online);
        	  
        	  RosterPlayer expiredRosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2000-04-16", player.getLastName(), player.getFirstName(), "golden-state-warriors", ProcessingType.online);
        	  assertThat(expiredRosterPlayer).isNull();
        	  
        	  RosterPlayer updateRosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2000-04-15", player.getLastName(), player.getFirstName(), "golden-state-warriors", ProcessingType.online);
        	  
              assertThat(updateRosterPlayer.getNumber()).isEqualTo("10");
              assertThat(updateRosterPlayer.getPlayer().getBirthPlace()).isEqualTo("Brooklyn, New York, USA");
              assertThat(updateRosterPlayer.getTeam().getKey()).isEqualTo("golden-state-warriors");
              
              RosterPlayer.delete(updateRosterPlayer, ProcessingType.online);
              Player.delete(player, ProcessingType.online);
          }
        });
    }
    
    @Test
    public void updateRosterPlayerValidation() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Player player = null;
        	  RosterPlayer rosterPlayer = null;
       		  try {
            	  player = TestMockHelper.getPlayer("1975-01-01");
            	  Player.create(player, ProcessingType.online);
            	  
            	  Team team = Team.findByAbbr("GS", ProcessingType.online);
            	  rosterPlayer = TestMockHelper.getRosterPlayer(player, team, "2000-04-04", "9999-12-31");
            	  RosterPlayer.create(rosterPlayer, ProcessingType.online);
            	  
            	  RosterPlayer createRosterPlayer = RosterPlayer.findByDatePlayerNameTeam("2000-04-04", player.getLastName(), player.getFirstName(), "golden-state-warriors", ProcessingType.online);

            	  createRosterPlayer.getPlayer().setFirstName(null);
       			  createRosterPlayer.update();
       		  } catch (PersistenceException e) {
       			  assertThat(e.getCause().getMessage().equalsIgnoreCase("Column 'first_name' cannot be null"));
       		  } finally {
       			  RosterPlayer.delete(rosterPlayer, ProcessingType.online);
       			  Player.delete(player, ProcessingType.online);
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
    
    public List<RosterPlayer> createRosterPlayers() {
    	List<RosterPlayer> rosterPlayers = new ArrayList<RosterPlayer>();
    	Team teamGS = Team.findByAbbr("GS", ProcessingType.online);
    	Team teamSAC = Team.findByAbbr("SAC", ProcessingType.online);
    	Team teamLAL = Team.findByAbbr("LAL", ProcessingType.online);
    			
  	  	Player player1 = TestMockHelper.getPlayer("1975-01-01");
  	  	Player.create(player1, ProcessingType.online);
  	  	
  	  	RosterPlayer rosterPlayer1 = TestMockHelper.getRosterPlayer(player1, teamGS, "2000-03-01", "2000-06-30"); 
  	  	RosterPlayer.create(rosterPlayer1, ProcessingType.online);
  	  	rosterPlayers.add(rosterPlayer1);
  	  	
  	  	RosterPlayer rosterPlayer2 = TestMockHelper.getRosterPlayer(player1, teamSAC, "2000-02-01", "2000-02-28"); 
  	  	RosterPlayer.create(rosterPlayer2, ProcessingType.online);
  	  	rosterPlayers.add(rosterPlayer2);
  	  	
  	  	RosterPlayer rosterPlayer3 = TestMockHelper.getRosterPlayer(player1, teamGS, "2000-01-01", "2000-01-31"); 
  	  	RosterPlayer.create(rosterPlayer3, ProcessingType.online);
  	  	rosterPlayers.add(rosterPlayer3);
  	  
  	  	Player player2 = TestMockHelper.getPlayer("1972-05-01");
  	  	Player.create(player2, ProcessingType.online);
  	  	
  	  	RosterPlayer rosterPlayer4 = TestMockHelper.getRosterPlayer(player2, teamSAC, "2000-04-01", "2000-04-30"); 
  	  	RosterPlayer.create(rosterPlayer4, ProcessingType.online);
  	  	rosterPlayers.add(rosterPlayer4);
  	  	
  	  	RosterPlayer rosterPlayer5 = TestMockHelper.getRosterPlayer(player2, teamLAL, "1999-11-01", "2000-02-28"); 
  	  	RosterPlayer.create(rosterPlayer5, ProcessingType.online);
  	  	rosterPlayers.add(rosterPlayer5);
  	  	
  	  	RosterPlayer rosterPlayer6 = TestMockHelper.getRosterPlayer(player2, teamSAC, "1998-10-30", "1999-06-30"); 
  	  	RosterPlayer.create(rosterPlayer6, ProcessingType.online);
  	  	rosterPlayers.add(rosterPlayer6);
  	  	
  	  	return rosterPlayers;
    }
}
