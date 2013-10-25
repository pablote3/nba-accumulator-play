package xmlStats;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import models.entity.BoxScore;
import models.entity.BoxScore.Location;
import models.entity.BoxScore.Result;
import models.entity.Game;
import models.entity.Game.SeasonType;
import models.entity.Game.Status;
import models.entity.Team;
import models.partial.XmlStats;

import org.junit.Test;

import util.DateTime;
import util.FileIO;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameJsonUrl {
    static final String AUTHORIZATION = "Authorization";
    static final String USER_AGENT = "User-agent";
    static final String ACCEPT_ENCODING = "Accept-encoding";
    static final String GZIP = "gzip";
    static final String ISO_8601_FMT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    static final SimpleDateFormat XMLSTATS_DATE = new SimpleDateFormat(ISO_8601_FMT);
    
    @Test
    public void updateGame() {
        running(fakeApplication(), new Runnable() {
        	public void run() {
		        InputStream baseJson = null;
		        try {
		        	Properties props = FileIO.loadProperties("config.basketball");
		        	if (props != null) {
		            	String urlBoxScore = props.getProperty("xmlstats.urlBoxScore");
		            	String event = "20120621-oklahoma-city-thunder-at-miami-heat.json";
			            URL url = new URL(urlBoxScore + event);
			            URLConnection connection = url.openConnection();
			            String accessToken = props.getProperty("xmlstats.accessToken");
			            String bearer = "Bearer " + accessToken;
			            String userAgentName = props.getProperty("xmlstats.userAgentName");
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
			    	        XmlStats xmlStats = mapper.readValue(baseJson, XmlStats.class);
			    	        
				            Game scheduleGame = new Game();
				            scheduleGame.setDate(xmlStats.event_information.getDate());
				            scheduleGame.setStatus(Status.scheduled);
				            scheduleGame.setSeasonType(xmlStats.event_information.getSeasonType());
				              
				            BoxScore homeBoxScore = new BoxScore();
				            homeBoxScore.setLocation(Location.home);
				            homeBoxScore.setTeam(Team.find.where().eq("key", xmlStats.home_team.getKey()).findUnique());
				            scheduleGame.addBoxScore(homeBoxScore);
				              
				            BoxScore awayBoxScore = new BoxScore();
				            awayBoxScore.setLocation(Location.away);
				            awayBoxScore.setTeam(Team.find.where().eq("key", xmlStats.away_team.getKey()).findUnique());
				            scheduleGame.addBoxScore(awayBoxScore);
				              
				            Game.create(scheduleGame);
				            
		    		  		Game completeGame = Game.findByDateTeamKey(DateTime.getFindDateShort(xmlStats.event_information.getDate()), xmlStats.home_team.getKey());
			      		  	completeGame.setStatus(Status.completed);	              
			      		  	completeGame.setGameOfficials(GameJsonHelper.getGameOfficials(xmlStats.officials));
			              
			      		  	homeBoxScore.setPeriodScores(GameJsonHelper.getPeriodScores(xmlStats.home_period_scores));
			      		  	GameJsonHelper.getBoxScoreStats(homeBoxScore, xmlStats.home_totals);              

			      		  	awayBoxScore.setPeriodScores(GameJsonHelper.getPeriodScores(xmlStats.away_period_scores));
			      		  	GameJsonHelper.getBoxScoreStats(awayBoxScore, xmlStats.away_totals);

			      		  	if (xmlStats.away_totals.getPoints() > xmlStats.home_totals.getPoints()) {
			      		  		homeBoxScore.setResult(Result.loss);
			      		  		awayBoxScore.setResult(Result.win);
			      		  	}
			      		  	else {
			      		  		homeBoxScore.setResult(Result.win);
			      		  		awayBoxScore.setResult(Result.loss);
			      		  	}
			              
			      		  	Game createGame = Game.findByDateTeamKey(DateTime.getFindDateShort(xmlStats.event_information.getDate()), xmlStats.home_team.getKey());
			              
			      		  	createGame.update();
			    	        
				            assertThat(createGame.getSeasonType()).isEqualTo(SeasonType.post);
				            if (createGame.getGameOfficials().size() > 0) {
				            	assertThat(createGame.getGameOfficials().get(1).getOfficial().getLastName()).endsWith("Crawford");
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
				            Game.delete(createGame.getId());
			    		    baseJson.close(); 
			    		} 
		        	}
		        	else { 
		        		//TODO improve error and exception handling
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
