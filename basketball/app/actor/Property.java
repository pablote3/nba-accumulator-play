package actor;

import static actor.ActorApi.InitializeStart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import util.DateTimeUtil;
import util.Utilities;
import models.Game;
import actor.ActorApi.PropertyException;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Property extends UntypedActor {
	private Properties properties;
	private ActorRef listener;
	
	public Property(ActorRef listener) {
		this.listener = listener;
	}
	
	private Properties getProperties() throws PropertyException {
		if (properties == null) {
			try {
				String strPath = System.getProperty("config.properties");
				if (strPath == null) {
					strPath = "/home/pablote/pdrive/pwork/play/accumulator/config/properties";
				}
        		Path path =  Paths.get(strPath).resolve("service.properties");
        		
        		File file = path.toFile();
				properties = new Properties();
				FileInputStream in = new FileInputStream(file);
				properties.load(in);
				in.close();
				
				if (properties.getProperty("game.date") != null && !DateTimeUtil.isDate(properties.getProperty("game.date")))
					throw new PropertyException("InvalidDate - game.date");
				
				if (properties.getProperty("xmlstats.size") != null && !Utilities.isValidNumber(properties.getProperty("xmlstats.size")))
					throw new PropertyException("InvalidNumber - xmlstats.size");
				
				if (properties.getProperty("xmlstats.delay") != null && !Utilities.isValidNumber(properties.getProperty("xmlstats.delay")))
					throw new PropertyException("InvalidNumber - xmlstats.delay");
				
				Game.ProcessingType.valueOf(properties.getProperty("accumulator.processType"));
			}
			catch (FileNotFoundException e) {
				throw new PropertyException("FileNotFoundException");
			}
			catch (IOException e) {
				throw new PropertyException("IOException");
			}
			catch (IllegalArgumentException e) {
				throw new PropertyException("IllegalArgumentException");
			}
		}
		return properties;
	}

	public void onReceive(Object message) {
		if (message.equals(InitializeStart)) {
			Properties props;
			try {
				props = getProperties();
				String date = props.getProperty("game.date");
				String team = props.getProperty("game.team");
				String size = props.getProperty("game.size");
				String processType = props.getProperty("accumulator.processType");
				String sourceBoxScore = props.getProperty("accumulator.source.boxScore");
				String sourceRoster = props.getProperty("accumulator.source.roster");
				String sourceStanding = props.getProperty("accumulator.source.standing");
				String accessToken = props.getProperty("xmlstats.accessToken");
				String userAgentName = props.getProperty("xmlstats.userAgentName");
				String urlBoxScore = props.getProperty("xmlstats.urlBoxScore");
				String fileBoxScore = props.getProperty("xmlstats.fileBoxScore");
				String urlStanding = props.getProperty("xmlstats.urlStanding");
				String fileStanding = props.getProperty("xmlstats.fileStanding");
				String urlRoster = props.getProperty("xmlstats.urlRoster");
				String fileRoster = props.getProperty("xmlstats.fileRoster");
				String delay = props.getProperty("xmlstats.delay");
				ServiceProps serviceProps = new ServiceProps(date, team, size, accessToken, userAgentName, urlBoxScore, fileBoxScore, urlRoster, fileRoster, urlStanding, fileStanding, delay, processType, sourceBoxScore, sourceRoster, sourceStanding);
				getSender().tell(serviceProps, getSelf());
				getContext().stop(getSelf());
			} 
			catch (PropertyException e) {
				listener.tell(e, getSelf());
			}
		} 
		else {
			unhandled(message);
		}
	}
}