package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.List;

import javax.persistence.PersistenceException;


import models.entity.Team;

import org.junit.Test;

import com.avaje.ebean.Page;

public class TeamTest {    
    @Test
    public void findTeamsAll() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Team> teams = Team.findAll();
        	  assertThat(teams.size()).isEqualTo(31);
          }
        });
    }
    
	@Test
    public void findTeamsActive() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Team> teams = Team.findActive(true);
        	  assertThat(teams.size()).isEqualTo(30);
          }
        });
    }
	
	@Test
    public void findTeamsFilter() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Team> teams = Team.findFilter("new");
        	  assertThat(teams.size()).isEqualTo(3);
          }
        });
    }
	
    @Test
    public void findTeamFinderKey() {
        running(fakeApplication(), new Runnable() {
          public void run() {
              Team team = Team.find.where().eq("key", "new-orleans-pelicans").findUnique();
              assertThat(team.getFullName()).isEqualTo("New Orleans Pelicans");
              assertThat(team.getAbbr()).isEqualTo("NOP");
              assertThat(team.getActive()).isTrue();
          }
        });
    }
    
    @Test
    public void findTeamFinderShortName() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Team team1 = Team.find.where().eq("shortName", "Pelicans").findUnique();
              assertThat(team1.getFullName()).isEqualTo("New Orleans Pelicans");
              assertThat(team1.getAbbr()).isEqualTo("NOP");
              assertThat(team1.getActive()).isTrue();
          }
        });
    }
    
    @Test
    public void createTeam() {
        running(fakeApplication(), new Runnable() {
          public void run() {       
              Team.create(TestMockHelper.getTeam());
              
              Team createTeam = Team.find.where().eq("key", "seattle-supersonics").findUnique();
              assertThat(createTeam.getFullName()).isEqualTo("Seattle Supersonics");
              assertThat(createTeam.getAbbr()).isEqualTo("SEA");
              Team.delete(createTeam.getId());
          }
        });
    }
    
    @Test
    public void updateTeam() {
        running(fakeApplication(), new Runnable() {
          public void run() {
              Team team = Team.find.where().eq("key", "new-orleans-hornets").findUnique();
              team.setActive(true);
              team.update();
              
              Team updateTeam = Team.find.where().eq("key", "new-orleans-hornets").findUnique();
              assertThat(updateTeam.getFullName()).isEqualTo("New Orleans Hornets");
              assertThat(updateTeam.getAbbr()).isEqualTo("NO");
              assertThat(updateTeam.getActive()).isTrue();
              updateTeam.setActive(false);
              updateTeam.update();
          }
        });
    }
    
    @Test
    public void updateTeamValidation() {
        running(fakeApplication(), new Runnable() {
          public void run() {
       		  try {
       			  Team team = Team.find.where().eq("key", "new-orleans-hornets").findUnique();
       			  team.setFullName(null);
       			  team.update();
       		  } catch (PersistenceException e) {
       			  assertThat(e.getCause().getMessage().equalsIgnoreCase("Column 'full_name' cannot be null"));
       		  }
          }
        });
    }

    @Test
    public void paginationTeams() {
        running(fakeApplication(), new Runnable() {
           public void run() {
               Page<Team> teams = Team.page(0, 15, "fullName", "ASC", "");
               assertThat(teams.getTotalRowCount()).isEqualTo(31);
               assertThat(teams.getList().size()).isEqualTo(15);
           }
        });
    }
    
    @Test
    public void pagnationTeamsFilter() {
        running(fakeApplication(), new Runnable() {
           public void run() {
               Page<Team> teams = Team.page(0, 15, "fullName", "ASC", "new");
               assertThat(teams.getTotalRowCount()).isEqualTo(3);
               assertThat(teams.getList().size()).isEqualTo(3);
           }
        });
    }
}
