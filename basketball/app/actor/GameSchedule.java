package actor;

import static actor.GameScheduleApi.Retrieve;
import static actor.PropertyApi.Service;

import java.util.List;

import models.partial.GameKey;
import actor.MasterApi.GameKeys;
import actor.PropertyApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class GameSchedule extends UntypedActor {
	private ActorRef masterActor = null;

	public void onReceive(Object message) {
		if (message.equals(Retrieve)) {
			masterActor = getSender();
			final ActorRef propertyActor = getContext().actorOf(Props.create(Property.class), "property");
			propertyActor.tell(Service, getSelf());
		}	
		else if (message instanceof ServiceProps) {		
			final ActorRef gameModelActor = getContext().actorOf(Props.create(GameModel.class), "gameModel");
			gameModelActor.tell(message, getSelf());	
			final ActorRef xmlStatsActor = getContext().actorOf(Props.create(XmlStats.class), "xmlStatsModel");
			xmlStatsActor.tell(message, getSelf()); 	
		}
		else if(message instanceof GameKeys) {
			List<GameKey> keys = ((GameKeys) message).games;
			for (int i = 0; i < keys.size(); i++) {
				GameKey key = keys.get(i);
				System.out.println("i: " + i + " " + key.getDate() + " " + key.getHomeTeamKey() + " " + key.getAwayTeamKey());
//				gameFinderActor.tell(new Work(start, nrOfElements), getSelf());
				
				try {
				    Thread.sleep(10000);
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
		}
		else {
			unhandled(message);
		}
	}
}