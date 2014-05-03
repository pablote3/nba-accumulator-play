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
	            
	              Roster xmlStats = mapper.readValue(baseJson, Roster.class);
	              
	              List<Player> players = JsonHelper.getPlayers(xmlStats.players);
	              
	              for (int i = 0; i < players.size(); i++) {
	            	Player searchPlayer = Player.findByNameBirthDate(players.get(i).getLastName(), players.get(i).getFirstName(), DateTime.getFindDateShort(players.get(i).getBirthDate()), ProcessingType.online);
	            	if (searchPlayer == null) {
	            		Player.create(players.get(i), ProcessingType.online);
	            	}
					Player createPlayer = Player.findByNameBirthDate(players.get(i).getLastName(), players.get(i).getFirstName(), DateTime.getFindDateShort(players.get(i).getBirthDate()), ProcessingType.online);
					assertThat(createPlayer.getBirthPlace()).isEqualTo(players.get(i).getBirthPlace());
	              }
	              
//	              for (int j = 0; j < players.size(); j++) {
//	            	  Player.delete(players.get(j), ProcessingType.online);
//	              }

        	  } catch (FileNotFoundException e) {
      	          e.printStackTrace();
      	      } catch (IOException e) {
      	          e.printStackTrace();
      	      }
          }
        });
    }
}