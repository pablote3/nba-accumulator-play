package actor;

import static actor.ActorApi.InitializeComplete;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.GZIPInputStream;

import json.xmlStats.JsonHelper;
import json.xmlStats.NBABoxScore;
import models.BoxScore;
import models.BoxScore.Result;
import models.BoxScorePlayer;
import models.Game;
import models.Game.ProcessingType;
import models.Game.Source;
import models.Game.Status;
import models.GameOfficial;
import util.DateTimeUtil;
import actor.ActorApi.ActiveGame;
import actor.ActorApi.OfficialException;
import actor.ActorApi.RosterException;
import actor.ActorApi.RetrieveGame;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.XmlStatsException;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class GameXmlStats extends UntypedActor {
    static final String AUTHORIZATION = "Authorization";
    static final String USER_AGENT = "User-agent";
    static final String ACCEPT_ENCODING = "Accept-encoding";
    static final String GZIP = "gzip";
	private String accessToken;
	private String userAgentName;
	private String urlBoxScore;
	private String fileBoxScore;
	private ProcessingType processingType;
	private Source source;
	private ActorRef listener;
	
	public GameXmlStats(ActorRef listener) {
		this.listener = listener;
	}
	
	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			accessToken = "Bearer " + ((ServiceProps) message).accessToken;
			userAgentName = ((ServiceProps) message).userAgentName;
			urlBoxScore = ((ServiceProps) message).urlBoxScore;
			fileBoxScore = ((ServiceProps) message).fileBoxScore;
			processingType = Game.ProcessingType.valueOf(((ServiceProps) message).processType);
			source = Game.Source.valueOf(((ServiceProps) message).sourceBoxScore);
			getSender().tell(InitializeComplete, getSelf());
		}
		else if(message instanceof RetrieveGame) {
			Game game = ((RetrieveGame)message).game;
			BoxScore awayBoxScore = game.getBoxScores().get(0);
			BoxScore homeBoxScore = game.getBoxScores().get(1);
			
			String urlDate = DateTimeUtil.getFindDateNaked(game.getDate());
			String urlAwayTeam = awayBoxScore.getTeam().getKey();
			String urlHomeTeam = homeBoxScore.getTeam().getKey();			
			String event = urlDate + "-" + urlAwayTeam + "-at-" + urlHomeTeam + ".json";
			InputStream inputStreamJson = null;
			InputStreamReader baseJson = null;

			try {
				if (source.equals(Source.file)) {
					Path path =  Paths.get(fileBoxScore).resolve(event);
					File file = path.toFile();
					inputStreamJson = new FileInputStream(file);
				}
				else {
					URL url = new URL(urlBoxScore + event);
					URLConnection connection = url.openConnection();
					connection.setRequestProperty(AUTHORIZATION, accessToken);
					connection.setRequestProperty(USER_AGENT, userAgentName);
					connection.setRequestProperty(ACCEPT_ENCODING, GZIP);
					inputStreamJson = connection.getInputStream();
					String encoding = connection.getContentEncoding();
					if (GZIP.equals(encoding)) {
						inputStreamJson = new GZIPInputStream(inputStreamJson);
					}
				}
				
				baseJson = new InputStreamReader(inputStreamJson, StandardCharsets.UTF_8);
		        if (baseJson != null) {
		           	ObjectMapper mapper = new ObjectMapper();
		           	mapper.registerModule(new JodaModule());  
		    	    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		    	    NBABoxScore xmlStatsBoxScore = mapper.readValue(baseJson, NBABoxScore.class);
		    	        
		    	    game.setStatus(Status.completed);
		    	        
		    	    if (game.getGameOfficials().size() > 0) {
		    	      	for (int i = 0; i < game.getGameOfficials().size(); i++) {
							GameOfficial.delete(game.getGameOfficials().get(i), processingType);
						}
		    	    }
		    	    List<GameOfficial> gameOfficials = JsonHelper.getGameOfficials(xmlStatsBoxScore.officials, processingType);		    	    
		    	    
		    	    if (gameOfficials != null)
						game.setGameOfficials(gameOfficials);
		    	    else
		    	    	throw new OfficialException("Official not found");
		    	    
		    	    JsonHelper.getBoxScoreStats(awayBoxScore, xmlStatsBoxScore.away_totals);
		    	    
		    	    int[] awayPeriodScores = xmlStatsBoxScore.away_period_scores;
		    	    awayBoxScore.setPointsPeriod1((short)awayPeriodScores[0]);
		    	    awayBoxScore.setPointsPeriod2((short)awayPeriodScores[1]);
		    	    awayBoxScore.setPointsPeriod3((short)awayPeriodScores[2]);
		    	    awayBoxScore.setPointsPeriod4((short)awayPeriodScores[3]);
		    	    if(awayPeriodScores.length > 4)
		    	    	awayBoxScore.setPointsPeriod5((short)awayPeriodScores[4]);
		    	    if(awayPeriodScores.length > 5)
		    	    	awayBoxScore.setPointsPeriod6((short)awayPeriodScores[5]);
		    	    if(awayPeriodScores.length > 6)
		    	    	awayBoxScore.setPointsPeriod7((short)awayPeriodScores[6]);
		    	    if(awayPeriodScores.length > 7)
		    	    	awayBoxScore.setPointsPeriod8((short)awayPeriodScores[7]);
		    	    
		    	    List<BoxScorePlayer> awayBoxScorePlayers = JsonHelper.getBoxScorePlayers(xmlStatsBoxScore.away_stats, DateTimeUtil.getFindDateShort(xmlStatsBoxScore.event_information.getDate()), processingType);
	
		    	    if (awayBoxScorePlayers != null) {
		    	    	System.out.println("  Away Team " + awayBoxScore.getTeam().getShortName() + " Roster is Complete");	
		    	    	awayBoxScore.setBoxScorePlayers(awayBoxScorePlayers);
		    	    }
		    	    else
		    	    	throw new RosterException(game.getId(), DateTimeUtil.getFindDateShort(game.getDate()), awayBoxScore.getTeam().getKey());
		    	        
		    	    JsonHelper.getBoxScoreStats(homeBoxScore, xmlStatsBoxScore.home_totals);
		    	    
		    	    int[] homePeriodScores = xmlStatsBoxScore.home_period_scores;
		    	    homeBoxScore.setPointsPeriod1((short)homePeriodScores[0]);
		    	    homeBoxScore.setPointsPeriod2((short)homePeriodScores[1]);
		    	    homeBoxScore.setPointsPeriod3((short)homePeriodScores[2]);
		    	    homeBoxScore.setPointsPeriod4((short)homePeriodScores[3]);
		    	    if(homePeriodScores.length > 4)
		    	    	homeBoxScore.setPointsPeriod5((short)homePeriodScores[4]);
		    	    if(homePeriodScores.length > 5)
		    	    	homeBoxScore.setPointsPeriod6((short)homePeriodScores[5]);
		    	    if(homePeriodScores.length > 6)
		    	    	homeBoxScore.setPointsPeriod7((short)homePeriodScores[6]);
		    	    if(homePeriodScores.length > 7)
		    	    	homeBoxScore.setPointsPeriod8((short)homePeriodScores[7]);
		    	    
		    	    List<BoxScorePlayer> homeBoxScorePlayers = JsonHelper.getBoxScorePlayers(xmlStatsBoxScore.home_stats, DateTimeUtil.getFindDateShort(xmlStatsBoxScore.event_information.getDate()), processingType);
		    	    if (homeBoxScorePlayers != null) {
						System.out.println("  Home Team " + homeBoxScore.getTeam().getShortName() + " Roster is Complete");						
						homeBoxScore.setBoxScorePlayers(homeBoxScorePlayers);
		    	    }
		    	    else
		    	    	throw new RosterException(game.getId(), DateTimeUtil.getFindDateShort(game.getDate()), homeBoxScore.getTeam().getKey());
		    	        
		    		if (xmlStatsBoxScore.away_totals.getPoints() > xmlStatsBoxScore.home_totals.getPoints()) {
		    		  	homeBoxScore.setResult(Result.loss);
		    		  	awayBoxScore.setResult(Result.win);
		    		}
		    		else {
		    		  	homeBoxScore.setResult(Result.win);
		    		  	awayBoxScore.setResult(Result.loss);
		    		}
		    		ActiveGame ag = new ActiveGame(game);
		    		getSender().tell(ag, getSelf());
				}
			}
			catch (MalformedURLException e) {
				listener.tell(new XmlStatsException("MalformedURLException"), getSelf());
			} 
			catch (IOException e) {
				listener.tell(new XmlStatsException("IOException"), getSelf());
			}
			catch (RosterException e) {
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