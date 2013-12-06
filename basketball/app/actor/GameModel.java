package actor;

import java.util.ArrayList;
import java.util.List;

import models.entity.Game;
import actor.MasterApi.GameIds;
import actor.PropertyApi.ServiceProps;
import akka.actor.UntypedActor;

public class GameModel extends UntypedActor {

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			String propDate = ((ServiceProps) message).date;
			String propTeam = ((ServiceProps) message).team;
			
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
			getSender().tell(ids, getSelf());
		}
		else if(message instanceof Long) {
			Long gameId = (Long)message;			
			Game game = Game.findById(gameId);
			getSender().tell(game, getSelf());
		}
		else {
			unhandled(message);
		}
	}
}