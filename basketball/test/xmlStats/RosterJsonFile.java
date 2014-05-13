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

import org.junit.Test;

import util.DateTime;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RosterJsonFile {

    @Test
    public void createRoster() {
        running(fakeApplication(), new Runnable() {
          public void run() {
        	  InputStream baseJson; 
        	  try {
        		  Path path =  Paths.get(System.getProperty("config.test")).resolve("sacramento-kings_2013_14_complete.json");
        		  File file = path.toFile();
	              baseJson = new FileInputStream(file);
		        
	              ObjectMapper mapper = new ObjectMapper();
	              mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	            
	              Roster xmlStatsRoster = mapper.readValue(baseJson, Roster.class);
	              
	              List<RosterPlayer> rosterPlayers = JsonHelper.getRosterPlayers(xmlStatsRoster, ProcessingType.online);
	              
	              for (int i = 0; i < rosterPlayers.size(); i++) {
	            	RosterPlayer rosterPlayer = rosterPlayers.get(i);
	            	Player player = rosterPlayer.getPlayer();
	            	Player searchPlayer = Player.findByNameBirthDate(player.getLastName(), player.getFirstName(), DateTime.getFindDateShort(player.getBirthDate()), ProcessingType.online);
	            	if (searchPlayer == null) {
	            		Player.create(player, ProcessingType.online);
	            		RosterPlayer.create(rosterPlayer, ProcessingType.online);
	            	}
					Player createPlayer = Player.findByNameBirthDate(player.getLastName(), player.getFirstName(), DateTime.getFindDateShort(player.getBirthDate()), ProcessingType.online);
					assertThat(createPlayer.getBirthPlace()).isEqualTo(player.getBirthPlace());
	              }
	              
	              for (int j = 0; j < rosterPlayers.size(); j++) {
	            	  RosterPlayer.delete(rosterPlayers.get(j), ProcessingType.online);
	            	  Player.delete(rosterPlayers.get(j).getPlayer(), ProcessingType.online);
	              }

        	  } catch (FileNotFoundException e) {
      	          e.printStackTrace();
      	      } catch (IOException e) {
      	          e.printStackTrace();
      	      }
          }
        });
    }
}