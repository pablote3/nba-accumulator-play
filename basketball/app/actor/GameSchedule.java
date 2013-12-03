package actor;

import static actor.GameScheduleApi.Retrieve;
import static actor.PropertyApi.Service;

import java.util.ArrayList;
import java.util.List;

import models.entity.Game;
import models.partial.GameKey;
import actor.MasterApi.GameKeys;
import actor.PropertyApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class GameSchedule extends UntypedActor {
	private ActorRef masterActor = null;
	private String accessToken;
	private String userAgentName;
	private String urlBoxScore;
	public void onReceive(Object message) {
		if (message.equals(Retrieve)) {
			masterActor = getSender();
			final ActorRef propertyActor = getContext().actorOf(Props.create(Property.class), "property");
			propertyActor.tell(Service, getSelf());
		}	
		else if (message instanceof ServiceProps) {	
			String propDate = ((ServiceProps) message).date;
			String propTeam = ((ServiceProps) message).team;
			accessToken = ((ServiceProps) message).accessToken;
			userAgentName = ((ServiceProps) message).userAgentName;
			urlBoxScore = ((ServiceProps) message).urlBoxScore;
			
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