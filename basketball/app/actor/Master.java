package actor;

import static actor.ActorApi.Start;
import static actor.ActorApi.Service;
import static actor.ActorApi.PropertyException;
import static actor.ActorApi.Complete;

import java.util.List;

import actor.ActorApi.GameId;
import actor.ActorApi.GameIds;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Master extends UntypedActor {
	private final ActorRef gameModelActor = getContext().actorOf(Props.create(GameModel.class), "gameModel");
	private int nbrSecondsDelay;

	public Master(final int nbrSecondsDelay, ActorRef listener) {
		this.nbrSecondsDelay = nbrSecondsDelay;
	}

	public void onReceive(Object message) {
		if (message.equals(Start)) {
			final ActorRef propertyActor = getContext().actorOf(Props.create(Property.class), "property");
			propertyActor.tell(Service, getSelf());
		}
		else if (message instanceof PropertyException) {
			PropertyException pe = (PropertyException) message;
			System.out.println("Property Exception " + pe.getMessage());
			getContext().stop(getSelf());
		}
		else if (message instanceof ServiceProps) {		
			gameModelActor.tell(message, getSelf());	
		}
		else if (message instanceof GameIds) {
			GameIds gameIds = (GameIds) message;
			List<Long> ids = gameIds.games;
			for (int i = 0; i < ids.size(); i++) {
				Long id = ids.get(i);
				GameId gid = new GameId(id);
				gameModelActor.tell(gid, getSelf());

				try {
				    Thread.sleep(nbrSecondsDelay);
				} 
				catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
		}
		else if (message.equals(Complete)) {
			getContext().stop(getSelf());
		} 
		else {
			unhandled(message);
		}
	}
}