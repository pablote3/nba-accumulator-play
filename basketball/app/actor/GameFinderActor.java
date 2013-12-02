package actor;

import static actor.GameFinderApi.Start;
import static actor.PropertyApi.GameDay;

import java.util.ArrayList;
import java.util.List;

import models.entity.Game;
import models.partial.GameKey;
import actor.MasterApi.GameKeys;
import actor.PropertyApi.GameDayProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class GameFinderActor extends UntypedActor {
	ActorRef masterActor = null;
	public void onReceive(Object message) {
		if (message.equals(Start)) {
			masterActor = getSender();
			final ActorRef propertyActor = getContext().actorOf(Props.create(PropertyActor.class), "property");
			propertyActor.tell(GameDay, getSelf());
		}	
		else if (message instanceof GameDayProps) {	
			String propDate = ((GameDayProps) message).date;
			String propTeam = ((GameDayProps) message).team;
			
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
			masterActor.tell(key, getSelf());				
		}
		else {
			unhandled(message);
		}
	}
}