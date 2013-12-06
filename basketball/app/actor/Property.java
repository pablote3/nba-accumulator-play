package actor;

import static actor.PropertyApi.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import util.FileIO;
import actor.PropertyApi.PropertyException;
import actor.PropertyApi.ServiceProps;
import akka.actor.UntypedActor;

public class Property extends UntypedActor {
	private Properties props;
	private Properties getProperties() throws PropertyException {
		if (props == null) {
			try {
				String path = FileIO.getPropertyPath("config.basketball");
				props = FileIO.loadProperties(path + "\\properties\\service.properties");
			}
			catch (FileNotFoundException e) {
				throw new PropertyException("FileNotFoundException");
			}
			catch (IOException e) {
				throw new PropertyException("IOException");
			}
		}
		return props;
	}

	public void onReceive(Object message) {
		if (message.equals(Service)) {
			Properties props = getProperties();
			String date = props.getProperty("gameday.date");
			if (!util.DateTime.isValidDate(date))
				throw new PropertyException("InvalidDate");
			String team = props.getProperty("gameday.team");
			String accessToken = props.getProperty("xmlstats.accessToken");
			String userAgentName = props.getProperty("xmlstats.userAgentName");
			String urlBoxScore = props.getProperty("xmlstats.urlBoxScore");
			ServiceProps serviceProps = new ServiceProps(date, team, accessToken, userAgentName, urlBoxScore);
			getSender().tell(serviceProps, getSelf());
		} 
		else {
			unhandled(message);
		}
	}
}