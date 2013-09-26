import java.io.IOException;

import models.entity.Team;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import play.data.Form;
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
}
