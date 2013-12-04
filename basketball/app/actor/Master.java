package actor;

import static actor.MasterApi.Start;
import static actor.GameScheduleApi.Retrieve;

import java.util.List;

import models.partial.GameKey;
import actor.MasterApi.GameKeys;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Master extends UntypedActor {
	private int nbrSecondsDelay;
	private final long start = System.currentTimeMillis();
	private final ActorRef gameScheduleActor = getContext().actorOf(Props.create(GameSchedule.class), "gameSchedule");

	public Master(final int nbrSecondsDelay, ActorRef listener) {
		this.nbrSecondsDelay = nbrSecondsDelay;
	}

	public void onReceive(Object message) {
		if (message.equals(Start)) {
			gameScheduleActor.tell(Retrieve, getSelf());
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