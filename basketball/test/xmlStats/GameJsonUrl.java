package xmlStats;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import json.xmlStats.JsonHelper;
import json.xmlStats.NBABoxScore;
import models.BoxScore;
import models.Game;
import models.Team;
import models.BoxScore.Location;
import models.BoxScore.Result;
import models.Game.ProcessingType;
import models.Game.SeasonType;
import models.Game.Status;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameJsonUrl {
    static final String AUTHORIZATION = "Authorization";
    static final String USER_AGENT = "User-agent";
    static final String ACCEPT_ENCODING = "Accept-encoding";
    static final String GZIP = "gzip";
    
    @Ignore
    @Test
    public void updateGame() {
        running(fakeApplication(), new Runnable() {
        	public void run() {
		        InputStream baseJson = null;
		        try {
	        		Path path =  Paths.get(System.getProperty("config.properties")).resolve("service.properties");
	        		File file = path.toFile();				
					Properties properties = new Properties();
					FileInputStream in = new FileInputStream(file);
					properties.load(in);
					in.close();

	            	String urlBoxScore = properties.getProperty("xmlstats.urlBoxScore");
	            	String event = "20120621-oklahoma-city-thunder-at-miami-heat.json";
		            URL url = new URL(urlBoxScore + event);
		            URLConnection connection = url.openConnection();
		            String accessToken = properties.getProperty("xmlstats.accessToken");
		            String bearer = "Bearer " + accessToken;
		            String userAgentName = properties.getProperty("xmlstats.userAgentName");
		            connection.setRequestProperty(AUTHORIZATION, bearer);
		            connection.setRequestProperty(USER_AGENT, userAgentName);
		            connection.setRequestProperty(ACCEPT_ENCODING, GZIP);
		            baseJson = connection.getInputStream();
		            String encoding = connection.getContentEncoding();
		            if (GZIP.equals(encoding)) {
		            	baseJson = new GZIPInputStream(baseJson);
		            }
			
		            if (baseJson != null) {
		            	ObjectMapper mapper = new ObjectMapper();
		    	        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		    	        NBABoxScore xmlStats = mapper.readValue(baseJson, NBABoxScore.class);
			    	        
			            Game scheduleGame = new Game();
			            scheduleGame.setDate(xmlStats.event_information.getDate());
			            scheduleGame.setStatus(Status.scheduled);
			            scheduleGame.setSeasonType(xmlStats.event_information.getSeasonType());
			            
			            BoxScore awayBoxScore = new BoxScore();
			            awayBoxScore.setLocation(Location.away);
			            awayBoxScore.setTeam(Team.findByKey("key", xmlStats.away_team.getKey()));
			            scheduleGame.addBoxScore(awayBoxScore);
			              
			            BoxScore homeBoxScore = new BoxScore();
			            homeBoxScore.setLocation(Location.home);
			            homeBoxScore.setTeam(Team.findByKey("key", xmlStats.home_team.getKey()));
			            scheduleGame.addBoxScore(homeBoxScore);

			            Game.create(scheduleGame);
			            Long gameId = scheduleGame.getId();
				              
			    		Game completeGame = Game.findById(gameId, ProcessingType.online);
			    		completeGame.setStatus(Status.completed);	              
		      		  	completeGame.setGameOfficials(JsonHelper.getGameOfficials(xmlStats.officials, ProcessingType.online));
		      		  	
		      		  	awayBoxScore = completeGame.getBoxScores().get(0);
		      		  	awayBoxScore.setPeriodScores(JsonHelper.getPeriodScores(xmlStats.away_period_scores));
		      		  	JsonHelper.getBoxScoreStats(awayBoxScore, xmlStats.away_totals);
		              
		      		  	homeBoxScore = completeGame.getBoxScores().get(1);
		      		  	homeBoxScore.setPeriodScores(JsonHelper.getPeriodScores(xmlStats.home_period_scores));
		      		  	JsonHelper.getBoxScoreStats(homeBoxScore, xmlStats.home_totals);              
		      		  	if (xmlStats.away_totals.getPoints() > xmlStats.home_totals.getPoints()) {
		      		  		homeBoxScore.setResult(Result.loss);
		      		  		awayBoxScore.setResult(Result.win);
		      		  	}
		      		  	else {
		      		  		homeBoxScore.setResult(Result.win);
		      		  		awayBoxScore.setResult(Result.loss);
		      		  	}
			              
		      		  	completeGame.update();
		      		  	Game createGame = Game.findById(gameId, ProcessingType.online);
		              
			            assertThat(createGame.getSeasonType()).isEqualTo(SeasonType.post);
			            assertThat(createGame.getGameOfficials().size()).isEqualTo(3);
			            if (createGame.getGameOfficials().size() > 0) {
			            	assertThat(createGame.getGameOfficials().get(1).getOfficial().getLastName()).endsWith("Crawford");
			            	assertThat(createGame.getBoxScores().size()).isEqualTo(2);
			              	for (int i = 0; i < createGame.getBoxScores().size(); i++) {
			              		BoxScore boxScore = createGame.getBoxScores().get(i);
			              		if (boxScore.getLocation().equals(Location.away)) {
			              			assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)36);
			              			assertThat(boxScore.getPeriodScores().get(0).getScore()).isEqualTo((short)26);
			              			assertThat(boxScore.getTeam().getAbbr()).isEqualTo("OKC");	              			  
			              		}
			              		else {
			              			assertThat(boxScore.getFieldGoalMade()).isEqualTo((short)40);
			              			assertThat(boxScore.getPeriodScores().get(0).getScore()).isEqualTo((short)31);
			              			assertThat(boxScore.getTeam().getAbbr()).isEqualTo("MIA");
			              		}
			              	}
		        	  	}
			            Game.delete(createGame.getId(), ProcessingType.online);
		    		    baseJson.close(); 
		    		} 
		        }
		        catch (FileNotFoundException e) {
		    	    e.printStackTrace();
		        } 
		        catch (IOException ex) {
		            ex.printStackTrace();
		        }
		    }
        });
    }
 }
