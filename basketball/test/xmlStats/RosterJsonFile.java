package xmlStats;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import json.xmlStats.JsonHelper;
import json.xmlStats.Roster;
import models.Game.ProcessingType;
import models.Player;
import models.RosterPlayer;

import org.joda.time.DateTime;
import org.junit.Test;

import util.DateTimeUtil;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class RosterJsonFile {

    @Test
    public void createRoster() {
        running(fakeApplication(), new Runnable() {
        	public void run() {
        		InputStream baseJson; 
        		try {
        			Path path =  Paths.get(System.getProperty("config.test")).resolve("sacramento-kings.json");
        			File file = path.toFile();
        			baseJson = new FileInputStream(file);
		        
        			ObjectMapper mapper = new ObjectMapper();
        			mapper.registerModule(new JodaModule());
        			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);	            
        			Roster xmlStatsRoster = mapper.readValue(baseJson, Roster.class);
	              
            		ProcessingType processingType = ProcessingType.online;
            		String rosterDate = "1998-05-14";
            		DateTime fromDate = DateTimeUtil.createDateFromStringDate(rosterDate);
            		DateTime toDate = DateTimeUtil.getDateMaxSeason(DateTimeUtil.createDateFromStringDate(rosterDate));
            		String rosterTeamKey = "sacramento-kings";
            		
        			List<RosterPlayer> xmlStatsRosterPlayers = JsonHelper.getRosterPlayers(xmlStatsRoster, processingType);
        			RosterPlayer xmlStatsRosterPlayer;
	  			  	Player xmlStatsPlayer; 
	  			  	RosterPlayer finderRosterPlayer;
	  			  	Player finderPlayer;

	  			  	xmlStatsRosterPlayer = xmlStatsRosterPlayers.get(0);
	  			  	xmlStatsPlayer = xmlStatsRosterPlayer.getPlayer();

	  			  	Player.create(xmlStatsPlayer, processingType);							
	  			  	xmlStatsRosterPlayer.setFromDate(fromDate);
	  			  	xmlStatsRosterPlayer.setToDate(toDate);
	  			  	RosterPlayer.create(xmlStatsRosterPlayer, processingType);

	  			  	finderRosterPlayer = RosterPlayer.findByDatePlayerNameTeam(rosterDate, xmlStatsPlayer.getLastName(), xmlStatsPlayer.getFirstName(), rosterTeamKey, processingType);
	  			  	finderPlayer = finderRosterPlayer.getPlayer();
	  			  	
	  			  	assertThat(finderRosterPlayer.getNumber()).isEqualTo(xmlStatsRosterPlayer.getNumber());
	  			  	assertThat(finderPlayer.getLastName()).isEqualTo(xmlStatsPlayer.getLastName());
	  			  	
	  			  	RosterPlayer.delete(finderRosterPlayer, processingType);
	  			  	Player.delete(finderPlayer, processingType);

        	  } catch (FileNotFoundException e) {
      	          e.printStackTrace();
      	      } catch (IOException e) {
      	          e.printStackTrace();
      	      }
          }
        });
    }
}