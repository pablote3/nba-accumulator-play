package actor;

import java.util.ArrayList;
import java.util.List;

import models.entity.Game;
import actor.PropertyApi.GameDayProps;
import akka.actor.UntypedActor;

public class GameFinderActor extends UntypedActor {

	public void onReceive(Object message) {
		if (message instanceof GameDayProps) {
			String propDate = ((GameDayProps) message).date;
			String propTeam = ((GameDayProps) message).team;
			
			List<Game> games;
			if (propTeam == null) {
				games = Game.findByDate(propDate);
			}
			else {
				games = new ArrayList<Game>();
				games.add(Game.findByDateTeamKey(propDate, propTeam));
			}
			getSender().tell(games, getSelf());				
		}
		else {
			unhandled(message);
		}
	}
}