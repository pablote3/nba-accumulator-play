package models;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.List;

import javax.persistence.PersistenceException;




import models.Game.ProcessingType;

import org.junit.Test;

import com.avaje.ebean.Page;

public class TeamTest {    
    @Test
    public void findAll() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Team> teams = Team.findAll();
        	  assertThat(teams.size()).isEqualTo(32);
          }
        });
    }
    
    @Test
    public void findByKey() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Team team1 = Team.findByKey("shortName", "Spurs", ProcessingType.online);
              assertThat(team1.getFullName()).isEqualTo("San Antonio Spurs");
              assertThat(team1.getAbbr()).isEqualTo("SA");
              assertThat(team1.getActive()).isTrue();
          }
        });
    }
    
	@Test
    public void findByActive() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Team> teams = Team.findByActive(true);
        	  assertThat(teams.size()).isEqualTo(30);
          }
        });
    }
	
	@Test
    public void findByTeamKeyOnline() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Team team = Team.findByTeamKey("san-antonio-spurs", ProcessingType.online);
        	  assertThat(team.getFullName()).isEqualTo("San Antonio Spurs");
          }
        });
    }
	
	@Test
    public void findByTeamKeyBatch() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Team team = Team.findByTeamKey("san-antonio-spurs", ProcessingType.batch);
        	  assertThat(team.getFullName()).isEqualTo("San Antonio Spurs");
          }
        });
    }
	
	@Test
    public void findByAbbrOnline() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Team team = Team.findByAbbr("SA", ProcessingType.online);
        	  assertThat(team.getFullName()).isEqualTo("San Antonio Spurs");
          }
        });
    }
	
	@Test
    public void findByAbbrBatch() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  Team team = Team.findByAbbr("SA", ProcessingType.batch);
        	  assertThat(team.getFullName()).isEqualTo("San Antonio Spurs");
          }
        });
    }
	
	@Test
    public void findWithFilter() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Team> teams = Team.findFilter("new");
        	  assertThat(teams.size()).isEqualTo(3);
          }
        });
    }
    
    @Test
    public void createTeam() {
        running(fakeApplication(), new Runnable() {
          public void run() {       
              Team.create(TestMockHelper.getTeam(), ProcessingType.online);
              
              Team createTeam = Team.findByTeamKey("seattle-supersonics", ProcessingType.online);
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
              Team team = Team.findByKey("key", "miami-heat", ProcessingType.online);
              team.setCity("Detroit");
              team.update();
              
              Team updateTeam = Team.findByKey("key", "miami-heat", ProcessingType.online);
              assertThat(updateTeam.getFullName()).isEqualTo("Miami Heat");
              assertThat(updateTeam.getAbbr()).isEqualTo("MIA");
              assertThat(updateTeam.getCity()).isEqualTo("Detroit");
              updateTeam.setCity("Miami");
              updateTeam.update();
          }
        });
    }
    
    @Test
    public void updateTeamValidation() {
        running(fakeApplication(), new Runnable() {
          public void run() {
       		  try {
       			  Team team = Team.findByKey("key", "miami-heat", ProcessingType.online);
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
               assertThat(teams.getTotalRowCount()).isEqualTo(32);
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
