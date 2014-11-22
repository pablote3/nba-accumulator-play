package xmlStats;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import json.xmlStats.JsonHelper;
import json.xmlStats.Standings;
import models.Game.ProcessingType;
import models.Standing;

import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class StandingJsonFile {

    @Test
    public void createStandings() {
        running(fakeApplication(), new Runnable() {
        	public void run() {
        		InputStream inputStreamJson;
        		InputStreamReader baseJson = null;
        		try {
        			Path path =  Paths.get(System.getProperty("config.test")).resolve("20020621-standings.json");
        			File file = path.toFile();
        			inputStreamJson = new FileInputStream(file);
        			baseJson = new InputStreamReader(inputStreamJson, StandardCharsets.UTF_8);
        			ProcessingType processingType = ProcessingType.online;
		        
        			ObjectMapper mapper = new ObjectMapper();
        			mapper.registerModule(new JodaModule());
        			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        			Standings xmlStatsStandings = mapper.readValue(baseJson, Standings.class);
        			List<Standing> standings = JsonHelper.getStandings(xmlStatsStandings, processingType);
        			
        			for (int i = 0; i < standings.size(); i++) {
						Standing.create(standings.get(i), processingType);
					}

            		String standingDate = "2002-06-21";
            		String standingTeamKey = "sacramento-kings";
	  			  	Standing finderStanding = Standing.findByDateTeam(standingDate, standingTeamKey, processingType);
	  			  	assertThat(finderStanding.getPointsAgainst()).isEqualTo((short)3232);
	  			  	
	  			  	List<Standing> finderStandingList;
	  			  	finderStandingList= Standing.findByDate(standingDate, processingType);
	  			  	assertThat(finderStandingList.size()).isEqualTo(30);
	  			  	
	  			  	finderStandingList = Standing.findByTeam(standingTeamKey, processingType);
	  			  	assertThat(finderStandingList.size()).isEqualTo(1);
	  			  	
	  			  	for (int i = 0; i < standings.size(); i++) {
	  			  		Standing.delete(standings.get(i).getId(), processingType);
	  			  	}
	  			  	
	  			  	finderStandingList= Standing.findByDate(standingDate, processingType);
	  			  	assertThat(finderStandingList.size()).isEqualTo(0);
        	  } catch (FileNotFoundException e) {
      	          e.printStackTrace();
      	      } catch (IOException e) {
      	          e.printStackTrace();
      	      }
          }
        });
    }
}