package actor;

import static actor.ActorApi.Start;
import static actor.ActorApi.InitializeStart;
import static actor.ActorApi.InitializeComplete;
import static actor.ActorApi.WorkStart;
import static actor.ActorApi.WorkComplete;
import static actor.ActorApi.Finish;
import actor.ActorApi.ServiceProps;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Master extends UntypedActor {
	private ActorRef listener;
	private final ActorRef gameController;
	
	public Master(ActorRef listener) {
		this.listener = listener;
		gameController = getContext().actorOf(Props.create(GameController.class, listener), "gameController");
	}

	public void onReceive(Object message) {
		if (message.equals(Start)) {
			final ActorRef property = getContext().actorOf(Props.create(Property.class, listener), "property");
			property.tell(InitializeStart, getSelf());
		}
		else if (message instanceof ServiceProps) {
			gameController.tell(message, getSelf());	
		}
		else if (message.equals(InitializeComplete)) {
			gameController.tell(WorkStart, getSelf());	
		} 
		else if (message.equals(WorkComplete)) {

			listener.tell(Finish, getSelf());
		} 
		else {
			unhandled(message);
		}
	}
}