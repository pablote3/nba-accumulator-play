import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.flash;
import static play.test.Helpers.redirectLocation;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.util.HashMap;
import java.util.Map;

import models.Team;

import org.junit.Test;

import play.data.Form;
import play.mvc.Content;
import play.mvc.Result;

public class FormTeamTest {
	static Form<Team> teamForm = Form.form(Team.class);

    @Test
    public void updateTeam() {
	    running(fakeApplication(), new Runnable() {
	        public void run() {
	        	Result result;
	        	
	            result = callAction(controllers.routes.ref.Teams.searchTeam("key", "atlanta-hawks"));
	            assertThat(status(result)).isEqualTo(OK);
	            Long teamId = Long.valueOf(contentAsString(result));

	            Map<String,String> data = new HashMap<String,String>();
	            data.put("key", "atlanta-hawks2");
	            data.put("fullName", "Atlanta Hawks");
	            data.put("abbr", "ATL");
	            data.put("active", "true");
	            data.put("conference", "East");
	            data.put("division", "Southeast");
	            data.put("siteName", "Phillips Arena");
	            data.put("city", "Atlanta");
	            data.put("state", "GA");
	            
                result = callAction(controllers.routes.ref.Teams.save(teamId), fakeRequest().withFormUrlEncodedBody(data));	            
	            assertThat(status(result)).isEqualTo(SEE_OTHER);
	            assertThat(flash(result).get("success")).isEqualTo("Team Atlanta Hawks has been updated");
	            assertThat(redirectLocation(result)).isEqualTo("/teams");

	            data.put("key", "atlanta-hawks");
                result = callAction(controllers.routes.ref.Teams.save(teamId), fakeRequest().withFormUrlEncodedBody(data));            
	            assertThat(status(result)).isEqualTo(SEE_OTHER);
	            assertThat(flash(result).get("success")).isEqualTo("Team Atlanta Hawks has been updated");
	            assertThat(redirectLocation(result)).isEqualTo("/teams");         
	        }
	    });
    }
    
    @Test
    public void updateTeam_MissingRequiredField() {
	    running(fakeApplication(), new Runnable() {
	        public void run() {
	        	Result result;
	        	
	            result = callAction(controllers.routes.ref.Teams.searchTeam("key", "atlanta-hawks"));
	            assertThat(status(result)).isEqualTo(OK);
	            Long teamId = Long.valueOf(contentAsString(result));

	            Map<String,String> data = new HashMap<String,String>();
	            data.put("conference", null);
	            
                result = callAction(controllers.routes.ref.Teams.save(teamId), fakeRequest().withFormUrlEncodedBody(data));	            
	            assertThat(status(result)).isEqualTo(BAD_REQUEST);
	        }
	    });
    }
    
    @Test
    public void saveAndDeleteTeam() {
	    running(fakeApplication(), new Runnable() {
	        public void run() {
	        	Result result;

	            Map<String,String> data = new HashMap<String,String>();
	            data.put("key", "seattle-supersonics");
	            data.put("fullName", "Seattle Supersonics");
	            data.put("abbr", "SEA");
	            data.put("active", "false");
	            data.put("conference", "West");
	            data.put("division", "Pacific");
	            data.put("siteName", "Key Arena");
	            data.put("city", "Seattle");
	            data.put("state", "WA");
	            
                result = callAction(controllers.routes.ref.Teams.save(-1L), fakeRequest().withFormUrlEncodedBody(data));	            
	            assertThat(status(result)).isEqualTo(SEE_OTHER);
	            assertThat(flash(result).get("success")).isEqualTo("Team Seattle Supersonics has been created");
	            assertThat(redirectLocation(result)).isEqualTo("/teams");
	            
	            result = callAction(controllers.routes.ref.Teams.searchTeam("key", "seattle-supersonics"));
	            assertThat(status(result)).isEqualTo(OK);
	            String teamId = contentAsString(result);
	            
	            result = callAction(controllers.routes.ref.Teams.deleteTeam(Integer.parseInt(teamId)));
	            assertThat(status(result)).isEqualTo(SEE_OTHER);
	            assertThat(flash(result).get("success")).isEqualTo("Team has been deleted");
	            assertThat(redirectLocation(result)).isEqualTo("/teams");	            
	        }
	    });
    }

    @Test
    public void renderTemplate() {
	    running(fakeApplication(), new Runnable() {
	        public void run() {
	        	Content html = views.html.index.render(Team.findAll(), teamForm);
	        	assertThat(contentType(html)).isEqualTo("text/html");
	        	assertThat(contentAsString(html)).contains("Team Entry");
	        }
	    });	        	
    }
}
