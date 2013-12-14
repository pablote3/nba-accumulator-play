package actor;

import static actor.ActorApi.InitializeComplete;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import models.entity.BoxScore;
import models.entity.BoxScore.Result;
import models.entity.Game;
import models.entity.Game.Status;
import models.partial.XmlStat;
import util.DateTime;
import xmlStats.GameJsonHelper;
import actor.ActorApi.CompleteGame;
import actor.ActorApi.ScheduleGame;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.XmlStatsException;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class XmlStats extends UntypedActor {
    static final String AUTHORIZATION = "Authorization";
    static final String USER_AGENT = "User-agent";
    static final String ACCEPT_ENCODING = "Accept-encoding";
    static final String GZIP = "gzip";
	private String accessToken;
	private String userAgentName;
	private String urlBoxScore;
	private ActorRef listener;
	
	public XmlStats(ActorRef listener) {
		this.listener = listener;
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			accessToken = "Bearer " + ((ServiceProps) message).accessToken;
			userAgentName = ((ServiceProps) message).userAgentName;
			urlBoxScore = ((ServiceProps) message).urlBoxScore;
			getSender().tell(InitializeComplete, getSelf());
		}
		else if(message instanceof ScheduleGame) {
			URL url;
			InputStream baseJson = null;
			Game game = ((ScheduleGame)message).game;
			BoxScore awayBoxScore = game.getBoxScores().get(0);
			BoxScore homeBoxScore = game.getBoxScores().get(1);
			
			String urlDate = DateTime.getFindDateNaked(game.getDate());
			String urlAwayTeam = awayBoxScore.getTeam().getKey();
			String urlHomeTeam = homeBoxScore.getTeam().getKey();			
			String urlEvent = urlDate + "-" + urlAwayTeam + "-at-" + urlHomeTeam + ".json";
						
			try {
				url = new URL(urlBoxScore + urlEvent);
				URLConnection connection = url.openConnection();
				connection.setRequestProperty(AUTHORIZATION, accessToken);
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
	    	        XmlStat xmlStat = mapper.readValue(baseJson, XmlStat.class);
	    	        
	    	        game.setStatus(Status.completed);
	    	        game.setGameOfficials(GameJsonHelper.getGameOfficials(xmlStat.officials));
	    	      
	    	        awayBoxScore.setPeriodScores(GameJsonHelper.getPeriodScores(xmlStat.away_period_scores));
	    	        GameJsonHelper.getBoxScoreStats(awayBoxScore, xmlStat.away_totals);
	    	        
	    	        homeBoxScore.setPeriodScores(GameJsonHelper.getPeriodScores(xmlStat.home_period_scores));
	    	        GameJsonHelper.getBoxScoreStats(homeBoxScore, xmlStat.home_totals); 
	    	        
	    		  	if (xmlStat.away_totals.getPoints() > xmlStat.home_totals.getPoints()) {
	    		  		homeBoxScore.setResult(Result.loss);
	    		  		awayBoxScore.setResult(Result.win);
	    		  	}
	    		  	else {
	    		  		homeBoxScore.setResult(Result.win);
	    		  		awayBoxScore.setResult(Result.loss);
	    		  	}
	    		  	CompleteGame cg = new CompleteGame(game);
	    		  	getSender().tell(cg, getSelf());
	            }
			} 
			catch (MalformedURLException e) {
				listener.tell(new XmlStatsException("MalformedURLException"), getSelf());
			} 
			catch (IOException e) {
				listener.tell(new XmlStatsException("IOException"), getSelf());
			}
			catch (Exception e) {
				System.out.println("Exception");
			}
		}
		else {
			unhandled(message);
		}
	}
}