package actor;

import static actor.ActorApi.InitializeComplete;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.GZIPInputStream;

import json.xmlStats.JsonHelper;
import json.xmlStats.NBABoxScore;
import models.BoxScore;
import models.BoxScore.Result;
import models.BoxScorePlayer;
import models.Game;
import models.Game.ProcessingType;
import models.Game.Status;
import models.GameOfficial;
import models.PeriodScore;
import util.DateTime;
import actor.ActorApi.CompleteGame;
import actor.ActorApi.IncompleteRosterException;
import actor.ActorApi.ScheduleGame;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.XmlStatsException;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameXmlStats extends UntypedActor {
    static final String AUTHORIZATION = "Authorization";
    static final String USER_AGENT = "User-agent";
    static final String ACCEPT_ENCODING = "Accept-encoding";
    static final String GZIP = "gzip";
	private String accessToken;
	private String userAgentName;
	private String urlBoxScore;
	private ProcessingType processingType;
	private ActorRef listener;
	
	public GameXmlStats(ActorRef listener) {
		this.listener = listener;
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			accessToken = "Bearer " + ((ServiceProps) message).accessToken;
			userAgentName = ((ServiceProps) message).userAgentName;
			urlBoxScore = ((ServiceProps) message).urlBoxScore;
			processingType = Game.ProcessingType.valueOf(((ServiceProps) message).processType);
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
	    	        NBABoxScore xmlStatsBoxScore = mapper.readValue(baseJson, NBABoxScore.class);
	    	        
	    	        game.setStatus(Status.completed);
	    	        
	    	        if (game.getGameOfficials().size() > 0) {
	    	        	for (int i = 0; i < game.getGameOfficials().size(); i++) {
							GameOfficial.delete(game.getGameOfficials().get(i), processingType);
						}
	    	        }
	    	        game.setGameOfficials(JsonHelper.getGameOfficials(xmlStatsBoxScore.officials, processingType));
	    	        
	    	        if (awayBoxScore.getPeriodScores().size() > 0) {
	    	        	for (int i = 0; i < awayBoxScore.getPeriodScores().size(); i++) {
							PeriodScore.delete(awayBoxScore.getPeriodScores().get(i), processingType);
						}
	    	        }	    	        
	    	        awayBoxScore.setPeriodScores(JsonHelper.getPeriodScores(xmlStatsBoxScore.away_period_scores));
	    	        JsonHelper.getBoxScoreStats(awayBoxScore, xmlStatsBoxScore.away_totals);
	    	        List<BoxScorePlayer> awayBoxScorePlayers = JsonHelper.getBoxScorePlayers(xmlStatsBoxScore.away_stats, DateTime.getFindDateShort(xmlStatsBoxScore.event_information.getDate()), processingType);

	    	        if (awayBoxScorePlayers != null)
	    	        	awayBoxScore.setBoxScorePlayers(awayBoxScorePlayers);
	    	        else
	    	        	throw new IncompleteRosterException(DateTime.getFindDateShort(game.getDate()), awayBoxScore.getTeam().getKey());
	    	        
	    	        if (homeBoxScore.getPeriodScores().size() > 0) {
	    	        	for (int i = 0; i < homeBoxScore.getPeriodScores().size(); i++) {
							PeriodScore.delete(homeBoxScore.getPeriodScores().get(i), processingType);
						}
	    	        }	 
	    	        homeBoxScore.setPeriodScores(JsonHelper.getPeriodScores(xmlStatsBoxScore.home_period_scores));
	    	        JsonHelper.getBoxScoreStats(homeBoxScore, xmlStatsBoxScore.home_totals);	    	        
	    	        List<BoxScorePlayer> homeBoxScorePlayers = JsonHelper.getBoxScorePlayers(xmlStatsBoxScore.home_stats, DateTime.getFindDateShort(xmlStatsBoxScore.event_information.getDate()), processingType);
	    	        if (homeBoxScorePlayers != null)
	    	        	homeBoxScore.setBoxScorePlayers(homeBoxScorePlayers);
	    	        else
	    	        	throw new IncompleteRosterException(DateTime.getFindDateShort(game.getDate()), homeBoxScore.getTeam().getKey());
	    	        
	    		  	if (xmlStatsBoxScore.away_totals.getPoints() > xmlStatsBoxScore.home_totals.getPoints()) {
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
			catch (IncompleteRosterException e) {
				getSender().tell(e, getSelf());
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