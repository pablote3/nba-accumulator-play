package actor;

import java.util.ArrayList;
import java.util.List;

import models.entity.Game;
import models.partial.GameKey;
import actor.MasterApi.GameKeys;
import actor.PropertyApi.ServiceProps;
import akka.actor.UntypedActor;

public class GameModel extends UntypedActor {

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			String propDate = ((ServiceProps) message).date;
			String propTeam = ((ServiceProps) message).team;
			
			List<GameKey> games;
			if (propTeam == null) {
				games = Game.findKeyByDate(propDate);
			}
			else {
				games = new ArrayList<GameKey>();
				GameKey key = Game.findKeyByDateTeam(propDate, propTeam);
				if (key != null) {
					games.add(key);
				}
			}
			GameKeys key = new GameKeys(games);
			getSender().tell(key, getSelf());
		}
		else {
			unhandled(message);
		}
	}
}