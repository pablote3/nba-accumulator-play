package actor;

import static actor.MasterApi.Start;

import java.util.List;

import models.partial.GameKey;
import actor.MasterApi.GameKeys;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class MasterActor extends UntypedActor {
	private int nbrSecondsDelay;
	private final long start = System.currentTimeMillis();

	public MasterActor(final int nbrSecondsDelay, ActorRef listener) {
		this.nbrSecondsDelay = nbrSecondsDelay;
	}

	public void onReceive(Object message) {
		if (message.equals(Start)) {
			final ActorRef gameFinderActor = getContext().actorOf(Props.create(GameFinderActor.class), "gameFinder");
			gameFinderActor.tell(Start, getSelf());
		}
		else if(message instanceof GameKeys) {
			@SuppressWarnings("unchecked")
			List<GameKey> keys = (List<GameKey>)message;
			for (int i = 0; i < keys.size(); i++) {
				GameKey key = keys.get(i);
				System.out.println("i: " + i + " " + key.getDate() + " " + key.getHomeTeamKey() + " " + key.getAwayTeamKey());
				//workerRouter.tell(new Work(start, nrOfElements), getSelf());
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