package actor;

import static actor.ActorApi.InitializeStart;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import models.entity.Game;

import util.FileIO;
import actor.ActorApi.PropertyException;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Property extends UntypedActor {
	private Properties props;
	private ActorRef listener;
	
	public Property(ActorRef listener) {
		this.listener = listener;
	}
	
	private Properties getProperties() throws PropertyException {
		if (props == null) {
			try {
				String path = FileIO.getPropertyPath("config.basketball");
				props = FileIO.loadProperties(path + "\\properties\\service.properties");
				
				if (!util.DateTime.isDate(props.getProperty("game.date")))
					throw new PropertyException("InvalidDate - game.date");
				
				if (!util.Numeric.isNumber(props.getProperty("xmlstats.delay")))
					throw new PropertyException("InvalidNumber - xmlstats.delay");
				
				Game.ProcessingType.valueOf(props.getProperty("aggregator.processType"));
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
		return props;
	}

	public void onReceive(Object message) {
		if (message.equals(InitializeStart)) {
			Properties props;
			try {
				props = getProperties();
				String date = props.getProperty("game.date");
				String team = props.getProperty("game.team");
				String processType = props.getProperty("aggregator.processType");
				String accessToken = props.getProperty("xmlstats.accessToken");
				String userAgentName = props.getProperty("xmlstats.userAgentName");
				String urlBoxScore = props.getProperty("xmlstats.urlBoxScore");
				String delay = props.getProperty("xmlstats.delay");
				ServiceProps serviceProps = new ServiceProps(date, team, accessToken, userAgentName, urlBoxScore, delay, processType);
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