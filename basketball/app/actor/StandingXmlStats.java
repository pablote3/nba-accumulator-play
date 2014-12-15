package actor;

import static actor.ActorApi.InitializeComplete;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import json.xmlStats.Standings;
import models.Game;
import models.Game.ProcessingType;
import models.Game.Source;
import models.Standing;
import actor.ActorApi.StandingsActive;
import actor.ActorApi.StandingsRetrieve;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.XmlStatsException;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class StandingXmlStats extends UntypedActor {
    static final String AUTHORIZATION = "Authorization";
    static final String USER_AGENT = "User-agent";
    static final String ACCEPT_ENCODING = "Accept-encoding";
    static final String GZIP = "gzip";
	private String accessToken;
	private String userAgentName;
	private String urlStanding;
	private String fileStanding;
	private ProcessingType processingType;
	private Source source;
	private ActorRef listener;
	
	public StandingXmlStats(ActorRef listener) {
		this.listener = listener;
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			accessToken = "Bearer " + ((ServiceProps) message).accessToken;
			userAgentName = ((ServiceProps) message).userAgentName;
			urlStanding = ((ServiceProps) message).urlStanding;
			fileStanding = ((ServiceProps) message).fileStanding;
			processingType = Game.ProcessingType.valueOf(((ServiceProps) message).processType);
			source = Game.Source.valueOf(((ServiceProps) message).sourceStanding);
			getSender().tell(InitializeComplete, getSelf());
		}
		else if(message instanceof StandingsRetrieve) {
			String standingsDate = ((StandingsRetrieve) message).date;
			System.out.println("  Retrieving standings for " + standingsDate);
			String event = standingsDate + ".json";
			InputStream inputStreamJson = null;
			InputStreamReader baseJson = null;
		
			try {
				if (source.equals(Source.file)) {
					Path path =  Paths.get(fileStanding).resolve(event);
					File file = path.toFile();
					inputStreamJson = new FileInputStream(file);
				}
				else {
					URL url = new URL(urlStanding + event);
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
				    mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
				    Standings xmlStatsStandings = mapper.readValue(baseJson, Standings.class);
				    List<Standing> standings = JsonHelper.getStandings(xmlStatsStandings, processingType);
				    StandingsActive sa = new StandingsActive(standings);
				    getSender().tell(sa, getSender());
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