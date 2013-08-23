import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;

import java.io.IOException;
import java.util.List;

import models.Team;
import models.Team.Conference;
import models.Team.Division;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.avaje.ebean.Ebean;

import play.test.FakeApplication;
import play.data.Form;
import play.mvc.Content;
import play.test.Helpers;

public class ApplicationTest {
	static Form<Team> teamForm = Form.form(Team.class);
	
	  public static FakeApplication app;
	  public static String createDdl = "";
	  public static String dropDdl = "";
	  
	  @BeforeClass
	  public static void startApp() {
	    app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
	    Helpers.start(app);
	    
	    // Reading the evolution file
	    String evolutionContent;
		try {
			evolutionContent = FileUtils.readFileToString(app.getWrappedApplication().getFile("conf/evolutions/default/1.sql"));
		    // Splitting the String to get Create & Drop DDL
		    String[] splittedEvolutionContent = evolutionContent.split("# --- !Ups");
		    String[] upsDowns = splittedEvolutionContent[1].split("# --- !Downs");
		    createDdl = upsDowns[0];
		    dropDdl = upsDowns[1];
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	  
	  @Before
	  public void createCleanDb() {
	    Ebean.execute(Ebean.createCallableSql(dropDdl));
	    Ebean.execute(Ebean.createCallableSql(createDdl));
	  } 
	  
	  @AfterClass
	  public static void stopApp() {
	    Helpers.stop(app);
	  } 
	  
    @Test 
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }
    
    @Test
    public void saveTeam() {
    	Team team = new Team();
        team.setKey("seattle-supersonics");
        team.setAbbr("SEA");
        team.setFullName("Seattle Supersonics");
        team.setConference(Conference.West);
        team.setDivision(Division.Pacific);
        team.setSiteName("Microsoft Stadium");
        team.setCity("Seattle");
        team.setState("WA");
        team.setActive(true);
        team.save();
        assertThat(team.getId()).isNotNull();
    }
    
    @Test
    public void getAllTeams() {
    	List<Team> teamList = Team.findAll();
     	assertThat(teamList.size()).isEqualTo(31);
    }

    @Test
    public void renderTemplate() {
        Content html = views.html.index.render(Team.findAll(), teamForm);
        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Team Entry");
    }
}
