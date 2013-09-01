import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.callAction;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.flash;
import static play.test.Helpers.redirectLocation;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import models.Team;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.data.Form;
import play.mvc.Result;
import play.test.FakeApplication;
import play.test.Helpers;

import com.avaje.ebean.Ebean;

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
    public void updateTeam() {
	    running(fakeApplication(), new Runnable() {
	        public void run() {
	        	Long teamId = 11L;
	        	Result result;

	            Map<String,String> data = new HashMap<String,String>();
	            data.put("id", "11");
	            data.put("key", "atlanta-hawks-update");
	            data.put("fullName", "Atlanta Hawks");
	            data.put("abbr", "ATL");
	            data.put("active", "true");
	            data.put("conference", "East");
	            data.put("division", "Southeast");
	            data.put("siteName", "Phillips Arena");
	            data.put("city", "Atlanta");
	            data.put("state", "GA");
	            
                result = callAction(controllers.routes.ref.Application.updateTeam(teamId), fakeRequest().withFormUrlEncodedBody(data));
	            
	            assertThat(status(result)).isEqualTo(SEE_OTHER);
	            assertThat(flash(result).get("success")).isEqualTo("Team Atlanta Hawks has been updated");
	            assertThat(redirectLocation(result)).isEqualTo("/teams");

	            data.put("key", "atlanta-hawks");
                result = callAction(controllers.routes.ref.Application.updateTeam(teamId), fakeRequest().withFormUrlEncodedBody(data));
            
	            assertThat(status(result)).isEqualTo(SEE_OTHER);
	            assertThat(flash(result).get("success")).isEqualTo("Team Atlanta Hawks has been updated");
	            assertThat(redirectLocation(result)).isEqualTo("/teams");         
	        }
	    });
    }

//    @Test
//    public void renderTemplate() {
//	    running(fakeApplication(), new Runnable() {
//	        public void run() {
//	        	Content html = views.html.index.render(Team.findAll(), teamForm);
//	        	assertThat(contentType(html)).isEqualTo("text/html");
//	        	assertThat(contentAsString(html)).contains("Team Entry");
//	        }
//	    });	        	
//    }
}
