package actor;

import static actor.ActorApi.InitializeComplete;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.GZIPInputStream;

import json.xmlStats.JsonHelper;
import json.xmlStats.Roster;
import models.Game;
import models.Game.ProcessingType;
import models.Game.Source;
import models.RosterPlayer;

import org.apache.commons.io.IOUtils;

import util.DateTimeUtil;
import actor.ActorApi.RosterActive;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.RosterRetrieve;
import actor.ActorApi.XmlStatsException;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class RosterXmlStats extends UntypedActor {
    static final String AUTHORIZATION = "Authorization";
    static final String USER_AGENT = "User-agent";
    static final String ACCEPT_ENCODING = "Accept-encoding";
    static final String GZIP = "gzip";
	private String accessToken;
	private String userAgentName;
	private String urlRoster;
	private String fileRoster;
	private ProcessingType processingType;
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
			processingType = Game.ProcessingType.valueOf(((ServiceProps) message).processType);
			source = Game.Source.valueOf(((ServiceProps) message).sourceRoster);
			getSender().tell(InitializeComplete, getSelf());
		}
		else if(message instanceof RosterRetrieve) {
			String gameDate = ((RosterRetrieve) message).date;
			String nakedDate = DateTimeUtil.getFindDateNaked(DateTimeUtil.createDateFromStringDate(gameDate));
			String gameTeam = ((RosterRetrieve) message).team;
			
			try {
				if (source.equals(Source.api)) {
					URL url;
					String urlTeam = urlRoster + gameTeam + ".json";
					url = new URL(urlTeam);
					URLConnection connection = url.openConnection();
					connection.setRequestProperty(AUTHORIZATION, accessToken);
					connection.setRequestProperty(USER_AGENT, userAgentName);
					connection.setRequestProperty(ACCEPT_ENCODING, GZIP);
					InputStream inputStreamApi = connection.getInputStream();
					String encoding = connection.getContentEncoding();
					if (GZIP.equals(encoding)) {
						inputStreamApi = new GZIPInputStream(inputStreamApi);
					}
					OutputStream outputStreamJson = new FileOutputStream(fileRoster + File.separatorChar + gameTeam + "-" + nakedDate + ".json");
					outputStreamJson.write(IOUtils.toByteArray(inputStreamApi));
					outputStreamJson.close();
					inputStreamApi.close();
				}
				String fileTeam =  gameTeam + "-" + nakedDate + ".json";				
				Path path =  Paths.get(fileRoster).resolve(fileTeam);
				File file = path.toFile();
				InputStream inputStreamFile = new FileInputStream(file);

				InputStreamReader baseJson = new InputStreamReader(inputStreamFile, StandardCharsets.UTF_8);
				if (baseJson != null) {
				  	ObjectMapper mapper = new ObjectMapper();
				  	mapper.registerModule(new JodaModule());  
				    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				    Roster xmlStatsRoster = mapper.readValue(baseJson, Roster.class);				    
				    List<RosterPlayer> rosterPlayers = JsonHelper.getRosterPlayers(xmlStatsRoster, processingType);
				    RosterActive rosterActive = new RosterActive(rosterPlayers);
				    getSender().tell(rosterActive, getSelf());
				}
				inputStreamFile.close();
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