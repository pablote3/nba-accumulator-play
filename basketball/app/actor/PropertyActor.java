package actor;

import static actor.PropertyApi.GameDay;
import static actor.PropertyApi.XmlStats;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import util.FileIO;
import actor.PropertyApi.GameDayProps;
import actor.PropertyApi.PropertyException;
import actor.PropertyApi.XmlStatProps;
import akka.actor.UntypedActor;

public class PropertyActor extends UntypedActor {
	private Properties props;
	private Properties getProperties() throws PropertyException {
		if (props == null) {
			try {
				String path = FileIO.getPropertyPath("config.basketball");
				props = FileIO.loadProperties(path + "//properties//service.properties");
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
		if (message.equals(XmlStats)) {
			Properties props = getProperties();
			String accessToken = props.getProperty("xmlstats.accessToken");
			String userAgentName = props.getProperty("xmlstats.userAgentName");
			String urlBoxScore = props.getProperty("xmlstats.urlBoxScore");
			XmlStatProps xmlStatProps = new XmlStatProps(accessToken, userAgentName, urlBoxScore);
			getSender().tell(xmlStatProps, getSelf());
		} 
		else if (message.equals(GameDay)) {
			Properties props = getProperties();
			String date = props.getProperty("gameday.date");
			if (!util.DateTime.isValidDate(date))
				throw new PropertyException("InvalidDate");
			String team = props.getProperty("gameday.team");
			GameDayProps gameDayProps = new GameDayProps(date, team);
			getSender().tell(gameDayProps, getSelf());				
		}
		else {
			unhandled(message);
		}
	}
}