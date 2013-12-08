package actor;

import static actor.XmlStatsApi.InitXmlStats;

import java.util.ArrayList;
import java.util.List;

import models.entity.Game;
import actor.MasterApi.GameIds;
import actor.PropertyApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class GameModel extends UntypedActor {
	private final ActorRef xmlStatsActor = getContext().actorOf(Props.create(XmlStats.class), "xmlStatsModel");
	private ActorRef masterActor;
	private String propDate;
	private String propTeam;

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			masterActor = getSender();
			propDate = ((ServiceProps) message).date;
			propTeam = ((ServiceProps) message).team;			
			xmlStatsActor.tell(message, getSelf());
		}
		else if (message.equals(InitXmlStats)) {					
			List<Long> games;
			if (propTeam == null) {
				games = Game.findIdsByDate(propDate);
			}
			else {
				games = new ArrayList<Long>();
				Long id = Game.findIdByDateTeam(propDate, propTeam);
				if (id != null) {
					games.add(id);
				}
			}
			GameIds ids = new GameIds(games);
			masterActor.tell(ids, getSender());
		}
		else if(message instanceof Long) {
			Long gameId = (Long)message;			
			Game game = Game.findById(gameId);
			xmlStatsActor.tell(game, getSelf());
		}
		else {
			unhandled(message);
		}
	}
}