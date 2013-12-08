package actor;

import static actor.XmlStatsApi.InitXmlStats;
import models.entity.Game;
import actor.PropertyApi.ServiceProps;
import akka.actor.UntypedActor;

public class XmlStats extends UntypedActor {
	private String accessToken;
	private String userAgentName;
	private String urlBoxScore;

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			accessToken = ((ServiceProps) message).accessToken;
			userAgentName = ((ServiceProps) message).userAgentName;
			urlBoxScore = ((ServiceProps) message).urlBoxScore;
			getSender().tell(InitXmlStats, getSelf());
		}
		else if(message instanceof Game) {
			Game game = (Game)message;
			System.out.println(game.getDate() + " " + game.getBoxScores().get(0).getTeam().getAbbr() + " " + game.getBoxScores().get(1).getTeam().getAbbr());
		}
		else {
			unhandled(message);
		}
	}
}