package actor;

import static actor.ActorApi.InitializeStart;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import util.FileIO;
import actor.ActorApi.ActorException;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Property extends UntypedActor {
	private Properties props;
	private ActorRef listener;
	
	public Property(ActorRef listener) {
		this.listener = listener;
	}
	
	private Properties getProperties() throws ActorException {
		if (props == null) {
			try {
				String path = FileIO.getPropertyPath("config.basketball");
				props = FileIO.loadProperties(path + "\\properties\\service.properties");
				
				if (!util.DateTime.isDate(props.getProperty("gameday.date")))
					throw new ActorException("InvalidDate - gameday.date");
				
				if (!util.Numeric.isNumber(props.getProperty("xmlstats.delay")))
					throw new ActorException("InvalidNumber - xmlstats.delay");
			}
			catch (FileNotFoundException e) {
				throw new ActorException("FileNotFoundException");
			}
			catch (IOException e) {
				throw new ActorException("IOException");
			}
		}
		return props;
	}

	public void onReceive(Object message) {
		if (message.equals(InitializeStart)) {
			Properties props;
			try {
				props = getProperties();
				String date = props.getProperty("gameday.date");
				String team = props.getProperty("gameday.team");
				String accessToken = props.getProperty("xmlstats.accessToken");
				String userAgentName = props.getProperty("xmlstats.userAgentName");
				String urlBoxScore = props.getProperty("xmlstats.urlBoxScore");
				String delay = props.getProperty("xmlstats.delay");
				ServiceProps serviceProps = new ServiceProps(date, team, accessToken, userAgentName, urlBoxScore, delay);
				getSender().tell(serviceProps, getSelf());
				getContext().stop(getSelf());
			} 
			catch (ActorException e) {
				listener.tell(e, getSelf());
			}
		} 
		else {
			unhandled(message);
		}
	}
}