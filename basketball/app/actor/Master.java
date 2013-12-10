package actor;

import static actor.ActorApi.Start;
import static actor.ActorApi.Service;

import java.util.List;

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
		else if (message instanceof ServiceProps) {		
			gameModelActor.tell(message, getSelf());	
		}
		else if(message instanceof GameIds) {
			GameIds gameIds = (GameIds) message;
			List<Long> ids = gameIds.games;
			for (int i = 0; i < ids.size(); i++) {
				Long id = ids.get(i);
				gameModelActor.tell(id, getSelf());

			try {
			    Thread.sleep(nbrSecondsDelay);
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
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