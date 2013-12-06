package actor;

import static actor.MasterApi.Start;
import static actor.PropertyApi.Service;
import static actor.XmlStatsApi.InitXmlStats;

import java.util.List;

import models.entity.Game;
import actor.MasterApi.GameIds;
import actor.PropertyApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Master extends UntypedActor {
	private boolean isInitXmlStats = false;
	private int nbrSecondsDelay;
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
		else if(message instanceof GameIds) {
			if (isInitXmlStats) {
				GameIds gameIds = (GameIds) message;
				List<Long> ids = gameIds.games;
				for (int i = 0; i < ids.size(); i++) {
					Long id = ids.get(i);
					gameModelActor.tell(id, getSelf());
					//xmlStatsActor.tell(id, getSelf());
					
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
		else if(message instanceof Game) {
			Game game = (Game)message;
			System.out.println(game.getDate() + " " + game.getBoxScores().get(0).getTeam().getAbbr() + " " + game.getBoxScores().get(1).getTeam().getAbbr());
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