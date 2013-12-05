package actor;

import static actor.MasterApi.Start;
import static actor.PropertyApi.Service;
import static actor.XmlStatsApi.InitXmlStats;

import java.util.List;

import models.partial.GameKey;
import actor.MasterApi.GameKeys;
import actor.PropertyApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Master extends UntypedActor {
	private boolean isInitXmlStats = false;
	private int nbrSecondsDelay;
	private final long start = System.currentTimeMillis();
	private final ActorRef gameModelActor = getContext().actorOf(Props.create(GameModel.class), "gameModel");
	private final ActorRef xmlStatsActor = getContext().actorOf(Props.create(XmlStats.class), "xmlStatsModel");

	public Master(final int nbrSecondsDelay, ActorRef listener) {
		this.nbrSecondsDelay = nbrSecondsDelay;
	}

	public void onReceive(Object message) {
		if (message.equals(Start)) {
			final ActorRef propertyActor = getContext().actorOf(Props.create(Property.class), "property");
			propertyActor.tell(Service, getSelf());
		}	
		else if (message instanceof ServiceProps) {		
			gameModelActor.tell(message, getSelf());	
			xmlStatsActor.tell(message, getSelf()); 	
		}
		else if (message.equals(InitXmlStats)) {		
			isInitXmlStats = true;
			System.out.println("InitXmlStats = " + isInitXmlStats);
		}
		else if(message instanceof GameKeys) {
			if (isInitXmlStats) {
				List<GameKey> keys = ((GameKeys) message).games;
				for (int i = 0; i < keys.size(); i++) {
					GameKey key = keys.get(i);
					System.out.println("i: " + i + " " + key.getDate() + " " + key.getHomeTeamKey() + " " + key.getAwayTeamKey());
//					gameFinderActor.tell(new Work(start, nrOfElements), getSelf());
					
					try {
					    Thread.sleep(nbrSecondsDelay);
					} catch(InterruptedException ex) {
					    Thread.currentThread().interrupt();
					}
				}
			}
			else {
				
			}
		}
//		else if (message instanceof Result) {
//			Result result = (Result) message;
//			pi += result.getValue();
//			nrOfResults += 1;
//			if (nrOfResults == nrOfMessages) {
//				// Send the result to the listener
//				Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
//				listener.tell(new PiApproximation(pi, duration), getSelf());
//				
//				// Stops this actor and all its supervised children
//				getContext().stop(getSelf());
//			}
//		} 
		else {
			unhandled(message);
		}
	}
}