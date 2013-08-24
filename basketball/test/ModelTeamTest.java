import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.util.List;

import models.Team;
import models.Team.Conference;
import models.Team.Division;

import org.junit.Test;

import com.avaje.ebean.Page;

public class ModelTeamTest {    
    @Test
    public void findAllTeams() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Team> teams = Team.findAll();
        	  assertThat(teams.size()).isEqualTo(31);
          }
        });
    }
    
	@Test
    public void findActiveTeams() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  List<Team> teams = Team.findActive(true);
        	  assertThat(teams.size()).isEqualTo(30);
          }
        });
    }
    
    @Test
    public void createTeam() {
        running(fakeApplication(), new Runnable() {
          public void run() {
              Team team = new Team();
              team.setKey("seattle-supersonics");
              team.setFullName("Seattle Supersonics");
              team.setAbbr("SEA");
              team.setConference(Conference.West);
              team.setDivision(Division.Pacific);
              team.setSiteName("Key Arena");
              team.setCity("Seattle");
              team.setState("WA");
              
              Team.create(team);
              
              Team createTeam = Team.find.where().eq("key", "seattle-supersonics").findUnique();
              assertThat(createTeam.getFullName()).isEqualTo("Seattle Supersonics");
              assertThat(createTeam.getAbbr()).isEqualTo("SEA");
              Team.delete(createTeam.getId());
          }
        });
    }

    @Test
    public void paginationTeams() {
        running(fakeApplication(), new Runnable() {
           public void run() {
               Page<Team> teams = Team.page(1, 15, "fullName", "ASC", "");
               assertThat(teams.getTotalRowCount()).isEqualTo(31);
               assertThat(teams.getList().size()).isEqualTo(15);
           }
        });
    }
}
