package actor;

import static actor.ActorApi.InitializeComplete;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.GZIPInputStream;

import json.xmlStats.JsonHelper;
import json.xmlStats.Roster;
import models.Game;
import models.Game.Source;
import models.Player;
import util.DateTime;
import actor.ActorApi.ActiveRoster;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.UpdateRoster;
import actor.ActorApi.XmlStatsException;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RosterXmlStats extends UntypedActor {
    static final String AUTHORIZATION = "Authorization";
    static final String USER_AGENT = "User-agent";
    static final String ACCEPT_ENCODING = "Accept-encoding";
    static final String GZIP = "gzip";
	private String accessToken;
	private String userAgentName;
	private String urlRoster;
	private String fileRoster;
	private Source source;
	private ActorRef listener;
	
	public RosterXmlStats(ActorRef listener) {
		this.listener = listener;
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			accessToken = "Bearer " + ((ServiceProps) message).accessToken;
			userAgentName = ((ServiceProps) message).userAgentName;
			urlRoster = ((ServiceProps) message).urlRoster;
			fileRoster = ((ServiceProps) message).fileRoster;
			source = Game.Source.valueOf(((ServiceProps) message).source);
			getSender().tell(InitializeComplete, getSelf());
		}
		else if(message instanceof UpdateRoster) {
			String gameDate = ((UpdateRoster) message).date;
			String nakedDate = DateTime.getFindDateNaked(DateTime.createDateFromStringDate(gameDate));
			String gameTeam = ((UpdateRoster) message).team;
			InputStream baseJson = null;
			
			try {
				if (source.equals(Source.file)) {
					String fileTeam =  gameTeam + "-" + nakedDate + ".json";				
					Path path =  Paths.get(fileRoster).resolve(fileTeam);
					File file = path.toFile();
					baseJson = new FileInputStream(file);
				}
				else {
					URL url;
					String urlTeam = urlRoster + gameTeam + ".json";
					url = new URL(urlTeam);
					URLConnection connection = url.openConnection();
					connection.setRequestProperty(AUTHORIZATION, accessToken);
					connection.setRequestProperty(USER_AGENT, userAgentName);
					connection.setRequestProperty(ACCEPT_ENCODING, GZIP);
					baseJson = connection.getInputStream();
					String encoding = connection.getContentEncoding();
					if (GZIP.equals(encoding)) {
						baseJson = new GZIPInputStream(baseJson);
					}
				}
				if (baseJson != null) {
				  	ObjectMapper mapper = new ObjectMapper();
				    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				    Roster xmlStatsRoster = mapper.readValue(baseJson, Roster.class);				    
				    List<Player> activePlayers = JsonHelper.getPlayers(xmlStatsRoster.players);
				    ActiveRoster activeRoster = new ActiveRoster(activePlayers);
	    		  	getSender().tell(activeRoster, getSelf());
				}
			} 
			catch (FileNotFoundException e) {
				listener.tell(new XmlStatsException("FileNotFoundException"), getSelf());
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